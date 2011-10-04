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

package net.sourceforge.atunes.kernel.modules.cdripper.encoders;

import java.io.File;

import net.sourceforge.atunes.kernel.modules.cdripper.ProgressListener;

/**
 * The Interface Encoder.
 */
public interface Encoder {

    /**
     * Encode.
     * 
     * @param originalFile
     *            the original file
     * @param encodedFile
     *            the encoded file
     * @param title
     *            the title
     * @param trackNumber
     *            the track number
     * @param artist
     *            the artist
     * @param composer
     *            the composer
     * 
     * @return true, if successful
     */
    public boolean encode(File originalFile, File encodedFile, String title, int trackNumber, String artist, String composer);

    /**
     * Gets the extension of encoded files.
     * 
     * @return the extension of encoded files
     */
    public String getExtensionOfEncodedFiles();

    /**
     * Sets the album.
     * 
     * @param album
     *            the new album
     */
    public void setAlbum(String album);

    /**
     * Sets the artist.
     * 
     * @param artist
     *            the new artist
     */
    public void setArtist(String artist);

    /**
     * Sets the genre.
     * 
     * @param genre
     *            the new genre
     */
    public void setGenre(String genre);

    /**
     * Sets the listener.
     * 
     * @param listener
     *            the new listener
     */
    public void setListener(ProgressListener listener);

    /**
     * Sets the quality.
     * 
     * @param quality
     *            the new quality
     */
    public void setQuality(String quality);

    /**
     * Sets the year.
     * 
     * @param year
     *            the new year
     */
    public void setYear(int year);

    /**
     * Stop.
     */
    public void stop();

    /**
     * Returns a list of all available qualities for this encoder
     * 
     * @return
     */
    public String[] getAvailableQualities();

    /**
     * Returns default quality
     * 
     * @return
     */
    public String getDefaultQuality();

    /**
     * Get format name
     * 
     * @return
     */
    public String getFormatName();

}
