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
 * Information about a list of albums
 * @author alex
 *
 */
public interface IAlbumListInfo extends Serializable {

    /**
     * Gets the albums.
     * 
     * @return the albums
     */
    public List<IAlbumInfo> getAlbums();

    /**
     * Gets the artist.
     * 
     * @return the artist
     */
    public String getArtist();

    /**
     * Sets the albums.
     * 
     * @param albums
     *            the albums to set
     */
    public void setAlbums(List<? extends IAlbumInfo> albums);

    /**
     * Sets the artist.
     * 
     * @param artist
     *            the artist to set
     */
    public void setArtist(String artist);

}
