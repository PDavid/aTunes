/*
 * aTunes 3.1.0
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IColumnSet;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * A set of columns used in a component
 * 
 * @author fleax
 */
@PluginApi
@SuppressWarnings("rawtypes")
public abstract class AbstractColumnSet implements IColumnSet {

    /** Available columns */
    private List<IColumn<?>> availableColumns;

    /** Column map for direct access */
    private Map<Class<? extends IColumn>, IColumn> columnMap;

    /** The current visible columns. */
    private List<Class<? extends IColumn<?>>> currentColumns;

    private List<IColumn<?>> allowedColumns;

    /**
     * @param allowedColumns
     */
    public final void setAllowedColumns(final List<IColumn<?>> allowedColumns) {
	this.allowedColumns = allowedColumns;
	int order = 0;
	for (IColumn column : this.allowedColumns) {
	    column.setOrder(order++);
	}
    }

    /**
     * Returns a list of columns allowed to be used in this column set
     * 
     * @return
     */
    protected final List<IColumn<?>> getAllowedColumns() {
	return allowedColumns;
    }

    /**
     * Gets the available columns.
     * 
     * @return the available columns
     */
    private List<IColumn<?>> getAvailableColumns() {
	if (availableColumns == null) {
	    // Try to get configuration saved
	    Map<String, ColumnBean> columnsBeans = getColumnsConfiguration();

	    availableColumns = getAllowedColumns();
	    columnMap = new HashMap<Class<? extends IColumn>, IColumn>();
	    for (IColumn c : availableColumns) {
		columnMap.put(c.getClass(), c);
	    }

	    // Apply configuration
	    if (columnsBeans != null) {
		for (IColumn column : availableColumns) {
		    ColumnBean bean = columnsBeans.get(column.getClass()
			    .getName());
		    if (bean != null) {
			column.applyColumnBean(bean);
		    }
		}
	    }
	}
	return availableColumns;
    }

    @Override
    public final void saveColumnSet() {
	// Get ColumnsBean from default columns and store it
	HashMap<String, ColumnBean> newColumnsBeans = new HashMap<String, ColumnBean>();
	for (IColumn column : getAvailableColumns()) {
	    newColumnsBeans.put(column.getClass().getName(),
		    column.getColumnBean());
	}
	setColumnsConfiguration(newColumnsBeans);
    }

    @Override
    public final int getVisibleColumnCount() {
	int visibleColumns = 0;
	for (IColumn c : getAvailableColumns()) {
	    if (c.isVisible()) {
		visibleColumns++;
	    }
	}
	return visibleColumns;
    }

    @Override
    public final List<IColumn<?>> getColumnsOrdered() {
	List<IColumn<?>> result = new ArrayList<IColumn<?>>(
		getAvailableColumns());
	Collections.sort(result);
	return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void setCurrentColumns() {
	int columnNumber = getVisibleColumnCount();
	if (columnNumber == 0) {
	    return;
	}

	currentColumns = new ArrayList<Class<? extends IColumn<?>>>();

	for (IColumn<?> c : getColumnsOrdered()) {
	    if (c.isVisible()) {
		currentColumns.add((Class<? extends IColumn<?>>) c.getClass());
	    }
	}
    }

    /**
     * Returns columns used for filtering
     * 
     * @return
     */
    private List<IColumn> getColumnsForFilter() {
	List<IColumn> columnsForFilter = new ArrayList<IColumn>();
	for (IColumn c : getAvailableColumns()) {
	    if (c.isVisible() && c.isUsedForFilter()) {
		columnsForFilter.add(c);
	    }
	}
	return columnsForFilter;
    }

    @Override
    public final Class<? extends IColumn<?>> getColumnId(final int colIndex) {
	if (currentColumns == null) {
	    setCurrentColumns();
	}
	return currentColumns.get(colIndex);
    }

    @Override
    public List<IColumn<?>> getColumnsForSelection() {
	return new ArrayList<IColumn<?>>(getAvailableColumns());
    }

    @Override
    public IColumn<?> getColumn(final Class<? extends IColumn<?>> columnClass) {
	return columnMap.get(columnClass);
    }

    @Override
    public List<IAudioObject> filterAudioObjects(
	    final List<IAudioObject> audioObjects, final String filter) {
	List<IAudioObject> result = new ArrayList<IAudioObject>();
	List<IColumn> columnsForFilter = getColumnsForFilter();
	String lowerCaseFilter = filter.toLowerCase();

	for (int position = 0; position < audioObjects.size(); position++) {
	    IAudioObject audioObject = audioObjects.get(position);
	    if (filterAudioObject(audioObject, columnsForFilter,
		    lowerCaseFilter, position)) {
		result.add(audioObject);
	    }
	}

	return result;
    }

    /**
     * Returns <code>true</code> if given audio object passes the given filter
     * for at least one column
     * 
     * @param audioObject
     * @param columns
     * @param filter
     * @param position
     * @return
     */
    private boolean filterAudioObject(final IAudioObject audioObject,
	    final List<IColumn> columns, final String filter, final int position) {
	boolean passed = false;
	int i = 0;
	while (!passed && i < columns.size()) {
	    String value = columns.get(i++).getValueForFilter(audioObject,
		    position);
	    if (value != null) {
		passed = value.toLowerCase().contains(filter);
	    }
	}
	return passed;
    }

    /**
     * Called to add a new available column
     * 
     * @param column
     */
    protected final void addNewColumn(final IColumn column) {
	column.setOrder(getAvailableColumns().size());
	getAvailableColumns().add(column);
	columnMap.put(column.getClass(), column);

	// Apply configuration if column has been previously used
	boolean needRefresh = false;
	Map<String, ColumnBean> columnsBeans = getColumnsConfiguration();
	if (columnsBeans != null) {
	    ColumnBean bean = columnsBeans.get(column.getClass().getName());
	    if (bean != null) {
		column.applyColumnBean(bean);
		needRefresh = true;
	    }
	}

	// Refresh columns if necessary
	if (needRefresh) {
	    refreshColumns();
	}
    }

    @Override
    public void removeColumn(final Class<?> columnClass) {
	IColumn column = columnMap.get(columnClass);
	columnMap.remove(columnClass);
	getAvailableColumns().remove(column);
	refreshColumns();
    }

    @Override
    public IColumn<?> getSortedColumn() {
	if (currentColumns != null) {
	    for (Class<? extends IColumn<?>> columnClass : currentColumns) {
		IColumn<?> column = getColumn(columnClass);
		if (column.getColumnSort() != null) {
		    return column;
		}
	    }
	}
	return null;
    }

    /**
     * Returns existing column configuration for this column set
     * 
     * @return
     */
    protected abstract Map<String, ColumnBean> getColumnsConfiguration();

    /**
     * Saves column configuration of this column set
     * 
     * @param columnsConfiguration
     */
    protected abstract void setColumnsConfiguration(
	    Map<String, ColumnBean> columnsConfiguration);

    /**
     * Method called to refresh columns when a plugin is activated or
     * deactivated
     */
    protected abstract void refreshColumns();

}
