/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.gui.model;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

/**
 * The Class NavigationTableColumnModel.
 */
public class NavigationTableColumnModel extends DefaultTableColumnModel {

    private static final long serialVersionUID = 1071222881574684439L;

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.table.DefaultTableColumnModel#addColumn(javax.swing.table
     * .TableColumn)
     */
    @Override
    public void addColumn(TableColumn aColumn) {
        super.addColumn(aColumn);

        int maxWidth = NavigationHandler.getInstance().getView(NavigationHandler.getInstance().getViewByName(ApplicationState.getInstance().getNavigationView()))
                .getNavigatorTableMaxWidthForColumn(aColumn.getModelIndex());

        if (maxWidth != -1) {
            aColumn.setMaxWidth(maxWidth);
        }

    }

}
