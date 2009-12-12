package net.sourceforge.atunes.gui.model;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

import net.sourceforge.atunes.kernel.modules.columns.Column;
import net.sourceforge.atunes.kernel.modules.columns.ColumnSet;

public abstract class CommonColumnModel extends DefaultTableColumnModel {

	private static final long serialVersionUID = -8202322203076350708L;
	
    /** The table. */
    JTable table;

    /** Column set */
    ColumnSet columnSet;
    
    /** The model. */
    CommonTableModel model;

    /** The column being moved. */
    int columnBeingMoved = -1;

    /** The column moved to. */
    int columnMovedTo = -1;
    
    private ColumnMoveListener columnMoveListener;
    
    private ColumnModelListener columnModelListener;

    /**
     * Instantiates a new column model.
     * 
     * @param table
     *            the play list
     */
    public CommonColumnModel(JTable table, ColumnSet columnSet) {
        super();
        this.table = table;
        this.columnSet = columnSet;
        this.model = (CommonTableModel) this.table.getModel();
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
            Class<? extends Column> col = getColumnId(i);
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
    private final void setWidthForColumn(Class<? extends Column> c, int width) {
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
    public final Column getColumnObject(int column) {
        return getColumn(getColumnId(column));
    }

    /**
     * Returns class of column given by index
     * @param index
     * @return
     */
    private final Class<? extends Column> getColumnId(int index) {
    	return columnSet.getColumnId(index);
    }    
    
    /**
     * Returns a column object given its class
     * @param columnClass
     * @return
     */
    private final Column getColumn(Class<? extends Column> columnClass) {
    	return columnSet.getColumn(columnClass);
    }
    
    /**
     * Returns alignment of current column
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
    			arrangeColumns(false);
    		}
    		columnBeingMoved = -1;
    		columnMovedTo = -1;
    	}
    };
    
    private class ColumnModelListener implements TableColumnModelListener {
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
     * @param aColumn
     */
    protected void updateColumnSettings(TableColumn aColumn) {
        // Get column data
        Column column = getColumnObject(aColumn.getModelIndex());

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
	public ColumnSet getColumnSet() {
		return columnSet;
	}

    
}
