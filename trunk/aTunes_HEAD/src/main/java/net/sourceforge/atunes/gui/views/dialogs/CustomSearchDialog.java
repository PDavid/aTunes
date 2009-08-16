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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.views.controls.CustomDialog;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * The Class CustomSearchDialog.
 */
public class CustomSearchDialog extends CustomDialog {

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
     * @param owner
     *            the owner
     */
    public CustomSearchDialog(JFrame owner) {
        super(owner, 600, 500);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setTitle(LanguageTool.getString("SEARCH"));
        add(getContent());
        GuiUtils.applyComponentOrientation(this);
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
        if (searchAtPanel == null) {
            searchAtPanel = new JPanel();
            searchAtPanel.setBorder(BorderFactory.createEtchedBorder());
            searchAtLabel = new JLabel(LanguageTool.getString("SEARCH_IN"));
            searchAtComboBox = new JComboBox();
            searchAtPanel.add(searchAtLabel);
            searchAtPanel.add(searchAtComboBox);
        }
        return searchAtPanel;
    }

    /**
     * Gets the simple rules panel.
     * 
     * @return the simple rules panel
     */
    public JPanel getSimpleRulesPanel() {
        if (simpleRulesPanel == null) {
            simpleRulesPanel = new JPanel(new GridBagLayout());
            simpleRulesPanel.setBorder(BorderFactory.createEtchedBorder());
            simpleRulesList = new JList();
            simpleRulesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            simpleRulesScrollPane = new JScrollPane(simpleRulesList);
            simpleRulesComboBox = new JComboBox();
            simpleRulesTextField = new JTextField();
            simpleRulesAddButton = new JButton(LanguageTool.getString("ADD"));

            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 5, 5, 5);

            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1;
            c.weighty = 1;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.BOTH;
            simpleRulesPanel.add(simpleRulesScrollPane, c);

            c.gridx = 0;
            c.gridy = 1;
            c.weightx = 0.3;
            c.weighty = 0;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            simpleRulesPanel.add(simpleRulesComboBox, c);

            c.gridx = 1;
            c.gridy = 1;
            c.weightx = 0.7;
            simpleRulesPanel.add(simpleRulesTextField, c);

            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 2;
            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            simpleRulesPanel.add(simpleRulesAddButton, c);
        }
        return simpleRulesPanel;
    }

    /**
     * Gets the complex rules panel.
     * 
     * @return the complex rules panel
     */
    public JPanel getComplexRulesPanel() {
        if (complexRulesPanel == null) {
            complexRulesPanel = new JPanel(new GridBagLayout());
            complexRulesPanel.setBorder(BorderFactory.createEtchedBorder());
            complexRulesTree = new JTree();

            complexRulesScrollPane = new JScrollPane(complexRulesTree);
            complexRulesAndButton = new JButton(LanguageTool.getString("AND"));
            complexRulesOrButton = new JButton(LanguageTool.getString("OR"));
            complexRulesNotButton = new JButton(LanguageTool.getString("NOT"));
            complexRulesRemoveButton = new JButton(LanguageTool.getString("REMOVE_FIELD"));

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
            complexRulesPanel.add(complexRulesAndButton, c);

            c.gridy = 1;
            complexRulesPanel.add(complexRulesOrButton, c);

            c.gridy = 2;
            complexRulesPanel.add(complexRulesNotButton, c);

            c.gridy = 3;
            complexRulesPanel.add(complexRulesRemoveButton, c);

        }
        return complexRulesPanel;
    }

    /**
     * Gets the advanced search panel.
     * 
     * @return the advanced search panel
     */
    private JPanel getAdvancedSearchPanel() {
        if (advancedSearchPanel == null) {
            advancedSearchPanel = new JPanel(new GridBagLayout());
            advancedSearchPanel.setBorder(BorderFactory.createEtchedBorder());
            advancedSearchCheckBox = new JCheckBox(LanguageTool.getString("ENABLE_ADVANCED_SEARCH"));
            advancedSearchTextField = new JTextField();
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(5, 5, 5, 5);
            advancedSearchPanel.add(advancedSearchCheckBox, c);
            c.gridy = 1;
            advancedSearchPanel.add(advancedSearchTextField, c);
        }
        return advancedSearchPanel;
    }

    /**
     * Gets the buttons panel.
     * 
     * @return the buttons panel
     */
    private JPanel getButtonsPanel() {
        if (buttonsPanel == null) {
            buttonsPanel = new JPanel();
            searchButton = new JButton(LanguageTool.getString("SEARCH"));
            cancelButton = new JButton(LanguageTool.getString("CANCEL"));
            buttonsPanel.add(searchButton);
            buttonsPanel.add(cancelButton);
        }
        return buttonsPanel;
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        new CustomSearchDialog(null).setVisible(true);
    }

    /**
     * Gets the search at combo box.
     * 
     * @return the searchAtComboBox
     */
    public JComboBox getSearchAtComboBox() {
        return searchAtComboBox;
    }

    /**
     * Gets the simple rules list.
     * 
     * @return the simpleRulesList
     */
    public JList getSimpleRulesList() {
        return simpleRulesList;
    }

    /**
     * Gets the cancel button.
     * 
     * @return the cancelButton
     */
    public JButton getCancelButton() {
        return cancelButton;
    }

    /**
     * Gets the simple rules combo box.
     * 
     * @return the simpleRulesComboBox
     */
    public JComboBox getSimpleRulesComboBox() {
        return simpleRulesComboBox;
    }

    /**
     * Gets the simple rules add button.
     * 
     * @return the simpleRulesAddButton
     */
    public JButton getSimpleRulesAddButton() {
        return simpleRulesAddButton;
    }

    /**
     * Gets the simple rules text field.
     * 
     * @return the simpleRulesTextField
     */
    public JTextField getSimpleRulesTextField() {
        return simpleRulesTextField;
    }

    /**
     * Gets the complex rules tree.
     * 
     * @return the complexRulesTree
     */
    public JTree getComplexRulesTree() {
        return complexRulesTree;
    }

    /**
     * Gets the complex rules and button.
     * 
     * @return the complexRulesAndButton
     */
    public JButton getComplexRulesAndButton() {
        return complexRulesAndButton;
    }

    /**
     * Gets the complex rules or button.
     * 
     * @return the complexRulesOrButton
     */
    public JButton getComplexRulesOrButton() {
        return complexRulesOrButton;
    }

    /**
     * Gets the complex rules not button.
     * 
     * @return the complexRulesNotButton
     */
    public JButton getComplexRulesNotButton() {
        return complexRulesNotButton;
    }

    /**
     * Gets the advanced search text field.
     * 
     * @return the advancedSearchTextField
     */
    public JTextField getAdvancedSearchTextField() {
        return advancedSearchTextField;
    }

    /**
     * Gets the complex rules remove button.
     * 
     * @return the complexRulesRemoveButton
     */
    public JButton getComplexRulesRemoveButton() {
        return complexRulesRemoveButton;
    }

    /**
     * Gets the advanced search check box.
     * 
     * @return the advancedSearchCheckBox
     */
    public JCheckBox getAdvancedSearchCheckBox() {
        return advancedSearchCheckBox;
    }

    /**
     * Gets the search button.
     * 
     * @return the searchButton
     */
    public JButton getSearchButton() {
        return searchButton;
    }

}
