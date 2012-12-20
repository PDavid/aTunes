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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.List;

import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IStatePlayer;

/**
 * Creates play lists
 * 
 * @author alex
 * 
 */
public class PlayListCreator {

	private IStatePlayer statePlayer;

	private IColumnSet playListColumnSet;

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
	 * @param playListColumnSet
	 */
	public void setPlayListColumnSet(final IColumnSet playListColumnSet) {
		this.playListColumnSet = playListColumnSet;
	}

	/**
	 * Returns a new play list
	 * 
	 * @param nameOfNewPlayList
	 * @param audioObjects
	 * @return
	 */
	IPlayList getNewPlayList(final String nameOfNewPlayList,
			final List<? extends IAudioObject> audioObjects) {
		PlayList newPlayList;
		if (audioObjects == null) {
			newPlayList = new PlayList(this.statePlayer,
					this.playListEventListeners);
		} else {
			newPlayList = new PlayList(audioObjects, this.statePlayer,
					this.playListEventListeners);
		}
		newPlayList.setName(nameOfNewPlayList);
		return newPlayList;
	}

	/**
	 * Returns a new play list with filtered audio objects from given play list
	 * 
	 * @param playList
	 * @param filter
	 * @return
	 */
	IPlayList getNewPlayListWithFilter(final IPlayList playList,
			final String filter) {
		return getNewPlayList(
				playList.getName(),
				this.playListColumnSet.filterAudioObjects(
						playList.getAudioObjectsList(), filter));
	}
}
