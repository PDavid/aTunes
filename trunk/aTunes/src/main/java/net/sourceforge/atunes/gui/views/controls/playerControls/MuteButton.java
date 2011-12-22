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

package net.sourceforge.atunes.gui.views.controls.playerControls;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JToggleButton;

import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IState;

public final class MuteButton extends JToggleButton implements ILookAndFeelChangeListener {

    private static final long serialVersionUID = 6007885049773560874L;

    private IState state;
    
    private VolumeIconCalculator volumeIconCalculator;
    
    /**
     * Instantiates a new mute button.
     * 
     * @param size
     * @param muteAction
     */
    public MuteButton(Dimension size, Action muteAction) {
        super(muteAction);

        // Force size
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(false);
    }
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		lookAndFeelManager.getCurrentLookAndFeel().putClientProperties(this);
		lookAndFeelManager.addLookAndFeelChangeListener(this);
	}
    
    /**
     * @param state
     */
    public void setState(IState state) {
		this.state = state;
	}
    
    /**
     * @param volumeIconCalculator
     */
    public void setVolumeIconCalculator(VolumeIconCalculator volumeIconCalculator) {
		this.volumeIconCalculator = volumeIconCalculator;
	}
    
    @Override
    public void lookAndFeelChanged() {
    	updateIcon(state);
    }
    
    /**
     * Updates icon of mute
     * @param state
     */
    public void updateIcon(IState state) {
    	setIcon(volumeIconCalculator.getVolumeIcon());
    }
}
