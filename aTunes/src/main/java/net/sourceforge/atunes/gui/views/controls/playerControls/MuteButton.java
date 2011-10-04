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

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import net.sourceforge.atunes.gui.images.VolumeMaxImageIcon;
import net.sourceforge.atunes.gui.images.VolumeMedImageIcon;
import net.sourceforge.atunes.gui.images.VolumeMinImageIcon;
import net.sourceforge.atunes.gui.images.VolumeMuteImageIcon;
import net.sourceforge.atunes.gui.images.VolumeZeroImageIcon;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.MuteAction;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IState;

public final class MuteButton extends JToggleButton implements ILookAndFeelChangeListener {

    private static final long serialVersionUID = 6007885049773560874L;

    private IState state;
    
    private ILookAndFeel lookAndFeel;
    
    /**
     * Instantiates a new mute button.
     * 
     * @param size
     * @param state
     * @param lookAndFeelManager
     */
    public MuteButton(Dimension size, IState state, ILookAndFeelManager lookAndFeelManager) {
        super(Actions.getAction(MuteAction.class));
        this.state = state;
        this.lookAndFeel = lookAndFeelManager.getCurrentLookAndFeel();

        // Force size
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(false);

        lookAndFeel.putClientProperties(this);
        lookAndFeelManager.addLookAndFeelChangeListener(this);
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
    	setIcon(getVolumeIcon(state, lookAndFeel));
    }
    
    /**
     * Returns icon to use depending on volume and mute state
     * @param state
     * @param lookAndFeel
     * @return
     */
    public static ImageIcon getVolumeIcon(IState state, ILookAndFeel lookAndFeel) {
        if (state.isMuteEnabled()) {
            return VolumeMuteImageIcon.getIcon(lookAndFeel);
        } else {
            int volume = state.getVolume();
            if (volume > 80) {
                return VolumeMaxImageIcon.getIcon(lookAndFeel);
            } else if (volume > 40) {
            	return VolumeMedImageIcon.getIcon(lookAndFeel);
            } else if (volume > 5) {
            	return VolumeMinImageIcon.getIcon(lookAndFeel);
            } else {
            	return VolumeZeroImageIcon.getIcon(lookAndFeel);
            }
        }
    }

}
