/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.podcast;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.FeedType;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IPodcastFeedEntry;

/**
 * Represents a rss or atom podcast feed.
 */
public class PodcastFeed implements IPodcastFeed {

    private static final long serialVersionUID = 1416452911272034086L;

    String name;
    String url;
    FeedType feedType;
    List<IPodcastFeedEntry> podcastFeedEntries;
    boolean hasNewEntries;
    /** If the name should be retrieved from the feed */
    boolean retrieveNameFromFeed;
    
    /**
     * No arg constructor for serialization
     */
    PodcastFeed() {
	}

    /**
     * Constructor.
     * 
     * @param name
     *            the name of the podcast feed
     * @param url
     *            the url of the podcast feed
     */
    public PodcastFeed(String name, String url) {
        this.name = name;
        this.url = url;
        podcastFeedEntries = new ArrayList<IPodcastFeedEntry>();
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (!(o instanceof PodcastFeed)) {
            return false;
        }
        return (name + url).equals(((IPodcastFeed) o).getName() + ((IPodcastFeed) o).getUrl());

    }

    @Override
    public synchronized List<IPodcastFeedEntry> getAudioObjects() {
        return new ArrayList<IPodcastFeedEntry>(podcastFeedEntries);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#getFeedType()
	 */
    @Override
	public FeedType getFeedType() {
        return feedType;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#getName()
	 */
    @Override
	public synchronized String getName() {
        return name;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#getPodcastFeedEntries()
	 */
    @Override
	public synchronized List<IPodcastFeedEntry> getPodcastFeedEntries() {
        return new ArrayList<IPodcastFeedEntry>(podcastFeedEntries);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#getUrl()
	 */
    @Override
	public String getUrl() {
        return url;
    }

    @Override
    public synchronized int hashCode() {
        return (name + url).hashCode();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#hasNewEntries()
	 */
    @Override
	public boolean hasNewEntries() {
        return hasNewEntries;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#isRetrieveNameFromFeed()
	 */
    @Override
	public boolean isRetrieveNameFromFeed() {
        return retrieveNameFromFeed;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#markEntriesAsListened()
	 */
    @Override
	public synchronized void markEntriesAsListened() {
        for (IPodcastFeedEntry entry : podcastFeedEntries) {
            entry.setListened(true);
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#markEntriesAsNotNew()
	 */
    @Override
	public void markEntriesAsNotNew() {
        hasNewEntries = false;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#addEntries(java.util.List, boolean)
	 */
    @Override
	public synchronized void addEntries(List<? extends IPodcastFeedEntry> entries, boolean removeOld) {
        List<? extends IPodcastFeedEntry> newEntries = new ArrayList<IPodcastFeedEntry>(entries);

        List<IPodcastFeedEntry> oldEntries = new ArrayList<IPodcastFeedEntry>(podcastFeedEntries);
        oldEntries.removeAll(newEntries);
        if (removeOld) {
            podcastFeedEntries.removeAll(oldEntries);
        }
        for (IPodcastFeedEntry oldPodcastFeedEntry : oldEntries) {
            oldPodcastFeedEntry.setOld(true);
        }

        newEntries.removeAll(podcastFeedEntries);
        if (!newEntries.isEmpty()) {
            hasNewEntries = true;
        }
        for (IPodcastFeedEntry newPodcastFeedEntry : newEntries) {
            newPodcastFeedEntry.setOld(false);
        }
        podcastFeedEntries.addAll(0, newEntries);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#removeEntry(net.sourceforge.atunes.model.IPodcastFeedEntry)
	 */
    @Override
	public synchronized void removeEntry(IPodcastFeedEntry podcastFeedEntry) {
        podcastFeedEntries.remove(podcastFeedEntry);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#setFeedType(net.sourceforge.atunes.kernel.modules.podcast.FeedType)
	 */
    @Override
	public void setFeedType(FeedType feedType) {
        this.feedType = feedType;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#setName(java.lang.String)
	 */
    @Override
	public synchronized void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#setUrl(java.lang.String)
	 */
    @Override
	public void setUrl(String url) {
        this.url = url;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#setRetrieveNameFromFeed(boolean)
	 */
    @Override
	public void setRetrieveNameFromFeed(boolean retrieveNameFromFeed) {
        this.retrieveNameFromFeed = retrieveNameFromFeed;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeed#getNewEntriesCount()
	 */
    @Override
	public synchronized int getNewEntriesCount() {
        int newEntries = 0;
        for (IPodcastFeedEntry entry : podcastFeedEntries) {
            if (!entry.isListened()) {
                newEntries++;
            }
        }
        return newEntries;
    }

    @Override
    public synchronized String toString() {
        return name;
    }

    @Override
    public synchronized String getTooltip() {
        return name;
    }

    @Override
    public boolean isExtendedTooltipSupported() {
        return true;
    }

    @Override
    public boolean isExtendedTooltipImageSupported() {
        return false;
    }
    
    @Override
    public int size() {
    	return podcastFeedEntries.size();
    }
}
