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

/**
 * Handles tag read and write
 * 
 * @author alex
 * 
 */
public interface ITagAdapter {

	/**
	 * Deletes tags
	 * 
	 * @param file
	 */
	void deleteTags(ILocalAudioObject file);

	/**
	 * Writes tag to file
	 * 
	 * @param file
	 * @param shouldEditCover
	 * @param cover
	 * @param title
	 * @param album
	 * @param artist
	 * @param year
	 * @param comment
	 * @param genre
	 * @param lyrics
	 * @param composer
	 * @param track
	 * @param discNumber
	 * @param albumArtist
	 */
	void writeTag(ILocalAudioObject file, boolean shouldEditCover,
			byte[] cover, String title, String album, String artist, int year,
			String comment, String genre, String lyrics, String composer,
			int track, int discNumber, String albumArtist);

	/**
	 * Changes album
	 * 
	 * @param file
	 * @param album
	 */
	void modifyAlbum(ILocalAudioObject file, String album);

	/**
	 * Changes genre
	 * 
	 * @param file
	 * @param genre
	 */
	void modifyGenre(ILocalAudioObject file, String genre);

	/**
	 * Changes lyrics
	 * 
	 * @param file
	 * @param lyrics
	 */
	void modifyLyrics(ILocalAudioObject file, String lyrics);

	/**
	 * Changes title
	 * 
	 * @param file
	 * @param newTitle
	 */
	void modifyTitle(ILocalAudioObject file, String newTitle);

	/**
	 * Changes track
	 * 
	 * @param file
	 * @param track
	 */
	void modifyTrack(ILocalAudioObject file, Integer track);

	/**
	 * Changes stars
	 * 
	 * @param audioObject
	 * @param starsToRating
	 */
	void modifyStars(ILocalAudioObject audioObject, String starsToRating);

	/**
	 * Reads tag
	 * 
	 * @param ao
	 * @param readAudioProperties
	 */
	void readTag(ILocalAudioObject ao, boolean readAudioProperties);
}
