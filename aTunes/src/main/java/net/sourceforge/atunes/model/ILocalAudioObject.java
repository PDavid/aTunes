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

package net.sourceforge.atunes.model;

import java.io.Serializable;

/**
 * An audio object located in disk
 * 
 * @author alex
 * 
 */
public interface ILocalAudioObject extends IAudioObject,
		Comparable<ILocalAudioObject>, Serializable {

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
	 * Checks for internal picture.
	 * 
	 * @return true, if successful
	 */
	boolean hasInternalPicture();

	/**
	 * Sets duration
	 * 
	 * @param trackLength
	 */
	void setDuration(int trackLength);

	/**
	 * Set bitrate
	 * 
	 * @param bitRateAsNumber
	 */
	void setBitrate(long bitRateAsNumber);

	/**
	 * @param variable
	 *            bitrate
	 */
	void setVariableBitrate(boolean variable);

	/**
	 * @return if bitrate is variable
	 */
	boolean isVariableBitrate();

	/**
	 * Set frequency
	 * 
	 * @param sampleRateAsNumber
	 */
	void setFrequency(int sampleRateAsNumber);

	/**
	 * Set when object is read
	 * 
	 * @param currentTimeMillis
	 */
	void setReadTime(long currentTimeMillis);

	/**
	 * @return size in bytes
	 */
	long getSize();

	/**
	 * @return time when audio object was read
	 */
	long getReadTime();
}
