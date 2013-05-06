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

import java.io.Serializable;

/**
 * Represents information about an albums' track, retrieved from a web service
 * 
 * @author alex
 * 
 */
public interface ITrackInfo extends Serializable {

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	String getTitle();

	/**
	 * Gets the artist
	 * 
	 * @return
	 */
	String getArtist();

	/**
	 * Gets the album
	 * 
	 * @return
	 */
	String getAlbum();

	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
	String getUrl();

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the title to set
	 */
	void setTitle(String title);

	/**
	 * Sets the url.
	 * 
	 * @param url
	 *            the url to set
	 */
	void setUrl(String url);

	/**
	 * Sets the artist
	 * 
	 * @param artist
	 */
	void setArtist(String artist);

	/**
	 * Sets the album
	 * 
	 * @param album
	 */
	void setAlbum(String album);

	/**
	 * @return if track is available in repository
	 */
	boolean isAvailable();

	/**
	 * @param available
	 */
	void setAvailable(boolean available);

	/**
	 * @return if track is favorite
	 */
	boolean isFavorite();

	/**
	 * @param favorite
	 */
	void setFavorite(boolean favorite);
}
