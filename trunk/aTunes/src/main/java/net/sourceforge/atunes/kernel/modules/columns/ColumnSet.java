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
package net.sourceforge.atunes.kernel.modules.columns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;

/**
 * A set of columns used in a component
 * @author fleax
 *
 */
public abstract class ColumnSet  {

	/** Available columns */
	private List<Column> availableColumns;
	
	/** Column map for direct access */
	private Map<Class<? extends Column>, Column> columnMap;
	
    /** The current visible columns. */
    private List<Class<? extends Column>> currentColumns;
    
    /** <code>true</code> for columns of play list */
    private boolean playListExclusive;
    
    /** Logger */
    private Logger logger;
    
    public ColumnSet(boolean playListExclusive) {
    	this.playListExclusive = playListExclusive;
    	ColumnSets.registerColumnSet(this);
	}
    
    /**
     * Returns logger
     * @return
     */
    protected final Logger getLogger() {
    	if (logger == null) {
    		logger = new Logger();
    	}
    	return logger;
    }
    
    /**
     * Gets the available columns.
     * 
     * @return the available columns
     */
    private List<Column> getAvailableColumns() {
        if (availableColumns == null) {
            // Try to get configuration saved
            Map<String, ColumnBean> columnsBeans = getColumnsConfiguration();

            availableColumns = Columns.getColumns(this.playListExclusive);
            columnMap = new HashMap<Class<? extends Column>, Column>();
            for (Column c : availableColumns) {
            	columnMap.put(c.getClass(), c);
            }

            // Apply configuration
            if (columnsBeans != null) {
                for (Column column : availableColumns) {
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
     * Store current column settings.
     */
    public final void storeCurrentColumnSettings() {
        // Get ColumnsBean from default columns and store it
        HashMap<String, ColumnBean> newColumnsBeans = new HashMap<String, ColumnBean>();
        for (Column column : getAvailableColumns()) {
            newColumnsBeans.put(column.getClass().getName(), column.getColumnBean());
        }
        setColumnsConfiguration(newColumnsBeans);
    }

    /**
     * Returns the amount of columns visible in this set
     * @return
     */
    public final int getVisibleColumnCount() {
        int visibleColumns = 0;
        for (Column c : getAvailableColumns()) {
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
    public final List<Column> getColumnsOrdered() {
        List<Column> result = new ArrayList<Column>(getAvailableColumns());
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

        currentColumns = new ArrayList<Class<? extends Column>>();

        for (Column c : getColumnsOrdered()) {
            if (c.isVisible()) {
                currentColumns.add(c.getClass());
            }
        }
    }
    
    /**
     * Returns columns used for filtering
     * @return
     */
    private List<Column> getColumnsForFilter() {
    	List<Column> columnsForFilter = new ArrayList<Column>();
        for (Column c : getAvailableColumns()) {
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
    public final Class<? extends Column> getColumnId(int colIndex) {
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
    public List<Column> getColumnsForSelection() {
        return new ArrayList<Column>(getAvailableColumns());
    }
    
    /**
     * Returns a column object given its class name
     * 
     * @param columnClass
     * @return
     */
    public Column getColumn(Class<? extends Column> columnClass) {
    	return columnMap.get(columnClass);
    }

    /**
     * Filters audio objects with given filter and current visible columns of this column set
     * @param audioObjects
     * @param filter
     * @return
     */
    public List<AudioObject> filterAudioObjects(List<AudioObject> audioObjects, String filter) {
    	List<AudioObject> result = new ArrayList<AudioObject>();
    	List<Column> columnsForFilter = getColumnsForFilter();
    	String lowerCaseFilter = filter.toLowerCase();
    	
    	for (AudioObject audioObject : audioObjects) {
    		if (filterAudioObject(audioObject, columnsForFilter, lowerCaseFilter)) {
    			result.add(audioObject);
    		}
    	}
    	
    	return result;
    }
    
    /**
     * Returns <code>true</code> if given audio object passes the given filter for at least one column
     * @param audioObject
     * @param columns
     * @return
     */
    private boolean filterAudioObject(AudioObject audioObject, List<Column> columns, String filter) {
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
     * @param column
     */
    protected final void addNewColumn(Column column) {
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
     * @param columnClass
     */
    public void removeColumn(Class<?> columnClass) {
		Column column = columnMap.get(columnClass);
		columnMap.remove(columnClass);
   		getAvailableColumns().remove(column);
    	refreshColumns();
    }

    /**
     * Returns existing column configuration for this column set
     * @return
     */
    protected abstract Map<String, ColumnBean> getColumnsConfiguration();
    
    /**
     * Sets column configuration of this column set
     * @param columnsConfiguration
     */
    protected abstract void setColumnsConfiguration(Map<String, ColumnBean> columnsConfiguration);

    /**
     * Method called to refresh columns when a plugin is activated or deactivated
     */
    protected abstract void refreshColumns();

}
