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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.INavigationViewSorter;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITreeGenerator;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds a Folder ViewMode for a view. Several views can use this code
 * (Repository and Device)
 * 
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

	@SuppressWarnings("unchecked")
	@Override
	public void buildTree(final INavigationTree tree, final String rootTextKey,
			final INavigationView view, final Map<String, ?> structure,
			final String currentFilter,
			final List<ITreeObject<? extends IAudioObject>> objectsSelected,
			final List<ITreeObject<? extends IAudioObject>> objectsExpanded) {
		// Refresh nodes
		tree.setRoot(new NavigationTreeRoot(I18nUtils.getString(rootTextKey),
				view.getIcon()));

		addFolderNodes(tree, structure, currentFilter, this.folderSorter);

		tree.reload();

		if (objectsExpanded.isEmpty()) {
			// In folder view root child nodes must be expanded always
			// So when refreshing folder view for first time add these nodes to
			// list of expanded objects
			List<?> rootChilds = tree.getRootChilds();
			for (Object rootChild : rootChilds) {
				objectsExpanded
						.add((ITreeObject<? extends IAudioObject>) rootChild);
			}
		}

		// Get nodes to select after refresh
		List<ITreeNode> nodesToSelect = tree.getNodes(objectsSelected);

		// Get nodes to expand after refresh
		List<ITreeNode> nodesToExpand = tree.getNodes(objectsExpanded);

		// Expand nodes
		tree.expandNodes(nodesToExpand);

		// Once tree has been refreshed, select previously selected nodes
		tree.selectNodes(nodesToSelect);
	}

	@Override
	public void selectAudioObject(final INavigationTree tree,
			final IAudioObject audioObject) {
		if (audioObject instanceof ILocalAudioObject) {
			String filePath = audioObject.getUrl();
			ITreeNode folderNode = new FolderAudioObjectSelector()
					.getNodeRepresentingAudioObject(tree, filePath);
			if (folderNode != null) {
				IFolder f = (IFolder) folderNode.getUserObject();
				String searchPath = filePath
						.substring(f.getName().length() + 1);
				String[] paths = searchPath.split(this.osManager
						.getFileSeparator());
				ITreeNode node = getTreeNodeForLevel(paths, 0, tree, folderNode);
				if (node != null) {
					tree.selectNode(node);
					tree.scrollToNode(node);
					tree.expandNode(node);
				}
			}
		}
	}

	private ITreeNode getTreeNodeForLevel(final String[] paths,
			final int currentLevel, final INavigationTree tree,
			final ITreeNode foldersRootNode) {
		ITreeNode folderNode = new FolderAudioObjectSelector()
				.getNodeRepresentingAudioObject(tree, foldersRootNode,
						paths[currentLevel]);
		if (folderNode != null) {
			if (currentLevel == paths.length - 2) {
				return folderNode;
			} else {
				return getTreeNodeForLevel(paths, currentLevel + 1, tree,
						folderNode);
			}
		}
		return null;
	}

	@Override
	public void selectArtist(final INavigationTree tree, final String artist) {
	}

	/**
	 * Adds the folder nodes to root node
	 * 
	 * @param folders
	 *            the folders
	 * @param currentFilter
	 *            the current filter
	 * @param sorter
	 *            the comparator for node sorting
	 */
	private void addFolderNodes(final INavigationTree tree,
			final Map<String, ?> folders, final String currentFilter,
			final INavigationViewSorter sorter) {
		List<String> folderNamesList = new ArrayList<String>(folders.keySet());
		sorter.sort(folderNamesList);
		for (int i = 0; i < folderNamesList.size(); i++) {
			IFolder f = (IFolder) folders.get(folderNamesList.get(i));
			ITreeNode child = tree.createNode(f);
			tree.addNode(child);
			addFolderNodes(tree, f.getFolders(), child, currentFilter, sorter);
		}
	}

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
	private void addFolderNodes(final INavigationTree tree,
			final Map<String, ?> folders, final ITreeNode node,
			final String currentFilter, final INavigationViewSorter sorter) {
		List<String> folderNamesList = new ArrayList<String>(folders.keySet());
		sorter.sort(folderNamesList);
		for (int i = 0; i < folderNamesList.size(); i++) {
			IFolder f = (IFolder) folders.get(folderNamesList.get(i));
			if (folderMatchesFilter(currentFilter, f)) {
				ITreeNode child = tree.createNode(f);
				node.add(child);
				addFolderNodes(tree, f.getFolders(), child, currentFilter,
						sorter);
			}
		}
	}

	private boolean folderMatchesFilter(final String currentFilter,
			final IFolder f) {
		return currentFilter == null
				|| folderOrSubfoldersMatchFilter(currentFilter, f);
	}

	private boolean folderOrSubfoldersMatchFilter(final String currentFilter,
			final IFolder f) {
		if (folderMatches(currentFilter, f)) {
			return true;
		} else if (!f.isLeaf()) {
			for (IFolder childFolder : f.getFolders().values()) {
				if (folderOrSubfoldersMatchFilter(currentFilter, childFolder)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param currentFilter
	 * @param f
	 * @return
	 */
	private boolean folderMatches(final String currentFilter, final IFolder f) {
		return f.getName().toUpperCase().contains(currentFilter.toUpperCase());
	}
}
