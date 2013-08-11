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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.kernel.PlayListEventListeners;

/**
 * A list of audio objects
 * 
 * @author alex
 * 
 */
public interface IPlayList extends Serializable, Cloneable {

	/**
	 * Moves one row in play list
	 * 
	 * @param sourceRow
	 * @param targetRow
	 */
	void moveRowTo(int sourceRow, int targetRow);

	/**
	 * Returns first index position of given audio object
	 * 
	 * @param audioObject
	 * @return
	 */
	int indexOf(IAudioObject audioObject);

	/**
	 * Returns size of this play list
	 * 
	 * @return
	 */
	int size();

	/**
	 * Returns <code>true</code> if this play list is empty
	 * 
	 * @return
	 */
	boolean isEmpty();

	/**
	 * Returns AudioObject at given index
	 * 
	 * @param index
	 * @return
	 */
	IAudioObject get(int index);

	/**
	 * Returns <code>true</code> if audio object is in play list
	 * 
	 * @param audioObject
	 * @return
	 */
	boolean contains(IAudioObject audioObject);

	/**
	 * Returns next audio object
	 * 
	 * @param index
	 *            1 for next, 2 for next of next'...
	 * @return
	 */
	IAudioObject getNextAudioObject(int index);

	/**
	 * Returns previous audio object
	 * 
	 * @param index
	 *            1 for previous, 2 for previous of previous'...
	 * @return
	 */
	IAudioObject getPreviousAudioObject(int index);

	/**
	 * Returns play list length in string format.
	 * 
	 * @return the length
	 */
	String getLength();

	/**
	 * Resets play list
	 */
	void reset();

	/**
	 * Returns name of play list
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Sets name to play list
	 * 
	 * @param newName
	 */
	void setName(String newName);

	/**
	 * Returns currently selected audio object in this play list
	 * 
	 * @return
	 */
	IAudioObject getCurrentAudioObject();

	/**
	 * Sorts play list with given column
	 * 
	 * @param column
	 */
	void sortByColumn(IColumn<?> column);

	/**
	 * Adds a list of audio objects in a location of play list
	 * 
	 * @param newLocation
	 * @param audioObjects
	 */
	void add(int newLocation, List<? extends IAudioObject> audioObjects);

	/**
	 * Returns a random position of play list
	 * 
	 * @return
	 */
	int getRandomPosition();

	/**
	 * Changes current audio object
	 * 
	 * @param selectedAudioObject
	 */
	void setCurrentAudioObjectIndex(int selectedAudioObject);

	/**
	 * Returns current audio object index
	 * 
	 * @return
	 */
	int getCurrentAudioObjectIndex();

	/**
	 * Clears play list
	 */
	void clear();

	/**
	 * Removes audio object by given index
	 * 
	 * @param i
	 */
	void remove(int i);

	/**
	 * Adds an object at given location
	 * 
	 * @param newLocation
	 * @param aux
	 */
	void add(int newLocation, IAudioObject aux);

	/**
	 * Shuffles play list
	 */
	void shuffle();

	/**
	 * Creates a copy
	 * 
	 * @return
	 */
	IPlayList copyPlayList();

	/**
	 * Moves to next audio object
	 * 
	 * @return
	 */
	IAudioObject moveToNextAudioObject();

	/**
	 * Moves to previous audio object
	 * 
	 * @return
	 */
	IAudioObject moveToPreviousAudioObject();

	/**
	 * Adds an audio object to playback history
	 * 
	 * @param object
	 */
	void addToPlaybackHistory(IAudioObject object);

	/**
	 * Removes a list of audio objects
	 * 
	 * @param audioObjects
	 */
	void remove(Collection<? extends IAudioObject> audioObjects);

	/**
	 * Returns a list with audio object contents
	 * 
	 * @return
	 */
	List<IAudioObject> getAudioObjectsList();

	/**
	 * Sets listeners to be notified when this play list changes
	 * 
	 * @param listeners
	 */
	void setPlayListEventListeners(final PlayListEventListeners listeners);

	/**
	 * @param statePlayer
	 */
	void setStatePlayer(IStatePlayer statePlayer);

	/**
	 * @param playListMode
	 */
	void setMode(IPlayListMode playListMode);

	/**
	 * @return true if play list is dynamic, managed by application
	 */
	boolean isDynamic();

	/**
	 * Request play list implementation to save internal data (before writting
	 * to disk)
	 */
	void saveInternalData();
}