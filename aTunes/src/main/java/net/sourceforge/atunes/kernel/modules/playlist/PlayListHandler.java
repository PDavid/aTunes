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

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.kernel.actions.SaveM3UPlayListAction;
import net.sourceforge.atunes.kernel.actions.SavePlayListAction;
import net.sourceforge.atunes.kernel.actions.ShufflePlayListAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
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

	/** Play lists stored */
	private IListOfPlayLists playListsRetrievedFromCache = new ListOfPlayLists();

	/** The play list tab controller. */
	private PlayListTabController playListTabController;

	/**
	 * Play list Controller
	 */
	private PlayListController playListController;

	private IPlayerHandler playerHandler;

	private PlayListEventListeners playListEventListeners;

	private IFilter playListFilter;

	private IFilterHandler filterHandler;

	private PlayListNameCreator playListNameCreator;

	private PlayListPersistor playListPersistor;

	private ListOfPlayListsCreator listOfPlayListsCreator;

	private IPlayListsContainer playListsContainer;

	private IStatePlayer statePlayer;

	private IStatePlaylist statePlaylist;

	private PlayListCreator playListCreator;

	private PlayListInformationInStatusBar playListInformationInStatusBar;

	private IContextHandler contextHandler;

	/**
	 * @param contextHandler
	 */
	public void setContextHandler(final IContextHandler contextHandler) {
		this.contextHandler = contextHandler;
	}

	/**
	 * @param playListInformationInStatusBar
	 */
	public void setPlayListInformationInStatusBar(final PlayListInformationInStatusBar playListInformationInStatusBar) {
		this.playListInformationInStatusBar = playListInformationInStatusBar;
	}

	/**
	 * @param playListCreator
	 */
	public void setPlayListCreator(final PlayListCreator playListCreator) {
		this.playListCreator = playListCreator;
	}

	/**
	 * @param statePlaylist
	 */
	public void setStatePlaylist(final IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(final IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	/**
	 * @param playListsContainer
	 */
	public void setPlayListsContainer(final IPlayListsContainer playListsContainer) {
		this.playListsContainer = playListsContainer;
	}

	/**
	 * @param playListTabController
	 */
	public void setPlayListTabController(final PlayListTabController playListTabController) {
		this.playListTabController = playListTabController;
	}

	/**
	 * @param playListController
	 */
	public void setPlayListController(final PlayListController playListController) {
		this.playListController = playListController;
	}

	/**
	 * @param listOfPlayListsCreator
	 */
	public void setListOfPlayListsCreator(final ListOfPlayListsCreator listOfPlayListsCreator) {
		this.listOfPlayListsCreator = listOfPlayListsCreator;
	}

	/**
	 * @param playListPersistor
	 */
	public void setPlayListPersistor(final PlayListPersistor playListPersistor) {
		this.playListPersistor = playListPersistor;
	}

	/**
	 * @param playListNameCreator
	 */
	public void setPlayListNameCreator(final PlayListNameCreator playListNameCreator) {
		this.playListNameCreator = playListNameCreator;
	}

	/**
	 * @param filterHandler
	 */
	public void setFilterHandler(final IFilterHandler filterHandler) {
		this.filterHandler = filterHandler;
	}

	/**
	 * @param playListFilter
	 */
	public void setPlayListFilter(final IFilter playListFilter) {
		this.playListFilter = playListFilter;
	}

	/**
	 * @param playListsRetrievedFromCache
	 */
	public void setPlayListsRetrievedFromCache(final IListOfPlayLists playListsRetrievedFromCache) {
		this.playListsRetrievedFromCache = playListsRetrievedFromCache;
	}

	/**
	 * @param playListEventListeners
	 */
	public void setPlayListEventListeners(final PlayListEventListeners playListEventListeners) {
		this.playListEventListeners = playListEventListeners;
	}

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	@Override
	protected void initHandler() {
		// Add audio file removed listener
		getBean(IRepositoryHandler.class).addAudioFilesRemovedListener(this);
	}

	@Override
	public void applicationStarted() {
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
	public int getPlayListCount() {
		return this.playListsContainer.getPlayListsCount();
	}

	@Override
	public void newPlayList(final List<IAudioObject> audioObjects) {
		newPlayList(playListNameCreator.getNameForPlaylist(playListsContainer, null), audioObjects);
	}

	@Override
	public void newPlayList(final String nameOfNewPlayList, final List<? extends IAudioObject> audioObjects) {
		addNewPlayList(nameOfNewPlayList, playListCreator.getNewPlayList(nameOfNewPlayList, audioObjects));
		playListsChanged();
	}

	private void addNewPlayList(final String name, final IPlayList playList) {
		playListsContainer.addPlayList(playList);
		playListTabController.newPlayList(name);
	}

	@Override
	public void renameCurrentVisiblePlayList(final String newName) {
		if (newName != null) {
			int index = playListTabController.getSelectedPlayListIndex();
			playListsContainer.getPlayListAt(index).setName(newName);
			playListTabController.renamePlayList(index, newName);
			playListsChanged();
		}
	}

	@Override
	public String getCurrentVisiblePlayListName() {
		return playListTabController.getPlayListName(playListTabController.getSelectedPlayListIndex());
	}

	@Override
	public void switchToPlaylist(final int index) {
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
	}

	/**
	 * Sets the play list.
	 * 
	 * @param playList
	 *            the new play list
	 */
	void setPlayList(final IPlayList playList) {
		getBean(SavePlayListAction.class).setEnabled(!getVisiblePlayList().isEmpty());
		getBean(SaveM3UPlayListAction.class).setEnabled(!getVisiblePlayList().isEmpty());
		getBean(ShufflePlayListAction.class).setEnabled(!getVisiblePlayList().isEmpty());
		playListInformationInStatusBar.showPlayListInformation(playList);

		// Update table model
		playListController.setVisiblePlayList(getVisiblePlayList());
		playListController.refreshPlayList();

		// If playlist is active then perform an auto scroll
		if (isActivePlayListVisible()) {
			playListController.scrollPlayList(false);
		}
	}

	@Override
	public boolean isVisiblePlayList(final int index) {
		return playListsContainer.getVisiblePlayListIndex() == index;
	}

	@Override
	public IAudioObject getCurrentAudioObjectFromVisiblePlayList() {
		if (getVisiblePlayList() != null) {
			return getVisiblePlayList().getCurrentAudioObject();
		}
		return null;
	}

	@Override
	public IAudioObject getCurrentAudioObjectFromCurrentPlayList() {
		if (getActivePlayList() != null) {
			return getActivePlayList().getCurrentAudioObject();
		}
		return null;
	}

	@Override
	public IPlayList getActivePlayList() {
		return playListsContainer.getActivePlayList();
	}

	@Override
	public IPlayList getVisiblePlayList() {
		return playListsContainer.getVisiblePlayList();
	}

	@Override
	public void addToVisiblePlayList(final List<? extends IAudioObject> audioObjects) {
		addToPlayList(getVisiblePlayList().size(), audioObjects, getVisiblePlayList());
	}

	@Override
	public void addToVisiblePlayList(final int location, final List<? extends IAudioObject> audioObjects) {
		addToPlayList(location, audioObjects, getVisiblePlayList());
	}

	@Override
	public void addToActivePlayList(final List<? extends IAudioObject> audioObjects) {
		addToPlayList(getActivePlayList().size(), audioObjects, getActivePlayList());
	}

	@Override
	public void addToActivePlayList(final int location, final List<? extends IAudioObject> audioObjects) {
		addToPlayList(location, audioObjects, getActivePlayList());
	}

	private void addToPlayList(final int location, final List<? extends IAudioObject> audioObjects, final IPlayList playList) {
		// If null or empty, nothing to do
		if (CollectionUtils.isEmpty(audioObjects)) {
			return;
		}

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

		getBean(SavePlayListAction.class).setEnabled(!getVisiblePlayList().isEmpty());
		getBean(SaveM3UPlayListAction.class).setEnabled(!getVisiblePlayList().isEmpty());
		getBean(ShufflePlayListAction.class).setEnabled(!getVisiblePlayList().isEmpty());

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
	private void setSelectedItemAfterAddToPlayList(final List<? extends IAudioObject> audioObjects, final IPlayList playList) {
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
	public void clearPlayList() {
		getBean(PlayListRemover.class).clearPlayList();
	}

	/**
	 * Called when play lists needs to be persisted
	 */
	@Override
	public void playListsChanged() {
		playListPersistor.persistPlayLists(listOfPlayListsCreator.getListOfPlayLists(playListsContainer));
	}

	@Override
	public void moveRowTo(final int sourceRow, final int targetRow) {
		getVisiblePlayList().moveRowTo(sourceRow, targetRow);
	}

	@Override
	public void setSelectionInterval(final int start, final int end) {
		playListController.setSelectionInterval(start, end);
	}

	@Override
	public void removeAudioObjects(final int[] rows) {
		getBean(PlayListRemover.class).removeAudioObjects(rows);
	}

	@Override
	public void selectedAudioObjectChanged(final IAudioObject audioObject) {
		addToPlaybackHistory(audioObject);
		playListController.updatePlayList();
		playListController.scrollPlayList(false);
		playListsChanged();
	};

	@Override
	public void shuffle() {
		IPlayList currentPlaylist = getVisiblePlayList();

		// If current play list is empty, don't shuffle
		if (currentPlaylist.isEmpty()) {
			return;
		}

		currentPlaylist.shuffle();
	}

	@Override
	public boolean isActivePlayListVisible() {
		return getVisiblePlayList() == getActivePlayList();
	}

	@Override
	public boolean isFiltered() {
		return playListsContainer.isFiltered();
	}

	@Override
	public void setFilter(final String filter) {
		playListsContainer.setFilter(filter);
	}

	@Override
	public final void setPositionToPlayInVisiblePlayList(final int pos) {
	}

	@Override
	public final void resetCurrentPlayList() {
		getActivePlayList().reset();
	}

	@Override
	public IAudioObject getNextAudioObject() {
		return getActivePlayList().moveToNextAudioObject();
	}

	@Override
	public IAudioObject getPreviousAudioObject() {
		return getActivePlayList().moveToPreviousAudioObject();
	}

	@Override
	public int getIndexOfAudioObject(final IAudioObject aObject) {
		return getActivePlayList().indexOf(aObject);
	}

	@Override
	public IAudioObject getAudioObjectAtIndex(final int index) {
		return getActivePlayList().getNextAudioObject(index);
	}

	@Override
	public void changeSelectedAudioObjectToIndex(final int index) {
		playListController.changeSelectedAudioObjectToIndex(index);
	}

	@Override
	public List<IAudioObject> getSelectedAudioObjects() {
		List<IAudioObject> audioObjects = new ArrayList<IAudioObject>();
		int[] selectedRows = playListController.getSelectedRows();
		if (selectedRows.length > 0) {
			for (int element : selectedRows) {
				IAudioObject file = getVisiblePlayList().get(element);
				if (file != null) {
					audioObjects.add(file);
				}
			}
		}
		return audioObjects;
	}

	@Override
	public boolean isCurrentVisibleRowPlaying(final int row) {
		return getVisiblePlayList().getCurrentAudioObjectIndex() == row && isActivePlayListVisible();
	}

	@Override
	public int getCurrentAudioObjectIndexInVisiblePlayList() {
		return getVisiblePlayList().getCurrentAudioObjectIndex();
	}

	@Override
	public void addToPlaybackHistory(final IAudioObject object) {
		getActivePlayList().addToPlaybackHistory(object);
		Logger.debug("Added to history: ", object.getTitle());
	}

	@Override
	public void audioFilesRemoved(final List<ILocalAudioObject> audioFiles) {
		getBean(PlayListRemover.class).removeAudioFiles(audioFiles);
	}

	@Override
	public void applicationStateChanged() {
		if (statePlaylist.isAutoScrollPlayListEnabled()) {
			playListController.scrollPlayList(true);
		}
		playListTabController.showPlayListSelectorComboBox();
	}

	@Override
	public String getPlayListNameAtIndex(final int index) {
		return playListsContainer.getPlayListAt(index).getName();
	}

	@Override
	public List<IAudioObject> getPlayListContent(final int index) {
		if (index >= this.playListsContainer.getPlayListsCount() || index < 0) {
			throw new IllegalArgumentException(new StringBuilder().append("Invalid play list index ").append(index).toString());
		}
		return playListsContainer.getPlayListAt(index).getAudioObjectsList();
	}

	@Override
	public void closeCurrentPlaylist() {
		getBean(PlayListRemover.class).closeCurrentPlaylist();
	}

	@Override
	public void closeOtherPlaylists() {
		getBean(PlayListRemover.class).closeOtherPlaylists();
	}

	@Override
	public void scrollPlayList(final boolean isUserAction) {
		playListController.scrollPlayList(isUserAction);
	}

	@Override
	public void refreshPlayList() {
		playListController.refreshPlayList();
	}

	@Override
	public void moveDown() {
		playListController.moveDown(getVisiblePlayList());
	}

	@Override
	public void moveToBottom() {
		playListController.moveToBottom(getVisiblePlayList());
	}

	@Override
	public void moveToTop() {
		playListController.moveToTop(getVisiblePlayList());
	}

	@Override
	public void moveUp() {
		playListController.moveUp(getVisiblePlayList());
	}

	@Override
	public void deleteSelection() {
		playListController.deleteSelection();
	}

	@Override
	public void audioObjectsAdded(final List<IPlayListAudioObject> audioObjectsAdded) {
		playListsChanged();
	}

	@Override
	public void audioObjectsRemoved(final List<IPlayListAudioObject> audioObjectsRemoved) {
		playListsChanged();
	}

	@Override
	public void reapplyFilter() {
		if (isFiltered()) {
			setFilter(filterHandler.getFilterText(playListFilter));
		}
	}

	@Override
	public void playListCleared() {
		playListsChanged();
	}

	@Override
	public void favoritesChanged() {
		// Update playlist to remove favorite icon
		refreshPlayList();
	}

	@Override
	public void deviceDisconnected(final String location) {
		getBean(PlayListRemoverFromDevice.class).removeAudioObjectsOfDevice(getVisiblePlayList(), location);
	}

	@Override
	public void windowDeiconified() {
		scrollPlayList(false);
	}

	@Override
	public void deferredInitialization() {
		// update information in context panel
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				contextHandler.retrieveInfoAndShowInPanel(getCurrentAudioObjectFromCurrentPlayList());
			}
		});
	}

	/**
	 * Called after initialization task completed
	 */
	void initializationTaskCompleted() {
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

		playListsRetrievedFromCache = null;
	}
}
