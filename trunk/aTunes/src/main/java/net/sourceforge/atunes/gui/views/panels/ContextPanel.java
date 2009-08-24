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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.YoutubeResultTableModel;
import net.sourceforge.atunes.gui.substance.SubstanceContextImageJTable;
import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddBannedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.AddLovedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.CopyLyricToClipboardAction;
import net.sourceforge.atunes.kernel.actions.DownloadFromYoutubeAction;
import net.sourceforge.atunes.kernel.actions.MoreResultsFromYouTubeAction;
import net.sourceforge.atunes.kernel.actions.OpenLyricsSourceAction;
import net.sourceforge.atunes.kernel.actions.OpenYouTubeResultsAction;
import net.sourceforge.atunes.kernel.actions.OpenYoutubeVideoInBrowserAction;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.ArtistInfo;
import net.sourceforge.atunes.kernel.modules.context.youtube.YoutubeResultEntry;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.border.DropShadowBorder;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 * The Class ContextPanel.
 */
public class ContextPanel extends JPanel {

    private static final long serialVersionUID = 707242790413122482L;

    /** The Constant PREF_SIZE. */
    public static final Dimension PREF_SIZE = new Dimension(230, 0);

    /** The Constant MINIMUM_SIZE. */
    public static final Dimension MINIMUM_SIZE = new Dimension(170, 0);

    /** The tabbed pane. */
    private JTabbedPane tabbedPane;

    /** The album tab panel. */
    private JPanel albumTabPanel;

    /** The artist tab panel. */
    private JPanel artistTabPanel;

    /** The similar tab panel. */
    private JPanel similarTabPanel;

    /** The audio object panel. */
    private JPanel audioObjectPanel;

    /** The album panel. */
    private JPanel albumPanel;

    /** The artist label. */
    private UrlLabel artistLabel;

    /** The album label. */
    private UrlLabel albumLabel;

    /** The year label. */
    private UrlLabel yearLabel;

    /** The album cover label. */
    private JLabel albumCoverLabel;

    /** The tracks table. */
    private JTable tracksTable;

    /** The audio object cover. */
    private JLabel audioObjectCover;

    /** The audio object label. */
    private JLabel audioObjectLabel;

    /** The audio object artist label. */
    private JLabel audioObjectArtistLabel;

    /** The audio object last play date label. */
    private JLabel audioObjectLastPlayDateLabel;

    /** The lyrics container. */
    private JTextArea lyricsContainer;

    /** The lyric scroll pane. */
    private JScrollPane lyricScrollPane;

    /** The add lyrics menu */
    private JMenu addLyrics;

    /** The albums panel. */
    private JPanel albumsPanel;

    /** The albums scroll pane. */
    private JScrollPane albumsScrollPane;

    /** The artist image label. */
    private JLabel artistImageLabel;

    /** The artist albums label. */
    private UrlLabel artistAlbumsLabel;

    /** The albums table. */
    private JTable albumsTable;

    /** The artist wiki abstract. */
    private JTextArea artistWikiAbstract;

    /** The artist wiki read more. */
    private UrlLabel artistWikiReadMore;

    /** The similar artists panel. */
    private JPanel similarArtistsPanel;

    /** The similar artists table. */
    private JTable similarArtistsTable;

    /** The similar artists scroll pane. */
    private JScrollPane similarArtistsScrollPane;

    private PopUpButton optionsButton;

    /**
     * The text of the first tab
     */
    private String firstTabTitle;

    /**
     * Tab to show results from YouTube
     */
    private JPanel youtubeTabPanel;

    /**
     * Table to show result from YouTube
     */
    private SubstanceContextImageJTable youtubeResultTable;

    /**
     * Button to show more results from YouTube
     */
    private JButton moreResultsFromYouTubeButton;

    /**
     * Button to open YouTube to show results
     */
    private JButton showResultAtYouTube;

    /**
     * Menu to show when right clicking in youtube results
     */
    private JPopupMenu youtubeTableMenu;

    /**
     * Instantiates a new audio scrobbler panel.
     */
    public ContextPanel() {
        super(new GridBagLayout());
        setPreferredSize(PREF_SIZE);
        setMinimumSize(MINIMUM_SIZE);
        setContent();
    }

