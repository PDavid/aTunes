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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * The Class AddRadioDialog.
 */
public class AddRadioDialog extends CustomModalDialog {

    private static final long serialVersionUID = 7295438534550341824L;

    /** The radio. */
    Radio radio;

    /** The name text field. */
    JTextField nameTextField;

    /** The url text field. */
    JTextField urlTextField;

    /** The label text field. */
    JTextField labelTextField;

    /**
     * Instantiates a new adds the radio dialog.
     * 
     * @param owner
     *            the owner
     */
    public AddRadioDialog(JFrame owner) {
        super(owner, 500, 200, true);
        setTitle(LanguageTool.getString("ADD_RADIO"));
        setResizable(false);
        setContent(getContent());
        GuiUtils.applyComponentOrientation(this);
        enableDisposeActionWithEscapeKey();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String args[]) {
        new AddRadioDialog(null).setVisible(true);
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(new GridBagLayout());

        JLabel nameLabel = new JLabel(LanguageTool.getString("NAME"));
        nameTextField = new JTextField();
        JLabel urlLabel = new JLabel(LanguageTool.getString("URL"));
        urlTextField = new JTextField();
        JLabel labelLabel = new JLabel(LanguageTool.getString("LABEL"));
        labelTextField = new JTextField();

        JButton okButton = new JButton(LanguageTool.getString("OK"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                radio = new Radio(nameTextField.getText(), urlTextField.getText(), labelTextField.getText());
                AddRadioDialog.this.dispose();
            }
        });
        JButton cancelButton = new JButton(LanguageTool.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddRadioDialog.this.dispose();
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 10, 5, 10);
        panel.add(nameLabel, c);
        c.gridx = 2;
        c.weightx = 1;
        panel.add(nameTextField, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0;
        panel.add(urlLabel, c);
        c.gridx = 2;
        c.weightx = 1;
        panel.add(urlTextField, c);
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0;
        panel.add(labelLabel, c);
        c.gridx = 2;
        c.weightx = 1;
        panel.add(labelTextField, c);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        c.fill = GridBagConstraints.NONE;
        c.weightx = -1;
        panel.add(new JLabel(ImageLoader.getImage(ImageLoader.RADIO)), c);

        JPanel auxPanel = new JPanel();
        auxPanel.add(okButton);
        auxPanel.add(cancelButton);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 3;
        c.insets = new Insets(0, 0, 0, 0);
        panel.add(auxPanel, c);

        return panel;
    }

    /**
     * Gets the radio.
     * 
     * @return the radio
     */
    public Radio getRadio() {
        return radio;
    }

}
