/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import net.sourceforge.atunes.gui.views.dialogs.fullScreen.FullScreenWindow;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;

public class FullScreenHandler extends AbstractHandler {

	private static FullScreenHandler instance;
	
	private FullScreenController controller;
	
	/**
	 * Returns singleton instance of this handler
	 * @return
	 */
	public static FullScreenHandler getInstance() {
		if (instance == null) {
			instance = new FullScreenHandler();
		}
		return instance;
	}
	
	@Override
	public void applicationStarted(List<AudioObject> playList) {
	}

	@Override
	public void applicationFinish() {
	}

	@Override
	public void applicationStateChanged(ApplicationState newState) {
	}

    @Override
    public void playListCleared() {
        // Next actions must be done ONLY if stopPlayerWhenPlayListClear is enabled
        if (ApplicationState.getInstance().isStopPlayerOnPlayListClear()) {

            // Remove audio object information from full screen mode
        	if (getFullScreenController() != null) {
        		getFullScreenController().setAudioObjects(null);
        	}
        }
    }
    
    @Override
    public void selectedAudioObjectChanged(AudioObject audioObject) {
        updateAudioObjectsToShow(audioObject);
    }

	@Override
	protected void initHandler() {
	}

	/**
	 * Returns full screen controller or null if not created, so it must be checked first
	 * @return
	 */
	private FullScreenController getFullScreenController() {
		return controller;
	}
	
	/**
	 * Creates full screen controller and window, to be called only when needed
	 */
	private void createFullScreenController() {
		JDialog.setDefaultLookAndFeelDecorated(false);
		FullScreenWindow window = new FullScreenWindow(GuiHandler.getInstance().getFrame().getFrame());
        JDialog.setDefaultLookAndFeelDecorated(true);
        controller = new FullScreenController(window);
	}
	
	private void updateAudioObjectsToShow(AudioObject audioObject) {
		if (getFullScreenController() != null) {
			getFullScreenController().setAudioObjects(getAudioObjectsToShow(audioObject));
		}
	}
	
	/**
	 * Returns audio objects to show in full screen: 2 previous audio objects, current one, and next three objects
	 * @param current current
	 * @return
	 */
	private List<AudioObject> getAudioObjectsToShow(AudioObject current) {
        List<AudioObject> objects = new ArrayList<AudioObject>(6);
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getPreviousAudioObject(2));
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getPreviousAudioObject(1));
        objects.add(current);
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getNextAudioObject(1));
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getNextAudioObject(2));
        
        // This is not visible in full screen, but used to prepare next cover
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getNextAudioObject(3)); 
		
        return objects;
	}
	
	/**
	 * Shows or hides full screen
	 */
	public void toggleFullScreenVisibility() {
		// Here must create controller and window, as first call to this method will be when full screen becomes visible
		createFullScreenController();
		
		// Be sure to update audio objects before show window
		if (!getFullScreenController().isVisible()) {
			updateAudioObjectsToShow(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList());
		}
		getFullScreenController().toggleVisibility();
	}

	/**
	 * Sets playing
	 * @param playing
	 */
	public void setPlaying(boolean playing) {
		if (getFullScreenController() != null) {
			getFullScreenController().setPlaying(playing);
		}
	}

	/**
	 * Returns true if full screen is visible
	 * @return
	 */
	public boolean isVisible() {
		if (getFullScreenController() != null) {
			return getFullScreenController().isVisible();
		}
		return false;
	}

	/**
	 * Sets audio object length
	 * @param currentLength
	 */
	public void setAudioObjectLength(long currentLength) {
		if (getFullScreenController() != null) {
			getFullScreenController().setAudioObjectLenght(currentLength);
		}
	}

	/**
	 * Set audio object played time
	 * @param actualPlayedTime
	 * @param currentAudioObjectLength
	 */
	public void setCurrentAudioObjectPlayedTime(long actualPlayedTime, long currentAudioObjectLength) {
		if (getFullScreenController() != null) {
			getFullScreenController().setCurrentAudioObjectPlayedTime(actualPlayedTime, currentAudioObjectLength);
		}
	}

	/**
	 * Sets volume
	 * @param finalVolume
	 */
	public void setVolume(int finalVolume) {
		if (getFullScreenController() != null) {
			getFullScreenController().setVolume(finalVolume);
		}
	}

}
