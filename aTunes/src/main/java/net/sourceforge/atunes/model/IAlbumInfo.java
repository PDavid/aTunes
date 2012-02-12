/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.model;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;

/**
 * Information about an album, usually retrieved from a web service
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
     * Sets the artist.
     * 
     * @param artist
     *            the artist to set
     */
    public void setArtist(String artist);

    /**
     * Sets the big cover url.
     * 
     * @param bigCoverURL
     *            the bigCoverURL to set
     */
    public void setBigCoverURL(String bigCoverURL);

    /**
     * Sets the release date string.
     * 
     * @param releaseDateString
     *            the releaseDateString to set
     */
    public void setReleaseDateString(String releaseDateString);

    /**
     * Sets the title.
     * 
     * @param title
     *            the title to set
     */
    public void setTitle(String title);

    /**
     * Sets the tracks.
     * 
     * @param tracks
     *            the tracks to set
     */
    public void setTracks(List<? extends ITrackInfo> tracks);

    /**
     * Sets the url.
     * 
     * @param url
     *            the url to set
     */
    public void setUrl(String url);
    
    /**
     * Returns Music Brainz ID
     * @return
     */
    public String getMbid();
    
    /**
     * Sets Music Brainz ID
     * @param mbid
     */
    public void setMbid(String mbid);

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString();

}
