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

import org.joda.time.DateTime;

/**
 * Information about an album, usually retrieved from a web service
 * 
 * @author alex
 * 
 */
public interface IAlbumInfo extends Serializable {

    /**
     * Gets the artist.
     * 
     * @return the artist
     */
    public String getArtist();

    /**
     * Gets the artist url.
     * 
     * @return the artist url
     */
    public String getArtistUrl();

    /**
     * Gets the big cover url.
     * 
     * @return the bigCoverURL
     */
    public String getBigCoverURL();

    /**
     * Gets the URL to get a thumb of cover
     * 
     * @return
     */
    public String getThumbCoverURL();

    /**
     * Gets the release date.
     * 
     * @return the release date
     */
    public DateTime getReleaseDate();

    /**
     * Gets the release date string.
     * 
     * @return the releaseDateString
     */
    public String getReleaseDateString();

    /**
     * Gets the title.
     * 
     * @return the title
     */
    public String getTitle();

    /**
     * Gets the tracks.
     * 
     * @return the tracks
     */
    public List<ITrackInfo> getTracks();

    /**
     * Gets the url.
     * 
     * @return the url
     */
    public String getUrl();

    /**
     * Gets the year.
     * 
     * @return the year
     */
    public String getYear();

    /**
     * Returns Music Brainz ID
     * 
     * @return
     */
    public String getMbid();

    /**
     * @return string representation
     */
    @Override
    public String toString();

}
