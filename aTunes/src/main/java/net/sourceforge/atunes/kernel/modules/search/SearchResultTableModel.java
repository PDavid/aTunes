/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.kernel.modules.search;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * The table model for search results.
 */
public abstract class SearchResultTableModel implements TableModel {

    /** Results to show. */
    private List<SearchResult> results;
    /** Listeners of this table model. */
    private List<TableModelListener> listeners;

    public SearchResultTableModel(List<SearchResult> results) {
        this.results = results;
        this.listeners = new ArrayList<TableModelListener>();
    }

    @Override
    public final void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public final Class<?> getColumnClass(int columnIndex) {
        // TODO: By now all columns in search results are string. Maybe in future we need to change this
        return String.class;
    }

    /**
     * Return column count. This method must be abstract as any implementation
     * of this class must add this information
     * 
     * @return the column count
     */
    @Override
    public abstract int getColumnCount();

    /**
     * Any implementation must give column names.
     * 
     * @param columnIndex
     *            the column index
     * 
     * @return the column name
     */
    @Override
    public abstract String getColumnName(int columnIndex);

    @Override
    public final int getRowCount() {
        return results.size();
    }

    /**
     * Any implementation must give cells values.
     * 
     * @param rowIndex
     *            the row index
     * @param columnIndex
     *            the column index
     * 
     * @return the value at
     */
    @Override
    public abstract Object getValueAt(int rowIndex, int columnIndex);

    /**
     * Returns true if cell is editable.
     * 
     * @param rowIndex
     *            the row index
     * @param columnIndex
     *            the column index
     * 
     * @return true, if checks if is cell editable
     */
    @Override
    public final boolean isCellEditable(int rowIndex, int columnIndex) {
        // TODO: By now all columns are not editable. Maybe in future we need to change this
        return false;
    }

    /**
     * Removes a table model listener.
     * 
     * @param l
     *            the l
     */
    @Override
    public final void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }

    /**
     * Sets cell values.
     * 
     * @param value
     *            the value
     * @param rowIndex
     *            the row index
     * @param columnIndex
     *            the column index
     */
    @Override
    public final void setValueAt(Object value, int rowIndex, int columnIndex) {
        // As all columns are not editable, this method has no sense.
    }

}
