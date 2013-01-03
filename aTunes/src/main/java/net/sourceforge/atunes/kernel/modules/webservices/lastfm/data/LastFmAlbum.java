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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.utils.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import de.umass.lastfm.Album;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.Playlist;
import de.umass.lastfm.Track;

/**
 * An album retrieved from last.fm
 * 
 * @author alex
 * 
 */
public class LastFmAlbum implements IAlbumInfo {

    private static final long serialVersionUID = -8021357529697065642L;

    private String artist;
    private String title;
    private String url;
    private String releaseDateString;
    private String mbid; // music brainz id
    private String bigCoverURL;
    private String thumbCoverURL;
    private List<ITrackInfo> tracks;

    /**
     * Gets the album.
     * 
     * @param a
     * @param pl
     * @return
     */
    public static IAlbumInfo getAlbum(final Album a, final Playlist pl) {
	LastFmAlbum album = new LastFmAlbum();

	album.artist = a.getArtist();
	album.title = a.getName();
	album.url = a.getUrl();
	album.mbid = a.getMbid();
	album.releaseDateString = a.getReleaseDate() != null ? a
		.getReleaseDate().toString() : "";
	album.bigCoverURL = getBiggestPosible(a);
	album.thumbCoverURL = getThumbURL(a);

	processPlayList(pl, album);

	return album;
    }

    /**
     * @param pl
     * @param album
     */
    private static void processPlayList(final Playlist pl,
	    final LastFmAlbum album) {
	if (pl != null) {
	    List<ITrackInfo> ts = new ArrayList<ITrackInfo>();
	    for (Track t : pl.getTracks()) {
		ts.add(LastFmTrack.getTrack(t));
	    }

	    // Process track list: if all tracks have a common string between
	    // (), [], {} as "(Live)" then it's removed from all of them
	    // In this way track names are more accurate
	    if (!ts.isEmpty()) {
		processListOfTracks(ts);
	    }

	    album.tracks = ts;
	}
    }

    /**
     * @param ts
     */
    private static void processListOfTracks(final List<ITrackInfo> ts) {
	String firstTrackTitle = ts.get(0).getTitle();
	// Get all text between () [] {}
	List<String> tokensOfFirstTrackTitle = StringUtils.getTextBetweenChars(
		firstTrackTitle, '(', ')');
	tokensOfFirstTrackTitle.addAll(StringUtils.getTextBetweenChars(
		firstTrackTitle, '[', ']'));
	tokensOfFirstTrackTitle.addAll(StringUtils.getTextBetweenChars(
		firstTrackTitle, '{', '}'));

	// Check what tokens are present in all track titles
	List<String> commonTokens = new ArrayList<String>();
	for (String token : tokensOfFirstTrackTitle) {
	    boolean common = true;
	    for (int i = 1; i < ts.size() && common; i++) {
		if (!ts.get(i).getTitle().contains(token)) {
		    common = false;
		}
	    }
	    if (common) {
		commonTokens.add(token);
	    }
	}

	// Then remove common tokens from all titles
	for (ITrackInfo ti : ts) {
	    for (String token : commonTokens) {
		ti.setTitle(ti.getTitle().replace(token, ""));
	    }
	    ti.setTitle(ti.getTitle().trim());
	}
    }

    /**
     * Returns URL of the biggest album cover
     * 
     * @param a
     * @return
     */
    private static String getBiggestPosible(final Album a) {
	// SMALL: 0
	// MEDIUM: 1
	// LARGE: 2
	// LARGESQUARE: 3
	// HUGE: 4
	// EXTRALARGE: 5
	// MEGA: 6
	// ORIGINAL: 7

	ImageSize[] sizes = ImageSize.values();
	// Start from extralarge
	for (int i = 5; i >= 0; i--) {
	    String url = a.getImageURL(sizes[i]);
	    if (url != null) {
		return url;
	    }
	}

	return null;
    }

    /**
     * Returns URL of the smallest album cover
     * 
     * @param a
     * @return
     */
    private static String getThumbURL(final Album a) {
	// SMALL: 0
	// MEDIUM: 1
	// LARGE: 2
	// LARGESQUARE: 3
	// HUGE: 4
	// EXTRALARGE: 5
	// MEGA: 6
	// ORIGINAL: 7

	ImageSize[] sizes = ImageSize.values();
	// Start from large
	for (int i = 2; i < sizes.length; i++) {
	    String url = a.getImageURL(sizes[i]);
	    if (url != null) {
		return url;
	    }
	}

	return null;
    }

    /**
     * Gets the artist.
     * 
     * @return the artist
     */
    @Override
    public String getArtist() {
	return artist;
    }

    /**
     * Gets the artist url.
     * 
     * @return the artist url
     */
    @Override
    public String getArtistUrl() {
	return url.substring(0, url.lastIndexOf('/'));
    }

    /**
     * Gets the big cover url.
     * 
     * @return the bigCoverURL
     */
    @Override
    public String getBigCoverURL() {
	return bigCoverURL;
    }

    /**
     * Gets the release date.
     * 
     * @return the release date
     */
    @Override
    public DateTime getReleaseDate() {
	try {
	    return DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss 'CEST' yyyy")
		    .withLocale(Locale.US).parseDateTime(releaseDateString);
	} catch (IllegalArgumentException e) {
	    return null;
	}
    }

    /**
     * Gets the release date string.
     * 
     * @return the releaseDateString
     */
    @Override
    public String getReleaseDateString() {
	return releaseDateString;
    }

    /**
     * Gets the title.
     * 
     * @return the title
     */
    @Override
    public String getTitle() {
	return title;
    }

    /**
     * Gets the tracks.
     * 
     * @return the tracks
     */
    @Override
    public List<ITrackInfo> getTracks() {
	return tracks;
    }

    /**
     * Gets the url.
     * 
     * @return the url
     */
    @Override
    public String getUrl() {
	return url;
    }

    /**
     * Gets the year.
     * 
     * @return the year
     */
    @Override
    public String getYear() {
	DateTime releaseDate = getReleaseDate();
	if (releaseDate == null) {
	    return "";
	}
	return Integer.toString(releaseDate.getYear());
    }

    @Override
    public String getThumbCoverURL() {
	return thumbCoverURL;
    }

    @Override
    public String toString() {
	return StringUtils.getString(artist, " - ", title);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((bigCoverURL == null) ? 0 : bigCoverURL.hashCode());
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	LastFmAlbum other = (LastFmAlbum) obj;
	if (bigCoverURL == null) {
	    if (other.bigCoverURL != null) {
		return false;
	    }
	} else if (!bigCoverURL.equals(other.bigCoverURL)) {
	    return false;
	}
	return true;
    }

    @Override
    public String getMbid() {
	return mbid;
    }

}
