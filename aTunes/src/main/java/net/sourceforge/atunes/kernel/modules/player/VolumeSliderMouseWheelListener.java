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

package net.sourceforge.atunes.kernel.modules.player;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import net.sourceforge.atunes.gui.views.controls.playerControls.VolumeSlider;

public final class VolumeSliderMouseWheelListener implements MouseWheelListener {
	
	private VolumeSlider volumeSlider;
	
	private Volume volumeController;
	
	/**
	 * @param volumeController
	 */
	public void setVolumeController(Volume volumeController) {
		this.volumeController = volumeController;
	}
	
	/**
	 * @param volumeSlider
	 */
	public void setVolumeSlider(VolumeSlider volumeSlider) {
		this.volumeSlider = volumeSlider;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	    int notches = e.getWheelRotation();
	    if (notches < 0) {
	    	volumeSlider.setValue(volumeSlider.getValue() + 5);
	    } else {
	    	volumeSlider.setValue(volumeSlider.getValue() - 5);
	    }

	    volumeController.setVolume(volumeSlider.getValue());
	}
}