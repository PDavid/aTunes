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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a genre, with a name, and a set of artist of this
 * genre.
 */
public class Genre implements Serializable, ITreeObject<ILocalAudioObject> {

    private static final long serialVersionUID = -6552057266561177152L;

    /** Name of the genre. */
    private String name;

    /** List of songs of this genre. */
    private List<ILocalAudioObject> audioFiles;

    /**
     * Constructor.
     * 
     * @param name
     *            the name
     */
    public Genre(String name) {
        this.name = name;
        audioFiles = new ArrayList<ILocalAudioObject>();
    }

    /**
     * Adds an audio file
     * 
     * @param a
     *            the a
     */
    public void addAudioFile(ILocalAudioObject a) {
        audioFiles.add(a);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Genre)) {
            return false;
        }
        return ((Genre) o).name.toLowerCase().equals(name.toLowerCase());
    }

    /**
     * Returns all songs of this genre (all songs of all artists) from the given
     * repository
     * 
     * @return the audio objects
     */
    @Override
    public List<ILocalAudioObject> getAudioObjects() {
        List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
        for (ILocalAudioObject song : audioFiles) {
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
    public void removeAudioFile(ILocalAudioObject a) {
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
        int songs = size();
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
        int songs = size();
        toolTip.setLine3(StringUtils.getString(songs, " ", (songs > 1 ? I18nUtils.getString("SONGS") : I18nUtils.getString("SONG"))));
    }

    @Override
    public ImageIcon getExtendedToolTipImage(IOSManager osManager) {
        return null;
    }

    @Override
    public boolean isExtendedToolTipImageSupported() {
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
    	return new ArtistStructureBuilder().getArtistObjects(audioFiles);
    }

    /**
     * Returns all artists of this genre
     */
    public Set<String> getArtistSet() {
    	return new ArtistStructureBuilder().getArtistSet(audioFiles);
    }

    @Override
    public int size() {
    	return audioFiles.size();
    }
}
