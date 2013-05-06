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

import net.sourceforge.atunes.model.ITrackInfo;
import de.umass.lastfm.Track;

/**
 * Information about a track retrieved from last.fm
 * 
 * @author alex
 * 
 */
public class LastFmTrack implements ITrackInfo {

	private static final long serialVersionUID = -2692319576271311514L;

	private String title;
	private String url;
	private String artist;
	private String album;

	private transient boolean available;
	private transient boolean favorite;

	/**
	 * Gets the track.
	 * 
	 * @return the track
	 */
	protected static LastFmTrack getTrack(final Track t) {
		LastFmTrack track = new LastFmTrack();

		track.title = t.getName();
		track.url = t.getUrl();
		track.artist = t.getArtist();
		track.album = t.getAlbum();

		return track;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	@Override
	public String getTitle() {
		return this.title;
	}

	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
	@Override
	public String getUrl() {
		return this.url;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the title to set
	 */
	@Override
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Sets the url.
	 * 
	 * @param url
	 *            the url to set
	 */
	@Override
	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public String getArtist() {
		return this.artist;
	}

	@Override
	public String getAlbum() {
		return this.album;
	}

	@Override
	public void setAlbum(final String album) {
		this.album = album;
	}

	@Override
	public void setArtist(final String artist) {
		this.artist = artist;
	}

	@Override
	public boolean isAvailable() {
		return this.available;
	}

	@Override
	public void setAvailable(final boolean available) {
		this.available = available;
	}

	@Override
	public void setFavorite(final boolean favorite) {
		this.favorite = favorite;
	}

	@Override
	public boolean isFavorite() {
		return this.favorite;
	}
}
