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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class CustomSearchDialog.
 */
public final class CustomSearchDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = -7447583622785097610L;

	/** The simple rules combo. */
	private JComboBox simpleRulesList;

	/** The simple rules combo box. */
	private JComboBox simpleRulesComboBox;

	/** The simple rules text field. */
	private JTextField simpleRulesTextField;

	/** The simple rules add button. */
	private JButton simpleRulesAddButton;

	/** The complex rules tree. */
	private JTree complexRulesTree;

	/** The complex rules and button. */
	private JButton complexRulesAndButton;

	/** The complex rules or button. */
	private JButton complexRulesOrButton;

	/** The complex rules not button. */
	private JButton complexRulesNotButton;

	/** The complex rules remove button. */
	private JButton complexRulesRemoveButton;

	/** The search button. */
	private JButton searchButton;

	/** The cancel button. */
	private JButton cancelButton;

	private JLabel searchHelp;

	private boolean queryCreationOnly;

	private boolean canceled;

	/**
	 * Instantiates a new custom search dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public CustomSearchDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 700, 400, controlsBuilder);
	}

	/**
	 * @param canceled
	 */
	public void setCanceled(final boolean canceled) {
		this.canceled = canceled;
	}

	/**
	 * @return
	 */
	public boolean isCanceled() {
		return this.canceled;
	}

	@Override
	public void initialize() {
		setResizable(false);
		setTitle(I18nUtils.getString("SEARCH"));
		add(getContent());
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	private JPanel getContent() {
		JPanel panel = new JPanel(new GridBagLayout());

		this.searchHelp = new JLabel(I18nUtils.getString("SEARCH_HELP"));

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 10, 10);

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(this.searchHelp, c);

		c.insets = new Insets(5, 5, 5, 5);
		c.gridy++;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(getComplexRulesPanel(), c);

		c.gridy++;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(getSimpleRulesPanel(), c);

		c.gridy++;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		panel.add(getButtonsPanel(), c);
		return panel;
	}

	/**
	 * Gets the simple rules panel.
	 * 
	 * @return the simple rules panel
	 */
	private JPanel getSimpleRulesPanel() {
		JPanel simpleRulesPanel = new JPanel(new GridBagLayout());
		this.simpleRulesList = new JComboBox();
		this.simpleRulesComboBox = new JComboBox();
		this.simpleRulesTextField = getControlsBuilder().createTextField();
		this.simpleRulesAddButton = new JButton(I18nUtils.getString("ADD"));

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 10, 5, 10);

		c.gridx = 0;
		c.gridy = 0;
		simpleRulesPanel.add(this.simpleRulesList, c);

		c.gridx = 1;
		c.weightx = 0.3;
		c.fill = GridBagConstraints.HORIZONTAL;
		simpleRulesPanel.add(this.simpleRulesComboBox, c);

		c.gridx = 2;
		c.weightx = 0.7;
		simpleRulesPanel.add(this.simpleRulesTextField, c);

		c.gridx = 3;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		simpleRulesPanel.add(this.simpleRulesAddButton, c);

		return simpleRulesPanel;
	}

	/**
	 * Gets the complex rules panel.
	 * 
	 * @return the complex rules panel
	 */
	private JPanel getComplexRulesPanel() {
		JPanel complexRulesPanel = new JPanel(new GridBagLayout());

		this.complexRulesTree = getControlsBuilder().createTree(false);
		this.complexRulesTree.setToggleClickCount(0);

		JScrollPane complexRulesScrollPane = getControlsBuilder()
				.createScrollPane(this.complexRulesTree);
		this.complexRulesAndButton = new JButton(I18nUtils.getString("AND"));
		this.complexRulesOrButton = new JButton(I18nUtils.getString("OR"));
		this.complexRulesNotButton = new JButton(I18nUtils.getString("NOT"));
		this.complexRulesRemoveButton = new JButton(
				I18nUtils.getString("REMOVE_FIELD"));

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 4;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		complexRulesPanel.add(complexRulesScrollPane, c);

		c.gridx = 1;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		complexRulesPanel.add(this.complexRulesAndButton, c);

		c.gridy = 1;
		complexRulesPanel.add(this.complexRulesOrButton, c);

		c.gridy = 2;
		complexRulesPanel.add(this.complexRulesNotButton, c);

		c.gridy = 3;
		complexRulesPanel.add(this.complexRulesRemoveButton, c);
		return complexRulesPanel;
	}

	/**
	 * Gets the buttons panel.
	 * 
	 * @return the buttons panel
	 */
	private JPanel getButtonsPanel() {
		JPanel buttonsPanel = new JPanel();
		this.searchButton = new JButton(I18nUtils.getString("SEARCH"));
		this.cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		buttonsPanel.add(this.searchButton);
		buttonsPanel.add(this.cancelButton);
		return buttonsPanel;
	}

	/**
	 * Gets the simple rules list.
	 * 
	 * @return the simpleRulesList
	 */
	public JComboBox getSimpleRulesList() {
		return this.simpleRulesList;
	}

	/**
	 * Gets the cancel button.
	 * 
	 * @return the cancelButton
	 */
	public JButton getCancelButton() {
		return this.cancelButton;
	}

	/**
	 * Gets the simple rules combo box.
	 * 
	 * @return the simpleRulesComboBox
	 */
	public JComboBox getSimpleRulesComboBox() {
		return this.simpleRulesComboBox;
	}

	/**
	 * Gets the simple rules add button.
	 * 
	 * @return the simpleRulesAddButton
	 */
	public JButton getSimpleRulesAddButton() {
		return this.simpleRulesAddButton;
	}

	/**
	 * Gets the simple rules text field.
	 * 
	 * @return the simpleRulesTextField
	 */
	public JTextField getSimpleRulesTextField() {
		return this.simpleRulesTextField;
	}

	/**
	 * Gets the complex rules tree.
	 * 
	 * @return the complexRulesTree
	 */
	public JTree getComplexRulesTree() {
		return this.complexRulesTree;
	}

	/**
	 * Gets the complex rules and button.
	 * 
	 * @return the complexRulesAndButton
	 */
	public JButton getComplexRulesAndButton() {
		return this.complexRulesAndButton;
	}

	/**
	 * Gets the complex rules or button.
	 * 
	 * @return the complexRulesOrButton
	 */
	public JButton getComplexRulesOrButton() {
		return this.complexRulesOrButton;
	}

	/**
	 * Gets the complex rules not button.
	 * 
	 * @return the complexRulesNotButton
	 */
	public JButton getComplexRulesNotButton() {
		return this.complexRulesNotButton;
	}

	/**
	 * Gets the complex rules remove button.
	 * 
	 * @return the complexRulesRemoveButton
	 */
	public JButton getComplexRulesRemoveButton() {
		return this.complexRulesRemoveButton;
	}

	/**
	 * Gets the search button.
	 * 
	 * @return the searchButton
	 */
	public JButton getSearchButton() {
		return this.searchButton;
	}

	/**
	 * Enables or disables complex rules controls
	 * 
	 * @param enable
	 */
	public void enableComplexRuleButtons(final boolean enable) {
		boolean complexRuleSelected = getComplexRulesTree().getSelectionPath() != null;
		getComplexRulesAndButton().setEnabled(enable && complexRuleSelected);
		getComplexRulesOrButton().setEnabled(enable && complexRuleSelected);
		getComplexRulesNotButton().setEnabled(enable && complexRuleSelected);
		getComplexRulesRemoveButton().setEnabled(enable && complexRuleSelected);
	}

	/**
	 * Configures dialog to only create queries
	 * 
	 * @param title
	 * @param text
	 */
	public void setQueryCreationOnly(final String title, final String text) {
		this.queryCreationOnly = true;
		setTitle(title);
		this.searchHelp.setText(text);
		this.searchButton.setText(I18nUtils.getString("OK"));
	}

	/**
	 * @return if dialog is configured only for query creation
	 */
	public boolean isQueryCreationOnly() {
		return this.queryCreationOnly;
	}

}
