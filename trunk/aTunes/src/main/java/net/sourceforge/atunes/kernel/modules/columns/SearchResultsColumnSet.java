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

import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.gui.model.SearchResultColumnModel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

/**
 * This class defines all columns than can be viewed in search results
 * 
 * @author fleax
 */
public final class SearchResultsColumnSet extends ColumnSet {

    /**
     * Singleton instance
     */
    private static SearchResultsColumnSet instance;

    /**
     * Private constructor
     */
    private SearchResultsColumnSet() {
        super();
    }

    /**
     * Returns singleton instance
     * 
     * @return
     */
    public static SearchResultsColumnSet getInstance() {
        if (instance == null) {
            instance = new SearchResultsColumnSet();
        }
        return instance;
    }

    @Override
    protected List<Column> getAllowedColumns() {
        return Columns.getColumns(false);
    }

    @Override
    protected Map<String, ColumnBean> getColumnsConfiguration() {
        return ApplicationState.getInstance().getSearchResultsColumns();
    }

    @Override
    protected void setColumnsConfiguration(Map<String, ColumnBean> columnsConfiguration) {
        ApplicationState.getInstance().setSearchResultsColumns(columnsConfiguration);
    }

    @Override
    protected void refreshColumns() {
        ((SearchResultColumnModel) ControllerProxy.getInstance().getSearchResultsController().getComponentControlled().getSearchResultsTable().getColumnModel())
                .arrangeColumns(false);
    }
}
