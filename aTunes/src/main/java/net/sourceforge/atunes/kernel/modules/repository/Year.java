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

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IYear;

/**
 * This class represents a year, with a set of artist of this year.
 */
public class Year implements IYear {

	private static final long serialVersionUID = -8560986690062265343L;

	/** Year value. */
	String year;

	/** List of songs of this year. */
	List<ILocalAudioObject> audioFiles;

	/**
	 * Default constructor for serialization
	 */
	Year() {
	}

	/**
	 * Constructor.
	 * 
	 * @param year
	 */
	public Year(final String year) {
		this.year = year;
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
		audioFiles.add(a);
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Year)) {
			return false;
		}
		return ((Year) o).year.equals(year);
	}

	/**
	 * Returns all songs of this year (all songs of all artists) from the given
	 * repository
	 * 
	 * @return the audio objects
	 */
	@Override
	public List<ILocalAudioObject> getAudioObjects() {
		return new ArrayList<ILocalAudioObject>(audioFiles);
	}

	@Override
	public String getName(final IUnknownObjectChecker unknownObjectChecker) {
		if (year.isEmpty()) {
			return unknownObjectChecker.getUnknownYear();
		}
		return year;
	}

	@Override
	public int hashCode() {
		return year.hashCode();
	}

	/**
	 * Removes an artist from this year.
	 * 
	 * @param a
	 *            the a
	 */
	@Override
	public void removeAudioObject(final ILocalAudioObject a) {
		audioFiles.remove(a);
	}

	@Override
	public String toString() {
		return year;
	}

	@Override
	public int size() {
		return audioFiles.size();
	}

	@Override
	public int compareTo(final IYear o) {
		try {
			return Integer.valueOf(this.getName(null)).compareTo(
					Integer.valueOf(o.getName(null)));
		} catch (NumberFormatException e) {
			return this.getName(null).compareTo(o.getName(null));
		}
	}
}
