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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.utils.PointedList;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a play list.
 * 
 * @author alex
 * 
 */
public class PlayList extends AbstractPlayList {

	private static final long serialVersionUID = 2756513776762920794L;

	/**
	 * Pointed List of audio objects of this play list
	 */
	PointedList<IAudioObject> audioObjects;

	/**
	 * No args constructor for serialization
	 */
	PlayList() {
		super();
	}

	/**
	 * Default constructor
	 * 
	 * @param statePlayer
	 * @param listeners
	 */
	protected PlayList(final IStatePlayer statePlayer) {
		this((List<IAudioObject>) null, statePlayer);
	}

	/**
	 * Builds a new play list with the given list of audio objects
	 * 
	 * @param audioObjectsList
	 * @param statePlayer
	 */
	protected PlayList(final List<? extends IAudioObject> audioObjectsList,
			final IStatePlayer statePlayer) {
		this.audioObjects = new PlayListPointedList(statePlayer);
		setMode(PlayListMode.getPlayListMode(this, statePlayer));
		setStatePlayer(statePlayer);
		if (audioObjectsList != null) {
			add(audioObjectsList);
		}
	}

	@Override
	public IPlayList copyPlayList() {
		PlayList copy = new PlayList();
		copy.setName(getName());
		copy.audioObjects = new PlayListPointedList(this.audioObjects,
				getStatePlayer());
		copy.setStatePlayer(getStatePlayer());
		copy.setMode(PlayListMode.getPlayListMode(this, getStatePlayer()));
		copy.setListeners(getListeners());
		return copy;
	}

	@Override
	public boolean isDynamic() {
		return false;
	}

	// ADD OPERATIONS

	/**
	 * Adds an audio object at the end of play list
	 * 
	 * @param audioObject
	 */
	protected final void add(final IAudioObject audioObject) {
		add(Collections.singletonList(audioObject));
	}

	/**
	 * Adds a list of audio objects at the end of play list
	 * 
	 * @param audioObjectsList
	 */
	private void add(final List<? extends IAudioObject> audioObjectsList) {
		add(this.audioObjects.size(), audioObjectsList);
	}

	/**
	 * Adds an audio object at given index position
	 * 
	 * @param index
	 * @param list
	 */
	@Override
	public final void add(final int index, final IAudioObject audioObject) {
		add(index, Collections.singletonList(audioObject));
	}

	/**
	 * Adds a list of audio objects at given index position
	 * 
	 * @param index
	 * @param audioObjectsList
	 */
	@Override
	public final void add(final int index,
			final List<? extends IAudioObject> audioObjectsList) {
		this.audioObjects.addAll(index, audioObjectsList);
		notifyAudioObjectsAdded(index, audioObjectsList);
	}

	// REMOVE OPERATIONS

	/**
	 * Removes given row from this play list
	 * 
	 * @param list
	 */
	@Override
	public void remove(final int index) {
		IAudioObject ao = get(index);
		if (ao != null) {
			PlayListAudioObject plao = new PlayListAudioObject();
			plao.setPosition(index);
			plao.setAudioObject(ao);
			List<IPlayListAudioObject> removedAudioObjects = new ArrayList<IPlayListAudioObject>();
			removedAudioObjects.add(plao);
			this.audioObjects.remove(index);
			notifyAudioObjectsRemoved(removedAudioObjects);
		}
	}

	/**
	 * Removes given list of audio objects from this play list. All occurrences
	 * are removed
	 * 
	 * @param list
	 */
	@Override
	public void remove(final Collection<? extends IAudioObject> list) {
		// First get all positions of objects to remove
		List<IPlayListAudioObject> playListAudioObjects = new ArrayList<IPlayListAudioObject>();
		for (IAudioObject ao : list) {
			List<IAudioObject> clonedList = new ArrayList<IAudioObject>(
					this.audioObjects.getList());
			while (clonedList.indexOf(ao) != -1) {
				int index = clonedList.indexOf(ao);
				PlayListAudioObject playListAudioObject = new PlayListAudioObject();
				playListAudioObject.setPosition(index);
				playListAudioObject.setAudioObject(ao);
				playListAudioObjects.add(playListAudioObject);
				clonedList = clonedList.subList(index + 1, clonedList.size());
			}
		}

		// Sort in reverse order to remove last index first and avoid shift
		Collections.sort(playListAudioObjects,
				new PlayListAudioObjectComparator());

		for (IPlayListAudioObject plao : playListAudioObjects) {
			this.audioObjects.remove(plao.getPosition());
		}
		notifyAudioObjectsRemoved(playListAudioObjects);
	}

	/**
	 * Clears play list
	 */
	@Override
	public void clear() {
		this.audioObjects.clear();
		notifyAudioObjectsRemovedAll();
	}

	/**
	 * Replaces given position with given object
	 * 
	 * @param index
	 * @param newObject
	 */
	protected void replace(final int index, final IAudioObject newObject) {
		this.audioObjects.replace(index, newObject);
	}

	// ROW MOVE OPERATIONS

	@Override
	public void moveRowTo(final int sourceRow, final int targetRow) {
		// Check arguments
		if (sourceRow < 0 || sourceRow >= size()) {
			throw new IllegalArgumentException(StringUtils.getString(
					"sourceRow = ", sourceRow, " playlist size = ", size()));
		}
		if (targetRow < 0 || targetRow >= size()) {
			throw new IllegalArgumentException(StringUtils.getString(
					"targetRow = ", targetRow, " playlist size = ", size()));
		}

		IAudioObject audioObjectToMove = get(sourceRow);

		boolean sourceRowIsPointed = this.audioObjects.getPointer() == sourceRow;

		// Remove from previous row
		remove(sourceRow);

		// Add at new row
		add(targetRow, audioObjectToMove);

		// Update current index if necessary
		if (sourceRowIsPointed) {
			this.audioObjects.setPointer(targetRow);
		}
	}

	@Override
	public void sortByColumn(final IColumn<?> column) {
		this.audioObjects.sort(column.getComparator());
		notifyCurrentAudioObjectChanged(this.audioObjects.getCurrentObject());
	}

	/**
	 * Shuffles this play list
	 */
	@Override
	public void shuffle() {
		this.audioObjects.shuffle();
		notifyCurrentAudioObjectChanged(this.audioObjects.getCurrentObject());
	}

	@Override
	protected PointedList<IAudioObject> getAudioObjectsPointedList() {
		return this.audioObjects;
	}

	@Override
	public void saveInternalData() {
	}
}
