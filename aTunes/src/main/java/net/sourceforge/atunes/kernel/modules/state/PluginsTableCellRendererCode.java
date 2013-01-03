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

package net.sourceforge.atunes.kernel.modules.state;

import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.AbstractTableCellRendererCode;
import net.sourceforge.atunes.utils.ImageUtils;

import org.commonjukebox.plugins.model.PluginInfo;

/**
 * Renderer for plugins table
 * 
 * @author alex
 * 
 */
public class PluginsTableCellRendererCode extends
		AbstractTableCellRendererCode<JLabel, PluginInfo> {

	@Override
	public JLabel getComponent(final JLabel c, final JTable table,
			final PluginInfo value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		c.setText(value.getName());
		if (value.getIcon() != null) {
			c.setIcon(ImageUtils.scaleImageBicubic(value.getIcon(),
					PluginsPanel.CELL_HEIGHT - 5, PluginsPanel.CELL_HEIGHT - 5));
		} else {
			c.setIcon(null);
		}
		return c;
	}
}