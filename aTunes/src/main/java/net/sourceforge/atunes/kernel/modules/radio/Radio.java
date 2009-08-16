/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.kernel.modules.radio;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.NetworkUtils;

/**
 * The Class Radio.
 */
public class Radio implements AudioObject, Serializable, TreeObject, Comparable<Radio> {

    private static final long serialVersionUID = 3295941106814718559L;

    /** The Constant PLAYLISTS. */
    private static final String[] PLAYLISTS = { "m3u", "pls", "asx", "wax", "b4s", "kpl", "wvx", "ram", "rm", "smil" };

    /** The comparator. */
    private static Comparator<Radio> comparator = new Comparator<Radio>() {
        @Override
        public int compare(Radio o1, Radio o2) {
            return o1.name.compareToIgnoreCase(o2.name);
        }
    };

    /** The name. */
    String name;

    /** The url. */
    private String url;

    /** The label. */
    private String label;

    /** The genre. */
    private String genre;

    /** The is removed. */
    private boolean isRemoved;

    // Song infos from radio stream
    /** The artist. */
    private transient String artist;

    /** The title. */
    private transient String title;

    /** The composer. */
    private transient String composer;

    /** The song info available. */
    private transient boolean songInfoAvailable;

    /** The bitrate. */
    private long bitrate;

    /** The frequency. */
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
    public static List<Radio> getRadios(List<AudioObject> audioObjects) {
        List<Radio> result = new ArrayList<Radio>();
        for (AudioObject audioObject : audioObjects) {
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Radio)) {
            return false;
        }
        return (name + url).equals(((Radio) o).getName() + ((Radio) o).getUrl());

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getAlbum()
     */
    @Override
    public String getAlbum() {
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getAlbumArtist()
     */
    @Override
    public String getAlbumArtist() {
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getArtist()
     */
    @Override
    public String getArtist() {
        return artist == null ? "" : artist;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.TreeObject#getAudioObjects()
     */
    @Override
    public List<AudioObject> getAudioObjects() {
        List<AudioObject> songs = new ArrayList<AudioObject>();
        songs.add(this);
        return songs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getBitrate()
     */
    @Override
    public long getBitrate() {
        return bitrate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getComposer()
     */
    @Override
    public String getComposer() {
        if (composer != null) {
            return composer;
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getDuration()
     */
    @Override
    public long getDuration() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getFrequency()
     */
    @Override
    public int getFrequency() {
        return frequency;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getGenre()
     */
    @Override
    public String getGenre() {
        if (genre != null) {
            return genre;
        }
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getLyrics()
     */
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

    /**
     * Gets the radios.
     * 
     * @return the radios
     */
    public List<Radio> getRadios() {
        List<Radio> songs = new ArrayList<Radio>();
        songs.add(this);
        return songs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getStars()
     */
    @Override
    public int getStars() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getTitle()
     */
    @Override
    public String getTitle() {
        return title == null ? getName() : title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getTitleOrFileName()
     */
    @Override
    public String getTitleOrFileName() {
        return getTitle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getTrackNumber()
     */

    @Override
    public Integer getTrackNumber() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getUrl()
     */
    @Override
    public String getUrl() {
        return url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#getYear()
     */
    @Override
    public String getYear() {
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (name + url).hashCode();
    }

    /**
     * Checks for playlist url.
     * 
     * @return true, if successful
     */
    public boolean hasPlaylistUrl() {
        // First check based on URL end (extension)
        for (String pl : PLAYLISTS) {
            if (url.trim().toLowerCase().endsWith(pl)) {
                return true;
            }
        }

        // WORKAROUND: If URL has no extension, then try to get from content
        // Just read first bytes to avoid starting read a non playlist url
        try {
            String radioContent = NetworkUtils.readURL(NetworkUtils.getConnection(url, Proxy.getProxy(ApplicationState.getInstance().getProxy())), 100);
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
     * Sets the composer.
     * 
     * @param composer
     *            the new composer
     */
    public void setComposer(String composer) {
        this.composer = composer;
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

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#setStars(int)
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.atunes.model.AudioObject#isSeekable()
     */
    @Override
    public boolean isSeekable() {
        return false;
    }

    @Override
    public int compareTo(Radio o) {
        return name.compareTo(o.getName());
    }

    @Override
    public Integer getDiscNumber() {
        return 0;
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
    public boolean canHaveCustomImages() {
        return false;
    }

    @Override
    public ImageIcon getCustomImage(int width, int height) {
        return null;
    }

    @Override
    public ImageIcon getGenericImage(GenericImageSize imageSize) {
        switch (imageSize) {
        case SMALL: {
            return ImageLoader.RADIO_LITTLE;
        }
        case MEDIUM: {
            return ImageLoader.RADIO;
        }
        case BIG: {
            return ImageLoader.RADIO_BIG;
        }
        default: {
            throw new IllegalArgumentException("unknown image size");
        }
        }
    }
}
