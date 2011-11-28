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

package net.sourceforge.atunes.model;

import java.io.File;
import java.util.List;


public interface ILocalAudioObject extends IAudioObject, Comparable<ILocalAudioObject> {
	
	/**
	 * Returns file containing audio object
	 * @return
	 */
	File getFile();

	/**
	 * Returns true if audio object information is updated
	 * @return
	 */
	boolean isUpToDate();
	
    /**
     * Sets the external pictures.
     * 
     * @param externalPictures
     *            the new external pictures
     */
    void setExternalPictures(List<File> externalPictures);
    
    /**
     * Adds the external picture.
     * 
     * @param picture
     *            the picture
     */
    void addExternalPicture(File picture);
    
    /**
     * Returns number of external pictures
     * @return
     */
    public int getExternalPicturesCount();
    
    /**
     * Returns external pictures list
     * @return
     */
    List<File> getExternalPictures();
    
    /**
     * Gets the tag.
     * 
     * @return the tag
     */
    ITag getTag();
    
    /**
     * Sets the tag.
     * 
     * @param tag
     *            the new tag
     */
    void setTag(ITag tag);
    
    /**
     * Gets the name without extension.
     * 
     * @return the name without extension
     */
    String getNameWithoutExtension();
    
    /**
     * Refresh tag.
     */
    void refreshTag();

    /**
     * Sets the file of this audio file
     * 
     * @param file
     */
    void setFile(File file);

    /**
     * Checks if the tag of this audio file does support internal images
     * 
     * @return if the tag of this audio file does support internal images
     */
    boolean supportsInternalPicture();
    
    /**
     * Checks for internal picture.
     * 
     * @return true, if successful
     */
    boolean hasInternalPicture();

	/**
	 * Sets duration
	 * @param trackLength
	 */
	void setDuration(int trackLength);

	/**
	 * Set bitrate
	 * @param bitRateAsNumber
	 */
	void setBitrate(long bitRateAsNumber);

	/**
	 * Set frequency
	 * @param sampleRateAsNumber
	 */
	void setFrequency(int sampleRateAsNumber);
}
