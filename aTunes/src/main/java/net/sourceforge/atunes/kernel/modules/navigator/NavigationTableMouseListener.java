/*
 * aTunes 3.0.0
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

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.NavigationTableModel;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.ITable;

/**
 * The listener interface for receiving navigationTableMouse events.
 */
public final class NavigationTableMouseListener extends MouseAdapter {

	private final NavigationController controller;
	private final ITable navigationTable;
	private final INavigationHandler navigationHandler;
	private final IPlayListHandler playListHandler;
	private final IOSManager osManager;

	/**
	 * Instantiates a new navigation table mouse listener.
	 * 
	 * @param controller
	 * @param navigationTable
	 * @param navigationHandler
	 * @param playListHandler
	 * @param osManager
	 */
	public NavigationTableMouseListener(final NavigationController controller,
			final ITable navigationTable,
			final INavigationHandler navigationHandler,
			final IPlayListHandler playListHandler, final IOSManager osManager) {
		this.controller = controller;
		this.navigationTable = navigationTable;
		this.navigationHandler = navigationHandler;
		this.playListHandler = playListHandler;
		this.osManager = osManager;
	}

	@Override
	public void mouseClicked(final MouseEvent event) {
		INavigationView currentView = this.navigationHandler.getCurrentView();

		if (GuiUtils.isSecondaryMouseButton(this.osManager, event)) {
			this.controller.setPopupMenuCaller(this.navigationTable
					.getSwingComponent());
			int[] rowsSelected = this.navigationTable.getSelectedRows();
			int selected = this.navigationTable.rowAtPoint(event.getPoint());
			boolean found = false;
			int i = 0;
			while (!found && i < rowsSelected.length) {
				if (rowsSelected[i] == selected) {
					found = true;
				}
				i++;
			}
			if (!found) {
				this.navigationTable.getSelectionModel().setSelectionInterval(
						selected, selected);
			}

			// Enable or disable actions of popup
			currentView.updateTablePopupMenuWithTableSelection(
					this.navigationTable, event);

			// Show popup
			currentView.getTablePopupMenu().show(
					this.controller.getPopupMenuCaller(), event.getX(),
					event.getY());
		} else {
			if (event.getClickCount() == 2) {
				int[] selRow = this.navigationTable.getSelectedRows();
				List<IAudioObject> songs = ((NavigationTableModel) this.navigationTable
						.getModel()).getAudioObjectsAt(selRow);
				if (songs != null && songs.size() >= 1) {
					this.playListHandler.addToVisiblePlayList(songs);
				}
			}
		}
	}
}
