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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.ILocalAudioObject;

/**
 * This class represents an album, with it's name, artist, and songs.
 * 
 * @author fleax
 */
public class Album implements IAlbum {

	private static final long serialVersionUID = -1481314950918557022L;

	/** Name of the album. */
	String name;

	/** Name of the artist. */
	IArtist artist;

	/** List of songs of this album. */
	Set<ILocalAudioObject> audioFiles;

	/** List of songs of this album. */
	private transient Set<ILocalAudioObject> synchronizedAudioFiles;

	private static TrackNumberComparator comparator = new TrackNumberComparator();

	/**
	 * Default constructor for serialization
	 */
	Album() {
	}

	/**
	 * Constructor.
	 * 
	 * @param artist
	 * @param name
	 */
	public Album(final IArtist artist, final String name) {
		this.artist = artist;
		this.name = name;
	}

	/**
	 * Returns audio files
	 * 
	 * @return
	 */
	private Set<ILocalAudioObject> getAudioFiles() {
		// Need to use a synchronized set to avoid concurrency problems
		// However as kryo serialization is tricky with synchronized collections
		// use a non-synchronized collection with kryo
		if (this.audioFiles == null) {
			TreeSet<ILocalAudioObject> treeSet = new TreeSet<ILocalAudioObject>(
					comparator);
			this.audioFiles = treeSet;
			this.synchronizedAudioFiles = Collections
					.synchronizedSortedSet(treeSet);
		} else if (this.synchronizedAudioFiles == null) {
			this.synchronizedAudioFiles = Collections
					.synchronizedSortedSet((TreeSet<ILocalAudioObject>) this.audioFiles);
		}
		return this.synchronizedAudioFiles;
	}

	/**
	 * Adds a song to this album.
	 * 
	 * @param file
	 *            the file
	 */
	@Override
	public void addAudioFile(final ILocalAudioObject file) {
		getAudioFiles().add(file);
	}

	/**
	 * Comparator.
	 * 
	 * @param o
	 *            the o
	 * 
	 * @return the int
	 */
	@Override
	public int compareTo(final IAlbum o) {
		if (o == null || this.name == null || this.artist == null) {
			return 1;
		} else {
			int artistCompare = this.artist.compareTo(o.getArtist());
			if (artistCompare == 0) {
				return this.name.compareToIgnoreCase(o.getName());
			} else {
				return artistCompare;
			}
		}
	}

	/**
	 * Returns the name of the artist of this album.
	 * 
	 * @return the artist
	 */
	@Override
	public IArtist getArtist() {
		return this.artist;
	}

	/**
	 * Returns a list of songs of this album.
	 * 
	 * @return the audio objects
	 */
	@Override
	public List<ILocalAudioObject> getAudioObjects() {
		return new ArrayList<ILocalAudioObject>(getAudioFiles());
	}

	/**
	 * Returns name of the album.
	 * 
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Removes a song from this album.
	 * 
	 * @param file
	 *            the file
	 */
	@Override
	public void removeAudioFile(final ILocalAudioObject file) {
		getAudioFiles().remove(file);
	}

	/**
	 * String representation of this object.
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.artist == null) ? 0 : this.artist.hashCode());
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Album other = (Album) obj;
		if (this.artist == null) {
			if (other.artist != null) {
				return false;
			}
		} else if (!this.artist.equals(other.artist)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equalsIgnoreCase(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns true if album has no audio files
	 * 
	 * @return
	 */
	@Override
	public boolean isEmpty() {
		return getAudioFiles().isEmpty();
	}

	/**
	 * Returns number of audio files of album
	 * 
	 * @return
	 */
	@Override
	public int size() {
		return getAudioFiles().size();
	}

}
