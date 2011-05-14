/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.images.FavoriteImageIcon;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.images.NewImageIcon;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTableCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;

public class PropertyTableCellRendererCode extends AbstractTableCellRendererCode {

    public PropertyTableCellRendererCode() {
    }

    @Override
    public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = superComponent;
        ImageIcon icon = null;
        Property val = (Property) value;
        if (val == Property.FAVORITE) {
            icon = FavoriteImageIcon.getIcon(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintForColorMutableIcon(comp));
        } else if (val == Property.NOT_LISTENED_ENTRY) {
            icon = NewImageIcon.getIcon(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintForColorMutableIcon(comp));
        } else if (val == Property.DOWNLOADED_ENTRY) {
            icon = Images.getImage(Images.DOWNLOAD_PODCAST);
        }
        ((JLabel) comp).setIcon(icon);
        ((JLabel) comp).setText(null);
        return comp;
    }

}
