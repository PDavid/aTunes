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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Future;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.model.AudioObjectProperty;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.StringUtils;

public abstract class AbstractCommonColumnModel extends DefaultTableColumnModel {

    private static final long serialVersionUID = -8202322203076350708L;
    /** The table. */
    private JTable table;
    /** Column set */
    private IColumnSet columnSet;
    /** The model. */
    private AbstractCommonTableModel model;
    /** The column being moved. */
    private int columnBeingMoved = -1;
    /** The column moved to. */
    private int columnMovedTo = -1;
    private ColumnMoveListener columnMoveListener;
    private ColumnModelListener columnModelListener;

    private ITaskService taskService;
    
    private ILookAndFeel lookAndFeel;
    
    /**
     * Instantiates a new column model
     * 
     * @param table
     * @param columnSet
     * @param taskService
     * @param lookAndFeel
     */
    public AbstractCommonColumnModel(JTable table, IColumnSet columnSet, ITaskService taskService, ILookAndFeel lookAndFeel) {
        this(table, taskService, lookAndFeel);
        this.columnSet = columnSet;
    }

    /**
     * Instantiates a new column model.
     * @param table
     * @param taskService
     * @param lookAndFeel
     */
    public AbstractCommonColumnModel(JTable table, ITaskService taskService, ILookAndFeel lookAndFeel) {
        super();
        this.taskService = taskService;
        this.lookAndFeel = lookAndFeel;
        this.table = table;
        this.model = (AbstractCommonTableModel) this.table.getModel();
    }
    
    protected ILookAndFeel getLookAndFeel() {
		return lookAndFeel;
	}

