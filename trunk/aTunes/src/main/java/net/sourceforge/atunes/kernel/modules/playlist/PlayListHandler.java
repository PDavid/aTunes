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
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.kernel.actions.SavePlayListAction;
import net.sourceforge.atunes.kernel.actions.ShufflePlayListAction;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IArtistAlbumSelectorDialog;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListObjectFilter;
import net.sourceforge.atunes.model.IPlayerControlsPanel;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class PlayListHandler.
 */
public final class PlayListHandler extends AbstractHandler implements IPlayListHandler {

    /** Stores original play list without filter. */
    private IPlayList nonFilteredPlayList;

    /** Play lists stored */
    private IListOfPlayLists playListsRetrievedFromCache;
    
    private IPlayListObjectFilter<ILocalAudioObject> playListLocalAudioObjectFilter;
    
    /** The play list tab controller. */
    private PlayListTabController playListTabController;

	/**
	 * Play list Controller
	 */
	private PlayListController playListController;

	private IColumnSet playListColumnSet;
	
	private IPlayerHandler playerHandler;
	
	private PlayListEventListeners playListEventListeners;

	private IFilter playListFilter;
	
	private IFilterHandler filterHandler;
	
	private IRepositoryHandler repositoryHandler;
	
	private PlayListNameCreator playListNameCreator;
	
	private PlayListPersistor playListPersistor;
	
	private ListOfPlayListsCreator listOfPlayListsCreator;
	
	private PlayListLoader playListLoader;
	
	private IPlayListsContainer playListsContainer;
	
	private PlayListRemover playListRemover;
	
	private IStatePlayer statePlayer;
	
    private IStatePlaylist statePlaylist;
    
    private PlayListCreator playListCreator;
    
    private PlayListInformationInStatusBar playListInformationInStatusBar;
    
    /**
     * @param playListInformationInStatusBar
     */
    public void setPlayListInformationInStatusBar(PlayListInformationInStatusBar playListInformationInStatusBar) {
		this.playListInformationInStatusBar = playListInformationInStatusBar;
	}
    
    /**
     * @param playListCreator
     */
    public void setPlayListCreator(PlayListCreator playListCreator) {
		this.playListCreator = playListCreator;
	}
    
    /**
     * @param statePlaylist
     */
    public void setStatePlaylist(IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}
	
