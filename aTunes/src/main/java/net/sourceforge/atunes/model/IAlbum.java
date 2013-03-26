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

/**
 * Represents an album, with it's name, artist, and songs.
 * @author alex
 *
 */
public interface IAlbum extends Serializable, ITreeObject<ILocalAudioObject>, Comparable<IAlbum> {

	/**
	 * Adds a song to this album.
	 * 
	 * @param file
	 *            the file
	 */
	void addAudioFile(ILocalAudioObject file);

	/**
	 * Returns the name of the artist of this album.
	 * 
	 * @return the artist
	 */
	IArtist getArtist();

	/**
	 * Returns name of the album.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Removes a song from this album.
	 * 
	 * @param file
	 *            the file
	 */
	void removeAudioFile(ILocalAudioObject file);

	/**
	 * Returns true if album has no audio files
	 * @return
	 */
	boolean isEmpty();

}