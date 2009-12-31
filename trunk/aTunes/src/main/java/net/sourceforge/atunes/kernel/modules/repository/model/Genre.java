/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a genre, with a name, and a set of artist of this
 * genre.
 */
public class Genre implements Serializable, TreeObject {

    private static final long serialVersionUID = -6552057266561177152L;

    /** Name of the genre. */
    private String name;

    /** List of songs of this genre. */
    private List<AudioFile> audioFiles;

    /**
     * Constructor.
     * 
     * @param name
     *            the name
     */
    public Genre(String name) {
        this.name = name;
        audioFiles = new ArrayList<AudioFile>();
    }

    /**
     * Adds an audio file
     * 
     * @param a
     *            the a
     */
    public void addAudioFile(AudioFile a) {
        audioFiles.add(a);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Genre)) {
            return false;
        }
        return ((Genre) o).name.equals(name);
    }

    /**
     * Returns all songs of this genre (all songs of all artists) from the given
     * repository
     * 
     * @return the audio objects
     */
    @Override
    public List<AudioObject> getAudioObjects() {
        List<AudioObject> result = new ArrayList<AudioObject>();
        for (AudioFile song : audioFiles) {
            result.add(song);
        }
        return result;
    }

    /**
     * Returns the name of this genre.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

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
    public void removeAudioFile(AudioFile a) {
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
    public String getToolTip() {
        int artistNumber = getArtistSet().size();
        int songs = getAudioObjects().size();
        return StringUtils.getString(getName(), " (", I18nUtils.getString("ARTISTS"), ": ", artistNumber, ", ", I18nUtils.getString("SONGS"), ": ", songs, ")");
    }

    @Override
    public boolean isExtendedToolTipSupported() {
        return true;
    }

    @Override
    public void setExtendedToolTip(ExtendedToolTip toolTip) {
        toolTip.setLine1(name);
        int artistNumber = getArtistSet().size();
        toolTip.setLine2(StringUtils.getString(artistNumber, " ", (artistNumber > 1 ? I18nUtils.getString("ARTISTS") : I18nUtils.getString("ARTIST"))));
        int songs = getAudioObjects().size();
        toolTip.setLine3(StringUtils.getString(songs, " ", (songs > 1 ? I18nUtils.getString("SONGS") : I18nUtils.getString("SONG"))));
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
        return I18nUtils.getString("UNKNOWN_GENRE");
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

    /**
     * Returns an structure of artists and albums containing songs of this genre
     * 
     * @return the artists
     */
    public Map<String, Artist> getArtistObjects() {
        Map<String, Artist> structure = new HashMap<String, Artist>();
        for (AudioFile song : audioFiles) {
            String artist = !song.getAlbumArtist().equals("") ? song.getAlbumArtist() : song.getArtist();
            if (!structure.containsKey(artist)) {
                structure.put(artist, new Artist(artist));
            }
            if (!structure.get(artist).getAlbums().containsKey(song.getAlbum())) {
                Album album = new Album(song.getAlbum());
                album.setArtist(structure.get(artist));
                structure.get(artist).addAlbum(album);
            }
            structure.get(artist).getAlbum(song.getAlbum()).addAudioFile(song);
        }
        return structure;
    }

    /**
     * Returns all artists of this genre
     */
    public Set<String> getArtistSet() {
        Set<String> artists = new HashSet<String>();
        for (AudioFile song : audioFiles) {
            if (!song.getAlbumArtist().equals("")) {
                artists.add(song.getAlbumArtist());
            } else {
                artists.add(song.getArtist());
            }
        }
        return artists;
    }

}
