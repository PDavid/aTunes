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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INavigationViewSorter;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds a Folder ViewMode for a view. Several views can use this code (Repository and Device)
 * @author fleax
 *
 */
public class FolderTreeGenerator implements ITreeGenerator {

	private INavigationViewSorter folderSorter;

	private IOSManager osManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param folderSorter
	 */
	public void setFolderSorter(final INavigationViewSorter folderSorter) {
		this.folderSorter = folderSorter;
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
	@SuppressWarnings("unchecked")
	public void buildTree(final String rootTextKey, final INavigationView view, final Map<String, ?> structure, final String currentFilter, final DefaultMutableTreeNode root, final DefaultTreeModel treeModel, final List<ITreeObject<? extends IAudioObject>> objectsSelected, final List<ITreeObject<? extends IAudioObject>> objectsExpanded) {
		// Refresh nodes
		root.setUserObject(I18nUtils.getString(rootTextKey));
		root.removeAllChildren();
		addFolderNodes(structure, root, currentFilter, folderSorter);

		treeModel.reload();

		if (objectsExpanded.isEmpty()) {
			// In folder view root child nodes must be expanded always
			// So when refreshing folder view for first time add these nodes to list of expanded objects
			for (int i = 0; i < root.getChildCount(); i++) {
				TreeNode childNode = root.getChildAt(i);
				objectsExpanded.add((ITreeObject<? extends IAudioObject>) ((DefaultMutableTreeNode) childNode).getUserObject());
			}
		}

		// Get nodes to select after refresh
		List<DefaultMutableTreeNode> nodesToSelect = getNodes(root, objectsSelected);

		// Get nodes to expand after refresh
		List<DefaultMutableTreeNode> nodesToExpand = getNodes(root, objectsExpanded);

		// Expand nodes
		NavigationViewHelper.expandNodes(view.getTree(), nodesToExpand);

		// Once tree has been refreshed, select previously selected nodes
		NavigationViewHelper.selectNodes(view.getTree(), nodesToSelect);
	}

	@Override
	public void selectAudioObject(final JTree tree, final IAudioObject audioObject) {
		if (audioObject instanceof ILocalAudioObject) {
			String filePath = audioObject.getUrl();
			DefaultMutableTreeNode folderNode = new FolderAudioObjectSelector().getNodeRepresentingAudioObject((DefaultMutableTreeNode)tree.getModel().getRoot(), filePath);
			if (folderNode != null) {
				IFolder f = (IFolder) folderNode.getUserObject();
				String searchPath = filePath.substring(f.getName().length()+1);
				String[] paths = searchPath.split(osManager.getFileSeparator());
				TreePath treePath = getTreePathForLevel(paths, 0, folderNode);
				if (treePath != null) {
					tree.setSelectionPath(treePath);
					tree.scrollPathToVisible(treePath);
					tree.expandPath(treePath);
				}
			}
		}
	}

	private TreePath getTreePathForLevel(final String[] paths, final int currentLevel, final DefaultMutableTreeNode foldersRootNode) {
		TreePath treePath = null;
		DefaultMutableTreeNode folderNode = new FolderAudioObjectSelector().getNodeRepresentingAudioObject(foldersRootNode, paths[currentLevel]);
		if (folderNode != null) {
			if (currentLevel == paths.length - 2) {
				treePath =  new TreePath(folderNode.getPath());
			} else {
				treePath = getTreePathForLevel(paths, currentLevel + 1, folderNode);
			}
		}
		return treePath;
	}

	@Override
	public void selectArtist(final JTree tree, final String artist) {}

	/**
	 * Adds the folder nodes.
	 * 
	 * @param folders
	 *            the folders
	 * @param node
	 *            the node
	 * @param currentFilter
	 *            the current filter
	 * @param sorter
	 *            the comparator for node sorting
	 */
	private void addFolderNodes(final Map<String, ?> folders, final DefaultMutableTreeNode node, final String currentFilter, final INavigationViewSorter sorter) {
		List<String> folderNamesList = new ArrayList<String>(folders.keySet());
		sorter.sort(folderNamesList);
		for (int i = 0; i < folderNamesList.size(); i++) {
			IFolder f = (IFolder) folders.get(folderNamesList.get(i));
			if (node.isRoot() || currentFilter == null || f.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(f);
				node.add(child);
				addFolderNodes(f.getFolders(), child, node.isRoot() ? currentFilter : null, sorter);
			}
		}
	}

	/**
	 * Recursive method to find nodes, based on whether user objects are present
	 * in given set
	 * 
	 * @param rootNode
	 * @param userObjects
	 * @return list of nodes
	 */
	private List<DefaultMutableTreeNode> getNodes(final DefaultMutableTreeNode rootNode, final List<ITreeObject<? extends IAudioObject>> userObjects) {
		List<DefaultMutableTreeNode> result = new ArrayList<DefaultMutableTreeNode>();

		if (userObjects.contains(rootNode.getUserObject())) {
			result.add(rootNode);
		}
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			result.addAll(getNodes((DefaultMutableTreeNode) rootNode.getChildAt(i), userObjects));
		}
		return result;
	}

}
