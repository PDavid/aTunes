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
import java.util.Map;

/**
 * Represents an artist, with a name, and a list of albums.
 * @author alex
 *
 */
public interface IArtist extends Serializable, ITreeObject<ILocalAudioObject>, Comparable<IArtist> {

	/**
	 * Adds an album to this artist.
	 * 
	 * @param album
	 *            the album
	 */
	void addAlbum(IAlbum album);

	/**
	 * Return an IAlbum for a given album name.
	 * 
	 * @param albumName
	 *            the album name
	 * 
	 * @return the album
	 */
	IAlbum getAlbum(String albumName);

	/**
	 * Return albums of this artist.
	 * 
	 * @return the albums
	 */
	Map<String, IAlbum> getAlbums();

	/**
	 * Returns the name of this artist.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Removes an album from this artist.
	 * 
	 * @param alb
	 *            the alb
	 */
	void removeAlbum(IAlbum alb);

	/**
	 * Returns true if artist has no audio files
	 * @return
	 */
	boolean isEmpty();

}