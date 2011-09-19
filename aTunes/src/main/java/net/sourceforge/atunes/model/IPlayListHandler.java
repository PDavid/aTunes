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

package net.sourceforge.atunes.model;

import java.util.List;

import javax.swing.JMenuItem;


public interface IPlayListHandler extends IAudioFilesRemovedListener, IHandler{

	/**
	 * Removes play list referred by index.
	 * 
	 * @param index
	 *            the index
	 */
	public void removePlayList(int index);

	/**
	 * Returns the number of play lists handled
	 * 
	 * @return
	 */
	public int getPlayListCount();

	/**
	 * Called to create a new play list with given audio objects.
	 * 
	 * @param audioObjects
	 *            the audio objects
	 */
	public void newPlayList(List<IAudioObject> audioObjects);

	/**
	 * Called to create a new play list with given audio objects.
	 * 
	 * @param nameOfNewPlayList
	 *            the name of the new play list as shown in play list tab
	 * 
	 * @param audioObjects
	 *            the audio objects to add to the new play list
	 */
	public void newPlayList(String nameOfNewPlayList,
			List<? extends IAudioObject> audioObjects);

	/**
	 * Renames current play list.
	 */
	public void renamePlayList();

	/**
	 * Called when switching play list at tabbed pane.
	 * 
	 * @param index
	 *            the index
	 */
	public void switchToPlaylist(int index);

	/**
	 * Returns <code>true</code> if given index is current index
	 * 
	 * @return the currentPlayListIndex
	 */
	public boolean isVisiblePlayList(int index);

	/**
	 * Returns the current audio object from the visible play list
	 * 
	 * @return
	 */
	public IAudioObject getCurrentAudioObjectFromVisiblePlayList();

	/**
	 * Returns the current audio object from the current play list
	 * 
	 * @return
	 */
	public IAudioObject getCurrentAudioObjectFromCurrentPlayList();

	/**
	 * Returns visible play list or active play list. Can return null while
	 * application starts as there is no play list added yet
	 * 
	 * @param visible
	 * @return
	 */
	public IPlayList getCurrentPlayList(boolean visible);

	/**
	 * Adds audio objects to play list at the end
	 * 
	 * @param audioObjects
	 *            the audio objects
	 */
	public void addToPlayList(List<? extends IAudioObject> audioObjects);

	/**
	 * Adds audio objects to play list at given location
	 * 
	 * @param location
	 *            the index of playlist where add audio objects
	 * @param audioObjects
	 *            the audio objects
	 */
	public void addToPlayList(int location,
			List<? extends IAudioObject> audioObjects, boolean visible);

	public void addToActivePlayList(
			List<? extends IAudioObject> audioObjects);

	/**
	 * Removes all audio objects from visible play list.
	 */
	public void clearPlayList();

	/**
	 * Retrieves stored play list and loads it. This method is used when opening
	 * application, to load play list of previous execution
	 */
	public void setPlayLists();

	/**
	 * Loads play list from a file.
	 */
	public void loadPlaylist();

	/**
	 * Move rows of play list up.
	 * 
	 * @param rows
	 *            the rows
	 */
	public void moveUp(int[] rows);

	/**
	 * Move rows of play list down.
	 * 
	 * @param rows
	 *            the rows
	 */
	public void moveDown(int[] rows);

	/**
	 * Move rows to bottom of play list.
	 * 
	 * @param rows
	 *            the rows
	 */
	public void moveToBottom(int[] rows);

	/**
	 * Move rows to top of play list.
	 * 
	 * @param rows
	 *            the rows
	 */
	public void moveToTop(int[] rows);

	public void moveRowTo(int sourceRow, int targetRow);

	/**
	 * Moves rows just after current audio object (play list must be active)
	 * @param rows
	 */
	public void moveSelectionAfterCurrentAudioObject();

	/**
	 * Plays audio object passed to argument. If is not added to play list, it
	 * adds
	 * 
	 * @param audioObject
	 *            the audio object
	 */
	public void playNow(IAudioObject audioObject);

