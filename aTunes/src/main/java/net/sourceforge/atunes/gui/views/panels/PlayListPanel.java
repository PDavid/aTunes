/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;

/**
 * The playlist panel.
 * 
 * @author fleax
 */
public final class PlayListPanel extends JPanel {

    private static final long serialVersionUID = -1886323054505118303L;

    /** The play list tab panel. */
    private PlayListTabPanel playListTabPanel;

    /** The play list table. */
    private PlayListTable playListTable;

    /** The play list table scroll. */
    private JScrollPane playListTableScroll;

    /**
     * Instantiates a new play list panel.
     */
    public PlayListPanel() {
        super(new BorderLayout());
        addContent();
    }

    /**
     * Adds the content.
     */
    private void addContent() {
        playListTabPanel = new PlayListTabPanel();
        playListTable = new PlayListTable();
        playListTableScroll = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableScrollPane(playListTable);

        add(playListTabPanel, BorderLayout.NORTH);
        add(playListTableScroll, BorderLayout.CENTER);
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
