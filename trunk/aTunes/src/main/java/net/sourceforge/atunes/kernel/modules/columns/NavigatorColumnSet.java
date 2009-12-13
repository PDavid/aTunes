/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.gui.model.NavigationTableColumnModel;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

/**
 * This class defines all columns than can be viewed in Navigator
 * 
 * @author fleax
 */
public class NavigatorColumnSet extends ColumnSet {

    /**
     * Singleton instance
     */
    private static NavigatorColumnSet instance;

    /**
     * Private constructor
     */
    private NavigatorColumnSet() {
        super(false);
    }

    /**
     * Returns singleton instance
     * 
     * @return
     */
    public static NavigatorColumnSet getInstance() {
        if (instance == null) {
            instance = new NavigatorColumnSet();
        }
        return instance;
    }

    @Override
    protected Map<String, ColumnBean> getColumnsConfiguration() {
        return ApplicationState.getInstance().getNavigatorColumns();
    }

    @Override
    protected void setColumnsConfiguration(Map<String, ColumnBean> columnsConfiguration) {
        ApplicationState.getInstance().setNavigatorColumns(columnsConfiguration);
    }

    @Override
    protected void refreshColumns() {
        ((NavigationTableColumnModel) GuiHandler.getInstance().getNavigationTablePanel().getNavigationTable().getColumnModel()).arrangeColumns(false);
    }
}
