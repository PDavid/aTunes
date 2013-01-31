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

package net.sourceforge.atunes.model;

import java.io.Serializable;

import org.joda.time.base.BaseDateTime;

/**
 * Represents the metadata information of an audio file
 * 
 * @author alex
 * 
 */
public interface ITag extends Serializable {

	/**
	 * Gets the album.
	 * 
	 * @return the album
	 */
	public String getAlbum();

	/**
	 * Gets the album artist.
	 * 
	 * @return the album artist
	 */
	public String getAlbumArtist();

	/**
	 * Gets the artist.
	 * 
	 * @return the artist
	 */
	public String getArtist();

	/**
	 * Gets the comment.
	 * 
	 * @return the comment
	 */
	public String getComment();

	/**
	 * Gets the composer.
	 * 
	 * @return the composer
	 */
	public String getComposer();

	/**
	 * Gets the genre.
	 * 
	 * @return the genre
	 */
	public String getGenre();

	/**
	 * Gets the lyrics.
	 * 
	 * @return the lyrics
	 */
	public String getLyrics();

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle();

	/**
	 * Gets the track number.
	 * 
	 * @return the track number
	 */
	public int getTrackNumber();

	/**
	 * Gets the year.
	 * 
	 * @return the year
	 */
	public int getYear();

	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	public BaseDateTime getDate();

	/**
	 * Sets the album.
	 * 
	 * @param album
	 *            the new album
	 */
	public void setAlbum(String album);

	/**
	 * Sets the album artist.
	 * 
	 * @param albumArtist
	 *            the new album artist
	 */
	public void setAlbumArtist(String albumArtist);

	/**
	 * Sets the artist.
	 * 
	 * @param artist
	 *            the new artist
	 */
	public void setArtist(String artist);

	/**
	 * Sets the comment.
	 * 
	 * @param comment
	 *            the new comment
	 */
	public void setComment(String comment);

	/**
	 * Sets the composer.
	 * 
	 * @param composer
	 *            the new composer
	 */
	public void setComposer(String composer);

	/**
	 * Sets the genre.
	 * 
	 * @param genre
	 *            the new genre
	 */
	public void setGenre(String genre);

	/**
	 * Sets the lyrics.
	 * 
	 * @param lyrics
	 *            the new lyrics
	 */
	public void setLyrics(String lyrics);

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the new title
	 */
	public void setTitle(String title);

	/**
	 * Sets the track number.
	 * 
	 * @param tracknumber
	 *            the new track number
	 */
	public void setTrackNumber(int tracknumber);

	/**
	 * Sets the year.
	 * 
	 * @param year
	 *            the new year
	 */
	public void setYear(int year);

	/**
	 * Sets the date.
	 * 
	 * @param date
	 *            the new date
	 */
	public void setDate(BaseDateTime date);

	/**
	 * Returns true if this tag has an internal image, false otherwise
	 * 
	 * @return true if this tag has an internal image, false otherwise
	 */
	public boolean hasInternalImage();

	/**
	 * Sets if this tag has an internal image
	 * 
	 * @param internalImage
	 *            if this tag has an internal image
	 */
	public void setInternalImage(boolean internalImage);

	/**
	 * @return the discNumber
	 */
	public int getDiscNumber();

	/**
	 * @param discNumber
	 *            the discNumber to set
	 */
	public void setDiscNumber(int discNumber);

	/**
	 * @return rating of object
	 */
	public int getStars();

	/**
	 * @param stars
	 */
	public void setStars(int stars);

}