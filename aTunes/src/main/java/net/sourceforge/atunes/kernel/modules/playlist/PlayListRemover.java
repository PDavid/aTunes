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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.List;

import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.kernel.actions.SaveM3UPlayListAction;
import net.sourceforge.atunes.kernel.actions.SavePlayListAction;
import net.sourceforge.atunes.kernel.actions.ShufflePlayListAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerControlsPanel;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Responsible of removing play lists
 * @author alex
 *
 */
public class PlayListRemover implements ApplicationContextAware {
	
	private IPlayListHandler playListHandler;
	
	private IPlayListsContainer playListsContainer;
	
	private IPlayerHandler playerHandler;
	
	private IPlayListController playListController;
	
	private IPlayListTabController playListTabController;
	
	private PlayListEventListeners playListEventListeners;
	
	private PlayListInformationInStatusBar playListInformationInStatusBar;

	private IStatePlaylist statePlaylist;
	
	private ApplicationContext context;
	
	/**
	 * @param statePlaylist
	 */
	public void setStatePlaylist(IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.context = applicationContext;
	}
	
	/**
	 * @param playListInformationInStatusBar
	 */
	public void setPlayListInformationInStatusBar(PlayListInformationInStatusBar playListInformationInStatusBar) {
		this.playListInformationInStatusBar = playListInformationInStatusBar;
	}

