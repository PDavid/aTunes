/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.controllers.customsearch;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.views.dialogs.CustomSearchDialog;
import net.sourceforge.atunes.kernel.modules.search.EmptyRule;
import net.sourceforge.atunes.kernel.modules.search.SearchHandler.LogicalOperator;

/**
 * This class controls selection events in complex rules tree.
 */
public class ComplexTreeSelectionListener implements TreeSelectionListener {

    /** Dialog controlled. */
    private CustomSearchDialog dialog;

    /** Tree controlled. */
    private JTree tree;

    /**
     * Constructor.
     * 
     * @param dialog
     *            the dialog
     * @param tree
     *            the tree
     */
    public ComplexTreeSelectionListener(CustomSearchDialog dialog, JTree tree) {
        this.dialog = dialog;
        this.tree = tree;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event
     * .TreeSelectionEvent)
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        // Get selected tree path
        TreePath path = tree.getSelectionPath();

        if (path != null) {
            // Get node selected
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) (path.getLastPathComponent());

            // If node is a NOT user can't add more simple rules, as one has been already added
            if (node.getUserObject().equals(LogicalOperator.NOT)) {
                dialog.getSimpleRulesAddButton().setEnabled(false);
            } else {
                // If it's not a NOT, user can add rules if node is any other logical operator or an empty rule
                dialog.getSimpleRulesAddButton().setEnabled(node.getUserObject() instanceof LogicalOperator || node.getUserObject() instanceof EmptyRule);
            }
        }
    }

}
