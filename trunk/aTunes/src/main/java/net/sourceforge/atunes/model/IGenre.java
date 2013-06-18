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
 * 
 * This class represents a genre, with a name, and a set of artist of this
 * genre.
 * 
 * @author alex
 * 
 */
public interface IGenre extends ITreeObject<ILocalAudioObject>, Serializable,
		Comparable<IGenre> {

	/**
	 * Adds an audio file
	 * 
	 * @param a
	 *            the a
	 */
	public void addAudioObject(ILocalAudioObject a);

	/**
	 * Returns the name of this genre.
	 * 
	 * @return the name
	 */
	public String getName();

	/**
	 * Removes an object from this genre.
	 * 
	 * @param a
	 *            the a
	 */
	public void removeAudioObject(ILocalAudioObject a);
}