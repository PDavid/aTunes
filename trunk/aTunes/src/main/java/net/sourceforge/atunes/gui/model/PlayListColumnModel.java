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
package net.sourceforge.atunes.gui.model;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.columns.Column;
import net.sourceforge.atunes.kernel.modules.columns.PlayListColumnSet;

/**
 * The Class PlayListColumnModel.
 */
public final class PlayListColumnModel extends CommonColumnModel {

    private static final long serialVersionUID = -2211160302611944001L;

    /**
     * Instantiates a new play list column model.
     * 
     * @param playList
     *            the play list
     */
    public PlayListColumnModel(PlayListTable playList) {
        super(playList, PlayListColumnSet.getInstance());
        enableColumnChange(true);
    }

    @Override
    protected void reapplyFilter() {
    	ControllerProxy.getInstance().getPlayListFilterController().reapplyFilter();    	
    }    
    
    @Override
    public void addColumn(TableColumn aColumn) {
        super.addColumn(aColumn);

        // Get column data
        Column column = getColumnObject(aColumn.getModelIndex());

        // Set preferred width
        aColumn.setPreferredWidth(column.getWidth());

        // Set resizable
        aColumn.setResizable(column.isResizable());

        // If has cell editor, set cell editor
        TableCellEditor cellEditor = column.getCellEditor();
        if (cellEditor != null) {
            aColumn.setCellEditor(cellEditor);
        }

        // If has renderer, set cell renderer
        TableCellRenderer cellRenderer = column.getCellRenderer();
        if (cellRenderer != null) {
            aColumn.setCellRenderer(cellRenderer);
        }
    }

}
