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

package net.sourceforge.atunes.gui;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.PlaybackState;

/**
 * Renderer for playback state column
 * @author alex
 *
 */
public final class PlaybackStateTableCellRendererCode extends AbstractTableCellRendererCode<JLabel, PlaybackState> {

	private IIconFactory playListPlayStateIcon;

	private IIconFactory playListStopStateIcon;

	private IIconFactory playListPauseStateIcon;

	/**
	 * @param playListPauseStateIcon
	 */
	public void setPlayListPauseStateIcon(final IIconFactory playListPauseStateIcon) {
		this.playListPauseStateIcon = playListPauseStateIcon;
	}

	/**
	 * @param playListPlayStateIcon
	 */
	public void setPlayListPlayStateIcon(final IIconFactory playListPlayStateIcon) {
		this.playListPlayStateIcon = playListPlayStateIcon;
	}

	/**
	 * @param playListStopStateIcon
	 */
	public void setPlayListStopStateIcon(final IIconFactory playListStopStateIcon) {
		this.playListStopStateIcon = playListStopStateIcon;
	}

	@Override
	public JLabel getComponent(final JLabel c, final JTable t, final PlaybackState value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
		c.setText(null);
		c.setIcon(getPlayStateIcon(getLookAndFeel().getPaintForColorMutableIcon(c, isSelected), value));
		return c;
	}

	/**
	 * @param color
	 * @param state
	 * @return
	 */
	private ImageIcon getPlayStateIcon(final Color color, final PlaybackState value) {
		if (value != null) {
			switch (value) {
			case PLAYING:
				return playListPlayStateIcon.getIcon(color);
			case RESUMING:
				return playListPlayStateIcon.getIcon(color);
			case PAUSED:
				return playListPauseStateIcon.getIcon(color);
			default:
				return playListStopStateIcon.getIcon(color);
			}
		}
		return null;
	}
}