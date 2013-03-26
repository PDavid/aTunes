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
 * Represents an album of an artist. Used to associate to statistics
 * @author alex
 *
 */
public interface IStatisticsAlbum {

	/**
	 * @return the artist
	 */
	public String getArtist();

	/**
	 * @param artist
	 *            the artist to set
	 */
	public void setArtist(String artist);

	/**
	 * @return the album
	 */
	public String getAlbum();

	/**
	 * @param album
	 *            the album to set
	 */
	public void setAlbum(String album);
}