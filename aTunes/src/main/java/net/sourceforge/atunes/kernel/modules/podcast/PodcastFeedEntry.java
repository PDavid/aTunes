/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.gui.images.RssImageIcon;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.DateUtils;

/**
 * Represents a entry of a podcast feed.
 */
public final class PodcastFeedEntry implements AudioObject, Serializable, Comparable<PodcastFeedEntry> {

    private static final long serialVersionUID = 4185336290582212484L;

    private static Comparator<PodcastFeedEntry> comparator = new Comparator<PodcastFeedEntry>() {
        @Override
        public int compare(PodcastFeedEntry o1, PodcastFeedEntry o2) {
            return o1.title.compareToIgnoreCase(o2.title);
        }
    };

    private String title;
    private String author;
    private String url;
    private String description;
    private int duration;
    private Date date;
    private PodcastFeed podcastFeed;
    private boolean listened;
    private boolean downloaded;
    private boolean old;

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
    public PodcastFeedEntry(String title, String author, String url, String description, Date date, int duration, PodcastFeed podcastFeed) {
        this.title = title;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PodcastFeedEntry)) {
            return false;
        }
        return getUrl().equals(((PodcastFeedEntry) o).getUrl());
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
            Calendar calendar = DateUtils.getCalendar();
            calendar.setTime(date);
            return String.valueOf(calendar.get(Calendar.YEAR));
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
     * Sets the podcast feed.
     * 
     * @param podcastFeed
     *            the corresponding podcast feed to set
     */
    public void setPodcastFeed(PodcastFeed podcastFeed) {
        this.podcastFeed = podcastFeed;
    }

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

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean isSeekable() {
        return downloaded;
    }

    @Override
    public int compareTo(PodcastFeedEntry o1) {
        return title.compareTo(o1.title);
    }

    @Override
    public int getDiscNumber() {
        return 0;
    }

    @Override
    public ImageIcon getImage(ImageSize imageSize) {
        return null;
    }

    @Override
    public ImageIcon getGenericImage(GenericImageSize imageSize) {
        switch (imageSize) {
        case SMALL: {
            return RssImageIcon.getSmallIcon();
        }
        case MEDIUM: {
            return RssImageIcon.getIcon();
        }
        case BIG: {
            return RssImageIcon.getBigIcon();
        }
        default: {
            throw new IllegalArgumentException("unknown image size");
        }
        }
    }

}
