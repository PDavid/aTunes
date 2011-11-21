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

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTableCellRendererCode;
import net.sourceforge.atunes.model.AudioObjectProperty;
import net.sourceforge.atunes.model.CachedIconFactory;
import net.sourceforge.atunes.model.ILookAndFeel;

public class PropertyTableCellRendererCode extends AbstractTableCellRendererCode {

    public PropertyTableCellRendererCode(ILookAndFeel lookAndFeel) {
		super(lookAndFeel);
	}

	@Override
    public JComponent getComponent(JComponent comp, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        ImageIcon icon = null;
        AudioObjectProperty val = (AudioObjectProperty) value;
        if (val == AudioObjectProperty.FAVORITE) {
            icon = Context.getBean("favoriteIcon", CachedIconFactory.class).getIcon(lookAndFeel.getPaintForColorMutableIcon(comp, isSelected));
        } else if (val == AudioObjectProperty.NOT_LISTENED_ENTRY) {
            icon = Context.getBean("newIcon", CachedIconFactory.class).getIcon(lookAndFeel.getPaintForColorMutableIcon(comp, isSelected));
        } else if (val == AudioObjectProperty.DOWNLOADED_ENTRY) {
            icon = Context.getBean("downloadIcon", CachedIconFactory.class).getIcon(lookAndFeel.getPaintForColorMutableIcon(comp, isSelected));
        }
        ((JLabel) comp).setIcon(icon);
        ((JLabel) comp).setText(null);
        return comp;
    }

}
