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

import java.awt.EventQueue;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IFullScreenHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IState;

public final class Volume {

	private IState state;
	
	private IPlayerHandler playerHandler;
	
	private IFullScreenHandler fullScreenHandler;
	
	/**
	 * @param state
	 */
	public void setState(IState state) {
		this.state = state;
	}
	
	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}
	
	/**
	 * @param fullScreenHandler
	 */
	public void setFullScreenHandler(IFullScreenHandler fullScreenHandler) {
		this.fullScreenHandler = fullScreenHandler;
	}
	
    /**
     * @param volume
     * @param saveVolume
     */
    public void setVolume(int volume, boolean saveVolume) {
        applyVolume(saveVolume, getVolumeLevel(volume));
    }
    
    /**
     * @param volume
     * @param state
     * @param playerHandler
     */
    public void setVolume(int volume) {
    	setVolume(volume, true);
    }

	/**
	 * @param saveVolume
	 * @param finalVolume
	 */
	private void applyVolume(boolean saveVolume, final int finalVolume) {
		if (saveVolume) {
        	state.setVolume(finalVolume);
        }
        playerHandler.setVolume(finalVolume);

        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                	fullScreenHandler.setVolume(finalVolume);
                }
            });
        } else {
        	fullScreenHandler.setVolume(finalVolume);
        }
	}

	/**
	 * @param volume
	 * @return
	 */
	private int getVolumeLevel(int volume) {
		int volumeLevel = volume;
        if (volumeLevel < 0) {
            volumeLevel = 0;
        } else if (volumeLevel > 100) {
            volumeLevel = 100;
        }
		return volumeLevel;
	}    
}
