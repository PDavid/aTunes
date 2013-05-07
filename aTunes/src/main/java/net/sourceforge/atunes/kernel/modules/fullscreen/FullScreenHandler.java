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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFullScreenHandler;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.IPlayListEventListener;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.model.PlaybackState;

/**
 * Manages full screen window
 * 
 * @author alex
 * 
 */
public class FullScreenHandler extends AbstractHandler implements
		IFullScreenHandler, IPlayListEventListener {

	private FullScreenController controller;

	private IPlayListHandler playListHandler;

	private IStatePlaylist statePlaylist;

	/**
	 * @param statePlaylist
	 */
	public void setStatePlaylist(final IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	@Override
	public void playListCleared() {
		// Next actions must be done ONLY if stopPlayerWhenPlayListClear is
		// enabled
		if (this.statePlaylist.isStopPlayerOnPlayListClear()
				&& getFullScreenController() != null) {
			getFullScreenController().setAudioObjects(null);
		}
	}

	@Override
	public void selectedAudioObjectChanged(final IAudioObject audioObject) {
		updateAudioObjectsToShow(audioObject);
	}

	@Override
	public void audioObjectsAdded(
			List<IPlayListAudioObject> playListAudioObjects) {
	}

	@Override
	public void audioObjectsRemoved(List<IPlayListAudioObject> audioObjectList) {
	}

	/**
	 * Returns full screen controller or null if not created, so it must be
	 * checked first
	 * 
	 * @return
	 */
	private FullScreenController getFullScreenController() {
		return this.controller;
	}

	/**
	 * Creates full screen controller, to be called only when needed
	 */
	private void createFullScreenController() {
		if (this.controller == null) {
			this.controller = getBean(FullScreenController.class);
		}
	}

	/**
	 * @param audioObject
	 */
	private void updateAudioObjectsToShow(final IAudioObject audioObject) {
		if (getFullScreenController() != null) {
			getFullScreenController().setAudioObjects(
					getAudioObjectsToShow(audioObject));
		}
	}

	/**
	 * Returns audio objects to show in full screen: 2 previous audio objects,
	 * current one, and next three objects
	 * 
	 * @param current
	 *            current
	 * @return
	 */
	private List<IAudioObject> getAudioObjectsToShow(final IAudioObject current) {
		List<IAudioObject> objects = new ArrayList<IAudioObject>(6);
		objects.add(this.playListHandler.getActivePlayList()
				.getPreviousAudioObject(2));
		objects.add(this.playListHandler.getActivePlayList()
				.getPreviousAudioObject(1));
		objects.add(current);
		objects.add(this.playListHandler.getActivePlayList()
				.getNextAudioObject(1));
		objects.add(this.playListHandler.getActivePlayList()
				.getNextAudioObject(2));

		// This object is not shown, but image is prefetched
		objects.add(this.playListHandler.getActivePlayList()
				.getNextAudioObject(3));
		return objects;
	}

	@Override
	public void toggleFullScreenVisibility() {
		// Here must create controller and window, as first call to this method
		// will be when full screen becomes visible
		createFullScreenController();

		// Be sure to update audio objects before show window
		if (!getFullScreenController().isVisible()) {
			updateAudioObjectsToShow(this.playListHandler
					.getCurrentAudioObjectFromCurrentPlayList());
		}
		getFullScreenController().toggleVisibility();
	}

	@Override
	public boolean isVisible() {
		if (getFullScreenController() != null) {
			return getFullScreenController().isVisible();
		}
		return false;
	}

	@Override
	public void playbackStateChanged(final PlaybackState newState,
			final IAudioObject currentAudioObject) {
		if (getFullScreenController() != null) {
			getFullScreenController().setPlaying(
					newState == PlaybackState.RESUMING
							|| newState == PlaybackState.PLAYING);
		}
	}
}
