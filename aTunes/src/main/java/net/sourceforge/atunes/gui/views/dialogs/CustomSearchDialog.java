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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class CustomSearchDialog.
 */
public final class CustomSearchDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = -7447583622785097610L;

	/** The search at panel. */
	private JPanel searchAtPanel;

	/** The search at label. */
	private JLabel searchAtLabel;

	/** The search at combo box. */
	private JComboBox searchAtComboBox;

	/** The simple rules panel. */
	private JPanel simpleRulesPanel;

	/** The simple rules scroll pane. */
	private JScrollPane simpleRulesScrollPane;

	/** The simple rules list. */
	private JList simpleRulesList;

	/** The simple rules combo box. */
	private JComboBox simpleRulesComboBox;

	/** The simple rules text field. */
	private JTextField simpleRulesTextField;

	/** The simple rules add button. */
	private JButton simpleRulesAddButton;

	/** The complex rules panel. */
	private JPanel complexRulesPanel;

	/** The complex rules tree. */
	private JTree complexRulesTree;

	/** The complex rules scroll pane. */
	private JScrollPane complexRulesScrollPane;

	/** The complex rules and button. */
	private JButton complexRulesAndButton;

	/** The complex rules or button. */
	private JButton complexRulesOrButton;

	/** The complex rules not button. */
	private JButton complexRulesNotButton;

	/** The complex rules remove button. */
	private JButton complexRulesRemoveButton;

	/** The advanced search panel. */
	private JPanel advancedSearchPanel;

	/** The advanced search check box. */
	private JCheckBox advancedSearchCheckBox;

	/** The advanced search text field. */
	private JTextField advancedSearchTextField;

	/** The buttons panel. */
	private JPanel buttonsPanel;

	/** The search button. */
	private JButton searchButton;

	/** The cancel button. */
	private JButton cancelButton;

	/**
	 * Instantiates a new custom search dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public CustomSearchDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 600, 500, controlsBuilder);
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

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(getSearchAtPanel(), c);

		c.gridy = 1;
		c.weightx = 0.5;
		c.weighty = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(getSimpleRulesPanel(), c);

		c.gridx = 1;
		panel.add(getComplexRulesPanel(), c);

		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 2;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(getAdvancedSearchPanel(), c);

		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		panel.add(getButtonsPanel(), c);
		return panel;
	}

	/**
	 * Gets the search at panel.
	 * 
	 * @return the search at panel
	 */
	private JPanel getSearchAtPanel() {
		if (this.searchAtPanel == null) {
			this.searchAtPanel = new JPanel();
			this.searchAtPanel.setBorder(BorderFactory.createEtchedBorder());
			this.searchAtLabel = new JLabel(I18nUtils.getString("SEARCH_IN"));
			this.searchAtComboBox = new JComboBox();
			this.searchAtPanel.add(this.searchAtLabel);
			this.searchAtPanel.add(this.searchAtComboBox);
		}
		return this.searchAtPanel;
	}

	/**
	 * Gets the simple rules panel.
	 * 
	 * @return the simple rules panel
	 */
	public JPanel getSimpleRulesPanel() {
		if (this.simpleRulesPanel == null) {
			this.simpleRulesPanel = new JPanel(new GridBagLayout());
			this.simpleRulesPanel.setBorder(BorderFactory.createEtchedBorder());
			this.simpleRulesList = getLookAndFeel().getList();
			this.simpleRulesList
					.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.simpleRulesScrollPane = getLookAndFeel().getListScrollPane(
					this.simpleRulesList);
			this.simpleRulesComboBox = new JComboBox();
			this.simpleRulesTextField = getControlsBuilder().createTextField();
			this.simpleRulesAddButton = new JButton(I18nUtils.getString("ADD"));

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(5, 5, 5, 5);

			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.gridwidth = 2;
			c.fill = GridBagConstraints.BOTH;
			this.simpleRulesPanel.add(this.simpleRulesScrollPane, c);

			c.gridx = 0;
			c.gridy = 1;
			c.weightx = 0.3;
			c.weighty = 0;
			c.gridwidth = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			this.simpleRulesPanel.add(this.simpleRulesComboBox, c);

			c.gridx = 1;
			c.gridy = 1;
			c.weightx = 0.7;
			this.simpleRulesPanel.add(this.simpleRulesTextField, c);

			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 2;
			c.weightx = 0;
			c.fill = GridBagConstraints.NONE;
			this.simpleRulesPanel.add(this.simpleRulesAddButton, c);
		}
		return this.simpleRulesPanel;
	}

	/**
	 * Gets the complex rules panel.
	 * 
	 * @return the complex rules panel
	 */
	public JPanel getComplexRulesPanel() {
		if (this.complexRulesPanel == null) {
			this.complexRulesPanel = new JPanel(new GridBagLayout());
			this.complexRulesPanel
					.setBorder(BorderFactory.createEtchedBorder());
			this.complexRulesTree = new JTree();

			this.complexRulesScrollPane = getLookAndFeel().getTreeScrollPane(
					this.complexRulesTree);
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
			this.complexRulesPanel.add(this.complexRulesScrollPane, c);

			c.gridx = 1;
			c.gridheight = 1;
			c.weightx = 0;
			c.weighty = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			this.complexRulesPanel.add(this.complexRulesAndButton, c);

			c.gridy = 1;
			this.complexRulesPanel.add(this.complexRulesOrButton, c);

			c.gridy = 2;
			this.complexRulesPanel.add(this.complexRulesNotButton, c);

			c.gridy = 3;
			this.complexRulesPanel.add(this.complexRulesRemoveButton, c);

		}
		return this.complexRulesPanel;
	}

	/**
	 * Gets the advanced search panel.
	 * 
	 * @return the advanced search panel
	 */
	private JPanel getAdvancedSearchPanel() {
		if (this.advancedSearchPanel == null) {
			this.advancedSearchPanel = new JPanel(new GridBagLayout());
			this.advancedSearchPanel.setBorder(BorderFactory
					.createEtchedBorder());
			this.advancedSearchCheckBox = new JCheckBox(
					I18nUtils.getString("ENABLE_ADVANCED_SEARCH"));
			this.advancedSearchTextField = getControlsBuilder()
					.createTextField();
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(5, 5, 5, 5);
			this.advancedSearchPanel.add(this.advancedSearchCheckBox, c);
			c.gridy = 1;
			this.advancedSearchPanel.add(this.advancedSearchTextField, c);
		}
		return this.advancedSearchPanel;
	}

	/**
	 * Gets the buttons panel.
	 * 
	 * @return the buttons panel
	 */
	private JPanel getButtonsPanel() {
		if (this.buttonsPanel == null) {
			this.buttonsPanel = new JPanel();
			this.searchButton = new JButton(I18nUtils.getString("SEARCH"));
			this.cancelButton = new JButton(I18nUtils.getString("CANCEL"));
			this.buttonsPanel.add(this.searchButton);
			this.buttonsPanel.add(this.cancelButton);
		}
		return this.buttonsPanel;
	}

	/**
	 * Gets the search at combo box.
	 * 
	 * @return the searchAtComboBox
	 */
	public JComboBox getSearchAtComboBox() {
		return this.searchAtComboBox;
	}

	/**
	 * Gets the simple rules list.
	 * 
	 * @return the simpleRulesList
	 */
	public JList getSimpleRulesList() {
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
	 * Gets the advanced search text field.
	 * 
	 * @return the advancedSearchTextField
	 */
	public JTextField getAdvancedSearchTextField() {
		return this.advancedSearchTextField;
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
	 * Gets the advanced search check box.
	 * 
	 * @return the advancedSearchCheckBox
	 */
	public JCheckBox getAdvancedSearchCheckBox() {
		return this.advancedSearchCheckBox;
	}

	/**
	 * Gets the search button.
	 * 
	 * @return the searchButton
	 */
	public JButton getSearchButton() {
		return this.searchButton;
	}

}
