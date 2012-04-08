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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.kernel.actions.SavePlayListAction;
import net.sourceforge.atunes.kernel.actions.ShufflePlayListAction;
import net.sourceforge.atunes.kernel.modules.process.LoadPlayListProcess;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IArtistAlbumSelectorDialog;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListIOService;
import net.sourceforge.atunes.model.IPlayListObjectFilter;
import net.sourceforge.atunes.model.IPlayListPanel;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.model.IPlayerControlsPanel;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class PlayListHandler.
 */
public final class PlayListHandler extends AbstractHandler implements IPlayListHandler {

    private static final String SLASH = " / ";

	private static final String PLAYLIST = "PLAYLIST";

    /** The play lists currently opened. */
    private List<IPlayList> playLists = new ArrayList<IPlayList>();

    /** Index of the active play list */
    private int activePlayListIndex = 0;

    /** Index of the visible play list: can be different of active play list */
    private int visiblePlayListIndex = 0;

    /** Stores original play list without filter. */
    private IPlayList nonFilteredPlayList;

    /** Play lists stored */
    private IListOfPlayLists playListsRetrievedFromCache;
    
    private IProcessFactory processFactory;
    
    private IPlayListIOService playListIOService;
    
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

	private IPlayListTable playListTable;
	
	private IPlayListPanel playListPanel;
	
	private IFilter playListFilter;
	
	private IFilterHandler filterHandler;
	
	private IRepositoryHandler repositoryHandler;
	
	private PlayListNameCreator playListNameCreator;
	
	private PlayListPersistor playListPersistor;
	
	private ListOfPlayListsCreator listOfPlayListsCreator;
	
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
	 * @param playListIOService
	 */
	public void setPlayListIOService(IPlayListIOService playListIOService) {
		this.playListIOService = playListIOService;
	}
	
	/**
	 * @param processFactory
	 */
	public void setProcessFactory(IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}
	
    /**
     * @param playListColumnSet
     */
    public void setPlayListColumnSet(IColumnSet playListColumnSet) {
		this.playListColumnSet = playListColumnSet;
	}
    
