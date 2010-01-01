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
package net.sourceforge.atunes.gui.model;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.columns.Column;
import net.sourceforge.atunes.kernel.modules.columns.NavigatorColumnSet;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationView;
import net.sourceforge.atunes.utils.GuiUtils;

public final class NavigationTableColumnModel extends CommonColumnModel {

    private static final long serialVersionUID = 1071222881574684439L;

    public NavigationTableColumnModel(JTable table) {
    	super(table, NavigatorColumnSet.getInstance());
	}
    
    @Override
    public void addColumn(TableColumn aColumn) {    	
    	NavigationView view = NavigationHandler.getInstance().getCurrentView();
    	int preferredWidth = -1;
    	if (view.isUseDefaultNavigatorColumns()) {
    		// Get column data
            Column column = NavigatorColumnSet.getInstance().getColumn(NavigatorColumnSet.getInstance().getColumnId(aColumn.getModelIndex()));

            updateColumnSettings(aColumn);
            
            updateColumnHeader(aColumn);
            
            preferredWidth = column.getWidth();
    	} else {
        	preferredWidth = view.getNavigatorTableMaxWidthForColumn(aColumn.getModelIndex());
        	aColumn.setResizable(false);
    	}
    	
    	if (preferredWidth != -1) {
    		aColumn.setPreferredWidth(preferredWidth);
    	}
    	super.addColumn(aColumn);    	
    }
    
    @Override
    public int getColumnAlignment(int column) {
    	NavigationView view = NavigationHandler.getInstance().getCurrentView();
    	if (view.isUseDefaultNavigatorColumns()) {
    		return super.getColumnAlignment(column);
    	} else {
    		return GuiUtils.getComponentOrientationAsSwingConstant();
    	}
    }
    
    @Override
    protected void reapplyFilter() {
    	ControllerProxy.getInstance().getNavigationController().updateTableContent(NavigationHandler.getInstance().getCurrentView().getTree());    	
    }
}
