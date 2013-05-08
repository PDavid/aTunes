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

import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListObjectFilter;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Removes device audio objects from play list
 * 
 * @author alex
 * 
 */
public class PlayListRemoverFromDevice {

	private IPlayListObjectFilter<ILocalAudioObject> playListLocalAudioObjectFilter;

	private IPlayListController playListController;

	private IPlayListHandler playListHandler;

	private IFileManager fileManager;

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param playListController
	 */
	public void setPlayListController(
			final IPlayListController playListController) {
		this.playListController = playListController;
	}

	/**
	 * @param playListLocalAudioObjectFilter
	 */
	public void setPlayListLocalAudioObjectFilter(
			final IPlayListObjectFilter<ILocalAudioObject> playListLocalAudioObjectFilter) {
		this.playListLocalAudioObjectFilter = playListLocalAudioObjectFilter;
	}

	/**
	 * Remove audio objects of device from given playlist
	 * 
	 * @param playList
	 * @param location
	 */
	void removeAudioObjectsOfDevice(final IPlayList playList,
			final String location) {
		if (!StringUtils.isEmpty(location)) {
			List<Integer> songsToRemove = new ArrayList<Integer>();
			for (ILocalAudioObject audioFile : this.playListLocalAudioObjectFilter
					.getObjects(playList)) {
				if (this.fileManager.getPath(audioFile).startsWith(location)) {
					songsToRemove.add(playList.indexOf(audioFile));
				}
			}
			int[] indexes = new int[songsToRemove.size()];
			for (int i = 0; i < songsToRemove.size(); i++) {
				indexes[i] = songsToRemove.get(i);
			}

			if (indexes.length > 0) {
				this.playListController.clearSelection();
				this.playListHandler.removeAudioObjects(indexes);
			}
		}
	}
}
