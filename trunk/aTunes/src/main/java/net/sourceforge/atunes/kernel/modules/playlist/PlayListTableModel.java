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

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.gui.views.controls.playList.Column;
import net.sourceforge.atunes.kernel.modules.columns.PlayListColumns;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class PlayListTableModel.
 * 
 * @author fleax
 */
public class PlayListTableModel implements TableModel {

    /** The listeners. */
    private List<TableModelListener> listeners;

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
        listeners = new ArrayList<TableModelListener>();
    }

    /**
     * Adds a listener.
     * 
     * @param l
     *            the l
     */
    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
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
        return PlayListColumns.getColumn(PlayListColumns.getColumnId(colIndex)).getColumnClass();
    }

    /**
     * Return column count.
     * 
     * @return the column count
     */
    @Override
    public int getColumnCount() {
        return PlayListColumns.getVisibleColumnCount();
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
        return I18nUtils.getString(PlayListColumns.getColumn(PlayListColumns.getColumnId(colIndex)).getHeaderText());
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
            return PlayListColumns.getColumn(PlayListColumns.getColumnId(colIndex)).getValueFor(visiblePlayList.get(rowIndex));
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
        Class<? extends Column> c = PlayListColumns.getColumnId(columnIndex);

        // Call Column method to see if is editable
        return PlayListColumns.getColumn(c).isEditable();
    }

    /**
     * Refresh table.
     */
    public void refresh() {
        TableModelEvent event;
        event = new TableModelEvent(this, -1, -1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE);

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).tableChanged(event);
        }
    }

    /**
     * Removes a listener.
     * 
     * @param l
     *            the l
     */
    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    /**
     * Sets value for a cell. Does nothing as cells are not editable
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
        Class<? extends Column> c = PlayListColumns.getColumnId(columnIndex);

        // Call column set value
        PlayListColumns.getColumn(c).setValueFor(file, aValue);
    }

	/**
	 * @param visiblePlayList the visiblePlayList to set
	 */
	public void setVisiblePlayList(PlayList visiblePlayList) {
		this.visiblePlayList = visiblePlayList;
	}
}
