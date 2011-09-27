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

package net.sourceforge.atunes.model;

import java.util.List;
import java.util.Map;


/**
 * Responsible of manage favorites
 * @author alex
 *
 */
public interface IFavoritesHandler extends IHandler {

	/**
	 * Adds or removes the favorite albums.
	 * 
	 * @param songs
	 *            the songs
	 */
	public void toggleFavoriteAlbums(List<ILocalAudioObject> songs);

	/**
	 * Adds or removes the favorite artists.
	 * 
	 * @param songs
	 *            the songs
	 */
	public void toggleFavoriteArtists(List<ILocalAudioObject> songs);

	/**
	 * Adds or remvoves the favorite songs.
	 * 
	 * @param songs
	 *            the songs
	 */
	public void toggleFavoriteSongs(List<ILocalAudioObject> songs);

	/**
	 * Adds the favorite songs, even if already added. Optionally can be automatically marked as loved in web services
	 * 
	 * @param songs
	 * @param automaticallyLoveInWebService
	 */
	public void addFavoriteSongs(List<ILocalAudioObject> songs, boolean automaticallyLoveInWebService);

	/**
	 * Gets the favorite albums info.
	 * 
	 * @return the favorite albums info
	 */
	public Map<String, Album> getFavoriteAlbumsInfo();

	/**
	 * Gets the favorite artists info.
	 * 
	 * @return the favorite artists info
	 */
	public Map<String, Artist> getFavoriteArtistsInfo();

	/**
	 * Gets the favorite songs.
	 * 
	 * @return the favorite songs
	 */
	public List<ILocalAudioObject> getFavoriteSongs();

	/**
	 * Gets the favorite songs map
	 */
	public Map<String, ILocalAudioObject> getFavoriteSongsMap();

	/**
	 * Gets the favorite songs info.
	 * 
	 * @return the favorite songs info
	 */
	public Map<String, ILocalAudioObject> getFavoriteSongsInfo();

	/**
	 * Removes the objects from favorites.
	 * 
	 * @param objects
	 *            list of objects of type TreeObject
	 */
	public void removeFromFavorites(List<ITreeObject<?>> objects);

	/**
	 * Removes the songs from favorites.
	 * 
	 * @param files
	 *            the files
	 */
	public void removeSongsFromFavorites(List<IAudioObject> files);

}