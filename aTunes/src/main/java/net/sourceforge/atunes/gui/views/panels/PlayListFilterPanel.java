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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * The Class PlayListFilterPanel.
 */
public class PlayListFilterPanel extends JPanel {

    private static final long serialVersionUID = 795619869506188088L;

    /** The filter label. */
    private JLabel filterLabel;

    /** The filter text field. */
    private JTextField filterTextField;

    /** The clear filter button. */
    private JLabel clearFilterButton;

    /**
     * Instantiates a new play list filter panel.
     */
    public PlayListFilterPanel() {
        super(new GridBagLayout());
        addContent();
    }

    /**
     * Adds the content.
     */
    private void addContent() {
        filterLabel = new JLabel(LanguageTool.getString("FILTER"));
        filterTextField = new JTextField();
        filterTextField.setToolTipText(LanguageTool.getString("FILTER_TEXTFIELD_TOOLTIP"));
        clearFilterButton = new JLabel(ImageLoader.UNDO);
        clearFilterButton.setToolTipText(LanguageTool.getString("CLEAR_FILTER_BUTTON_TOOLTIP"));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 5, 0, 5);
        add(filterLabel, c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 5, 1, 5);
        add(filterTextField, c);
        c.gridx = 2;
        c.weightx = 0;
        c.insets = new Insets(0, 5, 1, 5);
        add(clearFilterButton, c);
    }

    /**
     * Gets the clear filter button.
     * 
     * @return the clear filter button
     */
    public JLabel getClearFilterButton() {
        return clearFilterButton;
    }

    /**
     * Gets the filter text field.
     * 
     * @return the filter text field
     */
    public JTextField getFilterTextField() {
        return filterTextField;
    }

    /**
     * Sets focus to filterTextField.
     */
    public void setFocusToTextField() {
        filterTextField.requestFocusInWindow();
    }
}
