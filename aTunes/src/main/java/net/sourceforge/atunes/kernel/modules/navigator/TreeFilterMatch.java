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

package net.sourceforge.atunes.kernel.modules.navigator;

/**
 * Possible levels of a tree where a filter can match
 * 
 * @author alex
 * 
 */
public enum TreeFilterMatch {

	/**
	 * filter is empty or all entries match filter
	 */
	ALL,
	/**
	 * level for genre
	 */
	GENRE,
	/**
	 * level for year
	 */
	YEAR,
	/**
	 * Level for artist
	 */
	ARTIST,
	/**
	 * Level for album
	 */
	ALBUM,
	/**
	 * Any of the entries matches filter
	 */
	NONE;
}
