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

package net.sourceforge.atunes.kernel.modules.repository;

import java.io.File;
import java.io.Serializable;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.TimeUtils;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.base.BaseDateTime;

/**
 * AudioFile class initializes audio files so that tags and audio information
 * can be retrieved by the tagging library. Provides information about bitrate,
 * duration and frequency of the audio file. Provides tag information.
 * 
 * @author fleax
 */
public final class AudioFile implements ILocalAudioObject, Serializable {

	private static final long serialVersionUID = -1139001443603556703L;

	ITag tag;
	int duration;
	long bitrate;
	int frequency;
	long readTime;
	boolean variableBitrate;

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
	public String getAlbum(final IUnknownObjectChecker checker) {
		if (this.tag != null && this.tag.getAlbum() != null
				&& !this.tag.getAlbum().isEmpty()) {
			return this.tag.getAlbum();
		}
		return checker != null ? checker.getUnknownAlbum() : null;
	}

	@Override
	public String getAlbumArtist(final IUnknownObjectChecker checker) {
		if (this.tag != null && this.tag.getAlbumArtist() != null
				&& !this.tag.getAlbumArtist().isEmpty()) {
			return this.tag.getAlbumArtist();
		}
		return checker != null ? checker.getUnknownArtist() : null;
	}

	@Override
	public String getAlbumArtistOrArtist(final IUnknownObjectChecker checker) {
		return getAlbumArtist(checker).isEmpty()
				|| getAlbumArtist(checker).equals(checker.getUnknownArtist()) ? getArtist(checker)
				: getAlbumArtist(checker);
	}

	@Override
	public String getArtist(final IUnknownObjectChecker checker) {
		if (this.tag != null && this.tag.getArtist() != null
				&& !this.tag.getArtist().isEmpty()) {
			return this.tag.getArtist();
		}
		return checker != null ? checker.getUnknownArtist() : null;
	}

	@Override
	public long getBitrate() {
		return this.bitrate;
	}

	@Override
	public String getComposer() {
		if (this.tag != null && this.tag.getComposer() != null) {
			return this.tag.getComposer();
		}
		return "";
	}

	@Override
	public int getDuration() {
		return this.duration;
	}

	/**
	 * Return the file on disk
	 * 
	 * @return the file on disk
	 */
	private File getFile() {
		return this.filePath != null ? new File(this.filePath) : null;
	}

	@Override
	public int getFrequency() {
		return this.frequency;
	}

	@Override
	public String getGenre(final IUnknownObjectChecker checker) {
		if (this.tag != null && this.tag.getGenre() != null
				&& !this.tag.getGenre().isEmpty()) {
			return this.tag.getGenre();
		}
		return checker != null ? checker.getUnknownGenre() : null;
	}

	@Override
	public String getLyrics() {
		if (this.tag != null && this.tag.getLyrics() != null) {
			return this.tag.getLyrics();
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
		if (this.tag != null && this.tag.getComment() != null) {
			return this.tag.getComment();
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
		return FilenameUtils.getBaseName(this.filePath);
	}

	/**
	 * Gets the stars.
	 * 
	 * @return the stars
	 */
	@Override
	public int getStars() {
		return this.tag != null ? this.tag.getStars() : 0;
	}

	/**
	 * Gets the tag.
	 * 
	 * @return the tag
	 */
	@Override
	public ITag getTag() {
		return this.tag;
	}

	@Override
	public String getTitle() {
		if (this.tag != null && this.tag.getTitle() != null) {
			return this.tag.getTitle();
		}
		return "";
	}

	@Override
	public String getTitleOrFileName() {
		if (this.tag != null && this.tag.getTitle() != null
				&& !this.tag.getTitle().isEmpty()) {
			return this.tag.getTitle();
		}
		return getNameWithoutExtension();
	}

	@Override
	public int getTrackNumber() {
		if (this.tag != null) {
			return this.tag.getTrackNumber();
		}
		return 0;
	}

	@Override
	public String getUrl() {
		return this.filePath;
	}

	@Override
	public String getYear(final IUnknownObjectChecker checker) {
		if (this.tag != null && this.tag.getYear() > 0) {
			return Integer.toString(this.tag.getYear());
		}
		return checker != null ? checker.getUnknownYear() : null;
	}

	@Override
	public BaseDateTime getDate() {
		if (this.tag != null) {
			return this.tag.getDate();
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
		return this.tag != null && this.tag.hasInternalImage();
	}

	@Override
	public long getReadTime() {
		return this.readTime;
	}

	/**
	 * Sets the path of this audio file
	 * 
	 * @param path
	 *            the path of this audio file
	 */
	void setFile(final String path) {
		if (path == null) {
			throw new IllegalArgumentException("Null path");
		}
		this.filePath = path;
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
		return this.filePath;
	}

	@Override
	public boolean isSeekable() {
		return true;
	}

	@Override
	public int compareTo(final ILocalAudioObject o) {
		return getUrl().compareTo(o.getUrl());
	}

	@Override
	public int getDiscNumber() {
		if (this.tag != null && this.tag.getDiscNumber() >= 1) {
			return this.tag.getDiscNumber();
		}
		return 0;
	}

	/**
	 * Sets duration
	 * 
	 * @param duration
	 */
	@Override
	public void setDuration(final int duration) {
		this.duration = duration;
	}

	/**
	 * @param bitrate
	 *            the bitrate to set
	 */
	@Override
	public void setBitrate(final long bitrate) {
		this.bitrate = bitrate;
	}

	/**
	 * @param frequency
	 *            the frequency to set
	 */
	@Override
	public void setFrequency(final int frequency) {
		this.frequency = frequency;
	}

	@Override
	public String getAudioObjectDescription(
			final IUnknownObjectChecker unknownObjectChecker) {
		return StringUtils.getString(getTitleOrFileName(), " - ",
				getArtist(unknownObjectChecker), " (",
				TimeUtils.secondsToHoursMinutesSeconds(getDuration()), ")");
	}

	/**
	 * Updates time when object if read
	 * 
	 * @param readTime
	 */
	@Override
	public void setReadTime(final long readTime) {
		this.readTime = readTime;
	}

	@Override
	public long getSize() {
		if (this.fileSize == 0) {
			this.fileSize = getFile() != null ? getFile().length() : 0;
		}
		return this.fileSize;
	}

	@Override
	public boolean isVariableBitrate() {
		return this.variableBitrate;
	}

	@Override
	public void setVariableBitrate(final boolean variable) {
		this.variableBitrate = variable;
	}
}
