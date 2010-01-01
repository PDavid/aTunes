/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.gui.model;

import java.util.Comparator;

import net.sourceforge.atunes.kernel.modules.columns.Column;
import net.sourceforge.atunes.kernel.modules.columns.ColumnSet;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

public abstract class ColumnSetTableModel extends CommonTableModel {
	
	private ColumnSet columnSet;
	
	public ColumnSetTableModel(ColumnSet columnSet) {
		super();
		this.columnSet = columnSet;		
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
        return getColumn(colIndex).getColumnClass();
    }
    
    /**
     * Return column count.
     * 
     * @return the column count
     */
    @Override
    public int getColumnCount() {
        return columnSet.getVisibleColumnCount();
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
        return I18nUtils.getString(getColumn(colIndex).getHeaderText());
    }

    /**
     * Returns column in given index
     * @param colIndex
     * @return
     */
    protected Column getColumn(int colIndex) {
    	return columnSet.getColumn(columnSet.getColumnId(colIndex));
    }

    /**
     * Abstract method to sort by the given comparator
     * @param comparator
     */
    public abstract void sort(Comparator<AudioObject> comparator);
	

}
