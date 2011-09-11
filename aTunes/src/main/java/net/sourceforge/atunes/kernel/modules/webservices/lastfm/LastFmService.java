/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.kernel.modules.context.TrackInfo;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.proxy.ExtendedProxy;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmAlbum;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmAlbumList;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmArtistTopTracks;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmLovedTrack;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmSimilarArtists;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.CryptoUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.model.PluginApi;

import de.umass.lastfm.Album;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Authenticator;
import de.umass.lastfm.Caller;
import de.umass.lastfm.Event;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Playlist;
import de.umass.lastfm.Result;
import de.umass.lastfm.Result.Status;
import de.umass.lastfm.Session;
import de.umass.lastfm.Tag;
import de.umass.lastfm.Track;
import de.umass.lastfm.scrobble.ScrobbleResult;

/**
 * This class is responsible of retrieve information from Last.fm web services.
 */
@PluginApi
public final class LastFmService {

    private final class SubmitNowPlayingInfoRunnable implements Runnable {

		private final ILocalAudioObject audioFile;

		private SubmitNowPlayingInfoRunnable(ILocalAudioObject audioFile) {
			this.audioFile = audioFile;
		}

		@Override
		public void run() {
		    try {
		        submitNowPlayingInfo(audioFile);
		    } catch (ScrobblerException e) {
		        if (e.getStatus() == 2) {
		            Logger.error("Authentication failure on Last.fm service");
		            SwingUtilities.invokeLater(new Runnable() {

		                @Override
		                public void run() {
		                    GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("LASTFM_USER_ERROR"));
		                    // Disable service by deleting password
		                    state.setLastFmEnabled(false);
		                }
		            });
		        } else {
		            Logger.error(e.getMessage());
		        }
		    }
		}
	}

	private final class SubmitToLastFmRunnable implements Runnable {

		private final long secondsPlayed;
		private final IAudioObject audioFile;

		private SubmitToLastFmRunnable(long secondsPlayed, IAudioObject audioFile) {
			this.secondsPlayed = secondsPlayed;
			this.audioFile = audioFile;
		}

		@Override
		public void run() {
		    try {
		        submit(audioFile, secondsPlayed);
		    } catch (ScrobblerException e) {
		        if (e.getStatus() == 2) {
		            Logger.error("Authentication failure on Last.fm service");
		            SwingUtilities.invokeLater(new Runnable() {

		                @Override
		                public void run() {
		                    GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("LASTFM_USER_ERROR"));
		                    // Disable service by deleting password
		                    state.setLastFmEnabled(false);
		                }
		            });
		        } else {
		            Logger.error(e.getMessage());
		        }
		    }
		}
	}

	/*
     * DO NOT USE THESE KEYS FOR OTHER APPLICATIONS THAN aTunes!
     */
    private static final byte[] API_KEY = {78, 119, -39, -5, -89, -107, -38, 41, -87, -107, 122, 98, -33, 46, 32, -47, -44, 54, 97, 67, 105, 122, 11, -26, -81, 90, 94, 55, 121,
        11, 14, -104, -70, 123, -88, -70, -108, 75, -77, 98};
    private static final byte[] API_SECRET = {38, -8, 33, 63, 10, 86, 29, -2, 87, -63, 67, 111, -5, -101, -87, 38, 2, 35, 86, -86, 19, 110, -81, -115, 102, 54, -24, 27, 40, -124,
        -57, -62, -70, 123, -88, -70, -108, 75, -77, 98};
    private static final String CLIENT_ID = "atu";
    private static final String ARTIST_WILDCARD = "(%ARTIST%)";
    private static final String LANGUAGE_PARAM = "?setlang=";
    private static final String LANGUAGE_WILDCARD = "(%LANGUAGE%)";
    private static final String ARTIST_WIKI_URL = StringUtils.getString("http://www.lastfm.com/music/", ARTIST_WILDCARD, "/+wiki", LANGUAGE_PARAM, LANGUAGE_WILDCARD);
    private static final String VARIOUS_ARTISTS = "Various Artists";
    private static final int MIN_DURATION_TO_SUBMIT = 30;
    private static final int MAX_SUBMISSIONS = 50;
    private ExtendedProxy proxy;
    private String user;
    private String password;
    private static LastFmCache lastFmCache = new LastFmCache();
    /** Submissions need single threaded executor */
    private ExecutorService scrobblerExecutorService = Executors.newSingleThreadExecutor();
    
    private IState state;

    /**
     * Instantiates a new Last.fm service
     * 
     * @param state
     */
    public LastFmService(IState state) {
    	this.state = state;
    	updateService();
    }
    
    /**
     * Updates service after a configuration change
     */
    public void updateService() {
        ExtendedProxy proxy = null;
        try {
            if (state.getProxy() != null) {
                proxy = ExtendedProxy.getProxy(state.getProxy());
            }
        } catch (Exception e) {
            Logger.error(e);
        }

        this.proxy = proxy;
        this.user = state.getLastFmUser();
        this.password = state.getLastFmPassword();
        
        Logger.debug("User: ", user);
        Logger.debug("Password: ", password != null ? password.hashCode() : null);
        Caller.getInstance().setCache(null);
        Caller.getInstance().setProxy(proxy);
        Caller.getInstance().setUserAgent(CLIENT_ID);    	
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
    public IAlbumInfo getAlbum(String artist, String album) {
        try {
            // Try to get from cache
            IAlbumInfo albumObject = lastFmCache.retrieveAlbumInfo(artist, album);
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
            Logger.error(e);
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
        IAlbumInfo a = getAlbum(artist, album);
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
    public IAlbumListInfo getAlbumList(String artist, boolean hideVariousArtists, int minimumSongNumber) {
        try {
            // Try to get from cache
            IAlbumListInfo albumList = lastFmCache.retrieveAbumList(artist);
            if (albumList == null) {
                Collection<Album> as = Artist.getTopAlbums(artist, getApiKey());
                if (as != null) {
                    IAlbumListInfo albums = LastFmAlbumList.getAlbumList(as, artist);

                    List<IAlbumInfo> result = new ArrayList<IAlbumInfo>();
                    for (IAlbumInfo a : albums.getAlbums()) {
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
                List<IAlbumInfo> albumsFiltered = null;

                // Apply filter to hide "Various Artists" albums
                if (hideVariousArtists) {
                    albumsFiltered = new ArrayList<IAlbumInfo>();
                    for (IAlbumInfo albumInfo : albumList.getAlbums()) {
                        if (!albumInfo.getArtist().equals(VARIOUS_ARTISTS)) {
                            albumsFiltered.add(albumInfo);
                        }
                    }
                    albumList.setAlbums(albumsFiltered);
                }

                // Apply filter to hide albums with less than X songs
                if (minimumSongNumber > 0) {
                    albumsFiltered = new ArrayList<IAlbumInfo>();
                    for (IAlbumInfo albumInfo : albumList.getAlbums()) {
                        IAlbumInfo extendedAlbumInfo = getAlbum(artist, albumInfo.getTitle());
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
            Logger.error(e);
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
            Collection<Tag> topTags = Artist.getTopTags(artist, getApiKey());
            List<String> tags = new ArrayList<String>();
            for (Tag t : topTags) {
            	tags.add(t.getName());
            }
            return tags.isEmpty() ? "" : tags.get(0);
        } catch (Exception e) {
            Logger.error(e);
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
    public Image getImage(IAlbumInfo album) {
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
        	// Sometimes urls given by last.fm are forbidden, so avoid show full error stack traces
        	Logger.error(e.getMessage());
            Logger.debug(e);
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
    public Image getThumbImage(IArtistInfo artist) {
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
            Logger.error(e);
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
            Logger.error(e);
        }
        return null;
    }
    
    /**
     * Returns top tracks for given artist name
     * @param artistName
     * @return 
     */
    public IArtistTopTracks getTopTracks(String artistName) {
    	// Try to retrieve from cache
    	IArtistTopTracks topTracks = lastFmCache.retrieveArtistTopTracks(artistName);
    	
    	if (topTracks != null) {
    		return topTracks;
    	}
    	
    	// Try to get from LastFM
    	topTracks = LastFmArtistTopTracks.getTopTracks(artistName, Artist.getTopTracks(artistName, getApiKey()));
    	
    	if (topTracks != null) {
    		lastFmCache.storeArtistTopTracks(artistName, topTracks);
    	}
    	
    	return topTracks;
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
            PaginatedResult<de.umass.lastfm.Image> images = Artist.getImages(artistName, 1, 1, getApiKey());
            List<de.umass.lastfm.Image> imageList = new ArrayList<de.umass.lastfm.Image>(images.getPageResults());
            if (!imageList.isEmpty()) {
                Set<ImageSize> sizes = imageList.get(0).availableSizes();
                // Try to get the given size
                if (sizes.contains(size)) {
                    return NetworkUtils.getImage(NetworkUtils.getConnection(imageList.get(0).getImageURL(size), proxy));
                }
            }
        } catch (IOException e) {
            Logger.error(e);
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
    public ISimilarArtistsInfo getSimilarArtists(String artist) {
        try {
            // Try to get from cache
            ISimilarArtistsInfo similar = lastFmCache.retrieveArtistSimilar(artist);
            
            // Check cache content. Since "match" value changed in last.fm api can be entries in cache with old value.
            // For those entries match is equal or less than 1.0, so discard entries where maximum match is that value
            if (similar != null) {
            	float maxMatch = 0;
            	for (IArtistInfo artistInfo : similar.getArtists()) {
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
            Logger.error(e);
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

                Artist a = Artist.getInfo(artist, state.getLocale().getLocale(), null, getApiKey());
                wikiText = a != null ? a.getWikiSummary() : "";
                wikiText = wikiText.replaceAll("<.*?>", "");
                wikiText = StringUtils.unescapeHTML(wikiText, 0);

                lastFmCache.storeArtistWiki(artist, wikiText);
            }
            return wikiText;
        } catch (Exception e) {
            Logger.error(e);
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
                state.getLocale().getLocale().getLanguage());
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
    private void submit(IAudioObject file, long secondsPlayed) throws ScrobblerException {
        // Do all necessary checks
        if (!checkUser() || !checkPassword() || !checkArtist(file) || !checkTitle(file) || !checkDuration(file)) {
            return;
        }

        // Get started to play
        long startedToPlay = System.currentTimeMillis() / 1000 - secondsPlayed;

        Logger.info("Trying to submit song to Last.fm");
        ScrobbleResult result = Track.scrobble(file.getArtist(), file.getTitle(), (int) startedToPlay, getSession());

        if (result.isSuccessful() && !result.isIgnored()) {
        	Logger.info("Song submitted to Last.fm");
        } else {
        	lastFmCache.addSubmissionData(new net.sourceforge.atunes.kernel.modules.webservices.lastfm.SubmissionData(file.getArtist(), file.getTitle(), (int) startedToPlay));
        	throw new ScrobblerException(result.getStatus().toString());
        }
    }

    /**
     * Adds a song to the list of loved tracks in Last.fm
     * 
     * @param song
     */
    public void addLovedSong(IAudioObject song) {
        // Check all necessary conditions to submit request to Last.fm
        if (!checkUser() || !checkAudioFile(song) || !checkPassword() || !checkArtist(song)) {
            return;
        }

        Logger.info(StringUtils.getString("Trying to submit loved song to Last.fm: ", song.getArtist(), " - ", song.getTitle()));
        Result r = Track.love(song.getArtist(), song.getTitle(), getSession());
        if (r.getStatus().equals(Status.OK)) {
            Logger.info(StringUtils.getString("Loved song submitted OK"));
        } else {
            Logger.error(StringUtils.getString("Error while submitting loved song"));
            // TODO: Add a cache to submit
        }

    }

    /**
     * Removes a song from the list of loved tracks in Last.fm
     * 
     * @param song
     */
    public void removeLovedSong(IAudioObject song) {
        // Check all necessary conditions to submit request to Last.fm
        if (!checkUser() || !checkAudioFile(song) || !checkPassword() || !checkArtist(song)) {
            return;
        }

        Logger.info(StringUtils.getString("Trying to unlove song to Last.fm: ", song.getArtist(), " - ", song.getTitle()));
        Result r = Track.unlove(song.getArtist(), song.getTitle(), getSession());
        if (r.getStatus().equals(Status.OK)) {
            Logger.info(StringUtils.getString("Successfully unloved song"));
        } else {
            Logger.error(StringUtils.getString("Error while unloving song"));
            // TODO: Add a cache to submit
        }

    }

    /**
     * Adds a song to the list of banned tracks in Last.fm
     * 
     * @param song
     */
    public void addBannedSong(IAudioObject song) {
        // Check all necessary conditions to submit request to Last.fm
        if (!checkUser() || !checkAudioFile(song) || !checkPassword() || !checkArtist(song)) {
            return;
        }

        Logger.info(StringUtils.getString("Trying to submit banned song to Last.fm: ", song.getArtist(), " - ", song.getTitle()));
        Result r = Track.ban(song.getArtist(), song.getTitle(), getSession());
        if (r.getStatus().equals(Status.OK)) {
            Logger.info(StringUtils.getString("Banned song submitted OK"));
        } else {
            Logger.error(StringUtils.getString("Error while submitting banned song"));
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

            Logger.info("Trying to submit cache to Last.fm");
            ScrobbleResult result = null;
            boolean ok = true;
            for (net.sourceforge.atunes.kernel.modules.webservices.lastfm.SubmissionData submissionData : collectionWithSubmissionData) {                	
            	result = Track.scrobble(submissionData.getArtist(), submissionData.getTitle(), submissionData.getStartTime(), getSession());
            	ok = ok || result.isSuccessful();
            }

            if (ok) {
            	lastFmCache.removeSubmissionData();
            	Logger.info("Cache submitted to Last.fm");
            } else {
            	throw new ScrobblerException(result.getStatus().toString());
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
    private void submitNowPlayingInfo(ILocalAudioObject file) throws ScrobblerException {
        // Do all necessary checks
        if (!checkUser() || !checkPassword() || !checkArtist(file) || !checkTitle(file)) {
            return;
        }

        Logger.info("Trying to submit now playing info to Last.fm");
        ScrobbleResult status = Track.updateNowPlaying(file.getArtist(), file.getTitle(), getSession());
        if (status.isSuccessful() && !status.isIgnored()) {
        	Logger.info("Now playing info submitted to Last.fm");
        } else {
        	throw new ScrobblerException(status.getStatus().toString());
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
                Logger.error(e);
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
            Logger.debug("Don't submit to Last.fm: Empty user");
            return false;
        }
        return true;
    }

    /**
     * Check if parameter is a valid LocalAudioObject
     * 
     * @param ao
     * @return
     */
    private boolean checkAudioFile(IAudioObject ao) {
        if (!(ao instanceof ILocalAudioObject)) {
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
            Logger.debug("Don't submit to Last.fm: Empty password");
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
    private boolean checkArtist(IAudioObject ao) {
        if (net.sourceforge.atunes.model.Artist.isUnknownArtist(ao.getArtist())) {
            Logger.debug("Don't submit to Last.fm: Unknown artist");
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
    private boolean checkTitle(IAudioObject ao) {
        if (ao.getTitle().trim().equals("")) {
            Logger.debug("Don't submit to Last.fm: Unknown Title");
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
    private boolean checkDuration(IAudioObject ao) {
        if (ao.getDuration() < MIN_DURATION_TO_SUBMIT) {
            Logger.debug("Don't submit to Last.fm: Lenght < ", MIN_DURATION_TO_SUBMIT);
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
     * Return title of an LocalAudioObject known its artist and album
     * 
     * @param f
     * @return
     */
    public String getTitleForFile(ILocalAudioObject f) {
        // If has valid artist name, album name, and track number...
        if (!net.sourceforge.atunes.model.Artist.isUnknownArtist(f.getArtist())
                && !net.sourceforge.atunes.model.Album.isUnknownAlbum(f.getAlbum()) && f.getTrackNumber() > 0) {
            // Find album
            IAlbumInfo albumRetrieved = getAlbum(f.getArtist(), f.getAlbum());
            if (albumRetrieved != null && albumRetrieved.getTracks().size() >= f.getTrackNumber()) {
            	// Get track
            	return albumRetrieved.getTracks().get(f.getTrackNumber() - 1).getTitle();
            }
        }
        return null;
    }

    /**
     * Return track number of an LocalAudioObject known its artist and album
     * 
     * @param f
     * @return
     */
    public int getTrackNumberForFile(ILocalAudioObject f) {
        // If has valid artist name, album name and title
        if (!net.sourceforge.atunes.model.Artist.isUnknownArtist(f.getArtist())
                && !net.sourceforge.atunes.model.Album.isUnknownAlbum(f.getAlbum()) && !StringUtils.isEmpty(f.getTitle())) {
            // Find album
            IAlbumInfo albumRetrieved = getAlbum(f.getArtist(), f.getAlbum());
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
    public void submitToLastFm(final IAudioObject audioFile, final long secondsPlayed) {
        if (state.isLastFmEnabled()) {
            Runnable r = new SubmitToLastFmRunnable(secondsPlayed, audioFile);
            try {
                scrobblerExecutorService.submit(r);
            } catch (RejectedExecutionException e) {
                Logger.info("execution of submission runnable rejected");
            }
        }
    }

    /**
     * Submits Last.fm cache
     */
    public void submitCacheToLastFm() {
        if (state.isLastFmEnabled()) {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    try {
                        submitCache();
                    } catch (ScrobblerException e) {
                        if (e.getStatus() == 2) {
                            Logger.error("Authentication failure on Last.fm service");
                        } else {
                            Logger.error(e.getMessage());
                        }
                    }
                }
            };
            try {
                scrobblerExecutorService.submit(r);
            } catch (RejectedExecutionException e) {
                Logger.info("execution of cache submission runnable rejected");
            }
        }
    }

    /**
     * Submit now playing info to Last.fm
     * 
     * @param audioFile
     *            the file
     */
    public void submitNowPlayingInfoToLastFm(final ILocalAudioObject audioFile) {
        if (state.isLastFmEnabled()) {
            Runnable r = new SubmitNowPlayingInfoRunnable(audioFile);
            try {
                scrobblerExecutorService.submit(r);
            } catch (RejectedExecutionException e) {
                Logger.info("execution of now playing runnable rejected");
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
            Logger.error(e);
        } catch (IOException e) {
            Logger.error(e);
        }
        return "";
    }

    private static String getApiSecret() {
        try {
            return new String(CryptoUtils.decrypt(API_SECRET));
        } catch (GeneralSecurityException e) {
            Logger.error(e);
        } catch (IOException e) {
            Logger.error(e);
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
}
