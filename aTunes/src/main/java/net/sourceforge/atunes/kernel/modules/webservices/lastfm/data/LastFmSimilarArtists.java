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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import de.umass.lastfm.Artist;
import de.umass.lastfm.ImageSize;

/**
 * Similar artists retrieved from last.fm
 * 
 * @author alex
 * 
 */
public class LastFmSimilarArtists implements ISimilarArtistsInfo {

	private static final long serialVersionUID = -8771547790339792068L;

	private String artistName;
	private String picture;
	private List<IArtistInfo> artists;

	/**
	 * Gets the similar artists.
	 * 
	 * @param as
	 * @param a
	 * @param max
	 * @return
	 */
	public static ISimilarArtistsInfo getSimilarArtists(
			final Collection<Artist> as, final Artist a, final int max) {
		List<Artist> list = new ArrayList<Artist>(as);
		LastFmSimilarArtists similar = new LastFmSimilarArtists();

		similar.artistName = a.getName();
		similar.picture = a.getImageURL(ImageSize.LARGE);

		similar.artists = new ArrayList<IArtistInfo>();
		for (int i = 0; i < list.size(); i++) {
			if (i == max) {
				break;
			}
			similar.artists.add(LastFmArtist.getArtist(list.get(i),
					similar.artistName));
		}

		Collections.sort(similar.artists, new SimilarArtistComparator());

		return similar;
	}

	/**
	 * Gets the artist name.
	 * 
	 * @return the artist name
	 */
	@Override
	public String getArtistName() {
		return this.artistName;
	}

	/**
	 * Gets the artists.
	 * 
	 * @return the artists
	 */
	@Override
	public List<IArtistInfo> getArtists() {
		return this.artists;
	}

	/**
	 * Gets the picture.
	 * 
	 * @return the picture
	 */
	@Override
	public String getPicture() {
		return this.picture;
	}

	/**
	 * Sets the artist name.
	 * 
	 * @param artistName
	 *            the artistName to set
	 */
	@Override
	public void setArtistName(final String artistName) {
		this.artistName = artistName;
	}

	/**
	 * Sets the artists.
	 * 
	 * @param artists
	 *            the artists to set
	 */
	@Override
	public void setArtists(final List<? extends IArtistInfo> artists) {
		this.artists = artists != null ? new ArrayList<IArtistInfo>(artists)
				: null;
	}

	/**
	 * Sets the picture.
	 * 
	 * @param picture
	 *            the picture to set
	 */
	@Override
	public void setPicture(final String picture) {
		this.picture = picture;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.artistName == null) ? 0 : this.artistName.hashCode());
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
		LastFmSimilarArtists other = (LastFmSimilarArtists) obj;
		if (this.artistName == null) {
			if (other.artistName != null) {
				return false;
			}
		} else if (!this.artistName.equals(other.artistName)) {
			return false;
		}
		return true;
	}

}
