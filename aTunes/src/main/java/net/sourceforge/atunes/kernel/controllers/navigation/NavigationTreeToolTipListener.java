/*
 * aTunes 2.0.0-SNAPSHOT
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
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.Logger;

/**
 * The listener interface for receiving navigationTreeToolTip events.
 */
public class NavigationTreeToolTipListener extends MouseAdapter implements MouseMotionListener, MouseWheelListener {

    static final Logger logger = new Logger();

    NavigationController controller;

    /**
     * Instantiates a new navigation tree tool tip listener.
     * 
     * @param controller
     *            the controller
     * @param panel
     *            the panel
     */
    public NavigationTreeToolTipListener(NavigationController controller) {
        this.controller = controller;
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        if (!ApplicationState.getInstance().isShowExtendedTooltip()) {
            return;
        }

        controller.setCurrentExtendedToolTipContent(null);
        controller.getExtendedToolTip().setVisible(false);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!ApplicationState.getInstance().isShowExtendedTooltip()) {
            return;
        }

        TreePath selectedPath = NavigationHandler.getInstance().getCurrentView().getTree().getPathForLocation(e.getX(), e.getY());
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
                    controller.getExtendedToolTip().setLocation((int) NavigationHandler.getInstance().getCurrentView().getTree().getLocationOnScreen().getX() + e.getX(),
                            (int) NavigationHandler.getInstance().getCurrentView().getTree().getLocationOnScreen().getY() + e.getY() + 20);

                    controller.getExtendedToolTip().setToolTipContent(content);
                    controller.setCurrentExtendedToolTipContent(content);
                } else {
                    controller.setCurrentExtendedToolTipContent(null);
                }

                controller.getToolTipTimer().setInitialDelay(ApplicationState.getInstance().getExtendedTooltipDelay() * 1000);
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
        if (!ApplicationState.getInstance().isShowExtendedTooltip()) {
            return;
        }

        controller.setCurrentExtendedToolTipContent(null);
        controller.getExtendedToolTip().setVisible(false);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        // When user does click (to popup menu for example) tool tip must be hidden
        if (!ApplicationState.getInstance().isShowExtendedTooltip()) {
            return;
        }

        controller.setCurrentExtendedToolTipContent(null);
        controller.getExtendedToolTip().setVisible(false);
    }

}
