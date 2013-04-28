/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class StatsDialog.
 */
public final class StatsDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = -7822497871738495670L;

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

	/**
	 * Instantiates a new stats dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public StatsDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 750, 750, controlsBuilder);
	}

	@Override
	public void initialize() {
		setTitle(StringUtils.getString(I18nUtils.getString("STATS"), " - ",
				Constants.APP_NAME, " ", Constants.VERSION.toShortString()));
		setResizable(false);
		add(getContent(getLookAndFeel()));
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}

	/**
	 * Gets the albums chart.
	 * 
	 * @return the albums chart
	 */
	public JLabel getAlbumsChart() {
		return this.albumsChart;
	}

	/**
	 * Gets the albums table.
	 * 
	 * @return the albums table
	 */
	public JTable getAlbumsTable() {
		return this.albumsTable;
	}

	/**
	 * Gets the artists chart.
	 * 
	 * @return the artists chart
	 */
	public JLabel getArtistsChart() {
		return this.artistsChart;
	}

	/**
	 * Gets the artists table.
	 * 
	 * @return the artists table
	 */
	public JTable getArtistsTable() {
		return this.artistsTable;
	}

	/**
	 * Gets the content.
	 * 
	 * @param iLookAndFeel
	 * 
	 * @return the content
	 */
	private JPanel getContent(final ILookAndFeel iLookAndFeel) {
		JPanel panel = new JPanel(new BorderLayout());

		// General stats
		JPanel generalPanel = new JPanel(new GridBagLayout());
		this.generalTable = iLookAndFeel.getTable();
		JScrollPane generalScrollPane = getControlsBuilder().createScrollPane(
				this.generalTable);
		this.generalChart = new JLabel();
		this.generalChart.setHorizontalAlignment(SwingConstants.CENTER);

		// Songs stats
		JPanel songPanel = new JPanel(new GridBagLayout());
		this.songsTable = iLookAndFeel.getTable();
		JScrollPane songsScrollPane = getControlsBuilder().createScrollPane(
				this.songsTable);
		this.songsChart = new JLabel();
		this.songsChart.setHorizontalAlignment(SwingConstants.CENTER);

		// Albums stats
		JPanel albumPanel = new JPanel(new GridBagLayout());
		this.albumsTable = iLookAndFeel.getTable();
		JScrollPane albumsScrollPane = getControlsBuilder().createScrollPane(
				this.albumsTable);
		this.albumsChart = new JLabel();
		this.albumsChart.setHorizontalAlignment(SwingConstants.CENTER);

		// Artists stats
		JPanel artistPanel = new JPanel(new GridBagLayout());
		this.artistsTable = iLookAndFeel.getTable();
		JScrollPane artistsScrollPane = getControlsBuilder().createScrollPane(
				this.artistsTable);
		this.artistsChart = new JLabel();
		this.artistsChart.setHorizontalAlignment(SwingConstants.CENTER);

		arrangePanel(generalPanel, generalScrollPane, songPanel,
				songsScrollPane, albumPanel, albumsScrollPane, artistPanel,
				artistsScrollPane);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(I18nUtils.getString("GENERAL"), generalPanel);
		tabbedPane.addTab(I18nUtils.getString("SONG"), songPanel);
		tabbedPane.addTab(I18nUtils.getString("ALBUM"), albumPanel);
		tabbedPane.addTab(I18nUtils.getString("ARTIST"), artistPanel);
		panel.add(tabbedPane, BorderLayout.CENTER);
		return panel;
	}

	/**
	 * @param generalPanel
	 * @param generalScrollPane
	 * @param songPanel
	 * @param songsScrollPane
	 * @param albumPanel
	 * @param albumsScrollPane
	 * @param artistPanel
	 * @param artistsScrollPane
	 */
	private void arrangePanel(final JPanel generalPanel,
			final JScrollPane generalScrollPane, final JPanel songPanel,
			final JScrollPane songsScrollPane, final JPanel albumPanel,
			final JScrollPane albumsScrollPane, final JPanel artistPanel,
			final JScrollPane artistsScrollPane) {
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
		generalPanel.add(this.generalChart, c);

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
		songPanel.add(this.songsChart, c);

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		albumPanel.add(albumsScrollPane, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		albumPanel.add(this.albumsChart, c);

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		artistPanel.add(artistsScrollPane, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.weighty = 0;
		artistPanel.add(this.artistsChart, c);
	}

	/**
	 * Gets the general chart.
	 * 
	 * @return the general chart
	 */
	public JLabel getGeneralChart() {
		return this.generalChart;
	}

	/**
	 * Gets the general table.
	 * 
	 * @return the general table
	 */
	public JTable getGeneralTable() {
		return this.generalTable;
	}

	/**
	 * Gets the songs chart.
	 * 
	 * @return the songs chart
	 */
	public JLabel getSongsChart() {
		return this.songsChart;
	}

	/**
	 * Gets the songs table.
	 * 
	 * @return the songs table
	 */
	public JTable getSongsTable() {
		return this.songsTable;
	}
}
