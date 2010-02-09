/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.controllers.stats;

import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.lookandfeel.TableCellRendererCode;
import net.sourceforge.atunes.gui.views.dialogs.StatsDialog;
import net.sourceforge.atunes.kernel.controllers.model.SimpleController;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.Album;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.statistics.StatisticsHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.utils.GuiUtils;
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

public final class StatsDialogController extends SimpleController<StatsDialog> {

    /**
     * Instantiates a new stats dialog controller.
     * 
     * @param frame
     *            the frame
     */
    public StatsDialogController(StatsDialog frame) {
        super(frame);
    }

    @Override
    protected void addBindings() {
        // Nothing to do
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    /**
     * Gets the data set.
     * 
     * @param list
     *            the list
     * 
     * @return the data set
     */
    private DefaultCategoryDataset getDataSet(List<?> list) {
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Object[] obj = (Object[]) list.get(i);
                result.setValue((Integer) obj[1], "", (String) obj[0]);
            }
        }
        return result;
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }

    /**
     * Sets the albums chart.
     */
    private void setAlbumsChart() {
        DefaultCategoryDataset dataset = getDataSet(getMostPlayedAlbumsInRanking(10));
        JFreeChart chart = ChartFactory.createStackedBarChart3D(I18nUtils.getString("ALBUM_MOST_PLAYED"), null, null, dataset, PlotOrientation.HORIZONTAL, false, false, false);
        chart.getTitle().setFont(Fonts.CHART_TITLE_FONT);
        chart.setBackgroundPaint(Color.WHITE);
        chart.setPadding(new RectangleInsets(5, 0, 0, 0));
        NumberAxis axis = new NumberAxis();
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        axis.setTickLabelFont(Fonts.CHART_TICK_LABEL_FONT);
        chart.setBackgroundPaint(GuiUtils.getBackgroundColor());
        chart.getTitle().setPaint(GuiUtils.getForegroundColor());
        chart.getCategoryPlot().setRangeAxis(axis);
        chart.getCategoryPlot().setForegroundAlpha(1f);
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, Color.GREEN);
        chart.getCategoryPlot().getDomainAxis().setTickLabelFont(Fonts.CHART_TICK_LABEL_FONT);
        chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(GuiUtils.getForegroundColor());
        chart.getCategoryPlot().getRangeAxis().setTickLabelFont(Fonts.CHART_TICK_LABEL_FONT);
        chart.getCategoryPlot().getRangeAxis().setTickLabelPaint(GuiUtils.getForegroundColor());
        chart.getPlot().setBackgroundPaint(GuiUtils.getBackgroundColor());

        getComponentControlled().getAlbumsChart().setIcon(new ImageIcon(chart.createBufferedImage(710, 250)));
    }

    /**
     * Sets the albums table.
     */
    private void setAlbumsTable() {
        List<Object[]> albums = getMostPlayedAlbumsInRanking(-1);
        if (albums != null) {
            String[] headers = new String[] { I18nUtils.getString("ALBUM"), I18nUtils.getString("TIMES_PLAYED"), "%" };
            Object[][] content = new Object[albums.size()][3];
            for (int i = 0; i < albums.size(); i++) {
                content[i][0] = albums.get(i)[0];
                content[i][1] = albums.get(i)[1];
                if (StatisticsHandler.getInstance().getTotalAudioFilesPlayed() != -1) {
                    content[i][2] = StringUtils.toString(100.0 * (float) ((Integer) albums.get(i)[1]) / StatisticsHandler.getInstance().getTotalAudioFilesPlayed(), 2);
                } else {
                    content[i][2] = 0;
                }
            }
            setTable(getComponentControlled().getAlbumsTable(), headers, content);
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
    private List<Object[]> getMostPlayedAlbumsInRanking(int n) {
        List<Object[]> result = new ArrayList<Object[]>();
        List<Album> albums = StatisticsHandler.getInstance().getMostPlayedAlbums(n);
        List<Integer> count = StatisticsHandler.getInstance().getMostPlayedAlbumsCount(n);
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
     * Sets the artists chart.
     */
    private void setArtistsChart() {
        DefaultCategoryDataset dataset = getDataSet(getMostPlayedArtistsInRanking(10));
        JFreeChart chart = ChartFactory.createStackedBarChart3D(I18nUtils.getString("ARTIST_MOST_PLAYED"), null, null, dataset, PlotOrientation.HORIZONTAL, false, false, false);
        chart.getTitle().setFont(Fonts.CHART_TITLE_FONT);
        chart.setBackgroundPaint(Color.WHITE);
        chart.setPadding(new RectangleInsets(5, 0, 0, 0));
        NumberAxis axis = new NumberAxis();
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        axis.setTickLabelFont(Fonts.CHART_TICK_LABEL_FONT);
        chart.setBackgroundPaint(GuiUtils.getBackgroundColor());
        chart.getTitle().setPaint(GuiUtils.getForegroundColor());
        chart.getCategoryPlot().setRangeAxis(axis);
        chart.getCategoryPlot().setForegroundAlpha(1f);
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, Color.GREEN);
        chart.getCategoryPlot().getDomainAxis().setTickLabelFont(Fonts.CHART_TICK_LABEL_FONT);
        chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(GuiUtils.getForegroundColor());
        chart.getCategoryPlot().getRangeAxis().setTickLabelFont(Fonts.CHART_TICK_LABEL_FONT);
        chart.getCategoryPlot().getRangeAxis().setTickLabelPaint(GuiUtils.getForegroundColor());
        chart.getPlot().setBackgroundPaint(GuiUtils.getBackgroundColor());

        getComponentControlled().getArtistsChart().setIcon(new ImageIcon(chart.createBufferedImage(710, 250)));
    }

    /**
     * Sets the artists table.
     */

    private void setArtistsTable() {
        List<Object[]> artists = getMostPlayedArtistsInRanking(-1);
        if (artists != null) {
            String[] headers = new String[] { I18nUtils.getString("ARTIST"), I18nUtils.getString("TIMES_PLAYED"), "%" };
            Object[][] content = new Object[artists.size()][3];
            for (int i = 0; i < artists.size(); i++) {
                content[i][0] = artists.get(i)[0];
                content[i][1] = artists.get(i)[1];
                if (StatisticsHandler.getInstance().getTotalAudioFilesPlayed() != -1) {
                    content[i][2] = StringUtils.toString(100.0 * (float) ((Integer) artists.get(i)[1]) / StatisticsHandler.getInstance().getTotalAudioFilesPlayed(), 2);
                } else {
                    content[i][2] = 0;
                }
            }
            setTable(getComponentControlled().getArtistsTable(), headers, content);
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
    private List<Object[]> getMostPlayedArtistsInRanking(int n) {
        List<Object[]> result = new ArrayList<Object[]>();
        List<Artist> artists = StatisticsHandler.getInstance().getMostPlayedArtists(n);
        List<Integer> count = StatisticsHandler.getInstance().getMostPlayedArtistsCount(n);
        if (artists != null) {
            for (int i = 0; i < artists.size(); i++) {
                Object[] obj = new Object[2];
                obj[0] = artists.get(i).toString();
                obj[1] = count.get(i);
                result.add(obj);
            }
        }
        return result;
    }

    /**
     * Sets the general chart.
     */
    private void setGeneralChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        int different = StatisticsHandler.getInstance().getDifferentAudioFilesPlayed();
        int total = RepositoryHandler.getInstance().getAudioFilesList().size();
        dataset.setValue(I18nUtils.getString("SONGS_PLAYED"), different);
        dataset.setValue(I18nUtils.getString("SONGS_NEVER_PLAYED"), total - different);
        JFreeChart chart = ChartFactory.createPieChart3D(I18nUtils.getString("SONGS_PLAYED"), dataset, false, false, false);
        chart.getTitle().setFont(Fonts.CHART_TITLE_FONT);
        chart.setBackgroundPaint(GuiUtils.getBackgroundColor());
        chart.getTitle().setPaint(GuiUtils.getForegroundColor());
        chart.setPadding(new RectangleInsets(5, 0, 0, 0));
        DefaultDrawingSupplier drawingSupplier = new DefaultDrawingSupplier(new Paint[] { new Color(0, 1, 0, 0.6f), new Color(1, 0, 0, 0.6f) }, new Paint[] {
                new Color(0, 1, 0, 0.4f), new Color(1, 0, 0, 0.4f) }, DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        chart.getPlot().setDrawingSupplier(drawingSupplier);
        ((PiePlot3D) chart.getPlot()).setLabelFont(Fonts.CHART_TICK_LABEL_FONT);
        ((PiePlot3D) chart.getPlot()).setOutlineVisible(false);
        ((PiePlot3D) chart.getPlot()).setBackgroundPaint(GuiUtils.getBackgroundColor());
        getComponentControlled().getGeneralChart().setIcon(new ImageIcon(chart.createBufferedImage(710, 250)));
    }

    /**
     * Sets the general table.
     */

    private void setGeneralTable() {
        int different = StatisticsHandler.getInstance().getDifferentAudioFilesPlayed();
        int total = RepositoryHandler.getInstance().getAudioFilesList().size();
        if (total != 0) {
            String[] headers = new String[] { " ", I18nUtils.getString("COUNT"), "%" };
            Object[][] content = new Object[2][3];
            content[0] = new Object[3];
            content[0][0] = I18nUtils.getString("SONGS_PLAYED");
            content[0][1] = different;
            content[0][2] = StringUtils.toString((float) different / (float) total * 100, 2);
            content[1] = new Object[3];
            content[1][0] = I18nUtils.getString("SONGS_NEVER_PLAYED");
            content[1][1] = total - different;
            content[1][2] = StringUtils.toString((float) (total - different) / (float) total * 100, 2);
            setTable(getComponentControlled().getGeneralTable(), headers, content);
        }
    }

    /**
     * Sets the songs chart.
     */
    private void setSongsChart() {
        DefaultCategoryDataset dataset = getDataSet(getMostPlayedAudioFilesInRanking(10));
        JFreeChart chart = ChartFactory.createStackedBarChart3D(I18nUtils.getString("SONG_MOST_PLAYED"), null, null, dataset, PlotOrientation.HORIZONTAL, false, false, false);
        chart.getTitle().setFont(Fonts.CHART_TITLE_FONT);
        chart.setBackgroundPaint(Color.WHITE);
        chart.setPadding(new RectangleInsets(5, 0, 0, 0));
        NumberAxis axis = new NumberAxis();
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        axis.setTickLabelFont(Fonts.CHART_TICK_LABEL_FONT);
        chart.setBackgroundPaint(GuiUtils.getBackgroundColor());
        chart.getTitle().setPaint(GuiUtils.getForegroundColor());
        chart.getCategoryPlot().setRangeAxis(axis);
        chart.getCategoryPlot().setForegroundAlpha(1f);
        chart.getCategoryPlot().getRenderer().setSeriesPaint(0, Color.GREEN);
        chart.getCategoryPlot().getDomainAxis().setTickLabelFont(Fonts.CHART_TICK_LABEL_FONT);
        chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(GuiUtils.getForegroundColor());
        chart.getCategoryPlot().getRangeAxis().setTickLabelFont(Fonts.CHART_TICK_LABEL_FONT);
        chart.getCategoryPlot().getRangeAxis().setTickLabelPaint(GuiUtils.getForegroundColor());
        chart.getPlot().setBackgroundPaint(GuiUtils.getBackgroundColor());

        getComponentControlled().getSongsChart().setIcon(new ImageIcon(chart.createBufferedImage(710, 250)));
    }

    /**
     * Sets the songs table.
     */

    private void setSongsTable() {
        List<Object[]> songs = getMostPlayedAudioFilesInRanking(-1);
        if (songs != null) {
            String[] headers = new String[] { I18nUtils.getString("SONG"), I18nUtils.getString("TIMES_PLAYED"), "%" };
            Object[][] content = new Object[songs.size()][3];
            for (int i = 0; i < songs.size(); i++) {
                content[i][0] = songs.get(i)[0];
                content[i][1] = songs.get(i)[1];
                if (StatisticsHandler.getInstance().getTotalAudioFilesPlayed() != -1) {
                    content[i][2] = StringUtils.toString(100.0 * (float) ((Integer) songs.get(i)[1]) / StatisticsHandler.getInstance().getTotalAudioFilesPlayed(), 2);
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
    private List<Object[]> getMostPlayedAudioFilesInRanking(int n) {
        List<Object[]> result = new ArrayList<Object[]>();
        List<AudioFile> audioFiles = StatisticsHandler.getInstance().getMostPlayedAudioFiles(n);
        List<Integer> count = StatisticsHandler.getInstance().getMostPlayedAudioFilesCount(n);
        if (audioFiles != null) {
            for (int i = 0; i < audioFiles.size(); i++) {
                Object[] obj = new Object[2];
                AudioFile audioFile = audioFiles.get(i);
                obj[0] = StringUtils.getString(audioFile.getTitleOrFileName(), " (", audioFile.getArtist(), ")");
                obj[1] = count.get(i);
                result.add(obj);
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
    private void setTable(JTable table, Object[] headers, Object[][] content) {
        table.setModel(new DefaultTableModel(content, headers) {
            private static final long serialVersionUID = 0L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        table.getColumnModel().getColumn(0).setPreferredWidth(420);
        table.getColumnModel().getColumn(0).setWidth(table.getColumnModel().getColumn(0).getWidth());
        table.getColumnModel().getColumn(0).setCellRenderer(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(new TableCellRendererCode() {

            @Override
            public Component getComponent(Component superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) superComponent;
                l.setHorizontalAlignment(GuiUtils.getComponentOrientationAsSwingConstant());
                return l;
            }
        }));
        table.getColumnModel().getColumn(2).setPreferredWidth(30);
        table.getColumnModel().getColumn(2).setWidth(table.getColumnModel().getColumn(2).getWidth());

        table.getColumnModel().getColumn(1).setCellRenderer(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(new TableCellRendererCode() {

            @Override
            public Component getComponent(Component superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) superComponent;
                l.setHorizontalAlignment(SwingConstants.RIGHT);
                return l;
            }
        }));
        table.getColumnModel().getColumn(2).setCellRenderer(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(new TableCellRendererCode() {

            @Override
            public Component getComponent(Component superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) superComponent;
                l.setHorizontalAlignment(SwingConstants.RIGHT);
                return l;
            }
        }));
    }

    /**
     * Show stats.
     */
    public void showStats() {
        getLogger().debug(LogCategories.CONTROLLER);

        updateStats();
        StatsDialog frame = getComponentControlled();
        frame.setVisible(true);
    }

    /**
     * Update stats.
     */
    public void updateStats() {
        setArtistsTable();
        setAlbumsTable();
        setSongsTable();
        setGeneralTable();

        setArtistsChart();
        setAlbumsChart();
        setSongsChart();
        setGeneralChart();
    }

}
