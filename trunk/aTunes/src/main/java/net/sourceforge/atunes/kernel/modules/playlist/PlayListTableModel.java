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

import net.sourceforge.atunes.gui.AbstractColumnSetTableModel;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;

/**
 * The playlist table model.
 * 
 * @author fleax
 */
public class PlayListTableModel extends AbstractColumnSetTableModel {

	/**
	 * Reference to the visible play list
	 */
	private IPlayList visiblePlayList = null;

	private IPlayListHandler playListHandler;

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Return row count.
	 * 
	 * @return the row count
	 */
	@Override
	public int getRowCount() {
		if (this.visiblePlayList != null) {
			return this.visiblePlayList.size();
		}
		return 0;
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
		// Call Column method to get value from AudioFile
		if (this.visiblePlayList != null) {
			IAudioObject ao = this.visiblePlayList.get(rowIndex);
			if (ao != null) {
				IColumn<?> c = getColumn(colIndex);
				return c != null ? c.getValueFor(ao, rowIndex) : null;
			}
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
		IColumn<?> c = getColumn(columnIndex);
		return c != null ? c.isEditable() : false;
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
		// AudioFile
		IAudioObject audioObject = this.visiblePlayList.get(rowIndex);

		// Call column set value
		IColumn<?> c = getColumn(columnIndex);
		if (c != null) {
			c.setValueFor(audioObject, aValue);
		}

		// After changing audio object refresh playlist, as the same object can
		// be duplicated
		this.playListHandler.refreshPlayList();
	}

	/**
	 * @param visiblePlayList
	 *            the visiblePlayList to set
	 */
	public void setVisiblePlayList(final IPlayList visiblePlayList) {
		this.visiblePlayList = visiblePlayList;
	}

	@Override
	public void sortByColumn(final IColumn<?> column) {
		if (this.visiblePlayList == null) {
			return;
		}

		this.visiblePlayList.sortByColumn(column);
	}
}
