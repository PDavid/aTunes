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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomModalDialog;
import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.CustomTextArea;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class RepositorySelectionInfoDialog.
 */
public final class RepositorySelectionInfoDialog extends AbstractCustomModalDialog {

    private static final long serialVersionUID = 4369595555397951445L;

    /**
     * Instantiates a new repository selection info dialog.
     * 
     * @param owner
     *            the owner
     */
    public RepositorySelectionInfoDialog(JFrame owner) {
        super(owner, 400, 250, true);
        setResizable(false);
        setTitle(I18nUtils.getString("REPOSITORY_SELECTION_INFO"));
        setContent();
        GuiUtils.applyComponentOrientation(this);
        enableCloseActionWithEscapeKey();
    }

    /**
     * Sets the content.
     */
    private void setContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel icon = new JLabel(Images.getImage(Images.APP_LOGO_90));
        JTextArea text = new CustomTextArea(I18nUtils.getString("REPOSITORY_SELECTION_INFO_TEXT"));
        text.setOpaque(false);
        text.setEditable(false);
        text.setWrapStyleWord(true);
        text.setLineWrap(true);
        text.setBorder(BorderFactory.createEmptyBorder());
        CustomButton button = new CustomButton(null, I18nUtils.getString("OK"));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(10, 10, 10, 10);
        panel.add(icon, c);
        c.gridx = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        panel.add(text, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(button, c);
        setContent(panel);
    }
}
