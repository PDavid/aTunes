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

import java.util.Map;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.ISearchHandler;

/**
 * This class defines all columns than can be viewed in search results
 * 
 * @author fleax
 */
public final class SearchResultsColumnSet extends AbstractColumnSet {

    @Override
    protected Map<String, ColumnBean> getColumnsConfiguration() {
        return state.getSearchResultsColumns();
    }

    @Override
    protected void setColumnsConfiguration(Map<String, ColumnBean> columnsConfiguration) {
        state.setSearchResultsColumns(columnsConfiguration);
    }

    @Override
    protected void refreshColumns() {
    	Context.getBean(ISearchHandler.class).refreshSearchResultColumns();
    }
}
