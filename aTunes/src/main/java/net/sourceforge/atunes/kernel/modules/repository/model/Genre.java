/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.repository.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a genre, with a name, and a set of artist of this
 * genre.
 */
public class Genre implements Serializable, TreeObject {

    private static final long serialVersionUID = -6552057266561177152L;

    /** Name of the genre. */
    private String name;

    /** Artists of this genre, indexed by artist name. */
    private Map<String, Artist> artists;

    /**
     * Constructor.
     * 
     * @param name
     *            the name
     */
    public Genre(String name) {
        this.name = name;
        artists = new HashMap<String, Artist>();
    }

    /**
     * Adds an artist to this genre.
     * 
     * @param a
     *            the a
     */
    public void addArtist(Artist a) {
        artists.put(a.getName(), a);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Genre)) {
            return false;
        }
        return ((Genre) o).name.equals(name);
    }

    /**
     * Returns an Artist for a given artist name.
     * 
     * @param a
     *            the a
     * 
     * @return the artist
     */
    public Artist getArtist(String a) {
        return artists.get(a);
    }

    /**
     * Returns artists of this genre.
     * 
     * @return the artists
     */
    public Map<String, Artist> getArtists() {
        return artists;
    }

    /**
     * Gets the audio files.
     * 
     * @return the audio files
     */
    public List<AudioFile> getAudioFiles() {
        List<AudioFile> songs = new ArrayList<AudioFile>();
        for (Artist art : artists.values()) {
            songs.addAll(art.getAudioFiles());
        }
        return songs;
    }

    /**
     * Returns all songs of this genre (all songs of all artists).
     * 
     * @return the audio objects
     */
    @Override
    public List<AudioObject> getAudioObjects() {
        List<AudioObject> songs = new ArrayList<AudioObject>();
        for (Artist art : artists.values()) {
            songs.addAll(art.getAudioObjects());
        }
        return songs;
    }

    /**
     * Returns the name of this genre.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Removes an artist from this genre.
     * 
     * @param a
     *            the a
     */
    public void removeArtist(Artist a) {
        artists.remove(a.getName());
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
    public String getToolTip() {
        int artistNumber = getArtists().size();
        int songs = getAudioObjects().size();
        return StringUtils.getString(getName(), " (", LanguageTool.getString("ARTISTS"), ": ", artistNumber, ", ", LanguageTool.getString("SONGS"), ": ", songs, ")");
    }

    @Override
    public boolean isExtendedToolTipSupported() {
        return true;
    }

    @Override
    public void setExtendedToolTip(ExtendedToolTip toolTip) {
        toolTip.setLine1(name);
        int artistNumber = artists.size();
        toolTip.setLine2(StringUtils.getString(artistNumber, " ", (artistNumber > 1 ? LanguageTool.getString("ARTISTS") : LanguageTool.getString("ARTIST"))));
        int songs = getAudioObjects().size();
        toolTip.setLine3(StringUtils.getString(songs, " ", (songs > 1 ? LanguageTool.getString("SONGS") : LanguageTool.getString("SONG"))));
    }

    @Override
    public ImageIcon getExtendedToolTipImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isExtendedToolTipImageSupported() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Return unknown genre text
     * 
     * @return
     */
    public static String getUnknownGenre() {
        return LanguageTool.getString("UNKNOWN_GENRE");
    }

    /**
     * Return <code>true</code> if this genre is unknown
     * 
     * @return
     */
    public boolean isUnknownGenre() {
        return getName().equalsIgnoreCase(getUnknownGenre());
    }

    /**
     * Return <code>true</code> if this genre is unknown
     * 
     * @return
     */
    public static boolean isUnknownGenre(String genre) {
        return getUnknownGenre().equalsIgnoreCase(genre);
    }

}
