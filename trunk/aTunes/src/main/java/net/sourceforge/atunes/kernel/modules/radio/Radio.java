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

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IProxyBean;
import net.sourceforge.atunes.model.IRadio;

import org.commonjukebox.plugins.model.PluginApi;
import org.joda.time.base.BaseDateTime;

/**
 * A radio station.
 */
@PluginApi
public final class Radio implements IRadio {

    private static final long serialVersionUID = 3295941106814718559L;

    private static final String[] PLAYLISTS = { "m3u", "pls", "asx", "wax", "b4s", "kpl", "wvx", "ram", "rm", "smil" };

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
    public Radio(String name, String url, String label) {
        this.name = name;
        this.url = url;
        this.label = label;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#deleteSongInfo()
	 */
    @Override
	public void deleteSongInfo() {
        artist = null;
        title = null;
        songInfoAvailable = false;
    }

    @Override
    public String getAlbum() {
        return "";
    }

    @Override
    public String getAlbumArtist() {
        return "";
    }

    @Override
    public String getAlbumArtistOrArtist() {
        return getAlbumArtist().isEmpty() ? getArtist() : getAlbumArtist();
    }

    @Override
    public String getArtist() {
        return artist == null ? "" : artist;
    }

    @Override
    public List<IRadio> getAudioObjects() {
        List<IRadio> songs = new ArrayList<IRadio>();
        songs.add(this);
        return songs;
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
    public String getGenre() {
        if (genre != null) {
            return genre;
        }
        return "";
    }

    @Override
    public String getLyrics() {
        return "";
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#getName()
	 */
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
	public boolean hasPlaylistUrl(INetworkHandler networkHandler, IProxyBean proxy) {
        // First check based on URL end (extension)
        for (String pl : PLAYLISTS) {
            if (url.trim().toLowerCase().endsWith(pl)) {
                return true;
            }
        }

        // WORKAROUND: If URL has no extension, then try to get from content
        try {
            String radioContent = networkHandler.readURL(networkHandler.getConnection(url));
            for (String pl : PLAYLISTS) {
                if (radioContent.trim().toLowerCase().contains(pl)) {
                    return true;
                }
            }

        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return false;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#isRemoved()
	 */
    @Override
	public boolean isRemoved() {
        return isRemoved;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#isSongInfoAvailable()
	 */
    @Override
	public boolean isSongInfoAvailable() {
        return songInfoAvailable;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#setArtist(java.lang.String)
	 */
    @Override
	public void setArtist(String artist) {
        this.artist = artist;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#setBitrate(long)
	 */
    @Override
	public void setBitrate(long bitrate) {
        this.bitrate = bitrate;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#setFrequency(int)
	 */
    @Override
	public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#setGenre(java.lang.String)
	 */
    @Override
	public void setGenre(String genre) {
        this.genre = genre;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#setName(java.lang.String)
	 */
    @Override
	public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#setRemoved(boolean)
	 */
    @Override
	public void setRemoved(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#setSongInfoAvailable(boolean)
	 */
    @Override
	public void setSongInfoAvailable(boolean songInfoAvailable) {
        this.songInfoAvailable = songInfoAvailable;
    }

    @Override
    public void setStars(int stars) {
        // Nothing to do
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#setTitle(java.lang.String)
	 */
    @Override
	public void setTitle(String title) {
        this.title = title;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#setUrl(java.lang.String)
	 */
    @Override
	public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return name;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#getLabel()
	 */
    @Override
	public String getLabel() {
        return label;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.radio.IRadio#setLabel(java.lang.String)
	 */
    @Override
	public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean isSeekable() {
        return false;
    }

    @Override
    public int compareTo(IRadio o) {
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
    public String getToolTip() {
        return name;
    }

    @Override
    public boolean isExtendedToolTipSupported() {
        return false;
    }

    @Override
    public boolean isExtendedToolTipImageSupported() {
        return false;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
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
	public String getAudioObjectDescription() {
		return getTitle();
	}
}
