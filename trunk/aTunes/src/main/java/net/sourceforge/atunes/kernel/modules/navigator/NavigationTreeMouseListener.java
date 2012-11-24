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

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.model.ITreeNode;

/**
 * The listener interface for receiving navigationTreeMouse events.
 */
public final class NavigationTreeMouseListener extends MouseAdapter {

	private final NavigationController controller;

	private final INavigationHandler navigationHandler;

	private final ITable navigationTable;

	private final IStateNavigation stateNavigation;

	private final IPlayListHandler playListHandler;

	private final IOSManager osManager;

	/**
	 * Instantiates a new navigation tree mouse listener.
	 * 
	 * @param controller
	 * @param navigationTable
	 * @param stateNavigation
	 * @param navigationHandler
	 * @param playListHandler
	 * @param osManager
	 */
	public NavigationTreeMouseListener(final NavigationController controller,
			final ITable navigationTable,
			final IStateNavigation stateNavigation,
			final INavigationHandler navigationHandler,
			final IPlayListHandler playListHandler, final IOSManager osManager) {
		this.controller = controller;
		this.navigationTable = navigationTable;
		this.stateNavigation = stateNavigation;
		this.navigationHandler = navigationHandler;
		this.playListHandler = playListHandler;
		this.osManager = osManager;
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
	private boolean isNewRowSelection(final JTree tree, final MouseEvent e) {
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
	public void mouseClicked(final MouseEvent e) {
		INavigationView currentView = this.navigationHandler.getCurrentView();
		this.controller.setPopupMenuCaller(currentView.getTree()
				.getSwingComponent());

		if (GuiUtils.isSecondaryMouseButton(this.osManager, e)) {
			// BUG 1626896
			int row = currentView.getTree().getSwingComponent()
					.getRowForLocation(e.getX(), e.getY());
			if (isNewRowSelection(currentView.getTree().getSwingComponent(), e)
					&& row != -1) {
				currentView.getTree().getSwingComponent().setSelectionRow(row);
			}
			// BUG 1626896

			currentView.updateTreePopupMenuWithTreeSelection(e);

			currentView.getTreePopupMenu().show(
					currentView.getTree().getSwingComponent(), e.getX(),
					e.getY());
		} else {
			int selRow = currentView.getTree().getSwingComponent()
					.getRowForLocation(e.getX(), e.getY());
			if (selRow != -1 && e.getClickCount() == 2) {
				ITreeNode node = currentView.getTree().getSelectedNode(e);
				List<? extends IAudioObject> songs = this.controller
						.getAudioObjectsForTreeNode(currentView.getClass(),
								node);
				this.playListHandler.addToVisiblePlayList(songs);
			}
		}

		// When clicking in tree, table selection must be cleared
		this.navigationTable.getSelectionModel().clearSelection();
	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
		if (!this.stateNavigation.isShowExtendedTooltip()) {
			return;
		}

		this.controller.setCurrentExtendedToolTipContent(null);
		this.controller.getExtendedToolTip().setVisible(false);
		this.controller.getToolTipTimer().stop();
	}

}
