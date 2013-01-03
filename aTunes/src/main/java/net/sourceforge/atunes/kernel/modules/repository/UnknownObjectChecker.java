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

package net.sourceforge.atunes.kernel.modules.repository;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Checks for "unknown" tag attributes
 * 
 * @author alex
 * 
 */
public final class UnknownObjectChecker implements IUnknownObjectChecker {

    /**
     * Return unknown artist text
     * 
     * @return
     */
    @Override
    public String getUnknownArtist() {
	return I18nUtils.getString("UNKNOWN_ARTIST");
    }

    /**
     * Return <code>true</code> if this artist is unknown
     * 
     * @return
     */
    @Override
    public boolean isUnknownArtist(final IArtist artist) {
	return artist != null
		&& artist.getName().equalsIgnoreCase(getUnknownArtist());
    }

    /**
     * Return <code>true</code> if this artist is unknown
     * 
     * @return
     */
    @Override
    public boolean isUnknownArtist(final String artist) {
	return getUnknownArtist().equalsIgnoreCase(artist);
    }

    /**
     * Return unknown album text
     * 
     * @return
     */
    @Override
    public String getUnknownAlbum() {
	return I18nUtils.getString("UNKNOWN_ALBUM");
    }

    /**
     * Return <code>true</code> if this album is unknown
     * 
     * @return
     */
    @Override
    public boolean isUnknownAlbum(final String album) {
	return getUnknownAlbum().equalsIgnoreCase(album);
    }

    /**
     * Return unknown genre text
     * 
     * @return
     */
    @Override
    public String getUnknownGenre() {
	return I18nUtils.getString("UNKNOWN_GENRE");
    }

    /**
     * Return <code>true</code> if this genre is unknown
     * 
     * @return
     */
    @Override
    public boolean isUnknownGenre(final String genre) {
	return getUnknownGenre().equalsIgnoreCase(genre);
    }

    /**
     * Return unknown year text
     * 
     * @return
     */
    @Override
    public String getUnknownYear() {
	return I18nUtils.getString("UNKNOWN_YEAR");
    }

    /**
     * Return <code>true</code> if year is unknown
     * 
     * @return
     */
    @Override
    public boolean isUnknownYear(final String year) {
	return getUnknownYear().equalsIgnoreCase(year);
    }
}
