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

package net.sourceforge.atunes.kernel.controllers.context;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.ContextAlbumsTableModel;
import net.sourceforge.atunes.gui.model.ContextArtistsTableModel;
import net.sourceforge.atunes.gui.model.ContextTracksTableModel;
import net.sourceforge.atunes.gui.model.YoutubeResultTableModel;
import net.sourceforge.atunes.gui.views.panels.ContextPanel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddBannedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.AddLovedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.CopyLyricToClipboardAction;
import net.sourceforge.atunes.kernel.actions.OpenLyricsSourceAction;
import net.sourceforge.atunes.kernel.actions.OpenYouTubeResultsAction;
import net.sourceforge.atunes.kernel.controllers.model.PanelController;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.ArtistInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.context.Lyrics;
import net.sourceforge.atunes.kernel.modules.context.TrackInfo;
import net.sourceforge.atunes.kernel.modules.context.youtube.YoutubeResultEntry;
import net.sourceforge.atunes.kernel.modules.desktop.DesktopHandler;
import net.sourceforge.atunes.kernel.modules.internetsearch.SearchFactory;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.AudioFileStats;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.ClipboardFacade;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.border.DropShadowBorder;
import org.jfree.ui.ExtensionFileFilter;

/**
 * The Class ContextPanelController.
 */
public class ContextPanelController extends PanelController<ContextPanel> {

    static Logger logger = new Logger();

    private String lyricsSourceUrl;
    AudioObject audioObject;

