/*
 * aTunes 2.1.0-SNAPSHOT
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents an album, with it's name, artist, and songs.
 * 
 * @author fleax
 */
public class Album implements Serializable, TreeObject, Comparable<Album> {

    private static final long serialVersionUID = -1481314950918557022L;

    /** Name of the album. */
    private String name;

    /** Name of the artist. */
    private Artist artist;

    /** List of songs of this album. */
    private List<LocalAudioObject> audioFiles;

    /**
     * Constructor.
     * 
     * @param artist
     * @param name
     */
    public Album(Artist artist, String name) {
        this.artist = artist;
        this.name = name;
    }
    
    /**
     * Returns audio files
     * @return
     */
    private List<LocalAudioObject> getAudioFiles() {
    	if (audioFiles == null) {
    		audioFiles = new ArrayList<LocalAudioObject>();
    	}
    	return audioFiles;
    }

    /**
     * Adds a song to this album.
     * 
     * @param file
     *            the file
     */
    public void addAudioFile(LocalAudioObject file) {
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
    public int compareTo(Album o) {
    	if (o == null || name == null || artist == null) {
    		return 1;
    	} else {
    		int artistCompare = artist.compareTo(o.artist); 
    		if (artistCompare == 0) {
    			return name.compareTo(o.name);
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
    public Artist getArtist() {
        return artist;
    }

    /**
     * Returns a list of songs of this album.
     * 
     * @return the audio objects
     */
    @Override
    public List<AudioObject> getAudioObjects() {
        return new ArrayList<AudioObject>(getAudioFiles());
    }

    /**
     * Returns name of the album.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns name of the album and artist
     * 
     * @return
     */
    public String getNameAndArtist() {
        return StringUtils.getString(name, " (", artist, ")");
    }

    /**
     * Returns a picture of this album, with a given size.
     * 
     * @param width
     *            the width
     * @param heigth
     *            the heigth
     * 
     * @return the picture
     */
    public ImageIcon getPicture(ImageSize imageSize) {
        return getAudioFiles().get(0).getImage(imageSize);
    }

    
    /**
     * Removes a song from this album.
     * 
     * @param file
     *            the file
     */
    public void removeAudioFile(LocalAudioObject file) {
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
    public void setExtendedToolTip(ExtendedToolTip toolTip) {
        toolTip.setLine1(name);
        toolTip.setLine2(artist.getName());
        int songNumber = getAudioFiles().size();
        toolTip.setLine3(StringUtils.getString(songNumber, " ", (songNumber > 1 ? I18nUtils.getString("SONGS") : I18nUtils.getString("SONG"))));
    }

    @Override
    public ImageIcon getExtendedToolTipImage() {
        return getPicture(ImageSize.SIZE_MAX);
    }

    @Override
    public boolean isExtendedToolTipImageSupported() {
        return true;
    }

    /**
     * Return unknown album text
     * 
     * @return
     */
    public static String getUnknownAlbum() {
        return I18nUtils.getString("UNKNOWN_ALBUM");
    }

    /**
     * Return <code>true</code> if this album is unknown
     * 
     * @return
     */
    public static boolean isUnknownAlbum(String album) {
        return getUnknownAlbum().equalsIgnoreCase(album);
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Album other = (Album) obj;
		if (artist == null) {
			if (other.artist != null)
				return false;
		} else if (!artist.equals(other.artist))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	/**
	 * Returns true if album has no audio files
	 * @return
	 */
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
