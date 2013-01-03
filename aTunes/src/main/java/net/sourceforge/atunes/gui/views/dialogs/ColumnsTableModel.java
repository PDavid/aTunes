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

package net.sourceforge.atunes.gui.views.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.utils.I18nUtils;

class ColumnsTableModel implements TableModel {

	private static final long serialVersionUID = 5251001708812824836L;

	private List<IColumn<?>> columns;
	private final List<TableModelListener> listeners = new ArrayList<TableModelListener>();

	private final JTable columnsList;

	/**
	 * Instantiates a new columns table model.
	 * 
	 * @param columnsList
	 */
	ColumnsTableModel(final JTable columnsList) {
		this.columnsList = columnsList;
	}

	@Override
	public void addTableModelListener(final TableModelListener l) {
		this.listeners.add(l);
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return columnIndex == 0 ? Boolean.class : String.class;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(final int column) {
		return "";
	}

	@Override
	public int getRowCount() {
		if (this.columns != null) {
			return this.columns.size();
		}
		return 0;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		if (columnIndex == 0) {
			return this.columns.get(rowIndex).isVisible();
		}
		return I18nUtils.getString(this.columns.get(rowIndex).getColumnName());
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return columnIndex == 0;
	}

	/**
	 * Move down.
	 * 
	 * @param columnPos
	 *            the column pos
	 */
	public void moveDown(final int columnPos) {
		// Can't move down last column
		if (columnPos == (this.columns.size() - 1)) {
			return;
		} else if (columnPos >= this.columns.size() || columnPos < 0) {
			throw new IllegalArgumentException("Wrong columnPos = " + columnPos);
		}

		// Get this column and next
		IColumn<?> columnSelected = this.columns.get(columnPos);
		IColumn<?> nextColumn = this.columns.get(columnPos + 1);

		// Swap order
		int aux = columnSelected.getOrder();
		columnSelected.setOrder(nextColumn.getOrder());
		nextColumn.setOrder(aux);

		// Swap position on columns array
		this.columns.remove(nextColumn);
		this.columns.add(columnPos, nextColumn);

		callListeners();

		this.columnsList.getColumnModel().getColumn(0).setMaxWidth(20);
		this.columnsList.getSelectionModel().setSelectionInterval(
				columnPos + 1, columnPos + 1);
	}

	/**
	 * Move up.
	 * 
	 * @param columnPos
	 *            the column pos
	 */
	public void moveUp(final int columnPos) {
		// Can't move up first column
		if (columnPos == 0) {
			return;
		} else if (columnPos >= this.columns.size() || columnPos < 0) {
			throw new IllegalArgumentException("Wrong columnPos = " + columnPos);
		}

		// Get this column and previous
		IColumn<?> columnSelected = this.columns.get(columnPos);
		IColumn<?> previousColumn = this.columns.get(columnPos - 1);

		// Swap order
		int aux = columnSelected.getOrder();
		columnSelected.setOrder(previousColumn.getOrder());
		previousColumn.setOrder(aux);

		// Swap position on columns array
		this.columns.remove(previousColumn);
		this.columns.add(columnPos, previousColumn);

		callListeners();

		this.columnsList.getColumnModel().getColumn(0).setMaxWidth(20);

		this.columnsList.getSelectionModel().setSelectionInterval(
				columnPos - 1, columnPos - 1);
	}

	@Override
	public void removeTableModelListener(final TableModelListener l) {
		this.listeners.remove(l);
	}

	/**
	 * @param columns
	 */
	public void setColumns(final List<IColumn<?>> columns) {
		this.columns = columns;
		Collections.sort(this.columns);
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex,
			final int columnIndex) {
		if (columnIndex == 0) {
			this.columns.get(rowIndex).setVisible((Boolean) aValue);
		}

		callListeners();
	}

	/**
	 * 
	 */
	private void callListeners() {
		TableModelEvent event;
		event = new TableModelEvent(this, TableModelEvent.ALL_COLUMNS,
				TableModelEvent.UPDATE);

		for (int i = 0; i < this.listeners.size(); i++) {
			this.listeners.get(i).tableChanged(event);
		}
		this.columnsList.getColumnModel().getColumn(0).setMaxWidth(20);
	}

	/**
	 * @return if some column is visible
	 */
	boolean someColumnIsSelected() {
		for (IColumn<?> column : this.columns) {
			if (column.isVisible()) {
				return true;
			}
		}
		return false;
	}
}