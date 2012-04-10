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

import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;

/**
 * Responsible of removing play lists
 * @author alex
 *
 */
public class PlayListRemover {
	
	private IPlayListHandler playListHandler;
	
	private IPlayListsContainer playListsContainer;
	
	private IPlayerHandler playerHandler;
	
	private IPlayListController playListController;
	
	private IPlayListTabController playListTabController;
	
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
	public boolean removePlayList(int index) {
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

			return true;
		}
		return false;
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

}
