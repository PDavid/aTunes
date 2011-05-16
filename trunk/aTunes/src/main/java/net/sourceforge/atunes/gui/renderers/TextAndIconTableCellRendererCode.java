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

package net.sourceforge.atunes.gui.renderers;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.lookandfeel.AbstractTableCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.model.AbstractCommonColumnModel;
import net.sourceforge.atunes.kernel.modules.columns.TextAndIcon;

public class TextAndIconTableCellRendererCode extends AbstractTableCellRendererCode {

    private AbstractCommonColumnModel model;

    public TextAndIconTableCellRendererCode(AbstractCommonColumnModel model) {
        this.model = model;
    }

    @Override
    public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	int rowOver = 0;
        Point p = table.getMousePosition(true);
        if (p != null) {
        	rowOver = table.rowAtPoint(p);
        }
        boolean selected = isSelected || hasFocus || rowOver == row;

        Component c = superComponent;
        ((JLabel) c).setText(((TextAndIcon) value).getText());
        if (((TextAndIcon) value).getIcon() != null) {
        	((JLabel) c).setIcon(((TextAndIcon) value).getIcon().getIcon(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintForColorMutableIcon(c, selected)));
        } else {
        	((JLabel) c).setIcon(null);
        }        
        ((JLabel) c).setHorizontalTextPosition(((TextAndIcon) value).getHorizontalTextPosition());
        // Get alignment from model
        ((JLabel) c).setHorizontalAlignment(model.getColumnAlignment(column));
        return c;
    }

}
