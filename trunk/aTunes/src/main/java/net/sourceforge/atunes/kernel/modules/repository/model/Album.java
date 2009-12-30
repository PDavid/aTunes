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

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
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
    private List<AudioFile> audioFiles;

    /**
     * Constructor.
     * 
     * @param name
     *            the name
     */
    public Album(String name) {
        this.name = name;
        audioFiles = new ArrayList<AudioFile>();
    }

    /**
     * Adds a song to this album.
     * 
     * @param file
     *            the file
     */
    public void addSong(AudioFile file) {
        audioFiles.add(file);
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
        return getNameAndArtist().compareTo(o.getNameAndArtist());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Album)) {
            return false;
        }
        return ((Album) o).name.equals(name) && ((Album) o).artist.equals(artist);
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
     * Gets the audio files.
     * 
     * @return the audio files
     */
    public List<AudioFile> getAudioFiles() {
        return new ArrayList<AudioFile>(audioFiles);
    }

    /**
     * Returns a list of songs of this album.
     * 
     * @return the audio objects
     */
    @Override
    public List<AudioObject> getAudioObjects() {
        return new ArrayList<AudioObject>(audioFiles);
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
        return audioFiles.get(0).getImage(imageSize);
    }

    /**
     * Returns true if aTunes has saved cover image.
     * 
     * @return true, if checks for cover downloaded
     */
    public boolean hasCoverDownloaded() {
        return new File(AudioFilePictureUtils.getFileNameForCover(audioFiles.get(0))).exists();
    }

    @Override
    public int hashCode() {
        return StringUtils.getString(name, artist).hashCode();
    }

    /**
     * Removes a song from this album.
     * 
     * @param file
     *            the file
     */
    public void removeAudioFile(AudioFile file) {
        audioFiles.remove(file);
    }

    /**
     * Sets the name of the artist of this album.
     * 
     * @param artist
     *            the artist
     */
    public void setArtist(Artist artist) {
        this.artist = artist;
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
        int songs = getAudioObjects().size();
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
        int songNumber = audioFiles.size();
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
    public boolean isUnknownAlbum() {
        return getName().equalsIgnoreCase(getUnknownAlbum());
    }

    /**
     * Return <code>true</code> if this album is unknown
     * 
     * @return
     */
    public static boolean isUnknownAlbum(String album) {
        return getUnknownAlbum().equalsIgnoreCase(album);
    }

}
