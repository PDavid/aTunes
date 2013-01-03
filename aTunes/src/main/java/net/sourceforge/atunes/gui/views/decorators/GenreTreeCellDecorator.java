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
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.IIconFactory;

/**
 * Tree cell decorator for genres
 * @author alex
 *
 */
public class GenreTreeCellDecorator extends AbstractTreeCellDecorator<JLabel, IGenre> {

	private IIconFactory genreIcon;

	/**
	 * @param genreIcon
	 */
	public void setGenreIcon(final IIconFactory genreIcon) {
		this.genreIcon = genreIcon;
	}

	@Override
	public Component decorateTreeCellComponent(final JLabel component, final IGenre userObject, final boolean isSelected) {
		component.setIcon(genreIcon.getIcon(getLookAndFeel().getPaintForColorMutableIcon(component, isSelected)));
		return component;
	}

}
