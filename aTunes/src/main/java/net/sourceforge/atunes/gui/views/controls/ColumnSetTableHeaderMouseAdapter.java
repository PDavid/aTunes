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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IOSManager;

final class ColumnSetTableHeaderMouseAdapter extends MouseAdapter {

	private final ColumnSetPopupMenu menu;
	private final IOSManager osManager;

	/**
	 * @param menu
	 * @param osManager
	 */
	ColumnSetTableHeaderMouseAdapter(final ColumnSetPopupMenu menu,
			final IOSManager osManager) {
		this.menu = menu;
		this.osManager = osManager;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		// Use right button to arrange columns
		if (GuiUtils.isSecondaryMouseButton(this.osManager, e)) {
			this.menu.show(e.getX(), e.getY());
		}
	}
}