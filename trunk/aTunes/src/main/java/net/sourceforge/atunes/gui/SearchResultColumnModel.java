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

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ITaskService;

public class SearchResultColumnModel extends AbstractCommonColumnModel {

    private static final long serialVersionUID = -2211160302611944001L;

    /**
     * Instantiates a new play list column model.
     * 
     * @param table
     * @param columnSet
     * @param lookAndFeel 
     */
    public SearchResultColumnModel(JTable table, IColumnSet columnSet, ILookAndFeel lookAndFeel) {
        super(table, columnSet, Context.getBean(ITaskService.class), lookAndFeel);
        enableColumnChange(true);
    }

    @Override
    protected void reapplyFilter() {
        // Nothing to do    	
    }

    @Override
    public void addColumn(TableColumn aColumn) {
        super.addColumn(aColumn);
        updateColumnSettings(aColumn);
    }

}
