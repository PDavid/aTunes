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
import java.util.List;

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchNode;

/**
 * Loads playlists in UI
 * 
 * @author alex
 * 
 */
public class LoadPlayListsBackgroundWorker extends
		BackgroundWorker<Void, IPlayList> {

	private PlayListHandler playListHandler;
	private IPlayListsContainer playListsContainer;
	private PlayListNameCreator playListNameCreator;
	private PlayListCreator playListCreator;
	private PlayListTabController playListTabController;
	private ISearchHandler searchHandler;

	private IListOfPlayLists listOfPlayLists;

	/**
	 * @param listOfPlayLists
	 * @return
	 */
	public LoadPlayListsBackgroundWorker setListOfPlayLists(
			final IListOfPlayLists listOfPlayLists) {
		this.listOfPlayLists = listOfPlayLists;
		return this;
	}

	@Override
	protected void before() {
	}

	@Override
	protected Void doInBackground() {
		for (IPlayList playlist : this.listOfPlayLists.getPlayLists()) {
			if (playlist instanceof DynamicPlayList) {
				String name = this.playListNameCreator.getNameForPlaylist(
						this.playListsContainer, playlist);
				ISearchNode query = ((DynamicPlayList) playlist).getQuery()
						.createSearchQuery(getBeanFactory());
				Collection<IAudioObject> initialObjects = this.searchHandler
						.search(query);
				publish(this.playListCreator
						.getNewDynamicPlayList(
								name,
								query,
								initialObjects,
								((DynamicPlayList) playlist).getPointer(),
								((DynamicPlayList) playlist).getColumnSorted() != null ? getBeanFactory()
										.getBeanByClassName(
												((DynamicPlayList) playlist)
														.getColumnSorted(),
												IColumn.class) : null));

			} else {
				publish(this.playListCreator.replaceAudioObjects(playlist));
			}
		}
		return null;
	}

	@Override
	protected void whileWorking(final List<IPlayList> chunks) {
		for (IPlayList playList : chunks) {
			this.playListHandler.addNewPlayList(playList);
		}
	}

	@Override
	protected void done(final Void result) {
		// Set selected play list as default
		int selected = this.listOfPlayLists.getSelectedPlayList();
		if (selected < 0
				|| selected >= this.listOfPlayLists.getPlayLists().size()) {
			selected = 0;
		}

		// Initially active play list and visible play list are the same
		this.playListsContainer.setActivePlayListIndex(selected);
		this.playListsContainer.setVisiblePlayListIndex(selected);
		this.playListTabController.forceSwitchTo(selected);

		this.playListHandler.setPlayList(this.playListsContainer
				.getPlayListAt(selected));

	}

	/**
	 * @param playListHandler
	 *            the playListHandler to set
	 */
	public void setPlayListHandler(final PlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param playListsContainer
	 *            the playListsContainer to set
	 */
	public void setPlayListsContainer(
			final IPlayListsContainer playListsContainer) {
		this.playListsContainer = playListsContainer;
	}

	/**
	 * @param playListNameCreator
	 *            the playListNameCreator to set
	 */
	public void setPlayListNameCreator(
			final PlayListNameCreator playListNameCreator) {
		this.playListNameCreator = playListNameCreator;
	}

	/**
	 * @param playListCreator
	 *            the playListCreator to set
	 */
	public void setPlayListCreator(final PlayListCreator playListCreator) {
		this.playListCreator = playListCreator;
	}

	/**
	 * @param playListTabController
	 *            the playListTabController to set
	 */
	public void setPlayListTabController(
			final PlayListTabController playListTabController) {
		this.playListTabController = playListTabController;
	}

	/**
	 * @param searchHandler
	 *            the searchHandler to set
	 */
	public void setSearchHandler(final ISearchHandler searchHandler) {
		this.searchHandler = searchHandler;
	}
}
