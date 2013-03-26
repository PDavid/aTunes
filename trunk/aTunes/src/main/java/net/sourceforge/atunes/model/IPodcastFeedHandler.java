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

import java.util.List;

/**
 * Manages podcasts
 * 
 * @author alex
 * 
 */
public interface IPodcastFeedHandler extends IHandler {

    /**
     * Adds a Podcast Feed.
     */
    public void addPodcastFeed();

    /**
     * Returns a list with all Podcast Feeds.
     * 
     * @return The podcast feeds
     */
    public List<IPodcastFeed> getPodcastFeeds();

    /**
     * Returns a list with all Podcast Feed Entries.
     * 
     * @return A list with all Podcast Feed Entries
     */
    public List<IPodcastFeedEntry> getPodcastFeedEntries();

    /**
     * Removes a Podcast Feed.
     * 
     * @param podcastFeed
     *            A Podcast Feed that should be removed
     */
    public void removePodcastFeed(IPodcastFeed podcastFeed);

    /**
     * Retrieves Podcast Feed Entries and refreshes view asynchronously.
     * 
     * @see net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntryRetriever#retrievePodcastFeedEntries()
     */
    public void retrievePodcastFeedEntries();

    /**
     * Download Podcast Feed Entry.
     * 
     * @param podcastFeedEntry
     *            the podcast feed entry
     */
    public void downloadPodcastFeedEntry(
	    final IPodcastFeedEntry podcastFeedEntry);

    /**
     * Gets the download path.
     * 
     * @param podcastFeedEntry
     *            the podcast feed entry
     * 
     * @return the download path
     */
    public String getDownloadPath(IPodcastFeedEntry podcastFeedEntry);

    /**
     * Checks if is downloaded.
     * 
     * @param podcastFeedEntry
     *            the podcast feed entry
     * 
     * @return true, if is downloaded
     */
    public boolean isDownloaded(IPodcastFeedEntry podcastFeedEntry);

    /**
     * Delete downloaded podcast feed entry.
     * 
     * @param podcastFeedEntry
     *            the podcast feed entry
     */
    public void deleteDownloadedPodcastFeedEntry(
	    final IPodcastFeedEntry podcastFeedEntry);

    /**
     * Checks if is downloading.
     * 
     * @param podcastFeedEntry
     *            the podcast feed entry
     * 
     * @return true, if is downloading
     */
    public boolean isDownloading(IPodcastFeedEntry podcastFeedEntry);

}