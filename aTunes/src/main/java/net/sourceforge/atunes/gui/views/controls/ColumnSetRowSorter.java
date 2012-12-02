/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.gui.AbstractColumnSetTableModel;
import net.sourceforge.atunes.gui.AbstractCommonColumnModel;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumn;

/**
 * Calls sort on column model when user clicks a column header
 * @author alex
 *
 */
public final class ColumnSetRowSorter {

    private JTable table;
    private AbstractColumnSetTableModel model;
    private AbstractCommonColumnModel columnModel;

    /**
     * @param table
     * @param model
     * @param columnModel
     */
    public ColumnSetRowSorter(JTable table, AbstractColumnSetTableModel model, AbstractCommonColumnModel columnModel) {
        this.table = table;
        this.model = model;
        this.columnModel = columnModel;
        setListeners();
    }

    private void setListeners() {
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (GuiUtils.isPrimaryMouseButton(e)) {
                    int columnClickedIndex = table.getTableHeader().getColumnModel().getColumnIndexAtX(e.getX());
                    if (columnClickedIndex != -1) {
                        // Get column
                        IColumn<?> columnClicked = ColumnSetRowSorter.this.columnModel.getColumnObject(columnClickedIndex);
                        IColumn<?> lastColumnSorted = getColumnSorted();
                        if (lastColumnSorted != null && !lastColumnSorted.equals(columnClicked)) {
                            lastColumnSorted.setColumnSort(null);
                        }
                        if (columnClicked.isSortable()) {
                            sort(columnClicked.getComparator(true));
                        }
                    }
                }
            }
        });
    }

    /**
     * Returns the column sorted
     * 
     * @return
     */
    private IColumn<?> getColumnSorted() {
        for (int i = 0; i < this.columnModel.getColumnCount(); i++) {
            if (this.columnModel.getColumnObject(i).getColumnSort() != null) {
                return this.columnModel.getColumnObject(i);
            }
        }
        return null;
    }

    /**
     * Method to sort a column set. It must sort the underlying data
     * 
     * @param comparator
     */
    protected void sort(Comparator<IAudioObject> comparator) {

        // Sort model
        this.model.sort(comparator);

        // Refresh model
        model.refresh(TableModelEvent.UPDATE);
    }
}
