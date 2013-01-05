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

package net.sourceforge.atunes.kernel.modules.search;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.views.dialogs.CustomSearchDialog;
import net.sourceforge.atunes.kernel.modules.search.SearchHandler.LogicalOperator;

/**
 * This class controls selection events in complex rules tree.
 */
public final class ComplexTreeSelectionListener implements
		TreeSelectionListener {

	/** Dialog controlled. */
	private final CustomSearchDialog dialog;

	/** Tree controlled. */
	private final JTree tree;

	/**
	 * Constructor.
	 * 
	 * @param dialog
	 *            the dialog
	 * @param tree
	 *            the tree
	 */
	public ComplexTreeSelectionListener(final CustomSearchDialog dialog,
			final JTree tree) {
		this.dialog = dialog;
		this.tree = tree;
	}

	@Override
	public void valueChanged(final TreeSelectionEvent e) {
		// Get selected tree path
		TreePath path = this.tree.getSelectionPath();

		// Enable or disable AND, OR, NOT, REMOVE controls
		this.dialog.enableComplexRuleButtons(path != null);

		if (path != null) {
			// Get node selected
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) (path
					.getLastPathComponent());

			// If node is a NOT user can't add more simple rules, as one has
			// been already added
			if (node.getUserObject().equals(LogicalOperator.NOT)) {
				this.dialog.getSimpleRulesAddButton().setEnabled(false);
			} else {
				// If it's not a NOT, user can add rules if node is any other
				// logical operator or an empty rule
				this.dialog.getSimpleRulesAddButton().setEnabled(
						node.getUserObject() instanceof LogicalOperator
								|| node.getUserObject() instanceof EmptyRule);
			}
		}
	}

}
