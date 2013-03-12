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

import javax.swing.Action;

/**
 * A panel with player controls
 * 
 * @author alex
 * 
 */
public interface IPlayerControlsPanel extends IPanel {

	/**
	 * Returns progress slider
	 * 
	 * @return
	 */
	public IProgressSlider getProgressSlider();

	/**
	 * Set progress
	 * 
	 * @param time
	 * @param remainingTime
	 * @param fading
	 */
	public void setProgress(long time, long remainingTime, boolean fading);

	/**
	 * Updates volume controls with the volume level
	 * 
	 * @param volume
	 */
	public void setVolume(int volume);

	/**
	 * Set if app is playing or not
	 * 
	 * @param playing
	 */
	public void setPlaying(boolean playing);

	/**
	 * Adds a secondary control
	 * 
	 * @param action
	 */
	public void addSecondaryControl(Action action);

	/**
	 * Adds a secondary toggle control
	 * 
	 * @param action
	 */
	public void addSecondaryToggleControl(Action action);

	/**
	 * Hides or shows advanced controls
	 * 
	 * @param show
	 */
	public void showAdvancedPlayerControls(boolean show);

}