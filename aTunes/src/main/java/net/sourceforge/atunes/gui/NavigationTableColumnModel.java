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

package net.sourceforge.atunes.gui;


import javax.swing.table.TableColumn;

import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITable;
import net.sourceforge.atunes.model.ITaskService;

public final class NavigationTableColumnModel extends AbstractCommonColumnModel {

    private static final long serialVersionUID = 1071222881574684439L;

    private IState state;
    
    private INavigationHandler navigationHandler;
    
    /**
     * @param table
     * @param state
     * @param navigationHandler
     * @param taskService
     * @param lookAndFeel
     */
    public NavigationTableColumnModel(ITable table, IState state, INavigationHandler navigationHandler, ITaskService taskService, ILookAndFeel lookAndFeel) {
        super(table.getSwingComponent(), taskService, lookAndFeel);
        this.state = state;
        this.navigationHandler = navigationHandler;
        enableColumnChange(true);
    }

    @Override
    public void addColumn(TableColumn aColumn) {
        updateColumnSettings(aColumn);
        super.addColumn(aColumn);
    }

    @Override
    protected void reapplyFilter() {
    	navigationHandler.updateViewTable();
    }

    @Override
    public AbstractTableCellRendererCode<?> getRendererCodeFor(Class<?> clazz) {
        AbstractTableCellRendererCode<?> renderer = super.getRendererCodeFor(clazz);
        return new NavigationTableCellRendererCode(renderer, state, getLookAndFeel(), navigationHandler);
    }

}
