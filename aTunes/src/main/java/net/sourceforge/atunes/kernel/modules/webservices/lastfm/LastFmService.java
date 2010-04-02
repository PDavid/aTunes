/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import java.awt.Image;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import net.roarsoftware.lastfm.Album;
import net.roarsoftware.lastfm.Artist;
import net.roarsoftware.lastfm.Authenticator;
import net.roarsoftware.lastfm.Caller;
import net.roarsoftware.lastfm.Event;
import net.roarsoftware.lastfm.ImageSize;
import net.roarsoftware.lastfm.PaginatedResult;
import net.roarsoftware.lastfm.Playlist;
import net.roarsoftware.lastfm.Result;
import net.roarsoftware.lastfm.Session;
import net.roarsoftware.lastfm.Track;
import net.roarsoftware.lastfm.Result.Status;
import net.roarsoftware.lastfm.scrobble.ResponseStatus;
import net.roarsoftware.lastfm.scrobble.Scrobbler;
import net.roarsoftware.lastfm.scrobble.Source;
import net.roarsoftware.lastfm.scrobble.SubmissionData;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.AlbumListInfo;
import net.sourceforge.atunes.kernel.modules.context.ArtistInfo;
import net.sourceforge.atunes.kernel.modules.context.SimilarArtistsInfo;
import net.sourceforge.atunes.kernel.modules.context.TrackInfo;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmAlbum;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmAlbumList;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmLovedTrack;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmSimilarArtists;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.CryptoUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.PluginApi;

/**
 * This class is responsible of retrieve information from Last.fm web services.
 */
@PluginApi
public final class LastFmService {

    private final class SubmitNowPlayingInfoRunnable implements Runnable {
		private final AudioFile audioFile;

		private SubmitNowPlayingInfoRunnable(AudioFile audioFile) {
			this.audioFile = audioFile;
		}

