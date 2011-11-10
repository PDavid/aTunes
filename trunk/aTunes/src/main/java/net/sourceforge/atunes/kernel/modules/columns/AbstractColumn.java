/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.columns;

import java.util.Comparator;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.ColumnSort;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.utils.GuiUtils;

/**
 * This class represents a column
 * 
 * @author fleax
 */
public abstract class AbstractColumn implements IColumn {

    private static final long serialVersionUID = 7407756833207959017L;

    /** Header text of column. */
    private String columnName;

    /** Resizable. */
    private boolean resizable = true;

    /** Width of column. */
    private int width = 150;

    /** Class of data. */
    private Class<?> columnClass;

    /** Visible column. */
    private boolean visible;

    /** Order. */
    private int order;

    /** Text alignment. */
    private int alignment = GuiUtils.getComponentOrientationAsSwingConstant();

    /** Editable flag. */
    private boolean editable = false;

    /** Indicates if this column can be used to filter objects */
    private boolean usedForFilter = false;

    /**
     * Last sort order used for this column
     */
    private transient ColumnSort columnSort;

    /**
     * Constructor with columnId, headerText and columnClass.
     * 
     * @param columnId
     *            the column id
     * @param name
     *            the header text
     * @param columnClass
     *            the column class
     */
    public AbstractColumn(String name, Class<?> columnClass) {
        this.columnName = name;
        this.columnClass = columnClass;
    }

    @Override
	public void applyColumnBean(ColumnBean bean) {
        order = bean.getOrder();
        width = bean.getWidth();
        visible = bean.isVisible();
        columnSort = bean.getSort();
    }

    @Override
    public int compareTo(IColumn o) {
        return Integer.valueOf(order).compareTo(o.getOrder());
    }
    
    @Override
	public int getAlignment() {
        return alignment;
    }

    @Override
	public TableCellEditor getCellEditor() {
        return null;
    }

    @Override
	public TableCellRenderer getCellRenderer() {
        return null;
    }

    @Override
	public ColumnBean getColumnBean() {
        ColumnBean bean = new ColumnBean();
        bean.setOrder(order);
        bean.setWidth(width);
        bean.setVisible(visible);
        bean.setSort(columnSort);
        return bean;
    }

    @Override
	public Class<?> getColumnClass() {
        return columnClass;
    }

    @Override
	public String getColumnName() {
        return columnName;
    }

    /**
     * Compares two objects in ascending order
     * 
     * @return
     */
    protected abstract int ascendingCompare(IAudioObject o1, IAudioObject o2);

    @Override
	public String getHeaderText() {
        return columnName;
    }

    @Override
	public int getOrder() {
        return order;
    }

    @Override
	public boolean isPlayListExclusive() {
        return false;
    }

    @Override
	public int getWidth() {
        return width;
    }

    @Override
	public boolean isEditable() {
        return editable;
    }

    @Override
	public boolean isSortable() {
        return true;
    }

    @Override
	public boolean isResizable() {
        return resizable;
    }

    @Override
	public boolean isVisible() {
        return visible;
    }

    @Override
	public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    @Override
	public void setColumnClass(Class<?> columnClass) {
        this.columnClass = columnClass;
    }

    @Override
	public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
	public void setColumnName(String headerText) {
        this.columnName = headerText;
    }

    @Override
	public void setOrder(int order) {
        this.order = order;
    }

    @Override
	public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    @Override
	public void setValueFor(IAudioObject audioObject, Object value) {
        // Does nothing, should be overrided
    }

    @Override
	public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
	public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return columnName;
    }

    @Override
	public Comparator<IAudioObject> getComparator(boolean changeSort) {
        if (columnSort == null) {
            columnSort = ColumnSort.ASCENDING;
        } else if (columnSort == ColumnSort.ASCENDING && changeSort) {
            columnSort = ColumnSort.DESCENDING;
        } else if (columnSort == ColumnSort.DESCENDING && changeSort) {
            columnSort = ColumnSort.ASCENDING;
        }

        return new Comparator<IAudioObject>() {
            @Override
            public int compare(IAudioObject o1, IAudioObject o2) {
                return columnSort == ColumnSort.ASCENDING ? ascendingCompare(o1, o2) : -ascendingCompare(o1, o2);
            }
        };
    }

    @Override
	public boolean isUsedForFilter() {
        return usedForFilter;
    }

    @Override
	public void setUsedForFilter(boolean usedForFilter) {
        this.usedForFilter = usedForFilter;
    }

    @Override
	public String getValueForFilter(IAudioObject audioObject) {
        return getValueFor(audioObject).toString();
    }

    @Override
	public void setColumnSort(ColumnSort columnSort) {
        this.columnSort = columnSort;
    }

    @Override
	public ColumnSort getColumnSort() {
        return columnSort;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + order;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractColumn other = (AbstractColumn) obj;
		if (order != other.order)
			return false;
		return true;
	}
}
