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

package net.sourceforge.atunes.kernel.actions;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Starts playing immediately given audio object
 * 
 * @author alex
 * 
 */
public class PlayNowAction extends CustomAbstractAction {

    private static final long serialVersionUID = -2099290583376403144L;

    private IPlayListHandler playListHandler;

    private INavigationHandler navigationHandler;

    private IPlayerHandler playerHandler;

    /**
     * @param playerHandler
     */
    public void setPlayerHandler(final IPlayerHandler playerHandler) {
	this.playerHandler = playerHandler;
    }

    /**
     * @param playListHandler
     */
    public void setPlayListHandler(final IPlayListHandler playListHandler) {
	this.playListHandler = playListHandler;
    }

    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(final INavigationHandler navigationHandler) {
	this.navigationHandler = navigationHandler;
    }

    /**
     * Default constructor
     */
    public PlayNowAction() {
	super(I18nUtils.getString("PLAY_NOW"));
    }

    @Override
    protected void executeAction() {
	playNow(navigationHandler.getSelectedAudioObjectInNavigationTable());
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(
	    final List<IAudioObject> selection) {
	return selection != null && selection.size() == 1;
    }

    void playNow(final IAudioObject audioObject) {
	// Play now feature plays selected song immediately. If song is added to
	// play list, then is automatically selected.
	// If not, it's added to play list
	if (!playListHandler.getVisiblePlayList().contains(audioObject)) {
	    List<IAudioObject> list = new ArrayList<IAudioObject>();
	    list.add(audioObject);
	    addToPlayListAndPlay(list);
	} else {
	    playerHandler
		    .playAudioObjectInPlayListPositionOfVisiblePlayList(playListHandler
			    .getVisiblePlayList().indexOf(audioObject));
	}
    }

    private void addToPlayListAndPlay(final List<IAudioObject> audioObjects) {
	if (audioObjects == null || audioObjects.isEmpty()) {
	    return;
	}

	int playListCurrentSize = playListHandler.getVisiblePlayList().size();
	playListHandler.addToVisiblePlayList(audioObjects);
	playerHandler
		.playAudioObjectInPlayListPositionOfVisiblePlayList(playListCurrentSize);
    }
}
