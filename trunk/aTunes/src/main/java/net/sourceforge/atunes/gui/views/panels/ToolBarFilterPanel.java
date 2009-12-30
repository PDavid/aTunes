/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.utils.I18nUtils;

public class ToolBarFilterPanel extends JPanel {

    private static final long serialVersionUID = 1801321624657098000L;

    private PopUpButton filterButton;
    private JTextField filterTextField;
    private JButton clearFilterButton;

    public ToolBarFilterPanel() {
        super(new GridBagLayout());
        addContent();
    }

    private void addContent() {
        filterButton = new PopUpButton(I18nUtils.getString("FILTER"), PopUpButton.BOTTOM_RIGHT);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        add(filterButton, c);
        filterTextField = new JTextField(20);
        filterTextField.setToolTipText(I18nUtils.getString("FILTER_TEXTFIELD_TOOLTIP"));
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        add(filterTextField, c);
        clearFilterButton = new JButton(Images.getImage(Images.UNDO));
        clearFilterButton.setToolTipText(I18nUtils.getString("CLEAR_FILTER_BUTTON_TOOLTIP"));
        c.gridx = 2;
        add(clearFilterButton, c);
    }

    /**
     * @return the filterButton
     */
    public PopUpButton getFilterButton() {
        return filterButton;
    }

    /**
     * @return the filterTextField
     */
    public JTextField getFilterTextField() {
        return filterTextField;
    }

    /**
     * @return the clearFilterButton
     */
    public JButton getClearFilterButton() {
        return clearFilterButton;
    }

}
