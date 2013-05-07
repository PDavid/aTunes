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

package net.sourceforge.atunes.gui.lookandfeel.substance;

import javax.swing.JComponent;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.lookandfeel.ColumnSortIconGenerator;
import net.sourceforge.atunes.model.IColumnModel;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableHeaderCellRenderer;

/**
 * Table header cell renderer
 * 
 * @author alex
 * 
 */
public final class SubstanceLookAndFeelTableHeaderCellRenderer extends
		SubstanceDefaultTableHeaderCellRenderer {

	private static final long serialVersionUID = 1L;

	private IColumnModel model;

	private ColumnSortIconGenerator columnSortIconGenerator;

	/**
	 * @param columnSortIconGenerator
	 */
	public void setColumnSortIconGenerator(
			ColumnSortIconGenerator columnSortIconGenerator) {
		this.columnSortIconGenerator = columnSortIconGenerator;
	}

	/**
	 * @param model
	 */
	public void bindToModel(IColumnModel model) {
		this.model = model;
	}

	@Override
	public JComponent getTableCellRendererComponent(final JTable table,
			final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		JComponent c = (JComponent) super.getTableCellRendererComponent(table,
				value, isSelected, hasFocus, row, column);

		this.setIcon(columnSortIconGenerator.getIcon(this.model, column));
		return c;
	}
}