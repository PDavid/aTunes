/*
 * aTunes 1.14.0
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
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.utils.DateUtils;

/**
 * Represents a entry of a podcast feed.
 */
public class PodcastFeedEntry implements AudioObject, Serializable, Comparable<PodcastFeedEntry> {

    private static final long serialVersionUID = 4185336290582212484L;

    /** The comparator. */
    private static Comparator<PodcastFeedEntry> comparator = new Comparator<PodcastFeedEntry>() {
        @Override
        public int compare(PodcastFeedEntry o1, PodcastFeedEntry o2) {
            return o1.name.compareToIgnoreCase(o2.name);
        }
    };

    /** The name. */
    String name;

    /** The author. */
    private String author;

    /** The url. */
    private String url;

    /** The description. */
    private String description;

    /** The duration. */
    private long duration;

    /** The date. */
    private Date date;

    /** The podcast feed. */
    private PodcastFeed podcastFeed;

    /** The listened. */
    private boolean listened;

    /** The downloaded. */
    private boolean downloaded;

    /** The old. */
    private boolean old;

    /**
     * Constructor.
     * 
     * @param name
     *            the name of the podcast feed entry
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
    public PodcastFeedEntry(String name, String author, String url, String description, Date date, long duration, PodcastFeed podcastFeed) {
        this.name = name;
        this.author = author;
        this.url = url;
        this.description = description;
        this.date = date == null ? null : new Date(date.getTime());
        this.podcastFeed = podcastFeed;
        this.duration = duration;
        listened = false;
    }

    /**
     * Gets the comparator.
     * 
     * @return the comparator of the podcast feed entries
     */
    public static Comparator<PodcastFeedEntry> getComparator() {
        return comparator;
    }

    /**
     * Gets the podcast feed entries.
     * 
     * @param audioObjects
     *            the audio objects
     * 
     * @return the podcast feed entries
     */
    public static List<PodcastFeedEntry> getPodcastFeedEntries(List<AudioObject> audioObjects) {
        List<PodcastFeedEntry> result = new ArrayList<PodcastFeedEntry>();
        for (AudioObject audioObject : audioObjects) {
            if (audioObject instanceof PodcastFeedEntry) {
                result.add((PodcastFeedEntry) audioObject);
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof PodcastFeedEntry)) {
            return false;
        }
        return getUrl().equals(((PodcastFeedEntry) o).getUrl());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getAlbum()
     */
    @Override
    public String getAlbum() {
        return getPodcastFeed().getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getAlbumArtist()
     */
    @Override
    public String getAlbumArtist() {
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getArtist()
     */
    @Override
    public String getArtist() {
        return author;
    }

    /**
     * Gets the author.
     * 
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getBitrate()
     */
    @Override
    public long getBitrate() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getComposer()
     */
    @Override
    public String getComposer() {
        return "";
    }

    /**
     * Gets the date.
     * 
     * @return the date
     */
    public Date getDate() {
        return date == null ? null : new Date(date.getTime());
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getDuration()
     */
    @Override
    public long getDuration() {
        return duration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getFrequency()
     */
    @Override
    public int getFrequency() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getGenre()
     */
    @Override
    public String getGenre() {
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getLyrics()
     */
    @Override
    public String getLyrics() {
        return "";
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the old.
     * 
     * @param old
     *            the new old
     */
    public void setOld(boolean old) {
        this.old = old;
    }

    /**
     * Gets the podcast feed.
     * 
     * @return The corresponding podcast feed
     */
    public PodcastFeed getPodcastFeed() {
        return podcastFeed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getStars()
     */
    @Override
    public int getStars() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getTitle()
     */
    @Override
    public String getTitle() {
        return getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getTitleOrFileName()
     */
    @Override
    public String getTitleOrFileName() {
        return getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getTrackNumber()
     */

    @Override
    public Integer getTrackNumber() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getUrl()
     */
    @Override
    public String getUrl() {
        return url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getYear()
     */
    @Override
    public String getYear() {
        if (date != null) {
            Calendar calendar = DateUtils.getCalendar();
            calendar.setTime(date);
            return String.valueOf(calendar.get(Calendar.YEAR));
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getUrl().hashCode();
    }

    /**
     * Checks if is listened.
     * 
     * @return if the podcast feed entry was already listened
     */
    public boolean isListened() {
        return listened;
    }

    /**
     * Checks if is downloaded.
     * 
     * @return true, if is downloaded
     */
    public boolean isDownloaded() {
        return downloaded;
    }

    /**
     * Checks if is old.
     * 
     * @return true, if is old
     */
    public boolean isOld() {
        return old;
    }

    /**
     * Sets the author.
     * 
     * @param author
     *            the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Sets the date.
     * 
     * @param date
     *            the date to set
     */
    public void setDate(Date date) {
        this.date = date == null ? null : new Date(date.getTime());
    }

    /**
     * Sets the description.
     * 
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the downloaded.
     * 
     * @param downloaded
     *            the new downloaded
     */
    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    /**
     * Sets the listened.
     * 
     * @param listened
     *            if the podcast feed entry was already listened
     */
    public void setListened(boolean listened) {
        this.listened = listened;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the podcast feed.
     * 
     * @param podcastFeed
     *            the corresponding podcast feed to set
     */
    public void setPodcastFeed(PodcastFeed podcastFeed) {
        this.podcastFeed = podcastFeed;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#setStars(int)
     */
    @Override
    public void setStars(int stars) {
        // Nothing to do
    }

    /**
     * Sets the url.
     * 
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
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

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#isSeekable()
     */
    @Override
    public boolean isSeekable() {
        return downloaded;
    }

    @Override
    public int compareTo(PodcastFeedEntry o1) {
        return name.compareTo(o1.name);
    }

    @Override
    public Integer getDiscNumber() {
        return 0;
    }

    @Override
    public boolean canHaveCustomImages() {
        return false;
    }

    @Override
    public ImageIcon getCustomImage(int width, int height) {
        return null;
    }

    @Override
    public ImageIcon getGenericImage(GenericImageSize imageSize) {
        switch (imageSize) {
        case SMALL: {
            return ImageLoader.getImage(ImageLoader.RSS_LITTLE);
        }
        case MEDIUM: {
            return ImageLoader.getImage(ImageLoader.RSS);
        }
        case BIG: {
            return ImageLoader.getImage(ImageLoader.RSS_BIG);
        }
        default: {
            throw new IllegalArgumentException("unknown image size");
        }
        }
    }

}
