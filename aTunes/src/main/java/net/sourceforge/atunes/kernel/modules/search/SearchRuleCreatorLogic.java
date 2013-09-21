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

	/**
	 * @param dialog
	 * @param complexRuleTreeBuilder
	 */
	public SearchRuleCreatorLogic(final CustomSearchDialog dialog,
			final ComplexRuleTreeBuilder complexRuleTreeBuilder) {
		this.dialog = dialog;
		this.complexRuleTreeBuilder = complexRuleTreeBuilder;
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

	private void checkRule() {
		this.dialog.getSearchButton().setEnabled(
				this.complexRuleTreeBuilder.isValidRule(dialog));
	}

	void pressAddButton() {
		this.complexRuleTreeBuilder.createSimpleRule(this.dialog);
		checkRule();
		enableSearchRuleAddControls(currentSelectedNodeAllowsAdd());
	}

	void pressAndButton() {
		this.complexRuleTreeBuilder.addAndOperator(this.dialog);
		checkRule();
	}

	public void pressOrButton() {
		this.complexRuleTreeBuilder.addOrOperator(this.dialog);
		checkRule();
	}

	void pressNotButton() {
		this.complexRuleTreeBuilder.addNotOperator(this.dialog);
		checkRule();
	}

	void pressRemoveButton() {
		this.complexRuleTreeBuilder.removeRuleNode(this.dialog);
		checkRule();
	}

	void treeItemSelected() {
		// Get selected tree path
		TreePath path = this.dialog.getComplexRulesTree().getSelectionPath();

		// Enable or disable AND, OR, NOT, REMOVE controls
		this.dialog.enableComplexRuleButtons(path != null);

		enableSearchRuleAddControls(currentSelectedNodeAllowsAdd());
	}

	void ruleSelected(final ISearchField<?, ?> searchField) {
		this.dialog.getSimpleRulesComboBox().setModel(
				new DefaultComboBoxModel(searchField.getOperators().toArray()));

		enableSearchRuleAddControls(currentSelectedNodeAllowsAdd());
	}

	private void enableSearchRuleAddControls(final boolean enable) {
		// Only allow text field for binary operators
		boolean isBinary = (this.dialog.getSimpleRulesComboBox()
				.getSelectedObjects()[0]) instanceof ISearchBinaryOperator;

		this.dialog.getSimpleRulesTextField().setEnabled(enable && isBinary);
		if (!enable || !isBinary) {
			this.dialog.getSimpleRulesTextField().setText("");
		}
		this.dialog.getSimpleRulesAddButton().setEnabled(
				(enable && !StringUtils.isEmpty(this.dialog
						.getSimpleRulesTextField().getText()))
						|| (enable && !isBinary));

		this.dialog.getSimpleRulesComboBox().setEnabled(enable);
		this.dialog.getSimpleRulesList().setEnabled(enable);
	}

	private boolean currentSelectedNodeAllowsAdd() {
		// Get selected tree path
		TreePath path = this.dialog.getComplexRulesTree().getSelectionPath();

		if (path == null) {
			// No node selected -> allow add first rule
			return true;
		} else {
			// Get node selected
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) (path
					.getLastPathComponent());

			if (SearchRuleCreatorHelper.isSearchRule(node)) {
				// Don't allow add node to a search rule, nodes
				// must be added to logical nodes
				return false;
			} else if (node.getUserObject() instanceof NotLogicalSearchOperator
					&& !node.isLeaf()) {
				// If node is a NOT user can't add more simple rules if it has
				// already a child
				return false;
			}
		}
		return true;
	}
}
