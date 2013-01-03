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

package net.sourceforge.atunes.kernel.modules.tags;

import java.util.Collection;

import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IUIHandler;

/**
 * Refreshes tags after being modified
 * 
 * @author alex
 * 
 */
public final class RefreshTagAfterModifyRunnable implements Runnable {

	private Collection<ILocalAudioObject> audioFilesEditing;
	private IPlayListHandler playListHandler;
	private IPlayerHandler playerHandler;
	private INavigationHandler navigationHandler;
	private IUIHandler uiHandler;
	private PlayListEventListeners playListEventListeners;

	/**
	 * @param playListEventListeners
	 */
	public void setPlayListEventListeners(
			final PlayListEventListeners playListEventListeners) {
		this.playListEventListeners = playListEventListeners;
	}

	/**
	 * @param uiHandler
	 */
	public void setUiHandler(final IUIHandler uiHandler) {
		this.uiHandler = uiHandler;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(final IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param audioFilesEditing
	 */
	public void setAudioFilesEditing(
			final Collection<ILocalAudioObject> audioFilesEditing) {
		this.audioFilesEditing = audioFilesEditing;
	}

	@Override
	public void run() {
		// update Swing components if necessary
		boolean playListContainsRefreshedFile = false;
		for (ILocalAudioObject lao : this.audioFilesEditing) {
			if (this.playListHandler.getVisiblePlayList().contains(lao)) {
				playListContainsRefreshedFile = true;
			}

			// Changed current playing song
			if (this.playListHandler.getCurrentAudioObjectFromCurrentPlayList() != null
					&& this.playListHandler
							.getCurrentAudioObjectFromCurrentPlayList().equals(
									lao)) {

				this.playListEventListeners.selectedAudioObjectHasChanged(lao);

				if (this.playerHandler.isEnginePlaying()) {
					this.uiHandler.updateTitleBar(lao);
				}
			}
		}
		if (playListContainsRefreshedFile) {
			this.playListHandler.refreshPlayList();
		}
		this.navigationHandler.repositoryReloaded();
	}
}