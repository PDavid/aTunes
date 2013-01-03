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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.Serializable;
import java.util.List;

import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class CDInfo.
 */
public class CDInfo implements Serializable {

    private static final long serialVersionUID = -979380358668685738L;

    /** The id. */
    private String id;

    /** The tracks. */
    private int tracks;

    /** The duration. */
    private String duration;

    /** The durations. */
    private List<String> durations;

    /** The album. */
    private String album;
    // Album artist
    /** The artist. */
    private String artist;

    /** The titles. */
    private List<String> titles;

    /** The artists. */
    private List<String> artists;

    /** The composers. */
    private List<String> composers;
    
    /** Genre of CD */
    private String genre;

    /**
     * Gets the album.
     * 
     * @return the album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Gets the artist.
     * 
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Gets the artists.
     * 
     * @return the artists
     */
    public List<String> getArtists() {
        return artists;
    }

    /**
     * Gets the composers.
     * 
     * @return the composers
     */
    public List<String> getComposers() {
        return composers;
    }

    /**
     * Gets the duration.
     * 
     * @return the duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Gets the durations.
     * 
     * @return the durations
     */
    public List<String> getDurations() {
        return durations;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public String getID() {
        return id;
    }

    /**
     * Gets the titles.
     * 
     * @return the titles
     */
    public List<String> getTitles() {
        return titles;
    }

    /**
     * Gets the tracks.
     * 
     * @return the tracks
     */
    public int getTracks() {
        return tracks;
    }

    /**
     * Sets the album.
     * 
     * @param album
     *            the new album
     */
    public void setAlbum(String album) {
        this.album = album;
    }

    /**
     * Sets the artist.
     * 
     * @param artist
     *            the new artist
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Sets the artists.
     * 
     * @param artists
     *            the new artists
     */
    public void setArtists(List<String> artists) {
        this.artists = artists;
    }

    /**
     * Sets the composers.
     * 
     * @param composers
     *            the new composers
     */
    public void setComposers(List<String> composers) {
        this.composers = composers;
    }

    /**
     * Sets the duration.
     * 
     * @param duration
     *            the new duration
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Sets the durations.
     * 
     * @param durations
     *            the new durations
     */
    public void setDurations(List<String> durations) {
        this.durations = durations;
    }

    /**
     * Sets the id.
     * 
     * @param id
     *            the new id
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Sets the titles.
     * 
     * @param titles
     *            the new titles
     */
    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    /**
     * Sets the tracks.
     * 
     * @param tracks
     *            the new tracks
     */
    public void setTracks(int tracks) {
        this.tracks = tracks;
    }
    
    /**
     * @param genre
     */
    public void setGenre(String genre) {
		this.genre = genre;
	}
    
    /**
     * @return
     */
    public String getGenre() {
		return genre;
	}

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return StringUtils.getString("(id=", id, " tracks=", tracks, " duration=", duration, ")");
    }
}
