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

import java.util.Collection;

import net.sourceforge.atunes.kernel.AbstractStateRetrieveTask;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.model.IStateService;

/**
 * Reads playlists
 * 
 * @author alex
 * 
 */
public class PlayListHandlerInitializationTask extends
		AbstractStateRetrieveTask {

	private IListOfPlayLists list;

	@Override
	public void retrieveData(final IStateService stateService,
			final IBeanFactory beanFactory) {
		this.list = stateService.retrievePlayListsCache();
	}

	@Override
	public void setData(final IBeanFactory beanFactory) {
		if (this.list == null) {
			this.list = ListOfPlayLists.getEmptyPlayList(beanFactory
					.getBean(IStatePlayer.class));
		}
		initializationTaskCompleted(beanFactory.getBean(PlayListHandler.class),
				beanFactory.getBean(IPlayListsContainer.class),
				beanFactory.getBean(PlayListNameCreator.class),
				beanFactory.getBean(PlayListCreator.class),
				beanFactory.getBean(PlayListTabController.class),
				beanFactory.getBean(ISearchHandler.class), beanFactory);
	}

	/**
	 * Called after initialization task completed
	 */
	private void initializationTaskCompleted(
			final PlayListHandler playListHandler,
			final IPlayListsContainer playListsContainer,
			final PlayListNameCreator playListNameCreator,
			final PlayListCreator playListCreator,
			final PlayListTabController playListTabController,
			final ISearchHandler searchHandler, final IBeanFactory beanFactory) {
		// Playlists need to be loaded before other handlers access them in
		// allHandlersInitialized
		// Get playlists from application cache
		final IListOfPlayLists listOfPlayLists = this.list;

		// Set selected play list as default
		int selected = listOfPlayLists.getSelectedPlayList();
		if (selected < 0 || selected >= listOfPlayLists.getPlayLists().size()) {
			selected = 0;
		}

		// Add playlists
		playListsContainer.clear();
		for (IPlayList playlist : listOfPlayLists.getPlayLists()) {
			if (playlist instanceof DynamicPlayList) {
				String name = playListNameCreator.getNameForPlaylist(
						playListsContainer, playlist);
				ISearchNode query = ((DynamicPlayList) playlist).getQuery()
						.createSearchQuery(beanFactory);
				Collection<IAudioObject> initialObjects = searchHandler
						.search(query);
				playListHandler.addNewPlayList(name, playListCreator
						.getNewDynamicPlayList(name, query, initialObjects,
								((DynamicPlayList) playlist).getPointer()));

			} else {
				playListHandler.addNewPlayList(playListNameCreator
						.getNameForPlaylist(playListsContainer, playlist),
						playListCreator.replaceAudioObjects(playlist));
			}
		}
		// Initially active play list and visible play list are the same
		playListsContainer.setActivePlayListIndex(selected);
		playListsContainer.setVisiblePlayListIndex(selected);
		playListTabController.forceSwitchTo(selected);

		playListHandler.setPlayList(playListsContainer.getPlayListAt(selected));
	}
}