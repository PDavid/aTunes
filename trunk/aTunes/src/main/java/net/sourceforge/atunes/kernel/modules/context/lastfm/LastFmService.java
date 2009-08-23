/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.context.lastfm;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.roarsoftware.lastfm.Album;
import net.roarsoftware.lastfm.Artist;
import net.roarsoftware.lastfm.Authenticator;
import net.roarsoftware.lastfm.Caller;
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
import net.sourceforge.atunes.kernel.modules.context.lastfm.data.LastFmAlbum;
import net.sourceforge.atunes.kernel.modules.context.lastfm.data.LastFmAlbumList;
import net.sourceforge.atunes.kernel.modules.context.lastfm.data.LastFmLovedTrack;
import net.sourceforge.atunes.kernel.modules.context.lastfm.data.LastFmSimilarArtists;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class LastFmService.
 * 
 * This class is responsible of retrieve information from Last.fm web services.
 */
public class LastFmService {

    private static Logger logger = new Logger();

    /*
     * DO NOT USE THESE KEYS FOR OTHER APPLICATIONS THAN aTunes!
     */
    private static final String API_KEY = "e4dd8c105b26ecbf84e2806d7a9660f3";
    private static final String API_SECRET = "056392d397eb5fec6f4d07f38285d1a2";
    private static final String CLIENT_ID = "atu";
    private static final String CLIENT_VERSION = Constants.VERSION.toShortString();

    private static final String ARTIST_WILDCARD = "(%ARTIST%)";
    private static final String LANGUAGE_PARAM = "?setlang=";
    private static final String LANGUAGE_WILDCARD = "(%LANGUAGE%)";
    private static final String ARTIST_WIKI_URL = StringUtils.getString("http://www.lastfm.com/music/", ARTIST_WILDCARD, "/+wiki", LANGUAGE_PARAM, LANGUAGE_WILDCARD);

    private static final String VARIOUS_ARTISTS = "Various Artists";

    private static final int MIN_DURATION_TO_SUBMIT = 30;

    private static final int MAX_SUBMISSIONS = 50;

    private Proxy proxy;

    private Scrobbler scrobbler;
    private String user;
    private String password;
    private boolean handshakePerformed;
    private Locale locale;

    private LastFmCache lastFmCache;

