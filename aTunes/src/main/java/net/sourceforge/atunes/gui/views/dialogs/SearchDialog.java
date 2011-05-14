/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomModalDialog;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.kernel.modules.internetsearch.Search;
import net.sourceforge.atunes.kernel.modules.internetsearch.SearchFactory;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class SearchDialog.
 */
public final class SearchDialog extends AbstractCustomModalDialog {

    private static final long serialVersionUID = 89888215541058798L;

    /** The result. */
    private Search result;

    /** The set as default. */
    private boolean setAsDefault;

    /** The set as default check box. */
    private JCheckBox setAsDefaultCheckBox;

    /**
     * Instantiates a new search dialog.
     * 
     * @param owner
     *            the owner
     */
    public SearchDialog(JFrame owner) {
        super(owner, 300, 300, true);
        setContent();
        GuiUtils.applyComponentOrientation(this);
        enableCloseActionWithEscapeKey();
    }

    /**
     * The main method.
     * 
     * @param args
     *            the args
     */
    public static void main(String[] args) {
        new SearchDialog(null).setVisible(true);
    }

    /**
     * Gets the result.
     * 
     * @return the result
     */
    public Search getResult() {
        return result;
    }

    /**
     * Checks if is sets the as default.
     * 
     * @return true, if is sets the as default
     */
    public boolean isSetAsDefault() {
        return setAsDefault;
    }

    /**
     * Sets the content.
     */
    private void setContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel text = new JLabel(StringUtils.getString(I18nUtils.getString("SEARCH_AT"), "..."));
        text.setFont(text.getFont().deriveFont(Font.PLAIN));
        final JList list = new JList(SearchFactory.getSearches().toArray());
        list.setSelectedIndex(0);
        list.setOpaque(false);

        setAsDefaultCheckBox = new JCheckBox(I18nUtils.getString("SET_AS_DEFAULT"));
        setAsDefaultCheckBox.setOpaque(false);
        setAsDefaultCheckBox.setFont(setAsDefaultCheckBox.getFont().deriveFont(Font.PLAIN));
        setAsDefaultCheckBox.setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollPane = new JScrollPane(list);
        CustomButton okButton = new CustomButton(null, I18nUtils.getString("OK"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result = (Search) list.getSelectedValue();
                setAsDefault = setAsDefaultCheckBox.isSelected();
                setVisible(false);
            }
        });
        CustomButton cancelButton = new CustomButton(null, I18nUtils.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result = null;
                setVisible(false);
            }
        });

        JPanel auxPanel = new JPanel();
        auxPanel.setOpaque(false);
        auxPanel.add(okButton);
        auxPanel.add(cancelButton);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(15, 15, 0, 0);
        panel.add(text, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 15, 0, 15);
        panel.add(scrollPane, c);
        c.gridy = 2;
        c.weightx = 0;
        c.insets = new Insets(0, 0, 0, 0);
        panel.add(setAsDefaultCheckBox, c);
        c.gridy = 3;
        c.weightx = 1;
        panel.add(auxPanel, c);

        setContent(panel);
    }

    /**
     * Sets the sets the as default visible.
     * 
     * @param setAsDefaultVisible
     *            the new sets the as default visible
     */
    public void setSetAsDefaultVisible(boolean setAsDefaultVisible) {
        setAsDefaultCheckBox.setVisible(setAsDefaultVisible);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Dialog#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean b) {
        setAsDefaultCheckBox.setSelected(false);
        super.setVisible(b);
    }
}
