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

  /** name of the folder under StatePodcast.podcastFeedEntryDownloadPath */
  String folderName;

	FeedType feedType;
	List<IPodcastFeedEntry> podcastFeedEntries;
	boolean hasNewEntries;

	/** If the name should be retrieved from the feed */
	boolean retrieveNameFromFeed;

  final String DEFAULT_EMPTY_NAME = "";

	/** No arg constructor for serialization */
	PodcastFeed() {
	}

	/**
	 * Constructor.
	 *
	 * @param name the name of the podcast feed
	 * @param url the url of the podcast feed
   * @param folderName {@link #folderName name od the feed folder}
	 */
	public PodcastFeed(final String name, final String url, final String folderName) {

    if (isEmptyString(name)) {
		  this.name = DEFAULT_EMPTY_NAME;
      this.retrieveNameFromFeed = true;
    }
    else {
      this.name = name;
    }
    if (!isEmptyString(folderName))  {
      this.folderName = folderName;
    }
    this.url = url;
		podcastFeedEntries = new ArrayList<IPodcastFeedEntry>();
	}

	public PodcastFeed(final String name, final String url) {
    this(name,url,null);
	}

  /** gets {@link #folderName} */
  @Override
  public String getFolderName () {
    return folderName;
  }

  /** Sets {@link #folderName} */
  @Override
  public void setFolderName (String folderName) {
    this.folderName = folderName;
  }

  private boolean isEmptyString (String name) {
    return (name == null || name.trim().isEmpty());
  }

  @Override
	public synchronized boolean equals(final Object o) {
		if (!(o instanceof PodcastFeed)) {
			return false;
		}
		return (name + url).equals(((IPodcastFeed) o).getName() + ((IPodcastFeed) o).getUrl());

	}

	@Override
	public synchronized List<IPodcastFeedEntry> getAudioObjects() {
		return new ArrayList<IPodcastFeedEntry>(podcastFeedEntries);
	}

	@Override
	public FeedType getFeedType() {
		return feedType;
	}

	@Override
	public synchronized String getName() {
		return name;
	}

	@Override
	public synchronized List<IPodcastFeedEntry> getPodcastFeedEntries() {
		return new ArrayList<IPodcastFeedEntry>(podcastFeedEntries);
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public synchronized int hashCode() {
		return (name + url).hashCode();
	}

	@Override
	public boolean hasNewEntries() {
		return hasNewEntries;
	}

	@Override
	public boolean isRetrieveNameFromFeed() {
		return retrieveNameFromFeed;
	}

	@Override
	public synchronized void markEntriesAsListened() {
		for (IPodcastFeedEntry entry : podcastFeedEntries) {
			entry.setListened(true);
		}
	}

	@Override
	public void markEntriesAsNotNew() {
		hasNewEntries = false;
	}

	@Override
	public synchronized void addEntries(final List<? extends IPodcastFeedEntry> entries, final boolean removeOld) {
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

	@Override
	public synchronized void removeEntry(final IPodcastFeedEntry podcastFeedEntry) {
		podcastFeedEntries.remove(podcastFeedEntry);
	}

	@Override
	public void setFeedType(final FeedType feedType) {
		this.feedType = feedType;
	}

	@Override
	public synchronized void setName(final String name) {
		this.name = name;
	}

	@Override
	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public void setRetrieveNameFromFeed(final boolean retrieveNameFromFeed) {
		this.retrieveNameFromFeed = retrieveNameFromFeed;
	}

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
	public int size() {
		return podcastFeedEntries.size();
	}
}
