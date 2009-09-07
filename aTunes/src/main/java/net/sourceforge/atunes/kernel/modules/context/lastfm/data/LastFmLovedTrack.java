/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.context.lastfm.data;

import java.io.Serializable;

/**
 * This class represents a track loved by user in LastFm
 */
public class LastFmLovedTrack implements Serializable {

    private static final long serialVersionUID = -1808577722802627061L;

    /**
     * Artist name
     */
    private String artist;

    /**
     * Track title
     */
    private String title;

    /**
     * Constructor
     * 
     * @param artist
     * @param title
     */
    public LastFmLovedTrack(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    /**
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

}
