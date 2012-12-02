/*
 * aTunes 3.0.0
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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.IPlayList;

/**
 * Checks play lists to find non existent local audio objects
 * @author alex
 *
 */
public class PlayListsChecker {

	private PlayListsContainer playListsContainer;

	private ILocalAudioObjectFilter localAudioObjectFilter;

	/**
	 * @param localAudioObjectFilter
	 */
	public void setLocalAudioObjectFilter(
			final ILocalAudioObjectFilter localAudioObjectFilter) {
		this.localAudioObjectFilter = localAudioObjectFilter;
	}

	/**
	 * @param playListsContainer
	 */
	public void setPlayListsContainer(final PlayListsContainer playListsContainer) {
		this.playListsContainer = playListsContainer;
	}

	/**
	 * @return audio objects not found
	 */
	public List<ILocalAudioObject> checkPlayLists() {
		List<ILocalAudioObject> audioObjectsNotFound = new ArrayList<ILocalAudioObject>();
		for (int i = 0; i < playListsContainer.getPlayListsCount(); i++) {
			IPlayList playList = playListsContainer.getPlayListAt(i);
			for (ILocalAudioObject audioObject : localAudioObjectFilter.getLocalAudioObjects(playList.getAudioObjectsList())) {
				if (!exists(audioObject)) {
					audioObjectsNotFound.add(audioObject);
				}
			}
		}
		return audioObjectsNotFound;
	}

	private boolean exists(final IAudioObject audioObject) {
		// Currently only checks for local audio objects
		if (audioObject instanceof ILocalAudioObject) {
			return ((ILocalAudioObject)audioObject).getFile().exists();
		}
		return true;
	}
}
