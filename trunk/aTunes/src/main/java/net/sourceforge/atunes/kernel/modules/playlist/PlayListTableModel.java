/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.gui.model.CommonTableModel;
import net.sourceforge.atunes.kernel.modules.columns.Column;
import net.sourceforge.atunes.kernel.modules.columns.PlayListColumnSet;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The playlist table model.
 * 
 * @author fleax
 */
public class PlayListTableModel extends CommonTableModel {

    /**
     * Reference to the visible play list
     */
    private PlayList visiblePlayList = null;

    /**
     * Constructor.
     * 
     * @param table
     *            the table
     */
    public PlayListTableModel() {
        super();
    }

    /**
     * Returns column data class.
     * 
     * @param colIndex
     *            the col index
     * 
     * @return the column class
     */
    @Override
    public Class<?> getColumnClass(int colIndex) {
        return PlayListColumnSet.getInstance().getColumn(PlayListColumnSet.getInstance().getColumnId(colIndex)).getColumnClass();
    }

    /**
     * Return column count.
     * 
     * @return the column count
     */
    @Override
    public int getColumnCount() {
        return PlayListColumnSet.getInstance().getVisibleColumnCount();
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
    public String getColumnName(int colIndex) {
        return I18nUtils.getString(PlayListColumnSet.getInstance().getColumn(PlayListColumnSet.getInstance().getColumnId(colIndex)).getHeaderText());
    }

    /**
     * Return row count.
     * 
     * @return the row count
     */
    @Override
    public int getRowCount() {
        if (visiblePlayList != null) {
            return visiblePlayList.size();
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
    public Object getValueAt(int rowIndex, int colIndex) {
        // Call Column method to get value from AudioFile
        if (visiblePlayList != null) {
            return PlayListColumnSet.getInstance().getColumn(PlayListColumnSet.getInstance().getColumnId(colIndex)).getValueFor(visiblePlayList.get(rowIndex));
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
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Column ID
        Class<? extends Column> c = PlayListColumnSet.getInstance().getColumnId(columnIndex);

        // Call Column method to see if is editable
        return PlayListColumnSet.getInstance().getColumn(c).isEditable();
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
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // AudioFile
        AudioObject file = visiblePlayList.get(rowIndex);

        // Column ID
        Class<? extends Column> c = PlayListColumnSet.getInstance().getColumnId(columnIndex);

        // Call column set value
        PlayListColumnSet.getInstance().getColumn(c).setValueFor(file, aValue);
    }

    /**
     * @param visiblePlayList
     *            the visiblePlayList to set
     */
    public void setVisiblePlayList(PlayList visiblePlayList) {
        this.visiblePlayList = visiblePlayList;
    }
}
