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


/**
 * Responsible of show a full screen window to show what is being played
 * @author alex
 *
 */
public interface IFullScreenHandler extends IHandler {

	/**
	 * Shows or hides full screen
	 */
	public void toggleFullScreenVisibility();

	/**
	 * Sets playing
	 * @param playing
	 */
	public void setPlaying(boolean playing);

	/**
	 * Returns true if full screen is visible
	 * @return
	 */
	public boolean isVisible();

	/**
	 * Sets audio object length
	 * @param currentLength
	 */
	public void setAudioObjectLength(long currentLength);

	/**
	 * Set audio object played time
	 * @param actualPlayedTime
	 * @param currentAudioObjectLength
	 */
	public void setCurrentAudioObjectPlayedTime(long actualPlayedTime, long currentAudioObjectLength);

	/**
	 * Sets volume
	 * @param finalVolume
	 */
	public void setVolume(int finalVolume);

}
