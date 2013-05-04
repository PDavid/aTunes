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

/**
 * Responsible of manage favorites
 * 
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
	 * Adds the favorite songs, even if already added. Optionally can be
	 * automatically marked as loved in web services
	 * 
	 * @param songs
	 * @param automaticallyLoveInWebService
	 */
	public void addFavoriteSongs(List<ILocalAudioObject> songs,
			boolean automaticallyLoveInWebService);

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
	public void removeSongsFromFavorites(List<ILocalAudioObject> files);

	/**
	 * Updates favorites against given repository Each favorite song must be
	 * repository. If not, will search for an alternative or remove from
	 * favorites
	 * 
	 * @param repository
	 */
	public void updateFavorites(IRepository repository);

	/**
	 * @param userObject
	 * @return true if artist is favorite
	 */
	public boolean isArtistFavorite(IArtist userObject);

	/**
	 * @param artist
	 * @return true if artist is favorite
	 */
	public boolean isArtistFavorite(String artist);

	/**
	 * @return list of favorite artists
	 */
	public List<IArtist> getFavoriteArtists();

	/**
	 * @param userObject
	 * @return true if album is favorite
	 */
	public boolean isAlbumFavorite(IAlbum userObject);

	/**
	 * @param album
	 * @return true if album is favorite
	 */
	public boolean isAlbumFavorite(String album);

	/**
	 * @return list of favorite albums
	 */
	public List<IAlbum> getFavoriteAlbums();

	/**
	 * @return list of favorite songs
	 */
	public List<ILocalAudioObject> getFavoriteSongs();

	/**
	 * @param audioObject
	 * @return true if song is favorite
	 */
	public boolean isSongFavorite(ILocalAudioObject audioObject);

	/**
	 * @return list of audio objects of all favorite artists, favorite albums
	 *         and songs
	 */
	public List<ILocalAudioObject> getAllFavoriteSongs();

}