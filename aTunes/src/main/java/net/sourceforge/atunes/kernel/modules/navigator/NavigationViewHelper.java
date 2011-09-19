/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

class NavigationViewHelper {

    /**
     * Expands a list of nodes of a tree
     * 
     * @param tree
     * @param nodesToExpand
     */
    protected static final void expandNodes(JTree tree, List<DefaultMutableTreeNode> nodesToExpand) {
        for (DefaultMutableTreeNode node : nodesToExpand) {
            tree.expandPath(new TreePath(node.getPath()));
        }
    }

    /**
     * Selects a list of nodes of a tree
     * 
     * @param tree
     * @param nodesToSelect
     */
    protected static final void selectNodes(JTree tree, List<DefaultMutableTreeNode> nodesToSelect) {
        if (nodesToSelect.isEmpty()) {
            tree.setSelectionRow(0);
        } else {
            TreePath[] pathsToSelect = new TreePath[nodesToSelect.size()];
            int i = 0;
            for (DefaultMutableTreeNode node : nodesToSelect) {
                pathsToSelect[i++] = new TreePath(node.getPath());
            }
            tree.setSelectionPaths(pathsToSelect);
        }
    }
}
