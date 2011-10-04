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
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IState;

/**
 * The listener interface for receiving navigationTreeToolTip events.
 */
public final class NavigationTreeToolTipListener extends MouseAdapter implements MouseMotionListener, MouseWheelListener {

    private NavigationController controller;
    
    private IState state;
    
    private INavigationHandler navigationHandler;

    /**
     * Instantiates a new navigation tree tool tip listener.
     * 
     * @param controller
     * @param state
     * @param navigationHandler
     */
    public NavigationTreeToolTipListener(NavigationController controller, IState state, INavigationHandler navigationHandler) {
        this.controller = controller;
        this.state = state;
        this.navigationHandler = navigationHandler;
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        if (!state.isShowExtendedTooltip()) {
            return;
        }

        controller.setCurrentExtendedToolTipContent(null);
        controller.getExtendedToolTip().setVisible(false);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!state.isShowExtendedTooltip()) {
            return;
        }

        TreePath selectedPath = navigationHandler.getCurrentView().getTree().getPathForLocation(e.getX(), e.getY());
        if (selectedPath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
            final Object content = node.getUserObject();

            if (content.equals(controller.getCurrentExtendedToolTipContent())) {
                return;
            }

            // Show extended tooltip
            if (ExtendedToolTip.canObjectBeShownInExtendedToolTip(content)) {
                if (!controller.getExtendedToolTip().isVisible() || controller.getCurrentExtendedToolTipContent() == null
                        || controller.getCurrentExtendedToolTipContent() != content) {
                    if (controller.getExtendedToolTip().isVisible()) {
                        controller.getExtendedToolTip().setVisible(false);
                    }
                    controller.getExtendedToolTip().setLocation((int) navigationHandler.getCurrentView().getTree().getLocationOnScreen().getX() + e.getX(),
                            (int) navigationHandler.getCurrentView().getTree().getLocationOnScreen().getY() + e.getY() + 20);

                    controller.getExtendedToolTip().setToolTipContent(content);
                    controller.setCurrentExtendedToolTipContent(content);
                } else {
                    controller.setCurrentExtendedToolTipContent(null);
                }

                controller.getToolTipTimer().setInitialDelay(state.getExtendedTooltipDelay() * 1000);
                controller.getToolTipTimer().setRepeats(false);
                controller.getToolTipTimer().start();
            } else {
                controller.setCurrentExtendedToolTipContent(null);
                controller.getExtendedToolTip().setVisible(false);
                controller.getToolTipTimer().stop();
            }
        } else {
            controller.setCurrentExtendedToolTipContent(null);
            controller.getExtendedToolTip().setVisible(false);
            controller.getToolTipTimer().stop();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!state.isShowExtendedTooltip()) {
            return;
        }

        controller.setCurrentExtendedToolTipContent(null);
        controller.getExtendedToolTip().setVisible(false);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        // When user does click (to popup menu for example) tool tip must be hidden
        if (!state.isShowExtendedTooltip()) {
            return;
        }

        controller.setCurrentExtendedToolTipContent(null);
        controller.getExtendedToolTip().setVisible(false);
    }

}
