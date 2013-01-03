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

package net.sourceforge.atunes.gui.lookandfeel.system.macos;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Based in work:
 * 
 * http://explodingpixels.wordpress.com/2009/05/18/creating-a-better-jtable/
 * 
 * @author alex
 * 
 */
class StripedTableViewport extends JViewport {

    private static final long serialVersionUID = 7217178968532613985L;

    private final JTable fTable;

    /**
     * @param table
     */
    public StripedTableViewport(final JTable table) {
	fTable = table;
	setOpaque(false);
	initListeners();
    }

    private void initListeners() {
	// install a listener to cause the whole table to repaint when
	// a column is resized. we do this because the extended grid
	// lines may need to be repainted. this could be cleaned up,
	// but for now, it works fine.
	PropertyChangeListener listener = createTableColumnWidthListener();
	for (int i = 0; i < fTable.getColumnModel().getColumnCount(); i++) {
	    fTable.getColumnModel().getColumn(i)
		    .addPropertyChangeListener(listener);
	}

	addChangeListener(new ChangeListener() {

	    @Override
	    public void stateChanged(final ChangeEvent e) {
		repaint();
	    }
	});

	fTable.getSelectionModel().addListSelectionListener(
		new ListSelectionListener() {

		    @Override
		    public void valueChanged(final ListSelectionEvent e) {
			invalidate();
			repaint();
		    }
		});
    }

    private PropertyChangeListener createTableColumnWidthListener() {
	return new PropertyChangeListener() {
	    @Override
	    public void propertyChange(final PropertyChangeEvent evt) {
		repaint();
	    }
	};
    }

    @Override
    protected void paintComponent(final Graphics g) {
	paintStripedBackground(g);
	super.paintComponent(g);
    }

    private void paintStripedBackground(final Graphics g) {
	// get the row index at the top of the clip bounds (the first row
	// to paint).
	int rowAtPoint = fTable.rowAtPoint(g.getClipBounds().getLocation());
	// get the y coordinate of the first row to paint. if there are no
	// rows in the table, start painting at the top of the supplied
	// clipping bounds.
	int topY = rowAtPoint < 0 ? g.getClipBounds().y : fTable.getCellRect(
		rowAtPoint, 0, true).y;

	// create a counter variable to hold the current row. if there are no
	// rows in the table, start the counter at 0.
	int currentRow = rowAtPoint < 0 ? 0 : rowAtPoint;
	while (topY < g.getClipBounds().y + g.getClipBounds().height) {
	    int bottomY = topY + fTable.getRowHeight();
	    g.setColor(getRowColor(currentRow));
	    g.fillRect(g.getClipBounds().x, topY, g.getClipBounds().width,
		    bottomY);
	    topY = bottomY;
	    currentRow++;
	}
    }

    private Color getRowColor(final int row) {
	return row % 2 == 0 ? MacOSColors.EVEN_ROW_COLOR : getBackground();
    }
}
