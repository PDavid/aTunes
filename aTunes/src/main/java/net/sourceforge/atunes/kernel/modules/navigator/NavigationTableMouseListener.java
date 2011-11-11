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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.utils.GuiUtils;

/**
 * The listener interface for receiving navigationTableMouse events.
 */
public final class NavigationTableMouseListener extends MouseAdapter {

    private NavigationController controller;
    private ITable navigationTable;
    private INavigationHandler navigationHandler;

    /**
     * Instantiates a new navigation table mouse listener.
     * 
     * @param controller
     * @param navigationTable
     * @param navigationHandler
     */
    public NavigationTableMouseListener(NavigationController controller, ITable navigationTable, INavigationHandler navigationHandler) {
        this.controller = controller;
        this.navigationTable = navigationTable;
        this.navigationHandler = navigationHandler;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        INavigationView currentView = navigationHandler.getCurrentView();

        if (GuiUtils.isSecondaryMouseButton(event)) {
            controller.setPopupMenuCaller(navigationTable.getSwingComponent());
            int[] rowsSelected = navigationTable.getSelectedRows();
            int selected = navigationTable.rowAtPoint(event.getPoint());
            boolean found = false;
            int i = 0;
            while (!found && i < rowsSelected.length) {
                if (rowsSelected[i] == selected) {
                    found = true;
                }
                i++;
            }
            if (!found) {
                navigationTable.getSelectionModel().setSelectionInterval(selected, selected);
            }

            // Enable or disable actions of popup
            currentView.updateTablePopupMenuWithTableSelection(navigationTable, event);

            // Show popup
            currentView.getTablePopupMenu().show(controller.getPopupMenuCaller(), event.getX(), event.getY());
        } else {
            if (event.getClickCount() == 2) {
                int[] selRow = navigationTable.getSelectedRows();
                List<IAudioObject> songs = ((NavigationTableModel) navigationTable.getModel()).getAudioObjectsAt(selRow);
                if (songs != null && songs.size() >= 1) {
                	Context.getBean(IPlayListHandler.class).addToPlayList(songs);
                }
            }
        }
    }
}
