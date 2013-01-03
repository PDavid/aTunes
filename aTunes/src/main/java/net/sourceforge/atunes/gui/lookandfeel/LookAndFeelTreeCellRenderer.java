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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import net.sourceforge.atunes.model.ITreeCellRendererCode;

final class LookAndFeelTreeCellRenderer extends DefaultTreeCellRenderer {
    private final ITreeCellRendererCode code;
    /**
	 * 
	 */
    private static final long serialVersionUID = 5424315832943108932L;

    LookAndFeelTreeCellRenderer(ITreeCellRendererCode code) {
        this.code = code;
    }

    @Override
    public JComponent getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    	// Use custom JLabel component (if call super method, returned component can't be fully customized)
    	JComponent c = new JLabel(value.toString());
    	if (code != null) {
    		c = code.getComponent(c, tree, value,isSelected, expanded, leaf, row, hasFocus);
    	}
    	c.setOpaque(isSelected);
    	if (isSelected) {
    		c.setBackground(UIManager.getColor("Tree.selectionBackground"));
    		c.setForeground(UIManager.getColor("Tree.selectionForeground"));
    	}
        return c;
    }
}