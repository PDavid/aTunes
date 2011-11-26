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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.gui.views.controls.PopUpButton;

final class ContextTableRowPanelMouseAdapter extends MouseAdapter {
	private final JPanel panel;
	private final ContextTable table;
	private final PopUpButton button;

	ContextTableRowPanelMouseAdapter(JPanel panel,
			ContextTable table, PopUpButton button) {
		this.panel = panel;
		this.table = table;
		this.button = button;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Rectangle bounds = panel.getBounds();
		bounds.setLocation(0, 0);
		if (!bounds.contains(e.getPoint())) {
			button.hideMenu();
			table.tableChanged(new TableModelEvent(table.getModel(), -1, -1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
		}
	}
}