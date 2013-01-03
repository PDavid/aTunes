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
 * Has methods to check for unknown objects
 * 
 * @author alex
 * 
 */
public interface IUnknownObjectChecker {

    /**
     * Return <code>true</code> if this artist is unknown
     * 
     * @param artist
     * @return
     */
    public boolean isUnknownArtist(IArtist artist);

    /**
     * Return <code>true</code> if this artist is unknown
     * 
     * @param artist
     * @return
     */
    public boolean isUnknownArtist(String artist);

    /**
     * Return <code>true</code> if this album is unknown
     * 
     * @param album
     * @return
     */
    public boolean isUnknownAlbum(String album);

    /**
     * Return <code>true</code> if this genre is unknown
     * 
     * @param genre
     * @return
     */
    public boolean isUnknownGenre(String genre);

    /**
     * Return <code>true</code> if year is unknown
     * 
     * @param year
     * @return
     */
    public boolean isUnknownYear(String year);

    /**
     * @return unknown year text
     */
    public String getUnknownYear();

    /**
     * @return unknown genre text
     */
    public String getUnknownGenre();

    /**
     * @return unknown album text
     */
    public String getUnknownAlbum();

    /**
     * @return unknown artist text
     */
    public String getUnknownArtist();
}