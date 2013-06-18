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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.ILocalAudioObject;

/**
 * This class represents a genre, with a name, and a set of artist of this
 * genre.
 */
public class Genre implements IGenre {

	private static final long serialVersionUID = -6552057266561177152L;

	/** Name of the genre. */
	String name;

	/** List of objects of this genre. */
	List<ILocalAudioObject> audioFiles;

	/**
	 * Default constructor for serialization
	 */
	Genre() {
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the name
	 */
	public Genre(final String name) {
		this.name = name;
		this.audioFiles = new ArrayList<ILocalAudioObject>();
	}

	/**
	 * Adds an audio file
	 * 
	 * @param a
	 *            the a
	 */
	@Override
	public void addAudioObject(final ILocalAudioObject a) {
		this.audioFiles.add(a);
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Genre)) {
			return false;
		}
		return ((Genre) o).name.equalsIgnoreCase(name);
	}

	/**
	 * Returns all songs of this genre (all songs of all artists) from the given
	 * repository
	 * 
	 * @return the audio objects
	 */
	@Override
	public List<ILocalAudioObject> getAudioObjects() {
		return new ArrayList<ILocalAudioObject>(audioFiles);
	}

	/**
	 * Returns the name of this genre.
	 * 
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/**
	 * Removes an object from this genre.
	 * 
	 * @param a
	 *            the a
	 */
	@Override
	public void removeAudioObject(final ILocalAudioObject a) {
		audioFiles.remove(a);
	}

	/**
	 * String representation.
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int size() {
		return audioFiles.size();
	}

	@Override
	public int compareTo(final IGenre o) {
		return this.name.compareToIgnoreCase(o.getName());
	}
}
