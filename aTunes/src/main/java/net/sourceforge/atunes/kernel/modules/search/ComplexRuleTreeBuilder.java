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

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.dialogs.CustomSearchDialog;
import net.sourceforge.atunes.model.ILogicalSearchOperator;
import net.sourceforge.atunes.model.ISearchField;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.model.ISearchOperator;
import net.sourceforge.atunes.model.ISearchRule;

/**
 * Helper for build complex rules
 * 
 * @author alex
 * 
 */
public class ComplexRuleTreeBuilder {

	private ILogicalSearchOperator andLogicalSearchOperator;

	private ILogicalSearchOperator orLogicalSearchOperator;

	private ILogicalSearchOperator notLogicalSearchOperator;

	/**
	 * @param andLogicalSearchOperator
	 */
	public void setAndLogicalSearchOperator(
			ILogicalSearchOperator andLogicalSearchOperator) {
		this.andLogicalSearchOperator = andLogicalSearchOperator;
	}

	/**
	 * @param orLogicalSearchOperator
	 */
	public void setOrLogicalSearchOperator(
			ILogicalSearchOperator orLogicalSearchOperator) {
		this.orLogicalSearchOperator = orLogicalSearchOperator;
	}

	/**
	 * @param notLogicalSearchOperator
	 */
	public void setNotLogicalSearchOperator(
			ILogicalSearchOperator notLogicalSearchOperator) {
		this.notLogicalSearchOperator = notLogicalSearchOperator;
	}

	/**
	 * Adds AND operator to rule.
	 * 
	 * @param dialog
	 * @return
	 */
	DefaultMutableTreeNode addAndOperator(CustomSearchDialog dialog) {
		return addLogicalOperator(dialog, andLogicalSearchOperator);
	}

	/**
	 * Adds OR operator to rule.
	 * 
	 * @param dialog
	 * @return
	 */
	DefaultMutableTreeNode addOrOperator(CustomSearchDialog dialog) {
		return addLogicalOperator(dialog, orLogicalSearchOperator);
	}

	/**
	 * Adds NOT operator to rule.
	 * 
	 * @param dialog
	 * @return
	 */
	DefaultMutableTreeNode addNotOperator(CustomSearchDialog dialog) {
		return addLogicalOperator(dialog, notLogicalSearchOperator);
	}

	/**
	 * Creates a simple rule.
	 */
	void createSimpleRule(CustomSearchDialog dialog) {
		ISearchField<?, ?> field = (ISearchField<?, ?>) dialog
				.getSimpleRulesList().getSelectedItem();
		ISearchOperator operator = (ISearchOperator) dialog
				.getSimpleRulesComboBox().getSelectedItem();
		String value = dialog.getSimpleRulesTextField().getText();

		// Create object
		ISearchRule simpleRule = new SearchRule(field, operator, value);

		// Add simple rule to tree
		DefaultTreeModel model = ((DefaultTreeModel) dialog
				.getComplexRulesTree().getModel());
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) dialog
				.getComplexRulesTree().getModel().getRoot();

		DefaultMutableTreeNode node = new DefaultMutableTreeNode(simpleRule);

		// Rule is empty, new simple rule is root
		if (rootNode == null) {
			model.setRoot(node);
		} else {
			// Rule is not empty, get selected node
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) dialog
					.getComplexRulesTree().getSelectionPath()
					.getLastPathComponent();

