/*
 * aTunes 2.2.0-SNAPSHOT
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.ISelectorDialog;

/**
 * This is the dialog shown to select values.
 */
public final class SelectorDialog extends AbstractCustomDialog implements ISelectorDialog {

    private static final long serialVersionUID = 8846024391499257859L;

    /** The selection. */
    private String selection;

    private ILookAndFeelManager lookAndFeelManager;
    
    private ListCellRenderer cellRenderer;
    
    private JList list;
    
    /**
     * Instantiates a new selector dialog.
     * @param frame
     */
    public SelectorDialog(IFrame frame) {
        super(frame, 250, 350);
    }
    
    /**
     * @param cellRenderer
     */
    @Override
	public void setCellRenderer(ListCellRenderer cellRenderer) {
		this.cellRenderer = cellRenderer;
	}

    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
    
    /**
     * @param options
     */
    @Override
	public void setOptions(String[] options) {
        list.setListData(options);
        list.setSelectedIndex(0);
	}
    
    @Override
    public void initialize() {
        JPanel panel = new JPanel(new GridBagLayout());
        list = lookAndFeelManager.getCurrentLookAndFeel().getList();
        list.setFont(list.getFont().deriveFont(Font.PLAIN));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
        JScrollPane scrollPane = lookAndFeelManager.getCurrentLookAndFeel().getListScrollPane(list);
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

        JButton okButton = new JButton("OK");
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
    @Override
	public String getSelection() {
        return selection;
    }
}
