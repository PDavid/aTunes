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

package net.sourceforge.atunes.kernel.controllers.stats;

import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.views.dialogs.StatsDialog;
import net.sourceforge.atunes.kernel.controllers.model.FrameController;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.LanguageTool;
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
import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 * The Class StatsDialogController.
 */
public class StatsDialogController extends FrameController<StatsDialog> {

    /**
     * Instantiates a new stats dialog controller.
     * 
     * @param frame
     *            the frame
     */
    public StatsDialogController(StatsDialog frame) {
        super(frame);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.kernel.controllers.model.Controller#addBindings()
     */
    @Override
    protected void addBindings() {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.kernel.controllers.model.Controller#addStateBindings
     * ()
     */
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.kernel.controllers.model.Controller#notifyReload()
     */
    @Override
    protected void notifyReload() {
        // Nothing to do
    }

    /**
     * Sets the albums chart.
     */
    private void setAlbumsChart() {
        DefaultCategoryDataset dataset = getDataSet(RepositoryHandler.getInstance().getMostPlayedAlbumsInRanking(10));
        JFreeChart chart = ChartFactory.createStackedBarChart3D(LanguageTool.getString("ALBUM_MOST_PLAYED"), null, null, dataset, PlotOrientation.HORIZONTAL, false, false, false);
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

        getFrameControlled().getAlbumsChart().setIcon(new ImageIcon(chart.createBufferedImage(710, 250)));
    }

    /**
     * Sets the albums table.
     */

    private void setAlbumsTable() {
        List<Object[]> albums = RepositoryHandler.getInstance().getMostPlayedAlbumsInRanking(-1);
        if (albums != null) {
            String[] headers = new String[] { LanguageTool.getString("ALBUM"), LanguageTool.getString("TIMES_PLAYED"), "%" };
            Object[][] content = new Object[albums.size()][3];
            for (int i = 0; i < albums.size(); i++) {
                content[i][0] = albums.get(i)[0];
                content[i][1] = albums.get(i)[1];
                if (RepositoryHandler.getInstance().getTotalAudioFilesPlayed() != -1) {
                    content[i][2] = StringUtils.toString(100.0 * (float) ((Integer) albums.get(i)[1]) / RepositoryHandler.getInstance().getTotalAudioFilesPlayed(), 2);
                } else {
                    content[i][2] = 0;
                }
            }
            setTable(getFrameControlled().getAlbumsTable(), headers, content);
        }
    }

    /**
     * Sets the artists chart.
     */
    private void setArtistsChart() {
        DefaultCategoryDataset dataset = getDataSet(RepositoryHandler.getInstance().getMostPlayedArtistsInRanking(10));
        JFreeChart chart = ChartFactory.createStackedBarChart3D(LanguageTool.getString("ARTIST_MOST_PLAYED"), null, null, dataset, PlotOrientation.HORIZONTAL, false, false, false);
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

        getFrameControlled().getArtistsChart().setIcon(new ImageIcon(chart.createBufferedImage(710, 250)));
    }

    /**
     * Sets the artists table.
     */

    private void setArtistsTable() {
        List<Object[]> artists = RepositoryHandler.getInstance().getMostPlayedArtistsInRanking(-1);
        if (artists != null) {
            String[] headers = new String[] { LanguageTool.getString("ARTIST"), LanguageTool.getString("TIMES_PLAYED"), "%" };
            Object[][] content = new Object[artists.size()][3];
            for (int i = 0; i < artists.size(); i++) {
                content[i][0] = artists.get(i)[0];
                content[i][1] = artists.get(i)[1];
                if (RepositoryHandler.getInstance().getTotalAudioFilesPlayed() != -1) {
                    content[i][2] = StringUtils.toString(100.0 * (float) ((Integer) artists.get(i)[1]) / RepositoryHandler.getInstance().getTotalAudioFilesPlayed(), 2);
                } else {
                    content[i][2] = 0;
                }
            }
            setTable(getFrameControlled().getArtistsTable(), headers, content);
        }
    }

    /**
     * Sets the general chart.
     */
    private void setGeneralChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        int different = RepositoryHandler.getInstance().getDifferentAudioFilesPlayed();
        int total = RepositoryHandler.getInstance().getAudioFiles().size();
        dataset.setValue(LanguageTool.getString("SONGS_PLAYED"), different);
        dataset.setValue(LanguageTool.getString("SONGS_NEVER_PLAYED"), total - different);
        JFreeChart chart = ChartFactory.createPieChart3D(LanguageTool.getString("SONGS_PLAYED"), dataset, false, false, false);
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
        getFrameControlled().getGeneralChart().setIcon(new ImageIcon(chart.createBufferedImage(710, 250)));
    }

    /**
     * Sets the general table.
     */

    private void setGeneralTable() {
        int different = RepositoryHandler.getInstance().getDifferentAudioFilesPlayed();
        int total = RepositoryHandler.getInstance().getAudioFiles().size();
        if (total != 0) {
            String[] headers = new String[] { " ", LanguageTool.getString("COUNT"), "%" };
            Object[][] content = new Object[2][3];
            content[0] = new Object[3];
            content[0][0] = LanguageTool.getString("SONGS_PLAYED");
            content[0][1] = different;
            content[0][2] = StringUtils.toString((float) different / (float) total * 100, 2);
            content[1] = new Object[3];
            content[1][0] = LanguageTool.getString("SONGS_NEVER_PLAYED");
            content[1][1] = total - different;
            content[1][2] = StringUtils.toString((float) (total - different) / (float) total * 100, 2);
            setTable(getFrameControlled().getGeneralTable(), headers, content);
        }
    }

    /**
     * Sets the songs chart.
     */
    private void setSongsChart() {
        DefaultCategoryDataset dataset = getDataSet(RepositoryHandler.getInstance().getMostPlayedAudioFilesInRanking(10));
        JFreeChart chart = ChartFactory.createStackedBarChart3D(LanguageTool.getString("SONG_MOST_PLAYED"), null, null, dataset, PlotOrientation.HORIZONTAL, false, false, false);
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

        getFrameControlled().getSongsChart().setIcon(new ImageIcon(chart.createBufferedImage(710, 250)));
    }

    /**
     * Sets the songs table.
     */

    private void setSongsTable() {
        List<Object[]> songs = RepositoryHandler.getInstance().getMostPlayedAudioFilesInRanking(-1);
        if (songs != null) {
            String[] headers = new String[] { LanguageTool.getString("SONG"), LanguageTool.getString("TIMES_PLAYED"), "%" };
            Object[][] content = new Object[songs.size()][3];
            for (int i = 0; i < songs.size(); i++) {
                content[i][0] = songs.get(i)[0];
                content[i][1] = songs.get(i)[1];
                if (RepositoryHandler.getInstance().getTotalAudioFilesPlayed() != -1) {
                    content[i][2] = StringUtils.toString(100.0 * (float) ((Integer) songs.get(i)[1]) / RepositoryHandler.getInstance().getTotalAudioFilesPlayed(), 2);
                } else {
                    content[i][2] = 0;
                }
            }
            setTable(getFrameControlled().getSongsTable(), headers, content);
        }
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
        table.getColumnModel().getColumn(0).setCellRenderer(new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 4539744679194918575L;

            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(GuiUtils.getComponentOrientationAsSwingConstant());
                return l;
            }
        });
        table.getColumnModel().getColumn(2).setPreferredWidth(30);
        table.getColumnModel().getColumn(2).setWidth(table.getColumnModel().getColumn(2).getWidth());
        table.getColumnModel().getColumn(1).setCellRenderer(new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 4539744679194918575L;

            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.RIGHT);
                return l;
            }
        });
        table.getColumnModel().getColumn(2).setCellRenderer(new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = -2444813781467459040L;

            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.RIGHT);
                return l;
            }
        });
    }

    /**
     * Show stats.
     */
    public void showStats() {
        getLogger().debug(LogCategories.CONTROLLER);

        updateStats();
        StatsDialog frame = getFrameControlled();
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
