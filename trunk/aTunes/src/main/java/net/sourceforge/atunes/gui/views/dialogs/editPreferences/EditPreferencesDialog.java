/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import net.sourceforge.atunes.gui.lookandfeel.AbstractListCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomModalFrame;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * General characteristics of the preference dialog
 */
public final class EditPreferencesDialog extends AbstractCustomModalFrame {

    private static final long serialVersionUID = -4759149194433605946L;

    private JButton ok;
    private JButton cancel;
    private JPanel options;
    private JList list;
    private AbstractPreferencesPanel[] panels;

    /**
     * Instantiates a new edits the preferences dialog.
     * 
     * @param owner
     *            the owner
     */
    public EditPreferencesDialog(JFrame owner) {
        super(owner, GuiUtils.getComponentWidthForResolution(1280, 1000), GuiUtils.getComponentHeightForResolution(1024, 700));
        setResizable(true);
        setTitle(I18nUtils.getString("PREFERENCES"));
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
        list.setCellRenderer(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getListCellRenderer(new PreferencesListCellRendererCode()));
        JScrollPane scrollPane = new JScrollPane(list);
        // Force minimum width of scroll pane to show items
        scrollPane.setMinimumSize(new Dimension(200, 0));
        options = new JPanel();
        ok = new CustomButton(null, I18nUtils.getString("OK"));
        cancel = new CustomButton(null, I18nUtils.getString("CANCEL"));
        JPanel auxPanel = new JPanel();
        auxPanel.add(ok);
        auxPanel.add(cancel);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 1;
        c.fill = GridBagConstraints.VERTICAL;
        c.insets = new Insets(5, 5, 0, 5);
        container.add(scrollPane, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.insets = new Insets(5, 5, 0, 5);
        c.fill = GridBagConstraints.BOTH;
        container.add(options, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
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
    public void setListModel(ListModel listModel) {
        list.setModel(listModel);
    }

    /**
     * Sets the panels.
     * 
     * @param panels
     *            the new panels
     */
    public void setPanels(AbstractPreferencesPanel[] panels) {
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

    private static class PreferencesListCellRendererCode extends AbstractListCellRendererCode {
        @Override
        public Component getComponent(Component superComponent, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) superComponent;
            AbstractPreferencesPanel p = (AbstractPreferencesPanel) value;
            label.setText(p.getTitle());
            label.setIcon(p.getIcon());
            return label;
        }
    }

}
