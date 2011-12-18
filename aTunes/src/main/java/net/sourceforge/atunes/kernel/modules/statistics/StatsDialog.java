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

package net.sourceforge.atunes.kernel.modules.statistics;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class StatsDialog.
 */
public final class StatsDialog extends AbstractCustomDialog {

    private static final long serialVersionUID = -7822497871738495670L;

    /** The tabbed pane. */
    private JTabbedPane tabbedPane;

    /** The general table. */
    private JTable generalTable;

    /** The artists table. */
    private JTable artistsTable;

    /** The albums table. */
    private JTable albumsTable;

    /** The songs table. */
    private JTable songsTable;

    /** The general chart. */
    private JLabel generalChart;

    /** The artists chart. */
    private JLabel artistsChart;

    /** The albums chart. */
    private JLabel albumsChart;

    /** The songs chart. */
    private JLabel songsChart;
    
    private ILookAndFeelManager lookAndFeelManager;

    /**
     * Instantiates a new stats dialog.
     * @param frame
     * @param lookAndFeelManager
     */
    public StatsDialog(IFrame frame, ILookAndFeelManager lookAndFeelManager) {
        super(frame, 750, 750, true, CloseAction.DISPOSE, lookAndFeelManager.getCurrentLookAndFeel());
        this.lookAndFeelManager = lookAndFeelManager;
    }
    
    /**
     * Initializes dialog
     */
    public void initialize() {
        setTitle(StringUtils.getString(I18nUtils.getString("STATS"), " - ", Constants.APP_NAME, " ", Constants.VERSION.toShortString()));
        setResizable(false);
        add(getContent(lookAndFeelManager.getCurrentLookAndFeel()));
    }

    /**
     * Gets the albums chart.
     * 
     * @return the albums chart
     */
    public JLabel getAlbumsChart() {
        return albumsChart;
    }

    /**
     * Gets the albums table.
     * 
     * @return the albums table
     */
    public JTable getAlbumsTable() {
        return albumsTable;
    }

    /**
     * Gets the artists chart.
     * 
     * @return the artists chart
     */
    public JLabel getArtistsChart() {
        return artistsChart;
    }

    /**
     * Gets the artists table.
     * 
     * @return the artists table
     */
    public JTable getArtistsTable() {
        return artistsTable;
    }

    /**
     * Gets the content.
     * @param iLookAndFeel 
     * 
     * @return the content
     */
    private JPanel getContent(ILookAndFeel iLookAndFeel) {
        JPanel panel = new JPanel(new BorderLayout());

        // General stats
        JPanel generalPanel = new JPanel(new GridBagLayout());
        generalTable = iLookAndFeel.getTable();
        JScrollPane generalScrollPane = iLookAndFeel.getTableScrollPane(generalTable);
        generalChart = new JLabel();
        generalChart.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);
        generalPanel.add(generalScrollPane, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        generalPanel.add(generalChart, c);

        // Songs stats
        JPanel songPanel = new JPanel(new GridBagLayout());
        songsTable = iLookAndFeel.getTable();
        JScrollPane songsScrollPane = iLookAndFeel.getTableScrollPane(songsTable);
        songsChart = new JLabel();
        songsChart.setHorizontalAlignment(SwingConstants.CENTER);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);
        songPanel.add(songsScrollPane, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        songPanel.add(songsChart, c);

        // Albums stats
        JPanel albumPanel = new JPanel(new GridBagLayout());
        albumsTable = iLookAndFeel.getTable();
        JScrollPane albumsScrollPane = iLookAndFeel.getTableScrollPane(albumsTable);
        albumsChart = new JLabel();
        albumsChart.setHorizontalAlignment(SwingConstants.CENTER);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        albumPanel.add(albumsScrollPane, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        albumPanel.add(albumsChart, c);

        // Artists stats
        JPanel artistPanel = new JPanel(new GridBagLayout());
        artistsTable = iLookAndFeel.getTable();
        JScrollPane artistsScrollPane = iLookAndFeel.getTableScrollPane(artistsTable);
        artistsChart = new JLabel();
        artistsChart.setHorizontalAlignment(SwingConstants.CENTER);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        artistPanel.add(artistsScrollPane, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        artistPanel.add(artistsChart, c);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab(I18nUtils.getString("GENERAL"), generalPanel);
        tabbedPane.addTab(I18nUtils.getString("SONG"), songPanel);
        tabbedPane.addTab(I18nUtils.getString("ALBUM"), albumPanel);
        tabbedPane.addTab(I18nUtils.getString("ARTIST"), artistPanel);
        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Gets the general chart.
     * 
     * @return the general chart
     */
    public JLabel getGeneralChart() {
        return generalChart;
    }

    /**
     * Gets the general table.
     * 
     * @return the general table
     */
    public JTable getGeneralTable() {
        return generalTable;
    }

    /**
     * Gets the songs chart.
     * 
     * @return the songs chart
     */
    public JLabel getSongsChart() {
        return songsChart;
    }

    /**
     * Gets the songs table.
     * 
     * @return the songs table
     */
    public JTable getSongsTable() {
        return songsTable;
    }
}
