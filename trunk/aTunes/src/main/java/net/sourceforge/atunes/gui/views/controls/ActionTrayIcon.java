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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;

/**
 * Tray Icon that fires actions when left mouse button clicked
 * 
 * @author fleax
 * 
 */
public class ActionTrayIcon extends TrayIcon {

    /**
     * @param image
     * @param tooltip
     * @param popup
     */
    public ActionTrayIcon(Image image, String tooltip, PopupMenu popup, Action action) {
        super(image, tooltip, popup);
        addListener(action);
    }

    /**
     * @param image
     * @param tooltip
     */
    public ActionTrayIcon(Image image, String tooltip, Action action) {
        super(image, tooltip);
        addListener(action);
    }

    /**
     * @param image
     */
    public ActionTrayIcon(Image image, Action action) {
        super(image);
        addListener(action);
    }

    /**
     * Binds mouse listener to action
     * 
     * @param action
     */
    private void addListener(Action action) {
        addMouseListener(new ActionMouseListener(action));
    }

    /**
     * Mouse adapter to fire actions
     * 
     * @author fleax
     * 
     */
    private static class ActionMouseListener extends MouseAdapter {

        /**
         * Action to perform
         */
        private Action action;

        /**
         * Creates a new instance
         * 
         * @param action
         */
        public ActionMouseListener(Action action) {
            this.action = action;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                action.actionPerformed(null);
            }
        }
    }
}
