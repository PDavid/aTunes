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

package net.sourceforge.atunes.model;

import java.util.List;

/**
 * Metadata filled by user to rip a CD
 * @author alex
 *
 */
public class CDMetadata {

    private String albumArtist;
    private String album;
    private int year;
    private String genre;
    private int disc;
    private List<Integer> tracks;
    private List<String> trackNames;
    private List<String> artistNames;
    private List<String> composerNames;
	/**
	 * @return the albumArtist
	 */
	public String getAlbumArtist() {
		return albumArtist;
	}
	/**
	 * @param albumArtist the albumArtist to set
	 */
	public void setAlbumArtist(String albumArtist) {
		this.albumArtist = albumArtist;
	}
	/**
	 * @return the album
	 */
	public String getAlbum() {
		return album;
	}
	/**
	 * @param album the album to set
	 */
	public void setAlbum(String album) {
		this.album = album;
	}
	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}
	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}
	/**
	 * @param genre the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}
	/**
	 * @return the disc
	 */
	public int getDisc() {
		return disc;
	}
	/**
	 * @param disc the disc to set
	 */
	public void setDisc(int disc) {
		this.disc = disc;
	}
	/**
	 * @return the tracks
	 */
	public List<Integer> getTracks() {
		return tracks;
	}
	/**
	 * @param tracks the tracks to set
	 */
	public void setTracks(List<Integer> tracks) {
		this.tracks = tracks;
	}
	/**
	 * @return the trackNames
	 */
	public List<String> getTrackNames() {
		return trackNames;
	}
	/**
	 * @param trackNames the trackNames to set
	 */
	public void setTrackNames(List<String> trackNames) {
		this.trackNames = trackNames;
	}
	/**
	 * @return the artistNames
	 */
	public List<String> getArtistNames() {
		return artistNames;
	}
	/**
	 * @param artistNames the artistNames to set
	 */
	public void setArtistNames(List<String> artistNames) {
		this.artistNames = artistNames;
	}
	/**
	 * @return the composerNames
	 */
	public List<String> getComposerNames() {
		return composerNames;
	}
	/**
	 * @param composerNames the composerNames to set
	 */
	public void setComposerNames(List<String> composerNames) {
		this.composerNames = composerNames;
	}
	
    /**
     * @param trackNumber
     * @return Title for given track number
     */
    public String getTitle(int trackNumber) {
    	return getTrackNames() != null && getTrackNames().size() >= trackNumber ? getTrackNames().get(trackNumber - 1) : null;
    }
    
    /**
     * @param trackNumber
     * @return Artist for given track number
     */
    public String getArtist(int trackNumber) {
    	return getArtistNames().size() > trackNumber - 1 ? getArtistNames().get(trackNumber - 1) : null; 
    }
    
    /**
     * @param trackNumber
     * @return
     */
    public String getComposer(int trackNumber) {
    	return getComposerNames().size() > trackNumber - 1 ? getComposerNames().get(trackNumber - 1) : null;
    }
}
