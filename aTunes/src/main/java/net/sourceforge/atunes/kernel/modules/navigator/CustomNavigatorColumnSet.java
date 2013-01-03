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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.gui.NavigationTableColumnModel;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumnSet;
import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITable;

/**
 * Custom column set to be used by navigation views that use custom columns
 * 
 * @author fleax
 * 
 */
public final class CustomNavigatorColumnSet extends AbstractColumnSet {

    private ITable navigationTable;

    /**
     * Name of column set. Used to store and retrieve settings
     */
    private String columnSetName;

    private IStateNavigation stateNavigation;

    /**
     * @param stateNavigation
     */
    public void setStateNavigation(final IStateNavigation stateNavigation) {
	this.stateNavigation = stateNavigation;
    }

    /**
     * @param columnSetName
     */
    public void setColumnSetName(final String columnSetName) {
	this.columnSetName = columnSetName;
    }

    /**
     * @param navigationTable
     */
    public void setNavigationTable(final ITable navigationTable) {
	this.navigationTable = navigationTable;
    }

    @Override
    protected void setColumnsConfiguration(
	    final Map<String, ColumnBean> columnsConfiguration) {
	Map<String, Map<String, ColumnBean>> configuration = stateNavigation
		.getCustomNavigatorColumns();
	if (configuration == null) {
	    configuration = new HashMap<String, Map<String, ColumnBean>>();
	}
	configuration.put(this.columnSetName, columnsConfiguration);
	stateNavigation.setCustomNavigatorColumns(configuration);
    }

    @Override
    protected Map<String, ColumnBean> getColumnsConfiguration() {
	Map<String, Map<String, ColumnBean>> configuration = stateNavigation
		.getCustomNavigatorColumns();
	if (configuration != null) {
	    return configuration.get(this.columnSetName);
	}
	return null;
    }

    @Override
    protected void refreshColumns() {
	((NavigationTableColumnModel) navigationTable.getColumnModel())
		.arrangeColumns(false);
    }
}
