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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IPodcastFeedEntry;

import org.joda.time.DateTime;
import org.joda.time.base.BaseDateTime;

/**
 * Represents a entry of a podcast feed.
 */
public final class PodcastFeedEntry implements IPodcastFeedEntry {

    private static final long serialVersionUID = 4185336290582212484L;

    String title;
    String author;
    String url;
    String description;
    int duration;
    BaseDateTime date;
    IPodcastFeed podcastFeed;
    boolean listened;
    boolean downloaded;
    boolean old;
    
    /**
     * No arg constructor for serialization 
     */
    PodcastFeedEntry() {
	}

    /**
     * Constructor.
     * 
     * @param title
     *            the title of the podcast feed entry
     * @param author
     *            the author of the podcast feed entry
     * @param url
     *            the url of the podcast feed entry
     * @param description
     *            the description of the podcast feed entry
     * @param date
     *            the date of the podcast feed entry
     * @param duration
     *            the duration of the podcast feed entry
     * @param podcastFeed
     *            the corresponding podcast feed of the podcast feed entry
     */
    public PodcastFeedEntry(String title, String author, String url, String description, BaseDateTime date, int duration, IPodcastFeed podcastFeed) {
        this.title = title;
        this.author = author;
        this.url = url;
        this.description = description;
        this.date = date;
        this.podcastFeed = podcastFeed;
        this.duration = duration;
        listened = false;
    }

    /**
     * Gets the podcast feed entries.
     * 
     * @param audioObjects
     *            the audio objects
     * 
     * @return the podcast feed entries
     */
    public static List<IPodcastFeedEntry> getPodcastFeedEntries(List<IAudioObject> audioObjects) {
        List<IPodcastFeedEntry> result = new ArrayList<IPodcastFeedEntry>();
        for (IAudioObject audioObject : audioObjects) {
            if (audioObject instanceof IPodcastFeedEntry) {
                result.add((IPodcastFeedEntry) audioObject);
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IPodcastFeedEntry)) {
            return false;
        }
        return getUrl().equals(((IPodcastFeedEntry) o).getUrl());
    }

    @Override
    public String getAlbum() {
        return getPodcastFeed().getName();
    }

    @Override
    public String getAlbumArtist() {
        return "";
    }

    @Override
    public String getAlbumArtistOrArtist() {
        return getAlbumArtist().isEmpty() ? getArtist() : getAlbumArtist();
    }

    @Override
    public String getArtist() {
        return author;
    }

    @Override
    public long getBitrate() {
        return 0;
    }

    @Override
    public String getComposer() {
        return "";
    }

    /**
     * Gets the date.
     * 
     * @return the date
     */
    @Override
    public BaseDateTime getDate() {
        return date;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#getDescription()
	 */
    @Override
	public String getDescription() {
        return description;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getFrequency() {
        return 0;
    }

    @Override
    public String getGenre() {
        return "";
    }

    @Override
    public String getLyrics() {
        return "";
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#setOld(boolean)
	 */
    @Override
	public void setOld(boolean old) {
        this.old = old;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#getPodcastFeed()
	 */
    @Override
	public IPodcastFeed getPodcastFeed() {
        return podcastFeed;
    }

    @Override
    public int getStars() {
        return 0;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getTitleOrFileName() {
        return getTitle();
    }

    @Override
    public int getTrackNumber() {
        return 0;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getYear() {
        if (date != null) {
        	date.getYear();
        }
        return "";
    }

    @Override
    public String getComment() {
        return "";
    }

    @Override
    public int hashCode() {
        return getUrl().hashCode();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#isListened()
	 */
    @Override
	public boolean isListened() {
        return listened;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#isDownloaded()
	 */
    @Override
	public boolean isDownloaded() {
        return downloaded;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#isOld()
	 */
    @Override
	public boolean isOld() {
        return old;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#setDate(org.joda.time.DateTime)
	 */
    @Override
	public void setDate(DateTime date) {
        this.date = date;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#setDescription(java.lang.String)
	 */
    @Override
	public void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#setDownloaded(boolean)
	 */
    @Override
	public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#setListened(boolean)
	 */
    @Override
	public void setListened(boolean listened) {
        this.listened = listened;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#setPodcastFeed(net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed)
	 */
    @Override
	public void setPodcastFeed(IPodcastFeed podcastFeed) {
        this.podcastFeed = podcastFeed;
    }

    @Override
    public void setStars(int stars) {
        // Nothing to do
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#setUrl(java.lang.String)
	 */
    @Override
	public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean isSeekable() {
        return downloaded;
    }

    @Override
    public int compareTo(IPodcastFeedEntry o1) {
        return title.compareTo(o1.getTitle());
    }

    @Override
    public int getDiscNumber() {
        return 0;
    }

	@Override
	public String getAudioObjectDescription() {
		return getTitle();
	}
}
