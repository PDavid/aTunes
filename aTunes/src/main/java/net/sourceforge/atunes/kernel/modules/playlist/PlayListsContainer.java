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
import java.util.List;

import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Container holding playlists
 * 
 * @author alex
 * 
 */
public final class PlayListsContainer implements IPlayListsContainer {

	/** The play lists currently opened. */
	private final List<IPlayList> playLists = new ArrayList<IPlayList>();

	/** Stores original play list without filter. */
	private IPlayList nonFilteredPlayList;

	/** Index of the active play list */
	private int activePlayListIndex = 0;

	/** Index of the visible play list: can be different of active play list */
	private int visiblePlayListIndex = 0;

	private PlayListHandler playListHandler;

	private PlayListCreator playListCreator;

	/**
	 * @param playListCreator
	 */
	public void setPlayListCreator(final PlayListCreator playListCreator) {
		this.playListCreator = playListCreator;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final PlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Sets visible play list as active
	 */
	@Override
	public void setVisiblePlayListActive() {
		activePlayListIndex = visiblePlayListIndex;
	}

	/**
	 * Returns number of play lists
	 * 
	 * @return
	 */
	@Override
	public int getPlayListsCount() {
		return playLists.size();
	}

	/**
	 * Index of visible play list
	 * 
	 * @return
	 */
	@Override
	public int getVisiblePlayListIndex() {
		return visiblePlayListIndex;
	}

	/**
	 * Index of active play list
	 * 
	 * @return
	 */
	@Override
	public int getActivePlayListIndex() {
		return activePlayListIndex;
	}

	/**
	 * @param activePlayListIndex
	 */
	@Override
	public void setActivePlayListIndex(final int activePlayListIndex) {
		if (activePlayListIndex < 0 || activePlayListIndex >= playLists.size()) {
			throw new IllegalArgumentException(StringUtils.getString(
					"Wrong activePlayListIndex: ", activePlayListIndex));
		}
		this.activePlayListIndex = activePlayListIndex;
	}

	/**
	 * @param visiblePlayListIndex
	 */
	@Override
	public void setVisiblePlayListIndex(final int visiblePlayListIndex) {
		if (visiblePlayListIndex < 0
				|| visiblePlayListIndex >= playLists.size()) {
			throw new IllegalArgumentException(StringUtils.getString(
					"Wrong visiblePlayListIndex: ", visiblePlayListIndex));
		}
		this.visiblePlayListIndex = visiblePlayListIndex;
	}

	/**
	 * Removes play list of given index
	 * 
	 * @param index
	 */
	@Override
	public void removePlayList(final int index) {
		playLists.remove(index);
	}

	/**
	 * Returns play list at index
	 * 
	 * @param i
	 * @return
	 */
	@Override
	public IPlayList getPlayListAt(final int i) {
		if (i < 0 || i >= playLists.size()) {
			throw new IllegalArgumentException(StringUtils.getString(
					"Wrong index ", i));
		}
		return playLists.get(i);
	}

	/**
	 * Adds play list
	 * 
	 * @param newPlayList
	 */
	@Override
	public void addPlayList(final IPlayList newPlayList) {
		playLists.add(newPlayList);
	}

	/**
	 * Returns visible play list
	 */
	@Override
	public IPlayList getVisiblePlayList() {
		if (getPlayListsCount() == 0) {
			return new VoidPlayList();
		}
		// When upgrading from a previous version this can happen if playlists
		// can't be read
		if (visiblePlayListIndex >= playLists.size()) {
			Logger.error("Visible play list index = ",
					Integer.toString(visiblePlayListIndex),
					" greater than number of playlists: ",
					Integer.toString(playLists.size()));
			visiblePlayListIndex = 0;
		}
		return playLists.get(visiblePlayListIndex);
	}

	@Override
	public IPlayList getActivePlayList() {
		if (getPlayListsCount() == 0) {
			return new VoidPlayList();
		}

		// When upgrading from a previous version this can happen if playlists
		// can't be read
		if (activePlayListIndex >= playLists.size()) {
			Logger.error("Active play list index = ",
					Integer.toString(activePlayListIndex),
					" greater than number of playlists: ",
					Integer.toString(playLists.size()));
			activePlayListIndex = 0;
		}
		return playLists.get(activePlayListIndex);
	}

	/**
	 * Clear play lists
	 */
	@Override
	public void clear() {
		playLists.clear();
	}

	/**
	 * Adds play list at given position
	 * 
	 * @param position
	 * @param playList
	 */
	@Override
	public void addPlayList(final int position, final IPlayList playList) {
		playLists.add(position, playList);
	}

	@Override
	public IPlayList getNonFilteredPlayList() {
		return nonFilteredPlayList;
	}

	@Override
	public boolean isFiltered() {
		return nonFilteredPlayList != null;
	}

	@Override
	public void setFilter(final String filter) {
		String filterText = filter;

		// If filter is null, remove previous filter
		if (filterText == null) {
			// If play list was filtered, back to non-filtered play list
			if (nonFilteredPlayList != null) {
				if (getVisiblePlayList().getCurrentAudioObject() != null) {
					// User selected another object -> keep that object selected
					// in original play list
					nonFilteredPlayList
							.setCurrentAudioObjectIndex(nonFilteredPlayList
									.indexOf(getVisiblePlayList()
											.getCurrentAudioObject()));
				}
				setPlayListAfterFiltering(nonFilteredPlayList);
				nonFilteredPlayList = null;
			}
		} else {
			// Store original play list without filter
			if (nonFilteredPlayList == null) {
				nonFilteredPlayList = getVisiblePlayList().copyPlayList();
			}

			// Create a new play list by filtering elements
			IPlayList newPlayList = playListCreator.getNewPlayListWithFilter(
					nonFilteredPlayList, filterText);
			if (newPlayList.contains(nonFilteredPlayList
					.getCurrentAudioObject())) {
				newPlayList.setCurrentAudioObjectIndex(newPlayList
						.indexOf(nonFilteredPlayList.getCurrentAudioObject()));
			}
			setPlayListAfterFiltering(newPlayList);
		}
	}

	/**
	 * Sets the play list after filtering.
	 * 
	 * @param playList
	 *            the new play list after filtering
	 */
	private void setPlayListAfterFiltering(final IPlayList playList) {
		removePlayList(getVisiblePlayListIndex());
		addPlayList(getVisiblePlayListIndex(), playList);
		playListHandler.setPlayList(playList);
	}
}
