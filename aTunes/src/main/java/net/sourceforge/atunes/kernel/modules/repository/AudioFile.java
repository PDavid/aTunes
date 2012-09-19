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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;

import org.commonjukebox.plugins.model.PluginApi;
import org.joda.time.base.BaseDateTime;

/**
 * AudioFile class initializes audio files so that tags and audio information
 * can be retrieved by the tagging library. Provides information about bitrate,
 * duration and frequency of the audio file. Provides tag information.
 * 
 * @author fleax
 */
@PluginApi
public final class AudioFile implements ILocalAudioObject, Serializable {

	private static final long serialVersionUID = -1139001443603556703L;

	private static final String UNKNOWN_ARTIST = Context.getBean(IUnknownObjectChecker.class).getUnknownArtist();
	private static final String UNKNOWN_ALBUM = Context.getBean(IUnknownObjectChecker.class).getUnknownAlbum();
	private static final String UNKNOWN_GENRE = Context.getBean(IUnknownObjectChecker.class).getUnknownGenre();

	ITag tag;
	int duration;
	long bitrate;
	int frequency;
	long readTime;
	int stars;

	private transient long fileSize;

	/** The file on disk. */
	String filePath;

	/**
	 * Default constructor for serialization
	 */
	AudioFile() {
	}

	/**
	 * Instantiates a new audio file
	 * 
	 * @param file
	 *            the file
	 */
	public AudioFile(final File file) {
		setFile(file);
	}

