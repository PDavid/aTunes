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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.CustomDialog;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.LanguageTool;

import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 * The search results dialog.
 */
public class SearchResultsDialog extends CustomDialog {

    private static final long serialVersionUID = 7219089044964361102L;

    /** Scroll pane for table. */
    private JScrollPane tableScrollPane;

    /** Table to show search results. */
    private JTable searchResultsTable;

    /** Button to show element info. */
    private JButton showElementInfo;

    /** Button to add selected results to current play list. */
    private JButton addToCurrentPlayList;

    /** Button to add selected results to a new play list. */
    private JButton addToNewPlayList;

    /**
     * Instantiates a new search results dialog.
     * 
     * @param owner
     *            the owner
     */
    public SearchResultsDialog(JFrame owner) {
        super(owner, GuiUtils.getComponentWidthForResolution(1280, 900), 600);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(LanguageTool.getString("SEARCH_RESULTS"));
        add(getContent());

        enableDisposeActionWithEscapeKey();
        GuiUtils.applyComponentOrientation(this);
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(new GridBagLayout());
        searchResultsTable = new JTable();
        searchResultsTable.setDefaultRenderer(String.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 8864358368269039209L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
                Component c = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5);

                GuiUtils.applyComponentOrientation((JLabel) c);

                return c;
            }
        });
        tableScrollPane = new JScrollPane(searchResultsTable);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        panel.add(tableScrollPane, c);

        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 1;
        panel.add(getButtonsPanel(), c);

        return panel;
    }

    /**
     * Gets the buttons panel.
     * 
     * @return the buttons panel
     */
    private JPanel getButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        showElementInfo = new JButton(LanguageTool.getString("INFO"), ImageLoader.getImage(ImageLoader.INFO));
        addToCurrentPlayList = new JButton(LanguageTool.getString("ADD_TO_PLAYLIST"), ImageLoader.getImage(ImageLoader.ADD));
        addToNewPlayList = new JButton(LanguageTool.getString("ADD_TO_NEW_PLAYLIST"), ImageLoader.getImage(ImageLoader.ADD));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        buttonsPanel.add(showElementInfo, c);
        c.gridx = 1;
        buttonsPanel.add(addToCurrentPlayList, c);
        c.gridx = 2;
        buttonsPanel.add(addToNewPlayList, c);
        return buttonsPanel;
    }

    /**
     * Gets the search results table.
     * 
     * @return the searchResultsTable
     */
    public JTable getSearchResultsTable() {
        return searchResultsTable;
    }

    /**
     * Gets the show element info.
     * 
     * @return the showElementInfo
     */
    public JButton getShowElementInfo() {
        return showElementInfo;
    }

    /**
     * Gets the add to current play list.
     * 
     * @return the addToCurrentPlayList
     */
    public JButton getAddToCurrentPlayList() {
        return addToCurrentPlayList;
    }

    /**
     * Gets the add to new play list.
     * 
     * @return the addToNewPlayList
     */
    public JButton getAddToNewPlayList() {
        return addToNewPlayList;
    }

}
