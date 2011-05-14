/*
 * aTunes 2.1.0-SNAPSHOT
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

import net.sourceforge.atunes.gui.views.panels.PlayListTabPanel;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.CloseOtherPlaylistsAction;
import net.sourceforge.atunes.kernel.actions.ClosePlaylistAction;

/**
 * The listener interface for receiving playListTab events.
 */
final class PlayListTabListener implements ActionListener, ItemListener {

    private PlayListTabPanel panel;

    /**
     * Instantiates a new play list tab listener.
     * 
     * @param panel
     *            the panel
     */
    public PlayListTabListener(PlayListTabPanel panel) {
        this.panel = panel;
    }

	@Override
	public void itemStateChanged(ItemEvent e) {
        PlayListHandler.getInstance().switchToPlaylist(panel.getPlayListCombo().getSelectedIndex());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        boolean moreThanOnePlayList = PlayListHandler.getInstance().getPlayListCount() > 1;
        Actions.getAction(ClosePlaylistAction.class).setEnabled(moreThanOnePlayList);
        Actions.getAction(CloseOtherPlaylistsAction.class).setEnabled(moreThanOnePlayList);
	}

}
