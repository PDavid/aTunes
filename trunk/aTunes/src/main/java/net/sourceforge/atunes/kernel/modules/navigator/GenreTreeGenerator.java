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
import net.sourceforge.atunes.kernel.modules.repository.data.Genre;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds a Genre ViewMode for a view. Several views can use this code (Repository and Device)
 * @author fleax
 *
 */
public class GenreTreeGenerator implements TreeGenerator {

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
        // Nodes to be selected after refresh
        List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();
        // Nodes to be expanded after refresh
        List<DefaultMutableTreeNode> nodesToExpand = new ArrayList<DefaultMutableTreeNode>();
        
        // Refresh nodes
        root.setUserObject(I18nUtils.getString(rootTextKey));
        root.removeAllChildren();
        List<String> genreNamesList = new ArrayList<String>(structure.keySet());
        Collections.sort(genreNamesList, view.getDefaultComparator());

        for (int i = 0; i < genreNamesList.size(); i++) {
            Genre genre = (Genre) structure.get(genreNamesList.get(i));
            if (currentFilter == null || genre.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                DefaultMutableTreeNode genreNode = new DefaultMutableTreeNode(genre);
                // If node was selected before refreshing...
                if (objectsSelected.contains(genreNode.getUserObject())) {
                    nodesToSelect.add(genreNode);
                }
                // If node was expanded before refreshing...
                if (objectsExpanded.contains(genreNode.getUserObject())) {
                    nodesToExpand.add(genreNode);
                }
                List<String> artistNamesList = new ArrayList<String>(genre.getArtistSet());
                if (ApplicationState.getInstance().isUseSmartTagViewSorting() && !ApplicationState.getInstance().isUsePersonNamesArtistTagViewSorting()) {
                    Collections.sort(artistNamesList, view.getSmartComparator());
                } else if (ApplicationState.getInstance().isUsePersonNamesArtistTagViewSorting()) {
                    Collections.sort(artistNamesList, view.getArtistNamesComparator());
                } else {
                    Collections.sort(artistNamesList, view.getDefaultComparator());
                }
                Map<String, Artist> genreArtists = genre.getArtistObjects();
                for (int j = 0; j < artistNamesList.size(); j++) {
                    Artist artist = genreArtists.get(artistNamesList.get(j));
                    DefaultMutableTreeNode artistNode = new DefaultMutableTreeNode(artist);
                    List<String> albumNamesList = new ArrayList<String>(artist.getAlbums().keySet());
                    if (ApplicationState.getInstance().isUseSmartTagViewSorting()) {
                        Collections.sort(albumNamesList, view.getSmartComparator());
                    } else {
                        Collections.sort(albumNamesList, view.getDefaultComparator());
                    }
                    for (int k = 0; k < albumNamesList.size(); k++) {
                        Album album = artist.getAlbum(albumNamesList.get(k));
                        DefaultMutableTreeNode albumNode = new DefaultMutableTreeNode(album);
                        artistNode.add(albumNode);
                        genreNode.add(artistNode);
                        // If node was selected before refreshing...
                        if (objectsSelected.contains(artistNode.getUserObject())) {
                            nodesToSelect.add(artistNode);
                        }
                        // If node was selected before refreshing...
                        if (objectsSelected.contains(albumNode.getUserObject())) {
                            nodesToSelect.add(albumNode);
                        }
                        // If node was expanded before refreshing...
                        if (objectsExpanded.contains(artistNode.getUserObject()) && objectsExpanded.contains(((DefaultMutableTreeNode) artistNode.getParent()).getUserObject())) {
                            nodesToExpand.add(artistNode);
                        }
                    }
                }
                root.add(genreNode);
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
