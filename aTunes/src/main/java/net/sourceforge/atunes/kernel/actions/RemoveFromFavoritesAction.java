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

package net.sourceforge.atunes.kernel.actions;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.kernel.modules.navigator.FavoritesNavigationView;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.LocalAudioObjectFilter;
import net.sourceforge.atunes.utils.I18nUtils;

public class RemoveFromFavoritesAction extends CustomAbstractAction {

    private static final long serialVersionUID = -4288879781314486222L;

    private INavigationHandler navigationHandler;
    
    private IFavoritesHandler favoritesHandler;
    
    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    /**
     * @param favoritesHandler
     */
    public void setFavoritesHandler(IFavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}
    
    public RemoveFromFavoritesAction() {
        super(I18nUtils.getString("REMOVE_FROM_FAVORITES"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("REMOVE_FROM_FAVORITES"));
    }
    
    @SuppressWarnings("unchecked")
	@Override
    protected void executeAction() {
        if (navigationHandler.isActionOverTree() && navigationHandler.getCurrentView().equals(navigationHandler.getView(FavoritesNavigationView.class))) {
            TreePath[] paths = navigationHandler.getView(FavoritesNavigationView.class).getTree().getSelectionPaths();
            if (paths != null) {
                List<ITreeObject<? extends IAudioObject>> objects = new ArrayList<ITreeObject<? extends IAudioObject>>();
                for (TreePath element : paths) {
                    objects.add((ITreeObject<? extends IAudioObject>) ((DefaultMutableTreeNode) element.getLastPathComponent()).getUserObject());
                }
                favoritesHandler.removeFromFavorites(objects);
            }
        } else {
        	List<IAudioObject> audioObjects = navigationHandler.getSelectedAudioObjectsInNavigationTable();
            if (!audioObjects.isEmpty()) {
            	favoritesHandler.removeSongsFromFavorites(audioObjects);
            }
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        for (DefaultMutableTreeNode node : selection) {
            // Only allow to remove album if does not belong to a favorite artist
            if (node.getUserObject() instanceof Album) {
                Object[] objs = node.getUserObjectPath();
                for (Object element : objs) {
                    if (element instanceof Artist) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        // Enabled if all selected items are favorite songs (not belong to favorite artist nor album)
        return favoritesHandler.getFavoriteSongsInfo().values().containsAll(
        		new LocalAudioObjectFilter().getLocalAudioObjects(selection));
    }
}
