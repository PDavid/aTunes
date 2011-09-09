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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.gui.model.NavigationTableColumnModel;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumnSet;
import net.sourceforge.atunes.kernel.modules.columns.ColumnBean;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;

/**
 * Custom column set to be used by navigation views that use custom columns
 * 
 * @author fleax
 * 
 */
public abstract class AbstractCustomNavigatorColumnSet extends AbstractColumnSet {

    /**
     * Name of column set. Used to store and retrieve settings
     */
    private String columnSetName;

    /**
     * Default constructor
     */
    public AbstractCustomNavigatorColumnSet(String columnSetName) {
        super();
        this.columnSetName = columnSetName;
    }

    @Override
    protected void setColumnsConfiguration(Map<String, ColumnBean> columnsConfiguration) {
        Map<String, Map<String, ColumnBean>> configuration = state.getCustomNavigatorColumns();
        if (configuration == null) {
            configuration = new HashMap<String, Map<String, ColumnBean>>();
        }
        configuration.put(this.columnSetName, columnsConfiguration);
        state.setCustomNavigatorColumns(configuration);
    }

    @Override
    protected Map<String, ColumnBean> getColumnsConfiguration() {
        Map<String, Map<String, ColumnBean>> configuration = state.getCustomNavigatorColumns();
        if (configuration != null) {
            return configuration.get(this.columnSetName);
        }
        return null;
    }

    @Override
    protected void refreshColumns() {
        ((NavigationTableColumnModel) GuiHandler.getInstance().getNavigationTablePanel().getNavigationTable().getColumnModel()).arrangeColumns(false);
    }

}
