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

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.model.ITreeNode;

/**
 * A tree node of navigator
 * 
 * @author alex
 * 
 */
public class NavigatorTreeNode implements ITreeNode {

    private final DefaultMutableTreeNode node;

    /**
     * @param node
     */
    NavigatorTreeNode(final DefaultMutableTreeNode node) {
	this.node = node;
    }

    /**
     * @param userObject
     */
    NavigatorTreeNode(final Object userObject) {
	this.node = new DefaultMutableTreeNode(userObject);
    }

    @Override
    public boolean isRoot() {
	return node.isRoot();
    }

    @Override
    public void add(final ITreeNode child) {
	node.add(child.getNode());
    }

    @Override
    public DefaultMutableTreeNode getNode() {
	return node;
    }

    @Override
    public Object getUserObject() {
	return node.getUserObject();
    }

    @Override
    public ITreeNode getParent() {
	return new NavigatorTreeNode(
		(DefaultMutableTreeNode) this.node.getParent());
    }

    @Override
    public int getChildCount() {
	return node.getChildCount();
    }

    @Override
    public ITreeNode getChildAt(final int i) {
	return new NavigatorTreeNode(
		(DefaultMutableTreeNode) this.node.getChildAt(i));
    }
}
