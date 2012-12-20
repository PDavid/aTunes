/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm.data;

import java.io.Serializable;

import net.sourceforge.atunes.model.ILovedTrack;

/**
 * This class represents a track loved by user in LastFm
 */
public class LastFmLovedTrack implements Serializable, ILovedTrack {

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

    @Override
	public String getArtist() {
        return artist;
    }

    @Override
	public String getTitle() {
        return title;
    }

}
