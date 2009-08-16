/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.controllers.playListTab;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.atunes.gui.views.panels.PlayListTabPanel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.CloseOtherPlaylistsAction;
import net.sourceforge.atunes.kernel.actions.ClosePlaylistAction;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The listener interface for receiving playListTab events.
 */
class PlayListTabListener extends MouseAdapter implements ActionListener, ChangeListener {

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
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == panel.getPlayListsPopUpButton()) {
            panel.getPlayListsPopUpButton().removeAllItems();
            panel.addFixedMenuItems();
            // Get list of play lists and add a new menu item for each one
            List<String> playlists = ControllerProxy.getInstance().getPlayListTabController().getNamesOfPlayLists();
            for (int i = 0; i < playlists.size(); i++) {
                final int index = i;
                JMenuItem plMenuItem;
                if (PlayListHandler.getInstance().isVisiblePlayList(index)) {
                    plMenuItem = new JMenuItem(StringUtils.getString("<html><b>", playlists.get(i), "</b></html>"));
                } else {
                    plMenuItem = new JMenuItem(playlists.get(i));
                }
                plMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e1) {
                        ControllerProxy.getInstance().getPlayListTabController().forceSwitchTo(index);
                    }
                });
                panel.getPlayListsPopUpButton().add(plMenuItem);
            }
        } else if (e.getSource() == panel.getArrangeColumnsMenuItem()) {
            ControllerProxy.getInstance().getPlayListController().arrangeColumns();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Selected play list changes when clicking mouse so all popup actions work with current play list
        if (e.getButton() == MouseEvent.BUTTON3) {
            boolean moreThanOnePlayList = PlayListHandler.getInstance().getPlayListCount() > 1;
            Actions.getAction(ClosePlaylistAction.class).setEnabled(moreThanOnePlayList);
            Actions.getAction(CloseOtherPlaylistsAction.class).setEnabled(moreThanOnePlayList);
            panel.getPopupMenu().show(panel, e.getX(), e.getY());
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        PlayListHandler.getInstance().switchToPlaylist(panel.getPlayListTabbedPane().getSelectedIndex());
    }

}
