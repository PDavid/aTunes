/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.lookandfeel.system.macos;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JList;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class StripedListViewport extends JViewport {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7217178968532613985L;
	
	private final JList list;

    public StripedListViewport(JList list) {
        this.list = list;
        setOpaque(false);
        initListeners();
    }
    
    private void initListeners() {
    	list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				repaint();
			}
		});
    	
    	addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				repaint();
			}
		});
    }

    @Override
    protected void paintComponent(Graphics g) {
        paintStripedBackground(g);
        super.paintComponent(g);
    }

    private void paintStripedBackground(Graphics g) {
        // get the row index at the top of the clip bounds (the first row
        // to paint).
        int rowAtPoint = list.locationToIndex(g.getClipBounds().getLocation());
        // get the y coordinate of the first row to paint. if there are no
        // rows in the tree, start painting at the top of the supplied
        // clipping bounds.
        int topY = rowAtPoint < 0
                ? g.getClipBounds().y : list.getCellBounds(rowAtPoint, rowAtPoint).y;

        // create a counter variable to hold the current row. if there are no
        // rows in the tree, start the counter at 0.
        int currentRow = rowAtPoint < 0 ? 0 : rowAtPoint;
        while (topY < g.getClipBounds().y + g.getClipBounds().height) {
            int bottomY = topY + list.getCellBounds(rowAtPoint, rowAtPoint).height;
            g.setColor(getRowColor(currentRow));
            g.fillRect(g.getClipBounds().x, topY, g.getClipBounds().width, bottomY);
            topY = bottomY;
            currentRow ++;
        }
    }

    private Color getRowColor(int row) {
        return row % 2 == 0 ? MacOSColors.EVEN_ROW_COLOR : getBackground();
    }
}