	/**
	 * @param playListRemover
	 */
	public void setPlayListRemover(PlayListRemover playListRemover) {
		this.playListRemover = playListRemover;
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
	public void setPlayListTabController(PlayListTabController playListTabController) {
		this.playListTabController = playListTabController;
	}
	
	/**
	 * @param playListController
	 */
	public void setPlayListController(PlayListController playListController) {
		this.playListController = playListController;
	}
	
	/**
	 * @param playListLoader
	 */
	public void setPlayListLoader(PlayListLoader playListLoader) {
		this.playListLoader = playListLoader;
	}
	
	/**
	 * @param listOfPlayListsCreator
	 */
	public void setListOfPlayListsCreator(ListOfPlayListsCreator listOfPlayListsCreator) {
		this.listOfPlayListsCreator = listOfPlayListsCreator;
	}
	
	/**
	 * @param playListPersistor
	 */
	public void setPlayListPersistor(PlayListPersistor playListPersistor) {
		this.playListPersistor = playListPersistor;
	}
	
	/**
	 * @param playListNameCreator
	 */
	public void setPlayListNameCreator(PlayListNameCreator playListNameCreator) {
		this.playListNameCreator = playListNameCreator;
	}
	
	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
	
	/**
	 * @param filterHandler
	 */
	public void setFilterHandler(IFilterHandler filterHandler) {
		this.filterHandler = filterHandler;
	}
	
	/**
	 * @param playListFilter
	 */
	public void setPlayListFilter(IFilter playListFilter) {
		this.playListFilter = playListFilter;
	}

	/**
	 * @param playListsRetrievedFromCache
	 */
	public void setPlayListsRetrievedFromCache(IListOfPlayLists playListsRetrievedFromCache) {
		this.playListsRetrievedFromCache = playListsRetrievedFromCache;
	}
	
	/**
	 * @param playListLocalAudioObjectFilter
	 */
	public void setPlayListLocalAudioObjectFilter(IPlayListObjectFilter<ILocalAudioObject> playListLocalAudioObjectFilter) {
		this.playListLocalAudioObjectFilter = playListLocalAudioObjectFilter;
	}
	
    /**
     * @param playListColumnSet
     */
    public void setPlayListColumnSet(IColumnSet playListColumnSet) {
		this.playListColumnSet = playListColumnSet;
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
    
    @Override
    protected void initHandler() {
        // Add audio file removed listener
        repositoryHandler.addAudioFilesRemovedListener(this);
    }

    @Override
    public void applicationStarted() {
    	// Playlists need to be loaded before other handlers access them in allHandlersInitialized
        // Get playlists from application cache
        final IListOfPlayLists listOfPlayLists = playListsRetrievedFromCache;

        // Set selected play list as default 
        int selected = listOfPlayLists.getSelectedPlayList();
        if (selected < 0 || selected >= listOfPlayLists.getPlayLists().size()) {
            selected = 0;
        }

        // Add playlists
        playListsContainer.clear();
        for (IPlayList playlist : listOfPlayLists.getPlayLists()) {
        	addNewPlayList(playListNameCreator.getNameForPlaylist(playListsContainer, playlist), playlist);
        }
        // Initially active play list and visible play list are the same
        playListsContainer.setActivePlayListIndex(selected);
        playListsContainer.setVisiblePlayListIndex(selected);
        playListTabController.forceSwitchTo(selected);

        setPlayList(playListsContainer.getPlayListAt(selected));

        // Update table model
        playListController.setVisiblePlayList(getCurrentPlayList(true));

        // Refresh play list
        // For some strange reason, this is needed even if play list is empty
        playListController.refreshPlayList();

        playListsRetrievedFromCache = null;
    }
    
    @Override
    public void allHandlersInitialized() {
        // Create drag and drop listener
    	playListController.enableDragAndDrop();
    }

    /**
     * Sets visible play list as active
     */
    @Override
    public void setVisiblePlayListActive() {
    	playListsContainer.setVisiblePlayListActive();
    }

    @Override
	public void removePlayList(int index) {
    	if (playListRemover.removePlayList(index)) {
    		playListsChanged(true, true);
    	}
    }
    
    @Override
	public int getPlayListCount() {
        return this.playListsContainer.getPlayListsCount();
    }

    @Override
	public void newPlayList(List<IAudioObject> audioObjects) {
        newPlayList(playListNameCreator.getNameForPlaylist(playListsContainer, null), audioObjects);
    }

    @Override
	public void newPlayList(String nameOfNewPlayList, List<? extends IAudioObject> audioObjects) {
        addNewPlayList(nameOfNewPlayList, playListCreator.getNewPlayList(nameOfNewPlayList, audioObjects));
        playListsChanged(true, true);
    }
    
    private void addNewPlayList(String name, IPlayList playList) {
        playListsContainer.addPlayList(playList);
        playListTabController.newPlayList(name);    	
    }

    @Override
	public void renameCurrentVisiblePlayList(String newName) {
        if (newName != null) {
            int index = playListTabController.getSelectedPlayListIndex();
            playListsContainer.getPlayListAt(index).setName(newName);
            playListTabController.renamePlayList(index, newName);
        }
    }
    
    @Override
    public String getCurrentVisiblePlayListName() {
        return playListTabController.getPlayListName(playListTabController.getSelectedPlayListIndex());
    }
    
    @Override
	public void switchToPlaylist(int index) {
        // If play list is the same, do nothing, except if this method is called when deleting a play list
        if (index == playListsContainer.getVisiblePlayListIndex()) {
            return;
        }

        //don't stop player if user has setup the option
        if (statePlaylist.isStopPlayerOnPlayListSwitch()) {
            playerHandler.stopCurrentAudioObject(false);
        }

        // Remove filter from current play list before change play list
        setFilter(null);

        playListsContainer.setVisiblePlayListIndex(index);

        IPlayList newSelectedPlayList = playListsContainer.getPlayListAt(index);

        // Set selection interval to none
        playListController.clearSelection();

        setPlayList(newSelectedPlayList);
        // Update table model
        playListController.setVisiblePlayList(getCurrentPlayList(true));
        playListController.refreshPlayList();

        // If playlist is active then perform an auto scroll
        if (isActivePlayListVisible()) {
            playListController.scrollPlayList(false);
        }
    }

    /**
     * Sets the play list.
     * 
     * @param playList
     *            the new play list
     */
    private void setPlayList(IPlayList playList) {
    	getBean(SavePlayListAction.class).setEnabled(!getCurrentPlayList(true).isEmpty());
        getBean(ShufflePlayListAction.class).setEnabled(!getCurrentPlayList(true).isEmpty());
        playListInformationInStatusBar.showPlayListInformation(playList);
        if (isActivePlayListVisible()) {
        	playListEventListeners.selectedAudioObjectHasChanged(playList.getCurrentAudioObject());
        }
    }

    @Override
	public boolean isVisiblePlayList(int index) {
        return playListsContainer.getVisiblePlayListIndex() == index;
    }

    @Override
	public IAudioObject getCurrentAudioObjectFromVisiblePlayList() {
        if (getCurrentPlayList(true) != null) {
            return getCurrentPlayList(true).getCurrentAudioObject();
        }
        return null;
    }

    @Override
	public IAudioObject getCurrentAudioObjectFromCurrentPlayList() {
        if (getCurrentPlayList(false) != null) {
            return getCurrentPlayList(false).getCurrentAudioObject();
        }
        return null;
    }

    @Override
	public IPlayList getCurrentPlayList(boolean visible) {
    	return playListsContainer.getCurrentPlayList(visible);
    }

    @Override
	public void addToPlayList(List<? extends IAudioObject> audioObjects) {
        addToPlayList(getCurrentPlayList(true).size(), audioObjects, true);
    }

    @Override
	public void addToPlayList(int location, List<? extends IAudioObject> audioObjects, boolean visible) {
        // If null or empty, nothing to do
    	if (CollectionUtils.isEmpty(audioObjects)) {
            return;
        }

        // Get play list
        IPlayList playList = getCurrentPlayList(visible);

        // If play list is filtered remove filter
        if (isFiltered()) {
            setFilter(null);
        }

        int newLocation = location;
        // Add audio objects to play list
        if (location < 0 || location > playList.size()) {
            newLocation = playList.size();
        }
        playList.add(newLocation, audioObjects);

        setSelectedItemAfterAddToPlayList(audioObjects, playList);

        getBean(SavePlayListAction.class).setEnabled(!getCurrentPlayList(true).isEmpty());
        getBean(ShufflePlayListAction.class).setEnabled(!getCurrentPlayList(true).isEmpty());

        // Update play list number in status bar
        playListInformationInStatusBar.showPlayListInformation(playList);

        // Update play list table
        playListController.refreshPlayList();

        Logger.info(StringUtils.getString(audioObjects.size(), " audio objects added to play list"));
    }

	/**
	 * @param audioObjects
	 * @param playList
	 */
	private void setSelectedItemAfterAddToPlayList(List<? extends IAudioObject> audioObjects, IPlayList playList) {
		// If active play list was empty, then set audio object as selected for play
        if (isActivePlayListVisible() && playList.size() == audioObjects.size()) {
            int selectedAudioObject = 0;

            // If stopPlayerWhenPlayListClear property is disabled try to locate audio object in play list. If it's not available then stop playing
            if (!statePlaylist.isStopPlayerOnPlayListClear() && playerHandler.isEnginePlaying()) {
                int index = playList.indexOf(playerHandler.getAudioObject());
                if (index != -1) {
                    selectedAudioObject = index;
                } else {
                    playerHandler.stopCurrentAudioObject(false);
                }
            } else if (statePlayer.isShuffle()) {
                // If shuffle enabled, select a random audio object
                selectedAudioObject = playList.getRandomPosition();
            }

            playListEventListeners.selectedAudioObjectHasChanged(audioObjects.get(selectedAudioObject));
            playList.setCurrentAudioObjectIndex(selectedAudioObject);
        }
	}

    @Override
	public void addToActivePlayList(List<? extends IAudioObject> audioObjects) {
        IPlayList playList = getCurrentPlayList(false);
        addToPlayList(playList.getCurrentAudioObjectIndex() + 1, audioObjects, false);
    }

    @Override
	public void clearPlayList() {
        // Remove filter
        setFilter(null);

        // Set selection interval to none
        playListController.clearSelection();

        IPlayList playList = getCurrentPlayList(true);
        if (!playList.isEmpty()) {
            // Clear play list
            playList.clear();

            // Only if this play list is the active stop playback
            if (isActivePlayListVisible() && statePlaylist.isStopPlayerOnPlayListClear()) {
                playerHandler.stopCurrentAudioObject(false);
            }

            // Set first audio object as current
            playList.setCurrentAudioObjectIndex(0);

            // Disable actions
            getBean(SavePlayListAction.class).setEnabled(false);
            getBean(ShufflePlayListAction.class).setEnabled(false);

            // Update audio object number
            playListInformationInStatusBar.showPlayListInformation(playList);

            // disable progress slider
            if (!statePlaylist.isStopPlayerOnPlayListClear()) {
            	getBean(IPlayerControlsPanel.class).getProgressSlider().setEnabled(false);
            }
            playListController.repaint();

            // Refresh play list
            playListController.refreshPlayList();

            Logger.info("Play list clear");
        }

        // Fire clear event
        // Only if this play list is the active
        if (isActivePlayListVisible()) {
            clear();
        }
    }

    /**
     * Called when play lists needs to be persisted
     */
    private void playListsChanged(boolean definition, boolean contents) {
    	IListOfPlayLists listOfPlayLists = listOfPlayListsCreator.getListOfPlayLists(playListsContainer, isFiltered(), nonFilteredPlayList);
    	playListPersistor.persistPlayLists(listOfPlayLists, definition, contents);
    }

    @Override
    protected Runnable getPreviousInitializationTask() {
        return new PreviousInitializationTaskRunnable(this);
    }

    @Override
	public void loadPlaylist() {
    	playListLoader.loadPlaylist();
    }

    @Override
	public void moveRowTo(int sourceRow, int targetRow) {
        getCurrentPlayList(true).moveRowTo(sourceRow, targetRow);
    }
    
    @Override
	public void moveSelectionAfterCurrentAudioObject() {
        IPlayList currentPlayList = getCurrentPlayList(true);
        List<IAudioObject> selectedAudioObjects = getSelectedAudioObjects();
        
        //Recurse backwards to move the elements to the correct position
        Collections.reverse(selectedAudioObjects);
        
        int beginNewPosition = getCurrentAudioObjectIndexInVisiblePlayList();
        int endNewPosition = getCurrentAudioObjectIndexInVisiblePlayList();
        for (int i = 0; i < selectedAudioObjects.size(); i++) {
        	IAudioObject o = selectedAudioObjects.get(i);
        	int currentIndex = getCurrentAudioObjectIndexInVisiblePlayList();
        	int sourceIndex = currentPlayList.indexOf(o);

        	//Workaround otherwise file is put before current playing
        	if (sourceIndex > currentIndex) {
        		currentIndex++;
        	}
        	currentPlayList.moveRowTo(sourceIndex, currentIndex);

        	// Get min and max indexes to set selected
        	beginNewPosition = Math.min(currentIndex, beginNewPosition);
        	endNewPosition = Math.max(currentIndex, endNewPosition);
        }
        
        refreshPlayList();
        
        // Keep selected elements
        playListController.setSelectionInterval(getCurrentAudioObjectIndexInVisiblePlayList() + 1, getCurrentAudioObjectIndexInVisiblePlayList() + selectedAudioObjects.size());
    }

    @Override
	public void playNow(IAudioObject audioObject) {
        if (!getCurrentPlayList(true).contains(audioObject)) {
            List<IAudioObject> list = new ArrayList<IAudioObject>();
            list.add(audioObject);
            addToPlayListAndPlay(list);
        } else {
            setPositionToPlayInVisiblePlayList(getCurrentPlayList(true).indexOf(audioObject));
            playerHandler.playCurrentAudioObject(false);
        }
    }

    @Override
	public void removeAudioObjects(int[] rows) {
        IPlayList currentPlayList = getCurrentPlayList(true);
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
            if (isActivePlayListVisible()) {
            	playListEventListeners.selectedAudioObjectHasChanged(currentPlayList.getCurrentAudioObject());
            }
        }

        playListController.refreshPlayList();

        if (currentPlayList.isEmpty()) {
            getBean(SavePlayListAction.class).setEnabled(false);
            getBean(ShufflePlayListAction.class).setEnabled(false);
        }
        playListInformationInStatusBar.showPlayListInformation(currentPlayList);
        Logger.info(StringUtils.getString(rows.length, " objects removed from play list"));
    }

	/**
	 * @param currentPlayList
	 */
	private void currentAudioObjectHasToBeRemoved(IPlayList currentPlayList) {
		// Only stop if this is the active play list
		if (isActivePlayListVisible()) {
		    playerHandler.stopCurrentAudioObject(false);
		}
		if (currentPlayList.isEmpty()) {
		    clear();
		} else {
		    // If current audio object is removed, check if it's necessary to move current audio object (after remove current index is greater than play list size)
		    if (currentPlayList.getCurrentAudioObjectIndex() >= currentPlayList.size()) {
		        currentPlayList.setCurrentAudioObjectIndex(currentPlayList.size() - 1);
		    }
		    if (isActivePlayListVisible()) {
		    	playListEventListeners.selectedAudioObjectHasChanged(currentPlayList.getCurrentAudioObject());
		    }
		}
	}

    /**
     * Called when play list is cleared. Calls to all PlayListEventListener
     */
    private void clear() {
    	playListEventListeners.playListCleared();
    }

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {
        addToPlaybackHistory(audioObject);
        playListController.updatePlayList();
        playListController.scrollPlayList(false);
        playListsChanged(true, false);
	};

    @Override
	public void shuffle() {
        IPlayList currentPlaylist = getCurrentPlayList(true);

        // If current play list is empty, don't shuffle
        if (currentPlaylist.isEmpty()) {
            return;
        }

        currentPlaylist.shuffle();
    }

    @Override
	public boolean isActivePlayListVisible() {
        return getCurrentPlayList(true) == getCurrentPlayList(false);
    }

    @Override
	public void addToPlayListAndPlay(List<IAudioObject> audioObjects) {
        if (audioObjects == null || audioObjects.isEmpty()) {
            return;
        }

        int playListCurrentSize = getCurrentPlayList(true).size();
        addToPlayList(audioObjects);
        setPositionToPlayInVisiblePlayList(playListCurrentSize);
        playerHandler.playCurrentAudioObject(false);
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
        playListsContainer.removePlayList(playListsContainer.getVisiblePlayListIndex());
        playListsContainer.addPlayList(playListsContainer.getVisiblePlayListIndex(), playList);

        // Set selection interval to none
        playListController.clearSelection();

        setPlayList(playList);

        // Update table model
        playListController.setVisiblePlayList(playList);
        playListController.refreshPlayList();

        playListController.scrollPlayList(false);
    }

    @Override
	public final void setPositionToPlayInVisiblePlayList(int pos) {
        getCurrentPlayList(true).setCurrentAudioObjectIndex(pos);
    }

    @Override
	public final void resetCurrentPlayList() {
        getCurrentPlayList(false).reset();
    }

    @Override
	public IAudioObject getNextAudioObject() {
        return getCurrentPlayList(false).moveToNextAudioObject();
    }

    @Override
	public IAudioObject getPreviousAudioObject() {
        return getCurrentPlayList(false).moveToPreviousAudioObject();
    }

    @Override
	public int getIndexOfAudioObject(IAudioObject aObject) {
        return getCurrentPlayList(false).indexOf(aObject);
    }

    @Override
	public IAudioObject getAudioObjectAtIndex(int index) {
        return getCurrentPlayList(false).getNextAudioObject(index);
    }

    @Override
	public void changeSelectedAudioObjectToIndex(int index) {
        playListController.changeSelectedAudioObjectToIndex(index);
    }

    @Override
	public List<IAudioObject> getSelectedAudioObjects() {
        List<IAudioObject> audioObjects = new ArrayList<IAudioObject>();
        int[] selectedRows = playListController.getSelectedRows();
        if (selectedRows.length > 0) {
            for (int element : selectedRows) {
                IAudioObject file = getCurrentPlayList(true).get(element);
                if (file != null) {
                    audioObjects.add(file);
                }
            }
        }
        return audioObjects;
    }
    
    @Override
	public boolean isCurrentVisibleRowPlaying(int row) {
        return getCurrentPlayList(true).getCurrentAudioObjectIndex() == row && isActivePlayListVisible();
    }

    @Override
	public int getCurrentAudioObjectIndexInVisiblePlayList() {
        return getCurrentPlayList(true).getCurrentAudioObjectIndex();
    }

    @Override
	public void addToPlaybackHistory(IAudioObject object) {
        getCurrentPlayList(false).addToPlaybackHistory(object);
        Logger.debug("Added to history: ", object.getTitle());
    }

    @Override
    public void audioFilesRemoved(List<ILocalAudioObject> audioFiles) {
        // Remove these objects from all play lists
    	for (int i = 0; i < playListsContainer.getPlayListsCount(); i++) {
    		playListsContainer.getPlayListAt(i).remove(audioFiles);
        }
        // Update status bar
    	playListInformationInStatusBar.showPlayListInformation(getCurrentPlayList(true));
    }

    @Override
    public void applicationStateChanged() {
        if (statePlaylist.isAutoScrollPlayListEnabled()) {
            playListController.scrollPlayList(true);
        }
    }

    @Override
	public void movePlaylistToPosition(int from, int to) {
    	playListsContainer.movePlayListToPosition(from, to);
    }

    @Override
	public String getPlayListNameAtIndex(int index) {
        return playListsContainer.getPlayListAt(index).getName();
    }

    @Override
	public List<IAudioObject> getPlayListContent(int index) {
        if (index >= this.playListsContainer.getPlayListsCount() || index < 0) {
            throw new IllegalArgumentException(new StringBuilder().append("Invalid play list index ").append(index).toString());
        }
        return playListsContainer.getPlayListAt(index).getAudioObjectsList();
    }
    
    @Override
	public void closeCurrentPlaylist() {
        // The current selected play list when this action is fired
        int i = playListTabController.getSelectedPlayListIndex();
        if (i != -1) {
        	// As this action is not called when pressing close button in tab set removeTab argument to true
            removePlayList(i);
        }
    }
    
    @Override
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
            while (getPlayListCount() > 1) {
            	// As this action is not called when pressing close button in tab set removeTab argument to true
                removePlayList(getPlayListCount() - 1);
            }
        }
    }

	@Override
	public void scrollPlayList(boolean isUserAction) {
		playListController.scrollPlayList(isUserAction);		
	}

	@Override
	public void refreshPlayList() {
		playListController.refreshPlayList();		
	}

	@Override
	public void moveDown() {
		playListController.moveDown(getCurrentPlayList(true));		
	}

	@Override
	public void moveToBottom() {
		playListController.moveToBottom(getCurrentPlayList(true));
	}

	@Override
	public void moveToTop() {
		playListController.moveToTop(getCurrentPlayList(true));
	}

	@Override
	public void moveUp() {
		playListController.moveUp(getCurrentPlayList(true));		
	}

	@Override
	public void deleteSelection() {
		playListController.deleteSelection();
	}

	@Override
	public void audioObjectsAdded(List<IPlayListAudioObject> audioObjectsAdded) {
		playListsChanged(true, true);
	}
	
	@Override
	public void audioObjectsRemoved(List<IPlayListAudioObject> audioObjectsRemoved) {		
		playListsChanged(true, true);
	}
	
	@Override
	public void reapplyFilter() {
        if (isFiltered()) {
            setFilter(filterHandler.getFilterText(playListFilter));
        }
	}

	@Override
	public void playListCleared() {
		playListsChanged(true, true);
	}
	
	@Override
	public void favoritesChanged() {
        // Update playlist to remove favorite icon
        refreshPlayList();
	}
	
	@Override
	public void deviceDisconnected(String location) {
        List<Integer> songsToRemove = new ArrayList<Integer>();
        for (ILocalAudioObject audioFile : playListLocalAudioObjectFilter.getObjects(getCurrentPlayList(true))) {
            if (audioFile.getFile().getPath().startsWith(location)) {
                songsToRemove.add(getCurrentPlayList(true).indexOf(audioFile));
            }
        }
        int[] indexes = new int[songsToRemove.size()];
        for (int i = 0; i < songsToRemove.size(); i++) {
            indexes[i] = songsToRemove.get(i);
        }

        if (indexes.length > 0) {
            playListController.clearSelection();
            removeAudioObjects(indexes);
        }
	}
	
    @Override
	public void showAddArtistDragDialog(IArtist currentArtist) {
    	IArtistAlbumSelectorDialog dialog = getBean(IArtistAlbumSelectorDialog.class);
    	IAlbum album = dialog.showDialog(currentArtist);
    	if (album != null) {
    		addToPlayList(album.getAudioObjects());
    	}
    }
    
    @Override
    public void windowDeiconified() {
		scrollPlayList(false);
    }
}
