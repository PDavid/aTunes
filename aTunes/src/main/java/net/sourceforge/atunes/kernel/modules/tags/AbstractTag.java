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

package net.sourceforge.atunes.kernel.modules.tags;

import java.io.Serializable;

import net.sourceforge.atunes.model.ITag;

import org.joda.time.base.BaseDateTime;

/**
 * The abstract class for tags.
 * 
 * @author fleax
 */
public abstract class AbstractTag implements Serializable, ITag {

	private static final long serialVersionUID = -4044670497563446883L;

	/** The title. */
	String title;

	/** The artist. */
	String artist;

	/** The album. */
	String album;

	/** The year. */
	int year;

	/** The date. */
	BaseDateTime date;

	/** The comment. */
	String comment;

	/** The genre. */
	String genre;

	/** The lyrics. */
	String lyrics;

	/** The composer. */
	String composer;

	/** The album artist. */
	String albumArtist;

	/** The track number. */
	int trackNumber;

	/** The disc number */
	int discNumber;

	/**
	 * Rating
	 */
	int stars;

	/** If this tag has an internal image */
	boolean internalImage;

	@Override
	public String getAlbum() {
		return this.album;
	}

	@Override
	public String getAlbumArtist() {
		return this.albumArtist;
	}

	@Override
	public String getArtist() {
		return this.artist;
	}

	@Override
	public String getComment() {
		return this.comment;
	}

	@Override
	public String getComposer() {
		return this.composer;
	}

	@Override
	public String getGenre() {
		return this.genre;
	}

	@Override
	public String getLyrics() {
		return this.lyrics;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public int getTrackNumber() {
		return this.trackNumber >= 0 ? this.trackNumber : 0;
	}

	@Override
	public int getYear() {
		if (this.date != null) {
			return this.date.getYear();
		} else if (this.year >= 0) {
			return this.year;
		} else {
			return 0;
		}
	}

	@Override
	public BaseDateTime getDate() {
		return this.date;
	}

	@Override
	public void setAlbum(final String album) {
		this.album = album != null ? album.trim() : "";
	}

	@Override
	public void setAlbumArtist(final String albumArtist) {
		this.albumArtist = albumArtist != null ? albumArtist.trim() : "";
	}

	@Override
	public void setArtist(final String artist) {
		this.artist = artist != null ? artist.trim() : "";
	}

	@Override
	public void setComment(final String comment) {
		this.comment = comment != null ? comment.trim() : "";
	}

	@Override
	public void setComposer(final String composer) {
		this.composer = composer != null ? composer.trim() : "";
	}

	@Override
	public void setGenre(final String genre) {
		this.genre = genre != null ? genre.trim() : "";
	}

	@Override
	public void setLyrics(final String lyrics) {
		this.lyrics = lyrics != null ? lyrics : "";
	}

	@Override
	public void setTitle(final String title) {
		this.title = title != null ? title.trim() : "";
	}

	@Override
	public void setTrackNumber(final int tracknumber) {
		this.trackNumber = tracknumber;
	}

	@Override
	public void setYear(final int year) {
		// only update year if it is not derived from the date
		if (this.date == null) {
			this.year = year;
		}
	}

	@Override
	public void setDate(final BaseDateTime date) {
		this.date = date;
	}

	@Override
	public boolean hasInternalImage() {
		return this.internalImage;
	}

	@Override
	public void setInternalImage(final boolean internalImage) {
		this.internalImage = internalImage;
	}

	@Override
	public int getDiscNumber() {
		return this.discNumber >= 0 ? this.discNumber : 0;
	}

	@Override
	public void setDiscNumber(final int discNumber) {
		this.discNumber = discNumber;
	}

	/**
	 * @return the stars
	 */
	@Override
	public int getStars() {
		return this.stars;
	}

	/**
	 * @param stars
	 *            the stars to set
	 */
	@Override
	public void setStars(final int stars) {
		this.stars = stars;
	}
}
