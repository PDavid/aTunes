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
import java.util.List;


/**
 * A feed of podcast entries
 * @author alex
 *
 */
public interface IPodcastFeed extends ITreeObject<IPodcastFeedEntry>, Serializable {

	/**
	 * Gets the feed type.
	 *
	 * @return the feedType of the podcast feed
	 */
	public FeedType getFeedType();

	/**
	 * Gets the name.
	 *
	 * @return the name of the podcast feed
	 */
	public String getName();

	/**
	 * Gets the podcast feed entries.
	 *
	 * @return the podcast feed entries
	 */
	public List<IPodcastFeedEntry> getPodcastFeedEntries();

	/**
	 * Gets the url.
	 *
	 * @return the url of the podcast feed
	 */
	public String getUrl();

	/**
	 * Checks for new entries.
	 *
	 * @return if the podcast feed has new entries
	 */
	public boolean hasNewEntries();

	/**
	 * Returns if the name should be retrieved from the feed
	 *
	 * @return retrieveNameFromFeed if the name should be retrieved from the
	 *         feed
	 */
	public boolean isRetrieveNameFromFeed();

	/**
	 * Marks the entries of this podcast feed as listened.
	 */
	public void markEntriesAsListened();

	/**
	 * Marks the entries of this podcastfeed as not new.
	 */
	public void markEntriesAsNotNew();

	/**
	 * Sets the entries of this podcast feed and removes old entries if
	 * specified.
	 *
	 * @param entries
	 *            The entries of this podcast feed
	 * @param removeOld
	 *            If old entries should be removed
	 */
	public void addEntries(List<? extends IPodcastFeedEntry> entries,
			boolean removeOld);

	/**
	 * Removed Podcast feed entry from this Podcast feed.
	 *
	 * @param podcastFeedEntry
	 *            The Podcast feed entr that should be removed
	 */
	public void removeEntry(IPodcastFeedEntry podcastFeedEntry);

	/**
	 * Sets the feed type.
	 *
	 * @param feedType
	 *            the feedType of the podcast feed to set
	 */
	public void setFeedType(FeedType feedType);

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the name of the podcast feed to set
	 */
	public void setName(String name);

	/**
	 * Sets the url.
	 *
	 * @param url
	 *            the url of the podcast feed to set
	 */
	public void setUrl(String url);

	/**
	 * Sets if the name should be retrieved from the feed
	 *
	 * @param retrieveNameFromFeed
	 *            if the name should be retrieved from the feed
	 */
	public void setRetrieveNameFromFeed(boolean retrieveNameFromFeed);

	/**
	 * Returns number of new entries of this podcast
	 *
	 * @return
	 */
	public int getNewEntriesCount();

  /** Gets a name of the podcast feed's folder */
   public String getFolderName ();

   /** Sets a name of the podcast feed's folder */
   public void setFolderName (String folderName);

}