/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds an Album ViewMode for a view. Several views can use this code (Repository and Device)
 * @author fleax
 *
 */
public class AlbumTreeGenerator implements TreeGenerator {

    protected static final class AlbumDefaultMutableTreeNode extends DefaultMutableTreeNode {
        private static final long serialVersionUID = -1276777390072754207L;

        private AlbumDefaultMutableTreeNode(Object userObject) {
            super(userObject);
        }

        @Override
        public String toString() {
            return ((Album) getUserObject()).getNameAndArtist();
        }
    }

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
    public void buildTree(String rootTextKey, AbstractNavigationView view, Map<String, ?> structure, String currentFilter, DefaultMutableTreeNode root, DefaultTreeModel treeModel, List<TreeObject> objectsSelected, List<TreeObject> objectsExpanded) {
        // Set root
        root.setUserObject(I18nUtils.getString(rootTextKey));
        root.removeAllChildren();

        List<String> albumsNamesList = new ArrayList<String>(structure.keySet());
        if (ApplicationState.getInstance().isUseSmartTagViewSorting()) {
            Collections.sort(albumsNamesList, view.getSmartComparator());
        } else {
            Collections.sort(albumsNamesList, view.getDefaultComparator());
        }

        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();

        for (int i = 0; i < albumsNamesList.size(); i++) {
            Album album = (Album) structure.get(albumsNamesList.get(i));
            if (currentFilter == null || album.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                // Special album node that shows artist name too
                DefaultMutableTreeNode albumNode = new AlbumDefaultMutableTreeNode(album);

                root.add(albumNode);

                // If node was selected before refreshing...
                if (objectsSelected.contains(albumNode.getUserObject())) {
                    nodesToSelect.add(albumNode);
                }
            }
        }

        // Reload the tree to refresh content
        treeModel.reload();

        // Once tree has been refreshed, select previously selected nodes
        AbstractNavigationView.selectNodes(view.getTree(), nodesToSelect);
    }


}
