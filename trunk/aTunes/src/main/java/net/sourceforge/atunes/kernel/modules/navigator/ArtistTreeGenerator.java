/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds an Artist ViewMode for a view. Several views can use this code (Repository and Device)
 * @author fleax
 *
 */
public class ArtistTreeGenerator implements TreeGenerator {

	/**
	 * Builds tree
	 * @param rootTextKey
	 * @param view
	 * @param structure
	 * @param currentFilter
	 * @param root
	 * @param treeModel
	 * @param objectsSelected
	 * @param objectsExpanded
	 */
	@Override
    public void buildTree(String rootTextKey, AbstractNavigationView view, Map<String, ?> structure, String currentFilter, DefaultMutableTreeNode root, DefaultTreeModel treeModel, List<TreeObject> objectsSelected, List<TreeObject> objectsExpanded) {
        // Set root
        root.setUserObject(I18nUtils.getString(rootTextKey));
        root.removeAllChildren();

        List<String> artistNamesList = new ArrayList<String>(structure.keySet());
        if (ApplicationState.getInstance().isUseSmartTagViewSorting() && !ApplicationState.getInstance().isUsePersonNamesArtistTagViewSorting()) {
            Collections.sort(artistNamesList, view.getSmartComparator());
        } else if (ApplicationState.getInstance().isUsePersonNamesArtistTagViewSorting()) {
            Collections.sort(artistNamesList, view.getArtistNamesComparator());
        } else {
            Collections.sort(artistNamesList, view.getDefaultComparator());
        }

        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();

        for (int i = 0; i < artistNamesList.size(); i++) {
            Artist artist = (Artist) structure.get(artistNamesList.get(i));
            DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
            List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
            if (ApplicationState.getInstance().isUseSmartTagViewSorting()) {
                Collections.sort(albumNamesList, view.getSmartComparator());
            } else {
                Collections.sort(albumNamesList, view.getDefaultComparator());
            }
            if (currentFilter == null || artist.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                for (int j = 0; j < albumNamesList.size(); j++) {
                    Album album = artist.getAlbum(albumNamesList.get(j));
                    DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
                    artistNode.add(albumNode);
                    // If node was selected before refreshing...
                    if (objectsSelected.contains(albumNode.getUserObject())) {
                        nodesToSelect.add(albumNode);
                    }
                }
                root.add(artistNode);
                // Reload causes very important lag on large collections and if it is not used
                // selection does not work.

                // If node was selected before refreshing...
                if (objectsSelected.contains(artistNode.getUserObject())) {
                    nodesToSelect.add(artistNode);
                }
                // If node was expanded before refreshing...
                if (objectsExpanded.contains(artistNode.getUserObject())) {
                    nodesToExpand.add(artistNode);
                }
            }
        }

        // Reload the tree to refresh content
        treeModel.reload();

        // Expand nodes
        AbstractNavigationView.expandNodes(view.getTree(), nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        AbstractNavigationView.selectNodes(view.getTree(), nodesToSelect);
    }


}
