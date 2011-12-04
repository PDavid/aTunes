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

package net.sourceforge.atunes.utils;

import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.repository.data.Year;
import net.sourceforge.atunes.model.Artist;

public class UnknownObjectCheck {

    /**
     * Return unknown artist text
     * 
     * @return
     */
    public static String getUnknownArtist() {
        return I18nUtils.getString("UNKNOWN_ARTIST");
    }

    /**
     * Return <code>true</code> if this artist is unknown
     * 
     * @return
     */
    public static boolean isUnknownArtist(Artist artist) {
        return artist.getName().equalsIgnoreCase(getUnknownArtist());
    }

    /**
     * Return <code>true</code> if this artist is unknown
     * 
     * @return
     */
    public static boolean isUnknownArtist(String artist) {
        return getUnknownArtist().equalsIgnoreCase(artist);
    }
    
    /**
     * Return unknown album text
     * 
     * @return
     */
    public static String getUnknownAlbum() {
        return I18nUtils.getString("UNKNOWN_ALBUM");
    }

    /**
     * Return <code>true</code> if this album is unknown
     * 
     * @return
     */
    public static boolean isUnknownAlbum(String album) {
        return getUnknownAlbum().equalsIgnoreCase(album);
    }
    
    /**
     * Return unknown genre text
     * 
     * @return
     */
    public static String getUnknownGenre() {
        return I18nUtils.getString("UNKNOWN_GENRE");
    }

    /**
     * Return <code>true</code> if this genre is unknown
     * 
     * @return
     */
    public boolean isUnknownGenre(Genre genre) {
        return genre.getName().equalsIgnoreCase(getUnknownGenre());
    }

    /**
     * Return <code>true</code> if this genre is unknown
     * 
     * @return
     */
    public static boolean isUnknownGenre(String genre) {
        return getUnknownGenre().equalsIgnoreCase(genre);
    }

    /**
     * Return unknown year text
     * 
     * @return
     */
    public static String getUnknownYear() {
        return I18nUtils.getString("UNKNOWN_YEAR");
    }

    /**
     * Return <code>true</code> if year is unknown
     * 
     * @return
     */
    public boolean isUnknownYear(Year year) {
        return year.getName().equalsIgnoreCase(getUnknownYear());
    }

    /**
     * Return <code>true</code> if year is unknown
     * 
     * @return
     */
    public static boolean isUnknownYear(String year) {
        return year.isEmpty() || getUnknownYear().equalsIgnoreCase(year);
    }





}
