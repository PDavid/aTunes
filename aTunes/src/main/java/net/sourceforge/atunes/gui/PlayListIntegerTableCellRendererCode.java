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

package net.sourceforge.atunes.gui;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.model.IPlayListHandler;

/**
 * Renderer for integer columns in play list
 * @author alex
 *
 */
public final class PlayListIntegerTableCellRendererCode extends AbstractTableCellRendererCode<JLabel, Integer> {

	private IPlayListHandler playListHandler;

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	@Override
	public JLabel getComponent(final JLabel c, final JTable t, final Integer value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
		if (playListHandler.isCurrentVisibleRowPlaying(row)) {
			if (getLookAndFeel().getPlayListSelectedItemFont() != null) {
				c.setFont(getLookAndFeel().getPlayListSelectedItemFont());
			} else if (getLookAndFeel().getPlayListFont() != null) {
				c.setFont(getLookAndFeel().getPlayListFont());
			}
		}

		c.setText(value == null ? null : value.toString());
		c.setHorizontalAlignment(SwingConstants.CENTER);
		return c;
	}
}