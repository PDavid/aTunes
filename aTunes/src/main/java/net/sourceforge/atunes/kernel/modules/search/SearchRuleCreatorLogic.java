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

import java.awt.event.ItemEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.views.dialogs.CustomSearchDialog;
import net.sourceforge.atunes.model.ILogicalSearchOperator;
import net.sourceforge.atunes.model.ISearchBinaryOperator;
import net.sourceforge.atunes.model.ISearchField;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Controls logic creating search rules
 * 
 * @author alex
 * 
 */
class SearchRuleCreatorLogic {

	private final CustomSearchDialog dialog;

	private final ComplexRuleTreeBuilder complexRuleTreeBuilder;

	private final ILogicalSearchOperator notLogicalSearchOperator;

	/**
	 * @param dialog
	 * @param complexRuleTreeBuilder
	 * @param notLogicalSearchOperator
	 */
	public SearchRuleCreatorLogic(final CustomSearchDialog dialog,
			final ComplexRuleTreeBuilder complexRuleTreeBuilder,
			final ILogicalSearchOperator notLogicalSearchOperator) {
		this.dialog = dialog;
		this.complexRuleTreeBuilder = complexRuleTreeBuilder;
		this.notLogicalSearchOperator = notLogicalSearchOperator;
	}

	void disableSearch() {
		this.dialog.getSearchButton().setEnabled(false);
	}

	void adjustSearchRuleTextField(final ItemEvent event) {
		// Only allow text field for binary operators
		boolean isBinary = event.getItem() instanceof ISearchBinaryOperator;
		this.dialog.getSimpleRulesTextField().setEnabled(isBinary);
		if (!isBinary) {
			this.dialog.getSimpleRulesTextField().setText("");
		}
		adjustAddButton(!isBinary || !isRuleTextFieldEmpty());
	}

	void adjustAddButton(final boolean enabled) {
		this.dialog.getSimpleRulesAddButton().setEnabled(enabled);
	}

	void adjustAddButton() {
		this.dialog.getSimpleRulesAddButton().setEnabled(
				!isRuleTextFieldEmpty());
	}

	boolean isRuleTextFieldEmpty() {
		return StringUtils.isEmpty(this.dialog.getSimpleRulesTextField()
				.getText());
	}

	void pressRuleTextFieldEnter() {
		if (!isRuleTextFieldEmpty()) {
			pressAddButton();
		}
	}

	private void checkEmptyRule() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.dialog
				.getComplexRulesTree().getModel().getRoot();
		this.dialog.getSearchButton().setEnabled(root != null);
	}

	void pressAddButton() {
		this.complexRuleTreeBuilder.createSimpleRule(this.dialog);
		checkEmptyRule();
	}

	void pressAndButton() {
		this.complexRuleTreeBuilder.addAndOperator(this.dialog);
		checkEmptyRule();
	}

	public void pressOrButton() {
		this.complexRuleTreeBuilder.addOrOperator(this.dialog);
		checkEmptyRule();
	}

	void pressNotButton() {
		this.complexRuleTreeBuilder.addNotOperator(this.dialog);
		checkEmptyRule();
	}

	void pressRemoveButton() {
		this.complexRuleTreeBuilder.removeRuleNode(this.dialog);
		checkEmptyRule();
	}

	void treeItemSelected() {
		// Get selected tree path
		TreePath path = this.dialog.getComplexRulesTree().getSelectionPath();

		// Enable or disable AND, OR, NOT, REMOVE controls
		this.dialog.enableComplexRuleButtons(path != null);

		if (path != null) {
			// Get node selected
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) (path
					.getLastPathComponent());

			DefaultMutableTreeNode parent = null;
			if (node.getParent() != null) {
				parent = (DefaultMutableTreeNode) node.getParent();
			}

			boolean enabled = true;
			// If node is a NOT user can't add more simple rules if it has
			// already a child
			if (node.getUserObject().equals(this.notLogicalSearchOperator)
					&& !node.isLeaf()) {
				enabled = false;
			} else if (parent != null
					&& parent.getUserObject().equals(
							this.notLogicalSearchOperator)) {
				enabled = false;
			}

			this.dialog.getSimpleRulesTextField().setEnabled(enabled);
			if (!enabled) {
				this.dialog.getSimpleRulesTextField().setText("");
			}
			this.dialog.getSimpleRulesAddButton().setEnabled(
					enabled
							&& !StringUtils.isEmpty(this.dialog
									.getSimpleRulesTextField().getText()));
		}
	}

	void ruleSelected(final ISearchField<?, ?> searchField) {
		this.dialog.getSimpleRulesComboBox().setModel(
				new DefaultComboBoxModel(searchField.getOperators().toArray()));

		// Only allow text field for binary operators
		boolean isBinary = searchField.getOperators().get(0) instanceof ISearchBinaryOperator;
		this.dialog.getSimpleRulesTextField().setEnabled(isBinary);
		if (!isBinary) {
			this.dialog.getSimpleRulesTextField().setText("");
		}
		this.dialog.getSimpleRulesAddButton().setEnabled(
				!isBinary
						|| !StringUtils.isEmpty(this.dialog
								.getSimpleRulesTextField().getText()));
	}
}
