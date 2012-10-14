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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INavigationViewSorter;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds an Album ViewMode for a view. Several views can use this code (Repository and Device)
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
	public void buildTree(final String rootTextKey, final INavigationView view, final Map<String, ?> structure, final String currentFilter, final DefaultMutableTreeNode root, final DefaultTreeModel treeModel, final List<ITreeObject<? extends IAudioObject>> objectsSelected, final List<ITreeObject<? extends IAudioObject>> objectsExpanded) {
		// Set root
		root.setUserObject(I18nUtils.getString(rootTextKey));
		root.removeAllChildren();

		List<String> albumsNamesList = new ArrayList<String>(structure.keySet());
		albumSorter.sort(albumsNamesList);

		// Nodes to be selected after refresh
		List<DefaultMutableTreeNode> nodesToSelect = new ArrayList<DefaultMutableTreeNode>();

		for (String albumName : albumsNamesList) {
			buildAlbumNode(structure, currentFilter, root, objectsSelected, nodesToSelect, albumName);
		}

		// Reload the tree to refresh content
		treeModel.reload();

		// Once tree has been refreshed, select previously selected nodes
		NavigationViewHelper.selectNodes(view.getTree(), nodesToSelect);
	}

	/**
	 * @param structure
	 * @param currentFilter
	 * @param root
	 * @param objectsSelected
	 * @param nodesToSelect
	 * @param albumName
	 */
	private void buildAlbumNode(final Map<String, ?> structure, final String currentFilter,
			final DefaultMutableTreeNode root,
			final List<ITreeObject<? extends IAudioObject>> objectsSelected,
			final List<DefaultMutableTreeNode> nodesToSelect, final String albumName) {

		IAlbum album = (IAlbum) structure.get(albumName);
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

	@Override
	public void selectAudioObject(final JTree tree, final IAudioObject audioObject) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();

		DefaultMutableTreeNode albumNode = new AlbumAudioObjectSelector(unknownObjectChecker).getNodeRepresentingAudioObject(rootNode, audioObject);
		if (albumNode != null) {
			TreePath treePath = new TreePath(albumNode.getPath());
			tree.setSelectionPath(treePath);
			tree.scrollPathToVisible(treePath);
		}
	}

	@Override
	public void selectArtist(final JTree tree, final String artist) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();

		List<DefaultMutableTreeNode> albumsNodes = new AlbumWithArtistAudioObjectSelector().getNodesRepresentingAudioObject(rootNode, artist);
		if (!albumsNodes.isEmpty()) {
			List<TreePath> treePathList = new ArrayList<TreePath>();
			for (DefaultMutableTreeNode node : albumsNodes) {
				TreePath treePath = new TreePath(node.getPath());
				treePathList.add(treePath);
				tree.expandPath(treePath);
			}

			TreePath[] treePaths = new TreePath[treePathList.size()];
			treePaths = treePathList.toArray(treePaths);

			tree.setSelectionPaths(treePaths);
			tree.scrollPathToVisible(treePaths[0]);
		}
	}
}
