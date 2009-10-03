/*
 * aTunes 2.0.0-SNAPSHOT
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

import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents an artist, with a name, and a list of albums.
 * 
 * @author fleax
 */
public class Artist implements Serializable, TreeObject, Comparable<Artist> {

    private static final long serialVersionUID = -7981636660798555640L;

    /** Name of the artist. */
    private String name;

    /** List of Album objects, indexed by name. */
    private Map<String, Album> albums;

    /**
     * Constructor.
     * 
     * @param name
     *            the name
     */
    public Artist(String name) {
        this.name = name;
        albums = new HashMap<String, Album>();
    }

    /**
     * Adds an album to this artist.
     * 
     * @param album
     *            the album
     */
    public void addAlbum(Album album) {
        albums.put(album.getName(), album);
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
    public int compareTo(Artist o) {
        return this.name.compareTo(o.name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Artist)) {
            return false;
        }
        return ((Artist) o).name.equals(name);
    }

    /**
     * Return an Album for a given album name.
     * 
     * @param albumName
     *            the album name
     * 
     * @return the album
     */
    public Album getAlbum(String albumName) {
        return albums.get(albumName);
    }

    /**
     * Return albums of this artist.
     * 
     * @return the albums
     */
    public Map<String, Album> getAlbums() {
        return albums;
    }

    /**
     * Gets the audio files.
     * 
     * @return the audio files
     */
    public List<AudioFile> getAudioFiles() {
        List<AudioFile> songs = new ArrayList<AudioFile>();
        for (Album album : albums.values()) {
            songs.addAll(album.getAudioFiles());
        }
        return songs;
    }

    /**
     * Returns a list of songs of this artist (all songs of all albums).
     * 
     * @return the audio objects
     */
    @Override
    public List<AudioObject> getAudioObjects() {
        List<AudioObject> songs = new ArrayList<AudioObject>();
        for (Album album : albums.values()) {
            songs.addAll(album.getAudioObjects());
        }
        return songs;
    }

    /**
     * Returns the name of this artist.
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
     * Removes an album from this artist.
     * 
     * @param alb
     *            the alb
     */
    public void removeAlbum(Album alb) {
        albums.remove(alb.getName());
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
        int albums = getAlbums().size();
        return StringUtils.getString(getName(), " (", albums, " ", (albums > 1 ? I18nUtils.getString("ALBUMS") : I18nUtils.getString("ALBUM")), ")");
    }

    @Override
    public boolean isExtendedToolTipSupported() {
        return true;
    }

    @Override
    public void setExtendedToolTip(ExtendedToolTip toolTip) {
        toolTip.setLine1(name);
        int albumNumber = albums.size();
        toolTip.setLine2(StringUtils.getString(albumNumber, " ", (albumNumber > 1 ? I18nUtils.getString("ALBUMS") : I18nUtils.getString("ALBUM"))));
        Integer timesPlayed = RepositoryHandler.getInstance().getArtistTimesPlayed(this);
        toolTip.setLine3(StringUtils.getString(I18nUtils.getString("TIMES_PLAYED"), ": ", timesPlayed));
    }

    @Override
    public ImageIcon getExtendedToolTipImage() {
        Image img = LastFmService.getInstance().getImage(name);
        if (img != null) {
            return new ImageIcon(img);
        }
        return null;
    }

    @Override
    public boolean isExtendedToolTipImageSupported() {
        return true;
    }

    /**
     * Return unknown artist text
     * 
     * @return
     */
    public static String getUnknownArtist() {
        return I18nUtils.getString("UNKNOWN_ARTIST");
    }

    /**
     * Return <code>true</code> if this artist is unknown
     * 
     * @return
     */
    public boolean isUnknownArtist() {
        return getName().equalsIgnoreCase(getUnknownArtist());
    }

    /**
     * Return <code>true</code> if this artist is unknown
     * 
     * @return
     */
    public static boolean isUnknownArtist(String artist) {
        return getUnknownArtist().equalsIgnoreCase(artist);
    }
}