    /**
     * @param playListPanel
     */
    public void setPlayListPanel(IPlayListPanel playListPanel) {
		this.playListPanel = playListPanel;
	}

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
        setPlayLists();
    }
    
    @Override
    public void allHandlersInitialized() {
        // Create drag and drop listener
    	playListPanel.enableDragAndDrop();
    }

    /**
     * Sets visible play list as active
     */
    @Override
    public void setVisiblePlayListActive() {
        activePlayListIndex = visiblePlayListIndex;
    }

    @Override
	public void removePlayList(int index) {
        // If index is not valid, do nothing
        if (index < 0 || playLists.size() <= index) {
            return;
        }
        // if there is only one play list, don't delete
        if (playLists.size() <= 1) {
            return;
        }
        
        // remove tab and playlist
        // NOTE: removing visible tab play list calls automatically to switchToPlayList method
        if (index != visiblePlayListIndex) {
            // Remove playlist from list
            playLists.remove(index);

            boolean activePlayListIsBeingRemoved = index == activePlayListIndex;

            // If index < visiblePlayListIndex, visible play list has been moved to left, so
            // decrease in 1
            if (index < visiblePlayListIndex) {
                // If active play list visible then decrease in 1 too
                if (activePlayListIndex == visiblePlayListIndex) {
                    activePlayListIndex--;
                }
                visiblePlayListIndex--;
            }

            // Removed play list is active, then set visible play list as active and stop player
            if (activePlayListIsBeingRemoved) {
                activePlayListIndex = visiblePlayListIndex;
                playerHandler.stopCurrentAudioObject(false);
            }

            // Delete tab
           	getPlayListTabController().deletePlayList(index);

            // Refresh table
            getPlayListController().refreshPlayList();
        } else {
            // index == visiblePlayList
            // switch play list and then delete
            if (index == 0) {
                switchToPlaylist(1);
            } else {
                switchToPlaylist(visiblePlayListIndex - 1);
            }
            removePlayList(index);
        }
        
        playListsChanged(true, true);
    }
    
    @Override
	public int getPlayListCount() {
        return this.playLists.size();
    }

    @Override
	public void newPlayList(List<IAudioObject> audioObjects) {
        newPlayList(playListNameCreator.getNameForPlaylist(playLists, null), audioObjects);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#newPlayList(java.lang.String, java.util.List)
	 */
    @Override
	public void newPlayList(String nameOfNewPlayList, List<? extends IAudioObject> audioObjects) {
        PlayList newPlayList;
        if (audioObjects == null) {
            newPlayList = new PlayList(getState());
        } else {
            newPlayList = new PlayList(audioObjects, getState());
        }
        newPlayList.setName(nameOfNewPlayList);
        playLists.add(newPlayList);
        getPlayListTabController().newPlayList(nameOfNewPlayList);
        
        playListsChanged(true, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#renamePlayList()
	 */
    @Override
	public void renamePlayList() {
        int selectedPlaylist = getPlayListTabController().getSelectedPlayListIndex();
        String currentName = getPlayListTabController().getPlayListName(selectedPlaylist);
        IInputDialog dialog = getBean(IInputDialog.class);
        dialog.setTitle(I18nUtils.getString("RENAME_PLAYLIST"));
        dialog.showDialog(currentName);
        String newName = dialog.getResult();
        if (newName != null) {
            renamePlayList(selectedPlaylist, newName);
        }
    }
    
    /**
     * Renames given play list.
     * 
     * @param index
     *            the index
     * @param newName
     *            the new name
     */
    private void renamePlayList(int index, String newName) {
        playLists.get(index).setName(newName);
        getPlayListTabController().renamePlayList(index, newName);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#switchToPlaylist(int)
	 */
    @Override
	public void switchToPlaylist(int index) {
        // If play list is the same, do nothing, except if this method is called when deleting a play list
        if (index == visiblePlayListIndex) {
            return;
        }

        //don't stop player if user has setup the option
        if (getState().isStopPlayerOnPlayListSwitch()) {
            playerHandler.stopCurrentAudioObject(false);
        }

        // Remove filter from current play list before change play list
        setFilter(null);

        visiblePlayListIndex = index;

        IPlayList newSelectedPlayList = playLists.get(index);

        // Set selection interval to none
        playListTable.getSelectionModel().clearSelection();

        setPlayList(newSelectedPlayList);
        // Update table model
        ((PlayListTableModel) playListTable.getModel()).setVisiblePlayList(getCurrentPlayList(true));
        getPlayListController().refreshPlayList();

        // If playlist is active then perform an auto scroll
        if (isActivePlayListVisible()) {
            getPlayListController().scrollPlayList(false);
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
        showPlayListInformation(playList);
        if (isActivePlayListVisible()) {
        	playListEventListeners.selectedAudioObjectHasChanged(playList.getCurrentAudioObject());
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#isVisiblePlayList(int)
	 */
    @Override
	public boolean isVisiblePlayList(int index) {
        return visiblePlayListIndex == index;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#getCurrentAudioObjectFromVisiblePlayList()
	 */
    @Override
	public IAudioObject getCurrentAudioObjectFromVisiblePlayList() {
        if (getCurrentPlayList(true) != null) {
            return getCurrentPlayList(true).getCurrentAudioObject();
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#getCurrentAudioObjectFromCurrentPlayList()
	 */
    @Override
	public IAudioObject getCurrentAudioObjectFromCurrentPlayList() {
        if (getCurrentPlayList(false) != null) {
            return getCurrentPlayList(false).getCurrentAudioObject();
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#getCurrentPlayList(boolean)
	 */
    @Override
	public IPlayList getCurrentPlayList(boolean visible) {
        if (playLists.isEmpty()) {
            return null;
        }

        if (visible) {
            return playLists.get(visiblePlayListIndex);
        }
        return playLists.get(activePlayListIndex);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#addToPlayList(java.util.List)
	 */
    @Override
	public void addToPlayList(List<? extends IAudioObject> audioObjects) {
        addToPlayList(getCurrentPlayList(true).size(), audioObjects, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#addToPlayList(int, java.util.List, boolean)
	 */
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
        showPlayListInformation(playList);

        // Update play list table
        getPlayListController().refreshPlayList();

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
            if (!getState().isStopPlayerOnPlayListClear() && playerHandler.isEnginePlaying()) {
                int index = playList.indexOf(playerHandler.getAudioObject());
                if (index != -1) {
                    selectedAudioObject = index;
                } else {
                    playerHandler.stopCurrentAudioObject(false);
                }
            } else if (getState().isShuffle()) {
                // If shuffle enabled, select a random audio object
                selectedAudioObject = playList.getRandomPosition();
            }

            playListEventListeners.selectedAudioObjectHasChanged(audioObjects.get(selectedAudioObject));
            playList.setCurrentAudioObjectIndex(selectedAudioObject);
        }
	}

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#addToActivePlayList(java.util.List)
	 */
    @Override
	public void addToActivePlayList(List<? extends IAudioObject> audioObjects) {
        IPlayList playList = getCurrentPlayList(false);
        addToPlayList(playList.getCurrentAudioObjectIndex() + 1, audioObjects, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#clearPlayList()
	 */
    @Override
	public void clearPlayList() {
        // Remove filter
        setFilter(null);

        // Set selection interval to none
        playListTable.getSelectionModel().clearSelection();

        IPlayList playList = getCurrentPlayList(true);
        if (!playList.isEmpty()) {
            // Clear play list
            playList.clear();

            // Only if this play list is the active stop playback
            if (isActivePlayListVisible() && getState().isStopPlayerOnPlayListClear()) {
                playerHandler.stopCurrentAudioObject(false);
            }

            // Set first audio object as current
            playList.setCurrentAudioObjectIndex(0);

            // Disable actions
            getBean(SavePlayListAction.class).setEnabled(false);
            getBean(ShufflePlayListAction.class).setEnabled(false);

            // Update audio object number
            showPlayListInformation(playList);

            // disable progress slider
            if (!getState().isStopPlayerOnPlayListClear()) {
            	getBean(IPlayerControlsPanel.class).getProgressSlider().setEnabled(false);
            }
            playListPanel.getSwingComponent().repaint();

            // Refresh play list
            getPlayListController().refreshPlayList();

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
    	IListOfPlayLists listOfPlayLists = listOfPlayListsCreator.getListOfPlayLists(playLists, activePlayListIndex, isFiltered(), nonFilteredPlayList);
    	playListPersistor.persistPlayLists(listOfPlayLists, definition, contents);
    }

    @Override
    protected Runnable getPreviousInitializationTask() {
        return new PreviousInitializationTaskRunnable(this);
    }

	private void setPlayLists() {
        // Get playlists from application cache
        final IListOfPlayLists listOfPlayLists = playListsRetrievedFromCache;

        // Set selected play list as default 
        int selected = listOfPlayLists.getSelectedPlayList();
        if (selected < 0 || selected >= listOfPlayLists.getPlayLists().size()) {
            selected = 0;
        }

        // Add playlists
        playLists.clear();
        for (IPlayList playlist : listOfPlayLists.getPlayLists()) {
            playLists.add((PlayList)playlist);
            getPlayListTabController().newPlayList(playListNameCreator.getNameForPlaylist(playLists, (PlayList)playlist));
        }
        activePlayListIndex = selected;
        // Initially active play list and visible play list are the same
        visiblePlayListIndex = activePlayListIndex;
        getPlayListTabController().forceSwitchTo(visiblePlayListIndex);

        setPlayList(playLists.get(activePlayListIndex));

        // Update table model
        ((PlayListTableModel) playListTable.getModel()).setVisiblePlayList(getCurrentPlayList(true));

        // Refresh play list
        // For some strange reason, this is needed even if play list is empty
        getPlayListController().refreshPlayList();

        playListsRetrievedFromCache = null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#loadPlaylist()
	 */
    @Override
	public void loadPlaylist() {
        JFileChooser fileChooser = new JFileChooser(getState().getLoadPlaylistPath());
        FileFilter filter = playListIOService.getPlaylistFileFilter();
        // Open file chooser
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(getFrame().getFrame()) == JFileChooser.APPROVE_OPTION) {
            // Get selected file
            File file = fileChooser.getSelectedFile();

            // If exists...
            if (file.exists()) {
                getState().setLoadPlaylistPath(file.getParentFile().getAbsolutePath());
                // Read file names
                List<String> filesToLoad = playListIOService.read(file);
                // Background loading - but only when returned array is not null (Progress dialog hangs otherwise)
                if (filesToLoad != null) {
                    LoadPlayListProcess process = (LoadPlayListProcess) processFactory.getProcessByName("loadPlayListProcess");
                    process.setFilenamesToLoad(filesToLoad);
                    process.execute();
                }
            } else {
            	getBean(IErrorDialogFactory.class).getDialog().showErrorDialog(getFrame(), I18nUtils.getString("FILE_NOT_FOUND"));
            }
        }
    }

    private void moveRows(int[] rows, final boolean up) {
        if (rows == null || rows.length == 0) {
            return;
        }
        List<Integer> rowList = new ArrayList<Integer>();
        for (int row : rows) {
            rowList.add(row);
        }
        Collections.sort(rowList, new RowListComparator(up));
        for (Integer row : rowList) {
            moveRowTo(row, row + (up ? -1 : 1));
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#moveUp(int[])
	 */
    @Override
	public void moveUp(int[] rows) {
        moveRows(rows, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#moveDown(int[])
	 */
    @Override
	public void moveDown(int[] rows) {
        moveRows(rows, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#moveToBottom(int[])
	 */
    @Override
	public void moveToBottom(int[] rows) {
        IPlayList currentPlayList = getCurrentPlayList(true);
        int j = 0;
        for (int i = rows.length - 1; i >= 0; i--) {
            IAudioObject aux = currentPlayList.get(rows[i]);
            currentPlayList.remove(rows[i]);
            currentPlayList.add(currentPlayList.size() - j++, aux);
        }
        if (rows[rows.length - 1] < currentPlayList.getCurrentAudioObjectIndex()) {
            currentPlayList.setCurrentAudioObjectIndex(currentPlayList.getCurrentAudioObjectIndex() - rows.length);
        } else if (rows[0] <= currentPlayList.getCurrentAudioObjectIndex() && currentPlayList.getCurrentAudioObjectIndex() <= rows[rows.length - 1]) {
            currentPlayList.setCurrentAudioObjectIndex(currentPlayList.getCurrentAudioObjectIndex() + currentPlayList.size() - rows[rows.length - 1] - 1);
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#moveToTop(int[])
	 */
    @Override
	public void moveToTop(int[] rows) {
        IPlayList currentPlayList = getCurrentPlayList(true);
        for (int i = 0; i < rows.length; i++) {
            IAudioObject aux = currentPlayList.get(rows[i]);
            currentPlayList.remove(rows[i]);
            currentPlayList.add(i, aux);
        }
        if (rows[0] > currentPlayList.getCurrentAudioObjectIndex()) {
            currentPlayList.setCurrentAudioObjectIndex(currentPlayList.getCurrentAudioObjectIndex() + rows.length);
        } else if (rows[0] <= currentPlayList.getCurrentAudioObjectIndex() && currentPlayList.getCurrentAudioObjectIndex() <= rows[rows.length - 1]) {
            currentPlayList.setCurrentAudioObjectIndex(currentPlayList.getCurrentAudioObjectIndex() - rows[0]);
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#moveRowTo(int, int)
	 */
    @Override
	public void moveRowTo(int sourceRow, int targetRow) {
        getCurrentPlayList(true).moveRowTo(sourceRow, targetRow);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#moveSelectionAfterCurrentAudioObject()
	 */
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
        playListTable.getSelectionModel().setSelectionInterval(getCurrentAudioObjectIndexInVisiblePlayList() + 1, getCurrentAudioObjectIndexInVisiblePlayList() + selectedAudioObjects.size());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#playNow(net.sourceforge.atunes.model.IAudioObject)
	 */
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#removeAudioObjects(int[])
	 */
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

        getPlayListController().refreshPlayList();

        if (currentPlayList.isEmpty()) {
            getBean(SavePlayListAction.class).setEnabled(false);
            getBean(ShufflePlayListAction.class).setEnabled(false);
        }
        showPlayListInformation(currentPlayList);
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
        getPlayListController().updatePlayList();
        getPlayListController().scrollPlayList(false);
        playListsChanged(true, false);
	};

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#shuffle()
	 */
    @Override
	public void shuffle() {
        IPlayList currentPlaylist = getCurrentPlayList(true);

        // If current play list is empty, don't shuffle
        if (currentPlaylist.isEmpty()) {
            return;
        }

        currentPlaylist.shuffle();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#isActivePlayListVisible()
	 */
    @Override
	public boolean isActivePlayListVisible() {
        return getCurrentPlayList(true) == getCurrentPlayList(false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#addToPlayListAndPlay(java.util.List)
	 */
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#isFiltered()
	 */
    @Override
	public boolean isFiltered() {
        return nonFilteredPlayList != null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#setFilter(java.lang.String)
	 */
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
				nonFilteredPlayList = getCurrentPlayList(true).clone();
            }

            // Create a new play list by filtering elements
            PlayList newPlayList = new PlayList(playListColumnSet.filterAudioObjects(nonFilteredPlayList.getAudioObjectsList(), filterText), getState());
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
        playLists.remove(visiblePlayListIndex);
        playLists.add(visiblePlayListIndex, playList);

        // Set selection interval to none
        playListTable.getSelectionModel().clearSelection();

        setPlayList(playList);

        // Update table model
        ((PlayListTableModel) playListTable.getModel()).setVisiblePlayList(playList);
        getPlayListController().refreshPlayList();

        getPlayListController().scrollPlayList(false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#setPositionToPlayInVisiblePlayList(int)
	 */
    @Override
	public final void setPositionToPlayInVisiblePlayList(int pos) {
        getCurrentPlayList(true).setCurrentAudioObjectIndex(pos);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#resetCurrentPlayList()
	 */
    @Override
	public final void resetCurrentPlayList() {
        getCurrentPlayList(false).reset();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#getNextAudioObject()
	 */
    @Override
	public IAudioObject getNextAudioObject() {
        return getCurrentPlayList(false).moveToNextAudioObject();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#getPreviousAudioObject()
	 */
    @Override
	public IAudioObject getPreviousAudioObject() {
        return getCurrentPlayList(false).moveToPreviousAudioObject();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#getIndexOfAudioObject(net.sourceforge.atunes.model.IAudioObject)
	 */
    @Override
	public int getIndexOfAudioObject(IAudioObject aObject) {
        return getCurrentPlayList(false).indexOf(aObject);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#getAudioObjectAtIndex(int)
	 */
    @Override
	public IAudioObject getAudioObjectAtIndex(int index) {
        return getCurrentPlayList(false).getNextAudioObject(index);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#changeSelectedAudioObjectToIndex(int)
	 */
    @Override
	public void changeSelectedAudioObjectToIndex(int index) {
        playListTable.changeSelection(index, 0, false, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#getSelectedAudioObjects()
	 */
    @Override
	public List<IAudioObject> getSelectedAudioObjects() {
        List<IAudioObject> audioObjects = new ArrayList<IAudioObject>();
        int[] selectedRows = playListTable.getSelectedRows();
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
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#isCurrentVisibleRowPlaying(int)
	 */
    @Override
	public boolean isCurrentVisibleRowPlaying(int row) {
        return getCurrentPlayList(true).getCurrentAudioObjectIndex() == row && isActivePlayListVisible();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#getCurrentAudioObjectIndexInVisiblePlayList()
	 */
    @Override
	public int getCurrentAudioObjectIndexInVisiblePlayList() {
        return getCurrentPlayList(true).getCurrentAudioObjectIndex();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#addToPlaybackHistory(net.sourceforge.atunes.model.IAudioObject)
	 */
    @Override
	public void addToPlaybackHistory(IAudioObject object) {
        getCurrentPlayList(false).addToPlaybackHistory(object);
        Logger.debug("Added to history: ", object.getTitle());
    }

    @Override
    public void audioFilesRemoved(List<ILocalAudioObject> audioFiles) {
        // Remove these objects from all play lists
        for (IPlayList pl : playLists) {
            pl.remove(audioFiles);
        }
        // Update status bar
        showPlayListInformation(getCurrentPlayList(true));
    }

    @Override
    public void applicationStateChanged(IState newState) {
        if (newState.isAutoScrollPlayListEnabled()) {
            getPlayListController().scrollPlayList(true);
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#movePlaylistToPosition(int, int)
	 */
    @Override
	public void movePlaylistToPosition(int from, int to) {
        IPlayList activePlayList = playLists.get(activePlayListIndex);
        IPlayList visiblePlayList = playLists.get(visiblePlayListIndex);
        IPlayList playList = playLists.remove(from);
        playLists.add(to, playList);
        activePlayListIndex = playLists.indexOf(activePlayList);
        visiblePlayListIndex = playLists.indexOf(visiblePlayList);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#getPlayListNameAtIndex(int)
	 */
    @Override
	public String getPlayListNameAtIndex(int index) {
        return this.playLists.get(index).getName();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#getPlayListContent(int)
	 */
    @Override
	public List<IAudioObject> getPlayListContent(int index) {
        if (index >= this.playLists.size()) {
            throw new IllegalArgumentException(new StringBuilder().append("Invalid play list index ").append(index).toString());
        } else {
            List<IAudioObject> result = new ArrayList<IAudioObject>();
            IPlayList playlist = this.playLists.get(index);
            for (int i = 0; i < playlist.size(); i++) {
                result.add(playlist.get(i));
            }
            return result;
        }
    }
    
    /**
     * Gets the play list tab controller.
     * 
     * @return the play list tab controller
     */
    private PlayListTabController getPlayListTabController() {
        if (playListTabController == null) {
            playListTabController = new PlayListTabController(playListPanel.getPlayListTabPanel(), getState(), this);
        }
        return playListTabController;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#closeCurrentPlaylist()
	 */
    @Override
	public void closeCurrentPlaylist() {
        // The current selected play list when this action is fired
        int i = getPlayListTabController().getSelectedPlayListIndex();
        if (i != -1) {
        	// As this action is not called when pressing close button in tab set removeTab argument to true
            removePlayList(i);
        }
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#closeOtherPlaylists()
	 */
    @Override
	public void closeOtherPlaylists() {
        // The current selected play list when this action is fired
        int i = getPlayListTabController().getSelectedPlayListIndex();
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

	private PlayListController getPlayListController() {
        if (playListController == null) {
            playListController = new PlayListController(playListTable, playListPanel, getState(), this, playerHandler);
        }
        return playListController;
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#scrollPlayList(boolean)
	 */
	@Override
	public void scrollPlayList(boolean isUserAction) {
		getPlayListController().scrollPlayList(isUserAction);		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#refreshPlayList()
	 */
	@Override
	public void refreshPlayList() {
		getPlayListController().refreshPlayList();		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#moveDown()
	 */
	@Override
	public void moveDown() {
		getPlayListController().moveDown();		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#moveToBottom()
	 */
	@Override
	public void moveToBottom() {
		getPlayListController().moveToBottom();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#moveToTop()
	 */
	@Override
	public void moveToTop() {
		getPlayListController().moveToTop();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#moveUp()
	 */
	@Override
	public void moveUp() {
		getPlayListController().moveUp();		
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#deleteSelection()
	 */
	@Override
	public void deleteSelection() {
		getPlayListController().deleteSelection();
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
            playListTable.getSelectionModel().clearSelection();
            removeAudioObjects(indexes);
        }
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#getMenuItemsToSwitchPlayLists()
	 */
	@Override
	public List<JMenuItem> getMenuItemsToSwitchPlayLists() {
		List<JMenuItem> menuItems = new ArrayList<JMenuItem>(); 
        List<String> playlists = getPlayListTabController().getNamesOfPlayLists();
        for (int i = 0; i < playlists.size(); i++) {
            final int index = i;
            JMenuItem plMenuItem;
            if (isVisiblePlayList(index)) {
                plMenuItem = new JMenuItem(StringUtils.getString("<html><b>", playlists.get(i), "</b></html>"));
            } else {
                plMenuItem = new JMenuItem(playlists.get(i));
            }
            plMenuItem.addActionListener(new SwitchPlayListListener(getPlayListTabController(), index));
            menuItems.add(plMenuItem);
        }
        return menuItems;
	}
	
    private static class SwitchPlayListListener implements ActionListener {

    	private PlayListTabController controller;
    	
        private int index;

        public SwitchPlayListListener(PlayListTabController controller, int index) {
        	this.controller = controller;
            this.index = index;
        }

        public void actionPerformed(ActionEvent e1) {
            controller.forceSwitchTo(index);
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.playlist.IPlayListHandler#showAddArtistDragDialog(net.sourceforge.atunes.model.Artist)
	 */
    @Override
	public void showAddArtistDragDialog(IArtist currentArtist) {
    	IArtistAlbumSelectorDialog dialog = getBean(IArtistAlbumSelectorDialog.class);
    	IAlbum album = dialog.showDialog(currentArtist);
    	if (album != null) {
    		addToPlayList(album.getAudioObjects());
    	}
    }

    /**
     * Show play list information.
     * 
     * @param playList
     *            the play list
     */
    private void showPlayListInformation(IPlayList playList) {
        int audioFiles = playListLocalAudioObjectFilter.getObjects(playList).size();
        int radios = new PlayListRadioFilter().getObjects(playList).size();
        int podcastFeedEntries = new PlayListPodcastFeedEntryFilter().getObjects(playList).size();
        int audioObjects = playList.size();

        // Check if differenciation is required (needed by some slavic languages)
        String toolTip = StringUtils.getString(I18nUtils.getString(PLAYLIST), ": ", audioObjects, " ", I18nUtils.getString("SONGS"), " (",
        		playList.getLength(), ") ", " - ", audioFiles, " ", I18nUtils.getString("SONGS"), SLASH, radios, " ", I18nUtils.getString("RADIOS"),
        		SLASH, podcastFeedEntries, " ", (I18nUtils.getString("PODCAST_ENTRIES_COUNTER").isEmpty() ? I18nUtils.getString("PODCAST_ENTRIES") : I18nUtils.getString("PODCAST_ENTRIES_COUNTER")));

        
        String text = StringUtils.getString(I18nUtils.getString(PLAYLIST), ": ", audioObjects, " - ", audioFiles, SLASH, radios, SLASH, podcastFeedEntries);

        getFrame().setRightStatusBarText(text, toolTip);
    }
    
    @Override
    public void windowDeiconified() {
		scrollPlayList(false);
    }

    /**
     * @param playListTable
     */
    public void setPlayListTable(IPlayListTable playListTable) {
		this.playListTable = playListTable;
	}
}
