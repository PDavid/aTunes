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

import net.sourceforge.atunes.kernel.actions.CloseOtherPlaylistsAction;
import net.sourceforge.atunes.kernel.actions.ClosePlaylistAction;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IPlayListHandler;

/**
 * The listener interface for receiving playListTab events.
 */
final class PlayListTabListener implements ActionListener, ItemListener {

    private final IPlayListHandler playListHandler;

    private final PlayListSelectorWrapper playListSelectorWrapper;

    private final IBeanFactory beanFactory;

    /**
     * Instantiates a new play list tab listener.
     * 
     * @param playListHandler
     * @param playListSelectorWrapper
     * @param beanFactory
     */
    public PlayListTabListener(final IPlayListHandler playListHandler,
	    final PlayListSelectorWrapper playListSelectorWrapper,
	    final IBeanFactory beanFactory) {
	this.playListHandler = playListHandler;
	this.playListSelectorWrapper = playListSelectorWrapper;
	this.beanFactory = beanFactory;
    }

    @Override
    public void itemStateChanged(final ItemEvent e) {
	playListSelectorWrapper.switchToPlaylist(playListSelectorWrapper
		.getSelectedPlayListIndex());
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
	boolean moreThanOnePlayList = playListHandler.getPlayListCount() > 1;
	beanFactory.getBean(ClosePlaylistAction.class).setEnabled(
		moreThanOnePlayList);
	beanFactory.getBean(CloseOtherPlaylistsAction.class).setEnabled(
		moreThanOnePlayList);
    }

}
