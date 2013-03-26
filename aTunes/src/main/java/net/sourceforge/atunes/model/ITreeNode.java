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

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Wraps a node of a tree
 * 
 * @author alex
 * 
 */
public interface ITreeNode {

    /**
     * @return if node is root
     */
    boolean isRoot();

    /**
     * Adds child node
     * 
     * @param child
     */
    void add(ITreeNode child);

    /**
     * @return underlying node implementation
     */
    DefaultMutableTreeNode getNode();

    /**
     * @return object of this node
     */
    Object getUserObject();

    /**
     * @return parent node
     */
    ITreeNode getParent();

    /**
     * @return number of children
     */
    int getChildCount();

    /**
     * @param i
     * @return children at given position
     */
    ITreeNode getChildAt(int i);
}