	/**
	 * Removes audio objects from play list.
	 * 
	 * @param rows
	 *            the rows
	 */
	public void removeAudioObjects(int[] rows);

	/**
	 * Called when current audio object changes. Calls to all PlayListEventListener
	 * 
	 * @param audioObject
	 *            the audio object
	 */
	public void selectedAudioObjectHasChanged(
			final IAudioObject audioObject);

	/**
	 * Shuffle current play list
	 */
	public void shuffle();

	/**
	 * Checks if the active play list is visible (its tab is selected)
	 * 
	 * @return <code>true</code> if active play list is visible
	 */
	public boolean isActivePlayListVisible();

	/**
	 * Adds the to play list and play.
	 * 
	 * @param audioObjects
	 *            the audio objects
	 */
	public void addToPlayListAndPlay(List<IAudioObject> audioObjects);

	/**
	 * Checks if visible play list is filtered.
	 * 
	 * @return true, if current play list is filtered
	 */
	public boolean isFiltered();

	/**
	 * Applies filter to play list. If filter is null, previous existing filter
	 * is removed
	 * 
	 * @param filter
	 *            the filter
	 */
	public void setFilter(String filter);

	/**
	 * Sets position to play in visible play list
	 * 
	 * @param pos
	 *            the new play list position to play
	 */
	public void setPositionToPlayInVisiblePlayList(int pos);

	/**
	 * Resets current play list
	 */
	public void resetCurrentPlayList();

	/**
	 * Returns next audio object
	 * 
	 * @return
	 */
	public IAudioObject getNextAudioObject();

	/**
	 * Returns previous audio object
	 * 
	 * @return
	 */
	public IAudioObject getPreviousAudioObject();

	/**
	 * Returns the index of an audio object in a playlist.
	 * 
	 * @param aObject
	 *            The audio object you need the index of
	 * 
	 * @return The index of the audio object
	 */
	public int getIndexOfAudioObject(IAudioObject aObject);

	/**
	 * Returns the audio object at the given index in the playlist.
	 * 
	 * @param index
	 *            The index of the audio object
	 * 
	 * @return The audio object
	 */
	public IAudioObject getAudioObjectAtIndex(int index);

	/**
	 * Moves the selected row in the play list to the position given in the
	 * index
	 * 
	 * @param index
	 *            The index to move to
	 */
	public void changeSelectedAudioObjectToIndex(int index);

	/**
	 * Gets the selected audio objects in current play list
	 * 
	 * @return the selected audio objects
	 */
	public List<IAudioObject> getSelectedAudioObjects();

	/**
	 * Returns <code>true</code> if the row is both visible and being played
	 * 
	 * @param row
	 * @return
	 */
	public boolean isCurrentVisibleRowPlaying(int row);

	public int getCurrentAudioObjectIndexInVisiblePlayList();

	public void addToPlaybackHistory(IAudioObject object);

	public void movePlaylistToPosition(int from, int to);

	/**
	 * @return the playListFilter
	 */
	public IFilter getPlayListFilter();

	/**
	 * Returns play list name at given index
	 * 
	 * @param index
	 * @return
	 */
	public String getPlayListNameAtIndex(int index);

	/**
	 * Returns audio objects of play list with given index
	 * 
	 * @param index
	 * @return
	 */
	public List<IAudioObject> getPlayListContent(int index);

	/**
	 * Close current playlist
	 */
	public void closeCurrentPlaylist();

	/**
	 * Close all play lists except the current one
	 */
	public void closeOtherPlaylists();

	public void scrollPlayList(boolean isUserAction);

	public void refreshPlayList();

	public void moveDown();

	public void moveToBottom();

	public void moveToTop();

	public void moveUp();

	public void deleteSelection();

	public void reapplyFilter();

	/**
	 * Returns menu items to switch play lists
	 */
	public List<JMenuItem> getMenuItemsToSwitchPlayLists();

	/**
	 * Shows a dialog to select an album of given artist and add to current play list
	 * @param currentArtist
	 */
	public void showAddArtistDragDialog(Artist currentArtist);

    /**
     * Sets visible play list as active
     */
    public void setVisiblePlayListActive();

}