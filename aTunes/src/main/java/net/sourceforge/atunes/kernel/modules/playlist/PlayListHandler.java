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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.model.IListMessageDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.IPlayListEventListener;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListIOService;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class PlayListHandler.
 */
public final class PlayListHandler extends AbstractHandler implements
		IPlayListHandler, IPlayListEventListener {

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

	private IDialogFactory dialogFactory;

	private ISearchHandler searchHandler;

	/**
	 * @param searchHandler
	 */
	public void setSearchHandler(final ISearchHandler searchHandler) {
		this.searchHandler = searchHandler;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param contextHandler
	 */
	public void setContextHandler(final IContextHandler contextHandler) {
		this.contextHandler = contextHandler;
	}

	/**
	 * @param playListInformationInStatusBar
	 */
	public void setPlayListInformationInStatusBar(
			final PlayListInformationInStatusBar playListInformationInStatusBar) {
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
	public void setPlayListsContainer(
			final IPlayListsContainer playListsContainer) {
		this.playListsContainer = playListsContainer;
	}

	/**
	 * @param playListTabController
	 */
	public void setPlayListTabController(
			final PlayListTabController playListTabController) {
		this.playListTabController = playListTabController;
	}

	/**
	 * @param playListController
	 */
	public void setPlayListController(
			final PlayListController playListController) {
		this.playListController = playListController;
	}

	/**
	 * @param listOfPlayListsCreator
	 */
	public void setListOfPlayListsCreator(
			final ListOfPlayListsCreator listOfPlayListsCreator) {
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
	public void setPlayListNameCreator(
			final PlayListNameCreator playListNameCreator) {
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
	 * @param playListEventListeners
	 */
	public void setPlayListEventListeners(
			final PlayListEventListeners playListEventListeners) {
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
		this.playListController.enableDragAndDrop();
	}

	/**
	 * Sets visible play list as active
	 */
	@Override
	public void setVisiblePlayListActive() {
		this.playListsContainer.setVisiblePlayListActive();
	}

	@Override
	public int getPlayListCount() {
		return this.playListsContainer.getPlayListsCount();
	}

	@Override
	public void newPlayList(final List<IAudioObject> audioObjects) {
		newPlayList(this.playListNameCreator.getNameForPlaylist(
				this.playListsContainer, null), audioObjects);
	}

	@Override
	public void newPlayList(final String nameOfNewPlayList,
			final List<? extends IAudioObject> audioObjects) {
		addNewPlayList(this.playListCreator.getNewPlayList(nameOfNewPlayList,
				audioObjects));
		playListsChanged();
	}

	@Override
	public void newDynamicPlayList(final ISearchNode query,
			final Collection<IAudioObject> initialObjects) {
		String name = query.toString();

		addNewPlayList(this.playListCreator.getNewDynamicPlayList(name, query,
				initialObjects, 0, null));
		playListsChanged();
	}

	void addNewPlayList(final IPlayList playList) {
		// Each play list added to container must have its listeners, state
		// player and mode
		playList.setStatePlayer(this.statePlayer);
		playList.setMode(PlayListMode.getPlayListMode(playList,
				this.statePlayer));
		playList.setPlayListEventListeners(this.playListEventListeners);

		this.playListsContainer.addPlayList(playList);
		this.playListTabController.newPlayList(this.playListNameCreator
				.getNameForPlaylist(this.playListsContainer, playList),
				getBean(PlayListIconSelector.class).getIcon(playList));
	}

	@Override
	public void renameCurrentVisiblePlayList(final String newName) {
		if (newName != null) {
			int index = this.playListTabController.getSelectedPlayListIndex();
			this.playListsContainer.getPlayListAt(index).setName(newName);
			this.playListTabController.renamePlayList(index, newName);
			playListsChanged();
		}
	}

	@Override
	public String getCurrentVisiblePlayListName() {
		return this.playListTabController
				.getPlayListName(this.playListTabController
						.getSelectedPlayListIndex());
	}

	@Override
	public void nextPlayList() {
		int currentIndex = this.playListsContainer.getVisiblePlayListIndex();
		if (currentIndex < (getPlayListCount() - 1)) {
			this.playListTabController.forceSwitchTo(currentIndex + 1);
		} else {
			this.playListTabController.forceSwitchTo(0);
		}
	}

	@Override
	public void previousPlayList() {
		int currentIndex = this.playListsContainer.getVisiblePlayListIndex();
		if (currentIndex == 0) {
			this.playListTabController.forceSwitchTo(getPlayListCount() - 1);
		} else {
			this.playListTabController.forceSwitchTo(currentIndex - 1);
		}
	}

	@Override
	public void switchToPlaylist(final int index) {
		// If play list is the same, do nothing, except if this method is called
		// when deleting a play list
		if (index == this.playListsContainer.getVisiblePlayListIndex()) {
			return;
		}

		// don't stop player if user has setup the option
		if (this.statePlaylist.isStopPlayerOnPlayListSwitch()) {
			this.playerHandler.stopCurrentAudioObject(false);
		}

		// Remove filter from current play list before change play list
		setFilter(null);

		this.playListsContainer.setVisiblePlayListIndex(index);

		IPlayList newSelectedPlayList = this.playListsContainer
				.getPlayListAt(index);

		// Set selection interval to none
		this.playListController.clearSelection();

		setPlayList(newSelectedPlayList);
	}

	/**
	 * Sets the play list.
	 * 
	 * @param playList
	 *            the new play list
	 */
	void setPlayList(final IPlayList playList) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				// Set selection interval to none
				PlayListHandler.this.playListController.clearSelection();

				PlayListHandler.this.playListInformationInStatusBar
						.showPlayListInformation(playList);

				// Update table model
				PlayListHandler.this.playListController
						.setVisiblePlayList(getVisiblePlayList());
				PlayListHandler.this.playListController.refreshPlayList();

				// If playlist is active then perform an auto scroll
				if (isActivePlayListVisible()) {
					PlayListHandler.this.playListController
							.scrollPlayList(false);
				}
			}
		});
	}

	@Override
	public boolean isVisiblePlayList(final int index) {
		return this.playListsContainer.getVisiblePlayListIndex() == index;
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
		return this.playListsContainer.getActivePlayList();
	}

	@Override
	public IPlayList getVisiblePlayList() {
		return this.playListsContainer.getVisiblePlayList();
	}

	@Override
	public void addToVisiblePlayList(
			final List<? extends IAudioObject> audioObjects) {
		addToPlayList(getVisiblePlayList().size(), audioObjects,
				getVisiblePlayList());
	}

	@Override
	public void addToVisiblePlayList(final int location,
			final List<? extends IAudioObject> audioObjects) {
		addToPlayList(location, audioObjects, getVisiblePlayList());
	}

	@Override
	public void addToActivePlayList(
			final List<? extends IAudioObject> audioObjects) {
		addToPlayList(getActivePlayList().size(), audioObjects,
				getActivePlayList());
	}

	@Override
	public void addToActivePlayList(final int location,
			final List<? extends IAudioObject> audioObjects) {
		addToPlayList(location, audioObjects, getActivePlayList());
	}

	private void addToPlayList(final int location,
			final List<? extends IAudioObject> audioObjects,
			final IPlayList playList) {
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

		// callVisiblePlayListListeners();

		// Update play list number in status bar
		this.playListInformationInStatusBar.showPlayListInformation(playList);

		// Update play list table
		this.playListController.refreshPlayList();

		Logger.info(StringUtils.getString(audioObjects.size(),
				" audio objects added to play list"));
	}

	/**
	 * @param audioObjects
	 * @param playList
	 */
	private void setSelectedItemAfterAddToPlayList(
			final List<? extends IAudioObject> audioObjects,
			final IPlayList playList) {
		// If active play list was empty, then set audio object as selected for
		// play
		if (isActivePlayListVisible() && playList.size() == audioObjects.size()) {
			int selectedAudioObject = 0;

			// If stopPlayerWhenPlayListClear property is disabled try to locate
			// audio object in play list. If it's not available then stop
			// playing
			if (!this.statePlaylist.isStopPlayerOnPlayListClear()
					&& this.playerHandler.isEnginePlaying()) {
				int index = playList.indexOf(this.playerHandler
						.getAudioObject());
				if (index != -1) {
					selectedAudioObject = index;
				} else {
					this.playerHandler.stopCurrentAudioObject(false);
				}
			} else if (this.statePlayer.isShuffle()) {
				// If shuffle enabled, select a random audio object
				selectedAudioObject = playList.getRandomPosition();
			}

			this.playListEventListeners
					.selectedAudioObjectHasChanged(audioObjects
							.get(selectedAudioObject));
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
		this.playListPersistor.persistPlayLists(this.listOfPlayListsCreator
				.getListOfPlayLists(this.playListsContainer));
	}

	@Override
	public void moveRowTo(final int sourceRow, final int targetRow) {
		getVisiblePlayList().moveRowTo(sourceRow, targetRow);
	}

	@Override
	public void setSelectionInterval(final int start, final int end) {
		this.playListController.setSelectionInterval(start, end);
	}

	@Override
	public void removeAudioObjects(final int[] rows) {
		getBean(PlayListRemover.class).removeAudioObjects(rows);
	}

	@Override
	public void selectedAudioObjectChanged(final IAudioObject audioObject) {
		addToPlaybackHistory(audioObject);
		this.playListController.updatePlayList();
		this.playListController.scrollPlayList(false);
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
		return this.playListsContainer.isFiltered();
	}

	@Override
	public void setFilter(final String filter) {
		this.playListsContainer.setFilter(filter);
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
	public List<IAudioObject> getSelectedAudioObjects() {
		List<IAudioObject> audioObjects = new ArrayList<IAudioObject>();
		int[] selectedRows = this.playListController.getSelectedRows();
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
		return getVisiblePlayList().getCurrentAudioObjectIndex() == row
				&& isActivePlayListVisible();
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
		if (this.statePlaylist.isAutoScrollPlayListEnabled()) {
			this.playListController.scrollPlayList(true);
		}
		this.playListTabController.showPlayListSelectorComboBox();
	}

	@Override
	public String getPlayListNameAtIndex(final int index) {
		return this.playListsContainer.getPlayListAt(index).getName();
	}

	@Override
	public List<IAudioObject> getPlayListContent(final int index) {
		if (index >= this.playListsContainer.getPlayListsCount() || index < 0) {
			throw new IllegalArgumentException(new StringBuilder()
					.append("Invalid play list index ").append(index)
					.toString());
		}
		return this.playListsContainer.getPlayListAt(index)
				.getAudioObjectsList();
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
		this.playListController.scrollPlayList(isUserAction);
	}

	@Override
	public void refreshPlayList() {
		this.playListController.refreshPlayList();
	}

	@Override
	public void moveDown() {
		this.playListController.moveDown(getVisiblePlayList());
	}

	@Override
	public void moveToBottom() {
		this.playListController.moveToBottom(getVisiblePlayList());
	}

	@Override
	public void moveToTop() {
		this.playListController.moveToTop(getVisiblePlayList());
	}

	@Override
	public void moveUp() {
		this.playListController.moveUp(getVisiblePlayList());
	}

	@Override
	public void deleteSelection() {
		this.playListController.deleteSelection();
	}

	@Override
	public void audioObjectsAdded(
			final List<IPlayListAudioObject> audioObjectsAdded) {
		playListsChanged();
	}

	@Override
	public void audioObjectsRemoved(
			final List<IPlayListAudioObject> audioObjectsRemoved) {
		playListsChanged();
	}

	@Override
	public void reapplyFilter() {
		if (isFiltered()) {
			setFilter(this.filterHandler.getFilterText(this.playListFilter));
		}
	}

	@Override
	public void playListCleared() {
		playListsChanged();
	}

	@Override
	public void deviceDisconnected(final String location) {
		getBean(PlayListRemoverFromDevice.class).removeAudioObjectsOfDevice(
				getVisiblePlayList(), location);
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
				PlayListHandler.this.contextHandler
						.retrieveInfoAndShowInPanel(getCurrentAudioObjectFromCurrentPlayList());
			}
		});

		// Find for audio files or playlists on arguments and play immediately
		final List<String> songs = new ArrayList<String>();
		for (String arg : getBean(IApplicationArguments.class)
				.getOriginalArguments()) {
			if (getBean(ILocalAudioObjectValidator.class).isValidAudioFile(arg)) {
				songs.add(arg);
			} else if (getBean(IPlayListIOService.class).isValidPlayList(arg)) {
				songs.addAll(getBean(IPlayListIOService.class).read(
						new File(arg)));
			}
		}

		List<IAudioObject> audioObjects = getBean(IPlayListIOService.class)
				.getAudioObjectsFromFileNamesList(songs);

		addToPlayListAndPlay(audioObjects);

		checkPlayListsItems();
	}

	@Override
	public void addToPlayListAndPlay(final List<IAudioObject> audioObjects) {
		if (!CollectionUtils.isEmpty(audioObjects)) {
			int playListCurrentSize = getVisiblePlayList().size();
			addToVisiblePlayList(audioObjects);
			this.playerHandler
					.playAudioObjectInPlayListPositionOfVisiblePlayList(playListCurrentSize);
		}
	}

	/**
	 * Checks and removes audio objects of play lists that are no longer
	 * available
	 */
	@Override
	public void checkPlayListsItems() {
		final List<ILocalAudioObject> audioObjectsNotFound = getBean(
				PlayListsChecker.class).checkPlayLists();
		Logger.info(audioObjectsNotFound.size(),
				" audio objects of play lists not found");
		if (!audioObjectsNotFound.isEmpty()) {
			final List<String> items = new ArrayList<String>();
			for (ILocalAudioObject ao : audioObjectsNotFound) {
				items.add(ao.getUrl());
			}
			GuiUtils.callInEventDispatchThread(new Runnable() {
				@Override
				public void run() {
					if (PlayListHandler.this.dialogFactory
							.newDialog(IListMessageDialog.class)
							.showMessageConfirmation(
									I18nUtils
											.getString("ITEMS_FROM_PLAYLIST_CAN_BE_REMOVED"),
									items)) {
						getBean(PlayListRemover.class).removeAudioFiles(
								audioObjectsNotFound);
						PlayListHandler.this.playListController
								.refreshPlayList();
					}
				}
			});
		}
	}

	@Override
	public void favoritesChanged() {
		// Recalculate dynamic play lists
		for (int i = 0; i < this.playListsContainer.getPlayListsCount(); i++) {
			if (this.playListsContainer.getPlayListAt(i) instanceof DynamicPlayList) {
				recalculateDynamicPlayList((DynamicPlayList) this.playListsContainer
						.getPlayListAt(i));
			}
		}
		// Update playlist to remove favorite icon
		refreshPlayList();
	}

	@Override
	public void repositoryChanged(final IRepository repository) {
		// Recalculate dynamic play lists
		for (int i = 0; i < this.playListsContainer.getPlayListsCount(); i++) {
			if (this.playListsContainer.getPlayListAt(i) instanceof DynamicPlayList) {
				recalculateDynamicPlayList((DynamicPlayList) this.playListsContainer
						.getPlayListAt(i));
			}
		}
	}

	void recalculateDynamicPlayList(final DynamicPlayList playList) {
		if (playList.isDynamic()) {
			playList.replaceContent(this.searchHandler.search(playList
					.getQuery().createSearchQuery(getBeanFactory())),
					getBean(IAudioObjectComparator.class), getBeanFactory());
		}
	}

	@Override
	public void editDynamicQuery(final IPlayList playList) {
		if (playList.isDynamic()) {
			// Edit query
			ISearchNode query = this.searchHandler.editQuery(
					((DynamicPlayList) playList).getQuery().createSearchQuery(
							getBeanFactory()),
					I18nUtils.getString("EDIT_DYNAMIC_PLAYLIST"),
					I18nUtils.getString("EDIT_DYNAMIC_PLAYLIST_HELP"));
			if (query != null) {
				((DynamicPlayList) playList)
						.setQuery(query.getRepresentation());

				getBean(UpdateDynamicPlayListBackgroundWorker.class)
						.setPlayList((DynamicPlayList) playList).execute();
			}
		} else {
			throw new IllegalArgumentException("Not a dynamic playlist");
		}
	}
}
