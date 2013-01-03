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

package net.sourceforge.atunes.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Common code for table models
 * @author alex
 *
 */
public abstract class AbstractCommonTableModel implements TableModel {

	private final List<TableModelListener> listeners;

	protected AbstractCommonTableModel() {
		listeners = new ArrayList<TableModelListener>();
	}

	/**
	 * Adds a listener.
	 * 
	 * @param l
	 *            the l
	 */
	@Override
	public final void addTableModelListener(final TableModelListener l) {
		listeners.add(l);
	}

	/**
	 * Removes a listener.
	 * 
	 * @param l
	 *            the l
	 */
	@Override
	public final void removeTableModelListener(final TableModelListener l) {
		listeners.remove(l);
	}

	/**
	 * Refresh table.
	 * @param eventType
	 */
	public final void refresh(final int eventType) {
		TableModelEvent event;
		event = new TableModelEvent(this, -1, -1, TableModelEvent.ALL_COLUMNS, eventType);

		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).tableChanged(event);
		}
	}
}
