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

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;

/**
 * Controller for statistics dialog
 * 
 * @author alex
 * 
 */
public final class StatsDialogController extends
		AbstractSimpleController<StatsDialog> implements
		ILookAndFeelChangeListener {

	private IStatisticsHandler statisticsHandler;

	private IRepositoryHandler repositoryHandler;

	private ILookAndFeelManager lookAndFeelManager;

	private IUnknownObjectChecker unknownObjectChecker;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param statisticsHandler
	 */
	public void setStatisticsHandler(final IStatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * Instantiates a new stats dialog controller.
	 * 
	 * @param frame
	 */
	StatsDialogController(final StatsDialog frame) {
		super(frame);
	}

	/**
	 * Initializes controller
	 */
	public void initialize() {
		this.lookAndFeelManager.addLookAndFeelChangeListener(this);
	}

	/**
	 * Gets the data set.
	 * 
	 * @param list
	 *            the list
	 * 
	 * @return the data set
	 */
	private DefaultCategoryDataset getDataSet(final List<?> list) {
		DefaultCategoryDataset result = new DefaultCategoryDataset();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Object[] obj = (Object[]) list.get(i);
				result.setValue((Integer) obj[1], "", (String) obj[0]);
			}
		}
		return result;
	}

	/**
	 * Sets the albums table.
	 */
	private void setAlbumsTable() {
		List<Object[]> albums = getMostPlayedAlbumsInRanking(-1);
		if (albums != null) {
			String[] headers = new String[] { I18nUtils.getString("ALBUM"),
					I18nUtils.getString("TIMES_PLAYED"), "%" };
			Object[][] content = new Object[albums.size()][3];
			for (int i = 0; i < albums.size(); i++) {
				content[i][0] = albums.get(i)[0];
				content[i][1] = albums.get(i)[1];
				if (this.statisticsHandler.getTotalAudioObjectsPlayed() != -1) {
					content[i][2] = StringUtils.toString(
							100.0
									* (float) ((Integer) albums.get(i)[1])
									/ this.statisticsHandler
											.getTotalAudioObjectsPlayed(), 2);
				} else {
					content[i][2] = 0;
				}
			}
			setTable(getComponentControlled().getAlbumsTable(), headers,
					content);
		}
	}

	/**
	 * Gets the most played albums in ranking.
	 * 
	 * @param n
	 *            the n
	 * 
	 * @return the most played albums in ranking
	 */
	private List<Object[]> getMostPlayedAlbumsInRanking(final int n) {
		List<Object[]> result = new ArrayList<Object[]>();
		List<IAlbum> albums = this.statisticsHandler.getMostPlayedAlbums(n);
		List<Integer> count = this.statisticsHandler
				.getMostPlayedAlbumsCount(n);
		if (albums != null) {
			for (int i = 0; i < albums.size(); i++) {
				Object[] obj = new Object[2];
				obj[0] = albums.get(i).toString();
				obj[1] = count.get(i);
				result.add(obj);
			}
		}
		return result;
	}

	/**
	 * Sets the artists table.
	 */

	private void setArtistsTable() {
		List<Object[]> artists = getMostPlayedArtistsInRanking(-1);
		if (artists != null) {
			String[] headers = new String[] { I18nUtils.getString("ARTIST"),
					I18nUtils.getString("TIMES_PLAYED"), "%" };
			Object[][] content = new Object[artists.size()][3];
			for (int i = 0; i < artists.size(); i++) {
				content[i][0] = artists.get(i)[0];
				content[i][1] = artists.get(i)[1];
				if (this.statisticsHandler.getTotalAudioObjectsPlayed() != -1) {
					content[i][2] = StringUtils.toString(
							100.0
									* (float) ((Integer) artists.get(i)[1])
									/ this.statisticsHandler
											.getTotalAudioObjectsPlayed(), 2);
				} else {
					content[i][2] = 0;
				}
			}
			setTable(getComponentControlled().getArtistsTable(), headers,
					content);
		}
	}

	/**
	 * Gets the most played artists in ranking.
	 * 
	 * @param n
	 *            the n
	 * 
	 * @return the most played artists in ranking
	 */
	private List<Object[]> getMostPlayedArtistsInRanking(final int n) {
		List<Object[]> result = new ArrayList<Object[]>();
		List<IArtist> artists = this.statisticsHandler.getMostPlayedArtists(n);
		List<Integer> count = this.statisticsHandler
				.getMostPlayedArtistsCount(n);
		if (artists != null) {
			for (int i = 0; i < artists.size(); i++) {
				if (artists.get(i) != null) {
					Object[] obj = new Object[2];
					obj[0] = artists.get(i).toString();
					obj[1] = count.get(i);
					result.add(obj);
				}
			}
		}
		return result;
	}

	/**
	 * Sets the general table.
	 */

	private void setGeneralTable() {
		int different = this.statisticsHandler.getDifferentAudioObjectsPlayed();
		int total = this.repositoryHandler.getAudioFilesList().size();
		if (total != 0) {
			String[] headers = new String[] { " ",
					I18nUtils.getString("COUNT"), "%" };
			Object[][] content = new Object[2][3];
			content[0] = new Object[3];
			content[0][0] = I18nUtils.getString("SONGS_PLAYED");
			content[0][1] = different;
			content[0][2] = StringUtils.toString((float) different
					/ (float) total * 100, 2);
			content[1] = new Object[3];
			content[1][0] = I18nUtils.getString("SONGS_NEVER_PLAYED");
			content[1][1] = total - different;
			content[1][2] = StringUtils.toString((float) (total - different)
					/ (float) total * 100, 2);
			setTable(getComponentControlled().getGeneralTable(), headers,
					content);
		}
	}

	/**
	 * Sets the songs table.
	 */

	private void setSongsTable() {
		List<Object[]> songs = getMostPlayedAudioFilesInRanking(-1);
		if (songs != null) {
			String[] headers = new String[] { I18nUtils.getString("SONG"),
					I18nUtils.getString("TIMES_PLAYED"), "%" };
			Object[][] content = new Object[songs.size()][3];
			for (int i = 0; i < songs.size(); i++) {
				content[i][0] = songs.get(i)[0];
				content[i][1] = songs.get(i)[1];
				if (this.statisticsHandler.getTotalAudioObjectsPlayed() != -1) {
					content[i][2] = StringUtils.toString(
							100.0
									* (float) ((Integer) songs.get(i)[1])
									/ this.statisticsHandler
											.getTotalAudioObjectsPlayed(), 2);
				} else {
					content[i][2] = 0;
				}
			}
			setTable(getComponentControlled().getSongsTable(), headers, content);
		}
	}

	/**
	 * Gets the most played audio files in ranking.
	 * 
	 * @param n
	 *            the n
	 * 
	 * @return the most played audio files in ranking
	 */
	private List<Object[]> getMostPlayedAudioFilesInRanking(final int n) {
		List<Object[]> result = new ArrayList<Object[]>();
		List<IAudioObject> audioFiles = this.statisticsHandler
				.getMostPlayedAudioObjects(n);
		List<Integer> count = this.statisticsHandler
				.getMostPlayedAudioObjectsCount(n);
		if (audioFiles != null) {
			for (int i = 0; i < audioFiles.size(); i++) {
				Object[] obj = new Object[2];
				IAudioObject audioFile = audioFiles.get(i);
				if (audioFile != null) {
					obj[0] = StringUtils
							.getString(
									audioFile.getTitleOrFileName(),
									" (",
									audioFile
											.getArtist(this.unknownObjectChecker),
									")");
					obj[1] = count.get(i);
					result.add(obj);
				}
			}
		}
		return result;
	}

	/**
	 * Sets the table.
	 * 
	 * @param table
	 *            the table
	 * @param headers
	 *            the headers
	 * @param content
	 *            the content
	 */
	private void setTable(final JTable table, final Object[] headers,
			final Object[][] content) {
		table.setModel(new StatsDialogDefaultTableModel(content, headers));
		table.getColumnModel().getColumn(0).setPreferredWidth(420);
		table.getColumnModel().getColumn(0)
				.setWidth(table.getColumnModel().getColumn(0).getWidth());
		table.getColumnModel()
				.getColumn(0)
				.setCellRenderer(
						this.lookAndFeelManager
								.getCurrentLookAndFeel()
								.getTableCellRenderer(
										new SwingOrientationTableCellRendererCode(
												this.controlsBuilder)));
		table.getColumnModel().getColumn(2).setPreferredWidth(30);
		table.getColumnModel().getColumn(2)
				.setWidth(table.getColumnModel().getColumn(2).getWidth());

		table.getColumnModel()
				.getColumn(1)
				.setCellRenderer(
						this.lookAndFeelManager
								.getCurrentLookAndFeel()
								.getTableCellRenderer(
										new RightAlignmentTableCellRendererCode()));
		table.getColumnModel()
				.getColumn(2)
				.setCellRenderer(
						this.lookAndFeelManager
								.getCurrentLookAndFeel()
								.getTableCellRenderer(
										new RightAlignmentTableCellRendererCode()));
	}

	/**
	 * Show stats.
	 */
	void showStats() {
		updateStats();
		StatsDialog frame = getComponentControlled();
		frame.setVisible(true);
	}

	/**
	 * Update stats.
	 */
	void updateStats() {
		setArtistsTable();
		setAlbumsTable();
		setSongsTable();
		setGeneralTable();

		setArtistsChart();
		setAlbumsChart();
		setSongsChart();
		setGeneralChart();
	}

	@Override
	public void lookAndFeelChanged() {
		updateStats(); // Update color of charts
	}

	/**
	 * Puts image with chart in given label, with title and data provided
	 * 
	 * @param data
	 * @param titleKey
	 * @param chartLabel
	 */
	private void setChart(final List<?> data, final String titleKey,
			final JLabel chartLabel) {
		DefaultCategoryDataset dataset = getDataSet(data);
		JFreeChart chart = ChartFactory.createStackedBarChart3D(
				I18nUtils.getString(titleKey), null, null, dataset,
				PlotOrientation.HORIZONTAL, false, false, false);
		chart.setBackgroundPaint(Color.WHITE);
		chart.setPadding(new RectangleInsets(5, 0, 0, 0));
		NumberAxis axis = new NumberAxis();
		axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		chart.setBackgroundPaint(GuiUtils.getBackgroundColor());
		chart.getTitle().setPaint(GuiUtils.getForegroundColor());
		chart.getCategoryPlot().setRangeAxis(axis);
		chart.getCategoryPlot().setForegroundAlpha(1f);
		chart.getCategoryPlot().getRenderer().setSeriesPaint(0, Color.GREEN);
		chart.getCategoryPlot().getDomainAxis()
				.setTickLabelPaint(GuiUtils.getForegroundColor());
		chart.getCategoryPlot().getRangeAxis()
				.setTickLabelPaint(GuiUtils.getForegroundColor());
		chart.getPlot().setBackgroundPaint(GuiUtils.getBackgroundColor());
		chartLabel.setIcon(new ImageIcon(chart.createBufferedImage(710, 250)));
	}

	/**
	 * Sets the artists chart.
	 */
	private void setArtistsChart() {
		setChart(getMostPlayedArtistsInRanking(10), "ARTIST_MOST_PLAYED",
				getComponentControlled().getArtistsChart());
	}

	/**
	 * Sets the albums chart.
	 */
	private void setAlbumsChart() {
		setChart(getMostPlayedAlbumsInRanking(10), "ALBUM_MOST_PLAYED",
				getComponentControlled().getAlbumsChart());
	}

	/**
	 * Sets the songs chart.
	 */
	private void setSongsChart() {
		setChart(getMostPlayedAudioFilesInRanking(10), "SONG_MOST_PLAYED",
				getComponentControlled().getSongsChart());
	}

	/**
	 * Sets the general chart.
	 */
	private void setGeneralChart() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		int different = this.statisticsHandler.getDifferentAudioObjectsPlayed();
		int total = this.repositoryHandler.getAudioFilesList().size();
		dataset.setValue(I18nUtils.getString("SONGS_PLAYED"), different);
		dataset.setValue(I18nUtils.getString("SONGS_NEVER_PLAYED"), total
				- different);
		JFreeChart chart = ChartFactory.createPieChart3D(
				I18nUtils.getString("SONGS_PLAYED"), dataset, false, false,
				false);
		chart.setBackgroundPaint(GuiUtils.getBackgroundColor());
		chart.getTitle().setPaint(GuiUtils.getForegroundColor());
		chart.setPadding(new RectangleInsets(5, 0, 0, 0));
		DefaultDrawingSupplier drawingSupplier = new DefaultDrawingSupplier(
				new Paint[] { new Color(0, 1, 0, 0.6f),
						new Color(1, 0, 0, 0.6f) }, new Paint[] {
						new Color(0, 1, 0, 0.4f), new Color(1, 0, 0, 0.4f) },
				DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
				DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
		chart.getPlot().setDrawingSupplier(drawingSupplier);
		((PiePlot3D) chart.getPlot()).setOutlineVisible(false);
		((PiePlot3D) chart.getPlot()).setBackgroundPaint(GuiUtils
				.getBackgroundColor());
		getComponentControlled().getGeneralChart().setIcon(
				new ImageIcon(chart.createBufferedImage(710, 250)));
	}
}
