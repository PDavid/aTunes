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
import java.util.Map;
import java.util.Set;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

/**
 * This class represents a year, with a set of artist of this year.
 */
public class Year implements IYear {

    private static final long serialVersionUID = -8560986690062265343L;

    /** Year value. */
    private String year;

    /** List of songs of this year. */
    private List<ILocalAudioObject> audioFiles;

    /**
     * Constructor.
     * 
     * @param name
     *            the name
     */
    public Year(String year) {
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
	public void addAudioObject(ILocalAudioObject a) {
        audioFiles.add(a);
    }

    @Override
    public boolean equals(Object o) {
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

    /**
     * Returns the year as a string.
     * 
     * @return the year
     */
    @Override
	public String getName() {
        if (year.isEmpty()) {
            return UnknownObjectCheck.getUnknownYear();
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
	public void removeAudioObject(ILocalAudioObject a) {
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
    public boolean isExtendedToolTipImageSupported() {
        return false;
    }

    /**
     * Returns an structure of artists and albums containing songs of this year
     * 
     * @return the artists
     */
    @Override
	public Map<String, IArtist> getArtistObjects() {
    	return new ArtistStructureBuilder().getArtistObjects(audioFiles);
    }

    /**
     * Returns all artists of this year
     */
    @Override
	public Set<String> getArtistSet() {
    	return new ArtistStructureBuilder().getArtistSet(audioFiles);
    }
    
    @Override
    public int size() {
    	return audioFiles.size();
    }

}
