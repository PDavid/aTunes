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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.dialogs.CustomSearchDialog;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.ISearchField;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Controller for custom search dialog
 * 
 * @author alex
 * 
 */
public final class CustomSearchController extends
		AbstractSimpleController<CustomSearchDialog> {

	private ISearchHandler searchHandler;

	private IDialogFactory dialogFactory;

	private ComplexRuleTreeBuilder complexRuleTreeBuilder;

	private IControlsBuilder controlsBuilder;

	private ILookAndFeelManager lookAndFeelManager;

	private SearchRuleCreatorLogic logic;

	/**
	 * @param searchHandler
	 */
	public void setSearchHandler(final ISearchHandler searchHandler) {
		this.searchHandler = searchHandler;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param complexRuleTreeBuilder
	 */
	public void setComplexRuleTreeBuilder(
			final ComplexRuleTreeBuilder complexRuleTreeBuilder) {
		this.complexRuleTreeBuilder = complexRuleTreeBuilder;
	}

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * Initializes controller
	 */
	public void initialize() {
		setComponentControlled(this.dialogFactory
				.newDialog(CustomSearchDialog.class));
		this.logic = new SearchRuleCreatorLogic(getComponentControlled(),
				this.complexRuleTreeBuilder);
		addBindings();
	}

	void setSearchFieldsList(final List<ISearchField<?, ?>> beans) {
		// sort search fields by name
		Collections.sort(beans, new Comparator<ISearchField<?, ?>>() {
			@Override
			public int compare(final ISearchField<?, ?> o1,
					final ISearchField<?, ?> o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		int selected = 0;
		getComponentControlled().getSimpleRulesList().setModel(
				new DefaultComboBoxModel(beans.toArray()));
		getComponentControlled().getSimpleRulesList()
				.setSelectedIndex(selected);
		getComponentControlled().getSimpleRulesComboBox().setModel(
				new DefaultComboBoxModel(beans.get(selected).getOperators()
						.toArray()));
		getComponentControlled().enableComplexRuleButtons(false);
		getComponentControlled().getSimpleRulesAddButton().setEnabled(false);
	}

	/**
	 * Shows dialog
	 */
	void showSearchDialog() {
		getComponentControlled().setVisible(true);
	}

	/**
	 * Shows dialog so user can create a query
	 * 
	 * @param title
	 * @param text
	 * @return query created or null if user canceled
	 */
	ISearchNode showSearchDialogForQueryCreation(final String title,
			final String text) {
		this.logic.disableSearch();
		getComponentControlled().setQueryCreationOnly(title, text);
		getComponentControlled().setVisible(true);
		return this.complexRuleTreeBuilder
				.getSearchTree(getComponentControlled());
	}

	/**
	 * Shows dialog so user can edit a query
	 * 
	 * @param query
	 * @param title
	 * @param text
	 * @return
	 */
	ISearchNode showSearchDialogForQueryEdition(final ISearchNode query,
			final String title, final String text) {
		this.complexRuleTreeBuilder.setQuery(query, getComponentControlled());
		getComponentControlled().setQueryCreationOnly(title, text);
		getComponentControlled().setVisible(true);
		return !getComponentControlled().isCanceled() ? this.complexRuleTreeBuilder
				.getSearchTree(getComponentControlled()) : null;
	}

	/**
	 * Invokes a search with rule defined in dialog.
	 */
	private void search() {
		if (!getComponentControlled().isQueryCreationOnly()) {
			try {
				ISearchNode query = this.complexRuleTreeBuilder
						.getSearchTree(getComponentControlled());
				Collection<IAudioObject> result = this.searchHandler
						.search(query);
				// If no matches found show a message
				if (result.isEmpty()) {
					this.dialogFactory.newDialog(IMessageDialog.class)
							.showMessage(
									I18nUtils.getString("NO_MATCHES_FOUND"));
				}
				// Hide search dialog
				getComponentControlled().setVisible(false);
				// Show result
				this.searchHandler.showSearchResults(query, result);
			} catch (IllegalArgumentException e) {
				this.dialogFactory.newDialog(IErrorDialog.class)
						.showErrorDialog(
								I18nUtils.getString("INVALID_SEARCH_RULE"));
			} catch (IllegalStateException e) {
				this.dialogFactory.newDialog(IErrorDialog.class)
						.showErrorDialog(
								I18nUtils.getString("INVALID_SEARCH_RULE"));
			}
		} else {
			// Hide search dialog
			getComponentControlled().setVisible(false);
		}
	}

	@Override
	public void addBindings() {
		getComponentControlled().getComplexRulesTree().setCellRenderer(
				this.controlsBuilder
						.getTreeCellRenderer(new RuleTreeCellRenderer()));

		getComponentControlled().getSimpleRulesList().setRenderer(
				this.lookAndFeelManager.getCurrentLookAndFeel()
						.getListCellRenderer(new RuleListCellRenderer()));

		getComponentControlled().getSimpleRulesComboBox().setRenderer(
				this.lookAndFeelManager.getCurrentLookAndFeel()
						.getListCellRenderer(new RuleComboListCellRenderer()));

		getComponentControlled().getSimpleRulesComboBox().addItemListener(
				new ItemListener() {

					@Override
					public void itemStateChanged(final ItemEvent event) {
						if (event.getStateChange() == ItemEvent.SELECTED) {
							CustomSearchController.this.logic
									.adjustSearchRuleTextField(event);
						}
					}
				});

		getComponentControlled().getComplexRulesTree().setModel(
				new DefaultTreeModel(null));

		getComponentControlled().getSimpleRulesTextField().addKeyListener(
				new KeyAdapter() {

					@Override
					public void keyTyped(final KeyEvent event) {
						GuiUtils.callInEventDispatchThreadLater(new Runnable() {
							@Override
							public void run() {
								CustomSearchController.this.logic
										.adjustAddButton();
							}
						});
					}
				});
		getComponentControlled().getSimpleRulesTextField().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent event) {
						CustomSearchController.this.logic
								.pressRuleTextFieldEnter();
					}
				});
		getComponentControlled().getSimpleRulesAddButton().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent event) {
						CustomSearchController.this.logic.pressAddButton();
					}
				});

		getComponentControlled().getComplexRulesAndButton().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent arg0) {
						CustomSearchController.this.logic.pressAndButton();
					}
				});

		getComponentControlled().getComplexRulesOrButton().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						CustomSearchController.this.logic.pressOrButton();
					}
				});

		getComponentControlled().getComplexRulesNotButton().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						CustomSearchController.this.logic.pressNotButton();
					}
				});

		getComponentControlled().getComplexRulesRemoveButton()
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						CustomSearchController.this.logic.pressRemoveButton();
					}
				});

		getComponentControlled().getSearchButton().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						// Pressed Search button
						search();
					}
				});

		getComponentControlled().getComplexRulesTree()
				.addTreeSelectionListener(new TreeSelectionListener() {

					@Override
					public void valueChanged(final TreeSelectionEvent e) {
						CustomSearchController.this.logic.treeItemSelected();
					}
				});

		getComponentControlled().getCancelButton().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						// Pressed cancel button
						getComponentControlled().setCanceled(true);
						getComponentControlled().setVisible(false);
					}
				});

		getComponentControlled().getSimpleRulesList().addItemListener(
				new ItemListener() {

					@Override
					public void itemStateChanged(final ItemEvent event) {
						if (event.getStateChange() == ItemEvent.SELECTED) {
							ISearchField<?, ?> searchField = (ISearchField<?, ?>) event
									.getItem();
							CustomSearchController.this.logic
									.ruleSelected(searchField);
						}
					}
				});
	}
}
