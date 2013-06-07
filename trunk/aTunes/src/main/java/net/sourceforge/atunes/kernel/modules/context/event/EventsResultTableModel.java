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

package net.sourceforge.atunes.kernel.modules.context.event;

import java.util.List;

import javax.swing.table.DefaultTableModel;

import net.sourceforge.atunes.model.IEvent;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Table model used in events panel
 * 
 * @author alex
 * 
 */
public class EventsResultTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5847477016315898970L;

	/**
	 * List of events
	 */
	private List<IEvent> entries;

	/**
	 * @param entries
	 */
	public void setEntries(List<IEvent> entries) {
		this.entries = entries;
		fireTableDataChanged();
	}

	/**
	 * Removes all results from the model. Fires a refresh
	 */
	public void clearEntries() {
		entries.clear();
		fireTableDataChanged();
	}

	/**
	 * Returns an entry at given location
	 * 
	 * @param row
	 * @return
	 */
	public IEvent getEntry(int row) {
		return entries.get(row);
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		if (arg0 == 0) {
			return IEvent.class;
		}
		return null;
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public String getColumnName(int arg0) {
		return I18nUtils.getString("EVENTS");
	}

	@Override
	public int getRowCount() {
		return entries != null ? entries.size() : 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return entries.get(rowIndex);
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}
}
