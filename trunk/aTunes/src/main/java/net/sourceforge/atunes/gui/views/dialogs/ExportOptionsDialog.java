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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.CustomJFileChooser;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class ExportOptionsDialog.
 * 
 * @author fleax
 */
public class ExportOptionsDialog extends CustomModalDialog {

    private static final long serialVersionUID = 4403091324599627762L;

    /** RadioButton to export current navigator selection */
    private JRadioButton currentNavigatorSelectionRadioButton;

    /** RadioButton to export current play list selection */
    private JRadioButton currentPlayListSelectionRadioButton;

    /** The export location. */
    private CustomJFileChooser exportLocationFileChooser;

    /** The export button. */
    private JButton exportButton;

    /**
     * Setted to true when cancelled
     */
    boolean cancel;

    /**
     * Instantiates a new export options dialog.
     * 
     * @param parent
     *            the parent
     */
    public ExportOptionsDialog(JFrame parent) {
        super(parent, 400, 220, true);
        setResizable(false);
        setTitle(I18nUtils.getString("EXPORT"));
        setContent(getContent());
        GuiUtils.applyComponentOrientation(this);
        enableCloseActionWithEscapeKey();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                cancel = true;
            }
        });
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Export instructions
        JTextArea exportInstructions = new JTextArea(I18nUtils.getString("EXPORT_INSTRUCTIONS"));
        exportInstructions.setWrapStyleWord(true);
        exportInstructions.setLineWrap(true);
        exportInstructions.setEditable(false);
        exportInstructions.setOpaque(false);
        exportInstructions.setBorder(BorderFactory.createEmptyBorder());

        // Export Type
        JPanel exportTypePanel = new JPanel(new GridLayout(2, 1));
        currentNavigatorSelectionRadioButton = new JRadioButton(I18nUtils.getString("CURRENT_NAVIGATOR_SELECTION"));
        currentPlayListSelectionRadioButton = new JRadioButton(I18nUtils.getString("CURRENT_PLAYLIST_SELECTION"));
        ButtonGroup group = new ButtonGroup();
        group.add(currentNavigatorSelectionRadioButton);
        group.add(currentPlayListSelectionRadioButton);
        exportTypePanel.add(currentNavigatorSelectionRadioButton);
        exportTypePanel.add(currentPlayListSelectionRadioButton);
        currentNavigatorSelectionRadioButton.setSelected(true);

        // Export location
        JPanel exportLocationPanel = new JPanel(new BorderLayout());
        JLabel locationLabel = new JLabel(I18nUtils.getString("LOCATION"));
        exportLocationPanel.add(locationLabel, BorderLayout.WEST);
        exportLocationFileChooser = new CustomJFileChooser(this, 20, JFileChooser.DIRECTORIES_ONLY);
        exportLocationPanel.add(exportLocationFileChooser, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new GridLayout(1, 2, 5, 0));
        buttons.setOpaque(false);
        exportButton = new CustomButton(null, I18nUtils.getString("EXPORT"));
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        JButton cancelButton = new CustomButton(null, I18nUtils.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancel = true;
                setVisible(false);
            }
        });
        buttons.add(exportButton);
        buttons.add(cancelButton);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        container.add(exportInstructions, c);

        c.gridy = 1;
        c.weighty = 0.5;
        c.fill = GridBagConstraints.VERTICAL;
        container.add(exportTypePanel, c);

        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 15, 0, 15);
        container.add(exportLocationPanel, c);

        c.gridy = 3;
        c.anchor = GridBagConstraints.EAST;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(10, 0, 0, 0);
        container.add(buttons, c);
        return container;
    }

    /**
     * Gets the export button.
     * 
     * @return the export button
     */
    public JButton getExportButton() {
        return exportButton;
    }

    /**
     * Gets the export location.
     * 
     * @return the export location
     */
    public String getExportLocation() {
        return exportLocationFileChooser.getResult();
    }

    public void startDialog() {
        cancel = false;
        super.setVisible(true);
    }

    /**
     * @return the cancel
     */
    public boolean isCancel() {
        return cancel;
    }

    /**
     * Returns <code>true</code> if user selected to export current navigator
     * selection, <code>false</code> otherwise (then selected current play list)
     * 
     * @return
     */
    public boolean exportNavigatorSelection() {
        return currentNavigatorSelectionRadioButton.isSelected();
    }
}
