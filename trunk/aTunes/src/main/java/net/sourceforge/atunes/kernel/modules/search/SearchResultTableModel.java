/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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
import java.util.Comparator;
import java.util.List;

import net.sourceforge.atunes.gui.model.AbstractColumnSetTableModel;
import net.sourceforge.atunes.kernel.modules.columns.SearchResultsColumnSet;
import net.sourceforge.atunes.model.AudioObject;

/**
 * The table model for search results.
 */
public class SearchResultTableModel extends AbstractColumnSetTableModel {

    private List<AudioObject> results;

    /**
     * Constructor.
     * 
     * @param table
     *            the table
     */
    public SearchResultTableModel() {
        super(SearchResultsColumnSet.getInstance());
    }

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
    public Object getValueAt(int rowIndex, int colIndex) {
        return results != null ? getColumn(colIndex).getValueFor(results.get(rowIndex)) : null;
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
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        // Nothing to do
    }

    /**
     * @param results
     *            the results to set
     */
    public void setResults(List<AudioObject> results) {
        this.results = results;
    }

    @Override
    public void sort(Comparator<AudioObject> comparator) {
        Collections.sort(this.results, comparator);
    }
}
