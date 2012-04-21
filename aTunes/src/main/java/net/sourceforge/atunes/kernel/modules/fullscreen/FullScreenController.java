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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.util.List;

import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IAudioObject;

public class FullScreenController extends AbstractSimpleController<FullScreenWindow> {

	private FullScreenWindowFactory fullScreenWindowFactory;
	
	/**
	 * @param fullScreenWindowFactory
	 */
	public void setFullScreenWindowFactory(FullScreenWindowFactory fullScreenWindowFactory) {
		this.fullScreenWindowFactory = fullScreenWindowFactory;
	}
	
	/**
	 * Initializes controller
	 */
	public void initialize() {
        setComponentControlled(fullScreenWindowFactory.getFullScreenWindow());
	}

	/**
	 * Shows or hides full screen
	 */
	void toggleVisibility() {
		getComponentControlled().setVisible(!getComponentControlled().isVisible());
	}
	
	/**
	 * Sets the audio object.
	 * @param objects
	 */
	void setAudioObjects(List<IAudioObject> objects) {
		getComponentControlled().setAudioObjects(objects);
	}

	/**
	 * Sets the playing
	 * @param playing
	 */
	void setPlaying(boolean playing) {
		getComponentControlled().setPlaying(playing);
	}

	/**
	 * Returns true if full screen is visible
	 * @return
	 */
	boolean isVisible() {
		return getComponentControlled().isVisible();
	}

	/**
	 * Sets current audio object length
	 * @param currentLength
	 */
	void setAudioObjectLenght(long currentLength) {
		getComponentControlled().setAudioObjectLength(currentLength);
	}

	/**
	 * Sets current audio object played time
	 * @param actualPlayedTime
	 * @param currentAudioObjectLength
	 */
	void setCurrentAudioObjectPlayedTime(long actualPlayedTime, long currentAudioObjectLength) {
		getComponentControlled().setCurrentAudioObjectPlayedTime(actualPlayedTime, currentAudioObjectLength);		
	}

	/**
	 * Sets volume
	 * @param finalVolume
	 */
	public void setVolume(int finalVolume) {
		getComponentControlled().setVolume(finalVolume);
	}
}
