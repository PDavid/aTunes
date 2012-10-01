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

import java.util.Collection;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.PlayListEventListeners;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IUIHandler;

final class RefreshTagAfterModifyRunnable implements Runnable {
	
	private Collection<ILocalAudioObject> audioFilesEditing;
	private IPlayListHandler playListHandler;
	private IPlayerHandler playerHandler;

	RefreshTagAfterModifyRunnable(Collection<ILocalAudioObject> audioFilesEditing, IPlayListHandler playListHandler, IPlayerHandler playerHandler) {
		this.audioFilesEditing = audioFilesEditing;
		this.playListHandler = playListHandler;
		this.playerHandler = playerHandler;
	}

	@Override
	public void run() {
	    // update Swing components if necessary
	    boolean playListContainsRefreshedFile = false;
	    for (ILocalAudioObject lao : this.audioFilesEditing) {
	        if (playListHandler.getVisiblePlayList().contains(lao)) {
	            playListContainsRefreshedFile = true;
	        }

	        // Changed current playing song
	        if (playListHandler.getCurrentAudioObjectFromCurrentPlayList() != null
	                && playListHandler.getCurrentAudioObjectFromCurrentPlayList().equals(lao)) {
	        	
	        	Context.getBean(PlayListEventListeners.class).selectedAudioObjectHasChanged(lao);

	            if (playerHandler.isEnginePlaying()) {
	                Context.getBean(IUIHandler.class).updateTitleBar(lao);
	            }
	        }
	    }
	    if (playListContainsRefreshedFile) {
	    	playListHandler.refreshPlayList();
	    }
	    Context.getBean(INavigationHandler.class).repositoryReloaded();
	}
}