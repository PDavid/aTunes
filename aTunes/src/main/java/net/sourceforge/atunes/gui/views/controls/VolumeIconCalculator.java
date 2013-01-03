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

package net.sourceforge.atunes.gui.views.controls;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IStatePlayer;

/**
 * Returns volume icon according to volume level and look and feel
 * @author alex
 *
 */
public class VolumeIconCalculator {
	
	private ILookAndFeelManager lookAndFeelManager;
	
	private IIconFactory volumeMuteIcon;
	
	private IIconFactory volumeMaxIcon;
	
	private IIconFactory volumeMedIcon;
	
	private IIconFactory volumeMinIcon;
	
	private IIconFactory volumeZeroIcon;
	
	private IStatePlayer statePlayer;
	
	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}
	
	/**
	 * @param volumeMaxIcon
	 */
	public void setVolumeMaxIcon(IIconFactory volumeMaxIcon) {
		this.volumeMaxIcon = volumeMaxIcon;
	}
	
	/**
	 * @param volumeMedIcon
	 */
	public void setVolumeMedIcon(IIconFactory volumeMedIcon) {
		this.volumeMedIcon = volumeMedIcon;
	}
	
	/**
	 * @param volumeMinIcon
	 */
	public void setVolumeMinIcon(IIconFactory volumeMinIcon) {
		this.volumeMinIcon = volumeMinIcon;
	}
	
	/**
	 * @param volumeMuteIcon
	 */
	public void setVolumeMuteIcon(IIconFactory volumeMuteIcon) {
		this.volumeMuteIcon = volumeMuteIcon;
	}
	
	/**
	 * @param volumeZeroIcon
	 */
	public void setVolumeZeroIcon(IIconFactory volumeZeroIcon) {
		this.volumeZeroIcon = volumeZeroIcon;
	}
	
	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
	
	/**
     * Returns icon to use depending on volume and mute state
     * @return
     */
    public ImageIcon getVolumeIcon() {
    	return getIcon().getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls());
    }
    
    /**
     * Returns icon to use
     * @return
     */
    private IIconFactory getIcon() {
        if (statePlayer.isMuteEnabled()) {
            return volumeMuteIcon;
        } else {
            int volume = statePlayer.getVolume();
            if (volume > 80) {
                return volumeMaxIcon;
            } else if (volume > 40) {
            	return volumeMedIcon;
            } else if (volume > 5) {
            	return volumeMinIcon;
            } else {
            	return volumeZeroIcon;
            }
        }
    }
}
