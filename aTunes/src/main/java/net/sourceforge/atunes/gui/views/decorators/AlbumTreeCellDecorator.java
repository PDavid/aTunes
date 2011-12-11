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

package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.AbstractTreeCellDecorator;
import net.sourceforge.atunes.model.CachedIconFactory;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IFavoritesHandler;

public class AlbumTreeCellDecorator extends AbstractTreeCellDecorator<JLabel, IAlbum> {

	private IFavoritesHandler favoritesHandler;
	
	private CachedIconFactory albumFavoriteIcon;
	
	private CachedIconFactory albumSmallIcon;

	/**
	 * @param albumFavoriteIcon
	 */
	public void setAlbumFavoriteIcon(CachedIconFactory albumFavoriteIcon) {
		this.albumFavoriteIcon = albumFavoriteIcon;
	}
	
	/**
	 * @param albumSmallIcon
	 */
	public void setAlbumSmallIcon(CachedIconFactory albumSmallIcon) {
		this.albumSmallIcon = albumSmallIcon;
	}
	
    @Override
    public Component decorateTreeCellComponent(JLabel component, IAlbum userObject, boolean isSelected) {
    	if (!getState().isShowFavoritesInNavigator() || !favoritesHandler.getFavoriteAlbumsInfo().containsValue(userObject)) {
    		component.setIcon(albumSmallIcon.getIcon(getLookAndFeel().getPaintForColorMutableIcon(component, isSelected)));
    	} else {
    		component.setIcon(albumFavoriteIcon.getIcon(getLookAndFeel().getPaintForColorMutableIcon(component, isSelected)));
    	}
        return component;
    }
    
    public void setFavoritesHandler(IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}

}
