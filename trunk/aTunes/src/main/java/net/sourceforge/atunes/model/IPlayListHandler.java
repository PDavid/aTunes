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

package net.sourceforge.atunes.model;

import java.util.Collection;
import java.util.List;

/**
 * Responsible of managing play lists
 * 
 * @author alex
 * 
 */
public interface IPlayListHandler extends IAudioFilesRemovedListener, IHandler,
		IRepositoryListener {

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
	 * 
	 * @param newName
	 */
	public void renameCurrentVisiblePlayList(String newName);

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
	 * @param index
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
	 * Returns active play list. Can return null while application starts as
	 * there is no play list added yet
	 * 
	 * @return active play list
	 */
	public IPlayList getActivePlayList();

	/**
	 * Returns visible play list. Can return null while application starts as
	 * there is no play list added yet
	 * 
	 * @return visible play list
	 */
	public IPlayList getVisiblePlayList();

	/**
	 * Adds audio objects to visible play list at the end
	 * 
	 * @param audioObjects
	 *            the audio objects
	 */
	public void addToVisiblePlayList(List<? extends IAudioObject> audioObjects);

	/**
	 * Adds audio objects to visible play list at given location
	 * 
	 * @param location
	 * @param audioObjects
	 *            the audio objects
	 */
	public void addToVisiblePlayList(int location,
			List<? extends IAudioObject> audioObjects);

	/**
	 * Adds audio objects to active play list at the end
	 * 
	 * @param audioObjects
	 */
	public void addToActivePlayList(List<? extends IAudioObject> audioObjects);

	/**
	 * Adds audio objects to active play list at the given location
	 * 
	 * @param location
	 * @param audioObjects
	 *            the audio objects
	 */
	public void addToActivePlayList(int location,
			List<? extends IAudioObject> audioObjects);

	/**
	 * Removes all audio objects from visible play list.
	 */
	public void clearPlayList();

	/**
	 * Move one play list entry from one position to another
	 * 
	 * @param sourceRow
	 * @param targetRow
	 */
	public void moveRowTo(int sourceRow, int targetRow);

	/**
	 * Removes audio objects from play list.
	 * 
	 * @param rows
	 *            the rows
	 */
	public void removeAudioObjects(int[] rows);

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

	/**
	 * Returns the index of the current audio object in visible play list
	 * 
	 * @return
	 */
	public int getCurrentAudioObjectIndexInVisiblePlayList();

	/**
	 * Adds audio object to history
	 * 
	 * @param object
	 */
	public void addToPlaybackHistory(IAudioObject object);

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
	 * Close current play list
	 */
	public void closeCurrentPlaylist();

	/**
	 * Close all play lists except the current one
	 */
	public void closeOtherPlaylists();

	/**
	 * Scroll play list to current audio object
	 * 
	 * @param isUserAction
	 */
	public void scrollPlayList(boolean isUserAction);

	/**
	 * Refresh play list
	 */
	public void refreshPlayList();

	/**
	 * Move selection down
	 */
	public void moveDown();

	/**
	 * Move selection bottom
	 */
	public void moveToBottom();

	/**
	 * Move selection top
	 */
	public void moveToTop();

	/**
	 * Move selection up
	 */
	public void moveUp();

	/**
	 * Delete selected items
	 */
	public void deleteSelection();

	/**
	 * Apply filter
	 */
	public void reapplyFilter();

	/**
	 * Sets visible play list as active
	 */
	public void setVisiblePlayListActive();

	/**
	 * @return name of current visible play list
	 */
	public String getCurrentVisiblePlayListName();

	/**
	 * Called to save play lists
	 */
	public void playListsChanged();

	/**
	 * Select given interval in play list
	 * 
	 * @param start
	 * @param end
	 */
	void setSelectionInterval(int start, int end);

	/**
	 * Checks and removes audio objects of play lists that are no longer
	 * available
	 */
	void checkPlayListsItems();

	/**
	 * Adds audio objects to play list and plays immediately
	 * 
	 * @param audioObjects
	 */
	void addToPlayListAndPlay(List<IAudioObject> audioObjects);

	/**
	 * Switches to next play list
	 */
	void nextPlayList();

	/**
	 * Switches to previous play list
	 */
	public void previousPlayList();

	/**
	 * Creates a new dynamic play list with given initial audio objects and
	 * query used to retrieve them
	 * 
	 * @param query
	 * @param initialObjects
	 */
	public void newDynamicPlayList(ISearchNode query,
			Collection<IAudioObject> initialObjects);

	/**
	 * @param playList
	 */
	public void editDynamicQuery(IPlayList playList);
}