	/**
	 * @param playListEventListeners
	 */
	public void setPlayListEventListeners(PlayListEventListeners playListEventListeners) {
		this.playListEventListeners = playListEventListeners;
	}
	
	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}
	
	/**
	 * @param playListController
	 */
	public void setPlayListController(IPlayListController playListController) {
		this.playListController = playListController;
	}
	
	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
	
	/**
	 * @param playListsContainer
	 */
	public void setPlayListsContainer(IPlayListsContainer playListsContainer) {
		this.playListsContainer = playListsContainer;
	}
	
	/**
	 * @param playListTabController
	 */
	public void setPlayListTabController(IPlayListTabController playListTabController) {
		this.playListTabController = playListTabController;
	}
	
	/**
	 * Removes play list by given index
	 * @param index
	 * @return
	 */
	public void removePlayList(int index) {
		// If index is not valid, do nothing
		// if there is only one play list, don't delete
		if (validateIndex(index) && moreThanOnePlayList()) {
			switchPlayListIfNeeded(index);
			
			playListsContainer.removePlayList(index);

			adjustVisibleAndActivePlayLists(index);

			// Delete tab
			playListTabController.deletePlayList(index);

			// Refresh table
			playListController.refreshPlayList();

    		playListHandler.playListsChanged();
		}
    }
	
	public void closeCurrentPlaylist() {
        // The current selected play list when this action is fired
        int i = playListTabController.getSelectedPlayListIndex();
        if (i != -1) {
        	// As this action is not called when pressing close button in tab set removeTab argument to true
            removePlayList(i);
        }
    }
    
	public void closeOtherPlaylists() {
        // The current selected play list when this action is fired
        int i = playListTabController.getSelectedPlayListIndex();
        if (i != -1) {
            // Remove play lists from 0 to i. Remove first play list until current play list is at index 0  
            for (int j = 0; j < i; j++) {
            	// As this action is not called when pressing close button in tab set removeTab argument to true
                removePlayList(0);
            }
            // Now current play list is at index 0, so delete from play list size down to 1
            while (playListsContainer.getPlayListsCount() > 1) {
            	// As this action is not called when pressing close button in tab set removeTab argument to true
                removePlayList(playListsContainer.getPlayListsCount() - 1);
            }
        }
    }
    
	/**
	 * @param index
	 */
	private void adjustVisibleAndActivePlayLists(int index) {
		// If index < visiblePlayListIndex, visible play list has been moved to left, so decrease in 1
		if (index < playListsContainer.getVisiblePlayListIndex()) {
			// If active play list visible then decrease in 1 too
			if (playListsContainer.getActivePlayListIndex() == playListsContainer.getVisiblePlayListIndex()) {
				playListsContainer.setActivePlayListIndex(playListsContainer.getActivePlayListIndex() - 1);
			}
			playListsContainer.setVisiblePlayListIndex(playListsContainer.getVisiblePlayListIndex() - 1);
		}
		
		// Removed play list is active, then set visible play list as active and stop player
		if (index == playListsContainer.getActivePlayListIndex()) {
			playListsContainer.setVisiblePlayListActive();
			playerHandler.stopCurrentAudioObject(false);
		}
	}

	/**
	 * @param index
	 */
	private void switchPlayListIfNeeded(int index) {
		if (index == playListsContainer.getVisiblePlayListIndex()) {
			// index == visiblePlayList
			// switch play list and then delete
			if (index == 0) {
				playListHandler.switchToPlaylist(1);
			} else {
				playListHandler.switchToPlaylist(playListsContainer.getVisiblePlayListIndex() - 1);
			}
		}
	}

	/**
	 * @return
	 */
	private boolean moreThanOnePlayList() {
		return playListsContainer.getPlayListsCount() > 1;
	}

	/**
	 * @param index
	 * @return
	 */
	private boolean validateIndex(int index) {
		return index >= 0 && playListsContainer.getPlayListsCount() > index;
	}

	void removeAudioObjects(int[] rows) {
        IPlayList currentPlayList = playListHandler.getVisiblePlayList();
        IAudioObject playingAudioObject = currentPlayList.getCurrentAudioObject();
        boolean hasToBeRemoved = false;
        for (int element : rows) {
            if (element == currentPlayList.getCurrentAudioObjectIndex()) {
                hasToBeRemoved = true;
            }
        }
        for (int i = rows.length - 1; i >= 0; i--) {
            currentPlayList.remove(rows[i]);
        }

        if (hasToBeRemoved) {
            currentAudioObjectHasToBeRemoved(currentPlayList);
        } else {
            currentPlayList.setCurrentAudioObjectIndex(currentPlayList.indexOf(playingAudioObject));
            if (playListHandler.isActivePlayListVisible()) {
            	playListEventListeners.selectedAudioObjectHasChanged(currentPlayList.getCurrentAudioObject());
            }
        }

        playListController.refreshPlayList();

        if (currentPlayList.isEmpty()) {
        	context.getBean(SavePlayListAction.class).setEnabled(false);
        	context.getBean(SaveM3UPlayListAction.class).setEnabled(false);
        	context.getBean(ShufflePlayListAction.class).setEnabled(false);
        }
        playListInformationInStatusBar.showPlayListInformation(currentPlayList);
        Logger.info(StringUtils.getString(rows.length, " objects removed from play list"));
    }

	/**
	 * @param currentPlayList
	 */
	private void currentAudioObjectHasToBeRemoved(IPlayList currentPlayList) {
		// Only stop if this is the active play list
		if (playListHandler.isActivePlayListVisible()) {
		    playerHandler.stopCurrentAudioObject(false);
		}
		if (currentPlayList.isEmpty()) {
	    	playListEventListeners.playListCleared();
		} else {
		    // If current audio object is removed, check if it's necessary to move current audio object (after remove current index is greater than play list size)
		    if (currentPlayList.getCurrentAudioObjectIndex() >= currentPlayList.size()) {
		        currentPlayList.setCurrentAudioObjectIndex(currentPlayList.size() - 1);
		    }
		    if (playListHandler.isActivePlayListVisible()) {
		    	playListEventListeners.selectedAudioObjectHasChanged(currentPlayList.getCurrentAudioObject());
		    }
		}
	}
	
    /**
     * @param audioFiles
     */
    void removeAudioFiles(List<ILocalAudioObject> audioFiles) {
        // Remove these objects from all play lists
    	for (int i = 0; i < playListsContainer.getPlayListsCount(); i++) {
    		playListsContainer.getPlayListAt(i).remove(audioFiles);
        }
        // Update status bar
    	playListInformationInStatusBar.showPlayListInformation(playListHandler.getVisiblePlayList());
    }

	/**
	 * Clear play list 
	 */
	void clearPlayList() {
        // Remove filter
        playListHandler.setFilter(null);

        // Set selection interval to none
        playListController.clearSelection();

        IPlayList playList = playListHandler.getVisiblePlayList();
        if (!playList.isEmpty()) {
            // Clear play list
            playList.clear();

            // Only if this play list is the active stop playback
            if (playListHandler.isActivePlayListVisible() && statePlaylist.isStopPlayerOnPlayListClear()) {
                playerHandler.stopCurrentAudioObject(false);
            }

            // Set first audio object as current
            playList.setCurrentAudioObjectIndex(0);

            // Disable actions
            context.getBean(SavePlayListAction.class).setEnabled(false);
            context.getBean(SaveM3UPlayListAction.class).setEnabled(false);
            context.getBean(ShufflePlayListAction.class).setEnabled(false);

            // Update audio object number
            playListInformationInStatusBar.showPlayListInformation(playList);

            // disable progress slider
            if (!statePlaylist.isStopPlayerOnPlayListClear()) {
            	context.getBean(IPlayerControlsPanel.class).getProgressSlider().setEnabled(false);
            }
            playListController.repaint();

            // Refresh play list
            playListController.refreshPlayList();

            Logger.info("Play list clear");
        }

        // Fire clear event
        // Only if this play list is the active
        if (playListHandler.isActivePlayListVisible()) {
        	playListEventListeners.playListCleared();
        }
    }

}
