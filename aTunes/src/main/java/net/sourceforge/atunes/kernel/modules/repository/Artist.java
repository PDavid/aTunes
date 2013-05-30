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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.ILocalAudioObject;

/**
 * This class represents an artist, with a name, and a list of albums.
 * 
 * @author fleax
 */

public class Artist implements IArtist {

    /**
	 * 
	 */
    private static final long serialVersionUID = 2528917861599275619L;

    /** Name of the artist. */
    String name;

    /** List of IAlbum objects, indexed by name. */
    Map<String, IAlbum> albums;

    /**
     * Default constructor for serialization
     */
    Artist() {
    }

    /**
     * Constructor.
     * 
     * @param name
     *            the name
     */
    public Artist(final String name) {
	this.name = name;
    }

    /**
     * Adds an album to this artist.
     * 
     * @param album
     *            the album
     */
    @Override
    public void addAlbum(final IAlbum album) {
	getAlbums().put(album.getName(), album);
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
    public int compareTo(final IArtist o) {
	return this.name.compareToIgnoreCase(o.getName());
    }

    @Override
    public boolean equals(final Object o) {
	if (!(o instanceof Artist)) {
	    return false;
	}
	return ((Artist) o).name.equalsIgnoreCase(name);
    }

    /**
     * Return an IAlbum for a given album name.
     * 
     * @param albumName
     *            the album name
     * 
     * @return the album
     */
    @Override
    public IAlbum getAlbum(final String albumName) {
	return getAlbums().get(albumName);
    }

    /**
     * Return albums of this artist.
     * 
     * @return the albums
     */
    @Override
    public Map<String, IAlbum> getAlbums() {
	if (albums == null) {
	    // Use ConcurrentHashMap as read can happen while updating albums
	    albums = new ConcurrentHashMap<String, IAlbum>();
	}
	return albums;
    }

    /**
     * Returns a list of songs of this artist (all songs of all albums).
     * 
     * @return the audio objects
     */
    @Override
    public List<ILocalAudioObject> getAudioObjects() {
	List<ILocalAudioObject> songs = new ArrayList<ILocalAudioObject>();
	for (IAlbum album : getAlbums().values()) {
	    songs.addAll(album.getAudioObjects());
	}
	return songs;
    }

    /**
     * Returns the name of this artist.
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
     * Removes an album from this artist.
     * 
     * @param alb
     *            the alb
     */
    @Override
    public void removeAlbum(final IAlbum alb) {
	getAlbums().remove(alb.getName());
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

    /**
     * Returns true if artist has no audio files
     * 
     * @return
     */
    @Override
    public boolean isEmpty() {
	if (!getAlbums().isEmpty()) {
	    for (IAlbum a : getAlbums().values()) {
		if (!a.isEmpty()) {
		    return false;
		}
	    }
	}
	return true;
    }

    /**
     * Returns number of audio files of artist
     * 
     * @return
     */
    @Override
    public int size() {
	int size = 0;
	if (!getAlbums().isEmpty()) {
	    for (IAlbum a : getAlbums().values()) {
		size = size + a.size();
	    }
	}
	return size;
    }
}
