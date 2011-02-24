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

package net.sourceforge.atunes.kernel.modules.audioobjetproperties;

import java.util.List;

import net.sourceforge.atunes.gui.views.panels.AudioObjectPropertiesPanel;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;

public class AudioObjectPropertiesHandler extends AbstractHandler {

	private static AudioObjectPropertiesHandler instance;
	
	private AudioObjectPropertiesController controller;
	
	private AudioObjectPropertiesHandler() {
	}
	
	/**
	 * Singleton instance
	 * @return
	 */
	public static AudioObjectPropertiesHandler getInstance() {
		if (instance == null) {
			instance = new AudioObjectPropertiesHandler();
		}
		return instance;
	}
	
    /**
     * Gets the  controller.
     * 
     * @return the controller
     */
    private AudioObjectPropertiesController getController() {
        if (controller == null) {
            AudioObjectPropertiesPanel panel = GuiHandler.getInstance().getPropertiesPanel();
            controller = new AudioObjectPropertiesController(panel);
        }
        return controller;
    }
	
    /**
     * Show song properties.
     * 
     * @param show
     *            the show
     */
    public void showSongProperties(boolean show) {
        ApplicationState.getInstance().setShowAudioObjectProperties(show);
        GuiHandler.getInstance().getFrame().showSongProperties(show);
        if (show) {
            if (PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList() != null) {
                getController().updateValues(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList());
            } else {
            	getController().onlyShowPropertiesPanel();
            }
        }
    }

	@Override
	public void applicationStarted(List<AudioObject> playList) {
        showSongProperties(ApplicationState.getInstance().isShowAudioObjectProperties());
	}

	@Override
	public void applicationFinish() {
	}

	@Override
	public void applicationStateChanged(ApplicationState newState) {
	}

	@Override
	protected void initHandler() {
	}

	public void refreshPicture() {
		getController().refreshPicture();
	}

	private void refreshFavoriteIcons() {
		getController().refreshFavoriteIcons();
	}
	
    @Override
    public void playListCleared() {
        // Next actions must be done ONLY if stopPlayerWhenPlayListClear is enabled
        if (ApplicationState.getInstance().isStopPlayerOnPlayListClear() && ApplicationState.getInstance().isShowAudioObjectProperties()) {
        	getController().clearPanel();
        }
    }
    
    @Override
    public void selectedAudioObjectChanged(AudioObject audioObject) {
        // Update file properties
        if (ApplicationState.getInstance().isShowAudioObjectProperties()) {
            getController().updateValues(audioObject);
        }
    }
    
    @Override
    public void favoritesChanged() {
        // Update audio object properties panel
        refreshFavoriteIcons();
    }
}
