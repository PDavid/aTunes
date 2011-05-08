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

package net.sourceforge.atunes.gui.views.controls.playerControls;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import net.sourceforge.atunes.gui.images.VolumeMaxImageIcon;
import net.sourceforge.atunes.gui.images.VolumeMedImageIcon;
import net.sourceforge.atunes.gui.images.VolumeMinImageIcon;
import net.sourceforge.atunes.gui.images.VolumeMuteImageIcon;
import net.sourceforge.atunes.gui.images.VolumeZeroImageIcon;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelChangeListener;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.MuteAction;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

public final class MuteButton extends JToggleButton implements LookAndFeelChangeListener {

    private static final long serialVersionUID = 6007885049773560874L;

    /**
     * Instantiates a new mute button.
     * 
     * @param size
     */
    public MuteButton(Dimension size) {
        super(Actions.getAction(MuteAction.class));

        // Force size
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setFocusable(false);

        LookAndFeelSelector.getInstance().getCurrentLookAndFeel().putClientProperties(this);
        LookAndFeelSelector.getInstance().addLookAndFeelChangeListener(this);
    }
    
    @Override
    public void lookAndFeelChanged() {
    	updateIcon();
    }
    
    /**
     * Updates icon of mute
     */
    public void updateIcon() {
    	setIcon(getVolumeIcon());
    }
    
    /**
     * Returns icon to use depending on volume and mute state
     * @return
     */
    public static ImageIcon getVolumeIcon() {
        if (ApplicationState.getInstance().isMuteEnabled()) {
            return VolumeMuteImageIcon.getIcon();
        } else {
            int volume = ApplicationState.getInstance().getVolume();
            if (volume > 80) {
                return VolumeMaxImageIcon.getIcon();
            } else if (volume > 40) {
            	return VolumeMedImageIcon.getIcon();
            } else if (volume > 5) {
            	return VolumeMinImageIcon.getIcon();
            } else {
            	return VolumeZeroImageIcon.getIcon();
            }
        }
    }

}
