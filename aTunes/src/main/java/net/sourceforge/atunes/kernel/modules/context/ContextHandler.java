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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.Cursor;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.ApplicationFinishListener;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AddBannedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.AddLovedSongInLastFMAction;
import net.sourceforge.atunes.kernel.actions.ImportLovedTracksFromLastFM;
import net.sourceforge.atunes.kernel.modules.context.lastfm.LastFmCache;
import net.sourceforge.atunes.kernel.modules.context.lastfm.LastFmService;
import net.sourceforge.atunes.kernel.modules.context.lastfm.ScrobblerException;
import net.sourceforge.atunes.kernel.modules.context.lastfm.callables.LastFmAlbumCoverCallable;
import net.sourceforge.atunes.kernel.modules.context.lastfm.data.LastFmLovedTrack;
import net.sourceforge.atunes.kernel.modules.context.lastfm.runnables.LastFmRunnable;
import net.sourceforge.atunes.kernel.modules.context.lyrics.LyricsCache;
import net.sourceforge.atunes.kernel.modules.context.lyrics.LyricsRunnable;
import net.sourceforge.atunes.kernel.modules.context.lyrics.LyricsService;
import net.sourceforge.atunes.kernel.modules.context.lyrics.engines.LyricsEngine;
import net.sourceforge.atunes.kernel.modules.context.lyrics.engines.LyricsEngineInfo;
import net.sourceforge.atunes.kernel.modules.context.youtube.YoutubeMoreResultsRunnable;
import net.sourceforge.atunes.kernel.modules.context.youtube.YoutubeResultEntry;
import net.sourceforge.atunes.kernel.modules.context.youtube.YoutubeRunnable;
import net.sourceforge.atunes.kernel.modules.context.youtube.YoutubeService;
import net.sourceforge.atunes.kernel.modules.context.youtube.YoutubeVideoDownloader;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateChangeListener;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class ContextHandler.
 */
public final class ContextHandler implements ContextListener, ApplicationFinishListener, ApplicationStateChangeListener {

    private static ContextHandler instance;

    Logger logger = new Logger();

    /** Executor service for Last.fm and Youtube runnables */
    private ExecutorService executorService = Executors.newCachedThreadPool();

    /** Submissions need single threaded executor */
    private ExecutorService scrobblerExecutorService = Executors.newSingleThreadExecutor();

    LastFmService lastFmService;
    LastFmCache lastFmCache;
    LyricsService lyricsService;
    LyricsCache lyricsCache;
    YoutubeService yts;

    /**
     * Stores last Youtube search string to be used when calling for more
     * results
     */
    private String lastYoutubeSearchString;

    /**
     * Stores last Youtube start index to be used when calling for more results
     */
    private int lastYoutubeStartIndex;

    private AlbumInfo album;
    private Image image;
    private List<AlbumInfo> albums;
    private LastFmRunnable currentWorker;
    private volatile long currentWorkerId;
    private volatile String lastAlbumRetrieved;
    private volatile String lastArtistRetrieved;

    /**
     * The last audio object used to retrieve information
     */
    private AudioObject lastAudioObject;

    /**
     * timestamp when audio object was modified. Used to decide if context info
     * must be updated
     */
    private long lastAudioObjectModificationTime;

    /**
     * Instantiates a new audio scrobbler service handler.
     */
    private ContextHandler() {
        this.lastFmCache = new LastFmCache();
        this.lyricsCache = new LyricsCache();
        updateLastFmService(ApplicationState.getInstance().getProxy(), ApplicationState.getInstance().getLastFmUser(), ApplicationState.getInstance().getLastFmPassword());
        updateYoutubeService(ApplicationState.getInstance().getProxy());
        updateLyricsService(loadEngines(ApplicationState.getInstance().getProxy()), lyricsCache);
        this.currentWorkerId = System.currentTimeMillis();
        Kernel.getInstance().addFinishListener(this);
        ApplicationStateHandler.getInstance().addStateChangeListener(this);
    }

    /**
     * Gets the single instance of ContextHandler.
     * 
     * @return single instance of ContextHandler
     */
    public static ContextHandler getInstance() {
        if (instance == null) {
            instance = new ContextHandler();
        }
        return instance;
    }

    /**
     * Cancel if working.
     */
    private void cancelIfWorking() {
        if (currentWorker != null) {
            currentWorkerId = System.currentTimeMillis();
            currentWorker.interrupt();
            currentWorker = null;
            logger.debug(LogCategories.HANDLER, "Suspended Thread");
        }
    }

