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

import org.joda.time.DateTime;

/**
 * Represents statistics about an audio object
 * @author alex
 *
 */
public interface IAudioObjectStatistics extends Serializable {

	/**
	 * Gets the last played.
	 * 
	 * @return the last played
	 */
	public DateTime getLastPlayed();

	/**
	 * Gets the times played.
	 * 
	 * @return the times played
	 */
	public int getTimesPlayed();

	/**
	 * Increase statistics
	 */
	public void increaseStatistics();

	/**
	 * Reset.
	 */
	public void resetStatistics();
}