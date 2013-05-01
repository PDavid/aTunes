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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.menus.PlayListMenuFiller;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListTable;

/**
 * The listener interface for receiving play list events.
 */
public final class PlayListListener extends MouseAdapter implements
		ListSelectionListener {

	private IPlayListTable playListTable;

	private PlayListController playListController;

	private IOSManager osManager;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param playListTable
	 */
	public void setPlayListTable(final IPlayListTable playListTable) {
		this.playListTable = playListTable;
	}

	/**
	 * @param playListController
	 */
	public void setPlayListController(
			final PlayListController playListController) {
		this.playListController = playListController;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (e.getSource().equals(this.playListTable)) {
			if (e.getClickCount() == 2
					&& !GuiUtils.isSecondaryMouseButton(this.osManager, e)) {
				this.playListController.playSelectedAudioObject();
			} else if (GuiUtils.isSecondaryMouseButton(this.osManager, e)) {
				int[] currentlySelected = this.playListTable.getSelectedRows();
				int selected = this.playListTable.rowAtPoint(e.getPoint());
				boolean found = false;
				int i = 0;
				while (!found && i < currentlySelected.length) {
					if (currentlySelected[i] == selected) {
						found = true;
					}
					i++;
				}
				if (!found) {
					this.playListTable.getSelectionModel()
							.setSelectionInterval(selected, selected);
				}

				this.playListTable.getMenu().show(e.getComponent(), e.getX(),
						e.getY());
			}
		}
	}

	@Override
	public void valueChanged(final ListSelectionEvent e) {
		this.beanFactory.getBean(PlayListMenuFiller.class)
				.updatePlayListMenuItems();
	}
}
