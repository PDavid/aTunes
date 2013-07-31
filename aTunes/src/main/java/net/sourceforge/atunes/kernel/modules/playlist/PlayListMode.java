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
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.IPlayListMode;
import net.sourceforge.atunes.model.IStatePlayer;

final class PlayListMode implements IPlayListMode {

	/**
	 * List with play list positions in random order. This list is updated when
	 * there is a change in play list, even if shuffle is disabled
	 */
	private final ShufflePointedList shufflePlayList;

	/**
	 * Play back history
	 */
	private final PlaybackHistory playbackHistory;

	/**
	 * Play list bound to this mode
	 */
	private final AbstractPlayList playList;

	private final IStatePlayer statePlayer;

	/**
	 * @param playList
	 * @param statePlayer
	 * @return
	 */
	protected static IPlayListMode getPlayListMode(final IPlayList playList,
			final IStatePlayer statePlayer) {
		if (playList instanceof AbstractPlayList) {
			return new PlayListMode((AbstractPlayList) playList, statePlayer);
		} else {
			throw new UnsupportedOperationException("Not implemented");
		}
	}

	private PlayListMode(final AbstractPlayList playList,
			final IStatePlayer statePlayer) {
		// Initialize shuffle list
		this.shufflePlayList = new ShufflePointedList(statePlayer);
		this.statePlayer = statePlayer;
		this.playbackHistory = new PlaybackHistory();
		this.playList = playList;
		if (playList != null && playList.getAudioObjects() != null
				&& !playList.getAudioObjects().isEmpty()) {
			audioObjectsAdded(PlayListAudioObject.getList(0,
					playList.getAudioObjects()));
			// Set shuffle pointer
			int currentIndex = playList.getCurrentAudioObjectIndex();
			int indexInShuffle = this.shufflePlayList.indexOf(currentIndex);
			if (indexInShuffle != -1) {
				Collections.swap(this.shufflePlayList.getList(), 0,
						indexInShuffle);
			}
		}
	}

	@Override
	public IAudioObject moveToPreviousAudioObject() {
		if (this.playList.isEmpty()) {
			return null;
		}

		IAudioObject previosulyPlayedObject = this.playbackHistory
				.moveToPreviousInHistory();
		if (previosulyPlayedObject != null) {
			int index = this.playList.indexOf(previosulyPlayedObject);
			// Update pointed object
			this.playList.getAudioObjectsPointedList().setPointer(index);
			return previosulyPlayedObject;
		}

		// No previous object
		if (isShuffle()) {
			Integer previousIndex = this.shufflePlayList.moveToPreviousObject();
			if (previousIndex == null) {
				return null;
			}
			// Update current index and return object
			this.playList.setCurrentAudioObjectIndex(previousIndex);
			return this.playList.get(previousIndex);
		}

		return this.playList.getAudioObjectsPointedList()
				.moveToPreviousObject();
	}

	@Override
	public IAudioObject moveToNextAudioObject() {
		if (this.playList.isEmpty()) {
			return null;
		}

		IAudioObject nextPreviouslyPlayedObject = this.playbackHistory
				.moveToNextInHistory();
		if (nextPreviouslyPlayedObject != null) {
			int index = this.playList.indexOf(nextPreviouslyPlayedObject);
			// Update pointed object
			this.playList.getAudioObjectsPointedList().setPointer(index);
			return nextPreviouslyPlayedObject;
		}

		// No next previously object
		if (isShuffle()) {
			Integer nextIndex = this.shufflePlayList.moveToNextObject();
			if (nextIndex == null) {
				return null;
			}
			// Update current index of play list and return object
			this.playList.setCurrentAudioObjectIndex(nextIndex);
			return this.playList.get(nextIndex);
		}

		return this.playList.getAudioObjectsPointedList().moveToNextObject();
	}

	@Override
	public IAudioObject getPreviousAudioObject(final int index) {
		if (this.playList.isEmpty()) {
			return null;
		}

		IAudioObject previosulyPlayedObject = this.playbackHistory
				.getPreviousInHistory(index);
		if (previosulyPlayedObject != null) {
			return previosulyPlayedObject;
		}

		// No previous object
		if (isShuffle()) {
			Integer previousIndex = this.shufflePlayList
					.getPreviousObject(index);
			if (previousIndex == null) {
				return null;
			}
			return this.playList.get(previousIndex);
		}

		return this.playList.getAudioObjectsPointedList().getPreviousObject(
				index);
	}

	@Override
	public IAudioObject getNextAudioObject(final int index) {
		if (this.playList.isEmpty()) {
			return null;
		}

		IAudioObject nextPreviouslyPlayedObject = this.playbackHistory
				.getNextInHistory(index);
		if (nextPreviouslyPlayedObject != null) {
			return nextPreviouslyPlayedObject;
		}

		// No next previously object
		if (isShuffle()) {
			Integer nextIndex = this.shufflePlayList.getNextObject(index);
			if (nextIndex == null) {
				return null;
			}
			return this.playList.get(nextIndex);
		}

		return this.playList.getAudioObjectsPointedList().getNextObject(index);
	}

	@Override
	public void audioObjectsAdded(
			final List<IPlayListAudioObject> audioObjectsAdded) {
		if (audioObjectsAdded == null || audioObjectsAdded.isEmpty()) {
			return;
		}

		// Update shuffle play list
		this.shufflePlayList.add(audioObjectsAdded);
	}

	@Override
	public void audioObjectsRemoved(
			final List<IPlayListAudioObject> audioObjectsRemoved) {
		if (audioObjectsRemoved == null || audioObjectsRemoved.isEmpty()) {
			return;
		}

		// Update shuffle list
		for (IPlayListAudioObject plao : audioObjectsRemoved) {
			int indexToRemove = this.shufflePlayList
					.indexOf(plao.getPosition());
			this.shufflePlayList.remove(indexToRemove);
		}

		// Update history
		// Only remove from history audio objects removed from play list
		// If an audio object is duplicated in play list and is in history, if
		// one of its occurrences is removed, history is not updated
		List<IAudioObject> audioObjectsToRemoveFromHistory = new ArrayList<IAudioObject>();
		for (IPlayListAudioObject plao : audioObjectsRemoved) {
			if (!this.playList.contains(plao.getAudioObject())) {
				// If audio object is no more present in play list then remove
				// from history
				audioObjectsToRemoveFromHistory.add(plao.getAudioObject());
			}
		}
		this.playbackHistory.remove(audioObjectsToRemoveFromHistory);
	}

	@Override
	public void audioObjectsRemovedAll() {
		this.shufflePlayList.clear();
		this.playbackHistory.clear();
	}

	private boolean isShuffle() {
		return this.statePlayer.isShuffle();
	}

	@Override
	public void addToPlaybackHistory(final IAudioObject object) {
		this.playbackHistory.addToHistory(object);
	}

	@Override
	public void reset() {
		this.shufflePlayList.setPointer(0);
	}
}
