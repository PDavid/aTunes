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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Sets favorite album
 * 
 * @author alex
 * 
 */
public class SetFavoriteAlbumFromNavigatorAction extends
	AbstractActionOverSelectedObjects<ILocalAudioObject> {

    private static final long serialVersionUID = 836910667009804037L;

    private IFavoritesHandler favoritesHandler;

    private INavigationHandler navigationHandler;

    private IStateNavigation stateNavigation;

    /**
     * @param stateNavigation
     */
    public void setStateNavigation(final IStateNavigation stateNavigation) {
	this.stateNavigation = stateNavigation;
    }

    /**
     * @param favoritesHandler
     */
    public void setFavoritesHandler(final IFavoritesHandler favoritesHandler) {
	this.favoritesHandler = favoritesHandler;
    }

    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(final INavigationHandler navigationHandler) {
	this.navigationHandler = navigationHandler;
    }

    /**
     * Default constructor
     */
    public SetFavoriteAlbumFromNavigatorAction() {
	super(I18nUtils.getString("SET_FAVORITE_ALBUM"));
    }

    @Override
    protected void executeAction(final List<ILocalAudioObject> objects) {
	favoritesHandler.toggleFavoriteAlbums(objects);
	navigationHandler.refreshNavigationTable();
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	if (selection.isEmpty()) {
	    return false;
	}

	if (stateNavigation.getViewMode() == ViewMode.FOLDER) {
	    return false;
	}

	for (ITreeNode node : selection) {
	    if (!(node.getUserObject() instanceof IAlbum)) {
		return false;
	    }
	}

	return true;
    }
}
