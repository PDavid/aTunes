/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.awt.Paint;
import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.ColorMutableImageIcon;
import net.sourceforge.atunes.gui.images.RadioImageIcon;
import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.kernel.modules.proxy.ExtendedProxy;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.NetworkUtils;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * A radio station.
 */
@PluginApi
public final class Radio implements IAudioObject, Serializable, TreeObject<Radio>, Comparable<Radio> {

    private static final long serialVersionUID = 3295941106814718559L;

    private static final String[] PLAYLISTS = { "m3u", "pls", "asx", "wax", "b4s", "kpl", "wvx", "ram", "rm", "smil" };

    private static Comparator<Radio> comparator = new Comparator<Radio>() {
        @Override
        public int compare(Radio o1, Radio o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };

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

    /**
     * Gets the comparator.
     * 
     * @return the comparator
     */
    public static Comparator<Radio> getComparator() {
        return comparator;
    }

    /**
     * Gets the radios.
     * 
     * @param audioObjects
     *            the audio objects
     * 
     * @return the radios
     */
    public static List<Radio> getRadios(List<IAudioObject> audioObjects) {
        List<Radio> result = new ArrayList<Radio>();
        for (IAudioObject audioObject : audioObjects) {
            if (audioObject instanceof Radio) {
                result.add((Radio) audioObject);
            }
        }
        return result;
    }

    /**
     * Delete song info.
     */
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
    public List<Radio> getAudioObjects() {
        List<Radio> songs = new ArrayList<Radio>();
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

    /**
     * Gets the name.
     * 
     * @return Radio station name
     */
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
    public Date getDate() {
        return null;
    }

    /**
     * Checks for playlist url.
     * 
     * @return true, if successful
     */
    public boolean hasPlaylistUrl(ExtendedProxy proxy) {
        // First check based on URL end (extension)
        for (String pl : PLAYLISTS) {
            if (url.trim().toLowerCase().endsWith(pl)) {
                return true;
            }
        }

        // WORKAROUND: If URL has no extension, then try to get from content
        // Just read first bytes to avoid starting read a non playlist url
        try {
            String radioContent = NetworkUtils.readURL(NetworkUtils.getConnection(url, proxy), 100);
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

    /**
     * Checks if is removed.
     * 
     * @return true, if is removed
     */
    public boolean isRemoved() {
        return isRemoved;
    }

    /**
     * Checks if is song info available.
     * 
     * @return true, if is song info available
     */
    public boolean isSongInfoAvailable() {
        return songInfoAvailable;
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
     * Sets the bitrate.
     * 
     * @param bitrate
     *            the new bitrate
     */
    public void setBitrate(long bitrate) {
        this.bitrate = bitrate;
    }

    /**
     * Sets the frequency.
     * 
     * @param frequency
     *            the new frequency
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * Sets the genre.
     * 
     * @param genre
     *            the new genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set a preset station as removed. Set true for removing.
     * 
     * @param isRemoved
     *            the is removed
     */
    public void setRemoved(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }

    /**
     * Sets the song info available.
     * 
     * @param songInfoAvailable
     *            the new song info available
     */
    public void setSongInfoAvailable(boolean songInfoAvailable) {
        this.songInfoAvailable = songInfoAvailable;
    }

    @Override
    public void setStars(int stars) {
        // Nothing to do
    }

    /**
     * Sets the title.
     * 
     * @param title
     *            the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the url.
     * 
     * @param url
     *            the new url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Gets the label.
     * 
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label.
     * 
     * @param label
     *            the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean isSeekable() {
        return false;
    }

    @Override
    public int compareTo(Radio o) {
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
    public void setExtendedToolTip(ExtendedToolTip toolTip) {
    }

    @Override
    public ImageIcon getExtendedToolTipImage() {
        return null;
    }

    @Override
    public boolean isExtendedToolTipImageSupported() {
        return false;
    }

    @Override
    public ImageIcon getImage(ImageSize imageSize) {
        return null;
    }

    @Override
    public ColorMutableImageIcon getGenericImage(GenericImageSize imageSize) {
        switch (imageSize) {
        case SMALL: {
        	return new ColorMutableImageIcon() {
				@Override
				public ImageIcon getIcon(Paint paint) {
		            return RadioImageIcon.getSmallIcon(paint);
				}
			};
        }
        case MEDIUM: {
        	return new ColorMutableImageIcon() {
				@Override
				public ImageIcon getIcon(Paint paint) {
		            return RadioImageIcon.getIcon(paint);
				}
			};
        }
        case BIG: {
        	return new ColorMutableImageIcon() {
				@Override
				public ImageIcon getIcon(Paint paint) {
		            return RadioImageIcon.getBigIcon(paint);
				}
			};
        }
        default: {
            throw new IllegalArgumentException("unknown image size");
        }
        }
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
}
