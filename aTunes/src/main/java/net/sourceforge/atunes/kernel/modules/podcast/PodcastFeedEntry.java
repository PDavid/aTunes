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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

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
  String folderName;
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
	public PodcastFeedEntry(final String title, final String author,
			final String url, final String description,
			final BaseDateTime date, final int duration,
			final IPodcastFeed podcastFeed) {
		this.title = title;
		this.author = author;
		this.url = url;
		this.description = description;
		this.date = date;
		this.podcastFeed = podcastFeed;
		this.duration = duration;
		this.listened = false;
	}

	/**
	 * Gets the podcast feed entries.
	 *
	 * @param audioObjects
	 *            the audio objects
	 *
	 * @return the podcast feed entries
	 */
	public static List<IPodcastFeedEntry> getPodcastFeedEntries(
			final List<IAudioObject> audioObjects) {
		List<IPodcastFeedEntry> result = new ArrayList<IPodcastFeedEntry>();
		for (IAudioObject audioObject : audioObjects) {
			if (audioObject instanceof IPodcastFeedEntry) {
				result.add((IPodcastFeedEntry) audioObject);
			}
		}
		return result;
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof IPodcastFeedEntry)) {
			return false;
		}
		return getUrl().equals(((IPodcastFeedEntry) o).getUrl());
	}

	@Override
	public String getAlbum(final IUnknownObjectChecker unknownObjectChecker) {
		return getPodcastFeed().getName();
	}

	@Override
	public String getAlbumArtist(
			final IUnknownObjectChecker unknownObjectChecker) {
		return "";
	}

	@Override
	public String getAlbumArtistOrArtist(
			final IUnknownObjectChecker unknownObjectChecker) {
		return this.author;
	}

	@Override
	public String getArtist(final IUnknownObjectChecker unknownObjectChecker) {
		return this.author;
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
		return this.date;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public int getDuration() {
		return this.duration;
	}

	@Override
	public int getFrequency() {
		return 0;
	}

	@Override
	public String getGenre(final IUnknownObjectChecker unknownObjectChecker) {
		return "";
	}

	@Override
	public String getLyrics() {
		return "";
	}

	@Override
	public void setOld(final boolean old) {
		this.old = old;
	}

	@Override
	public IPodcastFeed getPodcastFeed() {
		return this.podcastFeed;
	}

	@Override
	public int getStars() {
		return 0;
	}

	@Override
	public String getTitle() {
		return this.title;
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
		return this.url;
	}

	@Override
	public String getYear(final IUnknownObjectChecker unknownObjectChecker) {
		if (this.date != null) {
			this.date.getYear();
		}
		return unknownObjectChecker != null ? unknownObjectChecker
				.getUnknownYear() : "";
	}

	@Override
	public String getComment() {
		return "";
	}

	@Override
	public int hashCode() {
		return getUrl().hashCode();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#isListened
	 * ()
	 */
	@Override
	public boolean isListened() {
		return this.listened;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#isDownloaded
	 * ()
	 */
	@Override
	public boolean isDownloaded() {
		return this.downloaded;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#isOld()
	 */
	@Override
	public boolean isOld() {
		return this.old;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#setDate
	 * (org.joda.time.DateTime)
	 */
	@Override
	public void setDate(final DateTime date) {
		this.date = date;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#
	 * setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#setDownloaded
	 * (boolean)
	 */
	@Override
	public void setDownloaded(final boolean downloaded) {
		this.downloaded = downloaded;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#setListened
	 * (boolean)
	 */
	@Override
	public void setListened(final boolean listened) {
		this.listened = listened;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#
	 * setPodcastFeed(net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed)
	 */
	@Override
	public void setPodcastFeed(final IPodcastFeed podcastFeed) {
		this.podcastFeed = podcastFeed;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * net.sourceforge.atunes.kernel.modules.podcast.IPodcastFeedEntry#setUrl
	 * (java.lang.String)
	 */
	@Override
	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return this.title;
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	@Override
	public int compareTo(final IPodcastFeedEntry o1) {
		return this.title.compareTo(o1.getTitle());
	}

	@Override
	public int getDiscNumber() {
		return 0;
	}

	@Override
	public String getAudioObjectDescription(
			final IUnknownObjectChecker unknownObjectChecker) {
		return getTitle();
	}
}
