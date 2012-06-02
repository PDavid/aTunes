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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.actions.CloseOtherPlaylistsAction;
import net.sourceforge.atunes.kernel.actions.ClosePlaylistAction;
import net.sourceforge.atunes.model.IPlayListHandler;

/**
 * The listener interface for receiving playListTab events.
 */
final class PlayListTabListener implements ActionListener, ItemListener {

    private IPlayListHandler playListHandler;
    
    private PlayListSelectorWrapper playListSelectorWrapper;
    
    /**
     * Instantiates a new play list tab listener.
     * @param playListHandler
     * @param playListSelectorWrapper
     */
    public PlayListTabListener(IPlayListHandler playListHandler, PlayListSelectorWrapper playListSelectorWrapper) {
        this.playListHandler = playListHandler;
        this.playListSelectorWrapper = playListSelectorWrapper;
    }

	@Override
	public void itemStateChanged(ItemEvent e) {
		playListSelectorWrapper.switchToPlaylist(playListSelectorWrapper.getSelectedPlayListIndex());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        boolean moreThanOnePlayList = playListHandler.getPlayListCount() > 1;
        Context.getBean(ClosePlaylistAction.class).setEnabled(moreThanOnePlayList);
        Context.getBean(CloseOtherPlaylistsAction.class).setEnabled(moreThanOnePlayList);
	}

}
