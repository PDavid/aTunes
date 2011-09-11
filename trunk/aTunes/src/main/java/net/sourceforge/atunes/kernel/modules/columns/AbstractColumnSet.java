/*
 * aTunes 2.1.0-SNAPSHOT
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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IState;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * A set of columns used in a component
 * 
 * @author fleax
 */
@PluginApi
public abstract class AbstractColumnSet {

    /** Available columns */
    private List<AbstractColumn> availableColumns;

    /** Column map for direct access */
    private Map<Class<? extends AbstractColumn>, AbstractColumn> columnMap;

    /** The current visible columns. */
    private List<Class<? extends AbstractColumn>> currentColumns;

	/**
	 * State of app
	 */
	protected IState state;	
	
    protected IFrame frame;
    
    public AbstractColumnSet() {
        ColumnSets.registerColumnSet(this);
    }
    
    public final void setState(IState state) {
    	this.state = state;
    }

    public final void setFrame(IFrame frame) {
		this.frame = frame;
	}



    /**
     * Gets the available columns.
     * 
     * @return the available columns
     */
    private List<AbstractColumn> getAvailableColumns() {
        if (availableColumns == null) {
            // Try to get configuration saved
            Map<String, ColumnBean> columnsBeans = getColumnsConfiguration();

            availableColumns = getAllowedColumns();
            columnMap = new HashMap<Class<? extends AbstractColumn>, AbstractColumn>();
            for (AbstractColumn c : availableColumns) {
                columnMap.put(c.getClass(), c);
            }

            // Apply configuration
            if (columnsBeans != null) {
                for (AbstractColumn column : availableColumns) {
                    ColumnBean bean = columnsBeans.get(column.getClass().getName());
                    if (bean != null) {
                        column.applyColumnBean(bean);
                    }
                }
            }
        }
        return availableColumns;
    }

    /**
     * Returns a list of columns allowed to be used in this column set
     * 
     * @return
     */
    protected abstract List<AbstractColumn> getAllowedColumns();

    /**
     * Store current column settings.
     */
    public final void saveColumnSet() {
        // Get ColumnsBean from default columns and store it
        HashMap<String, ColumnBean> newColumnsBeans = new HashMap<String, ColumnBean>();
        for (AbstractColumn column : getAvailableColumns()) {
            newColumnsBeans.put(column.getClass().getName(), column.getColumnBean());
        }
        setColumnsConfiguration(newColumnsBeans);
    }

    /**
     * Returns the amount of columns visible in this set
     * 
     * @return
     */
    public final int getVisibleColumnCount() {
        int visibleColumns = 0;
        for (AbstractColumn c : getAvailableColumns()) {
            if (c.isVisible()) {
                visibleColumns++;
            }
        }
        return visibleColumns;
    }

    /**
     * Returns columns in order.
     * 
     * @return the columns ordered
     */
    public final List<AbstractColumn> getColumnsOrdered() {
        List<AbstractColumn> result = new ArrayList<AbstractColumn>(getAvailableColumns());
        Collections.sort(result);
        return result;
    }

    /**
     * Sets columns
     */
    public final void setCurrentColumns() {
        int columnNumber = getVisibleColumnCount();
        if (columnNumber == 0) {
            return;
        }

        currentColumns = new ArrayList<Class<? extends AbstractColumn>>();

        for (AbstractColumn c : getColumnsOrdered()) {
            if (c.isVisible()) {
                currentColumns.add(c.getClass());
            }
        }
    }

    /**
     * Returns columns used for filtering
     * 
     * @return
     */
    private List<AbstractColumn> getColumnsForFilter() {
        List<AbstractColumn> columnsForFilter = new ArrayList<AbstractColumn>();
        for (AbstractColumn c : getAvailableColumns()) {
            if (c.isVisible() && c.isUsedForFilter()) {
                columnsForFilter.add(c);
            }
        }
        return columnsForFilter;
    }

    /**
     * Returns Column ID given a visible column number
     * 
     * @param colIndex
     *            the col index
     * 
     * @return the column id
     */
    public final Class<? extends AbstractColumn> getColumnId(int colIndex) {
        if (currentColumns == null) {
            setCurrentColumns();
        }
        return currentColumns.get(colIndex);
    }

    /**
     * Returns columns for selection
     * 
     * @return the columns for selection
     */
    public List<AbstractColumn> getColumnsForSelection() {
        return new ArrayList<AbstractColumn>(getAvailableColumns());
    }

    /**
     * Returns a column object given its class name
     * 
     * @param columnClass
     * @return
     */
    public AbstractColumn getColumn(Class<? extends AbstractColumn> columnClass) {
        return columnMap.get(columnClass);
    }

    /**
     * Filters audio objects with given filter and current visible columns of
     * this column set
     * 
     * @param audioObjects
     * @param filter
     * @return
     */
    public List<IAudioObject> filterAudioObjects(List<IAudioObject> audioObjects, String filter) {
        List<IAudioObject> result = new ArrayList<IAudioObject>();
        List<AbstractColumn> columnsForFilter = getColumnsForFilter();
        String lowerCaseFilter = filter.toLowerCase();

        for (IAudioObject audioObject : audioObjects) {
            if (filterAudioObject(audioObject, columnsForFilter, lowerCaseFilter)) {
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
     * @return
     */
    private boolean filterAudioObject(IAudioObject audioObject, List<AbstractColumn> columns, String filter) {
        boolean passed = false;
        int i = 0;
        while (!passed && i < columns.size()) {
            String value = columns.get(i++).getValueForFilter(audioObject);
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
    protected final void addNewColumn(AbstractColumn column) {
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

    /**
     * Called to remove an available column
     * 
     * @param columnClass
     */
    public void removeColumn(Class<?> columnClass) {
        AbstractColumn column = columnMap.get(columnClass);
        columnMap.remove(columnClass);
        getAvailableColumns().remove(column);
        refreshColumns();
    }

    /**
     * Returns the column sorted (if any)
     * 
     * @return
     */
    public AbstractColumn getSortedColumn() {
        if (currentColumns != null) {
            for (Class<? extends AbstractColumn> columnClass : currentColumns) {
                AbstractColumn column = getColumn(columnClass);
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
    protected abstract void setColumnsConfiguration(Map<String, ColumnBean> columnsConfiguration);

    /**
     * Method called to refresh columns when a plugin is activated or
     * deactivated
     */
    protected abstract void refreshColumns();

}
