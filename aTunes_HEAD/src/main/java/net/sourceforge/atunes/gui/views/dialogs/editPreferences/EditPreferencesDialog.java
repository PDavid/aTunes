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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.LanguageTool;

// General characteristics of the preference dialog

/**
 * The Class EditPreferencesDialog.
 */
public class EditPreferencesDialog extends CustomModalDialog {

    private static final long serialVersionUID = -4759149194433605946L;

    private JButton ok;
    private JButton cancel;
    private JPanel options;
    private JList list;
    private PreferencesPanel[] panels;

    /**
     * Instantiates a new edits the preferences dialog.
     * 
     * @param owner
     *            the owner
     */
    public EditPreferencesDialog(JFrame owner) {
        super(owner, GuiUtils.getComponentWidthForResolution(1280, 1000), GuiUtils.getComponentHeightForResolution(1024, 700), true);
        setResizable(true);
        setTitle(LanguageTool.getString("PREFERENCES"));
        add(getContent());
        GuiUtils.applyComponentOrientation(this);
        enableCloseActionWithEscapeKey();
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        new EditPreferencesDialog(null).setVisible(true);
    }

    /**
     * Gets the audio scrobbler panel.
     * 
     * @return the audio scrobbler panel
     */
    public ContextPanel getContextPanel() {
        return (ContextPanel) panels[6];
    }

    /**
     * Gets the cancel.
     * 
     * @return the cancel
     */
    public JButton getCancel() {
        return cancel;
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        list = new JList();
        JScrollPane scrollPane = new JScrollPane(list);
        // Force minimum width of scroll pane to show items
        scrollPane.setMinimumSize(new Dimension(200,0));
        options = new JPanel();
        ok = new CustomButton(null, LanguageTool.getString("OK"));
        cancel = new CustomButton(null, LanguageTool.getString("CANCEL"));
        JPanel auxPanel = new JPanel();
        auxPanel.add(ok);
        auxPanel.add(cancel);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 0, 5);
        container.add(scrollPane, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.6;
        c.insets = new Insets(5, 5, 0, 5);
        container.add(options, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(10, 0, 10, 10);
        container.add(auxPanel, c);

        return container;
    }

    /**
     * Gets the list.
     * 
     * @return the list
     */
    public JList getList() {
        return list;
    }

    /**
     * Gets the ok.
     * 
     * @return the ok
     */
    public JButton getOk() {
        return ok;
    }

    /**
     * Sets the list model.
     * 
     * @param listModel
     *            the new list model
     */
    public void setListModel(DefaultListModel listModel) {
        list.setModel(listModel);
    }

    /**
     * Sets the panels.
     * 
     * @param panels
     *            the new panels
     */
    public void setPanels(PreferencesPanel[] panels) {
        this.panels = panels;
        options.setLayout(new CardLayout());
        for (int i = 0; i < panels.length; i++) {
            options.add(Integer.toString(i), panels[i]);
        }
        GuiUtils.applyComponentOrientation(this);
    }

    /**
     * Show panel.
     * 
     * @param index
     *            the index
     */
    public void showPanel(int index) {
        ((CardLayout) options.getLayout()).show(options, Integer.toString(index));
    }

}
