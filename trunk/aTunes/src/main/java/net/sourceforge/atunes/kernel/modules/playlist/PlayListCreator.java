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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearchNode;
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

	private IRepositoryHandler repositoryHandler;

	private IAudioObjectComparator audioObjectComparator;

	/**
	 * @param audioObjectComparator
	 */
	public void setAudioObjectComparator(
			final IAudioObjectComparator audioObjectComparator) {
		this.audioObjectComparator = audioObjectComparator;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
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
	 * Returns a new play list without listeners bound
	 * 
	 * @param nameOfNewPlayList
	 * @param audioObjects
	 * @return
	 */
	IPlayList getNewPlayList(final String nameOfNewPlayList,
			final List<? extends IAudioObject> audioObjects) {
		IPlayList newPlayList;
		if (audioObjects == null) {
			newPlayList = new PlayList(this.statePlayer);
		} else {
			newPlayList = new PlayList(audioObjects, this.statePlayer);
		}
		newPlayList.setName(nameOfNewPlayList);
		return newPlayList;
	}

	/**
	 * Returns a new dynamic play list without listeners bound
	 * 
	 * @param name
	 * @param query
	 * @param initialObjects
	 * @param pointer
	 * @param sortedColumn
	 * @return
	 */
	IPlayList getNewDynamicPlayList(final String name, final ISearchNode query,
			final Collection<IAudioObject> initialObjects, final int pointer,
			final IColumn<?> columnSorted) {
		IPlayList newPlayList;
		if (initialObjects == null) {
			newPlayList = new DynamicPlayList(this.statePlayer,
					query.getRepresentation(), pointer);
		} else {
			List<IAudioObject> sortedList = new ArrayList<IAudioObject>(
					initialObjects);
			if (columnSorted != null) {
				Collections.sort(sortedList, columnSorted.getComparator());
			} else {
				this.audioObjectComparator.sort(sortedList);
			}

			this.audioObjectComparator.sort(sortedList);
			newPlayList = new DynamicPlayList(sortedList, this.statePlayer,
					query.getRepresentation(), pointer);
		}
		newPlayList.setName(name);
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

	/**
	 * Returns a play list where local audio objects are replaced by cached
	 * versions in repository
	 * 
	 * @param playlist
	 * @return
	 */
	IPlayList replaceAudioObjects(final IPlayList playlist) {
		List<IAudioObject> list = new ArrayList<IAudioObject>();
		for (IAudioObject ao : playlist.getAudioObjectsList()) {
			list.add(this.repositoryHandler.getAudioObjectIfLoaded(ao));
		}
		IPlayList newPlayList = getNewPlayList(playlist.getName(), list);
		newPlayList.setCurrentAudioObjectIndex(playlist
				.getCurrentAudioObjectIndex());
		return newPlayList;
	}
}
