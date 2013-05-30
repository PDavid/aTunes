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
 * Represents artists similar to another one
 * @author alex
 *
 */

public interface ISimilarArtistsInfo extends Serializable {

    /**
     * Gets the artist name.
     * 
     * @return the artist name
     */
    public String getArtistName();

    /**
     * Gets the artists.
     * 
     * @return the artists
     */
    public List<IArtistInfo> getArtists();

    /**
     * Gets the picture.
     * 
     * @return the picture
     */
    public String getPicture();

    /**
     * Sets the artist name.
     * 
     * @param artistName
     *            the artistName to set
     */
    public void setArtistName(String artistName);

    /**
     * Sets the artists.
     * 
     * @param artists
     *            the artists to set
     */
    public void setArtists(List<? extends IArtistInfo> artists);

    /**
     * Sets the picture.
     * 
     * @param picture
     *            the picture to set
     */
    public void setPicture(String picture);

}
