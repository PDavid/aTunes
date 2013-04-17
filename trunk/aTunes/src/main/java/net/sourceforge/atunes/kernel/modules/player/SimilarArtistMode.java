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

package net.sourceforge.atunes.kernel.modules.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.PlaybackState;

/**
 * Manages similar artist mode
 * 
 * @author Laurent Cathala
 * 
 */
public class SimilarArtistMode {

	private Map<String, List<ILocalAudioObject>> notAlreadySelectedSongsForArtist;

	private IPlayListHandler playListHandler;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Initializes object
	 */
	public void initialize() {
		this.notAlreadySelectedSongsForArtist = new HashMap<String, List<ILocalAudioObject>>();
	}

	/**
	 * Enables this mode
	 * 
	 * @param enabled
	 */
	void setEnabled(final boolean enabled) {
		if (enabled) {
			addSimilarArtistsAudioObjects(this.playListHandler
					.getCurrentAudioObjectFromCurrentPlayList());
		} else {
			this.notAlreadySelectedSongsForArtist.clear();
		}
	}

	/**
	 * Called when playback state changes
	 * 
	 * @param newState
	 * @param currentAudioObject
	 */
	void playbackStateChanged(final PlaybackState newState,
			final IAudioObject currentAudioObject) {
		if (newState.equals(PlaybackState.PLAYING)) {
			addSimilarArtistsAudioObjects(currentAudioObject);
		}
	}

	/**
	 * @param currentAudioObject
	 */
	private void addSimilarArtistsAudioObjects(
			final IAudioObject currentAudioObject) {
		if (currentAudioObject != null) {
			IPlayList currentPlayList = this.playListHandler
					.getActivePlayList();
			if (currentPlayList.get(currentPlayList.size() - 1).equals(
					currentAudioObject)) {
				this.beanFactory.getBean(
						GetSimilarArtistAudioObjectsBackgroundWorker.class)
						.getSimilarArtists(currentAudioObject,
								this.notAlreadySelectedSongsForArtist);
			}
		}
	}
}
