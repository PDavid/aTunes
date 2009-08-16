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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.utils.GuiUtils;

/**
 * This is the dialog shown to select values.
 */
public class SelectorDialog extends JDialog {

    private static final long serialVersionUID = 8846024391499257859L;

    /** The selection. */
    String selection;

    /** The list. */
    JList list;

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        new SelectorDialog(null, "Test", new String[] { "Lang1", "Lang2", "Lang3", "Lang4", "Lang5", "Lang6", "Lang7", "Lang8", "Lang9", "Lang10", "Lang11", "Lang12" }, null);
    }

    /**
     * Instantiates a new selector dialog.
     * 
     * @param owner
     *            the owner
     * @param title
     *            the title
     * @param strings
     *            the strings
     * @param cellRenderer
     *            the cell renderer
     */
    public SelectorDialog(JFrame owner, String title, String[] strings, ListCellRenderer cellRenderer) {
        super(owner, title, true);
        setSize(250, 350);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());

        list = new JList(strings);
        list.setSelectedIndex(0);
        list.setFont(list.getFont().deriveFont(Font.PLAIN));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
        JScrollPane scrollPane = new JScrollPane(list);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, c);

        if (cellRenderer != null) {
            list.setCellRenderer(cellRenderer);
        }

        JButton okButton = new CustomButton(null, "OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selection = (String) list.getSelectedValue();
                setVisible(false);
            }
        });
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        panel.add(okButton, c);

        add(panel);
        GuiUtils.applyComponentOrientation(this);
    }

    /**
     * Gets the selection.
     * 
     * @return the selection
     */
    public String getSelection() {
        return selection;
    }
}
