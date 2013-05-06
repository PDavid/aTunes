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

package net.sourceforge.atunes.kernel.modules.columns;

import java.text.Collator;
import java.util.Comparator;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.ColumnSort;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class represents a column
 * 
 * @author alex
 * 
 * @param <T>
 */
public abstract class AbstractColumn<T> implements IColumn<T> {

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
	private int alignment = -1;

	/** Editable flag. */
	private boolean editable = false;

	/** Indicates if this column can be used to filter objects */
	private boolean usedForFilter = false;

	/**
	 * Last sort order used for this column
	 */
	private transient ColumnSort columnSort;

	private transient Collator collator;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

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
	public AbstractColumn(final String name) {
		this.columnName = name;
	}

	/**
	 * Initializes column
	 */
	public void initialize() {
		this.columnClass = (Class<?>) ReflectionUtils
				.getTypeArgumentsOfParameterizedType(this.getClass())[0];
		if (this.alignment == -1) {
			this.alignment = this.controlsBuilder
					.getComponentOrientationAsSwingConstant();
		}
	}

	/**
	 * @param collator
	 */
	public void setCollator(final Collator collator) {
		this.collator = collator;
	}

	/**
	 * Helper method to compare strings
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	protected int compare(final String s1, final String s2) {
		return this.collator.compare(s1, s2);
	}

	@Override
	public void applyColumnBean(final ColumnBean bean) {
		this.order = bean.getOrder();
		this.width = bean.getWidth();
		this.visible = bean.isVisible();
		this.columnSort = bean.getSort();
	}

	@Override
	public int compareTo(final IColumn<?> o) {
		return Integer.valueOf(this.order).compareTo(o.getOrder());
	}

	@Override
	public int getAlignment() {
		return this.alignment;
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
		bean.setOrder(this.order);
		bean.setWidth(this.width);
		bean.setVisible(this.visible);
		bean.setSort(this.columnSort);
		return bean;
	}

	@Override
	public Class<?> getColumnClass() {
		return this.columnClass;
	}

	@Override
	public String getColumnName() {
		return this.columnName;
	}

	@Override
	public String getHeaderText() {
		return this.columnName;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public boolean isEditable() {
		return this.editable;
	}

	@Override
	public boolean isSortable() {
		return true;
	}

	@Override
	public boolean isResizable() {
		return this.resizable;
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public void setAlignment(final int alignment) {
		this.alignment = alignment;
	}

	@Override
	public void setColumnClass(final Class<?> columnClass) {
		this.columnClass = columnClass;
	}

	@Override
	public void setEditable(final boolean editable) {
		this.editable = editable;
	}

	@Override
	public void setColumnName(final String headerText) {
		this.columnName = headerText;
	}

	@Override
	public void setOrder(final int order) {
		this.order = order;
	}

	@Override
	public void setResizable(final boolean resizable) {
		this.resizable = resizable;
	}

	@Override
	public void setValueFor(final IAudioObject audioObject, final Object value) {
		// Does nothing, should be overrided
	}

	@Override
	public void setVisible(final boolean visible) {
		this.visible = visible;
	}

	@Override
	public void setWidth(final int width) {
		this.width = width;
	}

	@Override
	public String toString() {
		return this.columnName;
	}

	@Override
	public final Comparator<IAudioObject> getComparator() {
		Logger.debug("Returning comparator for column: ", this.getClass()
				.getName());

		if (this.columnSort == null
				|| this.columnSort.equals(ColumnSort.ASCENDING)) {
			return new AscendingColumnSortComparator(this);
		} else {
			return new DescendingColumnSortComparator(this);
		}
	}

	@Override
	public boolean isUsedForFilter() {
		return this.usedForFilter;
	}

	@Override
	public void setUsedForFilter(final boolean usedForFilter) {
		this.usedForFilter = usedForFilter;
	}

	@Override
	public String getValueForFilter(final IAudioObject audioObject,
			final int row) {
		T value = getValueFor(audioObject, row);
		return value != null ? value.toString() : null;
	}

	@Override
	public void setColumnSort(final ColumnSort columnSort) {
		this.columnSort = columnSort;
	}

	@Override
	public ColumnSort getColumnSort() {
		return this.columnSort;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.order;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		if (obj instanceof AbstractColumn<?>) {
			@SuppressWarnings("rawtypes")
			AbstractColumn other = (AbstractColumn) obj;
			if (this.order != other.order) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Compares two objects in ascending order
	 * 
	 * @param ao1
	 * @param ao2
	 * @return
	 */
	protected abstract int ascendingCompare(IAudioObject ao1, IAudioObject ao2);

	/**
	 * Compares two objects in descending order
	 * 
	 * @param ao1
	 * @param ao2
	 * @return
	 */
	protected abstract int descendingCompare(IAudioObject ao1, IAudioObject ao2);

}
