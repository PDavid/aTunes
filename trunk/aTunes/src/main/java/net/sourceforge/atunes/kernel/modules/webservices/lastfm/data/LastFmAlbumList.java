/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.modules.webservices.lastfm.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.roarsoftware.lastfm.Album;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.AlbumListInfo;

public class LastFmAlbumList implements AlbumListInfo {

    private static final long serialVersionUID = 5865751328573689357L;

    private String artist;
    private List<AlbumInfo> albums;

    /**
     * Gets the album list.
     * 
     * @return the album list
     */
    public static AlbumListInfo getAlbumList(Collection<Album> as, String artist) {
        List<AlbumInfo> albums = new ArrayList<AlbumInfo>();
        AlbumListInfo albumList = new LastFmAlbumList();

        for (Album a : as) {
            AlbumInfo album = LastFmAlbum.getAlbum(a, null);
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
    public List<AlbumInfo> getAlbums() {
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
    public void setAlbums(List<? extends AlbumInfo> albums) {
        this.albums = albums != null ? new ArrayList<AlbumInfo>(albums) : null;
    }

    /**
     * Sets the artist.
     * 
     * @param artist
     *            the artist to set
     */
    @Override
    public void setArtist(String artist) {
        this.artist = artist;
    }

}
