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
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import net.sourceforge.atunes.model.ITreeObject;

/**
 * Helps locating objects in a tree
 * @author alex
 *
 */
abstract class AudioObjectSelector<T extends ITreeObject<?>, U> {

	/**
	 * Returns first tree node representing audio object using given node as root of search
	 * @param root
	 * @param audioObject
	 * @return
	 */
	DefaultMutableTreeNode getNodeRepresentingAudioObject(DefaultMutableTreeNode root, U object) {
		List<DefaultMutableTreeNode> nodes = getNodesRepresentingAudioObject(root, object);
		return nodes.isEmpty() ? null : nodes.get(0);
	}

	/**
	 * Returns all tree nodes representing audio object using given node as root of search
	 * @param root
	 * @param audioObject
	 * @return
	 */
	@SuppressWarnings("unchecked")
	List<DefaultMutableTreeNode> getNodesRepresentingAudioObject(DefaultMutableTreeNode root, U object) {
		List<DefaultMutableTreeNode> treeNodes = new ArrayList<DefaultMutableTreeNode>();
		Enumeration<TreeNode> nodes = root.children();
		while (nodes.hasMoreElements()) {
			TreeNode node = nodes.nextElement();
			if (node instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode treeNode = ((DefaultMutableTreeNode)node);
				if (treeNode.getUserObject() instanceof ITreeObject) {
					ITreeObject<?> treeObject = (ITreeObject<?>) treeNode.getUserObject();
					if (equals((T) treeObject, object)) {
						treeNodes.add(treeNode);
					}
				}
			}
		}
		return treeNodes;
	}

	/**
	 * Returns true if tree object represents object
	 * @param treeObject
	 * @param object
	 * @return
	 */
	abstract boolean equals(T treeObject, U object);
}
