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

package net.sourceforge.atunes.model;

import java.util.List;

import javax.swing.ImageIcon;

/**
 * Manages web services
 * 
 * @author alex
 * 
 */
public interface IWebServicesHandler extends IHandler {

	/**
	 * Clears cache of stored web content
	 * 
	 * @return
	 */
	public boolean clearCache();

	/**
	 * Submits information of given audio object being played to web services
	 * 
	 * @param audioObject
	 */
	public void submitNowPlayingInfo(IAudioObject audioObject);

	/**
	 * Submits given audio object to web services after listening given amount
	 * of seconds
	 * 
	 * @param audioObject
	 * @param listened
	 */
	public void submit(IAudioObject audioObject, long listened);

	/**
	 * Adds audio object as banned in web services
	 * 
	 * @param song
	 */
	public void addBannedSong(IAudioObject song);

	/**
	 * Adds audio object as loved in web services
	 * 
	 * @param song
	 */
	public void addLovedSong(IAudioObject song);

	/**
	 * Removes audio object as loved in web services
	 * 
	 * @param song
	 */
	public void removeLovedSong(IAudioObject song);

	/**
	 * Get information about an album
	 * 
	 * @param artist
	 * @param album
	 * @return
	 */
	public IAlbumInfo getAlbum(String artist, String album);

	/**
	 * Gets album list
	 * 
	 * @param artist
	 * @return
	 */
	public IAlbumListInfo getAlbumList(String artist);

	/**
	 * Returns image of an album
	 * 
	 * @param albumInfo
	 * @return
	 */
	public ImageIcon getAlbumImage(IAlbumInfo albumInfo);

	/**
	 * Returns thumb image of an album
	 * 
	 * @param albumInfo
	 * @return
	 */
	public ImageIcon getAlbumThumbImage(IAlbumInfo albumInfo);

	/**
	 * Returns image of an album
	 * 
	 * @param artist
	 * @param album
	 * @return
	 */
	public ImageIcon getAlbumImage(String artist, String album);

	/**
	 * Returns image for given artist
	 * 
	 * @param artist
	 * @return
	 */
	public ImageIcon getArtistImage(String artist);

	/**
	 * Gets biographic information about artist
	 * 
	 * @param artist
	 * @return
	 */
	public String getBioText(String artist);

	/**
	 * Gets url where user can find more biographic information about artist
	 * 
	 * @param artist
	 * @return
	 */
	public String getBioURL(String artist);

	/**
	 * Gets top tracks of an artist
	 * 
	 * @param artist
	 * @return
	 */
	public IArtistTopTracks getTopTracks(String artist);

	/**
	 * Given an audio object returns the corresponding title from web services
	 * 
	 * @param f
	 * @return
	 */
	public String getTitleForAudioObject(IAudioObject f);

	/**
	 * Returns loved tracks from web services
	 * 
	 * @return
	 */
	public List<ILovedTrack> getLovedTracks();

	/**
	 * Tests login in web services with given user and password
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	public Boolean testLogin(String user, String password);

	/**
	 * Returns the most used tag user to describe an artist music
	 * 
	 * @param artist
	 * @return
	 */
	public String getArtistTopTag(String artist);

	/**
	 * Returns information about artists similar to the given
	 * 
	 * @param artist
	 * @return
	 */
	public ISimilarArtistsInfo getSimilarArtists(String artist);

	/**
	 * Returns thumb image of artist
	 * 
	 * @param a
	 * @return
	 */
	public ImageIcon getArtistThumbImage(IArtistInfo a);

	/**
	 * Returns track number for given audio object
	 * 
	 * @param audioObject
	 * @return
	 */
	public int getTrackNumber(ILocalAudioObject audioObject);

	/**
	 * Returns lyrics for given song
	 * 
	 * @param artist
	 * @param title
	 * @return
	 */
	public ILyrics getLyrics(String artist, String title);

	/**
	 * Consolidates web content
	 */
	public void consolidateWebContent();

	/**
	 * @param artist
	 * @return list of events of given artist
	 */
	public List<IEvent> getArtistEvents(String artist);

	/**
	 * @return recommended events
	 */
	public List<IEvent> getRecommendedEvents();

}