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

package net.sourceforge.atunes.model;

import java.util.Date;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.ColorMutableImageIcon;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * Interface for all audio objects (e.g. AudioFile, Radio, PodcastFeedEntry)
 */
@PluginApi
public interface IAudioObject {

    /**
     * Gets the album.
     * 
     * @return the album
     */
    public String getAlbum();

    /**
     * Gets the album artist.
     * 
     * @return the album artist
     */
    public String getAlbumArtist();

    /**
     * Returns album artist or artist
     * 
     * @return the artist or (if empty) the album artist
     */
    public String getAlbumArtistOrArtist();

    /**
     * Gets the artist.
     * 
     * @return the artist
     */
    public String getArtist();

    /**
     * Gets the bitrate.
     * 
     * @return the bitrate
     */
    public long getBitrate();

    /**
     * Gets the composer.
     * 
     * @return the composer
     */
    public String getComposer();

    /**
     * Gets the duration.
     * 
     * @return the duration
     */
    public int getDuration();

    /**
     * Gets the frequency.
     * 
     * @return the frequency
     */
    public int getFrequency();

    /**
     * Gets the genre.
     * 
     * @return the genre
     */
    public String getGenre();

    /**
     * Gets the lyrics.
     * 
     * @return the lyrics
     */
    public String getLyrics();

    /**
     * Gets the stars.
     * 
     * @return the stars
     */
    public int getStars();

    /**
     * Gets the title.
     * 
     * @return the title
     */
    public String getTitle();

    /**
     * Gets the title or file name.
     * 
     * @return the title or file name
     */
    public String getTitleOrFileName();

    /**
     * Gets the track number.
     * 
     * @return the track number
     */
    public int getTrackNumber();

    /**
     * Gets the url.
     * 
     * @return the url
     */
    public String getUrl();

    /**
     * Gets the year.
     * 
     * @return the year
     */
    public String getYear();

    /**
     * Gets the date.
     * 
     * @return the date
     */
    public Date getDate();

    /**
     * Gets the comment
     * 
     * @return the comment
     */
    public String getComment();

    /**
     * Sets the stars.
     * 
     * @param stars
     *            the new stars
     */
    public void setStars(int stars);

    /**
     * Checks if is seekable.
     * 
     * @return true, if is seekable
     */
    public boolean isSeekable();

    /**
     * Gets the disc number
     * 
     * @return
     */
    public int getDiscNumber();

    /**
     * Returns a generic image for this audio object.
     * 
     * @param imageSize
     *            the size of the generic image
     * @return the generic image or <code>null</code> if no such image is
     *         available
     */
    public ColorMutableImageIcon getGenericImage(GenericImageSize imageSize);

    /**
     * Returns a image for this audio object.
     * 
     * @param imageSize
     * @param osManager
     * @return the image or <code>null</code> if no such image is available
     */
    public ImageIcon getImage(ImageSize imageSize, IOSManager osManager);
    
    /**
     * A human-readable text describing this object
     * @return
     */
    public String getAudioObjectDescription();
}