    /**
     * Instantiates a new audio scrobbler controller.
     * 
     * @param panel
     *            the panel
     */
    public ContextPanelController(ContextPanel panel) {
        super(panel);
        addBindings();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.kernel.controllers.model.Controller#addBindings()
     */
    @Override
    protected void addBindings() {
        getPanelControlled().getTracksTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedTrack = getPanelControlled().getTracksTable().getSelectedRow();
                    if (selectedTrack != -1) {
                        TrackInfo track = ((ContextTracksTableModel) getPanelControlled().getTracksTable().getModel()).getTrack(selectedTrack);
                        DesktopHandler.getInstance().openURL(track.getUrl());
                    }
                }
            }
        });

        getPanelControlled().getAlbumsTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedAlbum = getPanelControlled().getAlbumsTable().getSelectedRow();
                    if (selectedAlbum != -1) {
                        AlbumInfo album = ((ContextAlbumsTableModel) getPanelControlled().getAlbumsTable().getModel()).getAlbum(selectedAlbum);
                        DesktopHandler.getInstance().openURL(album.getUrl());
                    }
                }
            }
        });

        getPanelControlled().getSimilarArtistsTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedArtist = getPanelControlled().getSimilarArtistsTable().getSelectedRow();
                    if (selectedArtist != -1) {
                        ArtistInfo artist = ((ContextArtistsTableModel) getPanelControlled().getSimilarArtistsTable().getModel()).getArtist(selectedArtist);
                        DesktopHandler.getInstance().openURL(artist.getUrl());
                    }
                }
            }
        });

        getPanelControlled().getTabbedPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = getPanelControlled().getTabbedPane().indexAtLocation(e.getX(), e.getY());
                if (i != -1) {
                    ApplicationState.getInstance().setSelectedContextTab(i);
                }
            }
        });
        getPanelControlled().getYoutubeTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
                if (e.getButton() == MouseEvent.BUTTON1) {
                    ControllerProxy.getInstance().getContextPanelController().playVideoAtYoutube();
                }
            }

            private void showPopup(MouseEvent e) {
                getPanelControlled().getYoutubeTable().getSelectionModel().setSelectionInterval(getPanelControlled().getYoutubeTable().rowAtPoint(e.getPoint()),
                        getPanelControlled().getYoutubeTable().rowAtPoint(e.getPoint()));
                getPanelControlled().getYoutubeTableMenu().show(e.getComponent(), e.getX(), e.getY());
            }
        });
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
     * Clear.
     * 
     * @param clearArtistAndSimilar
     *            the clear artist and similar
     */
    public void clear(boolean clearArtistAndSimilar) {
        lyricsSourceUrl = null;
        audioObject = null;

        getPanelControlled().getTracksTable().setModel(new ContextTracksTableModel(null));
        getPanelControlled().getAlbumPanel().setVisible(false);
        getPanelControlled().getAudioObjectCover().setVisible(false);
        getPanelControlled().getLyricsContainer().setText("");
        getPanelControlled().getAudioObjectLabel().setText("");
        getPanelControlled().getAudioObjectLabel().setToolTipText(null);
        getPanelControlled().getAudioObjectArtistLabel().setText("");
        getPanelControlled().getAudioObjectLastPlayDateLabel().setText("");

        getPanelControlled().getAddLyrics().setEnabled(false);
        getPanelControlled().getLyricScrollPane().setEnabled(false);

        getPanelControlled().getAddLyrics().setVisible(false);
        getPanelControlled().getAddLyrics().removeAll();

        getPanelControlled().getLyricScrollPane().setVisible(false);
        getPanelControlled().getOptionsButton().setVisible(false);

        Actions.getAction(CopyLyricToClipboardAction.class).setEnabled(false);
        Actions.getAction(OpenLyricsSourceAction.class).setEnabled(false);
        Actions.getAction(OpenYouTubeResultsAction.class).setEnabled(false);
        Actions.getAction(AddLovedSongInLastFMAction.class).setEnabled(false);
        Actions.getAction(AddBannedSongInLastFMAction.class).setEnabled(false);

        if (clearArtistAndSimilar) {
            getPanelControlled().getArtistImageLabel().setVisible(false);
            getPanelControlled().getArtistAlbumsLabel().setText("", "");
            getPanelControlled().getAlbumsPanel().setVisible(false);
            getPanelControlled().getSimilarArtistsPanel().setVisible(false);
            getPanelControlled().getArtistLabel().setText("", "");
        }

        ((YoutubeResultTableModel) getPanelControlled().getYoutubeTable().getModel()).clearEntries();
    }

    /**
     * Clear albums container.
     */
    public void clearAlbumsContainer() {
        getPanelControlled().getAlbumsTable().setModel(new ContextAlbumsTableModel());
    }

    /**
     * Clear similar artists container.
     */
    public void clearSimilarArtistsContainer() {
        getPanelControlled().getSimilarArtistsTable().setModel(new ContextArtistsTableModel());
    }

    /**
     * Copies current lyric to clipboard.
     */
    public void copyToClipboard() {
        String sLyric = getPanelControlled().getLyricsContainer().getText();
        if (sLyric == null) {
            sLyric = "";
        }
        ClipboardFacade.copyToClipboard(sLyric);
    }

    /**
     * Fill albums list.
     * 
     * @param album
     *            the album
     * @param cover
     *            the cover
     */
    void fillAlbumsList(AlbumInfo album, Image cover) {
        if (cover != null) {
            album.setCover(ImageUtils.scaleImageBicubic(cover, Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT));
        } else {
            album.setCover(ImageLoader.EMPTY_CONTEXT);
        }
        ((ContextAlbumsTableModel) getPanelControlled().getAlbumsTable().getModel()).addAlbum(album);

        getPanelControlled().getAlbumsTable().revalidate();
        getPanelControlled().getAlbumsTable().repaint();
    }

    /**
     * Fill similar artists list.
     * 
     * @param artist
     *            the artist
     * @param img
     *            the img
     */
    void fillSimilarArtistsList(ArtistInfo artist, Image img) {
        if (img != null) {
            artist.setImage(ImageUtils.scaleImageBicubic(img, Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT));
        } else {
            artist.setImage(ImageLoader.EMPTY_CONTEXT);
        }
        ((ContextArtistsTableModel) getPanelControlled().getSimilarArtistsTable().getModel()).addArtist(artist);

        getPanelControlled().getSimilarArtistsTable().revalidate();
        getPanelControlled().getSimilarArtistsTable().repaint();
    }

    /**
     * Gets the panel controlled.
     * 
     * @return the panel controlled
     */
    @Override
    protected ContextPanel getPanelControlled() {
        return super.getPanelControlled();
    }

    /**
     * Notify artist image.
     * 
     * @param img
     *            the img
     */
    public void notifyArtistImage(final Image img) {
        getPanelControlled().getArtistImageLabel().setIcon(new ImageIcon(img));
        getPanelControlled().getArtistImageLabel().setVisible(true);
    }

    /**
     * Notify finish get album info.
     * 
     * @param artist
     *            the artist
     * @param album
     *            the album
     * @param img
     *            the img
     */
    public void notifyFinishGetAlbumInfo(final String artist, final AlbumInfo album, final Image img) {
        getPanelControlled().getArtistLabel().setText(album != null ? album.getArtist() : artist, album != null ? album.getArtistUrl() : null);
        getPanelControlled().getAlbumLabel().setText(album != null ? album.getTitle() : Album.getUnknownAlbum(), album != null ? album.getUrl() : null);

        getPanelControlled().getArtistLabel().setEnabled(album != null && album.getArtistUrl() != null);
        getPanelControlled().getAlbumLabel().setEnabled(album != null && album.getUrl() != null);

        getPanelControlled().getYearLabel().setText(album != null ? album.getYear() : "",
                album != null && album.getYear() != null ? StringUtils.getString("http://en.wikipedia.org/wiki/", album.getYear()) : null);
        if (img != null) {
            ImageIcon image = ImageUtils.resize(new ImageIcon(img), Constants.ALBUM_IMAGE_SIZE, Constants.ALBUM_IMAGE_SIZE);
            getPanelControlled().getAlbumCoverLabel().setIcon(image);
        }
        getPanelControlled().getAlbumCoverLabel().setVisible(img != null);
        getPanelControlled().getTracksTable().setModel(new ContextTracksTableModel(album));
        getPanelControlled().getAlbumPanel().setVisible(true);
    }

    /**
     * Notify finish get albums info.
     * 
     * @param album
     *            the album
     * @param cover
     *            the cover
     */
    public void notifyFinishGetAlbumsInfo(final AlbumInfo album, final Image cover) {
        if (album.getArtist().equals(audioObject.getArtist())) {
            getPanelControlled().getArtistAlbumsLabel().setText(album.getArtist(), album.getArtistUrl());
            getPanelControlled().getArtistAlbumsLabel().setVisible(true);
        }

        if (!getPanelControlled().getAlbumsPanel().isVisible()) {
            getPanelControlled().getAlbumsPanel().setVisible(true);
        }
        fillAlbumsList(album, cover);
    }

    /**
     * Notify finish get similar artist.
     * 
     * @param artist
     *            the artist
     * @param img
     *            the img
     */
    public void notifyFinishGetSimilarArtist(final ArtistInfo artist, final Image img) {
        getPanelControlled().getSimilarArtistsPanel().setVisible(true);
        fillSimilarArtistsList(artist, img);
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
     * Opens a navigator to search for video.
     */
    public void openYoutube() {
        DesktopHandler.getInstance().openSearch(SearchFactory.getSearchForName("YouTube"), ContextHandler.getInstance().getYoutubeSearchForAudioObject(audioObject));
    }

    /**
     * Add a song as loved track in Last.fm
     */
    public void addLovedSong() {
        ContextHandler.getInstance().loveSong(audioObject);
    }

    /**
     * Add a song as banned track in Last.fm
     */
    public void addBannedSong() {
        ContextHandler.getInstance().banSong(audioObject);
    }

    public void openLyricsSourceUrl() {
        if (lyricsSourceUrl != null && !lyricsSourceUrl.trim().isEmpty()) {
            DesktopHandler.getInstance().openURL(lyricsSourceUrl);
        } else {
            if (audioObject instanceof AudioFile) {
                ControllerProxy.getInstance().getEditTagDialogController().editFiles(Arrays.asList((AudioFile) audioObject));
            }
        }
    }

    /**
     * Sets the audio object info
     * 
     * @param audioObject
     *            the audio object
     */
    public void setAudioObjectInfo(final AudioObject audioObject) {
        lyricsSourceUrl = null;
        this.audioObject = audioObject;

        Actions.getAction(CopyLyricToClipboardAction.class).setEnabled(false);
        Actions.getAction(OpenLyricsSourceAction.class).setEnabled(false);
        Actions.getAction(OpenYouTubeResultsAction.class).setEnabled(true);
        getPanelControlled().getOptionsButton().setVisible(true);

        // Allow to add loved or banned tracks only if Last.fm is enabled
        Actions.getAction(AddLovedSongInLastFMAction.class).setEnabled(ApplicationState.getInstance().isLastFmEnabled());
        Actions.getAction(AddBannedSongInLastFMAction.class).setEnabled(ApplicationState.getInstance().isLastFmEnabled());

        getPanelControlled().getAddLyrics().setEnabled(false);
        getPanelControlled().getAddLyrics().setVisible(true);
        getPanelControlled().getAddLyrics().removeAll();

        ((YoutubeResultTableModel) getPanelControlled().getYoutubeTable().getModel()).clearEntries();

        if (audioObject instanceof AudioFile) {
            getPanelControlled().getLyricsContainer().setText("");
            getPanelControlled().getLyricScrollPane().setVisible(true);
            getPanelControlled().getAudioObjectLabel().setText(audioObject.getTitleOrFileName());
            getPanelControlled().getAudioObjectArtistLabel().setText(audioObject.getArtist());

            // Get cover for audio file (internal or external: if its not found then get from last.fm)
            final AudioObject usedAudioObject = audioObject;
            new SwingWorker<ImageIcon, Void>() {

                @Override
                protected ImageIcon doInBackground() {
                    ImageIcon localImage = audioObject.getCustomImage(Constants.ALBUM_IMAGE_SIZE, Constants.ALBUM_IMAGE_SIZE);
                    if (localImage == null) {
                        Image image = ContextHandler.getInstance().getAlbumCover((AudioFile) audioObject);
                        if (image != null) {
                            localImage = ImageUtils.resize(new ImageIcon(image), Constants.ALBUM_IMAGE_SIZE, Constants.ALBUM_IMAGE_SIZE);
                        }
                    }
                    return localImage;
                }

                @Override
                protected void done() {
                    if (usedAudioObject.equals(ContextPanelController.this.audioObject)) {
                        try {
                            ImageIcon image = get();
                            if (image == null) {
                                image = ImageLoader.NO_COVER;
                            }
                            getPanelControlled().getAudioObjectCover().setIcon(image);
                            getPanelControlled().getAudioObjectCover().setBorder(new DropShadowBorder());
                            getPanelControlled().getAudioObjectCover().setVisible(true);
                        } catch (InterruptedException e) {
                            logger.error(LogCategories.IMAGE, e);
                        } catch (ExecutionException e) {
                            logger.error(LogCategories.IMAGE, e);
                        }
                    }
                }

            }.execute();

            // Get last date played
            AudioFileStats stats = RepositoryHandler.getInstance().getAudioFileStatistics((AudioFile) audioObject);
            if (stats == null) {
                getPanelControlled().getAudioObjectLastPlayDateLabel().setText(LanguageTool.getString("SONG_NEVER_PLAYED"));
            } else {
                // If song is playing, take previous play, if not, take last
                Date date;
                if (PlayerHandler.getInstance().isEnginePlaying()) {
                    date = stats.getPreviousPlayed();
                } else {
                    date = stats.getLastPlayed();
                }

                // If date is null -> never played
                if (date == null) {
                    getPanelControlled().getAudioObjectLastPlayDateLabel().setText(LanguageTool.getString("SONG_NEVER_PLAYED"));
                } else {
                    getPanelControlled().getAudioObjectLastPlayDateLabel().setText(
                            StringUtils.getString("<html>", LanguageTool.getString("LAST_DATE_PLAYED"), ":<br/><center> ", DateUtils.toString(date), "<center></html>"));
                }
            }
        } else if (audioObject instanceof Radio) {
            //clear(true);
            Radio radio = (Radio) audioObject;
            getPanelControlled().getAudioObjectCover().setIcon(ImageLoader.RADIO);
            getPanelControlled().getAudioObjectCover().setBorder(BorderFactory.createEmptyBorder());
            getPanelControlled().getAudioObjectCover().setVisible(true);
            getPanelControlled().getAudioObjectLabel().setText(radio.getName());
            getPanelControlled().getAudioObjectArtistLabel().setText(radio.getUrl());
            getPanelControlled().getLyricScrollPane().setVisible(true);
            getPanelControlled().getLyricsContainer().setLineWrap(true);
        } else if (audioObject instanceof PodcastFeedEntry) {
            //clear(true);
            PodcastFeedEntry podcastFeedEntry = (PodcastFeedEntry) audioObject;
            getPanelControlled().getAudioObjectCover().setIcon(ImageLoader.RSS);
            getPanelControlled().getAudioObjectCover().setBorder(BorderFactory.createEmptyBorder());
            getPanelControlled().getAudioObjectCover().setVisible(true);
            getPanelControlled().getAudioObjectLabel().setText(podcastFeedEntry.getName());
            getPanelControlled().getAudioObjectLabel().setToolTipText(podcastFeedEntry.getName());
            getPanelControlled().getLyricScrollPane().setVisible(true);
            getPanelControlled().getLyricsContainer().setText(podcastFeedEntry.getDescription());
            getPanelControlled().getLyricsContainer().setCaretPosition(0);
            getPanelControlled().getLyricsContainer().setLineWrap(true);
        }
    }

    /**
     * Sets the lyrics.
     * 
     * @param audioObject
     *            the audio object
     * @param lyrics
     *            the lyrics
     */
    public void setLyrics(AudioObject audioObject, Lyrics lyrics) {
        getPanelControlled().getLyricsContainer().setLineWrap(false);
        getPanelControlled().getLyricsContainer().setText(lyrics != null ? lyrics.getLyrics() : "");
        getPanelControlled().getLyricsContainer().setCaretPosition(0);

        boolean lyricsNotEmpty = lyrics != null && !lyrics.getLyrics().trim().isEmpty();
        Actions.getAction(CopyLyricToClipboardAction.class).setEnabled(lyricsNotEmpty);
        Actions.getAction(OpenLyricsSourceAction.class).setEnabled(lyricsNotEmpty);
        getPanelControlled().getAddLyrics().setEnabled(!lyricsNotEmpty);

        lyricsSourceUrl = lyrics != null ? lyrics.getUrl() : null;
        //getPanelControlled().getOpenLyricsSource().setEnabled(lyricsSourceUrl != null && !lyricsSourceUrl.trim().isEmpty());

        if (!lyricsNotEmpty) {
            JMenu menu = getPanelControlled().getAddLyrics();
            menu.removeAll();
            for (final Entry<String, String> entry : ContextHandler.getInstance().getUrlsForAddingLyrics(audioObject.getArtist(), audioObject.getTitle()).entrySet()) {
                JMenuItem mi = new JMenuItem(entry.getKey());
                mi.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DesktopHandler.getInstance().openURL(entry.getValue());
                    }
                });
                menu.add(mi);
            }
            menu.setEnabled(menu.getMenuComponentCount() > 0);
        }
    }

    /**
     * Sets the wiki information.
     * 
     * @param wikiText
     *            the wiki text
     * @param wikiURL
     *            the wiki url
     */
    public void setWikiInformation(String wikiText, String wikiURL) {
        getPanelControlled().getArtistWikiAbstract().setText(wikiText);
        getPanelControlled().getArtistWikiAbstract().setCaretPosition(0);
        getPanelControlled().getArtistWikiReadMore().setText(LanguageTool.getString("READ_MORE"), wikiURL);
    }

    /**
     * Delegate method to show all tabs disabled
     */
    public void showAllTabsDisabled() {
        getPanelControlled().showAllTabs(false);
    }

    public void showFirstTab(String string, ImageIcon icon) {
        getPanelControlled().showFirstTab(string, icon);
    }

    public void showAllTabs(String firstTabTitle, ImageIcon firstTabIcon, boolean enabled) {
        getPanelControlled().showAllTabs(firstTabTitle, firstTabIcon, enabled);
    }

    public void showAllTabs(boolean b) {
        getPanelControlled().showAllTabs(b);
    }

    public void setSelectedIndex(int index) {
        getPanelControlled().getTabbedPane().setSelectedIndex(index);
    }

    /**
     * Called when search at YouTube finished and results are available
     * 
     * @param result
     */
    public void notifyYoutubeSearchRetrieved(List<YoutubeResultEntry> result) {
        addYoutubeResults(result);
    }

    /**
     * Adds entries to youtube table
     * 
     * @param newEntries
     */
    public void addYoutubeResults(List<YoutubeResultEntry> newEntries) {
        ((YoutubeResultTableModel) getPanelControlled().getYoutubeTable().getModel()).addEntries(newEntries);
    }

    /**
     * Opens a dialog to select file and starts downloading video
     */
    public void downloadYoutubeVideo() {
        int selectedVideo = getPanelControlled().getYoutubeTable().getSelectedRow();
        if (selectedVideo != -1) {
            // get entry
            YoutubeResultEntry entry = ((YoutubeResultTableModel) getPanelControlled().getYoutubeTable().getModel()).getEntry(selectedVideo);

            // Open save dialog to select file
            JFileChooser dialog = new JFileChooser();
            dialog.setDialogType(JFileChooser.SAVE_DIALOG);
            dialog.setDialogTitle(LanguageTool.getString("SAVE_YOUTUBE_VIDEO"));
            dialog.setFileFilter(new ExtensionFileFilter("FLV", "FLV"));
            // Set default file name
            // for some reason dialog fails with files with [ or ] chars
            File defaultFileName = new File(FileNameUtils.getValidFileName(entry.getName().replace("\\", "\\\\").replace("$", "\\$").replace('[', ' ').replace(']', ' ')));
            dialog.setSelectedFile(defaultFileName);
            int returnValue = dialog.showSaveDialog(VisualHandler.getInstance().getFrame().getFrame());
            File selectedFile = dialog.getSelectedFile();
            if (selectedFile != null && JFileChooser.APPROVE_OPTION == returnValue) {
                ContextHandler.getInstance().downloadYoutubeVideo(entry, selectedFile);
            }
        }
    }

    /**
     * Opens a web browser to play YouTube video
     */
    public void playVideoAtYoutube() {
        int selectedVideo = getPanelControlled().getYoutubeTable().getSelectedRow();
        if (selectedVideo != -1) {
            // get entry
            YoutubeResultEntry entry = ((YoutubeResultTableModel) getPanelControlled().getYoutubeTable().getModel()).getEntry(selectedVideo);
            if (entry.getUrl() != null) {
                //open youtube url
                DesktopHandler.getInstance().openURL(entry.getUrl());
                // When playing a video in web browser automatically pause current song
                if (PlayerHandler.getInstance().isEnginePlaying()) {
                    PlayerHandler.getInstance().playCurrentAudioObject(true);
                }
            }
        }
    }
}
