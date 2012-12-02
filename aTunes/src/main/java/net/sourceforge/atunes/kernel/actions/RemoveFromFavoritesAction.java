/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.actions;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Removes selection from favorites
 * 
 * @author alex
 * 
 */
public class RemoveFromFavoritesAction extends CustomAbstractAction {

    private static final long serialVersionUID = -4288879781314486222L;

    private INavigationHandler navigationHandler;

    private IFavoritesHandler favoritesHandler;

    private INavigationView favoritesNavigationView;

    private ILocalAudioObjectFilter localAudioObjectFilter;

    /**
     * @param localAudioObjectFilter
     */
    public void setLocalAudioObjectFilter(
	    final ILocalAudioObjectFilter localAudioObjectFilter) {
	this.localAudioObjectFilter = localAudioObjectFilter;
    }

    /**
     * @param favoritesNavigationView
     */
    public void setFavoritesNavigationView(
	    final INavigationView favoritesNavigationView) {
	this.favoritesNavigationView = favoritesNavigationView;
    }

    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(final INavigationHandler navigationHandler) {
	this.navigationHandler = navigationHandler;
    }

    /**
     * @param favoritesHandler
     */
    public void setFavoritesHandler(final IFavoritesHandler favoritesHandler) {
	this.favoritesHandler = favoritesHandler;
    }

    /**
     * Default constructor
     */
    public RemoveFromFavoritesAction() {
	super(I18nUtils.getString("REMOVE_FROM_FAVORITES"));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void executeAction() {
	if (navigationHandler.isActionOverTree()
		&& navigationHandler.getCurrentView().equals(
			favoritesNavigationView)) {
	    List<ITreeNode> nodes = favoritesNavigationView.getTree()
		    .getSelectedNodes();
	    if (!CollectionUtils.isEmpty(nodes)) {
		List<ITreeObject<? extends IAudioObject>> objects = new ArrayList<ITreeObject<? extends IAudioObject>>();
		for (ITreeNode node : nodes) {
		    objects.add((ITreeObject<? extends IAudioObject>) node
			    .getUserObject());
		}
		favoritesHandler.removeFromFavorites(objects);
	    }
	} else {
	    List<IAudioObject> audioObjects = navigationHandler
		    .getSelectedAudioObjectsInNavigationTable();
	    if (!audioObjects.isEmpty()) {
		favoritesHandler.removeSongsFromFavorites(audioObjects);
	    }
	}
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	for (ITreeNode node : selection) {
	    // Only allow tree objects
	    if (!(node.getUserObject() instanceof ITreeObject<?>)) {
		return false;
	    }

	    // Only allow to remove album if does not belong to a favorite
	    // artist
	    if (node.getUserObject() instanceof IAlbum) {
		if (favoritesHandler.getFavoriteArtistsInfo().containsKey(
			((IAlbum) node.getUserObject()).getArtist().getName())) {
		    return false;
		}
	    }
	}
	return true;
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(
	    final List<IAudioObject> selection) {
	// Enabled if all selected items are favorite songs (not belong to
	// favorite artist nor album)
	return favoritesHandler
		.getFavoriteSongsInfo()
		.values()
		.containsAll(
			localAudioObjectFilter.getLocalAudioObjects(selection));
    }
}
