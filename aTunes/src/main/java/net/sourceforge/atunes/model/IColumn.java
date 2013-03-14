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

package net.sourceforge.atunes.model;

import java.io.Serializable;
import java.util.Comparator;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Represents a column of a table
 * 
 * @author alex
 * @param <T>
 * 
 */
public interface IColumn<T> extends Serializable, Comparable<IColumn<?>> {

	/**
	 * Apply column bean.
	 * 
	 * @param bean
	 *            the bean
	 */
	public void applyColumnBean(ColumnBean bean);

	/**
	 * Gets the alignment.
	 * 
	 * @return the alignment
	 */
	public int getAlignment();

	/**
	 * Gets the cell editor.
	 * 
	 * @return the cellEditor
	 */
	public TableCellEditor getCellEditor();

	/**
	 * Gets the cell renderer.
	 * 
	 * @return the cellRenderer
	 */
	public TableCellRenderer getCellRenderer();

	/**
	 * Gets the column bean.
	 * 
	 * @return the column bean
	 */
	public ColumnBean getColumnBean();

	/**
	 * Gets the column class.
	 * 
	 * @return the columnClass
	 */
	public Class<?> getColumnClass();

	/**
	 * Gets the column name.
	 * 
	 * @return the column name
	 */
	public String getColumnName();

	/**
	 * Gets the header text.
	 * 
	 * @return the headerText
	 */
	public String getHeaderText();

	/**
	 * Gets the order.
	 * 
	 * @return the order
	 */
	public int getOrder();

	/**
	 * Returns value for a column of an audio object in given row
	 * 
	 * @param audioObject
	 * @param row
	 * @return
	 */
	public T getValueFor(IAudioObject audioObject, int row);

	/**
	 * Gets the width.
	 * 
	 * @return the width
	 */
	public int getWidth();

	/**
	 * Checks if is editable.
	 * 
	 * @return the editable
	 */
	public boolean isEditable();

	/**
	 * Returns if this column can be sorted. All columns are sortable unless
	 * this method is overrided
	 * 
	 * @return
	 */
	public boolean isSortable();

	/**
	 * Checks if is resizable.
	 * 
	 * @return the resizable
	 */
	public boolean isResizable();

	/**
	 * Checks if is visible.
	 * 
	 * @return the visible
	 */
	public boolean isVisible();

	/**
	 * Sets the alignment.
	 * 
	 * @param alignment
	 *            the alignment to set
	 */
	public void setAlignment(int alignment);

	/**
	 * Sets the column class.
	 * 
	 * @param columnClass
	 *            the columnClass to set
	 */
	public void setColumnClass(Class<?> columnClass);

	/**
	 * Sets the editable.
	 * 
	 * @param editable
	 *            the editable to set
	 */
	public void setEditable(boolean editable);

	/**
	 * Sets the header text.
	 * 
	 * @param headerText
	 *            the headerText to set
	 */
	public void setColumnName(String headerText);

	/**
	 * Sets the order.
	 * 
	 * @param order
	 *            the order to set
	 */
	public void setOrder(int order);

	/**
	 * Sets the resizable.
	 * 
	 * @param resizable
	 *            the resizable to set
	 */
	public void setResizable(boolean resizable);

	/**
	 * Sets value for a property of an audio object.
	 * 
	 * @param audioObject
	 *            the audio object
	 * @param value
	 *            the value
	 */
	public void setValueFor(IAudioObject audioObject, Object value);

	/**
	 * Sets the visible.
	 * 
	 * @param visible
	 *            the visible to set
	 */
	public void setVisible(boolean visible);

	/**
	 * Sets the width.
	 * 
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width);

	/**
	 * Return a comparator to sort by this column. If no sort has been done
	 * before ascending comparator is returned
	 * 
	 * @param changeSort
	 * @return
	 */
	public Comparator<IAudioObject> getComparator();

	/**
	 * @return the usedForFilter
	 */
	public boolean isUsedForFilter();

	/**
	 * @param usedForFilter
	 *            the usedForFilter to set
	 */
	public void setUsedForFilter(boolean usedForFilter);

	/**
	 * Returns string used to filter by this column. By default it performs a
	 * <code>toString</code> over object returned by <code>getValueFor</code>
	 * 
	 * @param audioObject
	 * @param row
	 * @return
	 */
	public String getValueForFilter(IAudioObject audioObject, int row);

	/**
	 * @param columnSort
	 *            the columnSort to set
	 */
	public void setColumnSort(ColumnSort columnSort);

	/**
	 * @return the columnSort
	 */
	public ColumnSort getColumnSort();
}