		@Override
		public void run() {
		    try {
		        submitNowPlayingInfo(audioFile);
		    } catch (ScrobblerException e) {
		        if (e.getStatus() == 2) {
		            getLogger().error(LogCategories.SERVICE, "Authentication failure on Last.fm service");
		            SwingUtilities.invokeLater(new Runnable() {
		                @Override
		                public void run() {
		                    GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("LASTFM_USER_ERROR"));
		                    // Disable service by deleting password
		                    ApplicationState.getInstance().setLastFmEnabled(false);
		                }
		            });
		        } else {
		            getLogger().error(LogCategories.SERVICE, e.getMessage());
		        }
		    }
		}
	}

	private final class SubmitToLastFmRunnable implements Runnable {
		private final long secondsPlayed;
		private final AudioFile audioFile;

		private SubmitToLastFmRunnable(long secondsPlayed, AudioFile audioFile) {
			this.secondsPlayed = secondsPlayed;
			this.audioFile = audioFile;
		}

		@Override
		public void run() {
		    try {
		        submit(audioFile, secondsPlayed);
		    } catch (ScrobblerException e) {
		        if (e.getStatus() == 2) {
		            getLogger().error(LogCategories.SERVICE, "Authentication failure on Last.fm service");
		            SwingUtilities.invokeLater(new Runnable() {
		                @Override
		                public void run() {
		                    GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("LASTFM_USER_ERROR"));
		                    // Disable service by deleting password
		                    ApplicationState.getInstance().setLastFmEnabled(false);
		                }
		            });
		        } else {
		            getLogger().error(LogCategories.SERVICE, e.getMessage());
		        }
		    }
		}
	}

	/*
     * DO NOT USE THESE KEYS FOR OTHER APPLICATIONS THAN aTunes!
     */
    private static final byte[] API_KEY = { 78, 119, -39, -5, -89, -107, -38, 41, -87, -107, 122, 98, -33, 46, 32, -47, -44, 54, 97, 67, 105, 122, 11, -26, -81, 90, 94, 55, 121,
            11, 14, -104, -70, 123, -88, -70, -108, 75, -77, 98 };
    private static final byte[] API_SECRET = { 38, -8, 33, 63, 10, 86, 29, -2, 87, -63, 67, 111, -5, -101, -87, 38, 2, 35, 86, -86, 19, 110, -81, -115, 102, 54, -24, 27, 40, -124,
            -57, -62, -70, 123, -88, -70, -108, 75, -77, 98 };
    private static final String CLIENT_ID = "atu";
    private static final String CLIENT_VERSION = Constants.VERSION.toShortString();

    private static final String ARTIST_WILDCARD = "(%ARTIST%)";
    private static final String LANGUAGE_PARAM = "?setlang=";
    private static final String LANGUAGE_WILDCARD = "(%LANGUAGE%)";
    private static final String ARTIST_WIKI_URL = StringUtils.getString("http://www.lastfm.com/music/", ARTIST_WILDCARD, "/+wiki", LANGUAGE_PARAM, LANGUAGE_WILDCARD);

    private static final String VARIOUS_ARTISTS = "Various Artists";

    private static final int MIN_DURATION_TO_SUBMIT = 30;
    private static final int MAX_SUBMISSIONS = 50;

    private static Logger logger;

    private Proxy proxy;

    private Scrobbler scrobbler;
    private String user;
    private String password;
    private boolean handshakePerformed;

    private static LastFmCache lastFmCache = new LastFmCache();

    /** Submissions need single threaded executor */
    private ExecutorService scrobblerExecutorService = Executors.newSingleThreadExecutor();

    /**
     * Singleton instance
     */
    private static LastFmService instance;

    /**
     * Getter of singleton instance;
     * 
     * @return
     */
    public static LastFmService getInstance() {
        if (instance == null) {
            instance = new LastFmService(ApplicationState.getInstance().getProxy(), ApplicationState.getInstance().getLastFmUser(), ApplicationState.getInstance()
                    .getLastFmPassword());
        }
        return instance;
    }

    /**
     * Instantiates a new Last.fm service
     * 
     * @param proxyBean
     *            the proxy
     * @param user
     *            the Last.fm username
     * @param password
     *            the Last.fm password
     */
    private LastFmService(ProxyBean proxyBean, String user, String password) {
        Proxy proxy = null;
        try {
            if (proxyBean != null) {
                proxy = Proxy.getProxy(proxyBean);
            }
        } catch (Exception e) {
            getLogger().error(LogCategories.SERVICE, e);
        }

        this.proxy = proxy;
        this.user = user;
        this.password = password;
        Caller.getInstance().setCache(null);
        Caller.getInstance().setProxy(proxy);
        Caller.getInstance().setUserAgent(CLIENT_ID);
        // Use encoded version name to avoid errors from server
        scrobbler = Scrobbler.newScrobbler(CLIENT_ID, NetworkUtils.encodeString(CLIENT_VERSION), user);
        this.handshakePerformed = false;
    }

    /**
     * Updates service after a configuration change
     */
    public void updateService() {
        // Force create service again
        instance = null;
    }

    /**
     * Finishes service
     */
    public void finishService() {
        scrobblerExecutorService.shutdownNow();
        lastFmCache.shutdown();
    }

    /**
     * Gets the album.
     * 
     * @param artist
     *            the artist
     * @param album
     *            the album
     * 
     * @return the album
     */
    public AlbumInfo getAlbum(String artist, String album) {
        try {
            // Try to get from cache
            AlbumInfo albumObject = lastFmCache.retrieveAlbumInfo(artist, album);
            if (albumObject == null) {
                Album a = Album.getInfo(artist, album, getApiKey());
                if (a != null) {
                    Playlist pl = Playlist.fetchAlbumPlaylist(a.getId(), getApiKey());
                    albumObject = LastFmAlbum.getAlbum(a, pl);
                    lastFmCache.storeAlbumInfo(artist, album, albumObject);
                }
            }
            return albumObject;
        } catch (Exception e) {
            getLogger().error(LogCategories.SERVICE, e);
        }
        return null;
    }

    /**
     * Gets the image of the album
     * 
     * @param artist
     * @param album
     * @return
     */
    public Image getAlbumImage(String artist, String album) {
        Image image = null;
        AlbumInfo a = getAlbum(artist, album);
        if (a != null) {
            image = getImage(a);
        }
        return image;
    }

    /**
     * Gets the album list.
     * 
     * @param artist
     *            the artist
     * 
     * @param hideVariousArtists
     *            if <code>true</code> albums with artist name "Various Artists"
     *            are nor returned
     * 
     * @param minimumSongNumber
     *            albums with less songs than this argument won't be returned
     * 
     * @return the album list
     */
    public AlbumListInfo getAlbumList(String artist, boolean hideVariousArtists, int minimumSongNumber) {
        try {
            // Try to get from cache
            AlbumListInfo albumList = lastFmCache.retrieveAbumList(artist);
            if (albumList == null) {
                Collection<Album> as = Artist.getTopAlbums(artist, getApiKey());
                if (as != null) {
                    AlbumListInfo albums = LastFmAlbumList.getAlbumList(as, artist);

                    List<AlbumInfo> result = new ArrayList<AlbumInfo>();
                    for (AlbumInfo a : albums.getAlbums()) {
                        if (a.getBigCoverURL() != null && !a.getBigCoverURL().isEmpty()) {
                            result.add(a);
                        }
                    }

                    albumList = new LastFmAlbumList();
                    albumList.setArtist(artist);
                    albumList.setAlbums(result);
                    lastFmCache.storeAlbumList(artist, albumList);
                }
            }

            if (albumList != null) {
                List<AlbumInfo> albumsFiltered = null;

                // Apply filter to hide "Various Artists" albums
                if (hideVariousArtists) {
                    albumsFiltered = new ArrayList<AlbumInfo>();
                    for (AlbumInfo albumInfo : albumList.getAlbums()) {
                        if (!albumInfo.getArtist().equals(VARIOUS_ARTISTS)) {
                            albumsFiltered.add(albumInfo);
                        }
                    }
                    albumList.setAlbums(albumsFiltered);
                }

                // Apply filter to hide albums with less than X songs
                if (minimumSongNumber > 0) {
                    albumsFiltered = new ArrayList<AlbumInfo>();
                    for (AlbumInfo albumInfo : albumList.getAlbums()) {
                        AlbumInfo extendedAlbumInfo = getAlbum(artist, albumInfo.getTitle());
                        if (extendedAlbumInfo != null && extendedAlbumInfo.getTracks() != null && extendedAlbumInfo.getTracks().size() >= minimumSongNumber) {
                            albumsFiltered.add(albumInfo);
                        }
                    }
                }

                if (albumsFiltered != null) {
                    albumList.setAlbums(albumsFiltered);
                }
            }
            return albumList;
        } catch (Exception e) {
            getLogger().error(LogCategories.SERVICE, e);
        }
        return null;
    }

    /**
     * Gets the artist top tag.
     * 
     * @param artist
     *            the artist
     * 
     * @return the artist top tag
     */
    public String getArtistTopTag(String artist) {
        try {
            Collection<String> topTags = Artist.getTopTags(artist, getApiKey());
            List<String> tags = new ArrayList<String>(topTags);
            return tags.isEmpty() ? "" : tags.get(0);
        } catch (Exception e) {
            getLogger().error(LogCategories.SERVICE, e);
        }
        return null;
    }

    /**
     * Gets the image.
     * 
     * @param album
     *            the album
     * 
     * @return the image
     */
    public Image getImage(AlbumInfo album) {
        try {
            Image img = null;
            // Try to retrieve from cache
            img = lastFmCache.retrieveAlbumCover(album);
            if (img == null && album.getBigCoverURL() != null && !album.getBigCoverURL().isEmpty()) {
                img = NetworkUtils.getImage(NetworkUtils.getConnection(album.getBigCoverURL(), proxy));
                lastFmCache.storeAlbumCover(album, new ImageIcon(img));
            }

            return img;
        } catch (IOException e) {
            getLogger().error(LogCategories.SERVICE, e);
        }
        return null;
    }

    /**
     * Gets the image of an artist
     * 
     * @param artist
     *            the artist
     * 
     * @return the image
     */
    public Image getImage(ArtistInfo artist) {
        try {
            // Try to retrieve from cache
            Image img = lastFmCache.retrieveArtistThumbImage(artist);
            if (img == null && artist.getImageUrl() != null && !artist.getImageUrl().isEmpty()) {
                // Try to get from Artist.getImages() method 
                img = getArtistImageFromLastFM(artist.getName(), ImageSize.LARGE);

                // if not then get from artist info
                if (img == null) {
                    img = NetworkUtils.getImage(NetworkUtils.getConnection(artist.getImageUrl(), proxy));
                }

                lastFmCache.storeArtistThumbImage(artist, new ImageIcon(img));
            }
            return img;
        } catch (Exception e) {
            getLogger().error(LogCategories.SERVICE, e);
        }
        return null;
    }

    /**
     * Gets the image of the artist
     * 
     * @param artistName
     *            the similar
     * 
     * @return the image
     */
    public Image getImage(String artistName) {
        try {
            // Try to retrieve from cache
            Image img = lastFmCache.retrieveArtistImage(artistName);

            if (img != null) {
                return img;
            }

            // Try to get from LastFM
            img = getArtistImageFromLastFM(artistName, ImageSize.ORIGINAL);

            // Get from similar artist info
            if (img == null) {
                String similarUrl = getSimilarArtists(artistName).getPicture();
                if (!similarUrl.trim().isEmpty()) {
                    img = NetworkUtils.getImage(NetworkUtils.getConnection(similarUrl, proxy));
                }
            }

            if (img != null) {
                lastFmCache.storeArtistImage(artistName, new ImageIcon(img));
            }

            return img;
        } catch (Exception e) {
            getLogger().error(LogCategories.SERVICE, e);
        }
        return null;
    }

    /**
     * Returns current artist image at LastFM
     * 
     * @param artistName
     * @param size
     * @return
     */
    private Image getArtistImageFromLastFM(String artistName, ImageSize size) {
        try {
            // Try to get from Artist.getImages() method 
            PaginatedResult<net.roarsoftware.lastfm.Image> images = Artist.getImages(artistName, 1, 1, getApiKey());
            List<net.roarsoftware.lastfm.Image> imageList = new ArrayList<net.roarsoftware.lastfm.Image>(images.getPageResults());
            if (!imageList.isEmpty()) {
                Set<ImageSize> sizes = imageList.get(0).availableSizes();
                // Try to get the given size
                if (sizes.contains(size)) {
                    return NetworkUtils.getImage(NetworkUtils.getConnection(imageList.get(0).getImageURL(size), proxy));
                }
            }
        } catch (IOException e) {
            getLogger().error(LogCategories.SERVICE, e);
        }
        return null;
    }

    /**
     * Gets the similar artists.
     * 
     * @param artist
     *            the artist
     * 
     * @return the similar artists
     */
    public SimilarArtistsInfo getSimilarArtists(String artist) {
        try {
            // Try to get from cache
            SimilarArtistsInfo similar = lastFmCache.retrieveArtistSimilar(artist);
            
            // Check cache content. Since "match" value changed in last.fm api can be entries in cache with old value.
            // For those entries match is equal or less than 1.0, so discard entries where maximum match is that value
            if (similar != null) {
            	float maxMatch = 0;
            	for (ArtistInfo artistInfo : similar.getArtists()) {
            		float match = 0;
            		try {
            			match = Float.parseFloat(artistInfo.getMatch());
                		if (match > maxMatch) {
                			maxMatch = match;
                		}
            		} catch (NumberFormatException e) {
            			// Not a valid match value, better to discard cache content
            			similar = null;
            		}
            	}
            	if (maxMatch <= 1) {
            		similar = null;
            	}
            }
            
            if (similar == null) {
                Collection<Artist> as = Artist.getSimilar(artist, getApiKey());
                Artist a = Artist.getInfo(artist, getApiKey());
                if (a != null) {
                    similar = LastFmSimilarArtists.getSimilarArtists(as, a);
                    lastFmCache.storeArtistSimilar(artist, similar);
                }
            }
            return similar;
        } catch (Exception e) {
            getLogger().error(LogCategories.SERVICE, e);
        }
        return null;
    }

    /**
     * Gets the wiki text.
     * 
     * @param artist
     *            the artist
     * 
     * @return the wiki text
     */
    public String getWikiText(String artist) {
        try {
            // Try to get from cache
            String wikiText = lastFmCache.retrieveArtistWiki(artist);
            if (wikiText == null) {

                Artist a = Artist.getInfo(artist, ApplicationState.getInstance().getLocale().getLocale(), getApiKey());
                wikiText = a != null ? a.getWikiSummary() : "";
                wikiText = wikiText.replaceAll("<.*?>", "");
                wikiText = StringUtils.unescapeHTML(wikiText, 0);

                lastFmCache.storeArtistWiki(artist, wikiText);
            }
            return wikiText;
        } catch (Exception e) {
            getLogger().error(LogCategories.SERVICE, e);
        }
        return null;
    }

    /**
     * Gets the wiki url.
     * 
     * @param artist
     *            the artist
     * 
     * @return the wiki url
     */
    public String getWikiURL(String artist) {
        return ARTIST_WIKI_URL.replace(ARTIST_WILDCARD, NetworkUtils.encodeString(artist)).replace(LANGUAGE_WILDCARD,
                ApplicationState.getInstance().getLocale().getLocale().getLanguage());
    }

    /**
     * Submits song to Last.fm
     * 
     * @param file
     *            audio file
     * @param secondsPlayed
     *            seconds the audio file has already played
     * @throws ScrobblerException
     */
    private void submit(AudioFile file, long secondsPlayed) throws ScrobblerException {
        // Do all necessary checks
        if (!checkUser() || !checkPassword() || !checkArtist(file) || !checkTitle(file) || !checkDuration(file)) {
            return;
        }

        // Get started to play
        long startedToPlay = System.currentTimeMillis() / 1000 - secondsPlayed;

        getLogger().info(LogCategories.SERVICE, "Trying to submit song to Last.fm");
        try {
            performHandshakeIfNeeded();
            SubmissionData submissionData = new SubmissionData(file.getArtist(), file.getTitle(), file.getAlbum(), file.getDuration(), file.getTrackNumber(), Source.USER, null,
                    startedToPlay);
            ResponseStatus status = scrobbler.submit(submissionData);
            if (status.ok()) {
                getLogger().info(LogCategories.SERVICE, "Song submitted to Last.fm");
            } else {
                handshakePerformed = false;
                lastFmCache.addSubmissionData(new net.sourceforge.atunes.kernel.modules.webservices.lastfm.SubmissionData(file.getArtist(), file.getTitle(), file.getAlbum(), file
                        .getDuration(), file.getTrackNumber(), Source.USER.toString(), (int) startedToPlay));
                throw new ScrobblerException(status.getStatus());
            }

        } catch (IOException e) {
            getLogger().error(LogCategories.SERVICE, e);
            handshakePerformed = false;
            lastFmCache.addSubmissionData(new net.sourceforge.atunes.kernel.modules.webservices.lastfm.SubmissionData(file.getArtist(), file.getTitle(), file.getAlbum(), file
                    .getDuration(), file.getTrackNumber(), Source.USER.toString(), (int) startedToPlay));
            throw new ScrobblerException(e.getMessage());
        }
    }

    /**
     * Adds a song to the list of loved tracks in Last.fm
     * 
     * @param song
     */
    public void addLovedSong(AudioObject song) {
        // Check all necessary conditions to submit request to Last.fm
        if (!checkUser() || !checkAudioFile(song) || !checkPassword() || !checkArtist(song)) {
            return;
        }

        getLogger().info(LogCategories.SERVICE, StringUtils.getString("Trying to submit loved song to Last.fm: ", song.getArtist(), " - ", song.getTitle()));
        Result r = Track.love(song.getArtist(), song.getTitle(), getSession());
        if (r.getStatus().equals(Status.OK)) {
            getLogger().info(LogCategories.SERVICE, StringUtils.getString("Loved song submitted OK"));
        } else {
            getLogger().error(LogCategories.SERVICE, StringUtils.getString("Error while submitting loved song"));
            // TODO: Add a cache to submit
        }

    }

    /**
     * Adds a song to the list of banned tracks in Last.fm
     * 
     * @param song
     */
    public void addBannedSong(AudioObject song) {
        // Check all necessary conditions to submit request to Last.fm
        if (!checkUser() || !checkAudioFile(song) || !checkPassword() || !checkArtist(song)) {
            return;
        }

        getLogger().info(LogCategories.SERVICE, StringUtils.getString("Trying to submit banned song to Last.fm: ", song.getArtist(), " - ", song.getTitle()));
        Result r = Track.ban(song.getArtist(), song.getTitle(), getSession());
        if (r.getStatus().equals(Status.OK)) {
            getLogger().info(LogCategories.SERVICE, StringUtils.getString("Banned song submitted OK"));
        } else {
            getLogger().error(LogCategories.SERVICE, StringUtils.getString("Error while submitting banned song"));
            // TODO: Add a cache to submit
        }
    }

    /**
     * Submits cache data to Last.fm
     * 
     * @throws ScrobblerException
     */
    private void submitCache() throws ScrobblerException {
        // Do all necessary checks
        if (!checkUser() || !checkPassword()) {
            return;
        }

        List<net.sourceforge.atunes.kernel.modules.webservices.lastfm.SubmissionData> collectionWithSubmissionData = lastFmCache.getSubmissionData();
        if (!collectionWithSubmissionData.isEmpty()) {
            // More than MAX_SUBMISSIONS submissions at once are not allowed
            int size = collectionWithSubmissionData.size();
            if (size > MAX_SUBMISSIONS) {
                collectionWithSubmissionData = collectionWithSubmissionData.subList(size - MAX_SUBMISSIONS, size);
            }

            getLogger().info(LogCategories.SERVICE, "Trying to submit cache to Last.fm");
            try {
                performHandshakeIfNeeded();

                List<SubmissionData> submissionDataList = new ArrayList<SubmissionData>();
                for (net.sourceforge.atunes.kernel.modules.webservices.lastfm.SubmissionData submissionData : collectionWithSubmissionData) {
                    SubmissionData sd = new SubmissionData(submissionData.getArtist(), submissionData.getTitle(), submissionData.getAlbum(), submissionData.getDuration(),
                            submissionData.getTrackNumber(), Source.valueOf(submissionData.getSource()), null, submissionData.getStartTime());
                    submissionDataList.add(sd);
                }

                ResponseStatus status = scrobbler.submit(submissionDataList);
                if (status.ok()) {
                    lastFmCache.removeSubmissionData();
                    getLogger().info(LogCategories.SERVICE, "Cache submitted to Last.fm");
                } else {
                    handshakePerformed = false;
                    throw new ScrobblerException(status.getStatus());
                }

            } catch (IOException e) {
                getLogger().error(LogCategories.SERVICE, e);
                handshakePerformed = false;
                throw new ScrobblerException(e.getMessage());
            }
        }

    }

    /**
     * Submits now playing info to Last.fm
     * 
     * @param file
     *            audio file
     * @throws ScrobblerException
     */
    private void submitNowPlayingInfo(AudioFile file) throws ScrobblerException {
        // Do all necessary checks
        if (!checkUser() || !checkPassword() || !checkArtist(file) || !checkTitle(file)) {
            return;
        }

        getLogger().info(LogCategories.SERVICE, "Trying to submit now playing info to Last.fm");
        try {
            performHandshakeIfNeeded();
            ResponseStatus status = scrobbler.nowPlaying(file.getArtist(), file.getTitle(), file.getAlbum(), file.getDuration(), file.getTrackNumber());
            if (status.ok()) {
                getLogger().info(LogCategories.SERVICE, "Now playing info submitted to Last.fm");
            } else {
                handshakePerformed = false;
                throw new ScrobblerException(status.getStatus());
            }
        } catch (IOException e) {
            getLogger().error(LogCategories.SERVICE, e);
            handshakePerformed = false;
            throw new ScrobblerException(e.getMessage());
        }
    }

    /**
     * Performs handshake for submissions if needed
     * 
     * @throws IOException
     * @throws ScrobblerException
     */
    private void performHandshakeIfNeeded() throws IOException, ScrobblerException {
        if (!handshakePerformed) {
            ResponseStatus status = scrobbler.handshake(password);
            if (!status.ok()) {
                throw new ScrobblerException(status.getStatus());
            }
            handshakePerformed = true;
        }
    }

    /**
     * Returns a list of loved tracks from user profile
     * 
     * @return a list of loved tracks from user profile
     */
    public List<LastFmLovedTrack> getLovedTracks() {
        if (this.user != null) {
            try {
                return LastFmLovedTracks.getLovedTracks(this.user, null, proxy);
            } catch (Exception e) {
                getLogger().error(LogCategories.SERVICE, e);
            }
        }
        return Collections.emptyList();
    }

    /**
     * Checks user
     * 
     * @return
     */
    private boolean checkUser() {
        if (user == null || user.equals("")) {
            getLogger().debug(LogCategories.SERVICE, "Don't submit to Last.fm: Empty user");
            return false;
        }
        return true;
    }

    /**
     * Check if parameter is a valid AudioFile
     * 
     * @param ao
     * @return
     */
    private boolean checkAudioFile(AudioObject ao) {
        if (!(ao instanceof AudioFile)) {
            return false;
        }
        return true;
    }

    /**
     * Check password
     * 
     * @return
     */
    private boolean checkPassword() {
        if (password == null || password.equals("")) {
            getLogger().debug(LogCategories.SERVICE, "Don't submit to Last.fm: Empty password");
            return false;
        }
        return true;
    }

    /**
     * Check artist
     * 
     * @param ao
     * @return
     */
    private boolean checkArtist(AudioObject ao) {
        if (net.sourceforge.atunes.kernel.modules.repository.data.Artist.isUnknownArtist(ao.getArtist())) {
            getLogger().debug(LogCategories.SERVICE, "Don't submit to Last.fm: Unknown artist");
            return false;
        }
        return true;
    }

    /**
     * Check title
     * 
     * @param ao
     * @return
     */
    private boolean checkTitle(AudioObject ao) {
        if (ao.getTitle().trim().equals("")) {
            getLogger().debug(LogCategories.SERVICE, "Don't submit to Last.fm: Unknown Title");
            return false;
        }
        return true;
    }

    /**
     * Check duration
     * 
     * @param ao
     * @return
     */
    private boolean checkDuration(AudioObject ao) {
        if (ao.getDuration() < MIN_DURATION_TO_SUBMIT) {
            getLogger().debug(LogCategories.SERVICE, "Don't submit to Last.fm: Lenght < ", MIN_DURATION_TO_SUBMIT);
            return false;
        }
        return true;
    }

    /**
     * Delegate method to clear cache
     * 
     * @return
     */
    public boolean clearCache() {
        return lastFmCache.clearCache();
    }

    /**
     * Return title of an AudioFile known its artist and album
     * 
     * @param f
     * @return
     */
    public String getTitleForFile(AudioFile f) {
        // If has valid artist name, album name, and track number...
        if (!net.sourceforge.atunes.kernel.modules.repository.data.Artist.isUnknownArtist(f.getArtist())
                && !net.sourceforge.atunes.kernel.modules.repository.data.Album.isUnknownAlbum(f.getAlbum()) && f.getTrackNumber() > 0) {
            // Find album
            AlbumInfo albumRetrieved = getAlbum(f.getArtist(), f.getAlbum());
            if (albumRetrieved != null && albumRetrieved.getTracks().size() >= f.getTrackNumber()) {
            	// Get track
            	return albumRetrieved.getTracks().get(f.getTrackNumber() - 1).getTitle();
            }
        }
        return null;
    }

    /**
     * Return track number of an AudioFile known its artist and album
     * 
     * @param f
     * @return
     */
    public int getTrackNumberForFile(AudioFile f) {
        // If has valid artist name, album name and title
        if (!net.sourceforge.atunes.kernel.modules.repository.data.Artist.isUnknownArtist(f.getArtist())
                && !net.sourceforge.atunes.kernel.modules.repository.data.Album.isUnknownAlbum(f.getAlbum()) && !StringUtils.isEmpty(f.getTitle())) {
            // Find album
            AlbumInfo albumRetrieved = getAlbum(f.getArtist(), f.getAlbum());
            if (albumRetrieved != null) {
                // Try to match titles to get track
                int trackIndex = 1;
                for (TrackInfo track : albumRetrieved.getTracks()) {
                    if (track.getTitle().equalsIgnoreCase(f.getTitle())) {
                        return trackIndex;
                    }
                    trackIndex++;
                }
            }
        }
        return 0;
    }

    /**
     * Submit song to Last.fm
     * 
     * @param audioFile
     *            the file
     * @param secondsPlayed
     *            the seconds played
     */
    public void submitToLastFm(final AudioFile audioFile, final long secondsPlayed) {
        if (ApplicationState.getInstance().isLastFmEnabled()) {
            Runnable r = new SubmitToLastFmRunnable(secondsPlayed, audioFile);
            try {
                scrobblerExecutorService.submit(r);
            } catch (RejectedExecutionException e) {
                getLogger().info(LogCategories.SERVICE, "execution of submission runnable rejected");
            }
        }
    }

    /**
     * Submits Last.fm cache
     */
    public void submitCacheToLastFm() {
        if (ApplicationState.getInstance().isLastFmEnabled()) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        submitCache();
                    } catch (ScrobblerException e) {
                        if (e.getStatus() == 2) {
                            getLogger().error(LogCategories.SERVICE, "Authentication failure on Last.fm service");
                        } else {
                            getLogger().error(LogCategories.SERVICE, e.getMessage());
                        }
                    }
                }
            };
            try {
                scrobblerExecutorService.submit(r);
            } catch (RejectedExecutionException e) {
                getLogger().info(LogCategories.SERVICE, "execution of cache submission runnable rejected");
            }
        }
    }

    /**
     * Submit now playing info to Last.fm
     * 
     * @param audioFile
     *            the file
     */
    public void submitNowPlayingInfoToLastFm(final AudioFile audioFile) {
        if (ApplicationState.getInstance().isLastFmEnabled()) {
            Runnable r = new SubmitNowPlayingInfoRunnable(audioFile);
            try {
                scrobblerExecutorService.submit(r);
            } catch (RejectedExecutionException e) {
                getLogger().info(LogCategories.SERVICE, "execution of now playing runnable rejected");
            }
        }
    }

    /**
     * Returns events of an artist TODO: This is a convenience method to allow
     * plugins access last fm services without opening access to api key outside
     * this class
     * 
     * @param artist
     * @return
     */
    public Collection<Event> getArtistEvents(String artist) {
        return Artist.getEvents(artist, getApiKey());
    }

    /**
     * Test if given user and password are correct to login at last.fm
     * 
     * @param user
     * @param password
     */
    public boolean testLogin(String user, String password) {
        return Authenticator.getMobileSession(user, password, getApiKey(), getApiSecret()) != null;
    }

    private static String getApiKey() {
        try {
            return new String(CryptoUtils.decrypt(API_KEY));
        } catch (GeneralSecurityException e) {
            getLogger().internalError(e);
        } catch (IOException e) {
            getLogger().internalError(e);
        }
        return "";
    }

    private static String getApiSecret() {
        try {
            return new String(CryptoUtils.decrypt(API_SECRET));
        } catch (GeneralSecurityException e) {
            getLogger().internalError(e);
        } catch (IOException e) {
            getLogger().internalError(e);
        }
        return "";
    }

    /**
     * Returns session
     * 
     * @return
     */
    private Session getSession() {
        return Authenticator.getMobileSession(user, password, getApiKey(), getApiSecret());
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private static Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}
