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
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.IStateNavigation;

/**
 * Tree cell decorator for albums
 * 
 * @author alex
 * 
 */
public class AlbumTreeCellDecorator extends
		AbstractTreeCellDecorator<JLabel, IAlbum> {

	private IFavoritesHandler favoritesHandler;

	private IIconFactory albumFavoriteIcon;

	private IIconFactory albumSmallIcon;

	private IStateNavigation stateNavigation;

	/**
	 * @param stateNavigation
	 */
	public void setStateNavigation(final IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}

	/**
	 * @param albumFavoriteIcon
	 */
	public void setAlbumFavoriteIcon(final IIconFactory albumFavoriteIcon) {
		this.albumFavoriteIcon = albumFavoriteIcon;
	}

	/**
	 * @param albumSmallIcon
	 */
	public void setAlbumSmallIcon(final IIconFactory albumSmallIcon) {
		this.albumSmallIcon = albumSmallIcon;
	}

	@Override
	public Component decorateTreeCellComponent(final JLabel component,
			final IAlbum userObject, final boolean isSelected) {
		if (!this.stateNavigation.isShowFavoritesInNavigator()
				|| !this.favoritesHandler.isAlbumFavorite(userObject)) {
			component.setIcon(this.albumSmallIcon.getIcon(getLookAndFeel()
					.getPaintForColorMutableIcon(component, isSelected)));
		} else {
			component.setIcon(this.albumFavoriteIcon.getIcon(getLookAndFeel()
					.getPaintForColorMutableIcon(component, isSelected)));
		}
		return component;
	}

	/**
	 * @param favoritesHandler
	 */
	public void setFavoritesHandler(final IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}
}
