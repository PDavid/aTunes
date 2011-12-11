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

package net.sourceforge.atunes.model;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;


public interface IYear extends Serializable, ITreeObject<ILocalAudioObject> {

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
	 * @return the year
	 */
	public String getName();

	/**
	 * Removes an artist from this year.
	 * 
	 * @param a
	 *            the a
	 */
	public void removeAudioObject(ILocalAudioObject a);

	/**
	 * Returns an structure of artists and albums containing songs of this year
	 * 
	 * @return the artists
	 */
	public Map<String, IArtist> getArtistObjects();

	/**
	 * Returns all artists of this year
	 */
	public Set<String> getArtistSet();

}