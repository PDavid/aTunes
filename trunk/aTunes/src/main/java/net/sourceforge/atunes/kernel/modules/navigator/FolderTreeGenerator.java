/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import net.sourceforge.atunes.model.TreeObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Builds a Folder ViewMode for a view. Several views can use this code (Repository and Device)
 * @author fleax
 *
 */
public class FolderTreeGenerator implements TreeGenerator {

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
    public void buildTree(String rootTextKey, AbstractNavigationView view, Map<String, ?> structure, String currentFilter, DefaultMutableTreeNode root, DefaultTreeModel treeModel, List<TreeObject> objectsSelected, List<TreeObject> objectsExpanded) {

        // Refresh nodes
        root.setUserObject(I18nUtils.getString(rootTextKey));
        root.removeAllChildren();
        RefreshUtils.addFolderNodes(structure, root, currentFilter, view.getDefaultComparator());

        treeModel.reload();

        if (objectsExpanded.isEmpty()) {
            // In folder view root child nodes must be expanded always
            // So when refreshing folder view for first time add these nodes to list of expanded objects
            for (int i = 0; i < root.getChildCount(); i++) {
                TreeNode childNode = root.getChildAt(i);
                objectsExpanded.add((TreeObject) ((DefaultMutableTreeNode) childNode).getUserObject());
            }
        }

        // Get nodes to select after refresh
        List<DefaultMutableTreeNode> nodesToSelect = RefreshUtils.getNodes(root, objectsSelected);

        // Get nodes to expand after refresh
        List<DefaultMutableTreeNode> nodesToExpand = RefreshUtils.getNodes(root, objectsExpanded);

        // Expand nodes
        AbstractNavigationView.expandNodes(view.getTree(), nodesToExpand);

        // Once tree has been refreshed, select previously selected nodes
        AbstractNavigationView.selectNodes(view.getTree(), nodesToSelect);
    }


}
