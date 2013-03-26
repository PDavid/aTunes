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

/**
 * Ways to view an artist tree: organize music using artist tag, artist of album
 * tag, or both
 * 
 * @author alex
 * 
 */
public enum ArtistViewMode {

    /**
     * By artist
     */
    ARTIST,

    /**
     * By artist of album
     */
    ARTIST_OF_ALBUM,

    /**
     * Use both fields
     */
    BOTH;
}