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

import javax.swing.JTable;

import net.sourceforge.atunes.gui.AbstractColumnSetTableModel;
import net.sourceforge.atunes.gui.AbstractCommonColumnModel;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.ColumnSort;
import net.sourceforge.atunes.model.IColumn;

/**
 * Calls sort on column model when user clicks a column header
 * 
 * @author alex
 * 
 */
public final class ColumnSetRowSorter extends MouseAdapter {

	private final JTable table;
	private final AbstractColumnSetTableModel model;
	private final AbstractCommonColumnModel columnModel;

	/**
	 * @param table
	 * @param model
	 * @param columnModel
	 */
	public ColumnSetRowSorter(final JTable table,
			final AbstractColumnSetTableModel model,
			final AbstractCommonColumnModel columnModel) {
		this.table = table;
		this.model = model;
		this.columnModel = columnModel;
		this.table.getTableHeader().addMouseListener(this);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		IColumn<?> columnClicked = sortDetected(e);
		if (columnClicked != null) {
			IColumn<?> lastColumnSorted = this.columnModel.getColumnSet()
					.getSortedColumn();
			// If column changed then sort ascending the new column
			// otherwise change sort
			if (sortInDifferentColumn(columnClicked, lastColumnSorted)) {
				lastColumnSorted.setColumnSort(null);
				columnClicked.setColumnSort(ColumnSort.ASCENDING);
			} else {
				changeSort(columnClicked);
			}

			// Then sort
			this.model.sort(columnClicked);
		}
	}

	/**
	 * Detects a sort in a sortable column
	 * 
	 * @param e
	 * @return column to sort
	 */
	private IColumn<?> sortDetected(MouseEvent e) {
		if (GuiUtils.isPrimaryMouseButton(e)) {
			int columnClickedIndex = this.table.getTableHeader()
					.getColumnModel().getColumnIndexAtX(e.getX());
			if (columnClickedIndex != -1) {
				// Get column clicked
				IColumn<?> columnClicked = this.columnModel
						.getColumnObject(columnClickedIndex);
				// Only if it's sortable...
				if (columnClicked != null & columnClicked.isSortable()) {
					return columnClicked;
				}
			}
		}
		return null;
	}

	private boolean sortInDifferentColumn(IColumn<?> columnClicked,
			IColumn<?> lastColumnSorted) {
		return lastColumnSorted != null
				&& !lastColumnSorted.equals(columnClicked);
	}

	private void changeSort(final IColumn<?> column) {
		if (column.getColumnSort() == null
				|| column.getColumnSort() == ColumnSort.ASCENDING) {
			column.setColumnSort(ColumnSort.DESCENDING);
		} else {
			column.setColumnSort(ColumnSort.ASCENDING);
		}
	}
}
