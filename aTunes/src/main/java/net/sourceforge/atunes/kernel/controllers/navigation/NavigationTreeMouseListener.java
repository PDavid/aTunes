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

package net.sourceforge.atunes.kernel.controllers.navigation;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationView;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;

/**
 * The listener interface for receiving navigationTreeMouse events.
 */
public class NavigationTreeMouseListener extends MouseAdapter {

    /** The controller. */
    private NavigationController controller;

    /**
     * Instantiates a new navigation tree mouse listener.
     * 
     * @param controller
     *            the controller
     * @param panel
     *            the panel
     */
    public NavigationTreeMouseListener(NavigationController controller) {
        this.controller = controller;
    }

    /**
     * Checks if is new row selection.
     * 
     * @param tree
     *            the tree
     * @param e
     *            the e
     * 
     * @return true, if is new row selection
     */
    private boolean isNewRowSelection(JTree tree, MouseEvent e) {
        int[] rowsSelected = tree.getSelectionRows();
        if (rowsSelected == null) {
            return false;
        }
        int selected = tree.getRowForLocation(e.getX(), e.getY());
        boolean found = false;
        int i = 0;
        while (!found && i < rowsSelected.length) {
            if (rowsSelected[i] == selected) {
                found = true;
            }
            i++;
        }
        return !found;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        NavigationView currentView = NavigationHandler.getInstance().getCurrentView();
        controller.setPopupMenuCaller(currentView.getTree());

        if (e.getButton() == MouseEvent.BUTTON3) {
            //	BUG 1626896
            int row = currentView.getTree().getRowForLocation(e.getX(), e.getY());
            if (isNewRowSelection(currentView.getTree(), e) && row != -1) {
                currentView.getTree().setSelectionRow(row);
            }
            //	BUG 1626896

            currentView.updateTreePopupMenuWithTreeSelection(e);

            currentView.getTreePopupMenu().show(currentView.getTree(), e.getX(), e.getY());
        } else {
            int selRow = currentView.getTree().getRowForLocation(e.getX(), e.getY());
            TreePath selPath = currentView.getTree().getPathForLocation(e.getX(), e.getY());
            if (selRow != -1) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                    List<AudioObject> songs = controller.getAudioObjectsForTreeNode(currentView.getClass(), node);
                    PlayListHandler.getInstance().addToPlayList(songs);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent arg0) {
        if (!ApplicationState.getInstance().isShowExtendedTooltip()) {
            return;
        }

        controller.setCurrentExtendedToolTipContent(null);
        controller.getExtendedToolTip().setVisible(false);
        controller.getToolTipTimer().stop();
    }

}
