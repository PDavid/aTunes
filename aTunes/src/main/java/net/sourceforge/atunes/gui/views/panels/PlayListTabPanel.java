/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.CloseOtherPlaylistsAction;
import net.sourceforge.atunes.kernel.actions.ClosePlaylistAction;
import net.sourceforge.atunes.kernel.actions.CopyPlayListToDeviceAction;
import net.sourceforge.atunes.kernel.actions.RenamePlaylistAction;
import net.sourceforge.atunes.kernel.actions.SynchronizeDeviceWithPlayListAction;
import net.sourceforge.atunes.utils.GuiUtils;

public final class PlayListTabPanel extends JPanel {

    private static final long serialVersionUID = 7382098268271937439L;

    /** TabbedPane of play lists. */
    private JTabbedPane playListTabbedPane;

    private JPopupMenu popupMenu;

    /**
     * Instantiates a new play list tab panel.
     */
    public PlayListTabPanel() {
        super(new BorderLayout());
        addContent();
    }

    /**
     * Adds the content.
     */
    private void addContent() {
        playListTabbedPane = new JTabbedPane();
        playListTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        add(playListTabbedPane, BorderLayout.CENTER);

        popupMenu = new JPopupMenu();
        popupMenu.add(Actions.getAction(RenamePlaylistAction.class));
        popupMenu.addSeparator();
        popupMenu.add(Actions.getAction(CopyPlayListToDeviceAction.class));
        popupMenu.add(Actions.getAction(SynchronizeDeviceWithPlayListAction.class));
        popupMenu.addSeparator();
        popupMenu.add(Actions.getAction(ClosePlaylistAction.class));
        popupMenu.add(Actions.getAction(CloseOtherPlaylistsAction.class));
        GuiUtils.applyComponentOrientation(this, popupMenu);
    }

    /**
     * Gets the play list tabbed pane.
     * 
     * @return the playListTabbedPane
     */
    public JTabbedPane getPlayListTabbedPane() {
        return playListTabbedPane;
    }

    /**
     * Gets the popup menu.
     * 
     * @return the popupMenu
     */
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }
}
