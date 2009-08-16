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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * A custom file chooser with text field and button that opens the chooser.
 */
public class CustomJFileChooser extends JPanel {

    private static final long serialVersionUID = 4713483251093570020L;

    JTextField textField;
    String result;

    /**
     * Instantiates a new custom file chooser.
     * 
     * @param parent
     *            the parent
     * @param length
     *            the length
     * @param type
     *            the type
     */
    public CustomJFileChooser(final Component parent, int length, final int type) {
        super(new GridBagLayout());
        // Use user home by default
        final File defaultFolder = new File(SystemProperties.USER_HOME);
        textField = new JTextField(defaultFolder.getAbsolutePath(), length);
        JButton button = new JButton("...");
        button.setSize(textField.getPreferredSize().height, textField.getPreferredSize().height);
        button.setPreferredSize(button.getSize());

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser chooser = new JFileChooser(textField.getText());
                chooser.setFileSelectionMode(type);
                chooser.setSelectedFile(defaultFolder);
                if (type == JFileChooser.DIRECTORIES_ONLY) {
                    chooser.setDialogTitle(LanguageTool.getString("SELECT_FOLDER"));
                }
                if (chooser.showDialog(parent, null) == JFileChooser.APPROVE_OPTION) {
                    result = chooser.getSelectedFile().getAbsolutePath();
                    textField.setText(result);
                } else {
                    result = null;
                }
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        // c.insets = new Insets(0, 10, 0, 0);
        add(textField, c);
        c.gridx = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 2, 0, 0);
        add(button, c);
    }

    /**
     * Gets the result.
     * 
     * @return the result
     */
    public String getResult() {
        result = textField.getText();
        return result;
    }

    /**
     * Sets the text.
     * 
     * @param text
     *            the new text
     */
    public void setText(String text) {
        textField.setText(text);
        result = text;
    }

}