    /**
     * Return column for x position.
     * 
     * @param x
     *            the x
     * 
     * @return the column index at position
     */
    public final int getColumnIndexAtPosition(int x) {
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
    protected final void updateColumnWidth() {
        for (int i = 0; i < getColumnCount(); i++) {
            Class<? extends IColumn> col = getColumnId(i);
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
    private final void setWidthForColumn(Class<? extends IColumn> c, int width) {
        getColumn(c).setWidth(width);
    }

    /**
     * Arrange columns.
     * 
     * @param reapplyFilter
     *            the reapply filter
     */
    public final void arrangeColumns(boolean reapplyFilter) {
        setCurrentColumns();
        model.refresh(TableModelEvent.UPDATE);
        if (reapplyFilter) {
            reapplyFilter();
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
    public final IColumn getColumnObject(int column) {
        return getColumn(getColumnId(column));
    }

    /**
     * Returns class of column given by index
     * 
     * @param index
     * @return
     */
    private final Class<? extends IColumn> getColumnId(int index) {
        return columnSet.getColumnId(index);
    }

    /**
     * Returns a column object given its class
     * 
     * @param columnClass
     * @return
     */
    private final IColumn getColumn(Class<? extends IColumn> columnClass) {
        return columnSet.getColumn(columnClass);
    }

    /**
     * Returns alignment of current column
     * 
     * @param column
     * @return
     */
    public int getColumnAlignment(int column) {
        return getColumn(getColumnId(column)).getAlignment();
    }

    /**
     * Initializes columns
     */
    private void setCurrentColumns() {
        columnSet.setCurrentColumns();
    }

    private class ColumnMoveListener extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            if (columnBeingMoved != -1) {
                // Swap order in model

            	if (columnBeingMoved != columnMovedTo) {
            		// Column moved to right
            		if (columnBeingMoved < columnMovedTo) {
            			int columnDestinyOrder = getColumnObject(columnMovedTo).getOrder();
            			for (int i = columnBeingMoved + 1; i <= columnMovedTo; i++) {
            				int order = getColumnObject(i).getOrder();
            				getColumnObject(i).setOrder(order - 1);
            			}
            			getColumnObject(columnBeingMoved).setOrder(columnDestinyOrder);
            		} // Column moved to left
            		else if (columnBeingMoved > columnMovedTo) {
            			int columnDestinyOrder = getColumnObject(columnMovedTo).getOrder();
            			for (int i = columnBeingMoved - 1; i >= columnMovedTo; i--) {
            				int order = getColumnObject(i).getOrder();
            				getColumnObject(i).setOrder(order + 1);
            			}
            			getColumnObject(columnBeingMoved).setOrder(columnDestinyOrder);
            		}
            		
           			arrangeColumns(false);           			
            	}
            }
            columnBeingMoved = -1;
            columnMovedTo = -1;
        }
    };

    private class ColumnModelListener implements TableColumnModelListener {
    	
    	private Future<?> future;
    	
    	private void saveColumnSet() {
            if (future != null) {
            	future.cancel(false);
            }
            future = taskService.submitOnce("Save Column Model", 1, new Runnable() {
    			
    			@Override
    			public void run() {
    				// One second after last column width change save column set
    				// This is to avoid saving column set after each column change event
    		        columnSet.saveColumnSet();
    			}
    		});        
    	}
    	
        public void columnAdded(TableColumnModelEvent e) {
        	saveColumnSet();
        }

        public void columnMarginChanged(ChangeEvent e) {
            updateColumnWidth();
            saveColumnSet();
        }

        public void columnMoved(TableColumnModelEvent e) {
            if (columnBeingMoved == -1) {
                columnBeingMoved = e.getFromIndex();
            }
            columnMovedTo = e.getToIndex();
            saveColumnSet();
        }

        public void columnRemoved(TableColumnModelEvent e) {
        	saveColumnSet();
        }

        public void columnSelectionChanged(ListSelectionEvent e) {
            // Nothing to do
        }
    };

    private ColumnMoveListener getColumnMoveListener() {
        if (columnMoveListener == null) {
            columnMoveListener = new ColumnMoveListener();
        }
        return columnMoveListener;
    }

    private ColumnModelListener getColumnModelListener() {
        if (columnModelListener == null) {
            columnModelListener = new ColumnModelListener();
        }
        return columnModelListener;
    }

    public void enableColumnChange(boolean enable) {
        this.table.getTableHeader().setReorderingAllowed(enable);
        if (enable) {
            // Add listener for column size changes
            addColumnModelListener(getColumnModelListener());
            this.table.getTableHeader().addMouseListener(getColumnMoveListener());
        } else {
            removeColumnModelListener(getColumnModelListener());
            this.table.getTableHeader().removeMouseListener(getColumnMoveListener());
        }
    }

    /**
     * Apply filter
     */
    protected abstract void reapplyFilter();

    /**
     * Updates a column according to settings from column set
     * 
     * @param aColumn
     */
    protected void updateColumnSettings(TableColumn aColumn) {
        // Get column data
        IColumn column = getColumnObject(aColumn.getModelIndex());

        // Set width
        aColumn.setPreferredWidth(column.getWidth());
        aColumn.setWidth(column.getWidth());

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
     * @return the columnSet
     */
    public IColumnSet getColumnSet() {
        return columnSet;
    }

    /**
     * @param columnSet
     *            the columnSet to set
     */
    public void setColumnSet(IColumnSet columnSet) {
        this.columnSet = columnSet;
    }

    /**
     * Returns renderer code for given class
     * 
     * @param clazz
     * @return
     */
    public AbstractTableCellRendererCode getRendererCodeFor(Class<?> clazz) {
        if (clazz.equals(Integer.class)) {
            return new IntegerTableCellRendererCode(lookAndFeel);
        } else if (clazz.equals(ImageIcon.class)) {
            return new ImageIconTableCellRendererCode(this, lookAndFeel);
        } else if (clazz.equals(String.class)) {
            return new StringTableCellRendererCode(this, lookAndFeel);
        } else if (clazz.equals(TextAndIcon.class)) {
            return new TextAndIconTableCellRendererCode(this, lookAndFeel);
        } else if (clazz.equals(AudioObjectProperty.class)) {
            return new PropertyTableCellRendererCode(lookAndFeel);
        } else if (clazz.equals(IColorMutableImageIcon.class)) {
        	return new ColorMutableTableCellRendererCode(this, lookAndFeel);
        }
        throw new IllegalArgumentException(StringUtils.getString("No renderer found for class: ", clazz.getName()));
    }

    /**
     * @return the table
     */
    protected JTable getTable() {
        return table;
    }
}
