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

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListObjectFilter;

public class PlayListRemoverFromDevice {

    private IPlayListObjectFilter<ILocalAudioObject> playListLocalAudioObjectFilter;
    
    private IPlayListController playListController;
    
    private IPlayListHandler playListHandler;
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
    
    /**
     * @param playListController
     */
    public void setPlayListController(IPlayListController playListController) {
		this.playListController = playListController;
	}
    
    /**
     * @param playListLocalAudioObjectFilter
     */
    public void setPlayListLocalAudioObjectFilter(IPlayListObjectFilter<ILocalAudioObject> playListLocalAudioObjectFilter) {
		this.playListLocalAudioObjectFilter = playListLocalAudioObjectFilter;
	}
    
	/**
	 * Remove audio objects of device from given playlist
	 * @param playList
	 * @param location
	 */
	void removeAudioObjectsOfDevice(IPlayList playList, String location) {
        List<Integer> songsToRemove = new ArrayList<Integer>();
        for (ILocalAudioObject audioFile : playListLocalAudioObjectFilter.getObjects(playList)) {
            if (audioFile.getFile().getPath().startsWith(location)) {
                songsToRemove.add(playList.indexOf(audioFile));
            }
        }
        int[] indexes = new int[songsToRemove.size()];
        for (int i = 0; i < songsToRemove.size(); i++) {
            indexes[i] = songsToRemove.get(i);
        }

        if (indexes.length > 0) {
            playListController.clearSelection();
            playListHandler.removeAudioObjects(indexes);
        }
	}
}