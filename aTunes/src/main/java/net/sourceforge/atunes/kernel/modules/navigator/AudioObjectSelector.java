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

import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;

/**
 * Helps locating objects in a tree
 * 
 * @author alex
 * 
 */
abstract class AudioObjectSelector<T extends ITreeObject<?>, U> {

    /**
     * Returns first tree node representing audio object
     * 
     * @param tree
     * @param object
     * @return
     */
    final ITreeNode getNodeRepresentingAudioObject(final INavigationTree tree,
	    final U object) {
	List<ITreeNode> nodes = getNodesRepresentingAudioObject(tree, object);
	return nodes.isEmpty() ? null : nodes.get(0);
    }

    /**
     * Returns first tree node representing audio object
     * 
     * @param node
     * @param object
     * @return
     */
    final ITreeNode getNodeRepresentingAudioObject(final INavigationTree tree,
	    final ITreeNode node, final U object) {
	List<ITreeNode> nodes = getNodesRepresentingAudioObject(tree, node,
		object);
	return nodes.isEmpty() ? null : nodes.get(0);
    }

    /**
     * Returns all tree nodes representing audio object
     * 
     * @param tree
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
    final List<ITreeNode> getNodesRepresentingAudioObject(
	    final INavigationTree tree, final U object) {
	List<ITreeNode> treeNodes = new ArrayList<ITreeNode>();
	List<ITreeNode> rootChilds = tree.getRootChildsNodes();
	for (ITreeNode o : rootChilds) {
	    if (equals((T) o.getUserObject(), object)) {
		treeNodes.add(o);
	    }
	}
	return treeNodes;
    }

    /**
     * Returns all tree nodes representing audio object
     * 
     * @param tree
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
    final List<ITreeNode> getNodesRepresentingAudioObject(
	    final INavigationTree tree, final ITreeNode node, final U object) {
	List<ITreeNode> treeNodes = new ArrayList<ITreeNode>();
	List<ITreeNode> childs = tree.getChildsNodes(node);
	for (ITreeNode o : childs) {
	    if (equals((T) o.getUserObject(), object)) {
		treeNodes.add(o);
	    }
	}
	return treeNodes;
    }

    /**
     * Returns true if tree object represents object
     * 
     * @param treeObject
     * @param object
     * @return
     */
    abstract boolean equals(T treeObject, U object);
}
