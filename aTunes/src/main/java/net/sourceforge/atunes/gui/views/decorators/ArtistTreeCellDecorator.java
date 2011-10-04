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

import net.sourceforge.atunes.gui.images.ArtistFavoriteImageIcon;
import net.sourceforge.atunes.gui.images.ArtistImageIcon;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTreeCellDecorator;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IState;

public class ArtistTreeCellDecorator extends AbstractTreeCellDecorator {

	private IFavoritesHandler favoritesHandler;

    @Override
    public Component decorateTreeCellComponent(IState state, Component component, Object userObject, boolean isSelected, ILookAndFeel lookAndFeel) {
        if (userObject instanceof Artist) {
            if (!state.isShowFavoritesInNavigator() || !favoritesHandler.getFavoriteArtistsInfo().containsValue(userObject)) {
          		((JLabel) component).setIcon(ArtistImageIcon.getIcon(lookAndFeel.getPaintForColorMutableIcon(component, isSelected), lookAndFeel));
            } else {
                ((JLabel) component).setIcon(ArtistFavoriteImageIcon.getIcon(lookAndFeel.getPaintForColorMutableIcon(component, isSelected), lookAndFeel));
            }
        }
        return component;
    }
    
    public void setFavoritesHandler(IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}

}
