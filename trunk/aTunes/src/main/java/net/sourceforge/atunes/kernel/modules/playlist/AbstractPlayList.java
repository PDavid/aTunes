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
import java.util.List;
import java.util.Random;

import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.IPlayListMode;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.utils.PointedList;
import net.sourceforge.atunes.utils.TimeUtils;

/**
 * This class represents a play list.
 * 
 * @author alex
 * 
 */
public abstract class AbstractPlayList implements IPlayList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6433269277710030903L;

	/**
	 * Play list mode to select audio objects
	 */
	private transient IPlayListMode mode;

	/**
	 * Name of play list as shown on play list tabs.
	 */
	String name;

	private transient IStatePlayer statePlayer;

	private transient PlayListEventListeners listeners;

	/**
	 * No args constructor for serialization
	 */
	AbstractPlayList() {
	}

	/**
	 * @param listeners
	 */
	protected void setListeners(final PlayListEventListeners listeners) {
		this.listeners = listeners;
	}

	/**
	 * @return listeners
	 */
	protected PlayListEventListeners getListeners() {
		return this.listeners;
	}

	/**
	 * @return state player
	 */
	protected IStatePlayer getStatePlayer() {
		return this.statePlayer;
	}

	@Override
	public void setStatePlayer(final IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
		((PlayListPointedList) getAudioObjectsPointedList())
				.setStatePlayer(statePlayer);
	}

	@Override
	public void setPlayListEventListeners(final PlayListEventListeners listeners) {
		this.listeners = listeners;
	}

	// ////////////////////////////////////////////////////////////// OTHER
	// OPERATIONS /////////////////////////////////////////////////////////////

	@Override
	public int indexOf(final IAudioObject audioObject) {
		return getAudioObjectsPointedList().indexOf(audioObject);
	}

	@Override
	public int size() {
		return getAudioObjectsPointedList().size();
	}

	@Override
	public boolean isEmpty() {
		return getAudioObjectsPointedList().isEmpty();
	}

	@Override
	public IAudioObject get(final int index) {
		if (index < 0 || index >= getAudioObjectsPointedList().size()) {
			return null;
		}
		return getAudioObjectsPointedList().get(index);
	}

	/**
	 * Return current audio object.
	 * 
	 * @return the current audio object
	 */
	@Override
	public IAudioObject getCurrentAudioObject() {
		return getAudioObjectsPointedList().getCurrentObject();
	}

	/**
	 * Sets the current audio object index
	 * 
	 * @param index
	 */
	@Override
	public void setCurrentAudioObjectIndex(final int index) {
		getAudioObjectsPointedList().setPointer(index);
		notifyCurrentAudioObjectChanged(getAudioObjectsPointedList()
				.getCurrentObject());
	}

	/**
	 * Returns the index of the current audio object
	 * 
	 * @return
	 */
	@Override
	public int getCurrentAudioObjectIndex() {
		// If pointer is not null return index, otherwise return 0
		return getAudioObjectsPointedList().getPointer() != null ? getAudioObjectsPointedList()
				.getPointer() : 0;
	}

	@Override
	public boolean contains(final IAudioObject audioObject) {
		return getAudioObjectsPointedList().contains(audioObject);
	}

	/**
	 * Gets the name of this play list
	 * 
	 * @return the name
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of this play list
	 * 
	 * @param name
	 */
	@Override
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Returns a random index
	 * 
	 * @return
	 */
	@Override
	public int getRandomPosition() {
		return new Random(System.currentTimeMillis())
				.nextInt(getAudioObjectsPointedList().size());
	}

	/**
	 * Returns next audio object
	 * 
	 * @return
	 */
	@Override
	public IAudioObject moveToNextAudioObject() {
		IAudioObject nextObject = getMode().moveToNextAudioObject();
		notifyCurrentAudioObjectChanged(nextObject);
		return nextObject;
	}

	/**
	 * Returns previous audio object
	 * 
	 * @return
	 */
	@Override
	public IAudioObject moveToPreviousAudioObject() {
		IAudioObject previousObject = getMode().moveToPreviousAudioObject();
		notifyCurrentAudioObjectChanged(previousObject);
		return previousObject;
	}

	@Override
	public IAudioObject getNextAudioObject(final int index) {
		return getMode().getNextAudioObject(index);
	}

	@Override
	public IAudioObject getPreviousAudioObject(final int index) {
		return getMode().getPreviousAudioObject(index);
	}

	@Override
	public String getLength() {
		long seconds = 0;
		for (IAudioObject song : getAudioObjectsPointedList().getList()) {
			// Check for null elements
			if (song != null) {
				seconds += song.getDuration();
			}
		}
		return TimeUtils.secondsToDaysHoursMinutesSeconds(seconds);
	}

	/**
	 * Returns audio objects of this play list
	 * 
	 * @return
	 */
	protected List<IAudioObject> getAudioObjects() {
		return getAudioObjectsPointedList().getList();
	}

	/**
	 * Private method to call listeners
	 * 
	 * @param position
	 * @param audioObjectList
	 */
	protected void notifyAudioObjectsAdded(final int position,
			final Collection<? extends IAudioObject> audioObjectList) {
		List<IPlayListAudioObject> playListAudioObjects = PlayListAudioObject
				.getList(position, audioObjectList);

		// Notify mode too
		getMode().audioObjectsAdded(playListAudioObjects);

		if (this.listeners != null) {
			this.listeners.audioObjectsAdded(playListAudioObjects);
		}
	}

	/**
	 * Private method to call listeners
	 * 
	 * @param audioObjectList
	 */
	protected void notifyAudioObjectsRemoved(
			final List<IPlayListAudioObject> audioObjectList) {
		// Notify mode too
		getMode().audioObjectsRemoved(audioObjectList);

		if (this.listeners != null) {
			this.listeners.audioObjectsRemoved(audioObjectList);
		}
	}

	/**
	 * Private method to call listeners
	 */
	protected void notifyAudioObjectsRemovedAll() {

		// Notify mode too
		getMode().audioObjectsRemovedAll();

		if (this.listeners != null) {
			this.listeners.playListCleared();
		}
	}

	/**
	 * Private method to call listeners
	 * 
	 * @param audioObject
	 */
	protected void notifyCurrentAudioObjectChanged(
			final IAudioObject audioObject) {
		if (this.listeners != null) {
			this.listeners.selectedAudioObjectHasChanged(audioObject);
		}
	}

	/**
	 * @return the mode
	 */
	protected IPlayListMode getMode() {
		return this.mode;
	}

	@Override
	public void setMode(final IPlayListMode mode) {
		this.mode = mode;
	}

	@Override
	public void addToPlaybackHistory(final IAudioObject object) {
		this.mode.addToPlaybackHistory(object);
	}

	@Override
	public void reset() {
		setCurrentAudioObjectIndex(0);
		getMode().reset();
	}

	@Override
	public List<IAudioObject> getAudioObjectsList() {
		return new ArrayList<IAudioObject>(getAudioObjectsPointedList()
				.getList());
	}

	protected abstract PointedList<IAudioObject> getAudioObjectsPointedList();
}
