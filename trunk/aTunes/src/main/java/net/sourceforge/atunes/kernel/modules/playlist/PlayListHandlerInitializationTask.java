/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.playlist;

import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.model.IHandlerBackgroundInitializationTask;
import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStatePlayer;

/**
 * Reads playlists
 * 
 * @author alex
 * 
 */
public class PlayListHandlerInitializationTask implements
		IHandlerBackgroundInitializationTask {

	private PlayListHandler playListHandler;

	private IStateHandler stateHandler;

	private IStatePlayer statePlayer;

	private PlayListEventListeners playListEventListeners;

	/**
	 * @param playListEventListeners
	 */
	public void setPlayListEventListeners(
			final PlayListEventListeners playListEventListeners) {
		this.playListEventListeners = playListEventListeners;
	}

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(final IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	/**
	 * @param stateHandler
	 */
	public void setStateHandler(final IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final PlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	@Override
	public Runnable getInitializationTask() {
		return new Runnable() {
			@Override
			public void run() {
				IListOfPlayLists list = PlayListHandlerInitializationTask.this.stateHandler
						.retrievePlayListsCache();
				if (list == null) {
					list = ListOfPlayLists
							.getEmptyPlayList(
									PlayListHandlerInitializationTask.this.statePlayer,
									PlayListHandlerInitializationTask.this.playListEventListeners);
				}
				PlayListHandlerInitializationTask.this.playListHandler
						.setPlayListsRetrievedFromCache(list);
			}
		};
	}

	@Override
	public Runnable getInitializationCompletedTask() {
		return new Runnable() {
			@Override
			public void run() {
				PlayListHandlerInitializationTask.this.playListHandler
						.initializationTaskCompleted();
			}
		};
	}

}