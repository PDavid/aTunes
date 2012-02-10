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

package net.sourceforge.atunes.kernel.modules.repository.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents an album, with it's name, artist, and songs.
 * 
 * @author fleax
 */
public class Album implements IAlbum {

    private static final long serialVersionUID = -1481314950918557022L;

    /** Name of the album. */
    private String name;

    /** Name of the artist. */
    private IArtist artist;

    /** List of songs of this album. */
    private Set<ILocalAudioObject> audioFiles;

    /**
     * Constructor.
     * 
     * @param artist
     * @param name
     */
    public Album(IArtist artist, String name) {
        this.artist = artist;
        this.name = name;
    }
    
    /**
     * Returns audio files
     * @return
     */
    private Set<ILocalAudioObject> getAudioFiles() {
    	if (audioFiles == null) {
    		audioFiles = new TreeSet<ILocalAudioObject>(new TrackNumberComparator());
    	}
    	return audioFiles;
    }

    /**
     * Adds a song to this album.
     * 
     * @param file
     *            the file
     */
    @Override
	public void addAudioFile(ILocalAudioObject file) {
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
    public int compareTo(IAlbum o) {
    	if (o == null || name == null || artist == null) {
    		return 1;
    	} else {
    		int artistCompare = artist.compareTo(o.getArtist());
    		if (artistCompare == 0) {
    			return name.compareToIgnoreCase(o.getName());
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
        return artist;
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
        return name;
    }

    /**
     * Removes a song from this album.
     * 
     * @param file
     *            the file
     */
    @Override
	public void removeAudioFile(ILocalAudioObject file) {
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
    public String getToolTip() {
        int songs = size();
        return StringUtils.getString(getName(), " - ", getArtist(), " (", songs, " ", (songs > 1 ? I18nUtils.getString("SONGS") : I18nUtils.getString("SONG")), ")");
    }

    @Override
    public boolean isExtendedToolTipSupported() {
        return true;
    }

    @Override
    public boolean isExtendedToolTipImageSupported() {
        return true;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
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
		if (artist == null) {
			if (other.artist != null) {
				return false;
			}
		} else if (!artist.equals(other.artist)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}	
		} else if (!name.equalsIgnoreCase(other.name)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns true if album has no audio files
	 * @return
	 */
	@Override
	public boolean isEmpty() {
		return getAudioFiles().isEmpty();
	}
	
	/**
	 * Returns number of audio files of album
	 * @return
	 */
	public int size() {
		return getAudioFiles().size();
	}

}
