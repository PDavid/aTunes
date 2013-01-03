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

package net.sourceforge.atunes.kernel.modules.tags;

import java.util.ArrayList;
import java.util.List;

/**
 * Access genres
 * 
 * @author alex
 * 
 */
public class Genres {

    private List<String> genres;

    /**
     * @param genres
     */
    public void setGenres(final List<String> genres) {
	this.genres = genres;
    }

    /**
     * Tries to find the string provided in the table and returns the
     * corresponding int code if successful. Returns -1 if the genres is not
     * found in the table.
     * 
     * @param str
     *            the genre to search for
     * 
     * @return the integer code for the genre or -1 if the genre is not found
     */
    public int getGenre(final String str) {
	int retval = -1;

	for (int i = 0; (i < genres.size()); i++) {
	    if (genres.get(i).equalsIgnoreCase(str)) {
		retval = i;
		break;
	    }
	}

	return retval;
    }

    /**
     * Gets the genre for code.
     * 
     * @param code
     *            the code
     * 
     * @return the genre for code
     */
    public final String getGenreForCode(final int code) {
	return code >= 0 && code < genres.size() ? genres.get(code) : "";
    }

    /**
     * @return genres list
     */
    public List<String> getGenres() {
	return new ArrayList<String>(this.genres);
    }
}