    /**
     * Clear.
     */
    public void clear() {
        cancelIfWorking();
        ControllerProxy.getInstance().getContextPanelController().clear(true);
        ControllerProxy.getInstance().getContextPanelController().showAllTabsDisabled();
        clearLastRetrieved();
    }

    /**
     * <p>
     * Clears the caches (Last.fm & lyrics cache)
     * </p>
     * <p>
     * Deletes as many files in the cache directories as possible. Files that
     * are currently used by aTunes won't be deleted.
     * </p>
     */
    public void clearCaches() {
        SwingWorker<Boolean, Void> clearCaches = new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                boolean exception;
                exception = lastFmCache.clearCache();
                exception = lyricsCache.clearCache() || exception;
                logger.debug(LogCategories.HANDLER);
                logger.debug(LogCategories.HANDLER, exception);
                return exception;
            }

            @Override
            protected void done() {
                VisualHandler.getInstance().getEditPreferencesDialog().setCursor(Cursor.getDefaultCursor());
                VisualHandler.getInstance().getEditPreferencesDialog().getContextPanel().getClearCache().setEnabled(true);
            }
        };
        VisualHandler.getInstance().getEditPreferencesDialog().getContextPanel().getClearCache().setEnabled(false);
        VisualHandler.getInstance().getEditPreferencesDialog().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        clearCaches.execute();
    }

    /**
     * Clear last retrieved.
     */
    private void clearLastRetrieved() {
        lastAlbumRetrieved = null;
        lastArtistRetrieved = null;
        lastAudioObject = null;
        lastAudioObjectModificationTime = 0;
    }

    /**
     * Gets the album.
     * 
     * @return the album
     */
    public AlbumInfo getAlbum() {
        return album;
    }

    /**
     * Gets the album cover.
     * 
     * @param file
     *            the file
     * 
     * @return the album cover
     */
    public Image getAlbumCover(AudioFile file) {
        LastFmAlbumCoverCallable lastFmAlbumCoverCallable = new LastFmAlbumCoverCallable(lastFmService, file);
        try {
            return lastFmAlbumCoverCallable.call();
        } catch (Exception e) {
            logger.error(LogCategories.HANDLER, e);
            return null;
        }
    }

    @Override
    public List<AlbumInfo> getAlbums() {
        return albums != null ? new ArrayList<AlbumInfo>(albums) : null;
    }

    /**
     * Gets the genres for files.
     * 
     * @param files
     *            the files
     * 
     * @return the genres for files
     */
    public Map<AudioFile, String> getGenresForFiles(List<AudioFile> files) {
        Map<AudioFile, String> result = new HashMap<AudioFile, String>();

        Map<String, String> tagCache = new HashMap<String, String>();

        for (AudioFile f : files) {
            if (!Artist.isUnknownArtist(f.getArtist())) {
                String tag = null;
                if (tagCache.containsKey(f.getArtist())) {
                    tag = tagCache.get(f.getArtist());
                } else {
                    tag = lastFmService.getArtistTopTag(f.getArtist());
                    tagCache.put(f.getArtist(), tag);
                    // Wait one second to avoid IP banning
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // Nothing to do
                    }
                }
                if (tag != null) {
                    result.put(f, tag);
                }
            }
        }

        return result;
    }

    /**
     * Gets the covers for files.
     * 
     * @param files
     *            the files
     * 
     * @return the covers for files
     */
    public Map<AudioFile, Image> getCoversForFiles(List<AudioFile> files) {
        Map<AudioFile, Image> result = new HashMap<AudioFile, Image>();

        Map<Integer, Image> coverCache = new HashMap<Integer, Image>();

        for (AudioFile f : files) {
            if (!Artist.isUnknownArtist(f.getArtist()) && !Album.isUnknownAlbum(f.getAlbum())) {
                Image cover = null;
                int cacheKey = f.getArtist().hashCode() + f.getAlbum().hashCode();
                if (coverCache.containsKey(cacheKey)) {
                    cover = coverCache.get(cacheKey);
                } else {
                    AlbumInfo albumInfo = lastFmService.getAlbum(f.getArtist(), f.getAlbum());
                    if (albumInfo == null) {
                        continue;
                    }
                    cover = lastFmService.getImage(albumInfo);
                    if (cover == null) {
                        continue;
                    }
                    coverCache.put(cacheKey, cover);
                    // Wait one second to avoid IP banning
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // Nothing to do
                    }
                }
                if (cover != null) {
                    result.put(f, cover);
                }
            }
        }

        return result;
    }

    /**
     * Gets the last album retrieved.
     * 
     * @return the last album retrieved
     */
    public String getLastAlbumRetrieved() {
        return lastAlbumRetrieved;
    }

    /**
     * Gets the last artist retrieved.
     * 
     * @return the last artist retrieved
     */
    public String getLastArtistRetrieved() {
        return lastArtistRetrieved;
    }

    /**
     * Returns a hash of files with its songs titles.
     * 
     * @param files
     *            the files
     * 
     * @return the titles for files
     */

    public Map<AudioFile, String> getTitlesForFiles(List<AudioFile> files) {
        Map<AudioFile, String> result = new HashMap<AudioFile, String>();

        // For each file
        for (AudioFile f : files) {
            String title = getTitleForFile(f);
            if (title != null) {
                result.put(f, title);
            }
        }

        // Return files matched
        return result;
    }

    /**
     * Return title of an AudioFile
     * 
     * @param f
     * @return
     */
    public String getTitleForFile(AudioFile f) {
        // If has valid artist name, album name, and track number...
        if (!Artist.isUnknownArtist(f.getArtist()) && !Album.isUnknownAlbum(f.getAlbum()) && f.getTrackNumber() > 0) {
            // Find album
            AlbumInfo albumRetrieved = lastFmService.getAlbum(f.getArtist(), f.getAlbum());
            if (albumRetrieved != null) {
                if (albumRetrieved.getTracks().size() >= f.getTrackNumber()) {
                    // Get track
                    return albumRetrieved.getTracks().get(f.getTrackNumber() - 1).getTitle();
                }
            }
        }
        return null;
    }

    /**
     * Return track number of an AudioFile
     * 
     * @param f
     * @return
     */
    public int getTrackNumberForFile(AudioFile f) {
        // If has valid artist name, album name and title
        if (!Artist.isUnknownArtist(f.getArtist()) && !Album.isUnknownAlbum(f.getAlbum()) && !StringUtils.isEmpty(f.getTitle())) {
            // Find album
            AlbumInfo albumRetrieved = lastFmService.getAlbum(f.getArtist(), f.getAlbum());
            if (albumRetrieved != null) {
                // Try to match titles to get track
                int trackIndex = 1;
                for (TrackInfo track : albumRetrieved.getTracks()) {
                    if (track.getTitle().equalsIgnoreCase(f.getTitle())) {
                        return trackIndex;
                    }
                    trackIndex++;
                }
            }
        }
        return 0;
    }

    /**
     * Gets the track names for album.
     * 
     * @param artistName
     *            the artist name
     * @param albumName
     *            the album name
     * 
     * @return the track names for album
     */
    public List<String> getTrackNamesForAlbum(String artistName, String albumName) {
        AlbumInfo alb = lastFmService.getAlbum(artistName, albumName);
        if (alb == null) {
            return null;
        }
        logger.info(LogCategories.HANDLER, StringUtils.getString("Received track names for artist ", artistName, " album ", albumName));
        List<String> tracks = new ArrayList<String>();
        for (int i = 0; i < alb.getTracks().size(); i++) {
            tracks.add(alb.getTracks().get(i).getTitle());
        }
        return tracks;
    }

    /**
     * Returns the url for adding new lyrics to a lyric provider
     * 
     * @param artist
     *            artist
     * @param title
     *            title
     * @return the url for adding new lyrics to a lyric provider
     */
    public Map<String, String> getUrlsForAddingLyrics(String artist, String title) {
        Map<String, String> result = lyricsService.getUrlsForAddingNewLyrics(artist, title);
        Set<String> remove = new HashSet<String>();
        for (Entry<String, String> entry : result.entrySet()) {
            if (entry.getValue() == null || entry.getValue().trim().isEmpty()) {
                remove.add(entry.getKey());
            }
        }
        for (String key : remove) {
            result.remove(key);
        }
        return result;
    }

    @Override
    public void notifyAlbumRetrieved(AudioObject file, long id) {
        if (currentWorkerId != id) {
            return;
        }

        logger.debug(LogCategories.HANDLER);

        ControllerProxy.getInstance().getContextPanelController().notifyFinishGetAlbumInfo(file.getArtist(), album, image);

        lastAlbumRetrieved = file.getAlbum();
        lastArtistRetrieved = file.getArtist();
    }

    @Override
    public void notifyArtistImage(Image img, long id) {
        if (currentWorkerId != id) {
            return;
        }

        logger.debug(LogCategories.HANDLER);

        // Image is scaled to best fit panel
        ControllerProxy.getInstance().getContextPanelController().notifyArtistImage(
                ImageUtils.scaleImageBicubic(img, Constants.ARTIST_IMAGE_SIZE, Constants.ARTIST_IMAGE_SIZE).getImage());
    }

    @Override
    public void notifyCoverRetrieved(AlbumInfo alb, Image cover, long id) {
        if (currentWorkerId != id) {
            return;
        }

        logger.debug(LogCategories.HANDLER);

        ControllerProxy.getInstance().getContextPanelController().notifyFinishGetAlbumsInfo(alb, cover);

        lastArtistRetrieved = alb.getArtist();
    }

    @Override
    public void notifyFinishGetSimilarArtist(ArtistInfo a, Image img, long id) {
        if (currentWorkerId != id) {
            return;
        }

        logger.debug(LogCategories.HANDLER);

        // Update if artist is available
        a.setAvailable(RepositoryHandler.getInstance().getArtistStructure().containsKey(a.getName()));

        ControllerProxy.getInstance().getContextPanelController().notifyFinishGetSimilarArtist(a, img);
    }

    /**
     * Notify lyrics retrieved.
     * 
     * @param audioObject
     *            the audio object
     * @param lyrics
     *            the lyrics
     * @param id
     *            the id
     */
    @Override
    public void notifyLyricsRetrieved(AudioObject audioObject, Lyrics lyrics, long id) {
        if (currentWorkerId != id) {
            return;
        }

        ControllerProxy.getInstance().getContextPanelController().setLyrics(audioObject, lyrics);
    }

    @Override
    public void notifyStartRetrievingArtistImages(long id) {
        if (currentWorkerId != id) {
            return;
        }

        ControllerProxy.getInstance().getContextPanelController().clearSimilarArtistsContainer();
    }

    @Override
    public void notifyStartRetrievingCovers(long id) {
        if (currentWorkerId != id) {
            return;
        }

        ControllerProxy.getInstance().getContextPanelController().clearAlbumsContainer();
    }

    @Override
    public void notifyWikiInfoRetrieved(String wikiText, String wikiURL, long id) {
        if (currentWorkerId != id) {
            return;
        }

        ControllerProxy.getInstance().getContextPanelController().setWikiInformation(wikiText, wikiURL);
    }

    /**
     * Updates panel with audio object information.
     * 
     * @param ao
     *            the audio object
     */
    public void retrieveInfoAndShowInPanel(AudioObject ao) {
        logger.debug(LogCategories.HANDLER);

        // Avoid retrieve information about the same audio object twice except if is an AudioFile and has been recently changed
        if (lastAudioObject != null && lastAudioObject.equals(ao)) {
            if (ao instanceof AudioFile) {
                if (((AudioFile) ao).getFile().lastModified() == lastAudioObjectModificationTime) {
                    return;
                }
            } else if (!(ao instanceof Radio)) {
                return;
            }
        }
        lastAudioObject = ao;

        // Update modification time if necessary
        if (ao instanceof AudioFile) {
            lastAudioObjectModificationTime = ((AudioFile) ao).getFile().lastModified();
        } else {
            lastAudioObjectModificationTime = 0;
        }

        if (ApplicationState.getInstance().isUseContext()) {
            if (ao == null) {
                // Clear all tabs
                clear();
                ControllerProxy.getInstance().getContextPanelController().showAllTabsDisabled();
            } else {
                if (ao instanceof AudioFile) {
                    // Audio File -> show all tabs
                    ControllerProxy.getInstance().getContextPanelController().showAllTabs(true);
                    ControllerProxy.getInstance().getContextPanelController().setSelectedIndex(ApplicationState.getInstance().getSelectedContextTab());
                } else if (ao instanceof Radio) {
                    // Radio with song information -> show all tabs
                    if (((Radio) ao).isSongInfoAvailable()) {
                        ControllerProxy.getInstance().getContextPanelController().showAllTabs(LanguageTool.getString("RADIO"), ImageLoader.RADIO_LITTLE, true);
                        ControllerProxy.getInstance().getContextPanelController().setSelectedIndex(ApplicationState.getInstance().getSelectedContextTab());
                    } else {
                        // Radio without song information -> show only first tab
                        ControllerProxy.getInstance().getContextPanelController().showFirstTab(LanguageTool.getString("RADIO"), ImageLoader.RADIO_LITTLE);
                    }
                } else if (ao instanceof PodcastFeedEntry) {
                    // Podcast -> show only first tab
                    ControllerProxy.getInstance().getContextPanelController().showFirstTab(LanguageTool.getString("PODCAST_FEED"), ImageLoader.RSS_LITTLE);
                }
                retrieveInfo(ao);
            }
        }
    }

    /**
     * Retrieve info.
     * 
     * @param audioObject
     *            the audio object
     */
    private void retrieveInfo(AudioObject audioObject) {
        logger.debug(LogCategories.HANDLER);

        if (audioObject == null) {
            return;
        }

        boolean getArtist = true;
        // If artist and album are equals than previous retrieved, there is nothing to do
        if (audioObject.getArtist().equals(lastArtistRetrieved) && audioObject.getAlbum().equals(lastAlbumRetrieved)) {
            ControllerProxy.getInstance().getContextPanelController().notifyFinishGetAlbumInfo(audioObject.getArtist(), album, image);
            if (audioObject instanceof AudioFile) {
                // Find lyrics
                LyricsRunnable lyricsRunnable = new LyricsRunnable(this, audioObject, currentWorkerId);
                executorService.submit(lyricsRunnable);

                //add youtube runnable
                YoutubeRunnable youtubeRunnable = new YoutubeRunnable(this, audioObject, currentWorkerId);
                executorService.submit(youtubeRunnable);
            }
            // Show audio object info
            ControllerProxy.getInstance().getContextPanelController().setAudioObjectInfo(audioObject);
            return;
        }
        // If artist is the same, but album not, don't get artist
        else if (audioObject.getArtist().equals(lastArtistRetrieved)) {
            getArtist = false;
        }

        cancelIfWorking();
        ControllerProxy.getInstance().getContextPanelController().clear(getArtist);

        // Show audio object info
        ControllerProxy.getInstance().getContextPanelController().setAudioObjectInfo(audioObject);

        currentWorkerId = System.currentTimeMillis();

        // Return if audio object is a podcast feed entry or a radio without song info
        if ((audioObject instanceof Radio && !((Radio) audioObject).isSongInfoAvailable()) || audioObject instanceof PodcastFeedEntry) {
            clearLastRetrieved();
            return;
        }

        // Find lyrics
        LyricsRunnable lyricsRunnable = new LyricsRunnable(this, audioObject, currentWorkerId);
        executorService.submit(lyricsRunnable);

        currentWorker = new LastFmRunnable(this, lastFmService, audioObject, currentWorkerId, executorService);
        currentWorker.setRetrieveArtistInfo(getArtist);
        executorService.submit(currentWorker);

        //add youtube runnable
        YoutubeRunnable youtubeRunnable = new YoutubeRunnable(this, audioObject, currentWorkerId);
        executorService.submit(youtubeRunnable);
    }

    private void savePicture(Image img, AudioFile file, long id) {
        if (currentWorkerId != id) {
            return;
        }

        logger.debug(LogCategories.HANDLER);

        if (img != null && ApplicationState.getInstance().isSaveContextPicture()) { // save image in folder of file
            String imageFileName = AudioFilePictureUtils.getFileNameForCover(file);

            File imageFile = new File(imageFileName);
            if (!imageFile.exists()) {
                // Save picture
                try {
                    ImageUtils.writeImageToFile(img, imageFileName);
                    // Add picture to songs of album
                    RepositoryHandler.getInstance().addExternalPictureForAlbum(file.getArtist(), file.getAlbum(), imageFile);

                    // Update file properties panel
                    ControllerProxy.getInstance().getFilePropertiesController().refreshPicture();
                } catch (IOException e) {
                    logger.internalError(e);
                }
            }
        }

    }

    @Override
    public void setAlbum(AlbumInfo album, long id) {
        if (currentWorkerId != id) {
            return;
        }

        this.album = album;
    }

    @Override
    public void setAlbums(List<? extends AlbumInfo> albums, long id) {
        if (currentWorkerId != id) {
            return;
        }

        this.albums = albums != null ? new ArrayList<AlbumInfo>(albums) : null;
    }

    @Override
    public void setImage(Image image, AudioObject ao, long id) {
        if (currentWorkerId != id) {
            return;
        }

        if (ao instanceof AudioFile & image != null) {
            savePicture(image, (AudioFile) ao, id);

        }
        this.image = image;
    }

    @Override
    public void setLastAlbumRetrieved(String lastAlbumRetrieved, long id) {
        if (currentWorkerId != id) {
            return;
        }

        this.lastAlbumRetrieved = lastAlbumRetrieved;
    }

    @Override
    public void setLastArtistRetrieved(String lastArtistRetrieved, long id) {
        if (currentWorkerId != id) {
            return;
        }

        this.lastArtistRetrieved = lastArtistRetrieved;
    }

    /**
     * Submit song to Last.fm
     * 
     * @param audioFile
     *            the file
     * @param secondsPlayed
     *            the seconds played
     */
    public void submitToLastFm(final AudioFile audioFile, final long secondsPlayed) {
        if (isLastFmEnabled()) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        lastFmService.submit(audioFile, secondsPlayed);
                    } catch (ScrobblerException e) {
                        if (e.getStatus() == 2) {
                            logger.error(LogCategories.SERVICE, "Authentication failure on Last.fm service");
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    VisualHandler.getInstance().showErrorDialog(LanguageTool.getString("LASTFM_USER_ERROR"));
                                    // Disable service by deleting password
                                    ApplicationState.getInstance().setLastFmEnabled(false);
                                }
                            });
                        } else {
                            logger.error(LogCategories.SERVICE, e.getMessage());
                        }
                    }
                }
            };
            try {
                scrobblerExecutorService.submit(r);
            } catch (RejectedExecutionException e) {
                logger.info(LogCategories.SERVICE, "execution of submission runnable rejected");
            }
        }

    }

    /**
     * Submits Last.fm cache
     */
    public void submitCacheToLastFm() {
        if (isLastFmEnabled()) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        lastFmService.submitCache();
                    } catch (ScrobblerException e) {
                        if (e.getStatus() == 2) {
                            logger.error(LogCategories.SERVICE, "Authentication failure on Last.fm service");
                        } else {
                            logger.error(LogCategories.SERVICE, e.getMessage());
                        }
                    }
                }
            };
            try {
                scrobblerExecutorService.submit(r);
            } catch (RejectedExecutionException e) {
                logger.info(LogCategories.SERVICE, "execution of cache submission runnable rejected");
            }
        }
    }

    /**
     * Submit now playing info to Last.fm
     * 
     * @param audioFile
     *            the file
     */
    public void submitNowPlayingInfoToLastFm(final AudioFile audioFile) {
        if (isLastFmEnabled()) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        lastFmService.submitNowPlayingInfo(audioFile);
                    } catch (ScrobblerException e) {
                        if (e.getStatus() == 2) {
                            logger.error(LogCategories.SERVICE, "Authentication failure on Last.fm service");
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    VisualHandler.getInstance().showErrorDialog(LanguageTool.getString("LASTFM_USER_ERROR"));
                                    // Disable service by deleting password
                                    ApplicationState.getInstance().setLastFmEnabled(false);
                                }
                            });
                        } else {
                            logger.error(LogCategories.SERVICE, e.getMessage());
                        }

                    }
                }
            };
            try {
                scrobblerExecutorService.submit(r);
            } catch (RejectedExecutionException e) {
                logger.info(LogCategories.SERVICE, "execution of now playing runnable rejected");
            }
        }
    }

    private boolean isLastFmEnabled() {
        return ApplicationState.getInstance().isLastFmEnabled();
    }

    /**
     * Update Last.fm service.
     * 
     * @param proxy
     *            the proxy
     * @param user
     *            the user
     * @param password
     *            the password
     */
    private void updateLastFmService(ProxyBean proxy, final String user, String password) {
        logger.debug(LogCategories.HANDLER);

        cancelIfWorking();
        Proxy p = null;
        try {
            if (proxy != null) {
                p = Proxy.getProxy(proxy);
            }
            lastFmService = new LastFmService(p, user, password, ApplicationState.getInstance().getLocale().getLocale(), lastFmCache);
            // Enable / Disable action to import loved tracks if no user
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Actions.getAction(ImportLovedTracksFromLastFM.class).setEnabled(user != null && !user.trim().equals(""));
                }
            });
        } catch (Exception e) {
            logger.error(LogCategories.HANDLER, e);
            lastFmService = null;
        }
        clearLastRetrieved();
    }

    private void updateYoutubeService(ProxyBean proxy) {
        logger.debug(LogCategories.HANDLER);

        Proxy p = null;
        try {
            if (proxy != null) {
                p = Proxy.getProxy(proxy);
            }
            yts = new YoutubeService(p);

        } catch (Exception e) {
            logger.error(LogCategories.HANDLER, e);
        }
        clearLastRetrieved();
    }

    public void updateLyricsService(List<LyricsEngine> lyricsEngines, LyricsCache lyricsCache) {
        logger.debug(LogCategories.HANDLER);
        this.lyricsService = new LyricsService(lyricsEngines, lyricsCache);
        clearLastRetrieved();
    }

    /**
     * Retrieves lyrics for a given artist and title
     * 
     * @param artist
     *            the artist
     * @param title
     *            the title
     * @return the retrieved lyrics (can be <code>null</code>)
     */
    public Lyrics getLyrics(String artist, String title) {
        return lyricsService.getLyrics(artist, title);
    }

    /**
     * Finish context information
     */
    public void applicationFinish() {
        logger.debug(LogCategories.HANDLER);
        executorService.shutdownNow();
        scrobblerExecutorService.shutdownNow();
    }

    /**
     * Calls last.fm service to add a song as favorite
     * 
     * @param song
     */
    public void loveSong(final AudioObject song) {
        Actions.getAction(AddLovedSongInLastFMAction.class).setEnabled(false);
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                lastFmService.addLovedSong(song);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException e) {
                    logger.error(LogCategories.SERVICE, e);
                } catch (ExecutionException e) {
                    logger.error(LogCategories.SERVICE, e);
                } finally {
                    Actions.getAction(AddLovedSongInLastFMAction.class).setEnabled(true);
                }
            }

        }.execute();
    }

    /**
     * Calls last.fm service to ban a song
     * 
     * @param song
     */
    public void banSong(final AudioObject song) {
        Actions.getAction(AddBannedSongInLastFMAction.class).setEnabled(false);
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                lastFmService.addBannedSong(song);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException e) {
                    logger.error(LogCategories.SERVICE, e);
                } catch (ExecutionException e) {
                    logger.error(LogCategories.SERVICE, e);
                } finally {
                    Actions.getAction(AddBannedSongInLastFMAction.class).setEnabled(true);
                }
            }

        }.execute();
    }

    /**
     * Loads lyrics engines
     * 
     * @return the lyrics engines
     */
    private List<LyricsEngine> loadEngines(ProxyBean proxy) {
        List<LyricsEngineInfo> lyricsEnginesInfo = ApplicationState.getInstance().getLyricsEnginesInfo();

        Proxy p = null;
        try {
            if (proxy != null) {
                p = Proxy.getProxy(proxy);
            }
        } catch (Exception e) {
            logger.error(LogCategories.HANDLER, e);
        }

        boolean loadDefault = false;
        if (lyricsEnginesInfo != null) {
            for (LyricsEngineInfo lyricsEngineInfo : lyricsEnginesInfo) {
                if (lyricsEngineInfo == null) {
                    loadDefault = true;
                    break;
                }
            }
        } else {
            loadDefault = true;
        }

        if (loadDefault) {
            lyricsEnginesInfo = new ArrayList<LyricsEngineInfo>();
            // default lyrics engines
            lyricsEnginesInfo.add(new LyricsEngineInfo("LyricWiki", "net.sourceforge.atunes.kernel.modules.context.lyrics.engines.LyricWikiEngine", true));
            lyricsEnginesInfo.add(new LyricsEngineInfo("Lyricsfly", "net.sourceforge.atunes.kernel.modules.context.lyrics.engines.LyricsflyEngine", true));
            lyricsEnginesInfo.add(new LyricsEngineInfo("LyricsDirectory", "net.sourceforge.atunes.kernel.modules.context.lyrics.engines.LyricsDirectoryEngine", false));
            lyricsEnginesInfo.add(new LyricsEngineInfo("LyrcEngine", "net.sourceforge.atunes.kernel.modules.context.lyrics.engines.LyrcEngine", false));
        } else {
            lyricsEnginesInfo = new ArrayList<LyricsEngineInfo>(lyricsEnginesInfo);
        }
        ApplicationState.getInstance().setLyricsEnginesInfo(lyricsEnginesInfo);

        List<LyricsEngine> result = new ArrayList<LyricsEngine>();
        // Get engines
        for (LyricsEngineInfo lyricsEngineInfo : lyricsEnginesInfo) {
            if (lyricsEngineInfo.isEnabled()) {
                try {
                    Class<?> clazz = Class.forName(lyricsEngineInfo.getClazz());
                    Constructor<?> constructor = clazz.getConstructor(Proxy.class);
                    result.add((LyricsEngine) constructor.newInstance(p));
                } catch (ClassNotFoundException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (InstantiationException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (IllegalAccessException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (SecurityException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (NoSuchMethodException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (IllegalArgumentException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (InvocationTargetException e) {
                    logger.error(LogCategories.HANDLER, e);
                }
            }
        }
        return result;
    }

    /**
     * Sets the lyric engines for the lyrics service
     * 
     * @param lyricsEnginesInfo
     *            the lyrics engines info
     */
    private void setLyricsEngines(ProxyBean p, List<LyricsEngineInfo> lyricsEnginesInfo) {
        List<LyricsEngine> result = new ArrayList<LyricsEngine>();

        // Get engines
        for (LyricsEngineInfo lyricsEngineInfo : lyricsEnginesInfo) {
            if (lyricsEngineInfo.isEnabled()) {
                try {
                    Class<?> clazz = Class.forName(lyricsEngineInfo.getClazz());
                    Constructor<?> constructor = clazz.getConstructor(Proxy.class);
                    result.add((LyricsEngine) constructor.newInstance(p));
                } catch (ClassNotFoundException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (InstantiationException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (IllegalAccessException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (SecurityException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (NoSuchMethodException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (IllegalArgumentException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (InvocationTargetException e) {
                    logger.error(LogCategories.HANDLER, e);
                }
            }
        }
        lyricsService.setLyricsEngines(result);
    }

    /**
     * Called when a search a YouTube finishess
     * 
     * @param result
     * @param id
     */
    @Override
    public void notifyYoutubeSearchRetrieved(List<YoutubeResultEntry> result, long id) {
        if (currentWorkerId != id) {
            return;
        }
        ControllerProxy.getInstance().getContextPanelController().notifyYoutubeSearchRetrieved(result);
    }

    /**
     * downloads the youtube video to the given file. Opens a ProgressDialog and
     * starts the download in a SwingWorker process.
     * 
     * @param entry
     * @param file
     */
    public void downloadYoutubeVideo(final YoutubeResultEntry entry, final File file) {
        if (entry == null || entry.getUrl() == null)
            return;

        final YoutubeVideoDownloader downloader = new YoutubeVideoDownloader(entry, file);
        downloader.execute();
    }

    /**
     * Returns text to search at YouTube
     * 
     * @param ao
     * @return
     */
    public String getYoutubeSearchForAudioObject(AudioObject ao) {
        return yts.getSearchForAudioObject(ao);
    }

    /**
     * returns a URL which allows to download the youtube video. The html page
     * is opened and the swfArgs javascript is parsed to construct the download
     * URL.
     * 
     * @param url
     * @return
     */
    public String getDirectUrlToBeAbleToPlaySongFromYouTube(String url) {
        return yts.getDirectUrlToBeAbleToPlaySong(url);
    }

    /**
     * triggers youtube search and returns result in table model structure by
     * default only the first 10 result entries will be returned. Specify
     * startIndex to get the next 10 results from given startIndex.
     * 
     * @param searchString
     * @param startIndex
     * @return
     */
    public List<YoutubeResultEntry> searchInYoutube(String searchString, int startIndex) {
        lastYoutubeSearchString = searchString;
        lastYoutubeStartIndex = startIndex;
        return yts.searchInYoutube(searchString, startIndex);
    }

    /**
     * Searches for more results of the last search
     * 
     * @return
     */
    public void searchMoreResultsInYoutube() {
        YoutubeMoreResultsRunnable ytmrr = new YoutubeMoreResultsRunnable(this, lastYoutubeSearchString, lastYoutubeStartIndex, currentWorkerId);
        executorService.submit(ytmrr);
    }

    /**
     * Returns loved tracks in Last FM
     */
    public List<LastFmLovedTrack> getLovedTracks() {
        return lastFmService.getLovedTracks();
    }

    /**
     * Get image for Artist
     * 
     * @param artist
     * @return
     */
    public Image getImageForArtist(String artist) {
        SimilarArtistsInfo artistInfo = lastFmService.getSimilarArtists(artist);
        if (artistInfo != null) {
            return lastFmService.getImage(artistInfo);
        }
        return null;
    }
    
    @Override
    public void applicationStateChanged(ApplicationState newState) {
        updateLastFmService(newState.getProxy(), newState.getLastFmUser(), newState.getLastFmPassword());
        updateYoutubeService(newState.getProxy());
        setLyricsEngines(newState.getProxy(), newState.getLyricsEnginesInfo());
    }
}
