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

import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.model.IControlsBuilder;

/**
 * Cell renderer to apply component orientation
 * 
 * @author alex
 * 
 */
public class ComponentOrientationTableCellRendererCode extends
		AbstractTableCellRendererCode<JLabel, Object> {

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	@Override
	public JLabel getComponent(final JLabel superComponent, final JTable t,
			final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		this.controlsBuilder.applyComponentOrientation(superComponent);
		return superComponent;
	}
}