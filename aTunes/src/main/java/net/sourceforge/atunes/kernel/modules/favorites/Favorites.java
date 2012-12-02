/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.modules.favorites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.ILocalAudioObject;

/**
 * The Class Favorites.
 */
public class Favorites implements IFavorites {

    private static final long serialVersionUID = 4783402394156393291L;

    /** The favorite songs. */
    private Map<String, ILocalAudioObject> favoriteSongs;

    /** The favorite albums. */
    private Map<String, IAlbum> favoriteAlbums;

    /** The favorite artists. */
    private Map<String, IArtist> favoriteArtists;

    /**
     * Flag indicating if favorites information needs to be written to disk
     */
    private transient boolean dirty;

    /**
     * Instantiates a new favorites.
     */
    protected Favorites() {
        favoriteSongs = new HashMap<String, ILocalAudioObject>();
        favoriteAlbums = new HashMap<String, IAlbum>();
        favoriteArtists = new HashMap<String, IArtist>();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.favorites.IFavorites#getAllFavoriteSongs()
	 */
    @Override
	public List<ILocalAudioObject> getAllFavoriteSongs() {
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
        for (IArtist artist : favoriteArtists.values()) {
            result.addAll(artist.getAudioObjects());
        }
        for (IAlbum album : favoriteAlbums.values()) {
            result.addAll(album.getAudioObjects());
        }
        result.addAll(favoriteSongs.values());
        return result;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.favorites.IFavorites#getAllFavoriteSongsMap()
	 */
    @Override
	public Map<String, ILocalAudioObject> getAllFavoriteSongsMap() {
        Map<String, ILocalAudioObject> result = new HashMap<String, ILocalAudioObject>();
        for (ILocalAudioObject af : getAllFavoriteSongs()) {
            result.put(af.getUrl(), af);
        }
        return result;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.favorites.IFavorites#getFavoriteAlbums()
	 */
    @Override
	public Map<String, IAlbum> getFavoriteAlbums() {
        return favoriteAlbums;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.favorites.IFavorites#getFavoriteArtists()
	 */
    @Override
	public Map<String, IArtist> getFavoriteArtists() {
        return favoriteArtists;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.favorites.IFavorites#getFavoriteAudioFiles()
	 */
    @Override
	public Map<String, ILocalAudioObject> getFavoriteAudioFiles() {
        return favoriteSongs;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.favorites.IFavorites#setFavoriteAlbums(java.util.Map)
	 */
    @Override
	public void setFavoriteAlbums(Map<String, IAlbum> favoriteAlbums) {
        this.favoriteAlbums = favoriteAlbums;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.favorites.IFavorites#setFavoriteArtists(java.util.Map)
	 */
    @Override
	public void setFavoriteArtists(Map<String, IArtist> favoriteArtists) {
        this.favoriteArtists = favoriteArtists;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.repository.favorites.IFavorites#setFavoriteSongs(java.util.Map)
	 */
    @Override
	public void setFavoriteSongs(Map<String, ILocalAudioObject> favoriteSongs) {
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
