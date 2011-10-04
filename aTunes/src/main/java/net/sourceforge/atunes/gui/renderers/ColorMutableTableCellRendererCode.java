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

package net.sourceforge.atunes.gui.renderers;

import java.awt.Paint;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.lookandfeel.AbstractTableCellRendererCode;
import net.sourceforge.atunes.gui.model.AbstractCommonColumnModel;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILookAndFeel;

public class ColorMutableTableCellRendererCode extends AbstractTableCellRendererCode {

    private AbstractCommonColumnModel model;

    public ColorMutableTableCellRendererCode(AbstractCommonColumnModel model, ILookAndFeel lookAndFeel) {
    	super(lookAndFeel);
        this.model = model;
    }

    @Override
    public JComponent getComponent(JComponent c, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        ((JLabel) c).setText(null);
        Paint color = lookAndFeel.getPaintForColorMutableIcon(c, isSelected);
        ((JLabel) c).setIcon(((IColorMutableImageIcon)value).getIcon(color));

        // Get alignment from model
        ((JLabel) c).setHorizontalAlignment(model.getColumnAlignment(column));
        return c;
    }

}
