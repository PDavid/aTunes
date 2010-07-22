/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.SavePlayListAction;
import net.sourceforge.atunes.kernel.actions.ShufflePlayListAction;
import net.sourceforge.atunes.kernel.modules.columns.PlayListColumnSet;
import net.sourceforge.atunes.kernel.modules.filter.AbstractFilter;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.AudioFilesRemovedListener;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class PlayListHandler.
 */
public final class PlayListHandler extends AbstractHandler implements AudioFilesRemovedListener {

    private static final class RowListComparator implements Comparator<Integer> {
        private final boolean up;

        private RowListComparator(boolean up) {
            this.up = up;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            return (up ? 1 : -1) * o1.compareTo(o2);
        }
    }

    private static class PreviousInitializationTaskRunnable implements Runnable {
        @Override
        public void run() {
            playListsRetrievedFromCache = ApplicationStateHandler.getInstance().retrievePlayListsCache();
        }
    }

    /** Singleton instance. */
    private static PlayListHandler instance = new PlayListHandler();

    /**
     * The play list counter used when creating new play lists with default
     * name.
     */
    private static int playListNameCounter = 1;

    /** The play lists currently opened. */
    private List<PlayList> playLists = new ArrayList<PlayList>();

    /** Index of the active play list */
    private int activePlayListIndex = 0;

    /** Index of the visible play list: can be different of active play list */
    private int visiblePlayListIndex = 0;

    /** Stores original play list without filter. */
    private static PlayList nonFilteredPlayList;

    /** Play lists stored */
    private static ListOfPlayLists playListsRetrievedFromCache;

    /**
     * Flag indicating if playlists have changed (removed or created)
     */
    private boolean playListsChanged;

    /**
     * Listeners to be called when play list changes
     */
    private List<PlayListEventListener> playListEventListeners = new ArrayList<PlayListEventListener>();
    
    /**
     * Filter for play list
     */
    private AbstractFilter playListFilter = new AbstractFilter() {

        @Override
        public String getName() {
            return "PLAYLIST";
        };

        @Override
        public String getDescription() {
            return I18nUtils.getString("PLAYLIST");
        };

        @Override
        public void applyFilter(String filter) {
            setFilter(filter);
        }

    };

    /**
     * Private constructor.
     */
    private PlayListHandler() {
    }

    @Override
    protected void initHandler() {
        // Add audio file removed listener
        RepositoryHandler.getInstance().addAudioFilesRemovedListener(this);
    }

    @Override
    public void applicationStarted() {
    }

    /**
     * Method to access singleton instance.
     * 
     * @return PlayListHandler
     */
    public static PlayListHandler getInstance() {
        return instance;
    }

    /**
     * Sets visible play list as active
     */
    public static void setVisiblePlayListActive() {
        instance.activePlayListIndex = instance.visiblePlayListIndex;
    }

    /**
     * Removes play list referred by index.
     * 
     * @param index
     *            the index
     * @param removeTab
     * 			  true if call must remove tab (when look and feel does not remove tab when closing it or when method is not called from the action of pressing close button)
     */
    public void removePlayList(int index, boolean removeTab) {
        // If index is not valid, do nothing
        if (index < 0 || playLists.size() <= index) {
            return;
        }
        // if there is only one play list, don't delete
        if (playLists.size() <= 1) {
            return;
        }
        playListsChanged = true;
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
                PlayerHandler.getInstance().stopCurrentAudioObject(false);
            }

            // Delete tab
            if (removeTab) {
            	ControllerProxy.getInstance().getPlayListTabController().deletePlayList(index);
            }

