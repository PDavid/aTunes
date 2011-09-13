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

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFullScreenHandler;

public class FullScreenHandler extends AbstractHandler implements IFullScreenHandler {

	private FullScreenController controller;
	
    @Override
    public void playListCleared() {
        // Next actions must be done ONLY if stopPlayerWhenPlayListClear is enabled
        if (getState().isStopPlayerOnPlayListClear()) {

            // Remove audio object information from full screen mode
        	if (getFullScreenController() != null) {
        		getFullScreenController().setAudioObjects(null);
        	}
        }
    }
    
    @Override
    public void selectedAudioObjectChanged(IAudioObject audioObject) {
        updateAudioObjectsToShow(audioObject);
    }

	/**
	 * Returns full screen controller or null if not created, so it must be checked first
	 * @return
	 */
	private FullScreenController getFullScreenController() {
		return controller;
	}
	
	/**
	 * Creates full screen controller, to be called only when needed
	 */
	private void createFullScreenController() {
		if (controller == null) {
			controller = new FullScreenController(getState(), getFrame());
		}
	}
	
	private void updateAudioObjectsToShow(IAudioObject audioObject) {
		if (getFullScreenController() != null) {
			getFullScreenController().setAudioObjects(getAudioObjectsToShow(audioObject));
		}
	}
	
	/**
	 * Returns audio objects to show in full screen: 2 previous audio objects, current one, and next three objects
	 * @param current current
	 * @return
	 */
	private List<IAudioObject> getAudioObjectsToShow(IAudioObject current) {
        List<IAudioObject> objects = new ArrayList<IAudioObject>(6);
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getPreviousAudioObject(2));
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getPreviousAudioObject(1));
        objects.add(current);
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getNextAudioObject(1));
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getNextAudioObject(2));
        
        // This is not visible in full screen, but used to prepare next cover
        objects.add(PlayListHandler.getInstance().getCurrentPlayList(false).getNextAudioObject(3)); 
		
        return objects;
	}
	
	@Override
	public void toggleFullScreenVisibility() {
		// Here must create controller and window, as first call to this method will be when full screen becomes visible
		createFullScreenController();
		
		// Be sure to update audio objects before show window
		if (!getFullScreenController().isVisible()) {
			updateAudioObjectsToShow(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList());
		}
		getFullScreenController().toggleVisibility();
	}

	@Override
	public void setPlaying(boolean playing) {
		if (getFullScreenController() != null) {
			getFullScreenController().setPlaying(playing);
		}
	}

	@Override
	public boolean isVisible() {
		if (getFullScreenController() != null) {
			return getFullScreenController().isVisible();
		}
		return false;
	}

	@Override
	public void setAudioObjectLength(long currentLength) {
		if (getFullScreenController() != null) {
			getFullScreenController().setAudioObjectLenght(currentLength);
		}
	}

	@Override
	public void setCurrentAudioObjectPlayedTime(long actualPlayedTime, long currentAudioObjectLength) {
		if (getFullScreenController() != null) {
			getFullScreenController().setCurrentAudioObjectPlayedTime(actualPlayedTime, currentAudioObjectLength);
		}
	}

	@Override
	public void setVolume(int finalVolume) {
		if (getFullScreenController() != null) {
			getFullScreenController().setVolume(finalVolume);
		}
	}
}