    /**
     * Adds the album panel.
     */
    private void addAlbumPanel() {
        albumPanel = new JPanel(new GridBagLayout());

        artistLabel = new UrlLabel();
        artistLabel.setHorizontalAlignment(SwingConstants.CENTER);
        albumLabel = new UrlLabel();
        albumLabel.setHorizontalAlignment(SwingConstants.CENTER);
        albumLabel.setFont(Fonts.CONTEXT_INFORMATION_BIG_FONT);
        albumCoverLabel = new JLabel();
        albumCoverLabel.setBorder(new DropShadowBorder());
        albumCoverLabel.setVisible(false);
        yearLabel = new UrlLabel();
        yearLabel.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(15, 0, 0, 0);
        albumPanel.add(albumCoverLabel, c);

        c.gridy = 1;
        c.insets = new Insets(5, 5, 0, 5);
        albumPanel.add(albumLabel, c);

        c.gridy = 2;
        albumPanel.add(artistLabel, c);

        c.gridy = 3;
        albumPanel.add(yearLabel, c);

        tracksTable = new JTable();
        tracksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tracksTable.setShowGrid(false);
        tracksTable.setDefaultRenderer(String.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 6554520059295478131L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
                Component c = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5);
                GuiUtils.applyComponentOrientation((JLabel) c);
                return c;
            }
        });
        tracksTable.setDefaultRenderer(Integer.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 1328605998912553401L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
                Component c = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5);
                GuiUtils.applyComponentOrientation((JLabel) c);
                return c;
            }
        });
        tracksTable.getTableHeader().setReorderingAllowed(true);
        tracksTable.getTableHeader().setResizingAllowed(false);
        tracksTable.setColumnModel(new DefaultTableColumnModel() {
            private static final long serialVersionUID = 1338172152164826400L;

            @Override
            public void addColumn(TableColumn column) {
                super.addColumn(column);
                if (column.getModelIndex() == 0) {
                    column.setMaxWidth(25);
                }
            }
        });
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(20, 5, 5, 5);
        JScrollPane scroll = new JScrollPane(tracksTable);
        //scroll.setBorder(BorderFactory.createEmptyBorder());
        albumPanel.add(scroll, c);

        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);
        albumTabPanel.add(albumPanel, c);

        albumPanel.setVisible(false);
    }

    /**
     * Adds the albums panel.
     */
    private void addAlbumsPanel() {
        albumsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        artistImageLabel = new JLabel();
        artistImageLabel.setBorder(new DropShadowBorder());
        artistImageLabel.setVisible(false);
        artistAlbumsLabel = new UrlLabel();
        artistAlbumsLabel.setFont(Fonts.CONTEXT_INFORMATION_BIG_FONT);
        artistAlbumsLabel.setVisible(false);
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(15, 5, 0, 5);
        albumsPanel.add(artistImageLabel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 5);
        albumsPanel.add(artistAlbumsLabel, c);

        artistWikiAbstract = new JTextArea();
        artistWikiAbstract.setLineWrap(true);
        artistWikiAbstract.setWrapStyleWord(true);
        artistWikiAbstract.setEditable(false);
        artistWikiAbstract.setBorder(BorderFactory.createEmptyBorder());
        artistWikiAbstract.setOpaque(false);
        JScrollPane artistWikiScrollPane = new JScrollPane(artistWikiAbstract);
        artistWikiScrollPane.setBorder(BorderFactory.createEmptyBorder());
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 0.3;
        c.fill = GridBagConstraints.BOTH;
        albumsPanel.add(artistWikiScrollPane, c);

        artistWikiReadMore = new UrlLabel();
        c.gridy = 3;
        c.weighty = 0;
        albumsPanel.add(artistWikiReadMore, c);

        albumsTable = new SubstanceContextImageJTable();
        albumsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        albumsTable.setShowGrid(false);
        albumsTable.getTableHeader().setReorderingAllowed(false);
        albumsTable.getTableHeader().setResizingAllowed(false);
        albumsTable.setDefaultRenderer(AlbumInfo.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 620892562731682118L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
                Color backgroundColor = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5).getBackground();
                return getPanelForTableRenderer(((AlbumInfo) value).getCover(), StringUtils.getString("<html>", ((AlbumInfo) value).getTitle(), "</html>"), backgroundColor,
                        Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT);
            }
        });
        albumsScrollPane = new JScrollPane(albumsTable);
        albumsScrollPane.setAutoscrolls(false);
        albumsScrollPane.setOpaque(false);

        c.gridy = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 5, 0, 5);
        albumsPanel.add(albumsScrollPane, c);
        c.gridy = 5;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 0);
        artistTabPanel.add(albumsPanel, c);

        albumsPanel.setVisible(false);
    }

    /**
     * Adds the audio object panel.
     */
    private void addAudioObjectInfoPanel() {
        audioObjectCover = new JLabel();
        audioObjectCover.setVisible(false);
        audioObjectLabel = new JLabel();
        audioObjectLabel.setHorizontalAlignment(SwingConstants.CENTER);
        audioObjectLabel.setFont(Fonts.CONTEXT_INFORMATION_BIG_FONT);
        audioObjectArtistLabel = new JLabel();
        audioObjectArtistLabel.setHorizontalAlignment(SwingConstants.CENTER);
        audioObjectLastPlayDateLabel = new JLabel();
        audioObjectLastPlayDateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lyricsContainer = new JTextArea();
        lyricsContainer.setBorder(BorderFactory.createEmptyBorder());
        lyricsContainer.setEditable(false);
        lyricsContainer.setWrapStyleWord(true);
        lyricsContainer.setOpaque(false);
        lyricScrollPane = new JScrollPane(lyricsContainer);
        lyricScrollPane.setAutoscrolls(false);
        lyricScrollPane.setOpaque(false);
        lyricScrollPane.setBorder(BorderFactory.createEmptyBorder());
        addLyrics = new JMenu(LanguageTool.getString("ADD_LYRICS"));
        addLyrics.setToolTipText(LanguageTool.getString("ADD_LYRICS"));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(15, 0, 0, 0);
        audioObjectPanel.add(audioObjectCover, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 10, 0, 10);
        audioObjectPanel.add(audioObjectLabel, c);
        c.gridy = 2;
        c.insets = new Insets(5, 10, 10, 10);
        audioObjectPanel.add(audioObjectArtistLabel, c);
        c.gridy = 3;
        audioObjectPanel.add(audioObjectLastPlayDateLabel, c);
        c.gridy = 4;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 5, 5, 5);
        audioObjectPanel.add(lyricScrollPane, c);
        c.gridy = 5;
        c.weighty = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        optionsButton = new PopUpButton(LanguageTool.getString("OPTIONS"), PopUpButton.TOP_RIGHT);
        optionsButton.add(new JMenuItem(Actions.getAction(CopyLyricToClipboardAction.class)));
        optionsButton.add(addLyrics);
        optionsButton.add(new JMenuItem(Actions.getAction(OpenLyricsSourceAction.class)));
        optionsButton.add(new JSeparator());
        optionsButton.add(new JMenuItem(Actions.getAction(AddLovedSongInLastFMAction.class)));
        optionsButton.add(new JMenuItem(Actions.getAction(AddBannedSongInLastFMAction.class)));
        c.gridy = 6;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 5, 0, 0);
        audioObjectPanel.add(optionsButton, c);
    }

    /**
     * Adds the similar artists panel.
     */
    private void addSimilarArtistsPanel() {
        similarArtistsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        similarArtistsTable = new SubstanceContextImageJTable();
        similarArtistsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        similarArtistsTable.setShowGrid(false);
        similarArtistsTable.setDefaultRenderer(ArtistInfo.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 0L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
                Color backgroundColor = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5).getBackground();
                return getPanelForTableRenderer(((ArtistInfo) value).getImage(), StringUtils.getString("<html><br>", ((ArtistInfo) value).getName(), "<br>", ((ArtistInfo) value)
                        .getMatch(), "%<br>", ((ArtistInfo) value).isAvailable() ? LanguageTool.getString("AVAILABLE_IN_REPOSITORY") : "", "</html>"), backgroundColor,
                        Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT);
            }
        });
        similarArtistsTable.setColumnSelectionAllowed(false);
        similarArtistsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        similarArtistsTable.getTableHeader().setReorderingAllowed(false);
        similarArtistsScrollPane = new JScrollPane(similarArtistsTable);
        similarArtistsScrollPane.setAutoscrolls(false);
        similarArtistsScrollPane.setOpaque(false);
        similarArtistsPanel.add(similarArtistsScrollPane, c);

        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.3;
        similarTabPanel.add(similarArtistsPanel, c);

        similarArtistsPanel.setVisible(false);
    }

    /**
     * Creates a panel to be shown in each row of this panel tables
     * 
     * @param image
     * @param text
     * @param backgroundColor
     * @param imageMaxWidth
     * @param imageMaxHeight
     * @return
     */
    static JPanel getPanelForTableRenderer(ImageIcon image, String text, Color backgroundColor, int imageMaxWidth, int imageMaxHeight) {
        // This renderer is a little tricky because images have no the same size so we must add two labels with custom insets to
        // get desired alignment of images and text. Other ways to achieve this like setPreferredSize doesn't work because when width of panel is low
        // preferred size is ignored, but insets don't
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);
        GridBagConstraints c = new GridBagConstraints();

        JLabel imageLabel = new JLabel(image);
        imageLabel.setOpaque(true);
        imageLabel.setBorder(new DropShadowBorder());
        JLabel textLabel = new JLabel(text);
        textLabel.setOpaque(true);
        textLabel.setVerticalAlignment(SwingConstants.TOP);

        textLabel.setBackground(backgroundColor);
        panel.setBackground(backgroundColor);
        imageLabel.setBackground(backgroundColor);

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(2, (imageMaxWidth + 20) / 2 - (image != null ? image.getIconWidth() : 0) / 2, 0, 0);
        panel.add(imageLabel, c);
        c.gridx = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, (imageMaxWidth + 20) / 2 - (image != null ? image.getIconWidth() : 0) / 2, 0, 0);
        panel.add(textLabel, c);

        GuiUtils.applyComponentOrientation(panel);
        return panel;
    }

    /**
     * Gets the album cover label.
     * 
     * @return the album cover label
     */
    public JLabel getAlbumCoverLabel() {
        return albumCoverLabel;
    }

    /**
     * Gets the album label.
     * 
     * @return the album label
     */
    public UrlLabel getAlbumLabel() {
        return albumLabel;
    }

    /**
     * Gets the album panel.
     * 
     * @return the album panel
     */
    public JPanel getAlbumPanel() {
        return albumPanel;
    }

    /**
     * Gets the albums panel.
     * 
     * @return the albums panel
     */
    public JPanel getAlbumsPanel() {
        return albumsPanel;
    }

    /**
     * Gets the albums scroll pane.
     * 
     * @return the albums scroll pane
     */
    public JScrollPane getAlbumsScrollPane() {
        return albumsScrollPane;
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
     * Gets the artist albums label.
     * 
     * @return the artist albums label
     */
    public UrlLabel getArtistAlbumsLabel() {
        return artistAlbumsLabel;
    }

    /**
     * Gets the artist image label.
     * 
     * @return the artist image label
     */
    public JLabel getArtistImageLabel() {
        return artistImageLabel;
    }

    /**
     * Gets the artist label.
     * 
     * @return the artist label
     */
    public UrlLabel getArtistLabel() {
        return artistLabel;
    }

    /**
     * Gets the artist wiki abstract.
     * 
     * @return the artistWikiAbstract
     */
    public JTextArea getArtistWikiAbstract() {
        return artistWikiAbstract;
    }

    /**
     * Gets the artist wiki read more.
     * 
     * @return the artistWikiReadMore
     */
    public UrlLabel getArtistWikiReadMore() {
        return artistWikiReadMore;
    }

    /**
     * Gets the lyrics artist label.
     * 
     * @return the lyrics artist label
     */
    public JLabel getAudioObjectArtistLabel() {
        return audioObjectArtistLabel;
    }

    /**
     * Gets the lyrics container.
     * 
     * @return the lyrics container
     */
    public JTextArea getLyricsContainer() {
        return lyricsContainer;
    }

    /**
     * Gets the lyrics cover.
     * 
     * @return the lyrics cover
     */
    public JLabel getAudioObjectCover() {
        return audioObjectCover;
    }

    /**
     * Gets the lyric scroll pane.
     * 
     * @return the lyric scroll pane
     */
    public JScrollPane getLyricScrollPane() {
        return lyricScrollPane;
    }

    /**
     * Gets the lyrics label.
     * 
     * @return the lyrics label
     */
    public JLabel getAudioObjectLabel() {
        return audioObjectLabel;
    }

    /**
     * Gets the lyrics last play date label.
     * 
     * @return the lyrics last play date label
     */
    public JLabel getAudioObjectLastPlayDateLabel() {
        return audioObjectLastPlayDateLabel;
    }

    /**
     * Gets the similar artists panel.
     * 
     * @return the similar artists panel
     */
    public JPanel getSimilarArtistsPanel() {
        return similarArtistsPanel;
    }

    /**
     * Gets the similar artists scroll pane.
     * 
     * @return the similar artists scroll pane
     */
    public JScrollPane getSimilarArtistsScrollPane() {
        return similarArtistsScrollPane;
    }

    /**
     * Gets the similar artists table.
     * 
     * @return the similar artists table
     */
    public JTable getSimilarArtistsTable() {
        return similarArtistsTable;
    }

    /**
     * Gets the similar tab panel.
     * 
     * @return the similar tab panel
     */
    public JPanel getSimilarTabPanel() {
        return similarTabPanel;
    }

    /**
     * Gets the tabbed pane.
     * 
     * @return the tabbed pane
     */
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    /**
     * Gets the tracks table.
     * 
     * @return the tracks table
     */
    public JTable getTracksTable() {
        return tracksTable;
    }

    /**
     * Gets the year label.
     * 
     * @return the year label
     */
    public UrlLabel getYearLabel() {
        return yearLabel;
    }

    public JMenu getAddLyrics() {
        return addLyrics;
    }

    public PopUpButton getOptionsButton() {
        return optionsButton;
    }

    /**
     * Sets the content.
     */
    private void setContent() {
        tabbedPane = new JTabbedPane();
        albumTabPanel = new JPanel(new GridBagLayout());
        artistTabPanel = new JPanel(new GridBagLayout());
        similarTabPanel = new JPanel(new GridBagLayout());
        audioObjectPanel = new JPanel(new GridBagLayout());
        youtubeTabPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(3, 2, 0, 2);
        add(tabbedPane, c);

        addAlbumPanel();
        addAlbumsPanel();
        addSimilarArtistsPanel();
        addAudioObjectInfoPanel();
        addYoutubePanel();
        // Add tabs
        showAllTabs(true);
    }

    /**
     * youtube panel; simple panel with one table showing the youtube result
     */
    private void addYoutubePanel() {
        JPanel youtubePanel = new JPanel(new GridBagLayout());

        youtubeResultTable = new SubstanceContextImageJTable();
        youtubeResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        youtubeResultTable.setShowGrid(false);
        youtubeResultTable.getTableHeader().setReorderingAllowed(false);
        youtubeResultTable.setDefaultRenderer(YoutubeResultEntry.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 620892562731682118L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
                Color backgroundColor = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5).getBackground();

                return getPanelForTableRenderer(((YoutubeResultEntry) value).getImage(), StringUtils.getString("<html>", ((YoutubeResultEntry) value).getName(), "<br>(",
                        ((YoutubeResultEntry) value).getDuration(), ")</html>"), backgroundColor, Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT);
            }
        });
        youtubeResultTable.setColumnSelectionAllowed(false);
        createYoutubeResultTableContextMenu(youtubeResultTable);

        youtubeResultTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        youtubeResultTable.setColumnModel(new DefaultTableColumnModel() {
            private static final long serialVersionUID = 1338172152164826400L;

            @Override
            public void addColumn(TableColumn column) {
                super.addColumn(column);
                if (column.getModelIndex() == 0) {
                    column.setPreferredWidth(ContextPanel.this.getWidth() - 50);
                } else {
                    column.setResizable(false);
                }
            }
        });
        youtubeResultTable.setModel(new YoutubeResultTableModel());

        JScrollPane youtubeScrollPane = new JScrollPane(youtubeResultTable);
        youtubeScrollPane.setAutoscrolls(false);
        youtubeScrollPane.setOpaque(false);

        moreResultsFromYouTubeButton = new JButton(Actions.getAction(MoreResultsFromYouTubeAction.class));
        showResultAtYouTube = new JButton(Actions.getAction(OpenYouTubeResultsAction.class));

        //scrollpane
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        youtubePanel.add(youtubeScrollPane, c);

        c.gridy = 1;
        c.weighty = 0;
        c.weightx = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 0, 0);
        youtubePanel.add(moreResultsFromYouTubeButton, c);

        c.gridx = 1;
        c.anchor = GridBagConstraints.EAST;
        youtubePanel.add(showResultAtYouTube, c);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0.3;
        c.insets = new Insets(0, 0, 0, 0);
        c.fill = GridBagConstraints.BOTH;
        youtubeTabPanel.add(youtubePanel, c);
    }

    public JTable getYoutubeTable() {
        return youtubeResultTable;
    }

    public JPanel getYoutubePanel() {
        return youtubeTabPanel;
    }

    private void createYoutubeResultTableContextMenu(final SubstanceContextImageJTable youtubeResultTable) {
        youtubeTableMenu = new JPopupMenu();
        youtubeTableMenu.add(new JMenuItem(Actions.getAction(OpenYoutubeVideoInBrowserAction.class)));
        youtubeTableMenu.add(new JMenuItem(Actions.getAction(DownloadFromYoutubeAction.class)));
        youtubeTableMenu.setInvoker(youtubeResultTable);
    }

    /**
     * Shows all tabs in tabbed pane with given first tab title and icon
     * 
     * @param enabled
     */
    public void showAllTabs(String tabTitle, Icon firstTabIcon, boolean enabled) {
        firstTabTitle = tabTitle;
        tabbedPane.addTab(null, firstTabIcon, audioObjectPanel);
        tabbedPane.addTab(null, ImageLoader.getImage(ImageLoader.ALBUM), albumTabPanel);
        tabbedPane.addTab(null, ImageLoader.getImage(ImageLoader.ARTIST), artistTabPanel);
        tabbedPane.addTab(null, ImageLoader.getImage(ImageLoader.ARTIST_SIMILAR), similarTabPanel);
        tabbedPane.addTab(null, ImageLoader.getImage(ImageLoader.YOUTUBE), youtubeTabPanel);
        setContextTabsText(ApplicationState.getInstance().isShowContextTabsText());
        tabbedPane.setEnabledAt(0, enabled);
        tabbedPane.setEnabledAt(1, enabled);
        tabbedPane.setEnabledAt(2, enabled);
        tabbedPane.setEnabledAt(3, enabled);
        tabbedPane.setEnabledAt(4, enabled);
    }

    /**
     * Show all tabs in tabbed pane for AudioFile objects
     * 
     * @param enabled
     */
    public void showAllTabs(boolean enabled) {
        showAllTabs(LanguageTool.getString("SONG"), ImageLoader.getImage(ImageLoader.AUDIO_FILE_LITTLE), enabled);
    }

    /**
     * Shows only first tab in tabbed pane with given tab title
     * 
     * @param tabTitle
     */
    public void showFirstTab(String tabTitle, Icon icon) {
        tabbedPane.removeAll();
        firstTabTitle = tabTitle;
        tabbedPane.addTab(tabTitle, icon, audioObjectPanel);
    }

    /**
     * Method to show (<code>true</code>) or not (<code>false</code>) context
     * tabs text
     * 
     * @param set
     */
    public void setContextTabsText(boolean set) {
        tabbedPane.setTitleAt(0, set ? firstTabTitle : null);
        tabbedPane.setTitleAt(1, set ? LanguageTool.getString("ALBUM") : null);
        tabbedPane.setTitleAt(2, set ? LanguageTool.getString("ARTIST") : null);
        tabbedPane.setTitleAt(3, set ? LanguageTool.getString("SIMILAR") : null);
        tabbedPane.setTitleAt(4, set ? "YouTube" : null);
        tabbedPane.setToolTipTextAt(0, firstTabTitle);
        tabbedPane.setToolTipTextAt(1, LanguageTool.getString("ALBUM"));
        tabbedPane.setToolTipTextAt(2, LanguageTool.getString("ARTIST"));
        tabbedPane.setToolTipTextAt(3, LanguageTool.getString("SIMILAR"));
        tabbedPane.setToolTipTextAt(4, LanguageTool.getString("YOUTUBE_VIDEOS"));
    }

    /**
     * @return the youtubeTableMenu
     */
    public JPopupMenu getYoutubeTableMenu() {
        return youtubeTableMenu;
    }
}
