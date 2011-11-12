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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import net.sourceforge.atunes.gui.views.controls.playerControls.SecondaryControl;
import net.sourceforge.atunes.gui.views.controls.playerControls.SecondaryToggleControl;

public class SecondaryPlayerControlsBuilder {

	private SecondaryToggleControl shuffleButton;
	private SecondaryToggleControl repeatButton;
	private SecondaryControl equalizerButton;
	private SecondaryToggleControl normalizeButton;
	
	/**
	 * @param shuffleButton
	 */
	public void setShuffleButton(SecondaryToggleControl shuffleButton) {
		this.shuffleButton = shuffleButton;
	}
	
	/**
	 * @param repeatButton
	 */
	public void setRepeatButton(SecondaryToggleControl repeatButton) {
		this.repeatButton = repeatButton;
	}
	
	/**
	 * @param equalizerButton
	 */
	public void setEqualizerButton(SecondaryControl equalizerButton) {
		this.equalizerButton = equalizerButton;
	}
	
	/**
	 * @param normalizeButton
	 */
	public void setNormalizeButton(SecondaryToggleControl normalizeButton) {
		this.normalizeButton = normalizeButton;
	}
	
    /**
     * Returns a panel with secondary controls
     * @return
     */
    public JPanel getSecondaryControls() {
    	JPanel secondaryControls = new JPanel(new GridBagLayout());

    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 0;
    	c.insets = new Insets(0, 5, 0, 0);
    	secondaryControls.add(shuffleButton, c);
    	c.gridx = 1;
    	c.insets = new Insets(0, 1, 0, 0);
    	secondaryControls.add(repeatButton, c);
    	c.gridx = 2;
    	secondaryControls.add(equalizerButton, c);
    	c.gridx = 3;
    	secondaryControls.add(normalizeButton, c);
    	
    	return secondaryControls;
    }

}
