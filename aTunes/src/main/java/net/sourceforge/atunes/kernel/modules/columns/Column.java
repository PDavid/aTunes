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
package net.sourceforge.atunes.kernel.modules.columns;

import java.io.Serializable;
import java.util.Comparator;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.GuiUtils;

import org.commonjukebox.plugins.PluginApi;

/**
 * This class represents a column
 * 
 * @author fleax
 */
@PluginApi
public abstract class Column implements Comparable<Column>, Serializable {

    private static final long serialVersionUID = 7407756833207959017L;

    /**
     * Column sort: ascending or descending
     */
    public enum ColumnSort {
        ASCENDING, DESCENDING
    }

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
    public Column(String name, Class<?> columnClass) {
        this.columnName = name;
        this.columnClass = columnClass;
    }

    /**
     * Apply column bean.
     * 
     * @param bean
     *            the bean
     */
    public void applyColumnBean(ColumnBean bean) {
        order = bean.getOrder();
        width = bean.getWidth();
        visible = bean.isVisible();
    }

    /**
     * Compare method.
     * 
     * @param o
     *            the o
     * 
     * @return the int
     */

    @Override
    public int compareTo(Column o) {
        return Integer.valueOf(order).compareTo(o.order);
    }

    /**
     * Gets the alignment.
     * 
     * @return the alignment
     */
    public int getAlignment() {
        return alignment;
    }

    /**
     * Gets the cell editor.
     * 
     * @return the cellEditor
     */
    public TableCellEditor getCellEditor() {
        return null;
    }

    /**
     * Gets the cell renderer.
     * 
     * @return the cellRenderer
     */
    public TableCellRenderer getCellRenderer() {
        return null;
    }

    /**
     * Gets the column bean.
     * 
     * @return the column bean
     */
    public ColumnBean getColumnBean() {
        ColumnBean bean = new ColumnBean();
        bean.setOrder(order);
        bean.setWidth(width);
        bean.setVisible(visible);
        return bean;
    }

    /**
     * Gets the column class.
     * 
     * @return the columnClass
     */
    public Class<?> getColumnClass() {
        return columnClass;
    }

    /**
     * Gets the column name.
     * 
     * @return the column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Compares two audio objects in ascending order
     * 
     * @return
     */
    protected abstract int ascendingCompare(AudioObject ao1, AudioObject ao2);

    /**
     * Gets the header text.
     * 
     * @return the headerText
     */
    public String getHeaderText() {
        return columnName;
    }

    /**
     * Gets the order.
     * 
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * Returns value for a column of an audiofile.
     * 
     * @param audioObject
     *            the audio object
     * 
     * @return the value for
     */
    public abstract Object getValueFor(AudioObject audioObject);
    
    /**
     * Returns if this column can be shown only in play list. By default <code>false</code>
     * @return
     */
    public boolean isPlayListExclusive() {
    	return false;
    }

    /**
     * Gets the width.
     * 
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Checks if is editable.
     * 
     * @return the editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Checks if is resizable.
     * 
     * @return the resizable
     */
    public boolean isResizable() {
        return resizable;
    }

    /**
     * Checks if is visible.
     * 
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets the alignment.
     * 
     * @param alignment
     *            the alignment to set
     */
    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    /**
     * Sets the column class.
     * 
     * @param columnClass
     *            the columnClass to set
     */
    public void setColumnClass(Class<?> columnClass) {
        this.columnClass = columnClass;
    }

    /**
     * Sets the editable.
     * 
     * @param editable
     *            the editable to set
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Sets the header text.
     * 
     * @param headerText
     *            the headerText to set
     */
    public void setColumnName(String headerText) {
        this.columnName = headerText;
    }

    /**
     * Sets the order.
     * 
     * @param order
     *            the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Sets the resizable.
     * 
     * @param resizable
     *            the resizable to set
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    /**
     * Sets value for a property of an audio object.
     * 
     * @param audioObject
     *            the audio object
     * @param value
     *            the value
     */
    public void setValueFor(AudioObject audioObject, Object value) {
        // Does nothing, should be overrided
    }

    /**
     * Sets the visible.
     * 
     * @param visible
     *            the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Sets the width.
     * 
     * @param width
     *            the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return columnName;
    }

    /**
     * Return a comparator to sort playlist by this column. If last sort was
     * descending or not sort was done before, return ascending comparator If
     * las sort was ascending, return descending comparator
     * 
     * @return
     */
    public Comparator<AudioObject> getComparator() {
        if (columnSort == null || columnSort == ColumnSort.DESCENDING) {
            columnSort = ColumnSort.ASCENDING;
            return new Comparator<AudioObject>() {
                @Override
                public int compare(AudioObject o1, AudioObject o2) {
                    return ascendingCompare(o1, o2);
                }
            };
        } else {
            columnSort = ColumnSort.DESCENDING;
            return new Comparator<AudioObject>() {
                @Override
                public int compare(AudioObject o1, AudioObject o2) {
                    return -ascendingCompare(o1, o2);
                }
            };
        }
    }

	/**
	 * @return the usedForFilter
	 */
	public boolean isUsedForFilter() {
		return usedForFilter;
	}

	/**
	 * @param usedForFilter the usedForFilter to set
	 */
	public void setUsedForFilter(boolean usedForFilter) {
		this.usedForFilter = usedForFilter;
	}
	
	/**
	 * Returns string used to filter by this column. If usedForFilter is <code>true</code> this method must be overrided
	 * @param audioObject
	 * @return
	 */
	public String getValueForFilter(AudioObject audioObject) {
		return null;
	}

}
