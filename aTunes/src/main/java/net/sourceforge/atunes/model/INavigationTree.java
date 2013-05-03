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

package net.sourceforge.atunes.model;

import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JTree;

/**
 * UI component for navigation tree
 * 
 * @author alex
 * 
 */
public interface INavigationTree extends IComponent<JTree> {

	/**
	 * Sets root object
	 * 
	 * @param root
	 */
	void setRoot(INavigationTreeRoot root);

	/**
	 * Adds node to root
	 * 
	 * @param node
	 */
	void addNode(ITreeNode node);

	/**
	 * @param userObject
	 * @return node created
	 */
	ITreeNode createNode(Object userObject);

	/**
	 * Selects a list of nodes of a tree
	 * 
	 * @param nodesToSelect
	 */
	void selectNodes(final List<ITreeNode> nodesToSelect);

	/**
	 * Expands a list of nodes of a tree
	 * 
	 * @param nodesToExpand
	 */
	void expandNodes(List<ITreeNode> nodesToExpand);

	/**
	 * reloads tree model
	 */
	void reload();

	/**
	 * @return nodes expanded
	 */
	List<?> getExpandedDescendants();

	/**
	 * Recursive method to find nodes, based on whether user objects are present
	 * in given list
	 * 
	 * @param userObjects
	 * @return list of nodes
	 */
	List<ITreeNode> getNodes(final List<?> userObjects);

	/**
	 * @return childs of root
	 */
	List<?> getRootChilds();

	/**
	 * @return child nodes of root
	 */
	List<ITreeNode> getRootChildsNodes();

	/**
	 * Select a node
	 * 
	 * @param albumNode
	 */
	void selectNode(ITreeNode albumNode);

	/**
	 * Scrolls to a node
	 * 
	 * @param node
	 */
	void scrollToNode(ITreeNode node);

	/**
	 * @param node
	 * @return nodes child of given node
	 */
	List<ITreeNode> getChildsNodes(ITreeNode node);

	/**
	 * Expands given node
	 * 
	 * @param artistNode
	 */
	void expandNode(ITreeNode artistNode);

	/**
	 * @return selected nodes
	 */
	List<ITreeNode> getSelectedNodes();

	/**
	 * @return selected node
	 */
	ITreeNode getSelectedNode();

	/**
	 * @param e
	 * @return selected node given mouse position
	 */
	ITreeNode getSelectedNode(final MouseEvent e);
}
