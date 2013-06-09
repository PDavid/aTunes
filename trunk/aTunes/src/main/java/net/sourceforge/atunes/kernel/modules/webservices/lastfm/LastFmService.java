/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IEvent;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILovedTrack;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.Logger;
import de.umass.lastfm.Caller;

/**
 * This class is responsible of retrieve information from Last.fm web services.
 */

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
	public void setLastFmSongServices(
			final LastFmSongServices lastFmSongServices) {
		this.lastFmSongServices = lastFmSongServices;
	}

	/**
	 * @param lastFmArtistServices
	 */
	public void setLastFmArtistServices(
			final LastFmArtistServices lastFmArtistServices) {
		this.lastFmArtistServices = lastFmArtistServices;
	}

	/**
	 * @param lastFmAlbumServices
	 */
	public void setLastFmAlbumServices(
			final LastFmAlbumServices lastFmAlbumServices) {
		this.lastFmAlbumServices = lastFmAlbumServices;
	}

	/**
	 * @param lastFmCache
	 */
	public void setLastFmCache(final LastFmCache lastFmCache) {
		this.lastFmCache = lastFmCache;
	}

	/**
	 * @param lastFmUserServices
	 */
	public void setLastFmUserServices(
			final LastFmUserServices lastFmUserServices) {
		this.lastFmUserServices = lastFmUserServices;
	}

	/**
	 * @param lastFmLogin
	 */
	public void setLastFmLogin(final LastFmLogin lastFmLogin) {
		this.lastFmLogin = lastFmLogin;
	}

	/**
	 * @param lastFmAPIKey
	 */
	public void setLastFmAPIKey(final LastFmAPIKey lastFmAPIKey) {
		this.lastFmAPIKey = lastFmAPIKey;
	}

	/**
	 * Initializes service
	 */
	public void initialize() {
		Caller.getInstance().setCache(null);
		Caller.getInstance().setUserAgent(this.lastFmAPIKey.getClientId());
	}

	/**
	 * Finishes service
	 */
	public void finishService() {
		Logger.debug("Finalizing LastFmCache");
		this.lastFmCache.shutdown();
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
	public IAlbumInfo getAlbum(final String artist, final String album) {
		return this.lastFmAlbumServices.getAlbum(artist, album);
	}

	/**
	 * Gets the image of the album
	 * 
	 * @param artist
	 * @param album
	 * @return
	 */
	public ImageIcon getAlbumImage(final String artist, final String album) {
		return this.lastFmAlbumServices.getAlbumImage(artist, album);
	}

	/**
	 * Gets the album list.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the album list
	 */
	public IAlbumListInfo getAlbumList(final String artist) {
		return this.lastFmAlbumServices.getAlbumList(artist);
	}

	/**
	 * Gets the artist top tag.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the artist top tag
	 */
	public String getArtistTopTag(final String artist) {
		return this.lastFmArtistServices.getArtistTopTag(artist);
	}

	/**
	 * Gets the image.
	 * 
	 * @param album
	 *            the album
	 * 
	 * @return the image
	 */
	public ImageIcon getAlbumImage(final IAlbumInfo album) {
		return this.lastFmAlbumServices.getAlbumImage(album);
	}

	/**
	 * Gets the thumbnail image of album
	 * 
	 * @param album
	 *            the album
	 * 
	 * @return the image
	 */
	public ImageIcon getAlbumThumbImage(final IAlbumInfo album) {
		return this.lastFmAlbumServices.getAlbumThumbImage(album);
	}

	/**
	 * Gets the image of an artist
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the image
	 */
	public ImageIcon getArtistThumbImage(final IArtistInfo artist) {
		return this.lastFmArtistServices.getArtistThumbImage(artist);
	}

	/**
	 * Gets the image of the artist
	 * 
	 * @param artistName
	 *            the similar
	 * 
	 * @return the image
	 */
	public ImageIcon getArtistImage(final String artistName) {
		return this.lastFmArtistServices.getArtistImage(artistName);
	}

	/**
	 * Returns top tracks for given artist name
	 * 
	 * @param artistName
	 * @return
	 */
	public IArtistTopTracks getTopTracks(final String artistName) {
		return this.lastFmArtistServices.getTopTracks(artistName);
	}

	/**
	 * Gets the similar artists.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the similar artists
	 */
	public ISimilarArtistsInfo getSimilarArtists(final String artist) {
		return this.lastFmArtistServices.getSimilarArtists(artist);
	}

	/**
	 * Gets the wiki text.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the wiki text
	 */
	public String getWikiText(final String artist) {
		return this.lastFmArtistServices.getWikiText(artist);
	}

	/**
	 * Gets the wiki url.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the wiki url
	 */
	public String getWikiURL(final String artist) {
		return this.lastFmArtistServices.getWikiURL(artist);
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
	void submit(final IAudioObject file, final long secondsPlayed)
			throws ScrobblerException {
		this.lastFmUserServices.submit(file, secondsPlayed);
	}

	/**
	 * Adds a song to the list of loved tracks in Last.fm
	 * 
	 * @param song
	 */
	public void addLovedSong(final IAudioObject song) {
		this.lastFmUserServices.addLovedSong(song);
	}

	/**
	 * Removes a song from the list of loved tracks in Last.fm
	 * 
	 * @param song
	 */
	public void removeLovedSong(final IAudioObject song) {
		this.lastFmUserServices.removeLovedSong(song);
	}

	/**
	 * Adds a song to the list of banned tracks in Last.fm
	 * 
	 * @param song
	 */
	public void addBannedSong(final IAudioObject song) {
		this.lastFmUserServices.addBannedSong(song);
	}

	/**
	 * Submits now playing info to Last.fm
	 * 
	 * @param file
	 *            audio file
	 * @throws ScrobblerException
	 */
	void submitNowPlayingInfo(final ILocalAudioObject file)
			throws ScrobblerException {
		this.lastFmUserServices.submitNowPlayingInfo(file);
	}

	/**
	 * Returns a list of loved tracks from user profile
	 * 
	 * @return a list of loved tracks from user profile
	 */
	public List<ILovedTrack> getLovedTracks() {
		return this.lastFmUserServices.getLovedTracks();
	}

	/**
	 * Delegate method to clear cache
	 * 
	 * @return
	 */
	public boolean clearCache() {
		return this.lastFmCache.clearCache();
	}

	/**
	 * Return title of an LocalAudioObject known its artist and album
	 * 
	 * @param f
	 * @return
	 */
	public String getTitleForFile(final ILocalAudioObject f) {
		return this.lastFmSongServices.getTitleForFile(f);
	}

	/**
	 * Return track number of an LocalAudioObject known its artist and album
	 * 
	 * @param f
	 * @return
	 */
	public int getTrackNumberForFile(final ILocalAudioObject f) {
		return this.lastFmSongServices.getTrackNumberForFile(f);
	}

	/**
	 * Submit song to Last.fm
	 * 
	 * @param audioFile
	 * @param secondsPlayed
	 * @param taskService
	 */
	public void submitToLastFm(final IAudioObject audioFile,
			final long secondsPlayed, final ITaskService taskService) {
		this.lastFmUserServices.submitToLastFm(audioFile, secondsPlayed,
				taskService);
	}

	/**
	 * Submits Last.fm cache
	 * 
	 * @param service
	 */
	public void submitCacheToLastFm(final ITaskService service) {
		this.lastFmUserServices.submitCacheToLastFm(service);
	}

	/**
	 * Submit now playing info to Last.fm
	 * 
	 * @param audioFile
	 * @param taskService
	 */
	public void submitNowPlayingInfoToLastFm(final ILocalAudioObject audioFile,
			final ITaskService taskService) {
		this.lastFmUserServices.submitNowPlayingInfoToLastFm(audioFile,
				taskService);
	}

	/**
	 * Returns events of an artist
	 * 
	 * @param artist
	 * @return
	 */
	public List<IEvent> getArtistEvents(final String artist) {
		return this.lastFmArtistServices.getArtistEvents(artist);
	}

	/**
	 * Flush cache
	 */
	public void flush() {
		this.lastFmCache.flush();
	}

	/**
	 * Test if given user and password are correct to login at last.fm
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean testLogin(final String user, final String password) {
		return this.lastFmLogin.testLogin(user, password);
	}

	/**
	 * @return recommended events for current user
	 */
	public List<IEvent> getRecommendedEvents() {
		return this.lastFmUserServices.getRecommendedEvents();
	}
}
