/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.plugins.favorites;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.actions.Action;
import net.sourceforge.atunes.kernel.modules.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

public class RemoveFromFavoritesAction extends Action {

    private static final long serialVersionUID = -4288879781314486222L;

    public RemoveFromFavoritesAction() {
        super(I18nUtils.getString("REMOVE_FROM_FAVORITES"), ImageLoader.getImage(ImageLoader.DELETE_TAG));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("REMOVE_FROM_FAVORITES"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ControllerProxy.getInstance().getNavigationController().getPopupMenuCaller() == NavigationHandler.getInstance().getView(FavoritesNavigationView.class).getTree()) {
            TreePath[] paths = NavigationHandler.getInstance().getView(FavoritesNavigationView.class).getTree().getSelectionPaths();
            if (paths != null) {
                List<TreeObject> objects = new ArrayList<TreeObject>();
                for (TreePath element : paths) {
                    objects.add((TreeObject) ((DefaultMutableTreeNode) element.getLastPathComponent()).getUserObject());
                }
                FavoritesHandler.getInstance().removeFromFavorites(objects);
            }
        } else {
            int[] rows = ControllerProxy.getInstance().getNavigationController().getNavigationPanel().getNavigationTable().getSelectedRows();
            if (rows.length > 0) {
                List<AudioFile> audioFiles = ((NavigationTableModel) ControllerProxy.getInstance().getNavigationController().getNavigationPanel().getNavigationTable().getModel())
                        .getAudioFilesAt(rows);
                FavoritesHandler.getInstance().removeSongsFromFavorites(audioFiles);
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
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        // Enabled if all selected items are favorite songs (not belong to favorite artist nor album)
        return FavoritesHandler.getInstance().getFavoriteSongsInfo().values().containsAll(AudioFile.getAudioFiles(selection));
    }
}
