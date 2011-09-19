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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.gui.views.panels.NavigationTablePanel;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.GuiUtils;

/**
 * The listener interface for receiving navigationTableMouse events.
 */
public final class NavigationTableMouseListener extends MouseAdapter {

    private NavigationController controller;
    private NavigationTablePanel panel;
    private INavigationHandler navigationHandler;

    /**
     * Instantiates a new navigation table mouse listener.
     * 
     * @param controller
     *            the controller
     * @param panel
     *            the panel
     */
    public NavigationTableMouseListener(NavigationController controller, NavigationTablePanel panel, INavigationHandler navigationHandler) {
        this.controller = controller;
        this.panel = panel;
        this.navigationHandler = navigationHandler;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        INavigationView currentView = navigationHandler.getCurrentView();

        if (GuiUtils.isSecondaryMouseButton(event)) {
            controller.setPopupMenuCaller(panel.getNavigationTable());
            int[] rowsSelected = panel.getNavigationTable().getSelectedRows();
            int selected = panel.getNavigationTable().rowAtPoint(event.getPoint());
            boolean found = false;
            int i = 0;
            while (!found && i < rowsSelected.length) {
                if (rowsSelected[i] == selected) {
                    found = true;
                }
                i++;
            }
            if (!found) {
                panel.getNavigationTable().getSelectionModel().setSelectionInterval(selected, selected);
            }

            // Enable or disable actions of popup
            currentView.updateTablePopupMenuWithTableSelection(panel.getNavigationTable(), event);

            // Show popup
            currentView.getTablePopupMenu().show(controller.getPopupMenuCaller(), event.getX(), event.getY());
        } else {
            if (event.getClickCount() == 2) {
                int[] selRow = panel.getNavigationTable().getSelectedRows();
                List<IAudioObject> songs = ((NavigationTableModel) panel.getNavigationTable().getModel()).getAudioObjectsAt(selRow);
                if (songs != null && songs.size() >= 1) {
                	Context.getBean(IPlayListHandler.class).addToPlayList(songs);
                }
            }
//          } else if (panel.getNavigationTable().getSelectedRowCount() > 0) {
//              // Nothing to do
//          }
        }
    }
}
