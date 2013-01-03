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

package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.AbstractTreeCellDecorator;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.IYear;

/**
 * Tree cell decorator for years
 * @author alex
 *
 */
public class YearTreeCellDecorator extends AbstractTreeCellDecorator<JLabel, IYear> {

	private IIconFactory dateIcon;

	/**
	 * @param dateIcon
	 */
	public void setDateIcon(final IIconFactory dateIcon) {
		this.dateIcon = dateIcon;
	}

	@Override
	public Component decorateTreeCellComponent(final JLabel component, final IYear userObject, final boolean isSelected) {
		component.setIcon(dateIcon.getIcon(getLookAndFeel().getPaintForColorMutableIcon(component, isSelected)));
		return component;
	}

}
