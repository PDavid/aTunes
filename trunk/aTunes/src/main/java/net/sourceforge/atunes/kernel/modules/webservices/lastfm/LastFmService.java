/*
 * aTunes 2.2.0-SNAPSHOT
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

import javax.swing.ImageIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmAlbum;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmAlbumList;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmArtistTopTracks;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmSimilarArtists;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILovedTrack;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.utils.CryptoUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.UnknownObjectCheck;
import net.sourceforge.atunes.utils.XMLSerializerService;

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
    private static final int MIN_DURATION_TO_SUBMIT = 30;
    private static final int MAX_SUBMISSIONS = 50;
    private LastFmCache lastFmCache;
    
    private INetworkHandler networkHandler;
    
    private IState state;
    
    private IFrame frame;
    
    private IOSManager osManager;
    
    private XMLSerializerService xmlSerializerService;

    /**
     * @param state
     */
    public void setState(IState state) {
		this.state = state;
	}
    
    /**
     * @param frame
     */
    public void setFrame(IFrame frame) {
		this.frame = frame;
	}
    
    /**
     * @param osManager
     */
    public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
    
    /**
     * @param networkHandler
     */
    public void setNetworkHandler(INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}
    
    /**
     * @param xmlSerializerService
     */
    public void setXmlSerializerService(XMLSerializerService xmlSerializerService) {
		this.xmlSerializerService = xmlSerializerService;
	}
    
    /**
     * @return
     */
    private LastFmCache getCache() {
    	if (lastFmCache == null) {
        	Logger.debug("Initializing LastFmCache");
        	lastFmCache = new LastFmCache(osManager, xmlSerializerService);
    	}
    	return lastFmCache;
    }
    
    /**
     * Default constructor
     */
    public LastFmService() {
        Caller.getInstance().setCache(null);
        Caller.getInstance().setUserAgent(CLIENT_ID);    	
	}
    
    /**
     * Finishes service
     */
    public void finishService() {
    	Logger.debug("Finalizing LastFmCache");
    	getCache().shutdown();
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
            IAlbumInfo albumObject = getCache().retrieveAlbumInfo(artist, album);
            if (albumObject == null) {
                Album a = Album.getInfo(artist, album, getApiKey());
                if (a != null) {
                    Playlist pl = Playlist.fetchAlbumPlaylist(a.getId(), getApiKey());
                    albumObject = LastFmAlbum.getAlbum(a, pl);
                    getCache().storeAlbumInfo(artist, album, albumObject);
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
    public ImageIcon getAlbumImage(String artist, String album) {
        ImageIcon image = null;
        IAlbumInfo a = getAlbum(artist, album);
        if (a != null) {
            image = getAlbumImage(a);
        }
        return image;
    }

    /**
     * Gets the album list.
     * 
     * @param artist
     *            the artist
     * 
     * @return the album list
     */
    public IAlbumListInfo getAlbumList(String artist) {
        // Try to get from cache
		IAlbumListInfo albumList = getCache().retrieveAbumList(artist);
		if (albumList == null) {
		    Collection<Album> as = Artist.getTopAlbums(artist, getApiKey());
		    if (as != null) {
		        IAlbumListInfo albums = LastFmAlbumList.getAlbumList(as, artist);

		        List<IAlbumInfo> result = filterAlbumsFromSource(artist, albums);

		        albumList = new LastFmAlbumList();
		        albumList.setArtist(artist);
		        albumList.setAlbums(result);
		        getCache().storeAlbumList(artist, albumList);
		    }
		}

		return albumList;
    }

	/**
	 * Removes albums with no image or no mbid and with correct artist 
	 * @param artist
	 * @param albums
	 * @return
	 */
	private List<IAlbumInfo> filterAlbumsFromSource(String artist, IAlbumListInfo albums) {
		List<IAlbumInfo> result = new ArrayList<IAlbumInfo>();
		for (IAlbumInfo a : albums.getAlbums()) {
			if (hasMbid(a) && artistMatches(artist, a) && hasImage(a)) {
		        result.add(a);
		    }
		}
		return result;
	}
	
	/**
	 * Returns if album has mbid
	 * @param a
	 * @return
	 */
	private boolean hasMbid(IAlbumInfo a) {
		return !StringUtils.isEmpty(a.getMbid());
	}
	
	/**
	 * Returns if album has image
	 * @param a
	 * @return
	 */
	private boolean hasImage(IAlbumInfo a) {
		return !StringUtils.isEmpty(a.getThumbCoverURL()) && !a.getThumbCoverURL().contains("noimage");
	}
	
	/**
	 * Checks if artist of album is equal to given artist
	 * @param artist
	 * @param album
	 * @return
	 */
	private boolean artistMatches(String artist, IAlbumInfo album) {
		return album.getArtist().equalsIgnoreCase(artist);
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
    public ImageIcon getAlbumImage(IAlbumInfo album) {
        try {
            ImageIcon img = null;
            // Try to retrieve from cache
            img = getCache().retrieveAlbumCover(album);
            if (img == null && !StringUtils.isEmpty(album.getBigCoverURL())) {
            	Image tmp = networkHandler.getImage(networkHandler.getConnection(album.getBigCoverURL())); 
                if (tmp != null) {
                	img = new ImageIcon(tmp);
                    getCache().storeAlbumCover(album, img);
                }
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
     * Gets the thumbnail image of album
     * 
     * @param album
     *            the album
     * 
     * @return the image
     */
    public ImageIcon getAlbumThumbImage(IAlbumInfo album) {
        try {
            ImageIcon img = null;
            // Try to retrieve from cache
            img = getCache().retrieveAlbumCoverThumb(album);
            if (img == null && !StringUtils.isEmpty(album.getThumbCoverURL())) {
                Image tmp = networkHandler.getImage(networkHandler.getConnection(album.getThumbCoverURL()));
                if (tmp != null) {
                	// Resize image for thumb images
                	img = new ImageIcon(ImageUtils.scaleBufferedImageBicubic(tmp, Constants.THUMB_IMAGE_WIDTH, Constants.THUMB_IMAGE_HEIGHT));
                	getCache().storeAlbumCoverThumb(album, img);
                }
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
    public ImageIcon getArtistThumbImage(IArtistInfo artist) {
        try {
            // Try to retrieve from cache
            ImageIcon img = getCache().retrieveArtistThumbImage(artist);
            if (img == null && artist.getImageUrl() != null && !artist.getImageUrl().isEmpty()) {
                // Try to get from Artist.getImages() method 
                img = getArtistImageFromLastFM(artist.getName(), ImageSize.LARGE);

                // if not then get from artist info
                if (img == null) {
                    img = new ImageIcon(networkHandler.getImage(networkHandler.getConnection(artist.getImageUrl())));
                }

                if (img != null) {
                	// Resize image for thumb images
                	img = new ImageIcon(ImageUtils.scaleBufferedImageBicubic(img.getImage(), Constants.THUMB_IMAGE_WIDTH, Constants.THUMB_IMAGE_HEIGHT));
                    getCache().storeArtistThumbImage(artist, img);
                }
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
    public ImageIcon getArtistImage(String artistName) {
        try {
            // Try to retrieve from cache
            ImageIcon img = getCache().retrieveArtistImage(artistName);

            if (img != null) {
                return img;
            }

            // Try to get from LastFM
            img = getArtistImageFromLastFM(artistName, ImageSize.HUGE);

            // Get from similar artist info
            if (img == null) {
                String similarUrl = getSimilarArtists(artistName).getPicture();
                if (!similarUrl.trim().isEmpty()) {
                    img = new ImageIcon(networkHandler.getImage(networkHandler.getConnection(similarUrl)));
                }
            }

            if (img != null) {
            	// Resize image for thumb images
            	img = new ImageIcon(ImageUtils.scaleBufferedImageBicubic(img.getImage(), Constants.ARTIST_IMAGE_SIZE, Constants.ARTIST_IMAGE_SIZE));
                getCache().storeArtistImage(artistName, img);
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
    	IArtistTopTracks topTracks = getCache().retrieveArtistTopTracks(artistName);
    	
    	if (topTracks != null) {
    		return topTracks;
    	}
    	
    	// Try to get from LastFM
    	topTracks = LastFmArtistTopTracks.getTopTracks(artistName, Artist.getTopTracks(artistName, getApiKey()));
    	
    	if (topTracks != null) {
    		getCache().storeArtistTopTracks(artistName, topTracks);
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
    private ImageIcon getArtistImageFromLastFM(String artistName, ImageSize size) {
        try {
            // Try to get from Artist.getImages() method 
            PaginatedResult<de.umass.lastfm.Image> images = Artist.getImages(artistName, 1, 1, getApiKey());
            List<de.umass.lastfm.Image> imageList = new ArrayList<de.umass.lastfm.Image>(images.getPageResults());
            if (!imageList.isEmpty()) {
            	String url = getSmallestURL(imageList.get(0), size);
                if (url != null) {
                    return new ImageIcon(networkHandler.getImage(networkHandler.getConnection(url)));
                }
            }
        } catch (IOException e) {
            Logger.error(e);
        }
        return null;
    }
    
    /**
     * Returns URL of the smallest image
     * @param a
     * @return
     */
    private static String getSmallestURL(de.umass.lastfm.Image a, ImageSize start) {
//    	SMALL: 0
//    	MEDIUM: 1
//    	LARGE: 2
//    	LARGESQUARE: 3
//    	HUGE: 4
//    	EXTRALARGE: 5
//    	MEGA: 6
//    	ORIGINAL: 7

    	ImageSize[] sizes = ImageSize.values();
    	for (int i = start.ordinal(); i < sizes.length; i++) {
    		String url = a.getImageURL(sizes[i]);
    		if (url != null) {
    			return url;
    		}
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
            ISimilarArtistsInfo similar = getCache().retrieveArtistSimilar(artist);
            
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
                    getCache().storeArtistSimilar(artist, similar);
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
            String wikiText = getCache().retrieveArtistWiki(artist);
            if (wikiText == null) {

                Artist a = Artist.getInfo(artist, state.getLocale().getLocale(), null, getApiKey());
                wikiText = a != null ? a.getWikiSummary() : "";
                wikiText = wikiText.replaceAll("<.*?>", "");
                wikiText = StringUtils.unescapeHTML(wikiText, 0);

                getCache().storeArtistWiki(artist, wikiText);
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
        return ARTIST_WIKI_URL.replace(ARTIST_WILDCARD, networkHandler.encodeString(artist)).replace(LANGUAGE_WILDCARD,
                state.getLocale().getLocale().getLanguage());
    }

    /**
     * Check last.fm user and password
     * @return
     */
    private boolean checkCredentials() {
    	return checkUser() && checkPassword();
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
    void submit(IAudioObject file, long secondsPlayed) throws ScrobblerException {
        // Do all necessary checks
        if (!checkCredentials() || !checkArtist(file) || !checkTitle(file) || !checkDuration(file)) {
            return;
        }

        // Get started to play
        long startedToPlay = System.currentTimeMillis() / 1000 - secondsPlayed;

        Logger.info("Trying to submit song to Last.fm");
        ScrobbleResult result = Track.scrobble(file.getArtist(), file.getTitle(), (int) startedToPlay, getSession());

        if (result.isSuccessful() && !result.isIgnored()) {
        	Logger.info("Song submitted to Last.fm");
        } else {
        	getCache().addSubmissionData(new net.sourceforge.atunes.kernel.modules.webservices.lastfm.SubmissionData(file.getArtist(), file.getTitle(), (int) startedToPlay));
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
        if (!checkCredentials() || !checkAudioFile(song) || !checkArtist(song)) {
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
        if (!checkCredentials() || !checkAudioFile(song) || !checkArtist(song)) {
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
        if (!checkCredentials() || !checkAudioFile(song) || !checkArtist(song)) {
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
        if (!checkCredentials()) {
            return;
        }

        List<net.sourceforge.atunes.kernel.modules.webservices.lastfm.SubmissionData> collectionWithSubmissionData = getCache().getSubmissionData();
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
            	getCache().removeSubmissionData();
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
    void submitNowPlayingInfo(ILocalAudioObject file) throws ScrobblerException {
        // Do all necessary checks
        if (!checkCredentials() || !checkArtist(file) || !checkTitle(file)) {
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
    public List<ILovedTrack> getLovedTracks() {
        if (!StringUtils.isEmpty(state.getLastFmUser())) {
            try {
                return LastFmLovedTracks.getLovedTracks(state.getLastFmUser(), null, networkHandler);
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
        if (StringUtils.isEmpty(state.getLastFmUser())) {
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
        if (StringUtils.isEmpty(state.getLastFmPassword())) {
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
        if (UnknownObjectCheck.isUnknownArtist(ao.getArtist())) {
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
        return getCache().clearCache();
    }

    /**
     * Return title of an LocalAudioObject known its artist and album
     * 
     * @param f
     * @return
     */
    public String getTitleForFile(ILocalAudioObject f) {
        // If has valid artist name, album name, and track number...
        if (!UnknownObjectCheck.isUnknownArtist(f.getArtist())
                && !UnknownObjectCheck.isUnknownAlbum(f.getAlbum()) && f.getTrackNumber() > 0) {
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
        if (!UnknownObjectCheck.isUnknownArtist(f.getArtist())
                && !UnknownObjectCheck.isUnknownAlbum(f.getAlbum()) && !StringUtils.isEmpty(f.getTitle())) {
            // Find album
            IAlbumInfo albumRetrieved = getAlbum(f.getArtist(), f.getAlbum());
            if (albumRetrieved != null) {
                // Try to match titles to get track
                int trackIndex = 1;
                for (ITrackInfo track : albumRetrieved.getTracks()) {
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
     * @param secondsPlayed
     * @param taskService
     */
    public void submitToLastFm(final IAudioObject audioFile, final long secondsPlayed, ITaskService taskService) {
        if (state.isLastFmEnabled()) {
        	taskService.submitNow("Submit to Last.fm", new SubmitToLastFmRunnable(secondsPlayed, audioFile, this, frame, state));
        }
    }

    /**
     * Submits Last.fm cache
     * @param service
     */
    public void submitCacheToLastFm(ITaskService service) {
        if (state.isLastFmEnabled()) {
        	service.submitNow("Submit Cache to Last.fm", new Runnable() {

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
            });
        }
    }

    /**
     * Submit now playing info to Last.fm
     * 
     * @param audioFile
     *            the file
     */
    public void submitNowPlayingInfoToLastFm(final ILocalAudioObject audioFile, ITaskService taskService) {
        if (state.isLastFmEnabled()) {
        	taskService.submitNow("Submit Now Playing to Last.fm", new SubmitNowPlayingInfoRunnable(audioFile, this, frame, state));
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
        return Authenticator.getMobileSession(state.getLastFmUser(), state.getLastFmPassword(), getApiKey(), getApiSecret());
    }

	/**
	 * Flush cache
	 */
	public void flush() {
		getCache().flush();
	}
}
