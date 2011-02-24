/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.gui.views.dialogs.fullScreen.FullScreenWindow;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.AudioObject;

class FullScreenController extends AbstractSimpleController<FullScreenWindow> {

	FullScreenController(FullScreenWindow componentControlled) {
		super(componentControlled);
	}

	@Override
	protected void addBindings() {}

	@Override
	protected void addStateBindings() {}

	@Override
	protected void notifyReload() {}

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
	void setAudioObjects(List<AudioObject> objects) {
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
