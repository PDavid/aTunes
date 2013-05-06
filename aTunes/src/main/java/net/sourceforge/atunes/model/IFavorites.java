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

import java.io.Serializable;
import java.util.List;

/**
 * An object containing information about favorites
 * 
 * @author alex
 * 
 */
public interface IFavorites extends Serializable {

	/**
	 * Gets the all favorite songs.
	 * 
	 * @return the all favorite songs
	 */
	public List<ILocalAudioObject> getAllFavoriteSongs();

	/**
	 * @param artist
	 * @return true if artist is favorite
	 */
	boolean containsArtist(final IArtist artist);

	/**
	 * @param artist
	 * @return true if artist is favorite
	 */
	boolean containsArtist(String artist);

	/**
	 * Adds artist as favorite
	 * 
	 * @param artist
	 */
	void addArtist(IArtist artist);

	/**
	 * Removes artist from favorites
	 * 
	 * @param artist
	 */
	public void removeArtist(IArtist artist);

	/**
	 * Removes artist from favorites by name
	 * 
	 * @param artist
	 */
	public void removeArtist(String artist);

	/**
	 * @return list of favorite artists
	 */
	public List<IArtist> getFavoriteArtists();

	/**
	 * @param album
	 * @return true if album is favorite
	 */
	public boolean containsAlbum(IAlbum album);

	/**
	 * @param album
	 * @return true if album is favorite
	 */
	public boolean containsAlbum(String album);

	/**
	 * Adds album to favorites
	 * 
	 * @param album
	 */
	public void addAlbum(IAlbum album);

	/**
	 * Removes album from favorites
	 * 
	 * @param album
	 */
	public void removeAlbum(IAlbum album);

	/**
	 * Removes album by name
	 * 
	 * @param album
	 */
	public void removeAlbum(String album);

	/**
	 * @return list of favorite albums
	 */
	public List<IAlbum> getFavoriteAlbums();

	/**
	 * @param song
	 * @return true if song is favorite
	 */
	public boolean containsSong(ILocalAudioObject song);

	/**
	 * Add song to favorites
	 * 
	 * @param song
	 */
	public void addSong(ILocalAudioObject song);

	/**
	 * Removes song from favorites
	 * 
	 * @param file
	 */
	public void removeSong(ILocalAudioObject file);

	/**
	 * @return favorite songs
	 */
	public List<ILocalAudioObject> getFavoriteSongs();

}