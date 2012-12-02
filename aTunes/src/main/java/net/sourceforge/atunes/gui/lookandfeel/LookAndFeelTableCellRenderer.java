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

package net.sourceforge.atunes.gui.lookandfeel;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import net.sourceforge.atunes.model.ITableCellRendererCode;

final class LookAndFeelTableCellRenderer extends DefaultTableCellRenderer {
	
    private final ITableCellRendererCode code;
    
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    LookAndFeelTableCellRenderer(ITableCellRendererCode<?, ?> code) {
        this.code = code;
    }

    @Override
    public JComponent getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	JComponent c = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    	if (code != null) {
    		c = code.getComponent(c, table, value, isSelected, hasFocus, row, column);
    	}
        c.setOpaque(isSelected);
        return c;
    }
}