            // Refresh table
            ControllerProxy.getInstance().getPlayListController().refreshPlayList();
        } else {
            // index == visiblePlayList
            // switch play list and then delete
            if (index == 0) {
                switchToPlaylist(1);
            } else {
                switchToPlaylist(visiblePlayListIndex - 1);
            }
            removePlayList(index, removeTab);
        }
    }

    /**
     * Gets the list of play lists.
     * 
     * @return the list of play lists
     */
    private ListOfPlayLists getListOfPlayLists() {
        ListOfPlayLists l = new ListOfPlayLists();

        // Clone play lists to make changes in returned list if current play list is filtered
        l.setPlayLists(new ArrayList<PlayList>(playLists));
        l.setSelectedPlayList(activePlayListIndex);

        // If current play list is filtered return non-filtered play list
        if (isFiltered()) {
            l.getPlayLists().remove(activePlayListIndex);
            l.getPlayLists().add(activePlayListIndex, nonFilteredPlayList);
        }

        return l;
    }

    /**
     * Returns the number of play lists handled
     * 
     * @return
     */
    public int getPlayListCount() {
        return this.playLists.size();
    }

    /**
     * Gets the name for playlist.
     * 
     * @param pl
     *            the pl
     * 
     * @return the name for playlist
     */
    private String getNameForPlaylist(PlayList pl) {
        if (pl == null || pl.getName() == null) {
            String name = StringUtils.getString(I18nUtils.getString("PLAYLIST"), " ", playListNameCounter++);
            // If any play list already has the same name then call method again
            for (PlayList playList : playLists) {
                if (playList.getName() != null && name.equalsIgnoreCase(playList.getName().trim())) {
                    return getNameForPlaylist(pl);
                }
            }
            return name;
        }
        return pl.getName();
    }

    /**
     * Called to create a new play list with given audio objects.
     * 
     * @param audioObjects
     *            the audio objects
     */
    public void newPlayList(List<AudioObject> audioObjects) {
        newPlayList(getNameForPlaylist(null), audioObjects);
    }

    /**
     * Called to create a new play list with given audio objects.
     * 
     * @param nameOfNewPlayList
     *            the name of the new play list as shown in play list tab
     * 
     * @param audioObjects
     *            the audio objects to add to the new play list
     */
    public void newPlayList(String nameOfNewPlayList, List<AudioObject> audioObjects) {
        PlayList newPlayList;
        if (audioObjects == null) {
            newPlayList = new PlayList();
        } else {
            newPlayList = new PlayList(audioObjects);
        }
        newPlayList.setName(nameOfNewPlayList);
        playLists.add(newPlayList);
        ControllerProxy.getInstance().getPlayListTabController().newPlayList(nameOfNewPlayList);
        playListsChanged = true;
    }

    /**
     * Renames a play list.
     * 
     * @param index
     *            the index
     * @param newName
     *            the new name
     */
    public void renamePlayList(int index, String newName) {
        playLists.get(index).setName(newName);
        ControllerProxy.getInstance().getPlayListTabController().renamePlayList(index, newName);
    }

    /**
     * Called when switching play list at tabbed pane.
     * 
     * @param index
     *            the index
     */
    public void switchToPlaylist(int index) {
        // If play list is the same, do nothing, except if this method is called when deleting a play list
        if (index == visiblePlayListIndex) {
            return;
        }

        //don't stop player if user has setup the option
        if (ApplicationState.getInstance().isStopPlayerOnPlayListSwitch()) {
            PlayerHandler.getInstance().stopCurrentAudioObject(false);
        }

        // Remove filter from current play list before change play list
        setFilter(null);

        visiblePlayListIndex = index;

        PlayList newSelectedPlayList = playLists.get(index);

        // Set selection interval to none
        GuiHandler.getInstance().getPlayListTable().getSelectionModel().clearSelection();

        setPlayList(newSelectedPlayList);
        // Update table model
        ((PlayListTableModel) GuiHandler.getInstance().getPlayListTable().getModel()).setVisiblePlayList(getCurrentPlayList(true));
        ControllerProxy.getInstance().getPlayListController().refreshPlayList();

        // If playlist is active then perform an auto scroll
        if (getInstance().isActivePlayListVisible()) {
            ControllerProxy.getInstance().getPlayListController().scrollPlayList(false);
        }
    }

    /**
     * Sets the play list.
     * 
     * @param playList
     *            the new play list
     */
    private static void setPlayList(PlayList playList) {
        Actions.getAction(SavePlayListAction.class).setEnabled(!instance.getCurrentPlayList(true).isEmpty());
        Actions.getAction(ShufflePlayListAction.class).setEnabled(!instance.getCurrentPlayList(true).isEmpty());
        GuiHandler.getInstance().showPlayListInformation(playList);
        if (getInstance().isActivePlayListVisible()) {
            getInstance().selectedAudioObjectHasChanged(playList.getCurrentAudioObject());
        }
    }

    /**
     * Returns <code>true</code> if given index is current index
     * 
     * @return the currentPlayListIndex
     */
    public boolean isVisiblePlayList(int index) {
        return visiblePlayListIndex == index;
    }

    /**
     * Returns the current audio object from the visible play list
     * 
     * @return
     */
    public AudioObject getCurrentAudioObjectFromVisiblePlayList() {
        if (getCurrentPlayList(true) != null) {
            return getCurrentPlayList(true).getCurrentAudioObject();
        }
        return null;
    }

    /**
     * Returns the current audio object from the current play list
     * 
     * @return
     */
    public AudioObject getCurrentAudioObjectFromCurrentPlayList() {
        if (getCurrentPlayList(false) != null) {
            return getCurrentPlayList(false).getCurrentAudioObject();
        }
        return null;
    }

    /**
     * Returns visible play list or active play list. Can return null while
     * application starts as there is no play list added yet
     * 
     * @param visible
     * @return
     */
    public PlayList getCurrentPlayList(boolean visible) {
        if (playLists.isEmpty()) {
            return null;
        }

        if (visible) {
            return playLists.get(visiblePlayListIndex);
        }
        return playLists.get(activePlayListIndex);
    }

    /**
     * Adds audio objects to play list at the end
     * 
     * @param audioObjects
     *            the audio objects
     */
    public void addToPlayList(List<? extends AudioObject> audioObjects) {
        addToPlayList(getCurrentPlayList(true).size(), audioObjects, true);
    }

    /**
     * Adds audio objects to play list at given location
     * 
     * @param location
     *            the index of playlist where add audio objects
     * @param audioObjects
     *            the audio objects
     */
    public void addToPlayList(int location, List<? extends AudioObject> audioObjects, boolean visible) {
        // If null or empty, nothing to do
        if (audioObjects == null || audioObjects.isEmpty()) {
            return;
        }

        // Get play list
        PlayList playList = getCurrentPlayList(visible);

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

        // If active play list was empty, then set audio object as selected for play
        if (isActivePlayListVisible() && playList.size() == audioObjects.size()) {
            int selectedAudioObject = 0;

            // If stopPlayerWhenPlayListClear property is disabled try to locate audio object in play list. If it's not available then stop playing
            if (!ApplicationState.getInstance().isStopPlayerOnPlayListClear() && PlayerHandler.getInstance().isEnginePlaying()) {
                int index = playList.indexOf(PlayerHandler.getInstance().getAudioObject());
                if (index != -1) {
                    selectedAudioObject = index;
                } else {
                    PlayerHandler.getInstance().stopCurrentAudioObject(false);
                }
            } else if (ApplicationState.getInstance().isShuffle()) {
                // If shuffle enabled, select a random audio object
                selectedAudioObject = playList.getRandomPosition();
            }

            selectedAudioObjectHasChanged(audioObjects.get(selectedAudioObject));
            playList.setCurrentAudioObjectIndex(selectedAudioObject);
        }

        Actions.getAction(SavePlayListAction.class).setEnabled(!getCurrentPlayList(true).isEmpty());
        Actions.getAction(ShufflePlayListAction.class).setEnabled(!getCurrentPlayList(true).isEmpty());

        // Update play list number in status bar
        GuiHandler.getInstance().showPlayListInformation(playList);

        // Update play list table
        ControllerProxy.getInstance().getPlayListController().refreshPlayList();

        getLogger().info(LogCategories.HANDLER, StringUtils.getString(audioObjects.size(), " audio objects added to play list"));
    }

    public void addToActivePlayList(List<? extends AudioObject> audioObjects) {
        PlayList playList = getCurrentPlayList(false);
        addToPlayList(playList.getCurrentAudioObjectIndex() + 1, audioObjects, false);
    }

    /**
     * Removes all audio objects from visible play list.
     */
    public void clearPlayList() {
        getLogger().debug(LogCategories.HANDLER);

        // Remove filter
        setFilter(null);

        // Set selection interval to none
        GuiHandler.getInstance().getPlayListTable().getSelectionModel().clearSelection();

        PlayList playList = getCurrentPlayList(true);
        if (!playList.isEmpty()) {
            // Clear play list
            playList.clear();

            // Only if this play list is the active stop playback
            if (isActivePlayListVisible() && ApplicationState.getInstance().isStopPlayerOnPlayListClear()) {
                PlayerHandler.getInstance().stopCurrentAudioObject(false);
            }

            // Set first audio object as current
            playList.setCurrentAudioObjectIndex(0);

            // Disable actions
            Actions.getAction(SavePlayListAction.class).setEnabled(false);
            Actions.getAction(ShufflePlayListAction.class).setEnabled(false);

            // Update audio object number
            GuiHandler.getInstance().showPlayListInformation(playList);

            // hide the ticks and labels
            if (!ApplicationState.getInstance().isStopPlayerOnPlayListClear()) {
                GuiHandler.getInstance().getPlayerControls().getProgressBar().setEnabled(false);
            } else {
                GuiHandler.getInstance().getPlayerControls().setShowTicksAndLabels(false);
            }
            GuiHandler.getInstance().getPlayListPanel().repaint();

            // Refresh play list
            ControllerProxy.getInstance().getPlayListController().refreshPlayList();

            getLogger().info(LogCategories.HANDLER, "Play list clear");
        }

        // Fire clear event
        // Only if this play list is the active
        if (isActivePlayListVisible()) {
            clear();
        }
    }

    /**
     * Called by kernel when application is finishing.
     */
    public void applicationFinish() {
        // Store contents if play lists are dirty or list of play lists have changed
        boolean playListsDirty = false;
        boolean selectedItemDirty = false;
        for (PlayList playList : playLists) {
            playListsDirty = playListsDirty || playList.isDirty();
            selectedItemDirty = selectedItemDirty || playList.isSelectedItemDirty();
        }

        if (selectedItemDirty || playListsChanged) {
            // Store play list definition
            ApplicationStateHandler.getInstance().persistPlayListsDefinition(getListOfPlayLists());
        }
        if (playListsDirty || playListsChanged) {
        	// Store play list contents
            ApplicationStateHandler.getInstance().persistPlayListsContents(getPlayListsContents());
        }
        
        if (!playListsDirty && !selectedItemDirty && !playListsChanged) {
            getLogger().info(LogCategories.PLAYLIST, "Playlists are clean");
        }
    }

    @Override
    protected Runnable getPreviousInitializationTask() {
        return new PreviousInitializationTaskRunnable();
    }

    /**
     * Retrieves stored play list and loads it. This method is used when opening
     * application, to load play list of previous execution
     */
    public void setPlayLists() {
        getLogger().debug(LogCategories.HANDLER);

        // Get playlists from application cache
        final ListOfPlayLists listOfPlayLists = playListsRetrievedFromCache;

        // Set selected play list as default 
        int selected = listOfPlayLists.getSelectedPlayList();
        if (selected < 0 || selected >= listOfPlayLists.getPlayLists().size()) {
            selected = 0;
        }
        final PlayList lastPlayList = listOfPlayLists.getPlayLists().get(selected);

        // Add playlists
        playLists.clear();
        for (PlayList playlist : listOfPlayLists.getPlayLists()) {
            playLists.add(playlist);
            ControllerProxy.getInstance().getPlayListTabController().newPlayList(getNameForPlaylist(playlist));
        }
        activePlayListIndex = selected;
        // Initially active play list and visible play list are the same
        visiblePlayListIndex = activePlayListIndex;
        ControllerProxy.getInstance().getPlayListTabController().forceSwitchTo(visiblePlayListIndex);

        setPlayList(playLists.get(activePlayListIndex));

        // If play list is not empty
        // TODO: Do this for all play lists
        // Check that at least first entry exists. This is to avoid loading a play list that contains audio object deleted or moved
        if (lastPlayList.size() > 0 && (lastPlayList.get(0) instanceof Radio || lastPlayList.get(0) instanceof PodcastFeedEntry || lastPlayList.get(0) instanceof AudioFile)) {

        	// When possible, take audio objects from Repository instead of from PlayList stored.
        	// This way we prevent to have duplicated objects in PlayList for same audio object, one of PlayList and one of Repository
        	List<AudioObject> audioObjects = new ArrayList<AudioObject>(lastPlayList.getAudioObjects());
        	// lastPlayList.clear();
        	//TODO also for radios and podcast feed entries
        	for (int i = 0; i < audioObjects.size(); i++) {
        		AudioObject ao = audioObjects.get(i);
        		AudioFile repositoryFile = RepositoryHandler.getInstance().getFileIfLoaded(ao.getUrl());
        		if (repositoryFile != null) {
        			lastPlayList.replace(i, repositoryFile);
        		}
        	}
        }

        // Update table model
        ((PlayListTableModel) GuiHandler.getInstance().getPlayListTable().getModel()).setVisiblePlayList(getCurrentPlayList(true));

        // Refresh play list
        // For some strange reason, this is needed even if play list is empty
        ControllerProxy.getInstance().getPlayListController().refreshPlayList();

        playListsRetrievedFromCache = null;
    }

    /**
     * Loads play list from a file.
     */
    public void loadPlaylist() {
        getLogger().debug(LogCategories.HANDLER);
        JFileChooser fileChooser = new JFileChooser(ApplicationState.getInstance().getLoadPlaylistPath());
        FileFilter filter = PlayListIO.getPlaylistFileFilter();
        // Open file chooser
        if (GuiHandler.getInstance().showOpenDialog(fileChooser, filter) == JFileChooser.APPROVE_OPTION) {
            // Get selected file
            File file = fileChooser.getSelectedFile();

            // If exists...
            if (file.exists()) {
                ApplicationState.getInstance().setLoadPlaylistPath(file.getParentFile().getAbsolutePath());
                // Read file names
                List<String> filesToLoad = PlayListIO.read(file);
                // Background loading - but only when returned array is not null (Progress dialog hangs otherwise)
                if (filesToLoad != null) {
                    LoadPlayListProcess process = new LoadPlayListProcess(filesToLoad);
                    process.execute();
                }
            } else {
                GuiHandler.getInstance().showErrorDialog(I18nUtils.getString("FILE_NOT_FOUND"));
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

    /**
     * Move rows of play list up.
     * 
     * @param rows
     *            the rows
     */
    public void moveUp(int[] rows) {
        moveRows(rows, true);
    }

    /**
     * Move rows of play list down.
     * 
     * @param rows
     *            the rows
     */
    public void moveDown(int[] rows) {
        moveRows(rows, false);
    }

    /**
     * Move rows to bottom of play list.
     * 
     * @param rows
     *            the rows
     */
    public void moveToBottom(int[] rows) {
        getLogger().debug(LogCategories.HANDLER);
        PlayList currentPlayList = getCurrentPlayList(true);
        int j = 0;
        for (int i = rows.length - 1; i >= 0; i--) {
            AudioObject aux = currentPlayList.get(rows[i]);
            currentPlayList.remove(rows[i]);
            currentPlayList.add(currentPlayList.size() - j++, aux);
        }
        if (rows[rows.length - 1] < currentPlayList.getCurrentAudioObjectIndex()) {
            currentPlayList.setCurrentAudioObjectIndex(currentPlayList.getCurrentAudioObjectIndex() - rows.length);
        } else if (rows[0] <= currentPlayList.getCurrentAudioObjectIndex() && currentPlayList.getCurrentAudioObjectIndex() <= rows[rows.length - 1]) {
            currentPlayList.setCurrentAudioObjectIndex(currentPlayList.getCurrentAudioObjectIndex() + currentPlayList.size() - rows[rows.length - 1] - 1);
        }
    }

    /**
     * Move rows to top of play list.
     * 
     * @param rows
     *            the rows
     */
    public void moveToTop(int[] rows) {
        getLogger().debug(LogCategories.HANDLER);
        PlayList currentPlayList = getCurrentPlayList(true);
        for (int i = 0; i < rows.length; i++) {
            AudioObject aux = currentPlayList.get(rows[i]);
            currentPlayList.remove(rows[i]);
            currentPlayList.add(i, aux);
        }
        if (rows[0] > currentPlayList.getCurrentAudioObjectIndex()) {
            currentPlayList.setCurrentAudioObjectIndex(currentPlayList.getCurrentAudioObjectIndex() + rows.length);
        } else if (rows[0] <= currentPlayList.getCurrentAudioObjectIndex() && currentPlayList.getCurrentAudioObjectIndex() <= rows[rows.length - 1]) {
            currentPlayList.setCurrentAudioObjectIndex(currentPlayList.getCurrentAudioObjectIndex() - rows[0]);
        }
    }

    public void moveRowTo(int sourceRow, int targetRow) {
        getCurrentPlayList(true).moveRowTo(sourceRow, targetRow);
    }

    /**
     * Plays audio object passed to argument. If is not added to play list, it
     * adds
     * 
     * @param audioObject
     *            the audio object
     */
    public void playNow(AudioObject audioObject) {
        if (!getCurrentPlayList(true).contains(audioObject)) {
            List<AudioObject> list = new ArrayList<AudioObject>();
            list.add(audioObject);
            addToPlayListAndPlay(list);
        } else {
            setPositionToPlayInVisiblePlayList(getCurrentPlayList(true).indexOf(audioObject));
            PlayerHandler.getInstance().playCurrentAudioObject(false);
        }
    }

    /**
     * Removes audio objects from play list.
     * 
     * @param rows
     *            the rows
     */
    public void removeAudioObjects(int[] rows) {
        PlayList currentPlayList = getCurrentPlayList(true);
        AudioObject playingAudioObject = currentPlayList.getCurrentAudioObject();
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
            // Only stop if this is the active play list
            if (isActivePlayListVisible()) {
                PlayerHandler.getInstance().stopCurrentAudioObject(false);
            }
            if (currentPlayList.isEmpty()) {
                clear();
            } else {
                // If current audio object is removed, check if it's necessary to move current audio object (after remove current index is greater than play list size)
                if (currentPlayList.getCurrentAudioObjectIndex() >= currentPlayList.size()) {
                    currentPlayList.setCurrentAudioObjectIndex(currentPlayList.size() - 1);
                }
                if (isActivePlayListVisible()) {
                    selectedAudioObjectHasChanged(currentPlayList.getCurrentAudioObject());
                }
            }
        } else {
            currentPlayList.setCurrentAudioObjectIndex(currentPlayList.indexOf(playingAudioObject));
            if (isActivePlayListVisible()) {
                selectedAudioObjectHasChanged(currentPlayList.getCurrentAudioObject());
            }
        }

        ControllerProxy.getInstance().getPlayListController().refreshPlayList();

        if (currentPlayList.isEmpty()) {
            Actions.getAction(SavePlayListAction.class).setEnabled(false);
            Actions.getAction(ShufflePlayListAction.class).setEnabled(false);
            GuiHandler.getInstance().getPlayerControls().setShowTicksAndLabels(false);
        }
        GuiHandler.getInstance().showPlayListInformation(currentPlayList);
        getLogger().info(LogCategories.HANDLER, StringUtils.getString(rows.length, " objects removed from play list"));
    }

    /**
     * Called when play list is cleared. Calls to all PlayListEventListener
     */
    private void clear() {
    	for (PlayListEventListener listener : playListEventListeners) {
    		listener.clear();
    	}
    }

    /**
     * Called when current audio object changes. Calls to all PlayListEventListener
     * 
     * @param audioObject
     *            the audio object
     */
    public void selectedAudioObjectHasChanged(final AudioObject audioObject) {
        if (audioObject == null) {
            return;
        }

        addToPlaybackHistory(audioObject);
    	for (final PlayListEventListener listener : playListEventListeners) {
    		listener.selectedAudioObjectChanged(audioObject);
    	}
    }

    /**
     * Shuffle current play list
     */
    public void shuffle() {
        getLogger().debug(LogCategories.HANDLER);

        PlayList currentPlaylist = getCurrentPlayList(true);

        // If current play list is empty, don't shuffle
        if (currentPlaylist.isEmpty()) {
            return;
        }

        currentPlaylist.shuffle();
    }

    /**
     * Checks if the active play list is visible (its tab is selected)
     * 
     * @return <code>true</code> if active play list is visible
     */
    public boolean isActivePlayListVisible() {
        return getCurrentPlayList(true) == getCurrentPlayList(false);
    }

    /**
     * Adds the to play list and play.
     * 
     * @param audioObjects
     *            the audio objects
     */
    public void addToPlayListAndPlay(List<AudioObject> audioObjects) {
        if (audioObjects == null || audioObjects.isEmpty()) {
            return;
        }

        int playListCurrentSize = getCurrentPlayList(true).size();
        addToPlayList(audioObjects);
        setPositionToPlayInVisiblePlayList(playListCurrentSize);
        PlayerHandler.getInstance().playCurrentAudioObject(false);
    }

    /**
     * Checks if visible play list is filtered.
     * 
     * @return true, if current play list is filtered
     */
    public boolean isFiltered() {
        return nonFilteredPlayList != null;
    }

    /**
     * Applies filter to play list. If filter is null, previous existing filter
     * is removed
     * 
     * @param filter
     *            the filter
     */
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
            PlayList newPlayList = new PlayList(PlayListColumnSet.getInstance().filterAudioObjects(nonFilteredPlayList.getAudioObjects(), filterText));
            setPlayListAfterFiltering(newPlayList);
        }
    }

    /**
     * Sets the play list after filtering.
     * 
     * @param playList
     *            the new play list after filtering
     */
    private void setPlayListAfterFiltering(PlayList playList) {
        playLists.remove(visiblePlayListIndex);
        playLists.add(visiblePlayListIndex, playList);

        // Set selection interval to none
        GuiHandler.getInstance().getPlayListTable().getSelectionModel().clearSelection();

        setPlayList(playList);

        // Update table model
        ((PlayListTableModel) GuiHandler.getInstance().getPlayListTable().getModel()).setVisiblePlayList(playList);
        ControllerProxy.getInstance().getPlayListController().refreshPlayList();

        ControllerProxy.getInstance().getPlayListController().scrollPlayList(false);

        //        PlayList currentPlayList = getCurrentPlayList(true);
        //
        //        if (playList.size() > currentPlayList.size()) { // Removing filter
        //            AudioObject selectedAudioObject = currentPlayList.getCurrentAudioObject();
        //            int index = playList.indexOf(selectedAudioObject);
        //            for (int i = 0; i < playList.size(); i++) {
        //                currentPlayList.add(playList.get(i));
        //            }
        //            playLists.remove(visiblePlayListIndex);
        //            playLists.add(visiblePlayListIndex, playList);
        //            getCurrentPlayList(true).setCurrentAudioObjectIndex(index != -1 ? index : 0);
        //            if (index == -1 && isActivePlayListVisible()) {
        //                selectedAudioObjectChanged(currentPlayList.get(0));
        //            }
        //        } else {
        //            // Remove from table 
        //            List<Integer> rowsToRemove = new ArrayList<Integer>();
        //            for (int i = 0; i < currentPlayList.size(); i++) {
        //                AudioObject ao = currentPlayList.get(i);
        //                if (!playList.contains(ao)) {
        //                    rowsToRemove.add(i);
        //                }
        //            }
        //            int[] rowsToRemoveArray = new int[rowsToRemove.size()];
        //            for (int i = 0; i < rowsToRemove.size(); i++) {
        //                rowsToRemoveArray[i] = rowsToRemove.get(i);
        //            }
        //
        //            removeAudioObjects(rowsToRemoveArray);
        //        }
        //        GuiHandler.getInstance().showPlayListInformation(currentPlayList);
    }

    /**
     * Sets position to play in visible play list
     * 
     * @param pos
     *            the new play list position to play
     */
    public final void setPositionToPlayInVisiblePlayList(int pos) {
        getCurrentPlayList(true).setCurrentAudioObjectIndex(pos);
    }

    /**
     * Sets position to play in current play list
     * 
     * @param pos
     *            the new play list position to play
     */
    public final void setPositionToPlayInCurrentPlayList(int pos) {
        getCurrentPlayList(false).setCurrentAudioObjectIndex(pos);
    }

    /**
     * Returns next audio object
     * 
     * @return
     */
    public AudioObject getNextAudioObject() {
        return getCurrentPlayList(false).moveToNextAudioObject();
    }

    /**
     * Returns previous audio object
     * 
     * @return
     */
    public AudioObject getPreviousAudioObject() {
        return getCurrentPlayList(false).moveToPreviousAudioObject();
    }

    /**
     * Returns the index of an audio object in a playlist.
     * 
     * @param aObject
     *            The audio object you need the index of
     * 
     * @return The index of the audio object
     */
    public int getIndexOfAudioObject(AudioObject aObject) {
        return getCurrentPlayList(false).indexOf(aObject);
    }

    /**
     * Returns the audio object at the given index in the playlist.
     * 
     * @param index
     *            The index of the audio object
     * 
     * @return The audio object
     */
    public AudioObject getAudioObjectAtIndex(int index) {
        return getCurrentPlayList(false).getNextAudioObject(index);
    }

    /**
     * Moves the selected row in the play list to the position given in the
     * index
     * 
     * @param index
     *            The index to move to
     */
    public void changeSelectedAudioObjectToIndex(int index) {
        GuiHandler.getInstance().getPlayListTable().changeSelection(index, 0, false, false);
    }

    /**
     * Gets the selected audio objects in current play list
     * 
     * @return the selected audio objects
     */
    public List<AudioObject> getSelectedAudioObjects() {
        List<AudioObject> audioObjects = new ArrayList<AudioObject>();
        int[] selectedRows = GuiHandler.getInstance().getPlayListTable().getSelectedRows();
        if (selectedRows.length > 0) {
            for (int element : selectedRows) {
                AudioObject file = PlayListHandler.getInstance().getCurrentPlayList(true).get(element);
                if (file != null) {
                    audioObjects.add(file);
                }
            }
        }
        return audioObjects;
    }

    /**
     * Returns <code>true</code> if the row is both visible and being played
     * 
     * @param row
     * @return
     */
    public boolean isCurrentVisibleRowPlaying(int row) {
        return getCurrentPlayList(true).getCurrentAudioObjectIndex() == row && isActivePlayListVisible();
    }

    public int getCurrentAudioObjectIndexInVisiblePlayList() {
        return getCurrentPlayList(true).getCurrentAudioObjectIndex();
    }

    public void addToPlaybackHistory(AudioObject object) {
        getCurrentPlayList(false).addToPlaybackHistory(object);
        getLogger().debug(LogCategories.PLAYLIST, "Added to history: ", object.getTitle());
    }

    @Override
    public void audioFilesRemoved(List<AudioFile> audioFiles) {
        List<AudioObject> audioObjects = AudioFile.getAudioObjects(audioFiles);
        // Remove these objects from all play lists
        for (PlayList pl : playLists) {
            pl.remove(audioObjects);
        }
        // Update status bar
        GuiHandler.getInstance().showPlayListInformation(getCurrentPlayList(true));
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        if (newState.isAutoScrollPlayListEnabled()) {
            ControllerProxy.getInstance().getPlayListController().scrollPlayList(true);
        }
    }

    public void movePlaylistToPosition(int from, int to) {
        PlayList activePlayList = playLists.get(activePlayListIndex);
        PlayList visiblePlayList = playLists.get(visiblePlayListIndex);
        PlayList playList = playLists.remove(from);
        playLists.add(to, playList);
        activePlayListIndex = playLists.indexOf(activePlayList);
        visiblePlayListIndex = playLists.indexOf(visiblePlayList);
    }

    /**
     * Returns content of all play lists
     * 
     * @return
     */
    private List<List<AudioObject>> getPlayListsContents() {
        List<List<AudioObject>> result = new ArrayList<List<AudioObject>>(playLists.size());
        for (PlayList playList : playLists) {
            result.add(playList.getAudioObjects());
        }
        return result;
    }

    /**
     * @return the playListFilter
     */
    public AbstractFilter getPlayListFilter() {
        return playListFilter;
    }

    /**
     * Returns play list name at given index
     * 
     * @param index
     * @return
     */
    public String getPlayListNameAtIndex(int index) {
        return this.playLists.get(index).getName();
    }

    /**
     * Returns audio objects of play list with given index
     * 
     * @param index
     * @return
     */
    public List<AudioObject> getPlayListContent(int index) {
        if (index >= this.playLists.size()) {
            throw new IllegalArgumentException(new StringBuilder().append("Invalid play list index ").append(index).toString());
        } else {
            List<AudioObject> result = new ArrayList<AudioObject>();
            PlayList playlist = this.playLists.get(index);
            for (int i = 0; i < playlist.size(); i++) {
                result.add(playlist.get(i));
            }
            return result;
        }
    }
    
    /**
     * Adds a new play list event listener
     * @param listener
     */
    public void addPlayListEventListener(PlayListEventListener listener) {
    	this.playListEventListeners.add(listener);
    }
}
