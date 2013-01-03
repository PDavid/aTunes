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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INavigationViewSorter;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds an Album ViewMode for a view. Several views can use this code
 * (Repository and Device)
 * 
 * @author fleax
 * 
 */
public class AlbumTreeGenerator implements ITreeGenerator {

    private INavigationViewSorter albumSorter;

    private IUnknownObjectChecker unknownObjectChecker;

    /**
     * @param unknownObjectChecker
     */
    public void setUnknownObjectChecker(
	    final IUnknownObjectChecker unknownObjectChecker) {
	this.unknownObjectChecker = unknownObjectChecker;
    }

    /**
     * @param albumSorter
     */
    public void setAlbumSorter(final INavigationViewSorter albumSorter) {
	this.albumSorter = albumSorter;
    }

    @Override
    public void buildTree(final INavigationTree tree, final String rootTextKey,
	    final INavigationView view, final Map<String, ?> structure,
	    final String currentFilter,
	    final List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    final List<ITreeObject<? extends IAudioObject>> objectsExpanded) {

	// Set root
	tree.setRoot(I18nUtils.getString(rootTextKey));

	List<String> albumsNamesList = new ArrayList<String>(structure.keySet());
	albumSorter.sort(albumsNamesList);

	// Nodes to be selected after refresh
	List<ITreeNode> nodesToSelect = new ArrayList<ITreeNode>();

	for (String albumName : albumsNamesList) {
	    buildAlbumNode(tree, structure, currentFilter, objectsSelected,
		    nodesToSelect, albumName);
	}

	// Reload the tree to refresh content
	tree.reload();

	// Once tree has been refreshed, select previously selected nodes
	tree.selectNodes(nodesToSelect);
    }

    /**
     * @param tree
     * @param structure
     * @param currentFilter
     * @param objectsSelected
     * @param nodesToSelect
     * @param albumName
     */
    private void buildAlbumNode(final INavigationTree tree,
	    final Map<String, ?> structure, final String currentFilter,
	    final List<ITreeObject<? extends IAudioObject>> objectsSelected,
	    final List<ITreeNode> nodesToSelect, final String albumName) {

	IAlbum album = (IAlbum) structure.get(albumName);
	if (currentFilter == null
		|| album.getName().toUpperCase()
			.contains(currentFilter.toUpperCase())) {
	    // Special album node that shows artist name too
	    ITreeNode albumNode = tree.createNode(album);
	    tree.addNode(albumNode);
	    // If node was selected before refreshing...
	    if (objectsSelected.contains(albumNode.getUserObject())) {
		nodesToSelect.add(albumNode);
	    }
	}
    }

    @Override
    public void selectAudioObject(final INavigationTree tree,
	    final IAudioObject audioObject) {
	ITreeNode albumNode = new AlbumAudioObjectSelector(unknownObjectChecker)
		.getNodeRepresentingAudioObject(tree, audioObject);
	if (albumNode != null) {
	    tree.selectNode(albumNode);
	    tree.scrollToNode(albumNode);
	}
    }

    @Override
    public void selectArtist(final INavigationTree tree, final String artist) {
	List<ITreeNode> albumsNodes = new AlbumWithArtistAudioObjectSelector()
		.getNodesRepresentingAudioObject(tree, artist);
	if (!albumsNodes.isEmpty()) {
	    tree.expandNodes(albumsNodes);
	    tree.selectNodes(albumsNodes);
	    tree.scrollToNode(albumsNodes.get(0));
	}
    }
}
