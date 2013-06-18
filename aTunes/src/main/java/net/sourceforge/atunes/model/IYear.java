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
 * Represents a year with audio objects
 * 
 * @author alex
 * 
 */
public interface IYear extends Serializable, ITreeObject<ILocalAudioObject>,
		Comparable<IYear> {

	/**
	 * Adds an audio file
	 * 
	 * @param a
	 *            the a
	 */
	public void addAudioObject(ILocalAudioObject a);

	/**
	 * Returns the year as a string.
	 * 
	 * @param unknownObjectChecker
	 * @return the year
	 */
	public String getName(IUnknownObjectChecker unknownObjectChecker);

	/**
	 * Removes an artist from this year.
	 * 
	 * @param a
	 *            the a
	 */
	public void removeAudioObject(ILocalAudioObject a);

}