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

import java.util.Comparator;

import net.sourceforge.atunes.model.ColumnSort;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * @author alex
 * 
 */
public abstract class AbstractColumnSetTableModel extends
		AbstractCommonTableModel {

	private IColumnSet columnSet;

	/**
	 * Returns column data class.
	 * 
	 * @param colIndex
	 *            the col index
	 * 
	 * @return the column class
	 */
	@Override
	public Class<?> getColumnClass(final int colIndex) {
		return getColumn(colIndex).getColumnClass();
	}

	/**
	 * Return column count.
	 * 
	 * @return the column count
	 */
	@Override
	public int getColumnCount() {
		return this.columnSet != null ? this.columnSet.getVisibleColumnCount()
				: 0;
	}

	/**
	 * Return column name.
	 * 
	 * @param colIndex
	 *            the col index
	 * 
	 * @return the column name
	 */
	@Override
	public String getColumnName(final int colIndex) {
		// Use a white space if empty to force to show header if no more columns
		// are visible
		String text = getColumn(colIndex).getHeaderText();
		text = I18nUtils.getString(StringUtils.isEmpty(text) ? " " : text);
		ColumnSort sort = getColumn(colIndex).getColumnSort();
		if (sort == ColumnSort.ASCENDING) {
			text = StringUtils.getString(text, " \u2193");
		} else if (sort == ColumnSort.DESCENDING) {
			text = StringUtils.getString(text, " \u2191");
		}
		return text;
	}

	/**
	 * Returns column in given index
	 * 
	 * @param colIndex
	 * @return
	 */
	protected IColumn<?> getColumn(final int colIndex) {
		return this.columnSet != null ? this.columnSet.getColumn(this.columnSet
				.getColumnId(colIndex)) : null;
	}

	/**
	 * Abstract method to sort by the given comparator
	 * 
	 * @param comparator
	 */
	public abstract void sort(Comparator<IAudioObject> comparator);

	/**
	 * @param columnSet
	 *            the columnSet to set
	 */
	public void setColumnSet(final IColumnSet columnSet) {
		this.columnSet = columnSet;
	}
}
