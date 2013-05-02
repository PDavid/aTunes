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

import org.joda.time.base.BaseDateTime;

/**
 * Interface for all audio objects (e.g. AudioFile, Radio, PodcastFeedEntry)
 */
public interface IAudioObject {

	/**
	 * Gets the album.
	 * 
	 * @param checker
	 * @return the album or null or unknown album if checker is not null
	 */
	public String getAlbum(final IUnknownObjectChecker checker);

	/**
	 * Gets the album artist.
	 * 
	 * @param checker
	 * @return the album artist or null or unknown artist text if checker is not
	 *         null
	 */
	public String getAlbumArtist(IUnknownObjectChecker checker);

	/**
	 * Returns album artist or artist
	 * 
	 * @param unknownObjectChecker
	 * @return the artist or (if empty) the album artist or null or unknown
	 *         artist text if checker is not null
	 */
	public String getAlbumArtistOrArtist(
			IUnknownObjectChecker unknownObjectChecker);

	/**
	 * Gets the artist.
	 * 
	 * @param checker
	 * @return the artist or null or unknown artist if checker is not null
	 */
	public String getArtist(final IUnknownObjectChecker checker);

	/**
	 * Gets the bitrate.
	 * 
	 * @return the bitrate
	 */
	public long getBitrate();

	/**
	 * Gets the composer.
	 * 
	 * @return the composer
	 */
	public String getComposer();

	/**
	 * Gets the duration.
	 * 
	 * @return the duration
	 */
	public int getDuration();

	/**
	 * Gets the frequency.
	 * 
	 * @return the frequency
	 */
	public int getFrequency();

	/**
	 * Gets the genre.
	 * 
	 * @param checker
	 * @return the genre or null or unknown genre text if checker is not null
	 */
	public String getGenre(IUnknownObjectChecker checker);

	/**
	 * Gets the year.
	 * 
	 * @param checker
	 * @return the year or null or unknown year text if checker is not null
	 */
	public String getYear(IUnknownObjectChecker checker);

	/**
	 * Gets the lyrics.
	 * 
	 * @return the lyrics
	 */
	public String getLyrics();

	/**
	 * Gets the stars.
	 * 
	 * @return the stars
	 */
	public int getStars();

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle();

	/**
	 * Gets the title or file name.
	 * 
	 * @return the title or file name
	 */
	public String getTitleOrFileName();

	/**
	 * Gets the track number.
	 * 
	 * @return the track number
	 */
	public int getTrackNumber();

	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
	public String getUrl();

	/**
	 * Gets the date.
	 * 
	 * @return the date
	 */
	public BaseDateTime getDate();

	/**
	 * Gets the comment
	 * 
	 * @return the comment
	 */
	public String getComment();

	/**
	 * Checks if is seekable.
	 * 
	 * @return true, if is seekable
	 */
	public boolean isSeekable();

	/**
	 * Gets the disc number
	 * 
	 * @return
	 */
	public int getDiscNumber();

	/**
	 * A human-readable text describing this object
	 * 
	 * @param unknownObjectChecker
	 * @return
	 */
	public String getAudioObjectDescription(
			IUnknownObjectChecker unknownObjectChecker);
}