			if (selectedNode.getUserObject() instanceof SearchRule) {
				if (selectedNode.isRoot()) {
					// Create an AND or OR operator to add both rules
					DefaultMutableTreeNode logicalOperator = createLogicalOperator(
							dialog, (ISearchRule) selectedNode.getUserObject(),
							simpleRule);
					((DefaultMutableTreeNode) selectedNode.getParent())
							.remove(selectedNode);
					logicalOperator.add(selectedNode);
					logicalOperator.add(node);
				} else {
					// Add node as sibling
					((DefaultMutableTreeNode) selectedNode.getParent())
							.add(node);
				}
			} else {
				// Add as child
				selectedNode.add(node);
			}
		}

		// Reload model
		model.reload();

		// Expand complex rules tree to display full rule
		GuiUtils.expandTree(dialog.getComplexRulesTree());

		// Reset simple rule text field
		dialog.getSimpleRulesTextField().setText("");
	}

	private DefaultMutableTreeNode createLogicalOperator(
			CustomSearchDialog dialog, ISearchRule rule1, ISearchRule rule2) {
		if (rule1.getField().getClass().equals(rule2.getField().getClass())) {
			// Same class -> return OR
			return addOrOperator(dialog);
		} else {
			// Different class -> return AND
			return addAndOperator(dialog);
		}
	}

	/**
	 * Removes a node from rule tree.
	 * 
	 * @param dialog
	 */
	void removeRuleNode(CustomSearchDialog dialog) {
		// Get selected node and parent
		DefaultTreeModel model = ((DefaultTreeModel) dialog
				.getComplexRulesTree().getModel());
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) dialog
				.getComplexRulesTree().getSelectionPath()
				.getLastPathComponent();
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode
				.getParent();

		// If it's root, remove it
		if (selectedNode.isRoot()) {
			model.setRoot(null);
			dialog.getSimpleRulesAddButton().setEnabled(true);
		} else {
			parentNode.remove(selectedNode);
		}

		// Reload model
		model.reload();

		// Expand complex rules tree to display full rule
		GuiUtils.expandTree(dialog.getComplexRulesTree());
	}

	/**
	 * Generic method to add a logical operator to rule.
	 * 
	 * @param dialog
	 * @param operator
	 * @return node of logical operator
	 */
	private DefaultMutableTreeNode addLogicalOperator(
			CustomSearchDialog dialog, final ILogicalSearchOperator operator) {
		// Get selected node and parent
		DefaultTreeModel model = ((DefaultTreeModel) dialog
				.getComplexRulesTree().getModel());
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) dialog
				.getComplexRulesTree().getSelectionPath()
				.getLastPathComponent();
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode
				.getParent();

		// Create node
		DefaultMutableTreeNode logicalNode = new DefaultMutableTreeNode(
				operator);

		// Node is root
		if (parentNode == null) {
			// add as root
			model.setRoot(logicalNode);
		} else {
			// add as child of parent

			// remove all nodes and add again to keep order
			List<MutableTreeNode> nodes = new ArrayList<MutableTreeNode>();
			for (int i = 0; i < parentNode.getChildCount(); i++) {
				nodes.add((MutableTreeNode) parentNode.getChildAt(i));
			}
			parentNode.removeAllChildren();
			for (MutableTreeNode node : nodes) {
				if (node.equals(selectedNode)) {
					parentNode.add(logicalNode);
				} else {
					parentNode.add(node);
				}
			}

		}
		// Add previous selected node as child of new logical operator node
		logicalNode.add(selectedNode);

		// Reload model
		model.reload();

		// Expand complex rules tree to display full rule
		GuiUtils.expandTree(dialog.getComplexRulesTree());

		return logicalNode;
	}

	ISearchNode getSearchTree(CustomSearchDialog dialog)
			throws IllegalStateException {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) dialog
				.getComplexRulesTree().getModel().getRoot();
		return createQuery(root);
	}

	private ISearchNode createQuery(DefaultMutableTreeNode node)
			throws IllegalStateException {
		Object object = node.getUserObject();
		if (object instanceof ILogicalSearchOperator) {
			LogicalSearchNode logicalSearchNode = new LogicalSearchNode();
			logicalSearchNode.setOperator((ILogicalSearchOperator) object);
			if (node.getChildCount() > 0) {
				for (int i = 0; i < node.getChildCount(); i++) {
					logicalSearchNode
							.addChild(createQuery((DefaultMutableTreeNode) node
									.getChildAt(i)));
				}
			} else {
				throw new IllegalStateException("Logical node has no childs");
			}
			return logicalSearchNode;
		} else {
			return (ISearchNode) object;
		}
	}
}
