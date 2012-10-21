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

package net.sourceforge.atunes.kernel.modules.context;

import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.AbstractTableCellRendererCode;
import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.ITrackInfo;

/**
 * Cell renderer for ITrackInfo
 * @author alex
 *
 */
public class TrackInfoTableCellRendererCode extends AbstractTableCellRendererCode<JLabel, ITrackInfo> {

	@Override
	public JLabel getComponent(final JLabel superComponent, final JTable t, final ITrackInfo value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
		superComponent.setText(value.getTitle());
		GuiUtils.applyComponentOrientation(superComponent);
		if (!value.isAvailable()) {
			superComponent.setForeground(ColorDefinitions.GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR);
		}
		return superComponent;
	}
}