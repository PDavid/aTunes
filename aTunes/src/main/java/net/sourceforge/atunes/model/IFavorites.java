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
import java.util.Map;

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
     * Gets a map with all favorite songs, with url as key
     * 
     * @return
     */
    public Map<String, ILocalAudioObject> getAllFavoriteSongsMap();

    /**
     * Gets the favorite albums.
     * 
     * @return the favorite albums
     */
    public Map<String, IAlbum> getFavoriteAlbums();

    /**
     * Gets the favorite artists.
     * 
     * @return the favorite artists
     */
    public Map<String, IArtist> getFavoriteArtists();

    /**
     * Gets the favorite songs.
     * 
     * @return the favorite songs
     */
    public Map<String, ILocalAudioObject> getFavoriteAudioFiles();

    /**
     * Sets the favorite albums.
     * 
     * @param favoriteAlbums
     *            the favorite albums
     */
    public void setFavoriteAlbums(Map<String, IAlbum> favoriteAlbums);

    /**
     * Sets the favorite artists.
     * 
     * @param favoriteArtists
     *            the favorite artists
     */
    public void setFavoriteArtists(Map<String, IArtist> favoriteArtists);

    /**
     * Sets the favorite songs.
     * 
     * @param favoriteSongs
     *            the favorite songs
     */
    public void setFavoriteSongs(Map<String, ILocalAudioObject> favoriteSongs);

}