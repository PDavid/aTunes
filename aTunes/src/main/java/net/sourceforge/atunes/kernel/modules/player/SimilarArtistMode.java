/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.kernel.modules.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBackgroundWorker;
import net.sourceforge.atunes.model.IBackgroundWorker.IActionsWithBackgroundResult;
import net.sourceforge.atunes.model.IBackgroundWorkerFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.PlaybackState;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Manages similar artist mode
 * @author Laurent Cathala
 *
 */
public class SimilarArtistMode implements ApplicationContextAware {

	private Map<String, List<ILocalAudioObject>> notAlreadySelectedSongsForArtist;

	private IPlayListHandler playListHandler;

	private IBackgroundWorkerFactory backgroundWorkerFactory;

	private ApplicationContext context;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.context = applicationContext;
	}

	/**
	 * @param backgroundWorkerFactory
	 */
	public void setBackgroundWorkerFactory(final IBackgroundWorkerFactory backgroundWorkerFactory) {
		this.backgroundWorkerFactory = backgroundWorkerFactory;
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
		notAlreadySelectedSongsForArtist = new HashMap<String, List<ILocalAudioObject>>();
	}

	/**
	 * Enables this mode
	 * @param enabled
	 */
	void setEnabled(final boolean enabled) {
		if (enabled) {
			addSimilarArtistsAudioObjects(playListHandler.getCurrentAudioObjectFromCurrentPlayList());
		} else {
			notAlreadySelectedSongsForArtist.clear();
		}
	}

	/**
	 * Called when playback state changes
	 * @param newState
	 * @param currentAudioObject
	 */
	void playbackStateChanged(final PlaybackState newState, final IAudioObject currentAudioObject) {
		if (newState.equals(PlaybackState.PLAYING)) {
			addSimilarArtistsAudioObjects(currentAudioObject);
		}
	}

	/**
	 * @param currentAudioObject
	 */
	private void addSimilarArtistsAudioObjects(final IAudioObject currentAudioObject) {
		if (currentAudioObject != null) {
			IPlayList currentPlayList = playListHandler.getActivePlayList();
			if (currentPlayList.get(currentPlayList.size() - 1).equals(currentAudioObject)) {
				IBackgroundWorker<List<IAudioObject>> worker = backgroundWorkerFactory.getWorker();
				GetSimilarArtistAudioObjectsCallable callable = context.getBean(GetSimilarArtistAudioObjectsCallable.class);
				callable.setArtistName(currentAudioObject.getArtist(unknownObjectChecker));
				callable.setNotAlreadySelectedSongsForArtist(notAlreadySelectedSongsForArtist);
				worker.setBackgroundActions(callable);
				worker.setActionsWhenDone(new IActionsWithBackgroundResult<List<IAudioObject>>() {

					@Override
					public void call(final List<IAudioObject> result) {
						playListHandler.addToVisiblePlayList(result);
					}
				});
				worker.execute();
			}
		}
	}
}
