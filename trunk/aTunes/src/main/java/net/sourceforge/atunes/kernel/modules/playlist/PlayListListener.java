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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.menus.PlayListMenuFiller;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListTable;

/**
 * The listener interface for receiving play list events.
 */
public final class PlayListListener extends MouseAdapter implements
		ListSelectionListener {

	private final IPlayListTable table;
	private final PlayListController controller;
	private final PlayListMenuFiller playListMenuFiller;
	private final IOSManager osManager;

	/**
	 * Instantiates a new play list listener.
	 * 
	 * @param table
	 * @param controller
	 * @param playListMenuFiller
	 * @param osManager
	 */
	protected PlayListListener(final IPlayListTable table,
			final PlayListController controller,
			final PlayListMenuFiller playListMenuFiller,
			final IOSManager osManager) {
		this.table = table;
		this.controller = controller;
		this.playListMenuFiller = playListMenuFiller;
		this.osManager = osManager;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (e.getSource().equals(this.table)) {
			if (e.getClickCount() == 2
					&& !GuiUtils.isSecondaryMouseButton(this.osManager, e)) {
				this.controller.playSelectedAudioObject();
			} else if (GuiUtils.isSecondaryMouseButton(this.osManager, e)) {
				int[] currentlySelected = this.table.getSelectedRows();
				int selected = this.table.rowAtPoint(e.getPoint());
				boolean found = false;
				int i = 0;
				while (!found && i < currentlySelected.length) {
					if (currentlySelected[i] == selected) {
						found = true;
					}
					i++;
				}
				if (!found) {
					this.table.getSelectionModel().setSelectionInterval(
							selected, selected);
				}

				this.table.getMenu().show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	@Override
	public void valueChanged(final ListSelectionEvent e) {
		this.playListMenuFiller.updatePlayListMenuItems();
	}

}
