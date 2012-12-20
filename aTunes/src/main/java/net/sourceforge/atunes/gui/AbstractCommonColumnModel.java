/*
 * aTunes 3.1.0
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
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.ITableCellRendererCode;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.PlaybackState;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Common column model for tables
 * 
 * @author alex
 * 
 */
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

	private ILookAndFeelManager lookAndFeelManager;

	private IBeanFactory beanFactory;

	protected final IBeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param table
	 */
	public void setTable(final JTable table) {
		this.table = table;
	}

	/**
	 * @param model
	 */
	public void setModel(final AbstractCommonTableModel model) {
		this.model = model;
	}

	protected final ILookAndFeel getLookAndFeel() {
		return this.lookAndFeelManager.getCurrentLookAndFeel();
	}

	/**
	 * Return column for x position.
	 * 
	 * @param x
	 *            the x
	 * 
	 * @return the column index at position
	 */
	public final int getColumnIndexAtPosition(final int x) {
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
			Class<? extends IColumn<?>> col = getColumnId(i);
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
	private final void setWidthForColumn(final Class<? extends IColumn<?>> c,
			final int width) {
		getColumn(c).setWidth(width);
	}

	/**
	 * Arrange columns.
	 * 
	 * @param reapplyFilter
	 *            the reapply filter
	 */
	public final void arrangeColumns(final boolean reapplyFilter) {
		setCurrentColumns();
		this.model.refresh(TableModelEvent.UPDATE);
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
	public final IColumn<?> getColumnObject(final int column) {
		return getColumn(getColumnId(column));
	}

	/**
	 * Returns class of column given by index
	 * 
	 * @param index
	 * @return
	 */
	private final Class<? extends IColumn<?>> getColumnId(final int index) {
		return this.columnSet.getColumnId(index);
	}

	/**
	 * Returns a column object given its class
	 * 
	 * @param columnClass
	 * @return
	 */
	private final IColumn<?> getColumn(
			final Class<? extends IColumn<?>> columnClass) {
		return this.columnSet.getColumn(columnClass);
	}

	/**
	 * Returns alignment of current column
	 * 
	 * @param column
	 * @return
	 */
	public int getColumnAlignment(final int column) {
		return getColumn(getColumnId(column)).getAlignment();
	}

	/**
	 * Initializes columns
	 */
	private void setCurrentColumns() {
		this.columnSet.setCurrentColumns();
	}

	private class ColumnMoveListener extends MouseAdapter {

		@Override
		public void mouseReleased(final MouseEvent e) {
			if (AbstractCommonColumnModel.this.columnBeingMoved != -1
					&& AbstractCommonColumnModel.this.columnBeingMoved != AbstractCommonColumnModel.this.columnMovedTo) {
				// Swap order in model
				// Column moved to right
				if (AbstractCommonColumnModel.this.columnBeingMoved < AbstractCommonColumnModel.this.columnMovedTo) {
					int columnDestinyOrder = getColumnObject(
							AbstractCommonColumnModel.this.columnMovedTo)
							.getOrder();
					for (int i = AbstractCommonColumnModel.this.columnBeingMoved + 1; i <= AbstractCommonColumnModel.this.columnMovedTo; i++) {
						int order = getColumnObject(i).getOrder();
						getColumnObject(i).setOrder(order - 1);
					}
					getColumnObject(
							AbstractCommonColumnModel.this.columnBeingMoved)
							.setOrder(columnDestinyOrder);
				} // Column moved to left
				else if (AbstractCommonColumnModel.this.columnBeingMoved > AbstractCommonColumnModel.this.columnMovedTo) {
					int columnDestinyOrder = getColumnObject(
							AbstractCommonColumnModel.this.columnMovedTo)
							.getOrder();
					for (int i = AbstractCommonColumnModel.this.columnBeingMoved - 1; i >= AbstractCommonColumnModel.this.columnMovedTo; i--) {
						int order = getColumnObject(i).getOrder();
						getColumnObject(i).setOrder(order + 1);
					}
					getColumnObject(
							AbstractCommonColumnModel.this.columnBeingMoved)
							.setOrder(columnDestinyOrder);
				}

				arrangeColumns(false);
			}
			AbstractCommonColumnModel.this.columnBeingMoved = -1;
			AbstractCommonColumnModel.this.columnMovedTo = -1;
		}
	};

	private class ColumnModelListener implements TableColumnModelListener {

		private Future<?> future;

		private void saveColumnSet() {
			if (this.future != null) {
				this.future.cancel(false);
			}
			this.future = AbstractCommonColumnModel.this.taskService
					.submitOnce("Save Column Model", 1, new Runnable() {

						@Override
						public void run() {
							// One second after last column width change save
							// column set
							// This is to avoid saving column set after each
							// column change event
							AbstractCommonColumnModel.this.columnSet
									.saveColumnSet();
						}
					});
		}

		@Override
		public void columnAdded(final TableColumnModelEvent e) {
			saveColumnSet();
		}

		@Override
		public void columnMarginChanged(final ChangeEvent e) {
			updateColumnWidth();
			saveColumnSet();
		}

		@Override
		public void columnMoved(final TableColumnModelEvent e) {
			if (AbstractCommonColumnModel.this.columnBeingMoved == -1) {
				AbstractCommonColumnModel.this.columnBeingMoved = e
						.getFromIndex();
			}
			AbstractCommonColumnModel.this.columnMovedTo = e.getToIndex();
			saveColumnSet();
		}

		@Override
		public void columnRemoved(final TableColumnModelEvent e) {
			saveColumnSet();
		}

		@Override
		public void columnSelectionChanged(final ListSelectionEvent e) {
			// Nothing to do
		}
	};

	private ColumnMoveListener getColumnMoveListener() {
		if (this.columnMoveListener == null) {
			this.columnMoveListener = new ColumnMoveListener();
		}
		return this.columnMoveListener;
	}

	private ColumnModelListener getColumnModelListener() {
		if (this.columnModelListener == null) {
			this.columnModelListener = new ColumnModelListener();
		}
		return this.columnModelListener;
	}

	/**
	 * Allows user to move columns
	 * 
	 * @param enable
	 */
	public void enableColumnChange(final boolean enable) {
		this.table.getTableHeader().setReorderingAllowed(enable);
		if (enable) {
			// Add listener for column size changes
			addColumnModelListener(getColumnModelListener());
			this.table.getTableHeader().addMouseListener(
					getColumnMoveListener());
		} else {
			removeColumnModelListener(getColumnModelListener());
			this.table.getTableHeader().removeMouseListener(
					getColumnMoveListener());
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
	protected void updateColumnSettings(final TableColumn aColumn) {
		// Get column data
		IColumn<?> column = getColumnObject(aColumn.getModelIndex());

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
		return this.columnSet;
	}

	/**
	 * @param columnSet
	 *            the columnSet to set
	 */
	public void setColumnSet(final IColumnSet columnSet) {
		this.columnSet = columnSet;
	}

	/**
	 * Returns renderer code for given class
	 * 
	 * @param clazz
	 * @return
	 */
	public ITableCellRendererCode<?, ?> getRendererCodeFor(final Class<?> clazz) {
		AbstractTableCellRendererCode<?, ?> renderer = null;
		if (clazz.equals(PlaybackState.class)) {
			renderer = this.beanFactory
					.getBean(PlaybackStateTableCellRendererCode.class);
		} else if (clazz.equals(Integer.class)) {
			renderer = this.beanFactory
					.getBean(IntegerTableCellRendererCode.class);
		} else if (clazz.equals(ImageIcon.class)) {
			renderer = this.beanFactory
					.getBean(ImageIconTableCellRendererCode.class);
		} else if (clazz.equals(String.class)) {
			renderer = this.beanFactory.getBean("stringTableCellRendererCode",
					StringTableCellRendererCode.class);
		} else if (clazz.equals(TextAndIcon.class)) {
			renderer = this.beanFactory
					.getBean(TextAndIconTableCellRendererCode.class);
		} else if (clazz.equals(AudioObjectProperty.class)) {
			renderer = this.beanFactory
					.getBean(PropertyTableCellRendererCode.class);
		} else if (clazz.equals(IColorMutableImageIcon.class)) {
			renderer = this.beanFactory
					.getBean(ColorMutableTableCellRendererCode.class);
		} else {
			throw new IllegalArgumentException(StringUtils.getString(
					"No renderer found for class: ", clazz.getName()));
		}
		return renderer;
	}

	/**
	 * @return the table
	 */
	protected JTable getTable() {
		return this.table;
	}
}
