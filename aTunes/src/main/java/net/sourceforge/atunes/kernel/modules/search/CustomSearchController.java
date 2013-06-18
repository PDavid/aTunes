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

import java.awt.Component;
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
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.dialogs.CustomSearchDialog;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.ILogicalSearchOperator;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.ISearchField;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.model.ISearchOperator;
import net.sourceforge.atunes.model.ISearchRule;
import net.sourceforge.atunes.model.ITreeCellRendererCode;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

final class CustomSearchController extends
		AbstractSimpleController<CustomSearchDialog> {

	private final ISearchHandler searchHandler;

	private final IDialogFactory dialogFactory;

	private final ComplexRuleTreeBuilder treeBuilder;

	private final IControlsBuilder controlsBuilder;

	private final ILogicalSearchOperator notLogicalSearchOperator;

	/**
	 * @param dialog
	 * @param searchHandler
	 * @param dialogFactory
	 * @param controlsBuilder
	 * @param treeBuilder
	 * @param notLogicalSearchOperator
	 */
	CustomSearchController(final CustomSearchDialog dialog,
			final ISearchHandler searchHandler,
			final IDialogFactory dialogFactory,
			IControlsBuilder controlsBuilder,
			ComplexRuleTreeBuilder treeBuilder,
			ILogicalSearchOperator notLogicalSearchOperator) {
		super(dialog);
		this.searchHandler = searchHandler;
		this.dialogFactory = dialogFactory;
		this.controlsBuilder = controlsBuilder;
		this.treeBuilder = treeBuilder;
		this.notLogicalSearchOperator = notLogicalSearchOperator;
		addBindings();
	}

	void setSearchFieldsList(final List<ISearchField<?, ?>> beans) {
		// sort search fields by name
		Collections.sort(beans, new Comparator<ISearchField<?, ?>>() {
			@Override
			public int compare(ISearchField<?, ?> o1, ISearchField<?, ?> o2) {
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
	 * Invokes a search with rule defined in dialog.
	 */
	void search() {
		try {
			ISearchNode query = this.treeBuilder
					.getSearchTree(getComponentControlled());
			Collection<IAudioObject> result = this.searchHandler.search(query);
			// If no matches found show a message
			if (result.isEmpty()) {
				this.dialogFactory.newDialog(IMessageDialog.class).showMessage(
						I18nUtils.getString("NO_MATCHES_FOUND"));
			} else {
				// Hide search dialog
				getComponentControlled().setVisible(false);
				// Show result
				this.searchHandler.showSearchResults(result);
			}
		} catch (IllegalArgumentException e) {
			this.dialogFactory.newDialog(IErrorDialog.class).showErrorDialog(
					I18nUtils.getString("INVALID_SEARCH_RULE"));
		} catch (IllegalStateException e) {
			this.dialogFactory.newDialog(IErrorDialog.class).showErrorDialog(
					I18nUtils.getString("INVALID_SEARCH_RULE"));
		}
	}

	@Override
	public void addBindings() {
		getComponentControlled()
				.getComplexRulesTree()
				.setCellRenderer(
						this.controlsBuilder
								.getTreeCellRenderer(new ITreeCellRendererCode<JComponent, DefaultMutableTreeNode>() {
									@Override
									public JComponent getComponent(
											JComponent superComponent,
											JTree tree,
											DefaultMutableTreeNode value,
											boolean isSelected,
											boolean expanded, boolean leaf,
											int row, boolean isHasFocus) {
										if (value.getUserObject() instanceof ILogicalSearchOperator) {
											((JLabel) superComponent)
													.setText(((ILogicalSearchOperator) value
															.getUserObject())
															.getDescription());
										} else if (value.getUserObject() instanceof ISearchRule) {
											ISearchRule rule = (ISearchRule) value
													.getUserObject();
											((JLabel) superComponent).setText(StringUtils
													.getString(
															rule.getField()
																	.getName(),
															" ",
															rule.getOperator()
																	.getDescription(),
															" ", rule
																	.getValue()));
										}
										return superComponent;
									}
								}));
		getComponentControlled().getSimpleRulesList().setRenderer(
				new DefaultListCellRenderer() {
					/**
					 * 
					 */
					private static final long serialVersionUID = -3292080447818613013L;

					@Override
					public Component getListCellRendererComponent(JList list,
							Object value, int index, boolean isSelected,
							boolean cellHasFocus) {
						Component c = super.getListCellRendererComponent(list,
								value, index, isSelected, cellHasFocus);
						((JLabel) c).setText(((ISearchField<?, ?>) value)
								.getName());
						return c;
					}
				});

		getComponentControlled().getSimpleRulesComboBox().setRenderer(
				new DefaultListCellRenderer() {
					/**
					 * 
					 */
					private static final long serialVersionUID = -3292080447818613013L;

					@Override
					public Component getListCellRendererComponent(JList list,
							Object value, int index, boolean isSelected,
							boolean cellHasFocus) {
						Component c = super.getListCellRendererComponent(list,
								value, index, isSelected, cellHasFocus);
						((JLabel) c).setText(((ISearchOperator<?>) value)
								.getDescription());
						return c;
					}
				});
		getComponentControlled().getComplexRulesTree().setModel(
				new DefaultTreeModel(null));

		getComponentControlled().getSimpleRulesTextField().addKeyListener(
				new KeyAdapter() {

					@Override
					public void keyTyped(KeyEvent event) {
						GuiUtils.callInEventDispatchThreadLater(new Runnable() {
							@Override
							public void run() {
								getComponentControlled()
										.getSimpleRulesAddButton()
										.setEnabled(
												!StringUtils
														.isEmpty(getComponentControlled()
																.getSimpleRulesTextField()
																.getText()));
							}
						});
					}
				});
		getComponentControlled().getSimpleRulesTextField().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent event) {
						if (!StringUtils.isEmpty(getComponentControlled()
								.getSimpleRulesTextField().getText())) {
							// Pressed Add button
							treeBuilder
									.createSimpleRule(getComponentControlled());
						}
					}
				});
		getComponentControlled().getSimpleRulesAddButton().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent event) {
						// Pressed Add button
						treeBuilder.createSimpleRule(getComponentControlled());
					}
				});

		getComponentControlled().getComplexRulesAndButton().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						// Pressed AND button
						treeBuilder.addAndOperator(getComponentControlled());
					}
				});

		getComponentControlled().getComplexRulesOrButton().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// Pressed OR button
						treeBuilder.addOrOperator(getComponentControlled());
					}
				});

		getComponentControlled().getComplexRulesNotButton().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// Pressed NOT button
						treeBuilder.addNotOperator(getComponentControlled());
					}
				});

		getComponentControlled().getComplexRulesRemoveButton()
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// Pressed Remove button
						treeBuilder.removeRuleNode(getComponentControlled());
					}
				});

		getComponentControlled().getSearchButton().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// Pressed Search button
						search();
					}
				});

		getComponentControlled().getComplexRulesTree()
				.addTreeSelectionListener(
						new ComplexTreeSelectionListener(
								getComponentControlled(),
								getComponentControlled().getComplexRulesTree(),
								notLogicalSearchOperator));

		getComponentControlled().getCancelButton().addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// Pressed cancel button
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
							getComponentControlled().getSimpleRulesComboBox()
									.setModel(
											new DefaultComboBoxModel(
													searchField.getOperators()
															.toArray()));
						}
					}
				});
	}
}
