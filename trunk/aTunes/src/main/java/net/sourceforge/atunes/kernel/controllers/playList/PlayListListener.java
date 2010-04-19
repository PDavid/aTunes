/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.controllers.playList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.menus.PlayListMenu;

/**
 * The listener interface for receiving play list events.
 */
public final class PlayListListener extends MouseAdapter implements ListSelectionListener {

    private PlayListTable table;
    private PlayListController controller;

    /**
     * Instantiates a new play list listener.
     * 
     * @param table
     *            the table
     * @param controller
     *            the controller
     */
    protected PlayListListener(PlayListTable table, PlayListController controller) {
        this.table = table;
        this.controller = controller;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().equals(table)) {
            if (e.getClickCount() == 2) {
                controller.playSelectedAudioObject();
            } else if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
                int[] currentlySelected = table.getSelectedRows();
                int selected = table.rowAtPoint(e.getPoint());
                boolean found = false;
                int i = 0;
                while (!found && i < currentlySelected.length) {
                    if (currentlySelected[i] == selected) {
                        found = true;
                    }
                    i++;
                }
                if (!found) {
                    table.getSelectionModel().setSelectionInterval(selected, selected);
                }

                table.getMenu().show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        PlayListMenu.updatePlayListMenuItems(table);
    }

}
