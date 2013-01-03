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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * The Interface Search.
 */
public interface ISearch {

    /**
     * Returns an URL for a given search query.
     * 
     * @param query
     *            the query
     * 
     * @return the URL
     * 
     * @throws MalformedURLException
     *             the malformed url exception
     */
    public URL getURL(String query) throws MalformedURLException;

    /**
     * String representation of a search.
     * 
     * @return the string
     */
    public String toString();
}