	/**
	 * Instantiates a new audio file
	 * 
	 * @param fileName
	 *            the file name
	 */
	public AudioFile(final String fileName) {
		this.filePath = fileName;
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof AudioFile)) {
			return false;
		}
		return ((AudioFile) o).getUrl().equals(getUrl());
	}

	@Override
	public String getAlbum() {
		if (tag != null && tag.getAlbum() != null && !tag.getAlbum().isEmpty()) {
			return tag.getAlbum();
		}
		return UNKNOWN_ALBUM;
	}

	@Override
	public String getAlbumArtist() {
		if (tag != null && tag.getAlbumArtist() != null && !tag.getAlbumArtist().isEmpty()) {
			return tag.getAlbumArtist();
		}
		return UNKNOWN_ARTIST;
	}

	@Override
	public String getAlbumArtistOrArtist() {
		return getAlbumArtist().isEmpty() || getAlbumArtist().equals(UNKNOWN_ARTIST) ? getArtist() : getAlbumArtist();
	}

	@Override
	public String getArtist() {
		if (tag != null && tag.getArtist() != null && !tag.getArtist().isEmpty()) {
			return tag.getArtist();
		}
		return UNKNOWN_ARTIST;
	}

	@Override
	public long getBitrate() {
		return bitrate;
	}

	@Override
	public String getComposer() {
		if (tag != null && tag.getComposer() != null) {
			return tag.getComposer();
		}
		return "";
	}

	@Override
	public int getDuration() {
		return duration;
	}

	/**
	 * Return the file on disk
	 * 
	 * @return the file on disk
	 */
	@Override
	public File getFile() {
		return filePath != null ? new File(filePath) : null;
	}

	@Override
	public int getFrequency() {
		return frequency;
	}

	@Override
	public String getGenre() {
		if (tag != null && tag.getGenre() != null && !tag.getGenre().isEmpty()) {
			return tag.getGenre();
		}
		return UNKNOWN_GENRE;
	}

	@Override
	public String getLyrics() {
		if (tag != null && tag.getLyrics() != null) {
			return tag.getLyrics();
		}
		return "";
	}

	/**
	 * Return tag comment
	 * 
	 * @return
	 */
	@Override
	public String getComment() {
		if (tag != null && tag.getComment() != null) {
			return tag.getComment();
		}
		return "";
	}

	/**
	 * Gets the name without extension.
	 * 
	 * @return the name without extension
	 */
	@Override
	public String getNameWithoutExtension() {
		if (filePath == null) {
			return null;
		}
		return getFile().getName();
	}

	/**
	 * Gets the stars.
	 * 
	 * @return the stars
	 */
	@Override
	public int getStars() {
		return stars;
	}

	/**
	 * Gets the tag.
	 * 
	 * @return the tag
	 */
	@Override
	public ITag getTag() {
		return tag;
	}

	@Override
	public String getTitle() {
		if (tag != null && tag.getTitle() != null) {
			return tag.getTitle();
		}
		return "";
	}

	@Override
	public String getTitleOrFileName() {
		if (tag != null && tag.getTitle() != null && !tag.getTitle().isEmpty()) {
			return tag.getTitle();
		}
		return getNameWithoutExtension();
	}

	@Override
	public int getTrackNumber() {
		if (tag != null) {
			return tag.getTrackNumber();
		}
		return 0;
	}

	@Override
	public String getUrl() {
		return filePath;
	}

	@Override
	public String getYear() {
		if (tag != null && tag.getYear() > 0) {
			return Integer.toString(tag.getYear());
		}
		return "";
	}

	@Override
	public BaseDateTime getDate() {
		if (tag != null) {
			return tag.getDate();
		}
		return null;
	}

	@Override
	public int hashCode() {
		return getUrl().hashCode();
	}

	/**
	 * Checks for internal picture.
	 * 
	 * @return true, if successful
	 */
	@Override
	public final boolean hasInternalPicture() {
		return tag != null && tag.hasInternalImage();
	}

	/**
	 * Checks if is up to date.
	 * 
	 * @return true, if is up to date
	 */
	@Override
	public boolean isUpToDate() {
		if (filePath == null) {
			return false;
		}
		return readTime > getFile().lastModified();
	}

	/**
	 * Sets the file of this audio file
	 * 
	 * @param file
	 *            the file of this audio file
	 * @throws IOException
	 */
	@Override
	public void setFile(final File file) {
		if (file == null) {
			throw new IllegalArgumentException();
		}
		this.filePath = file.getAbsolutePath();
	}

	/**
	 * Sets the stars.
	 * 
	 * @param stars
	 *            the stars to set
	 */
	@Override
	public void setStars(final int stars) {
		this.stars = stars;
	}

	/**
	 * Sets the tag.
	 * 
	 * @param tag
	 *            the new tag
	 */
	@Override
	public void setTag(final ITag tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return filePath;
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	@Override
	public int compareTo(final ILocalAudioObject o) {
		if (getFile() == null || o.getFile() == null) {
			return 0;
		}
		return getFile().compareTo(o.getFile());
	}

	@Override
	public int getDiscNumber() {
		if (tag != null && tag.getDiscNumber() >= 1) {
			return tag.getDiscNumber();
		}
		return 0;
	}

	/**
	 * Sets duration
	 * @param duration
	 */
	@Override
	public void setDuration(final int duration) {
		this.duration = duration;
	}

	/**
	 * @param bitrate the bitrate to set
	 */
	@Override
	public void setBitrate(final long bitrate) {
		this.bitrate = bitrate;
	}

	/**
	 * @param frequency the frequency to set
	 */
	@Override
	public void setFrequency(final int frequency) {
		this.frequency = frequency;
	}

	@Override
	public String getAudioObjectDescription() {
		return StringUtils.getString(getTitleOrFileName(), " - ", getArtist(), " (", TimeUtils.secondsToHoursMinutesSeconds(getDuration()), ")");
	}

	/**
	 * Updates time when object if read
	 * @param readTime
	 */
	@Override
	public void setReadTime(final long readTime) {
		this.readTime = readTime;
	}

	@Override
	public long getSize() {
		if (fileSize == 0) {
			fileSize = getFile() != null ? getFile().length() : 0;
		}
		return fileSize;
	}
}
