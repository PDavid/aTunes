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
 * State specific for podcasts
 *
 * @author alex
 *
 */
public interface IStatePodcast extends IState {

	/**
	 * Podcast retrieval interval
	 *
	 * @return
	 */
	public long getPodcastFeedEntriesRetrievalInterval();

	/**
	 * Podcast retrieval interval
	 *
	 * @param podcastFeedEntriesRetrievalInterval
	 */
	public void setPodcastFeedEntriesRetrievalInterval(
			long podcastFeedEntriesRetrievalInterval);

	/**
	 * Retrieves podcast download path.
	 *
	 * @return podcast's download path
	 */
	public String getPodcastFeedEntryDownloadPath();

	/**
	 * sets podcast download path.
	 *
	 * @param podcastFeedEntryDownloadPath
	 */
	public void setPodcastFeedEntryDownloadPath(String podcastFeedEntryDownloadPath);

	/**
	 * Use downloaded podcasts
	 *
	 * @return
	 */
	public boolean isUseDownloadedPodcastFeedEntries();

	/**
	 * Use downloaded podcasts
	 *
	 * @param useDownloadedPodcastFeedEntries
	 */
	public void setUseDownloadedPodcastFeedEntries(
			boolean useDownloadedPodcastFeedEntries);

	/**
	 * Remove podcast entries
	 *
	 * @return
	 */
	public boolean isRemovePodcastFeedEntriesRemovedFromPodcastFeed();

	/**
	 * Remove podcast entries
	 *
	 * @param removePodcastFeedEntriesRemovedFromPodcastFeed
	 */
	public void setRemovePodcastFeedEntriesRemovedFromPodcastFeed(
			boolean removePodcastFeedEntriesRemovedFromPodcastFeed);

}
