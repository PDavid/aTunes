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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.gui.views.controls.playList.Column;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.columns.PlayListColumns;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListTableModel;

/**
 * The Class PlayListColumnModel.
 */
public final class PlayListColumnModel extends DefaultTableColumnModel {

    private static final long serialVersionUID = -2211160302611944001L;

    /** The play list. */
    PlayListTable playList;

    /** The model. */
    PlayListTableModel model;

    /** The column being moved. */
    int columnBeingMoved = -1;

    /** The column moved to. */
    int columnMovedTo = -1;

    /**
     * Instantiates a new play list column model.
     * 
     * @param playList
     *            the play list
     */
    public PlayListColumnModel(PlayListTable playList) {
        super();
        this.playList = playList;
        this.model = (PlayListTableModel) this.playList.getModel();

        // Add listener for column size changes
        addColumnModelListener(new TableColumnModelListener() {
            public void columnAdded(TableColumnModelEvent e) {
                // Nothing to do
            }

            public void columnMarginChanged(ChangeEvent e) {
                updateColumnWidth();
            }

            public void columnMoved(TableColumnModelEvent e) {
                if (columnBeingMoved == -1) {
                    columnBeingMoved = e.getFromIndex();
                }
                columnMovedTo = e.getToIndex();
            }

            public void columnRemoved(TableColumnModelEvent e) {
                // Nothing to do
            }

            public void columnSelectionChanged(ListSelectionEvent e) {
                // Nothing to do
            }
        });

        this.playList.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (columnBeingMoved != -1) {
                    // Test if first column has been moved, and undo movement
                    if (columnBeingMoved == 0 || columnMovedTo == 0) {
                        // Swap columns
                        PlayListColumnModel.this.playList.moveColumn(columnMovedTo, columnBeingMoved);
                    } else {
                        // Swap order in model

                        // Column moved to right
                        if (columnBeingMoved < columnMovedTo) {
                            int columnDestinyOrder = getColumnObject(columnMovedTo).getOrder();
                            for (int i = columnBeingMoved + 1; i <= columnMovedTo; i++) {
                                int order = getColumnObject(i).getOrder();
                                getColumnObject(i).setOrder(order - 1);
                            }
                            getColumnObject(columnBeingMoved).setOrder(columnDestinyOrder);
                        }
                        // Column moved to left
                        else if (columnBeingMoved > columnMovedTo) {
                            int columnDestinyOrder = getColumnObject(columnMovedTo).getOrder();
                            for (int i = columnBeingMoved - 1; i >= columnMovedTo; i--) {
                                int order = getColumnObject(i).getOrder();
                                getColumnObject(i).setOrder(order + 1);
                            }
                            getColumnObject(columnBeingMoved).setOrder(columnDestinyOrder);
                        }
                    }
                    arrangeColumns(false);
                }
                columnBeingMoved = -1;
                columnMovedTo = -1;
            }
        });
    }

    // When a new column is added, set properties based on model
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

    /**
     * Return column for x position.
     * 
     * @param x
     *            the x
     * 
     * @return the column index at position
     */
    public int getColumnIndexAtPosition(int x) {
        int computedX = x;
        if (computedX < 0) {
            return -1;
        }

        for (int column = 0; column < getColumnCount(); column++) {
            computedX = computedX - getColumn(column).getPreferredWidth();
            if (computedX < 0) {
                return column;
            }
        }

        return -1;
    }

    /**
     * Updates columns width.
     */
    protected void updateColumnWidth() {
        for (int i = 0; i < getColumnCount(); i++) {
            Class<? extends Column> col = PlayListColumns.getColumnId(i);
            int width = getColumn(i).getPreferredWidth();
            setWidthForColumn(col, width);
        }
    }

    /**
     * Sets width for a column.
     * 
     * @param c
     *            the c
     * @param width
     *            the width
     */
    private static void setWidthForColumn(Class<? extends Column> c, int width) {
        PlayListColumns.getColumn(c).setWidth(width);
    }

    /**
     * Arrange columns.
     * 
     * @param reapplyFilter
     *            the reapply filter
     */
    public void arrangeColumns(boolean reapplyFilter) {
        PlayListColumns.setCurrentColumns();
        ((PlayListTableModel) playList.getModel()).refresh();
        if (reapplyFilter) {
            ControllerProxy.getInstance().getPlayListFilterController().reapplyFilter();
        }
    }

    /**
     * Return Column object for a given column number.
     * 
     * @param column
     *            the column
     * 
     * @return the column
     */
    public Column getColumnObject(int column) {
        return PlayListColumns.getColumn(PlayListColumns.getColumnId(column));
    }
}
