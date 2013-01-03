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

package net.sourceforge.atunes.gui.lookandfeel;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

import net.sourceforge.atunes.model.IListCellRendererCode;

final class LookAndFeelListCellRenderer extends DefaultListCellRenderer {
    private final IListCellRendererCode code;
    /**
	 * 
	 */
    private static final long serialVersionUID = 2572603555660744197L;

    LookAndFeelListCellRenderer(IListCellRendererCode code) {
        this.code = code;
    }

    @Override
    public JComponent getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    	JComponent c = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    	if (code != null) {
    		c = code.getComponent(c, list, value, index, isSelected, cellHasFocus);
    	}
        c.setOpaque(isSelected);
        return c;
    }
}