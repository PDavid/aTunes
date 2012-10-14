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

package net.sourceforge.atunes.kernel.modules.radio;

import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

import org.commonjukebox.plugins.model.PluginApi;
import org.joda.time.base.BaseDateTime;

/**
 * A radio station.
 */
@PluginApi
public final class Radio implements IRadio {

	private static final long serialVersionUID = 3295941106814718559L;

	private String name;
	private String url;
	private String label;
	private String genre;
	private boolean isRemoved;
	// Song infos from radio stream
	private transient String artist;
	private transient String title;
	private transient boolean songInfoAvailable;
	private long bitrate;
	private int frequency;

	/**
	 * Instantiates a new radio.
	 * 
	 * @param name
	 *            the name
	 * @param url
	 *            the url
	 * @param label
	 *            the label
	 */
	public Radio(final String name, final String url, final String label) {
		this.name = name;
		this.url = url;
		this.label = label;
	}

	@Override
	public void deleteSongInfo() {
		artist = null;
		title = null;
		songInfoAvailable = false;
	}

	@Override
	public String getAlbum(final IUnknownObjectChecker unknownObjectChecker) {
		return "";
	}

	@Override
	public String getAlbumArtist(final IUnknownObjectChecker unknownObjectChecker) {
		return "";
	}

	@Override
	public String getAlbumArtistOrArtist(final IUnknownObjectChecker unknownObjectChecker) {
		return getArtist(unknownObjectChecker);
	}

	@Override
	public String getArtist(final IUnknownObjectChecker unknownObjectChecker) {
		return artist == null ? "" : artist;
	}

	@Override
	public List<IRadio> getAudioObjects() {
		return Collections.singletonList((IRadio)this);
	}

	@Override
	public long getBitrate() {
		return bitrate;
	}

	@Override
	public String getComposer() {
		return "";
	}

	@Override
	public int getDuration() {
		return 0;
	}

	@Override
	public int getFrequency() {
		return frequency;
	}

	@Override
	public String getGenre(final IUnknownObjectChecker unknownObjectChecker) {
		if (genre != null) {
			return genre;
		}
		return "";
	}

	@Override
	public String getLyrics() {
		return "";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getStars() {
		return 0;
	}

	@Override
	public String getTitle() {
		return title == null ? getName() : title;
	}

	@Override
	public String getTitleOrFileName() {
		return getTitle();
	}

	@Override
	public int getTrackNumber() {
		return 0;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getYear() {
		return "";
	}

	@Override
	public BaseDateTime getDate() {
		return null;
	}

	@Override
	public boolean isRemoved() {
		return isRemoved;
	}

	@Override
	public boolean isSongInfoAvailable() {
		return songInfoAvailable;
	}

	@Override
	public void setArtist(final String artist) {
		this.artist = artist;
	}

	@Override
	public void setBitrate(final long bitrate) {
		this.bitrate = bitrate;
	}

	@Override
	public void setFrequency(final int frequency) {
		this.frequency = frequency;
	}

	@Override
	public void setGenre(final String genre) {
		this.genre = genre;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public void setRemoved(final boolean isRemoved) {
		this.isRemoved = isRemoved;
	}

	@Override
	public void setSongInfoAvailable(final boolean songInfoAvailable) {
		this.songInfoAvailable = songInfoAvailable;
	}

	@Override
	public void setStars(final int stars) {
		// Nothing to do
	}

	@Override
	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(final String label) {
		this.label = label;
	}

	@Override
	public boolean isSeekable() {
		return false;
	}

	@Override
	public int compareTo(final IRadio o) {
		return name.compareTo(o.getName());
	}

	@Override
	public int getDiscNumber() {
		return 0;
	}

	@Override
	public String getComment() {
		return "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isRemoved ? 1231 : 1237);
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		if (!(obj instanceof Radio)) {
			return false;
		}
		Radio other = (Radio) obj;
		if (isRemoved != other.isRemoved) {
			return false;
		}
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!url.equals(other.url)) {
			return false;
		}
		return true;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public String getAudioObjectDescription(final IUnknownObjectChecker unknownObjectChecker) {
		return getTitle();
	}
}
