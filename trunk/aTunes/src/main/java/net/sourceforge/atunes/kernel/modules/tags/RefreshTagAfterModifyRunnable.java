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

package net.sourceforge.atunes.kernel.modules.tags;

import java.util.List;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IUIHandler;

final class RefreshTagAfterModifyRunnable implements Runnable {
	
	private List<ILocalAudioObject> audioFilesEditing;
	private IPlayListHandler playListHandler;
	private IPlayerHandler playerHandler;

	RefreshTagAfterModifyRunnable(List<ILocalAudioObject> audioFilesEditing, IPlayListHandler playListHandler, IPlayerHandler playerHandler) {
		this.audioFilesEditing = audioFilesEditing;
		this.playListHandler = playListHandler;
		this.playerHandler = playerHandler;
	}

	@Override
	public void run() {
	    // update Swing components if necessary
	    boolean playListContainsRefreshedFile = false;
	    for (int i = 0; i < audioFilesEditing.size(); i++) {
	        if (playListHandler.getCurrentPlayList(true).contains(audioFilesEditing.get(i))) {
	            playListContainsRefreshedFile = true;
	        }

	        // Changed current playing song
	        if (playListHandler.getCurrentAudioObjectFromCurrentPlayList() != null
	                && playListHandler.getCurrentAudioObjectFromCurrentPlayList().equals(audioFilesEditing.get(i))) {
	        	
	        	Context.getBean(PlayListEventListeners.class).selectedAudioObjectHasChanged(audioFilesEditing.get(i));

	            if (playerHandler.isEnginePlaying()) {
	                Context.getBean(IUIHandler.class).updateTitleBar(audioFilesEditing.get(i));
	            }
	        }
	    }
	    if (playListContainsRefreshedFile) {
	    	playListHandler.refreshPlayList();
	    }
	    Context.getBean(INavigationHandler.class).repositoryReloaded();
	}
}