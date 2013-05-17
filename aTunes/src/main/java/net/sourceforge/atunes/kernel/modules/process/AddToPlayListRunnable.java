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

package net.sourceforge.atunes.kernel.modules.process;

import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;

class AddToPlayListRunnable implements Runnable {

	private List<IAudioObject> songsLoaded;

	private IPlayListHandler playListHandler;

	private String playListName;

	private boolean replacePlayList;

	/**
	 * @param songsLoaded
	 * @param playListHandler
	 * @param playListName
	 * @param replacePlayList
	 */
	public AddToPlayListRunnable(List<IAudioObject> songsLoaded,
			IPlayListHandler playListHandler, String playListName,
			boolean replacePlayList) {
		this.songsLoaded = songsLoaded;
		this.playListHandler = playListHandler;
		this.playListName = playListName;
		this.replacePlayList = replacePlayList;
	}

	@Override
	public void run() {
		if (songsLoaded.size() >= 1) {
			if (this.replacePlayList) {
				playListHandler.clearPlayList();
				playListHandler.addToVisiblePlayList(songsLoaded);
				playListHandler.renameCurrentVisiblePlayList(playListName);
			} else {
				playListHandler.newPlayList(playListName, songsLoaded);
			}
		}
	}
}