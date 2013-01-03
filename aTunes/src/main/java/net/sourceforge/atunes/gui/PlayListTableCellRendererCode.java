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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.kernel.modules.columns.PlayListColumnSet;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.ITableCellRendererCode;

/**
 * Renderer for play list table, includes highlight of incomplete tags
 * 
 * @author alex
 * 
 */
public class PlayListTableCellRendererCode extends
		AbstractTableCellRendererCode<JComponent, Object> {

	private ITableCellRendererCode<JComponent, Object> renderer;

	private IPlayListHandler playListHandler;

	private PlayListColumnSet playListColumnSet;

	/**
	 * @param playListColumnSet
	 */
	public void setPlayListColumnSet(final PlayListColumnSet playListColumnSet) {
		this.playListColumnSet = playListColumnSet;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * @param renderer
	 */
	public void setRenderer(
			final ITableCellRendererCode<JComponent, Object> renderer) {
		this.renderer = renderer;
	}

	@Override
	public JComponent getComponent(final JComponent superComponent,
			final JTable t, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		// Get result from super renderer
		JComponent c = this.renderer.getComponent(superComponent, t, value,
				isSelected, hasFocus, row, column);

		// Apply component orientation
		((JLabel) c).setHorizontalAlignment(this.playListColumnSet.getColumn(
				this.playListColumnSet.getColumnId(column)).getAlignment());

		// Apply font to current row
		if (this.playListHandler.isCurrentVisibleRowPlaying(row)) {
			if (getLookAndFeel().getPlayListSelectedItemFont() != null) {
				c.setFont(getLookAndFeel().getPlayListSelectedItemFont());
			} else if (getLookAndFeel().getPlayListFont() != null) {
				c.setFont(getLookAndFeel().getPlayListFont());
			}
		}
		return c;
	}
}