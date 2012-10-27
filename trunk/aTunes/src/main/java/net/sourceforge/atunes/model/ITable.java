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

    public ListSelectionModel getSelectionModel();

    @Override
    public void addMouseListener(MouseListener listener);

    public int[] getSelectedRows();

    public int getSelectedRow();

    public int getRowCount();

    public Rectangle getVisibleRect();

    public Rectangle getCellRect(int row, int column, boolean includeSpacing);

    public void scrollRectToVisible(Rectangle visibleRect);

    public void setModel(TableModel model);

    public TableModel getModel();

    public void setTransferHandler(TransferHandler transferHandler);

    public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
	    boolean extend);

    public int rowAtPoint(Point point);

    public void setColumnModel(TableColumnModel columnModel);

    public TableColumnModel getColumnModel();

    public void addRowSelectionInterval(int start, int end);

    public void repaint();

    public void setSelectionMode(int multipleIntervalSelection);

    public void setAutoResizeMode(int autoResize);
}
