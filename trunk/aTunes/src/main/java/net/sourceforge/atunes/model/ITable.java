/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.model;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * A graphical component representing a table
 * 
 * @author alex
 * 
 */
public interface ITable extends IComponent<JTable> {

	@Override
	public void addKeyListener(KeyListener keyListener);

	/**
	 * @return selection model
	 */
	public ListSelectionModel getSelectionModel();

	@Override
	public void addMouseListener(MouseListener listener);

	/**
	 * @return selected rows
	 */
	public int[] getSelectedRows();

	/**
	 * @return selected row
	 */
	public int getSelectedRow();

	/**
	 * @return number of rows
	 */
	public int getRowCount();

	/**
	 * @return visible area
	 */
	public Rectangle getVisibleRect();

	/**
	 * @param row
	 * @param column
	 * @param includeSpacing
	 * @return are of row
	 */
	public Rectangle getCellRect(int row, int column, boolean includeSpacing);

	/**
	 * Moves scroll to given area
	 * 
	 * @param visibleRect
	 */
	public void scrollRectToVisible(Rectangle visibleRect);

	/**
	 * @param model
	 */
	public void setModel(TableModel model);

	/**
	 * @return model
	 */
	public TableModel getModel();

	/**
	 * Sets transfer handler for drag and drop
	 * 
	 * @param transferHandler
	 */
	public void setTransferHandler(TransferHandler transferHandler);

	/**
	 * @param point
	 * @return row at given point
	 */
	public int rowAtPoint(Point point);

	/**
	 * @param columnModel
	 */
	public void setColumnModel(TableColumnModel columnModel);

	/**
	 * @return column model
	 */
	public TableColumnModel getColumnModel();

	/**
	 * Adds rows to selection
	 * 
	 * @param start
	 * @param end
	 */
	public void addRowSelectionInterval(int start, int end);

	/**
	 * Repaints table
	 */
	public void repaint();

	/**
	 * @param multipleIntervalSelection
	 */
	public void setSelectionMode(int multipleIntervalSelection);

	/**
	 * @param autoResize
	 */
	public void setAutoResizeMode(int autoResize);
}
