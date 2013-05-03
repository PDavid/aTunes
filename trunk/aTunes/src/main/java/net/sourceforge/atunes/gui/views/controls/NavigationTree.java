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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.TransferableList;
import net.sourceforge.atunes.model.INavigationTree;
import net.sourceforge.atunes.model.INavigationTreeRoot;
import net.sourceforge.atunes.model.ITreeNode;

/**
 * Navigation tree
 * 
 * @author alex
 * 
 */
public final class NavigationTree extends JTree implements DragSourceListener,
		DragGestureListener, INavigationTree {

	private static final long serialVersionUID = 5130815364968225924L;

	private DragSource dragSource;

	/**
	 * Instantiates a new drag source tree.
	 * 
	 * @param root
	 * @param renderer
	 */
	public NavigationTree(final String root, final TreeCellRenderer renderer) {
		super(new DefaultTreeModel(new DefaultMutableTreeNode(root)));
		setToggleClickCount(0);
		setCellRenderer(renderer);
		setDragSource();
	}

	@Override
	public void dragDropEnd(final DragSourceDropEvent dsde) {
		// Nothing to do
	}

	@Override
	public void dragEnter(final DragSourceDragEvent dsde) {
		// Nothing to do
	}

	@Override
	public void dragExit(final DragSourceEvent dse) {
		// Nothing to do
	}

	@Override
	public void dragGestureRecognized(final DragGestureEvent dge) {
		TreePath[] paths = getSelectionPaths();
		if (paths != null) {
			List<Object> itemsToDrag = new ArrayList<Object>();
			for (TreePath element : paths) {
				itemsToDrag
						.add(new NavigatorTreeNode(
								(DefaultMutableTreeNode) element
										.getLastPathComponent()));
			}
			TransferableList<Object> items = new TransferableList<Object>(
					itemsToDrag);
			this.dragSource.startDrag(dge, DragSource.DefaultCopyDrop, items,
					this);
		}
	}

	@Override
	public void dragOver(final DragSourceDragEvent dsde) {
		// Nothing to do
	}

	@Override
	public void dropActionChanged(final DragSourceDragEvent dsde) {
		// Nothing to do
	}

	/**
	 * Sets the drag source.
	 */
	private void setDragSource() {
		this.dragSource = new DragSource();
		this.dragSource.createDefaultDragGestureRecognizer(this,
				DnDConstants.ACTION_COPY, this);
	}

	@Override
	public void setRoot(INavigationTreeRoot rootObject) {
		DefaultMutableTreeNode root = getRoot();
		root.setUserObject(rootObject);
		// Check number of children to avoid unexpected errors
		if (root.getChildCount() > 0) {
			root.removeAllChildren();
		}
	}

	/**
	 * @return
	 */
	private DefaultMutableTreeNode getRoot() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel()
				.getRoot();
		return root;
	}

	@Override
	public void addNode(final ITreeNode node) {
		getRoot().add(node.getNode());
	}

	@Override
	public void selectNodes(final List<ITreeNode> nodesToSelect) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				if (nodesToSelect.isEmpty()) {
					setSelectionRow(0);
				} else {
					TreePath[] pathsToSelect = new TreePath[nodesToSelect
							.size()];
					int i = 0;
					for (ITreeNode node : nodesToSelect) {
						pathsToSelect[i++] = new TreePath(node.getNode()
								.getPath());
					}
					setSelectionPaths(pathsToSelect);
				}
			}
		});
	}

	@Override
	public void selectNode(final ITreeNode node) {
		TreePath treePath = new TreePath(node.getNode().getPath());
		setSelectionPath(treePath);
	}

	@Override
	public void scrollToNode(final ITreeNode node) {
		TreePath treePath = new TreePath(node.getNode().getPath());
		scrollPathToVisible(treePath);
	}

	@Override
	public void expandNodes(final List<ITreeNode> nodesToExpand) {
		for (ITreeNode node : nodesToExpand) {
			expandNode(node);
		}
	}

	@Override
	public void expandNode(final ITreeNode node) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				expandPath(new TreePath(node.getNode().getPath()));
			}
		});
	}

	@Override
	public ITreeNode createNode(final Object userObject) {
		return new NavigatorTreeNode(userObject);
	}

	@Override
	public void reload() {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				((DefaultTreeModel) getModel()).reload();
			}
		});
	}

	@Override
	public List<?> getExpandedDescendants() {
		List<Object> result = new ArrayList<Object>();
		Enumeration<TreePath> enume = getExpandedDescendants(new TreePath(
				((DefaultMutableTreeNode) getModel().getRoot()).getPath()));
		// If any node was expanded
		if (enume != null) {
			while (enume.hasMoreElements()) {
				result.add(((DefaultMutableTreeNode) enume.nextElement()
						.getLastPathComponent()).getUserObject());
			}
		}
		return result;
	}

	@Override
	public List<ITreeNode> getNodes(final List<?> userObjects) {
		return getNodes((DefaultMutableTreeNode) getModel().getRoot(),
				userObjects);
	}

	/**
	 * Recursive method to find nodes, based on whether user objects are present
	 * in given set
	 * 
	 * @param rootNode
	 * @param userObjects
	 * @return list of nodes
	 */
	private List<ITreeNode> getNodes(final DefaultMutableTreeNode rootNode,
			final List<?> userObjects) {
		List<ITreeNode> result = new ArrayList<ITreeNode>();

		if (userObjects.contains(rootNode.getUserObject())) {
			result.add(new NavigatorTreeNode(rootNode));
		}
		for (int i = 0; i < rootNode.getChildCount(); i++) {
			result.addAll(getNodes(
					(DefaultMutableTreeNode) rootNode.getChildAt(i),
					userObjects));
		}
		return result;
	}

	@Override
	public List<?> getRootChilds() {
		List<Object> result = new ArrayList<Object>();
		DefaultMutableTreeNode root = ((DefaultMutableTreeNode) getModel()
				.getRoot());
		for (int i = 0; i < root.getChildCount(); i++) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) root
					.getChildAt(i);
			result.add(childNode.getUserObject());
		}
		return result;
	}

	@Override
	public List<ITreeNode> getRootChildsNodes() {
		List<ITreeNode> result = new ArrayList<ITreeNode>();
		DefaultMutableTreeNode root = ((DefaultMutableTreeNode) getModel()
				.getRoot());
		for (int i = 0; i < root.getChildCount(); i++) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) root
					.getChildAt(i);
			result.add(new NavigatorTreeNode(childNode));
		}
		return result;
	}

	@Override
	public List<ITreeNode> getChildsNodes(final ITreeNode node) {
		List<ITreeNode> result = new ArrayList<ITreeNode>();
		for (int i = 0; i < node.getNode().getChildCount(); i++) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node
					.getNode().getChildAt(i);
			result.add(new NavigatorTreeNode(childNode));
		}
		return result;
	}

	@Override
	public List<ITreeNode> getSelectedNodes() {
		List<ITreeNode> nodes = new ArrayList<ITreeNode>();
		TreePath[] paths = getSelectionPaths();
		if (paths != null) {
			for (TreePath path : paths) {
				nodes.add(new NavigatorTreeNode((DefaultMutableTreeNode) path
						.getLastPathComponent()));
			}
		}
		return nodes;
	}

	@Override
	public ITreeNode getSelectedNode(final MouseEvent e) {
		TreePath selPath = getPathForLocation(e.getX(), e.getY());
		if (selPath != null) {
			return new NavigatorTreeNode(
					(DefaultMutableTreeNode) selPath.getLastPathComponent());
		}
		return null;
	}

	@Override
	public JTree getSwingComponent() {
		return this;
	}

	@Override
	public ITreeNode getSelectedNode() {
		return new NavigatorTreeNode(
				(DefaultMutableTreeNode) getSelectionPath()
						.getLastPathComponent());
	}
}
