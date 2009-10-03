/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Represents a rss or atom podcast feed.
 */
public class PodcastFeed implements TreeObject, Serializable {

    private static final long serialVersionUID = 1416452911272034086L;

    /** The logger. */
    private static Logger logger = new Logger();

    /** The comparator. */
    private static Comparator<PodcastFeed> comparator = new Comparator<PodcastFeed>() {
        @Override
        public int compare(PodcastFeed o1, PodcastFeed o2) {
            return o1.name.compareToIgnoreCase(o2.name);
        }
    };

    /** The name. */
    String name;

    /** The url. */
    private String url;

    /** The feed type. */
    private FeedType feedType;

    /** The podcast feed entries. */
    private List<PodcastFeedEntry> podcastFeedEntries;

    /** The has new entries. */
    private boolean hasNewEntries;

    /** If the name should be retrieved from the feed */
    private boolean retrieveNameFromFeed;

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
        podcastFeedEntries = new ArrayList<PodcastFeedEntry>();
    }

    /**
     * Gets the comparator.
     * 
     * @return the comparator
     */
    public static Comparator<PodcastFeed> getComparator() {
        return comparator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof PodcastFeed)) {
            return false;
        }
        return (name + url).equals(((PodcastFeed) o).getName() + ((PodcastFeed) o).getUrl());

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.TreeObject#getAudioObjects()
     */
    @Override
    public List<AudioObject> getAudioObjects() {
        return new ArrayList<AudioObject>(podcastFeedEntries);
    }

    /**
     * Gets the feed type.
     * 
     * @return the feedType of the podcast feed
     */
    public FeedType getFeedType() {
        return feedType;
    }

    /**
     * Gets the name.
     * 
     * @return the name of the podcast feed
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the podcast feed entries.
     * 
     * @return the podcast feed entries
     */
    public List<PodcastFeedEntry> getPodcastFeedEntries() {
        return new ArrayList<PodcastFeedEntry>(podcastFeedEntries);
    }

    /**
     * Gets the url.
     * 
     * @return the url of the podcast feed
     */
    public String getUrl() {
        return url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (name + url).hashCode();
    }

    /**
     * Checks for new entries.
     * 
     * @return if the podcast feed has new entries
     */
    public boolean hasNewEntries() {
        return hasNewEntries;
    }

    /**
     * Returns if the name should be retrieved from the feed
     * 
     * @return retrieveNameFromFeed if the name should be retrieved from the
     *         feed
     */
    public boolean isRetrieveNameFromFeed() {
        return retrieveNameFromFeed;
    }

    /**
     * Marks the entries of this podcast feed as listened.
     */
    public void markEntriesAsListened() {
        for (PodcastFeedEntry entry : podcastFeedEntries) {
            entry.setListened(true);
        }
    }

    /**
     * Marks the entries of this podcastfeed as not new.
     */
    public void markEntriesAsNotNew() {
        hasNewEntries = false;
    }

    /**
     * Sets the entries of this podcast feed and removes old entries if
     * specified.
     * 
     * @param entries
     *            The entries of this podcast feed
     * @param removeOld
     *            If old entries should be removed
     */
    public void addEntries(List<? extends PodcastFeedEntry> entries, boolean removeOld) {
        logger.debug(LogCategories.PODCAST);

        List<? extends PodcastFeedEntry> newEntries = new ArrayList<PodcastFeedEntry>(entries);

        List<PodcastFeedEntry> oldEntries = new ArrayList<PodcastFeedEntry>(podcastFeedEntries);
        oldEntries.removeAll(newEntries);
        if (removeOld) {
            podcastFeedEntries.removeAll(oldEntries);
        }
        for (PodcastFeedEntry oldPodcastFeedEntry : oldEntries) {
            oldPodcastFeedEntry.setOld(true);
        }

        newEntries.removeAll(podcastFeedEntries);
        if (!newEntries.isEmpty()) {
            hasNewEntries = true;
        }
        for (PodcastFeedEntry newPodcastFeedEntry : newEntries) {
            newPodcastFeedEntry.setOld(false);
        }
        podcastFeedEntries.addAll(0, newEntries);
    }

    /**
     * Removed Podcast feed entry from this Podcast feed.
     * 
     * @param podcastFeedEntry
     *            The Podcast feed entr that should be removed
     */
    public void removeEntry(PodcastFeedEntry podcastFeedEntry) {
        podcastFeedEntries.remove(podcastFeedEntry);
    }

    /**
     * Sets the feed type.
     * 
     * @param feedType
     *            the feedType of the podcast feed to set
     */
    public void setFeedType(FeedType feedType) {
        this.feedType = feedType;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the name of the podcast feed to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the url.
     * 
     * @param url
     *            the url of the podcast feed to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Sets if the name should be retrieved from the feed
     * 
     * @param retrieveNameFromFeed
     *            if the name should be retrieved from the feed
     */
    public void setRetrieveNameFromFeed(boolean retrieveNameFromFeed) {
        this.retrieveNameFromFeed = retrieveNameFromFeed;
    }

    /**
     * Returns number of new entries of this podcast
     * 
     * @return
     */
    public int getNewEntriesCount() {
        int newEntries = 0;
        for (PodcastFeedEntry entry : podcastFeedEntries) {
            if (!entry.isListened()) {
                newEntries++;
            }
        }
        return newEntries;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getToolTip() {
        return name;
    }

    @Override
    public boolean isExtendedToolTipSupported() {
        return true;
    }

    @Override
    public void setExtendedToolTip(ExtendedToolTip toolTip) {
        toolTip.setLine1(name);
        toolTip.setLine2(StringUtils.getString(I18nUtils.getString("PODCAST_ENTRIES"), ": ", podcastFeedEntries.size()));
        toolTip.setLine3(StringUtils.getString(I18nUtils.getString("NEW_PODCAST_ENTRIES_TOOLTIP"), ": ", getNewEntriesCount()));
    }

    @Override
    public ImageIcon getExtendedToolTipImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isExtendedToolTipImageSupported() {
        // TODO Auto-generated method stub
        return false;
    }
}
