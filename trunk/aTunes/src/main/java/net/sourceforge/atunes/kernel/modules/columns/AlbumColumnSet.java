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

import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.IColumn;

/**
 * This class defines all columns than can be viewed in album list
 * 
 * @author encestre
 */
public final class AlbumColumnSet extends AbstractColumnSet {
	
	private List<IColumn> allowedColumns;
	
	/**
	 * @param allowedColumns
	 */
	public void setAllowedColumns(List<IColumn> allowedColumns) {
		this.allowedColumns = allowedColumns;
		int order = 0;
		for (IColumn column : this.allowedColumns) {
			column.setOrder(order++);
		}
	}
	
    @Override
    protected List<IColumn> getAllowedColumns() {
        return allowedColumns;
    }

    @Override
    protected Map<String, ColumnBean> getColumnsConfiguration() {
    	return state.getAlbumsColumns();
    }

    @Override
    protected void setColumnsConfiguration(Map<String, ColumnBean> columnsConfiguration) {
        state.setAlbumColumns(columnsConfiguration);
    }

    @Override
    protected void refreshColumns() {}
}
