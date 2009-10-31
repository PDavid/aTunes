/*
 * aTunes 2.0.0-SNAPSHOT
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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;

/**
 * The Class PlayListPanel.
 * 
 * @author fleax
 */
public final class PlayListPanel extends JPanel {

    private static final long serialVersionUID = -1886323054505118303L;

    /** The play list tab panel. */
    private PlayListTabPanel playListTabPanel;

    /** The play list filter. */
    private PlayListFilterPanel playListFilter;

    /** The play list table. */
    private PlayListTable playListTable;

    /** The play list table scroll. */
    private JScrollPane playListTableScroll;

    /** The play list controls. */
    private PlayListControlsPanel playListControls;

    /**
     * Instantiates a new play list panel.
     */
    public PlayListPanel() {
        super(new GridBagLayout());
        addContent();
    }

    /**
     * Adds the content.
     */
    private void addContent() {
        playListTabPanel = new PlayListTabPanel();
        playListFilter = new PlayListFilterPanel();
        // Hide by default
        playListFilter.setVisible(false);
        playListTable = new PlayListTable();
        playListTableScroll = new JScrollPane(playListTable);
        //playListTableScroll.setBorder(BorderFactory.createEmptyBorder());
        playListControls = new PlayListControlsPanel(playListTable);

        JPanel auxPanel = new JPanel(new BorderLayout());
        auxPanel.add(playListTabPanel, BorderLayout.NORTH);
        auxPanel.add(playListTableScroll, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(1, 1, 0, 1);
        add(playListFilter, c);
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(auxPanel, c);
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 1, 0, 1);
        add(playListControls, c);
    }

    /**
     * Gets the play list controls.
     * 
     * @return the play list controls
     */
    public PlayListControlsPanel getPlayListControls() {
        return playListControls;
    }

    /**
     * Gets the play list filter.
     * 
     * @return the play list filter
     */
    public PlayListFilterPanel getPlayListFilter() {
        return playListFilter;
    }

    /**
     * Gets the play list table.
     * 
     * @return the play list table
     */
    public PlayListTable getPlayListTable() {
        return playListTable;
    }

    /**
     * Gets the play list table scroll.
     * 
     * @return the play list table scroll
     */
    public JScrollPane getPlayListTableScroll() {
        return playListTableScroll;
    }

    /**
     * Gets the play list tab panel.
     * 
     * @return the playListTabPanel
     */
    public PlayListTabPanel getPlayListTabPanel() {
        return playListTabPanel;
    }

}
