/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.gui.GuiUtils;

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
	 * @param action
	 */
	public ActionTrayIcon(final Image image, final String tooltip, final PopupMenu popup, final Action action) {
		super(image, tooltip, popup);
		addListener(action);
	}

	/**
	 * @param image
	 * @param tooltip
	 * @param action
	 */
	public ActionTrayIcon(final Image image, final String tooltip, final Action action) {
		super(image, tooltip);
		addListener(action);
	}

	/**
	 * @param image
	 * @param action
	 */
	public ActionTrayIcon(final Image image, final Action action) {
		super(image);
		addListener(action);
	}

	/**
	 * Binds mouse listener to action
	 * 
	 * @param action
	 */
	private void addListener(final Action action) {
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
		private final Action action;

		/**
		 * Creates a new instance
		 * 
		 * @param action
		 */
		public ActionMouseListener(final Action action) {
			this.action = action;
		}


		@Override
		public void mousePressed(final MouseEvent e) {
			if (GuiUtils.isPrimaryMouseButton(e)) {
				action.actionPerformed(null);
			}
		}
	}
}
