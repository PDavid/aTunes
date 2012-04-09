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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.utils.StringUtils;

public final class PlayListsContainer implements IPlayListsContainer {

    /** The play lists currently opened. */
    private List<IPlayList> playLists = new ArrayList<IPlayList>();

    /** Index of the active play list */
    private int activePlayListIndex = 0;

    /** Index of the visible play list: can be different of active play list */
    private int visiblePlayListIndex = 0;

	/**
	 * Sets visible play list as active
	 */
	@Override
	public void setVisiblePlayListActive() {
        activePlayListIndex = visiblePlayListIndex;
	}
	
	/**
	 * Returns number of play lists
	 * @return
	 */
	@Override
	public int getPlayListsCount() {
		return playLists.size();
	}
	
	/**
	 * Index of visible play list
	 * @return
	 */
	@Override
	public int getVisiblePlayListIndex() {
		return visiblePlayListIndex;
	}
	
	/**
	 * Index of active play list
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
	public void setActivePlayListIndex(int activePlayListIndex) {
		this.activePlayListIndex = activePlayListIndex;
	}
	
	/**
	 * @param visiblePlayListIndex
	 */
	@Override
	public void setVisiblePlayListIndex(int visiblePlayListIndex) {
		this.visiblePlayListIndex = visiblePlayListIndex;
	}

	/**
	 * Removes play list of given index
	 * @param index
	 */
	@Override
	public void removePlayList(int index) {
		playLists.remove(index);
	}

	/**
	 * Returns play list at index
	 * @param i
	 * @return
	 */
	@Override
	public IPlayList getPlayListAt(int i) {
		if (i < 0 || i >= playLists.size()) {
			throw new IllegalArgumentException(StringUtils.getString("Wrong index ", i));
		}
		return playLists.get(i);
	}

	/**
	 * Adds play list
	 * @param newPlayList
	 */
	@Override
	public void addPlayList(IPlayList newPlayList) {
        playLists.add(newPlayList);
	}

	/**
	 * Returns visible or active play list
	 * @param visible
	 */
	@Override
	public IPlayList getCurrentPlayList(boolean visible) {
        if (getPlayListsCount() == 0) {
            return null;
        }

        if (visible) {
            return playLists.get(visiblePlayListIndex);
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
	 * @param position
	 * @param playList
	 */
	@Override
	public void addPlayList(int position, IPlayList playList) {
		playLists.add(position, playList);
	}

	/**
	 * Changes position of two play lists
	 * @param from
	 * @param to
	 */
	@Override
	public void movePlayListToPosition(int from, int to) {
        IPlayList activePlayList = playLists.get(activePlayListIndex);
        IPlayList visiblePlayList = playLists.get(visiblePlayListIndex);
        IPlayList playList = playLists.remove(from);
        playLists.add(to, playList);
        activePlayListIndex = playLists.indexOf(activePlayList);
        visiblePlayListIndex = playLists.indexOf(visiblePlayList);
	}
}