    /**
     * Instantiates a new Last.fm service
     * 
     * @param proxy
     *            the proxy
     * @param user
     *            the Last.fm username
     * @param password
     *            the Last.fm password
     */
    public LastFmService(Proxy proxy, String user, String password, Locale locale, LastFmCache lastFmCache) {
        this.proxy = proxy;
        this.user = user;
        this.password = password;
        Caller.getInstance().setCache(null);
        Caller.getInstance().setProxy(proxy);
        Caller.getInstance().setUserAgent(CLIENT_ID);
        // Use encoded version name to avoid errors from server
        scrobbler = Scrobbler.newScrobbler(CLIENT_ID, NetworkUtils.encodeString(CLIENT_VERSION), user);
        this.handshakePerformed = false;
        this.locale = locale;
        this.lastFmCache = lastFmCache;
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
                Album a = Album.getInfo(artist, album, API_KEY);
                if (a != null) {
                    Playlist pl = Playlist.fetchAlbumPlaylist(a.getId(), API_KEY);
                    albumObject = LastFmAlbum.getAlbum(a, pl);
                    lastFmCache.storeAlbumInfo(artist, album, albumObject);
                }
            }
            return albumObject;
        } catch (Exception e) {
            logger.error(LogCategories.SERVICE, e);
        }
        return null;
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
                Collection<Album> as = Artist.getTopAlbums(artist, API_KEY);
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
            logger.error(LogCategories.SERVICE, e);
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
            Collection<String> topTags = Artist.getTopTags(artist, API_KEY);
            List<String> tags = new ArrayList<String>(topTags);
            return tags.isEmpty() ? "" : tags.get(0);
        } catch (Exception e) {
            logger.error(LogCategories.SERVICE, e);
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
                lastFmCache.storeAlbumCover(album, img);
            }

            return img;
        } catch (IOException e) {
            logger.error(LogCategories.SERVICE, e);
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

                lastFmCache.storeArtistThumbImage(artist, img);
            }
            return img;
        } catch (Exception e) {
            logger.error(LogCategories.SERVICE, e);
        }
        return null;
    }

    /**
     * Gets the image of the artist
     * 
     * @param similar
     *            the similar
     * 
     * @return the image
     */
    public Image getImage(SimilarArtistsInfo similar) {
        try {
            // Try to retrieve from cache
            Image img = lastFmCache.retrieveArtistImage(similar);

            if (img != null) {
                return img;
            }

            // Try to get from LastFM
            img = getArtistImageFromLastFM(similar.getArtistName(), ImageSize.ORIGINAL);

            // Get from similar artist info
            if (img == null) {
                String similarUrl = similar.getPicture();
                if (!similarUrl.trim().isEmpty()) {
                    img = NetworkUtils.getImage(NetworkUtils.getConnection(similarUrl, proxy));
                }
            }

            if (img != null) {
                lastFmCache.storeArtistImage(similar, img);
            }

            return img;
        } catch (Exception e) {
            logger.error(LogCategories.SERVICE, e);
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
            PaginatedResult<net.roarsoftware.lastfm.Image> images = Artist.getImages(artistName, 1, 1, API_KEY);
            List<net.roarsoftware.lastfm.Image> imageList = new ArrayList<net.roarsoftware.lastfm.Image>(images.getPageResults());
            if (!imageList.isEmpty()) {
                Set<ImageSize> sizes = imageList.get(0).availableSizes();
                // Try to get the given size
                if (sizes.contains(size)) {
                    return NetworkUtils.getImage(NetworkUtils.getConnection(imageList.get(0).getImageURL(size), proxy));
                }
            }
        } catch (IOException e) {
            logger.error(LogCategories.SERVICE, e);
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
            if (similar == null) {
                Collection<Artist> as = Artist.getSimilar(artist, API_KEY);
                Artist a = Artist.getInfo(artist, API_KEY);
                if (a != null) {
                    similar = LastFmSimilarArtists.getSimilarArtists(as, a);
                    lastFmCache.storeArtistSimilar(artist, similar);
                }
            }
            return similar;
        } catch (Exception e) {
            logger.error(LogCategories.SERVICE, e);
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

                Artist a = Artist.getInfo(artist, locale, API_KEY);
                wikiText = a != null ? a.getWikiSummary() : "";
                wikiText = wikiText.replaceAll("<.*?>", "");
                wikiText = StringUtils.unescapeHTML(wikiText, 0);

                lastFmCache.storeArtistWiki(artist, wikiText);
            }
            return wikiText;
        } catch (Exception e) {
            logger.error(LogCategories.SERVICE, e);
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
        return ARTIST_WIKI_URL.replace(ARTIST_WILDCARD, NetworkUtils.encodeString(artist)).replace(LANGUAGE_WILDCARD, locale.getLanguage());
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
    public void submit(AudioFile file, long secondsPlayed) throws ScrobblerException {
        // Do all necessary checks
        if (!checkUser() || !checkPassword() || !checkArtist(file) || !checkTitle(file) || !checkDuration(file)) {
            return;
        }

        // Get started to play
        long startedToPlay = System.currentTimeMillis() / 1000 - secondsPlayed;

        logger.info(LogCategories.SERVICE, "Trying to submit song to Last.fm");
        try {
            performHandshakeIfNeeded();
            SubmissionData submissionData = new SubmissionData(file.getArtist(), file.getTitle(), file.getAlbum(), (int) file.getDuration(), file.getTrackNumber(), Source.USER,
                    null, startedToPlay);
            ResponseStatus status = scrobbler.submit(submissionData);
            if (status.ok()) {
                logger.info(LogCategories.SERVICE, "Song submitted to Last.fm");
            } else {
                handshakePerformed = false;
                lastFmCache.addSubmissionData(new net.sourceforge.atunes.kernel.modules.context.lastfm.SubmissionData(file.getArtist(), file.getTitle(), file.getAlbum(),
                        (int) file.getDuration(), file.getTrackNumber(), Source.USER.toString(), (int) startedToPlay));
                throw new ScrobblerException(status.getStatus());
            }

        } catch (IOException e) {
            logger.error(LogCategories.SERVICE, e);
            handshakePerformed = false;
            lastFmCache.addSubmissionData(new net.sourceforge.atunes.kernel.modules.context.lastfm.SubmissionData(file.getArtist(), file.getTitle(), file.getAlbum(), (int) file
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

        logger.info(LogCategories.SERVICE, StringUtils.getString("Trying to submit loved song to Last.fm: ", song.getArtist(), " - ", song.getTitle()));
        Session s = Authenticator.getMobileSession(user, password, API_KEY, API_SECRET);
        Result r = Track.love(song.getArtist(), song.getTitle(), s);
        if (r.getStatus().equals(Status.OK)) {
            logger.info(LogCategories.SERVICE, StringUtils.getString("Loved song submitted OK"));
        } else {
            logger.error(LogCategories.SERVICE, StringUtils.getString("Error while submitting loved song"));
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

        logger.info(LogCategories.SERVICE, StringUtils.getString("Trying to submit banned song to Last.fm: ", song.getArtist(), " - ", song.getTitle()));
        Session s = Authenticator.getMobileSession(user, password, API_KEY, API_SECRET);
        Result r = Track.ban(song.getArtist(), song.getTitle(), s);
        if (r.getStatus().equals(Status.OK)) {
            logger.info(LogCategories.SERVICE, StringUtils.getString("Banned song submitted OK"));
        } else {
            logger.error(LogCategories.SERVICE, StringUtils.getString("Error while submitting banned song"));
            // TODO: Add a cache to submit
        }
    }

    /**
     * Submits cache data to Last.fm
     * 
     * @throws ScrobblerException
     */
    public void submitCache() throws ScrobblerException {
        // Do all necessary checks
        if (!checkUser() || !checkPassword()) {
            return;
        }

        List<net.sourceforge.atunes.kernel.modules.context.lastfm.SubmissionData> collectionWithSubmissionData = lastFmCache.getSubmissionData();
        if (!collectionWithSubmissionData.isEmpty()) {
            // More than MAX_SUBMISSIONS submissions at once are not allowed
            int size = collectionWithSubmissionData.size();
            if (size > MAX_SUBMISSIONS) {
                collectionWithSubmissionData = collectionWithSubmissionData.subList(size - MAX_SUBMISSIONS, size);
            }

            logger.info(LogCategories.SERVICE, "Trying to submit cache to Last.fm");
            try {
                performHandshakeIfNeeded();

                List<SubmissionData> submissionDataList = new ArrayList<SubmissionData>();
                for (net.sourceforge.atunes.kernel.modules.context.lastfm.SubmissionData submissionData : collectionWithSubmissionData) {
                    SubmissionData sd = new SubmissionData(submissionData.getArtist(), submissionData.getTitle(), submissionData.getAlbum(), submissionData.getDuration(),
                            submissionData.getTrackNumber(), Source.valueOf(submissionData.getSource()), null, submissionData.getStartTime());
                    submissionDataList.add(sd);
                }

                ResponseStatus status = scrobbler.submit(submissionDataList);
                if (status.ok()) {
                    lastFmCache.removeSubmissionData();
                    logger.info(LogCategories.SERVICE, "Cache submitted to Last.fm");
                } else {
                    handshakePerformed = false;
                    throw new ScrobblerException(status.getStatus());
                }

            } catch (IOException e) {
                logger.error(LogCategories.SERVICE, e);
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
    public void submitNowPlayingInfo(AudioFile file) throws ScrobblerException {
        // Do all necessary checks
        if (!checkUser() || !checkPassword() || !checkArtist(file) || !checkTitle(file)) {
            return;
        }

        logger.info(LogCategories.SERVICE, "Trying to submit now playing info to Last.fm");
        try {
            performHandshakeIfNeeded();
            ResponseStatus status = scrobbler.nowPlaying(file.getArtist(), file.getTitle(), file.getAlbum(), (int) file.getDuration(), file.getTrackNumber());
            if (status.ok()) {
                logger.info(LogCategories.SERVICE, "Now playing info submitted to Last.fm");
            } else {
                handshakePerformed = false;
                throw new ScrobblerException(status.getStatus());
            }
        } catch (IOException e) {
            logger.error(LogCategories.SERVICE, e);
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
                logger.error(LogCategories.SERVICE, e);
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
            logger.debug(LogCategories.SERVICE, "Don't submit to Last.fm: Empty user");
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
        if (ao == null || !(ao instanceof AudioFile)) {
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
            logger.debug(LogCategories.SERVICE, "Don't submit to Last.fm: Empty password");
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
        if (net.sourceforge.atunes.kernel.modules.repository.model.Artist.isUnknownArtist(ao.getArtist())) {
            logger.debug(LogCategories.SERVICE, "Don't submit to Last.fm: Unknown artist");
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
            logger.debug(LogCategories.SERVICE, "Don't submit to Last.fm: Unknown Title");
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
            logger.debug(LogCategories.SERVICE, StringUtils.getString("Don't submit to Last.fm: Lenght < ", MIN_DURATION_TO_SUBMIT));
            return false;
        }
        return true;
    }
}
