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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.awt.event.MouseEvent;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;

class TabReorderer extends MouseInputAdapter {

	private PlayListTabController controller;
	
	private JTabbedPane tabPane;
	private int draggedTabIndex;

	public TabReorderer(PlayListTabController controller, JTabbedPane pane) {
		this.controller = controller;
		this.tabPane = pane;
		draggedTabIndex = -1;
	}

	public void enableReordering() {
		tabPane.addMouseListener(this);
		tabPane.addMouseMotionListener(this);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		draggedTabIndex = tabPane.getUI().tabForCoordinate(tabPane, e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (draggedTabIndex == -1) {
			return;
		}

		int targetTabIndex = tabPane.getUI().tabForCoordinate(tabPane, e.getX(), e.getY());
		if (targetTabIndex != -1 && targetTabIndex != draggedTabIndex) {

			controller.switchPlayListTabs(draggedTabIndex, targetTabIndex);
			PlayListHandler.getInstance().movePlaylistToPosition(draggedTabIndex, targetTabIndex);

			((PlayListTableModel) GuiHandler.getInstance().getPlayListTable().getModel()).setVisiblePlayList(PlayListHandler.getInstance().getCurrentPlayList(true));
			controller.forceSwitchTo(targetTabIndex);

			draggedTabIndex = -1;

			SwingUtilities.invokeLater(new SwitchPlayList(targetTabIndex));
		}
	}
	
    private static class SwitchPlayList implements Runnable {

        private int targetTabIndex;

        public SwitchPlayList(int targetTabIndex) {
            this.targetTabIndex = targetTabIndex;
        }

        @Override
        public void run() {
            PlayListHandler.getInstance().switchToPlaylist(targetTabIndex);
        }
    }
}

