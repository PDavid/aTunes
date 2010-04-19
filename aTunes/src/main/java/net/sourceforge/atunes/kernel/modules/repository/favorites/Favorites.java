/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.repository.favorites;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;

/**
 * The Class Favorites.
 */
public class Favorites implements Serializable {

    private static final long serialVersionUID = 4783402394156393291L;

    /** The favorite songs. */
    private Map<String, AudioFile> favoriteSongs;

    /** The favorite albums. */
    private Map<String, Album> favoriteAlbums;

    /** The favorite artists. */
    private Map<String, Artist> favoriteArtists;

    /**
     * Flag indicating if favorites information needs to be written to disk
     */
    private transient boolean dirty;

    /**
     * Instantiates a new favorites.
     */
    public Favorites() {
        favoriteSongs = new HashMap<String, AudioFile>();
        favoriteAlbums = new HashMap<String, Album>();
        favoriteArtists = new HashMap<String, Artist>();
    }

    /**
     * Gets the all favorite songs.
     * 
     * @return the all favorite songs
     */
    public List<AudioFile> getAllFavoriteSongs() {
        List<AudioFile> result = new ArrayList<AudioFile>();
        for (Artist artist : favoriteArtists.values()) {
            result.addAll(artist.getAudioFiles());
        }
        for (Album album : favoriteAlbums.values()) {
            result.addAll(album.getAudioFiles());
        }
        result.addAll(favoriteSongs.values());
        return result;
    }

    /**
     * Gets a map with all favorite songs, with url as key
     */
    public Map<String, AudioFile> getAllFavoriteSongsMap() {
        Map<String, AudioFile> result = new HashMap<String, AudioFile>();
        for (AudioFile af : getAllFavoriteSongs()) {
            result.put(af.getUrl(), af);
        }
        return result;
    }

    /**
     * Gets the favorite albums.
     * 
     * @return the favorite albums
     */
    public Map<String, Album> getFavoriteAlbums() {
        return favoriteAlbums;
    }

    /**
     * Gets the favorite artists.
     * 
     * @return the favorite artists
     */
    public Map<String, Artist> getFavoriteArtists() {
        return favoriteArtists;
    }

    /**
     * Gets the favorite songs.
     * 
     * @return the favorite songs
     */
    public Map<String, AudioFile> getFavoriteAudioFiles() {
        return favoriteSongs;
    }

    /**
     * Sets the favorite albums.
     * 
     * @param favoriteAlbums
     *            the favorite albums
     */
    public void setFavoriteAlbums(Map<String, Album> favoriteAlbums) {
        this.favoriteAlbums = favoriteAlbums;
    }

    /**
     * Sets the favorite artists.
     * 
     * @param favoriteArtists
     *            the favorite artists
     */
    public void setFavoriteArtists(Map<String, Artist> favoriteArtists) {
        this.favoriteArtists = favoriteArtists;
    }

    /**
     * Sets the favorite songs.
     * 
     * @param favoriteSongs
     *            the favorite songs
     */
    public void setFavoriteSongs(Map<String, AudioFile> favoriteSongs) {
        this.favoriteSongs = favoriteSongs;
    }

    /**
     * @return the dirty
     */
    protected boolean isDirty() {
        return dirty;
    }

    /**
     * @param dirty
     *            the dirty to set
     */
    protected void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
