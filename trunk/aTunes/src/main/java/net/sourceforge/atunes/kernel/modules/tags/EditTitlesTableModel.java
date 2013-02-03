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

package net.sourceforge.atunes.kernel.modules.tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Table model for title edition when importing CD
 * 
 * @author alex
 * 
 */
public final class EditTitlesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -4440078678648669115L;

	/** The files. */
	private final List<ILocalAudioObject> files;

	/** The new values. */
	private final Map<ILocalAudioObject, String> newValues;

	/** The listeners. */
	private final List<TableModelListener> listeners;

	private final IFileManager fileManager;

	/**
	 * Instantiates a new edits the titles table model.
	 * 
	 * @param files
	 * @param fileManager
	 */
	public EditTitlesTableModel(final List<ILocalAudioObject> files,
			final IFileManager fileManager) {
		this.files = files;
		this.newValues = new HashMap<ILocalAudioObject, String>();
		this.listeners = new ArrayList<TableModelListener>();
		this.fileManager = fileManager;
	}

	/**
	 * Adds the listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addListener(final TableModelListener listener) {
		this.listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(final int column) {
		if (column == 0) {
			return I18nUtils.getString("FILE");
		}
		return I18nUtils.getString("TITLE");
	}

	/**
	 * Gets the new values.
	 * 
	 * @return the new values
	 */
	public Map<ILocalAudioObject, String> getNewValues() {
		return this.newValues;
	}

	@Override
	public int getRowCount() {
		return this.files.size();
	}

	@Override
	public String getValueAt(final int rowIndex, final int columnIndex) {
		ILocalAudioObject file = this.files.get(rowIndex);
		if (columnIndex == 0) {
			return this.fileManager.getFileName(file);
		}
		if (this.newValues.containsKey(file)) {
			return this.newValues.get(file);
		}
		return file.getTitle();
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return columnIndex == 1;
	}

	/**
	 * Sets the titles.
	 * 
	 * @param titles
	 *            the new titles
	 */
	public void setTitles(final List<String> titles) {
		for (int i = 0; i < this.files.size(); i++) {
			String title = titles.size() > i ? titles.get(i) : "";
			this.newValues.put(this.files.get(i), title);
		}

		TableModelEvent event;
		event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS,
				TableModelEvent.UPDATE);

		for (int i = 0; i < this.listeners.size(); i++) {
			this.listeners.get(i).tableChanged(event);
		}
	}

	/**
	 * Sets the value at.
	 * 
	 * @param aValue
	 *            the a value
	 * @param rowIndex
	 *            the row index
	 * @param columnIndex
	 *            the column index
	 */
	@Override
	public void setValueAt(final Object aValue, final int rowIndex,
			final int columnIndex) {
		this.newValues.put(this.files.get(rowIndex), (String) aValue);
		fireTableCellUpdated(rowIndex, columnIndex);
	}
}
