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

import org.joda.time.DateTime;

/**
 * An entry of a podcast feed
 * @author alex
 *
 */
public interface IPodcastFeedEntry extends IAudioObject, Serializable, Comparable<IPodcastFeedEntry> {

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription();

	/**
	 * Sets the old.
	 * 
	 * @param old
	 *            the new old
	 */
	public void setOld(boolean old);

	/**
	 * Gets the podcast feed.
	 * 
	 * @return The corresponding podcast feed
	 */
	public IPodcastFeed getPodcastFeed();

	/**
	 * Checks if is listened.
	 * 
	 * @return if the podcast feed entry was already listened
	 */
	public boolean isListened();

	/**
	 * Checks if is downloaded.
	 * 
	 * @return true, if is downloaded
	 */
	public boolean isDownloaded();

	/**
	 * Checks if is old.
	 * 
	 * @return true, if is old
	 */
	public boolean isOld();

	/**
	 * Sets the date.
	 * 
	 * @param date
	 *            the date to set
	 */
	public void setDate(DateTime date);

	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description);

	/**
	 * Sets the downloaded.
	 * 
	 * @param downloaded
	 *            the new downloaded
	 */
	public void setDownloaded(boolean downloaded);

	/**
	 * Sets the listened.
	 * 
	 * @param listened
	 *            if the podcast feed entry was already listened
	 */
	public void setListened(boolean listened);

	/**
	 * Sets the podcast feed.
	 * 
	 * @param podcastFeed
	 *            the corresponding podcast feed to set
	 */
	public void setPodcastFeed(IPodcastFeed podcastFeed);

	/**
	 * Sets the url.
	 * 
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url);

}