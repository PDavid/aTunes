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

import java.awt.Color;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.ColumnSort;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IColumnModel;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * Generates icon for column sort
 * 
 * @author alex
 * 
 */
public class ColumnSortIconGenerator {

	private IIconFactory arrowUpImageIcon;

	private IIconFactory arrowDownImageIcon;

	private ILookAndFeelManager lookAndFeelManager;

	/**
	 * @param arrowDownImageIcon
	 */
	public void setArrowDownImageIcon(IIconFactory arrowDownImageIcon) {
		this.arrowDownImageIcon = arrowDownImageIcon;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param arrowUpImageIcon
	 */
	public void setArrowUpImageIcon(IIconFactory arrowUpImageIcon) {
		this.arrowUpImageIcon = arrowUpImageIcon;
	}

	/**
	 * @param model
	 * @param column
	 * @return icon
	 */
	public ImageIcon getIcon(final IColumnModel model, final int column) {
		IColumnSet cs = model.getColumnSet();
		if (cs != null) {
			Class<? extends IColumn<?>> colId = cs.getColumnId(column);
			if (colId != null) {
				IColumn<?> col = cs.getColumn(colId);
				if (col != null) {
					return getIconForSort(col);
				}
			}
		}
		return null;
	}

	private ImageIcon getIconForSort(IColumn<?> col) {
		ColumnSort sort = col.getColumnSort();
		if (sort != null) {
			Color color = lookAndFeelManager.getCurrentLookAndFeel()
					.getPaintForSpecialControls();
			if (sort == ColumnSort.ASCENDING) {
				return arrowUpImageIcon.getColorMutableIcon().getIcon(color);
			} else {
				return arrowDownImageIcon.getColorMutableIcon().getIcon(color);
			}
		}
		return null;
	}
}
