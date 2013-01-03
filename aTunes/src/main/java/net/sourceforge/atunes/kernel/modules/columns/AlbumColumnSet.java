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

import java.util.Map;

import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.IStateCore;

/**
 * This class defines all columns than can be viewed in album list
 * 
 * @author encestre
 */
public final class AlbumColumnSet extends AbstractColumnSet {
	
	private IStateCore stateCore;
	
	/**
	 * @param stateCore
	 */
	public void setStateCore(IStateCore stateCore) {
		this.stateCore = stateCore;
	}
	
    @Override
    protected Map<String, ColumnBean> getColumnsConfiguration() {
    	return stateCore.getAlbumsColumns();
    }

    @Override
    protected void setColumnsConfiguration(Map<String, ColumnBean> columnsConfiguration) {
    	stateCore.setAlbumColumns(columnsConfiguration);
    }

    @Override
    protected void refreshColumns() {}
}
