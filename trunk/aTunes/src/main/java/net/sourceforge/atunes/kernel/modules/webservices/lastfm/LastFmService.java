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

import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILovedTrack;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.Logger;

import org.commonjukebox.plugins.model.PluginApi;

import de.umass.lastfm.Caller;
import de.umass.lastfm.Event;

/**
 * This class is responsible of retrieve information from Last.fm web services.
 */
@PluginApi
public final class LastFmService {

    private LastFmCache lastFmCache;
    
    private LastFmAPIKey lastFmAPIKey;
    
    private LastFmLogin lastFmLogin;
    
    private LastFmUserServices lastFmUserServices;
    
    private LastFmAlbumServices lastFmAlbumServices;
    
    private LastFmArtistServices lastFmArtistServices;
    
    private LastFmSongServices lastFmSongServices;
    
    /**
     * @param lastFmSongServices
     */
    public void setLastFmSongServices(LastFmSongServices lastFmSongServices) {
		this.lastFmSongServices = lastFmSongServices;
	}
    
    /**
     * @param lastFmArtistServices
     */
    public void setLastFmArtistServices(LastFmArtistServices lastFmArtistServices) {
		this.lastFmArtistServices = lastFmArtistServices;
	}
    
    /**
     * @param lastFmAlbumServices
     */
    public void setLastFmAlbumServices(LastFmAlbumServices lastFmAlbumServices) {
		this.lastFmAlbumServices = lastFmAlbumServices;
	}
    
    /**
     * @param lastFmCache
     */
    public void setLastFmCache(LastFmCache lastFmCache) {
		this.lastFmCache = lastFmCache;
	}
    
    /**
     * @param lastFmUserServices
     */
    public void setLastFmUserServices(LastFmUserServices lastFmUserServices) {
		this.lastFmUserServices = lastFmUserServices;
	}
    
    /**
     * @param lastFmLogin
     */
    public void setLastFmLogin(LastFmLogin lastFmLogin) {
		this.lastFmLogin = lastFmLogin;
	}
    
    /**
     * @param lastFmAPIKey
     */
    public void setLastFmAPIKey(LastFmAPIKey lastFmAPIKey) {
		this.lastFmAPIKey = lastFmAPIKey;
	}
    
    /**
     * Initializes service
     */
    public void initialize() {
        Caller.getInstance().setCache(null);
        Caller.getInstance().setUserAgent(lastFmAPIKey.getClientId());    	
	}
    
    /**
     * Finishes service
     */
    public void finishService() {
    	Logger.debug("Finalizing LastFmCache");
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
        return lastFmAlbumServices.getAlbum(artist, album);
    }

    /**
     * Gets the image of the album
     * 
     * @param artist
     * @param album
     * @return
     */
    public ImageIcon getAlbumImage(String artist, String album) {
        return lastFmAlbumServices.getAlbumImage(artist, album);
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
    	return lastFmAlbumServices.getAlbumList(artist);
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
    	return lastFmArtistServices.getArtistTopTag(artist);
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
        return lastFmAlbumServices.getAlbumImage(album);
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
        return lastFmAlbumServices.getAlbumThumbImage(album);
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
        return lastFmArtistServices.getArtistThumbImage(artist);
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
        return lastFmArtistServices.getArtistImage(artistName);
    }
    
    /**
     * Returns top tracks for given artist name
     * @param artistName
     * @return 
     */
    public IArtistTopTracks getTopTracks(String artistName) {
    	return lastFmArtistServices.getTopTracks(artistName);
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
        return lastFmArtistServices.getSimilarArtists(artist);
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
        return lastFmArtistServices.getWikiText(artist);
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
    	return lastFmArtistServices.getWikiURL(artist);
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
    	lastFmUserServices.submit(file, secondsPlayed);
    }

    /**
     * Adds a song to the list of loved tracks in Last.fm
     * 
     * @param song
     */
    public void addLovedSong(IAudioObject song) {
    	lastFmUserServices.addLovedSong(song);
    }

    /**
     * Removes a song from the list of loved tracks in Last.fm
     * 
     * @param song
     */
    public void removeLovedSong(IAudioObject song) {
    	lastFmUserServices.removeLovedSong(song);
    }

    /**
     * Adds a song to the list of banned tracks in Last.fm
     * 
     * @param song
     */
    public void addBannedSong(IAudioObject song) {
    	lastFmUserServices.addBannedSong(song);
    }

    /**
     * Submits now playing info to Last.fm
     * 
     * @param file
     *            audio file
     * @throws ScrobblerException
     */
    void submitNowPlayingInfo(ILocalAudioObject file) throws ScrobblerException {
    	lastFmUserServices.submitNowPlayingInfo(file);
    }

    /**
     * Returns a list of loved tracks from user profile
     * 
     * @return a list of loved tracks from user profile
     */
    public List<ILovedTrack> getLovedTracks() {
    	return lastFmUserServices.getLovedTracks();
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
        return lastFmSongServices.getTitleForFile(f);
    }

    /**
     * Return track number of an LocalAudioObject known its artist and album
     * 
     * @param f
     * @return
     */
    public int getTrackNumberForFile(ILocalAudioObject f) {
        return lastFmSongServices.getTrackNumberForFile(f);
    }

    /**
     * Submit song to Last.fm
     * 
     * @param audioFile
     * @param secondsPlayed
     * @param taskService
     */
    public void submitToLastFm(IAudioObject audioFile, long secondsPlayed, ITaskService taskService) {
    	lastFmUserServices.submitToLastFm(audioFile, secondsPlayed, taskService);
    }

    /**
     * Submits Last.fm cache
     * @param service
     */
    public void submitCacheToLastFm(ITaskService service) {
    	lastFmUserServices.submitCacheToLastFm(service);
    }

    /**
     * Submit now playing info to Last.fm
     * 
     * @param audioFile
     *            the file
     */
    public void submitNowPlayingInfoToLastFm(ILocalAudioObject audioFile, ITaskService taskService) {
    	lastFmUserServices.submitNowPlayingInfoToLastFm(audioFile, taskService);
    }

    /**
     * Returns events of an artist. This is a convenience method to allow
     * plugins access last fm services without opening access to api key outside
     * this class
     * 
     * @param artist
     * @return
     */
    public Collection<Event> getArtistEvents(String artist) {
    	return lastFmArtistServices.getArtistEvents(artist);
    }

	/**
	 * Flush cache
	 */
	public void flush() {
		lastFmCache.flush();
	}

    /**
     * Test if given user and password are correct to login at last.fm
     * 
     * @param user
     * @param password
     */
	public boolean testLogin(String user, String password) {
		return lastFmLogin.testLogin(user, password);
	}
}
