/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.kernel.modules.repository.data.Folder;
import net.sourceforge.atunes.model.TreeObject;

/**
 * The Class RefreshUtils.
 */
public class RefreshUtils {

    /**
     * Adds the folder nodes.
     * 
     * @param folders
     *            the folders
     * @param node
     *            the node
     * @param currentFilter
     *            the current filter
     * @param comparator
     *            the comparator for node sorting
     */
    static void addFolderNodes(Map<String, ?> folders, DefaultMutableTreeNode node, String currentFilter, Comparator<String> comparator) {
        List<String> folderNamesList = new ArrayList<String>(folders.keySet());
        Collections.sort(folderNamesList, comparator);

        for (int i = 0; i < folderNamesList.size(); i++) {
            Folder f = (Folder) folders.get(folderNamesList.get(i));
            if (node.isRoot() || currentFilter == null || f.getName().toUpperCase().contains(currentFilter.toUpperCase())) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(f);
                node.add(child);
                addFolderNodes(f.getFolders(), child, node.isRoot() ? currentFilter : null, comparator);
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
    static List<DefaultMutableTreeNode> getNodes(DefaultMutableTreeNode rootNode, List<TreeObject> userObjects) {
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
