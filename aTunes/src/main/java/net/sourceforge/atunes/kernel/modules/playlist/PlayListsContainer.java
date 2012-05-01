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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.utils.StringUtils;

public final class PlayListsContainer implements IPlayListsContainer {

    /** The play lists currently opened. */
    private List<IPlayList> playLists = new ArrayList<IPlayList>();
    
    /** Stores original play list without filter. */
    private IPlayList nonFilteredPlayList;

    /** Index of the active play list */
    private int activePlayListIndex = 0;

    /** Index of the visible play list: can be different of active play list */
    private int visiblePlayListIndex = 0;
    
    private IStatePlayer statePlayer;
    
    private PlayListHandler playListHandler;
    
    private IPlayListController playListController;
    
	private IColumnSet playListColumnSet;
	
	/**
	 * @param playListColumnSet
	 */
	public void setPlayListColumnSet(IColumnSet playListColumnSet) {
		this.playListColumnSet = playListColumnSet;
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
    public void setPlayListHandler(PlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    /**
     * @param statePlayer
     */
    public void setStatePlayer(IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	/**
	 * Sets visible play list as active
	 */
	@Override
	public void setVisiblePlayListActive() {
        activePlayListIndex = visiblePlayListIndex;
	}
	
	/**
	 * Returns number of play lists
	 * @return
	 */
	@Override
	public int getPlayListsCount() {
		return playLists.size();
	}
	
	/**
	 * Index of visible play list
	 * @return
	 */
	@Override
	public int getVisiblePlayListIndex() {
		return visiblePlayListIndex;
	}
	
	/**
	 * Index of active play list
	 * @return
	 */
	@Override
	public int getActivePlayListIndex() {
		return activePlayListIndex;
	}
	
	/**
	 * @param activePlayListIndex
	 */
	@Override
	public void setActivePlayListIndex(int activePlayListIndex) {
		if (activePlayListIndex < 0 || activePlayListIndex >= playLists.size()) {
			throw new IllegalArgumentException(StringUtils.getString("Wrong activePlayListIndex: ", activePlayListIndex));
		}
		this.activePlayListIndex = activePlayListIndex;
	}
	
	/**
	 * @param visiblePlayListIndex
	 */
	@Override
	public void setVisiblePlayListIndex(int visiblePlayListIndex) {
		if (visiblePlayListIndex < 0 || visiblePlayListIndex >= playLists.size()) {
			throw new IllegalArgumentException(StringUtils.getString("Wrong visiblePlayListIndex: ", visiblePlayListIndex));
		}
		this.visiblePlayListIndex = visiblePlayListIndex;
	}

	/**
	 * Removes play list of given index
	 * @param index
	 */
	@Override
	public void removePlayList(int index) {
		playLists.remove(index);
	}

	/**
	 * Returns play list at index
	 * @param i
	 * @return
	 */
	@Override
	public IPlayList getPlayListAt(int i) {
		if (i < 0 || i >= playLists.size()) {
			throw new IllegalArgumentException(StringUtils.getString("Wrong index ", i));
		}
		return playLists.get(i);
	}

	/**
	 * Adds play list
	 * @param newPlayList
	 */
	@Override
	public void addPlayList(IPlayList newPlayList) {
        playLists.add(newPlayList);
	}

	/**
	 * Returns visible or active play list
	 * @param visible
	 */
	@Override
	public IPlayList getCurrentPlayList(boolean visible) {
        if (getPlayListsCount() == 0) {
            return null;
        }

        if (visible) {
            return playLists.get(visiblePlayListIndex);
        }
        return playLists.get(activePlayListIndex);
	}

	/**
	 * Clear play lists 
	 */
	@Override
	public void clear() {
		playLists.clear();
	}

	/**
	 * Adds play list at given position
	 * @param position
	 * @param playList
	 */
	@Override
	public void addPlayList(int position, IPlayList playList) {
		playLists.add(position, playList);
	}

	@Override
	public IPlayList getNonFilteredPlayList() {
		return nonFilteredPlayList;
	}
	
    @Override
	public boolean isFiltered() {
        return nonFilteredPlayList != null;
    }

    @Override
    public void setFilter(String filter) {
        String filterText = filter;

        // If filter is null, remove previous filter
        if (filterText == null) {
            // If play list was filtered, back to non-filtered play list
            if (nonFilteredPlayList != null) {
                setPlayListAfterFiltering(nonFilteredPlayList);
                nonFilteredPlayList = null;
            }
        } else {
            // Store original play list without filter
            if (nonFilteredPlayList == null) {
				nonFilteredPlayList = getCurrentPlayList(true).copyPlayList();
            }

            // Create a new play list by filtering elements
            PlayList newPlayList = new PlayList(playListColumnSet.filterAudioObjects(nonFilteredPlayList.getAudioObjectsList(), filterText), statePlayer);
            setPlayListAfterFiltering(newPlayList);
        }
    }

    /**
     * Sets the play list after filtering.
     * 
     * @param playList
     *            the new play list after filtering
     */
    private void setPlayListAfterFiltering(IPlayList playList) {
        removePlayList(getVisiblePlayListIndex());
        addPlayList(getVisiblePlayListIndex(), playList);

        // Set selection interval to none
        playListController.clearSelection();

        playListHandler.setPlayList(playList);

        // Update table model
        playListController.setVisiblePlayList(playList);
        playListController.refreshPlayList();

        playListController.scrollPlayList(false);
    }

}
