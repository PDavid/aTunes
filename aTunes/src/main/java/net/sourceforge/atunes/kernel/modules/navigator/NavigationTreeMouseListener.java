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

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.GuiUtils;

/**
 * The listener interface for receiving navigationTreeMouse events.
 */
public final class NavigationTreeMouseListener extends MouseAdapter {

    private NavigationController controller;
    
    private IState state;
    
    private INavigationHandler navigationHandler;

    /**
     * Instantiates a new navigation tree mouse listener.
     * @param controller
     * @param state
     * @param navigationHandler
     */
    public NavigationTreeMouseListener(NavigationController controller, IState state, INavigationHandler navigationHandler) {
        this.controller = controller;
        this.state = state;
        this.navigationHandler = navigationHandler;
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

    @Override
    public void mouseClicked(MouseEvent e) {
        INavigationView currentView = navigationHandler.getCurrentView();
        controller.setPopupMenuCaller(currentView.getTree());

        if (GuiUtils.isSecondaryMouseButton(e)) {
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
            if (selRow != -1 && e.getClickCount() == 2) {
            	DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            	List<? extends IAudioObject> songs = controller.getAudioObjectsForTreeNode(currentView.getClass(), node);
            	Context.getBean(IPlayListHandler.class).addToPlayList(songs);
            }
        }
        
        // When clicking in tree, table selection must be cleared
        controller.getNavigationTablePanel().getNavigationTable().getSelectionModel().clearSelection();
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        if (!state.isShowExtendedTooltip()) {
            return;
        }

        controller.setCurrentExtendedToolTipContent(null);
        controller.getExtendedToolTip().setVisible(false);
        controller.getToolTipTimer().stop();
    }

}
