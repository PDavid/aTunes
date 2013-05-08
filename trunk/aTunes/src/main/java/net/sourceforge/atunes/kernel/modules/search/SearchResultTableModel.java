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

package net.sourceforge.atunes.kernel.modules.search;

import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.gui.AbstractColumnSetTableModel;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumn;

/**
 * The table model for search results.
 */
public class SearchResultTableModel extends AbstractColumnSetTableModel {

	private List<IAudioObject> results;

	/**
	 * Return row count.
	 * 
	 * @return the row count
	 */
	@Override
	public int getRowCount() {
		return results != null ? results.size() : 0;
	}

	/**
	 * Returns value of a row and column.
	 * 
	 * @param rowIndex
	 *            the row index
	 * @param colIndex
	 *            the col index
	 * 
	 * @return the value at
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int colIndex) {
		if (results != null) {
			IColumn<?> c = getColumn(colIndex);
			return c != null ? c.getValueFor(results.get(rowIndex), rowIndex)
					: null;
		}
		return null;
	}

	/**
	 * Returns if a cell is editable.
	 * 
	 * @param rowIndex
	 *            the row index
	 * @param columnIndex
	 *            the column index
	 * 
	 * @return true, if checks if is cell editable
	 */
	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}

	/**
	 * Sets value for a cell
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
		// Nothing to do
	}

	/**
	 * @param results
	 *            the results to set
	 */
	public void setResults(final List<IAudioObject> results) {
		this.results = results;
	}

	@Override
	public void sortByColumn(IColumn<?> column) {
		Collections.sort(this.results, column.getComparator());
	}
}
