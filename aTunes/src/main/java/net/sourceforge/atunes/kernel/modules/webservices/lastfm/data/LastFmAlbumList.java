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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import de.umass.lastfm.Album;

/**
 * A list of last.fm albums
 * 
 * @author alex
 * 
 */
public class LastFmAlbumList implements IAlbumListInfo {

    private static final long serialVersionUID = 5865751328573689357L;

    private String artist;
    private List<IAlbumInfo> albums;

    /**
     * Gets the album list.
     * 
     * @param as
     * @param artist
     * @return
     */
    public static IAlbumListInfo getAlbumList(final Collection<Album> as,
	    final String artist) {
	List<IAlbumInfo> albums = new ArrayList<IAlbumInfo>();
	IAlbumListInfo albumList = new LastFmAlbumList();

	for (Album a : as) {
	    IAlbumInfo album = LastFmAlbum.getAlbum(a, null);
	    albums.add(album);
	}

	albumList.setAlbums(albums);
	return albumList;
    }

    /**
     * Gets the albums.
     * 
     * @return the albums
     */
    @Override
    public List<IAlbumInfo> getAlbums() {
	return albums;
    }

    /**
     * Gets the artist.
     * 
     * @return the artist
     */
    @Override
    public String getArtist() {
	return artist;
    }

    /**
     * Sets the albums.
     * 
     * @param albums
     *            the albums to set
     */
    @Override
    public void setAlbums(final List<? extends IAlbumInfo> albums) {
	this.albums = albums != null ? new ArrayList<IAlbumInfo>(albums) : null;
    }

    /**
     * Sets the artist.
     * 
     * @param artist
     *            the artist to set
     */
    @Override
    public void setArtist(final String artist) {
	this.artist = artist;
    }

}
