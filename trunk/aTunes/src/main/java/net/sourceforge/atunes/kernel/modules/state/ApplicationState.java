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

package net.sourceforge.atunes.kernel.modules.state;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.LookAndFeelSelector;
import net.sourceforge.atunes.gui.StandardFrame;
import net.sourceforge.atunes.gui.views.controls.playList.ColumnBean;
import net.sourceforge.atunes.gui.views.dialogs.FontChooserDialog.FontSettings;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController.ViewMode;
import net.sourceforge.atunes.kernel.modules.hotkeys.HotkeysConfig;
import net.sourceforge.atunes.kernel.modules.internetsearch.Search;
import net.sourceforge.atunes.kernel.modules.internetsearch.SearchFactory;
import net.sourceforge.atunes.kernel.modules.navigator.RepositoryNavigationView;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.repository.HighlightFoldersByIncompleteTags;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler.SortType;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.TagAttribute;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyricsEngineInfo;

/**
 * <p>
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * </p>
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationState {

    /**
     * Singleton instance
     */
    private static ApplicationState instance;

    /** The show all radio stations. */
    private boolean showAllRadioStations = true;

    /** The show navigation panel. */
    private boolean showNavigationPanel = true;

    /** The show navigation table. */
    private boolean showNavigationTable = true;

    /** The show song properties. */
    private boolean showAudioObjectProperties;

    /** The show status bar. */
    private boolean showStatusBar = true;

    /** The show osd. */
    private boolean showOSD;

    /** The shuffle. */
    private boolean shuffle;

    /** The repeat. */
    private boolean repeat;

    /** The show system tray. */
    private boolean showSystemTray;

    /** The show tray player. */
    private boolean showTrayPlayer;

    /** The navigation view. */
    private String navigationView = RepositoryNavigationView.class.getName();

    /** The repository view */
    private ViewMode viewMode = ViewMode.ARTIST;

    /** The locale. */
    private LocaleBean locale;

    /** The default search. */
    private String defaultSearch;

    /** The use audio scrobbler. */
    private boolean useContext = true;

    /** The selected audio scrobbler tab. */
    private int selectedContextTab;

    /** The multiple window. */
    private boolean multipleWindow;

    /** The show playlist controls. */
    private boolean showPlaylistControls = true;

    /** The proxy. */
    private ProxyBean proxy;

    /** The theme. */
    private String skin = LookAndFeelSelector.DEFAULT_SKIN;

    private FontSettings fontSettings;

    /** The play at startup. */
    private boolean playAtStartup;

    /** The cache files before playing. */
    private boolean cacheFilesBeforePlaying = false;

    // MPlayer options
    /** The use normalisation. */
    private boolean useNormalisation;

    /** The karaoke. */
    private boolean karaoke;

    /** The use short path names. */
    private boolean useShortPathNames = true;

    /** The equalizer settings. */
    private float[] equalizerSettings;

    /** The use fade away. */
    private boolean useFadeAway;

    /** The show ticks. */
    private boolean showTicks;

    /** The read info from radio stream. */
    private boolean readInfoFromRadioStream = true;

    /** The enable advanced search. */
    private boolean enableAdvancedSearch;

    /** The enable hotkeys. */
    private boolean enableHotkeys;

    /** The show title. */
    private boolean showTitle = true;

    /** The osd duration. */
    private int osdDuration = 2; // In seconds

    /** The auto repository refresh time. */
    private int autoRepositoryRefreshTime = 60; // In minutes

    /** The save repository as xml. */
    private boolean saveRepositoryAsXml = true;

    /** The show favorites in navigator. */
    private boolean showFavoritesInNavigator = true;

    /** Use error correction for CD ripping. */
    private boolean useCdErrorCorrection = false;

    /** The use smart tag view sorting. */
    private boolean useSmartTagViewSorting;

    /** The use person names artist tag view sorting */
    private boolean usePersonNamesArtistTagViewSorting;

    /** The save picture from audio scrobbler. */
    private boolean saveContextPicture;

    /** The show extended tooltip. */
    private boolean showExtendedTooltip = true;

    /** The album tooltip delay. */
    private int extendedTooltipDelay = 1; // In seconds

    /**
     * The last fm enabled. It is not enabled until user sets login user and
     * password
     */
    private boolean lastFmEnabled = false;

    /** The last fm user. */
    private String lastFmUser;

    /** The last fm password. */
    private String lastFmPassword;

    /** Option to add loved songs in LastFM when adding a song as favorite */
    private boolean autoLoveFavoriteSong = false;

    /** The encrypted last fm password. */
    private byte[] encryptedLastFmPassword;

    /** The lyrics engines info. */
    private List<LyricsEngineInfo> lyricsEnginesInfo;

    /** The full screen background. */
    private String fullScreenBackground;

    /**
     * The window x position. Default -1: No window position
     */
    private int windowXPosition = -1;

    /**
     * The window y position. Default -1: No window position
     */
    private int windowYPosition = -1;

    /** The maximized. */
    private boolean maximized;

    /** The window width. */
    private int windowWidth;

    /** The window height. */
    private int windowHeight;

    /** The multiple view x position. */
    private int multipleViewXPosition;

    /** The multiple view y position. */
    private int multipleViewYPosition;

    /** The multiple view width. */
    private int multipleViewWidth;

    /** The multiple view height. */
    private int multipleViewHeight;

    /** The nag dialog counter. */
    private int nagDialogCounter;

    /** The volume. */
    private int volume = 50;

    /**
     * Muted property: <code>true</code> if sound is disabled
     */
    private boolean muteEnabled;

    /** The encoder. */
    private String encoder = "OGG";

    /** The encoder quality. */
    private String encoderQuality = "5";

    /** The mp3 encoder quality. */
    private String mp3EncoderQuality = "medium";

    /** The flac encoder quality. */
    private String flacEncoderQuality = "-5";

    /** The file name pattern. */
    private String cdRipperFileNamePattern;

    // Columns config
    /** The columns. */
    private Map<String, ColumnBean> columns;

    // Split panes divider location
    /** The left vertical split pane divider location. */
    private int leftVerticalSplitPaneDividerLocation = StandardFrame.NAVIGATION_PANEL_WIDTH;

    /** The right vertical split pane divider location. */
    private int rightVerticalSplitPaneDividerLocation;

    /** The left horizontal split pane divider location. */
    private int leftHorizontalSplitPaneDividerLocation = StandardFrame.NAVIGATOR_SPLIT_PANE_DIVIDER_LOCATION;

    /**
     * Default location where device is plugged. Used to connect device
     * automatically
     */
    private String defaultDeviceLocation;

    /** The podcast feed entries retrieval interval. */
    private long podcastFeedEntriesRetrievalInterval = PodcastFeedHandler.DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL;

    /** The podcast feed entry download path. */
    private String podcastFeedEntryDownloadPath;

    /** The use downloaded podcast feed entries. */
    private boolean useDownloadedPodcastFeedEntries = true;

    /** The remove podcast feed entries removed from podcast feed. */
    private boolean removePodcastFeedEntriesRemovedFromPodcastFeed;

    /** Property to show navigator tabs at left (true) or not (false) */
    private boolean showNavigatorTabsAtLeft = true;

    /** Property to show navigator tabs text or not */
    private boolean showNavigatorTabsText = true;

    /** Property to show context tabs text or not */
    private boolean showContextTabsText = true;

    /**
     * Property to hide albums of artist "Various Artists" in ContextPanel
     */
    private boolean hideVariousArtistsAlbums = true;

    /**
     * Property of minimum song number per album in ContextPanel. 0 no minimum
     * value
     */
    private int minimumSongNumberPerAlbum = 0;

    /**
     * Property to highlight folder with incomplete tags in navigator (true) or
     * not (false)
     */
    private boolean highlightIncompleteTagFolders = true;

    /**
     * list with tag attributes used to hightlight folders
     */
    private List<TagAttribute> highlightIncompleteTagFoldersAttributes = HighlightFoldersByIncompleteTags.getDefaultTagAttributesToHighlightFolders();

    /** Property to show tool bar or not */
    private boolean showToolBar = true;

    /**
     * Current PlayerHandler implementation. DEFAULT PLAYER IS MPLAYER
     */
    private String playerEngine = PlayerHandler.DEFAULT_ENGINE;

    /**
     * Option to scroll automatically play list to current audio object
     */
    private boolean autoScrollPlayListEnabled = true;

    /**
     * Type of sort used for navigator and when adding to playlist
     */
    private SortType sortType = SortType.BY_ARTIST_AND_ALBUM;

    /**
     * List of folders of last loaded repository
     */
    private List<String> lastRepositoryFolders;

    /**
     * The last path used for loading a playlist file
     */
    private String loadPlaylistPath;
    /**
     * The last path used for storing a playlist file
     */
    private String savePlaylistPath;

    /**
     * Property to stop or not the player when switching between playlists
     */
    private boolean stopPlayerOnPlayListSwitch = false;

    /**
     * Property to stop or not the player when deleting a playlist
     */
    private boolean stopPlayerOnPlayListClear = true;

    /**
     * Pattern used to rename files when importing / exporting
     */
    private String importExportFileNamePattern;

    /**
     * Pattern used to rename files when copying to device
     */
    private String deviceFileNamePattern;

    /**
     * Pattern used to folder's path where store files when importing /
     * exporting
     */
    private String importExportFolderPathPattern;

    /**
     * Pattern used to folder's path where store files when copying to device
     * exporting
     */
    private String deviceFolderPathPattern;

    /**
     * Property that indicates if repeated songs are allowed in device for
     * different albums
     */
    private boolean allowRepeatedSongsInDevice;

    /**
     * Property to review tags by user before import
     */
    private boolean reviewTagsBeforeImport = true;

    /**
     * Property to apply changes in tags to source files before import
     */
    private boolean applyChangesToSourceFilesBeforeImport = false;

    /**
     * Property to set track numbers when importing
     */
    private boolean setTrackNumbersWhenImporting = true;

    /**
     * Property to set titles when importing
     */
    private boolean setTitlesWhenImporting = true;

    /**
     * Property to set width of OSD (default 300 pixels)
     */
    private int osdWidth = 300;

    /**
     * Property to set horizontal alignment of OSD
     */
    private int osdHorizontalAlignment = SwingConstants.RIGHT;

    /**
     * Property to set vertical alignment of OSD
     */
    private int osdVerticalAlignment = SwingConstants.BOTTOM;

    private HotkeysConfig hotkeysConfig;

    /**
     * List of patterns used for recognition
     */
    private List<String> recognitionPatterns;

    /**
     * List of patterns used for massive recognition
     */
    private List<String> massiveRecognitionPatterns;

    /**
     * Command to execute before access to repository
     */
    private String commandBeforeAccessRepository;

    /**
     * Command to execute after access to repository
     */
    private String commandAfterAccessRepository;

    private boolean useLibnotify = false;

    //////////////////////////////////////////////////////////// END OF ATTRIBUTES /////////////////////////////////////////////////////////////

    /**
     * Instantiates a new application state.
     */
    public ApplicationState() {
        // Nothing to do
    }

    /**
     * @return the instance
     */
    public static ApplicationState getInstance() {
        if (instance == null) {
            // Read the state
            instance = ApplicationStateHandler.getInstance().readState();
        }
        return instance;
    }

    /**
     * Gets the album tooltip delay.
     * 
     * @return the album tooltip delay
     */
    public int getExtendedTooltipDelay() {
        return extendedTooltipDelay;
    }

    /**
     * Gets the auto repository refresh time.
     * 
     * @return the auto repository refresh time
     */
    public int getAutoRepositoryRefreshTime() {
        return autoRepositoryRefreshTime;
    }

    /**
     * Gets the columns.
     * 
     * @return the columns
     */
    public Map<String, ColumnBean> getColumns() {
        return columns;
    }

    /**
     * Gets the default device location.
     * 
     * @return the defaultDeviceLocation
     */
    public String getDefaultDeviceLocation() {
        return defaultDeviceLocation;
    }

    /**
     * Gets the default search.
     * 
     * @return the default search
     */
    public String getDefaultSearch() {
        return defaultSearch;
    }

    /**
     * Gets the default search object.
     * 
     * @return the default search object
     */
    public Search getDefaultSearchObject() {
        return SearchFactory.getSearchForName(defaultSearch);
    }

    /**
     * Gets the encoder.
     * 
     * @return the encoder
     */
    public String getEncoder() {
        return encoder;
    }

    /**
     * Last used setting of the ogg encoder.
     * 
     * @return Returns the last used encoder quality setting for the ogg encoder
     */
    public String getEncoderQuality() {
        return encoderQuality;
    }

    /**
     * Gets the encrypted last fm password.
     * 
     * @return the encryptedLastFmPassword
     */
    public byte[] getEncryptedLastFmPassword() {
        if (encryptedLastFmPassword == null) {
            return null;
        }
        return Arrays.copyOf(encryptedLastFmPassword, encryptedLastFmPassword.length);
    }

    /**
     * Gets the equalizer settings.
     * 
     * @return Equalizer settings for mplayer
     */
    public float[] getEqualizerSettings() {
        if (equalizerSettings == null) {
            return null;
        }
        return Arrays.copyOf(equalizerSettings, equalizerSettings.length);
    }

    /**
     * Last used filename pattern.
     * 
     * @return Returns the last used filename pattern setting.
     */
    public String getCdRipperFileNamePattern() {
        return cdRipperFileNamePattern;
    }

    /**
     * Last used setting of the flac encoder.
     * 
     * @return Returns the last used encoder quality setting for the flac
     *         encoder.
     */
    public String getFlacEncoderQuality() {
        return flacEncoderQuality;
    }

    /**
     * Gets the full screen background.
     * 
     * @return the fullScreenBackground
     */
    public String getFullScreenBackground() {
        return fullScreenBackground;
    }

    /**
     * Gets the last fm password.
     * 
     * @return the lastFmPassword
     */
    public String getLastFmPassword() {
        return lastFmPassword;
    }

    /**
     * Gets the last fm user.
     * 
     * @return the lastFmUser
     */
    public String getLastFmUser() {
        return lastFmUser;
    }

    /**
     * Gets the left horizontal split pane divider location.
     * 
     * @return the leftHorizontalSplitPaneDividerLocation
     */
    public int getLeftHorizontalSplitPaneDividerLocation() {
        return leftHorizontalSplitPaneDividerLocation;
    }

    /**
     * Gets the left vertical split pane divider location.
     * 
     * @return the leftVerticalSplitPaneDividerLocation
     */
    public int getLeftVerticalSplitPaneDividerLocation() {
        return leftVerticalSplitPaneDividerLocation;
    }

    /**
     * Gets the locale.
     * 
     * @return the locale
     */
    public LocaleBean getLocale() {
        return locale;
    }

    /**
     * Last used setting of the mp3 encoder.
     * 
     * @return Returns the last used encoder quality setting for the mp3 encoder
     */
    public String getMp3EncoderQuality() {
        return mp3EncoderQuality;
    }

    /**
     * Gets the multiple view height.
     * 
     * @return the multipleViewHeight
     */
    public int getMultipleViewHeight() {
        return multipleViewHeight;
    }

    /**
     * Gets the multiple view width.
     * 
     * @return the multipleViewWidth
     */
    public int getMultipleViewWidth() {
        return multipleViewWidth;
    }

    /**
     * Gets the multiple view x position.
     * 
     * @return the multipleViewXPosition
     */
    public int getMultipleViewXPosition() {
        return multipleViewXPosition;
    }

    /**
     * Gets the multiple view y position.
     * 
     * @return the multipleViewYPosition
     */
    public int getMultipleViewYPosition() {
        return multipleViewYPosition;
    }

    /**
     * Gets the nag dialog counter.
     * 
     * @return the nag dialog counter
     */
    public int getNagDialogCounter() {
        return nagDialogCounter;
    }

    /**
     * Gets the navigation view.
     * 
     * @return the navigation view
     */
    public String getNavigationView() {
        return navigationView;
    }

    /**
     * Gets the osd duration.
     * 
     * @return the osd duration
     */
    public int getOsdDuration() {
        return osdDuration;
    }

    /**
     * Gets the podcast feed entries retrieval interval.
     * 
     * @return the podcastFeedEntriesRetrievalInterval
     */
    public long getPodcastFeedEntriesRetrievalInterval() {
        return podcastFeedEntriesRetrievalInterval;
    }

    /**
     * Gets the proxy.
     * 
     * @return the proxy
     */
    public ProxyBean getProxy() {
        return proxy;
    }

    /**
     * Gets the right vertical split pane divider location.
     * 
     * @return the rightVerticalSplitPaneDividerLocation
     */
    public int getRightVerticalSplitPaneDividerLocation() {
        return rightVerticalSplitPaneDividerLocation;
    }

    /**
     * Gets the selected audio scrobbler tab.
     * 
     * @return the selected audio scrobbler tab
     */
    public int getSelectedContextTab() {
        return selectedContextTab;
    }

    /**
     * Gets the skin.
     * 
     * @return the skin
     */
    public String getSkin() {
        return skin;
    }

    /**
     * Gets the volume.
     * 
     * @return the volume
     */
    public int getVolume() {
        return volume;
    }

    /**
     * Gets the window height.
     * 
     * @return the windowHeight
     */
    public int getWindowHeight() {
        return windowHeight;
    }

    /**
     * Gets the window width.
     * 
     * @return the windowWidth
     */
    public int getWindowWidth() {
        return windowWidth;
    }

    /**
     * Gets the window x position.
     * 
     * @return the windowXPosition
     */
    public int getWindowXPosition() {
        return windowXPosition;
    }

    /**
     * Gets the window y position.
     * 
     * @return the windowYPosition
     */
    public int getWindowYPosition() {
        return windowYPosition;
    }

    /**
     * Checks if is enable hotkeys.
     * 
     * @return true, if is enable hotkeys
     */
    public boolean isEnableHotkeys() {
        return enableHotkeys;
    }

    /**
     * Checks if is karaoke.
     * 
     * @return the karaoke
     */
    public boolean isKaraoke() {
        return karaoke;
    }

    /**
     * Checks if is last fm enabled.
     * 
     * @return the lastFmEnabled
     */
    public boolean isLastFmEnabled() {
        return lastFmEnabled;
    }

    /**
     * Checks if is maximized.
     * 
     * @return true, if is maximized
     */
    public boolean isMaximized() {
        return maximized;
    }

    /**
     * Checks if is multiple window.
     * 
     * @return true, if is multiple window
     */
    public boolean isMultipleWindow() {
        return multipleWindow;
    }

    /**
     * Checks if is play at startup.
     * 
     * @return the playAtStartup
     */
    public boolean isPlayAtStartup() {
        return playAtStartup;
    }

    /**
     * Checks if is read info from radio stream.
     * 
     * @return true, if is read info from radio stream
     */
    public boolean isReadInfoFromRadioStream() {
        return readInfoFromRadioStream;
    }

    /**
     * Checks if is repeat.
     * 
     * @return true, if is repeat
     */
    public boolean isRepeat() {
        return repeat;
    }

    /**
     * Checks if is save picture from audio scrobbler.
     * 
     * @return true, if is save picture from audio scrobbler
     */
    public boolean isSaveContextPicture() {
        return saveContextPicture;
    }

    /**
     * Checks if is save repository as xml.
     * 
     * @return true, if is save repository as xml
     */
    public boolean isSaveRepositoryAsXml() {
        return saveRepositoryAsXml;
    }

    /**
     * Checks if is show album tooltip.
     * 
     * @return true, if is show album tooltip
     */
    public boolean isShowExtendedTooltip() {
        return showExtendedTooltip;
    }

    /**
     * Checks if is show all radio stations.
     * 
     * @return true, if is show all radio stations
     */
    public boolean isShowAllRadioStations() {
        return showAllRadioStations;
    }

    /**
     * Checks if is show favorites in navigator.
     * 
     * @return true, if is show favorites in navigator
     */
    public boolean isShowFavoritesInNavigator() {
        return showFavoritesInNavigator;
    }

    /**
     * Checks if is show navigation panel.
     * 
     * @return true, if is show navigation panel
     */
    public boolean isShowNavigationPanel() {
        return showNavigationPanel;
    }

    /**
     * Checks if is show navigation table.
     * 
     * @return true, if is show navigation table
     */
    public boolean isShowNavigationTable() {
        return showNavigationTable;
    }

    /**
     * Checks if is show osd.
     * 
     * @return true, if is show osd
     */
    public boolean isShowOSD() {
        return showOSD;
    }

    /**
     * Checks if is show playlist controls.
     * 
     * @return true, if is show playlist controls
     */
    public boolean isShowPlaylistControls() {
        return showPlaylistControls;
    }

    /**
     * Checks if is show song properties.
     * 
     * @return true, if is show song properties
     */
    public boolean isShowAudioObjectProperties() {
        return showAudioObjectProperties;
    }

    /**
     * Checks if is show status bar.
     * 
     * @return true, if is show status bar
     */
    public boolean isShowStatusBar() {
        return showStatusBar;
    }

    /**
     * Checks if is show system tray.
     * 
     * @return true, if is show system tray
     */
    public boolean isShowSystemTray() {
        return showSystemTray;
    }

    /**
     * Checks if is show title.
     * 
     * @return true, if is show title
     */
    public boolean isShowTitle() {
        return showTitle;
    }

    /**
     * Checks if is show tray player.
     * 
     * @return true, if is show tray player
     */
    public boolean isShowTrayPlayer() {
        return showTrayPlayer;
    }

    /**
     * Checks if is shuffle.
     * 
     * @return true, if is shuffle
     */
    public boolean isShuffle() {
        return shuffle;
    }

    /**
     * Checks if is use audio scrobbler.
     * 
     * @return true, if is use audio scrobbler
     */
    public boolean isUseContext() {
        return useContext;
    }

    /**
     * Checks if normalisation is used.
     * 
     * @return True if normalization is used
     */
    public boolean isUseNormalisation() {
        return useNormalisation;
    }

    /**
     * Checks if the cd error correction is enabled
     * 
     * @return True if the error correction is enabled
     */
    public boolean isUseCdErrorCorrection() {
        return useCdErrorCorrection;
    }

    /**
     * Checks if is use short path names.
     * 
     * @return True if short path names are used
     */
    public boolean isUseShortPathNames() {
        return useShortPathNames;
    }

    /**
     * Checks if is use smart tag view sorting.
     * 
     * @return the useSmartTagViewSorting
     */
    public boolean isUseSmartTagViewSorting() {
        return useSmartTagViewSorting;
    }

    /**
     * Sets the album tooltip delay.
     * 
     * @param extendedTooltipDelay
     *            the new album tooltip delay
     */
    public void setExtendedTooltipDelay(int albumTooltipDelay) {
        this.extendedTooltipDelay = albumTooltipDelay;
    }

    /**
     * Sets the auto repository refresh time.
     * 
     * @param autoRepositoryRefreshTime
     *            the new auto repository refresh time
     */
    public void setAutoRepositoryRefreshTime(int autoRepositoryRefreshTime) {
        this.autoRepositoryRefreshTime = autoRepositoryRefreshTime;
    }

    /**
     * Sets the columns.
     * 
     * @param columns
     *            the columns to set
     */
    public void setColumns(Map<String, ColumnBean> columns) {
        this.columns = columns;
    }

    /**
     * Sets the default device location.
     * 
     * @param defaultDeviceLocation
     *            the defaultDeviceLocation to set
     */
    public void setDefaultDeviceLocation(String defaultDeviceLocation) {
        this.defaultDeviceLocation = defaultDeviceLocation;
    }

    /**
     * Sets the default search.
     * 
     * @param defaultSearch
     *            the new default search
     */
    public void setDefaultSearch(String defaultSearch) {
        this.defaultSearch = defaultSearch;
    }

    /**
     * Sets the default search object.
     * 
     * @param defaultSearch
     *            the new default search object
     */
    public void setDefaultSearchObject(Search defaultSearch) {
        this.defaultSearch = defaultSearch.toString();
    }

    /**
     * Sets the enable hotkeys.
     * 
     * @param enableHotkeys
     *            the new enable hotkeys
     */
    public void setEnableHotkeys(boolean enableHotkeys) {
        this.enableHotkeys = enableHotkeys;
    }

    /**
     * Sets the encoder to use for CD ripping.
     * 
     * @param encoder
     *            As of aTunes 1.7.3 one of the following is permissible: "OGG",
     *            "FLAC" or "MP3".
     */
    public void setEncoder(String encoder) {
        this.encoder = encoder;
    }

    /**
     * Sets the ogg encoder quality to use for CD ripping.
     * 
     * @param encoderQuality
     *            One of the following integer (no decimals) are permissible: -1
     *            to 10
     */
    public void setEncoderQuality(String encoderQuality) {
        this.encoderQuality = encoderQuality;
    }

    /**
     * Sets the encrypted last fm password.
     * 
     * @param encryptedLastFmPassword
     *            the encryptedLastFmPassword to set
     */
    public void setEncryptedLastFmPassword(byte[] encryptedLastFmPassword) {
        if (encryptedLastFmPassword == null) {
            this.encryptedLastFmPassword = null;
        } else {
            this.encryptedLastFmPassword = Arrays.copyOf(encryptedLastFmPassword, encryptedLastFmPassword.length);
        }
    }

    /**
     * Sets the equalizer settings.
     * 
     * @param equalizerSettings
     *            the equalizer settings
     */
    public void setEqualizerSettings(float[] equalizerSettings) {
        if (equalizerSettings == null) {
            this.equalizerSettings = null;
        } else {
            this.equalizerSettings = Arrays.copyOf(equalizerSettings, equalizerSettings.length);
        }
    }

    /**
     * Sets the filename pattern.
     * 
     * @param fileNamePattern
     *            For valid filename patterns please see RipCdDialog.
     */
    public void setCdRipperFileNamePattern(String fileNamePattern) {
        this.cdRipperFileNamePattern = fileNamePattern;
    }

    /**
     * Sets the flac encoder quality to use for CD ripping.
     * 
     * @param flacEncoderQuality
     *            One of the following negative integer (no decimals) are
     *            permissible: -8 to -0
     */
    public void setFlacEncoderQuality(String flacEncoderQuality) {
        this.flacEncoderQuality = flacEncoderQuality;
    }

    /**
     * Sets the full screen background.
     * 
     * @param fullScreenBackground
     *            the fullScreenBackground to set
     */
    public void setFullScreenBackground(String fullScreenBackground) {
        this.fullScreenBackground = fullScreenBackground;
    }

    /**
     * Sets the karaoke.
     * 
     * @param karaoke
     *            the karaoke to set
     */
    public void setKaraoke(boolean karaoke) {
        this.karaoke = karaoke;
    }

    /**
     * Sets the last fm enabled.
     * 
     * @param lastFmEnabled
     *            the lastFmEnabled to set
     */
    public void setLastFmEnabled(boolean lastFmEnabled) {
        this.lastFmEnabled = lastFmEnabled;
    }

    /**
     * Sets the last fm password.
     * 
     * @param lastFmPassword
     *            the lastFmPassword to set
     */
    public void setLastFmPassword(String lastFmPassword) {
        this.lastFmPassword = lastFmPassword;
    }

    /**
     * Sets the last fm user.
     * 
     * @param lastFmUser
     *            the lastFmUser to set
     */
    public void setLastFmUser(String lastFmUser) {
        this.lastFmUser = lastFmUser;
    }

    /**
     * Sets the left horizontal split pane divider location.
     * 
     * @param leftHorizontalSplitPaneDividerLocation
     *            the leftHorizontalSplitPaneDividerLocation to set
     */
    public void setLeftHorizontalSplitPaneDividerLocation(int leftHorizontalSplitPaneDividerLocation) {
        this.leftHorizontalSplitPaneDividerLocation = leftHorizontalSplitPaneDividerLocation;
    }

    /**
     * Sets the left vertical split pane divider location.
     * 
     * @param leftVerticalSplitPaneDividerLocation
     *            the leftVerticalSplitPaneDividerLocation to set
     */
    public void setLeftVerticalSplitPaneDividerLocation(int leftVerticalSplitPaneDividerLocation) {
        this.leftVerticalSplitPaneDividerLocation = leftVerticalSplitPaneDividerLocation;
    }

    /**
     * Sets the locale.
     * 
     * @param locale
     *            the new locale
     */
    public void setLocale(LocaleBean locale) {
        this.locale = locale;
    }

    /**
     * Sets the maximized.
     * 
     * @param maximized
     *            the new maximized
     */
    public void setMaximized(boolean maximized) {
        this.maximized = maximized;
    }

    /**
     * Sets the mp3 encoder quality to use for CD ripping.
     * 
     * @param mp3EncoderQuality
     *            One of the following are permissible: insane, extreme, medium,
     *            standard, 128, 160, 192, 224, 256, 320
     */
    public void setMp3EncoderQuality(String mp3EncoderQuality) {
        this.mp3EncoderQuality = mp3EncoderQuality;
    }

    /**
     * Sets the multiple view height.
     * 
     * @param multipleViewHeight
     *            the multipleViewHeight to set
     */
    public void setMultipleViewHeight(int multipleViewHeight) {
        this.multipleViewHeight = multipleViewHeight;
    }

    /**
     * Sets the multiple view width.
     * 
     * @param multipleViewWidth
     *            the multipleViewWidth to set
     */
    public void setMultipleViewWidth(int multipleViewWidth) {
        this.multipleViewWidth = multipleViewWidth;
    }

    /**
     * Sets the multiple view x position.
     * 
     * @param multipleViewXPosition
     *            the multipleViewXPosition to set
     */
    public void setMultipleViewXPosition(int multipleViewXPosition) {
        this.multipleViewXPosition = multipleViewXPosition;
    }

    /**
     * Sets the multiple view y position.
     * 
     * @param multipleViewYPosition
     *            the multipleViewYPosition to set
     */
    public void setMultipleViewYPosition(int multipleViewYPosition) {
        this.multipleViewYPosition = multipleViewYPosition;
    }

    /**
     * Sets the multiple window.
     * 
     * @param multipleWindow
     *            the new multiple window
     */
    public void setMultipleWindow(boolean multipleWindow) {
        this.multipleWindow = multipleWindow;
    }

    /**
     * Sets the nag dialog counter.
     * 
     * @param nagDialogCounter
     *            the new nag dialog counter
     */
    public void setNagDialogCounter(int nagDialogCounter) {
        this.nagDialogCounter = nagDialogCounter;
    }

    /**
     * Sets the navigation view.
     * 
     * @param navigationView
     *            the new navigation view
     */
    public void setNavigationView(String navigationView) {
        this.navigationView = navigationView;
    }

    /**
     * Sets the osd duration.
     * 
     * @param osdDuration
     *            the new osd duration
     */
    public void setOsdDuration(int osdDuration) {
        this.osdDuration = osdDuration;
    }

    /**
     * Sets the play at startup.
     * 
     * @param playAtStartup
     *            the playAtStartup to set
     */
    public void setPlayAtStartup(boolean playAtStartup) {
        this.playAtStartup = playAtStartup;
    }

    /**
     * Sets the podcast feed entries retrieval interval.
     * 
     * @param podcastFeedEntriesRetrievalInterval
     *            the podcastFeedEntriesRetrievalInterval to set
     */
    public void setPodcastFeedEntriesRetrievalInterval(long podcastFeedEntriesRetrievalInterval) {
        this.podcastFeedEntriesRetrievalInterval = podcastFeedEntriesRetrievalInterval;
    }

    /**
     * Sets the proxy.
     * 
     * @param proxy
     *            the new proxy
     */
    public void setProxy(ProxyBean proxy) {
        this.proxy = proxy;
    }

    /**
     * Sets the read info from radio stream.
     * 
     * @param readInfoFromRadioStream
     *            the new read info from radio stream
     */
    public void setReadInfoFromRadioStream(boolean readInfoFromRadioStream) {
        this.readInfoFromRadioStream = readInfoFromRadioStream;
    }

    /**
     * Sets the repeat.
     * 
     * @param repeat
     *            the new repeat
     */
    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    /**
     * Sets the right vertical split pane divider location.
     * 
     * @param rightVerticalSplitPaneDividerLocation
     *            the rightVerticalSplitPaneDividerLocation to set
     */
    public void setRightVerticalSplitPaneDividerLocation(int rightVerticalSplitPaneDividerLocation) {
        this.rightVerticalSplitPaneDividerLocation = rightVerticalSplitPaneDividerLocation;
    }

    /**
     * Sets the save picture from audio scrobbler.
     * 
     * @param saveContexInformationPicture
     *            the new save picture from audio scrobbler
     */
    public void setSaveContextPicture(boolean saveContexInformationPicture) {
        this.saveContextPicture = saveContexInformationPicture;
    }

    /**
     * Sets the save repository as xml.
     * 
     * @param saveRepositoryAsXml
     *            the new save repository as xml
     */
    public void setSaveRepositoryAsXml(boolean saveRepositoryAsXml) {
        this.saveRepositoryAsXml = saveRepositoryAsXml;
    }

    /**
     * Sets the selected audio scrobbler tab.
     * 
     * @param selectedContextTab
     *            the new selected context information tab
     */
    public void setSelectedContextTab(int selectedContextTab) {
        this.selectedContextTab = selectedContextTab;
    }

    /**
     * Sets the show album tooltip.
     * 
     * @param showExtendedTooltip
     *            the new show album tooltip
     */
    public void setShowExtendedTooltip(boolean showAlbumTooltip) {
        this.showExtendedTooltip = showAlbumTooltip;
    }

    /**
     * Sets if preset radio list should be displayed or not.
     * 
     * @param showAllRadioStations
     *            Set false for only showing user stations
     */
    public void setShowAllRadioStations(boolean showAllRadioStations) {
        this.showAllRadioStations = showAllRadioStations;
    }

    /**
     * Sets the show favorites in navigator.
     * 
     * @param showFavoritesInNavigator
     *            the new show favorites in navigator
     */
    public void setShowFavoritesInNavigator(boolean showFavoritesInNavigator) {
        this.showFavoritesInNavigator = showFavoritesInNavigator;
    }

    /**
     * Sets the show navigation panel.
     * 
     * @param showNavigationPanel
     *            the new show navigation panel
     */
    public void setShowNavigationPanel(boolean showNavigationPanel) {
        this.showNavigationPanel = showNavigationPanel;
    }

    /**
     * Sets the show navigation table.
     * 
     * @param showNavigationTable
     *            the new show navigation table
     */
    public void setShowNavigationTable(boolean showNavigationTable) {
        this.showNavigationTable = showNavigationTable;
    }

    /**
     * Sets the show osd.
     * 
     * @param showOSD
     *            the new show osd
     */
    public void setShowOSD(boolean showOSD) {
        this.showOSD = showOSD;
    }

    /**
     * Sets the show playlist controls.
     * 
     * @param showPlaylistControls
     *            the new show playlist controls
     */
    public void setShowPlaylistControls(boolean showPlaylistControls) {
        this.showPlaylistControls = showPlaylistControls;
    }

    /**
     * Sets the show song properties.
     * 
     * @param showSongProperties
     *            the new show song properties
     */
    public void setShowAudioObjectProperties(boolean showSongProperties) {
        this.showAudioObjectProperties = showSongProperties;
    }

    /**
     * Sets the show status bar.
     * 
     * @param showStatusBar
     *            the new show status bar
     */
    public void setShowStatusBar(boolean showStatusBar) {
        this.showStatusBar = showStatusBar;
    }

    /**
     * Sets the show system tray.
     * 
     * @param showSystemTray
     *            the new show system tray
     */
    public void setShowSystemTray(boolean showSystemTray) {
        this.showSystemTray = showSystemTray;
    }

    /**
     * Sets the show title.
     * 
     * @param showTitle
     *            the new show title
     */
    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    /**
     * Sets the show tray player.
     * 
     * @param showTrayPlayer
     *            the new show tray player
     */
    public void setShowTrayPlayer(boolean showTrayPlayer) {
        this.showTrayPlayer = showTrayPlayer;
    }

    /**
     * Sets the shuffle.
     * 
     * @param shuffle
     *            the new shuffle
     */
    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    /**
     * Sets the skin.
     * 
     * @param skin
     *            the new skin
     */
    public void setSkin(String skin) {
        this.skin = skin;
    }

    /**
     * Sets the use audio scrobbler.
     * 
     * @param useContext
     *            the new use audio scrobbler
     */
    public void setUseContext(boolean useContext) {
        this.useContext = useContext;
    }

    /**
     * Sets if the error correction for CD ripping is used
     * 
     * @param useCdErrorCorrection
     *            True if error correction is used
     */
    public void setUseCdErrorCorrection(boolean useCdErrorCorrection) {
        this.useCdErrorCorrection = useCdErrorCorrection;
    }

    /**
     * Sets the use normalisation.
     * 
     * @param useNormalisation
     *            the useNormalisation to set
     */
    public void setUseNormalisation(boolean useNormalisation) {
        this.useNormalisation = useNormalisation;
    }

    /**
     * Sets the use short path names.
     * 
     * @param useShortPathNames
     *            the new use short path names
     */
    public void setUseShortPathNames(boolean useShortPathNames) {
        this.useShortPathNames = useShortPathNames;
    }

    /**
     * Sets the use smart tag view sorting.
     * 
     * @param useSmartTagViewSorting
     *            the useSmartTagViewSorting to set
     */
    public void setUseSmartTagViewSorting(boolean useSmartTagViewSorting) {
        this.useSmartTagViewSorting = useSmartTagViewSorting;
    }

    /**
     * Sets the volume.
     * 
     * @param volume
     *            the new volume
     */
    public void setVolume(int volume) {
        if (volume < 0) {
            this.volume = 0;
        } else if (volume > 100) {
            this.volume = 100;
        } else {
            this.volume = volume;
        }
    }

    /**
     * Sets the window height.
     * 
     * @param windowHeight
     *            the windowHeight to set
     */
    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    /**
     * Sets the window width.
     * 
     * @param windowWidth
     *            the windowWidth to set
     */
    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    /**
     * Sets the window x position.
     * 
     * @param windowXPosition
     *            the windowXPosition to set
     */
    public void setWindowXPosition(int windowXPosition) {
        this.windowXPosition = windowXPosition;
    }

    /**
     * Sets the window y position.
     * 
     * @param windowYPosition
     *            the windowYPosition to set
     */
    public void setWindowYPosition(int windowYPosition) {
        this.windowYPosition = windowYPosition;
    }

    /**
     * Checks if is use fade away.
     * 
     * @return the useFadeAway
     */
    public boolean isUseFadeAway() {
        return useFadeAway;
    }

    /**
     * Sets the use fade away.
     * 
     * @param useFadeAway
     *            the useFadeAway to set
     */
    public void setUseFadeAway(boolean useFadeAway) {
        this.useFadeAway = useFadeAway;
    }

    /**
     * Checks if is show ticks.
     * 
     * @return the showTicks
     */
    public boolean isShowTicks() {
        return showTicks;
    }

    /**
     * Sets the show ticks.
     * 
     * @param showTicks
     *            the showTicks to set
     */
    public void setShowTicks(boolean showTicks) {
        this.showTicks = showTicks;
    }

    /**
     * Checks if is enable advanced search.
     * 
     * @return true, if is enable advanced search
     */
    public boolean isEnableAdvancedSearch() {
        return enableAdvancedSearch;
    }

    /**
     * Sets the enable advanced search.
     * 
     * @param enableAdvancedSearch
     *            the new enable advanced search
     */
    public void setEnableAdvancedSearch(boolean enableAdvancedSearch) {
        this.enableAdvancedSearch = enableAdvancedSearch;
    }

    /**
     * Gets the podcast feed entry download path.
     * 
     * @return the podcast feed entry download path
     */
    public String getPodcastFeedEntryDownloadPath() {
        return podcastFeedEntryDownloadPath;
    }

    /**
     * Sets the podcast feed entry download path.
     * 
     * @param podcastFeedEntryDownloadPath
     *            the new podcast feed entry download path
     */
    public void setPodcastFeedEntryDownloadPath(String podcastFeedEntryDownloadPath) {
        this.podcastFeedEntryDownloadPath = podcastFeedEntryDownloadPath;
    }

    /**
     * Checks if is use downloaded podcast feed entries.
     * 
     * @return true, if is use downloaded podcast feed entries
     */
    public boolean isUseDownloadedPodcastFeedEntries() {
        return useDownloadedPodcastFeedEntries;
    }

    /**
     * Sets the use downloaded podcast feed entries.
     * 
     * @param useDownloadedPodcastFeedEntries
     *            the new use downloaded podcast feed entries
     */
    public void setUseDownloadedPodcastFeedEntries(boolean useDownloadedPodcastFeedEntries) {
        this.useDownloadedPodcastFeedEntries = useDownloadedPodcastFeedEntries;
    }

    /**
     * Checks if is removes the podcast feed entries removed from podcast feed.
     * 
     * @return true, if is removes the podcast feed entries removed from podcast
     *         feed
     */
    public boolean isRemovePodcastFeedEntriesRemovedFromPodcastFeed() {
        return removePodcastFeedEntriesRemovedFromPodcastFeed;
    }

    /**
     * Sets the removes the podcast feed entries removed from podcast feed.
     * 
     * @param removePodcastFeedEntriesRemovedFromPodcastFeed
     *            the new removes the podcast feed entries removed from podcast
     *            feed
     */
    public void setRemovePodcastFeedEntriesRemovedFromPodcastFeed(boolean removePodcastFeedEntriesRemovedFromPodcastFeed) {
        this.removePodcastFeedEntriesRemovedFromPodcastFeed = removePodcastFeedEntriesRemovedFromPodcastFeed;
    }

    /**
     * Checks if is cache files before playing.
     * 
     * @return the cacheFilesBeforePlaying
     */
    public boolean isCacheFilesBeforePlaying() {
        return cacheFilesBeforePlaying;
    }

    /**
     * Sets the cache files before playing.
     * 
     * @param cacheFilesBeforePlaying
     *            the cacheFilesBeforePlaying to set
     */
    public void setCacheFilesBeforePlaying(boolean cacheFilesBeforePlaying) {
        this.cacheFilesBeforePlaying = cacheFilesBeforePlaying;
    }

    /**
     * @return the repositoryView
     */
    public ViewMode getViewMode() {
        return viewMode;
    }

    /**
     * @param viewMode
     *            the repositoryView to set
     */
    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    /**
     * @return the showNavigatorTabsAtLeft
     */
    public boolean isShowNavigatorTabsAtLeft() {
        return showNavigatorTabsAtLeft;
    }

    /**
     * @param showNavigatorTabsAtLeft
     *            the showNavigatorTabsAtLeft to set
     */
    public void setShowNavigatorTabsAtLeft(boolean showNavigatorTabsAtLeft) {
        this.showNavigatorTabsAtLeft = showNavigatorTabsAtLeft;
    }

    /**
     * @return the showNavigatorTabsText
     */
    public boolean isShowNavigatorTabsText() {
        return showNavigatorTabsText;
    }

    /**
     * @param showNavigatorTabsText
     *            the showNavigatorTabsText to set
     */
    public void setShowNavigatorTabsText(boolean showNavigatorTabsText) {
        this.showNavigatorTabsText = showNavigatorTabsText;
    }

    /**
     * @return the showToolBar
     */
    public boolean isShowToolBar() {
        return showToolBar;
    }

    /**
     * @param showToolBar
     *            the showToolBar to set
     */
    public void setShowToolBar(boolean showToolBar) {
        this.showToolBar = showToolBar;
    }

    /**
     * @return the highlightIncompleteTagFolders
     */
    public boolean isHighlightIncompleteTagFolders() {
        return highlightIncompleteTagFolders;
    }

    /**
     * @param highlightIncompleteTagFolders
     *            the highlightIncompleteTagFolders to set
     */
    public void setHighlightIncompleteTagFolders(boolean highlightIncompleteTagFolders) {
        this.highlightIncompleteTagFolders = highlightIncompleteTagFolders;
    }

    /**
     * @return the highlightIncompleteTagFoldersAttributes
     */
    public List<TagAttribute> getHighlightIncompleteTagFoldersAttributes() {
        return highlightIncompleteTagFoldersAttributes;
    }

    /**
     * @param highlightIncompleteTagFoldersAttributes
     *            the highlightIncompleteTagFoldersAttributes to set
     */
    public void setHighlightIncompleteTagFoldersAttributes(List<TagAttribute> highlightIncompleteTagFoldersAttributes) {
        this.highlightIncompleteTagFoldersAttributes = highlightIncompleteTagFoldersAttributes;
    }

    /**
     * @return the showContextTabsText
     */
    public boolean isShowContextTabsText() {
        return showContextTabsText;
    }

    /**
     * @param showContextTabsText
     *            the showContextTabsText to set
     */
    public void setShowContextTabsText(boolean showContextTabsText) {
        this.showContextTabsText = showContextTabsText;
    }

    public String getPlayerEngine() {
        return playerEngine;
    }

    public void setPlayerEngine(String playerEngine) {
        this.playerEngine = playerEngine;
    }

    /**
     * @return the muteEnabled
     */
    public boolean isMuteEnabled() {
        return muteEnabled;
    }

    /**
     * @param muteEnabled
     *            the muteEnabled to set
     */
    public void setMuteEnabled(boolean muteEnabled) {
        this.muteEnabled = muteEnabled;
    }

    /**
     * @return the autoScrollPlayList
     */
    public boolean isAutoScrollPlayListEnabled() {
        return autoScrollPlayListEnabled;
    }

    /**
     * @param autoScrollPlayList
     *            the autoScrollPlayList to set
     */
    public void setAutoScrollPlayListEnabled(boolean autoScrollPlayList) {
        this.autoScrollPlayListEnabled = autoScrollPlayList;
    }

    /**
     * @return the sortType
     */
    public SortType getSortType() {
        return sortType;
    }

    /**
     * @param sortType
     *            the sortType to set
     */
    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    /**
     * @return the lyricsEnginesInfo
     */
    public List<LyricsEngineInfo> getLyricsEnginesInfo() {
        return lyricsEnginesInfo;
    }

    /**
     * @param lyricsEnginesInfo
     *            the lyricsEnginesInfo to set
     */
    public void setLyricsEnginesInfo(List<LyricsEngineInfo> lyricsEnginesInfo) {
        this.lyricsEnginesInfo = lyricsEnginesInfo;
    }

    /**
     * @return the usePersonNamesArtistTagViewSorting
     */
    public boolean isUsePersonNamesArtistTagViewSorting() {
        return usePersonNamesArtistTagViewSorting;
    }

    /**
     * @param usePersonNamesArtistTagViewSorting
     *            the usePersonNamesArtistTagViewSorting to set
     */
    public void setUsePersonNamesArtistTagViewSorting(boolean usePersonNamesArtistTagViewSorting) {
        this.usePersonNamesArtistTagViewSorting = usePersonNamesArtistTagViewSorting;
    }

    /**
     * @return the lastRepositoryFolders
     */
    public List<String> getLastRepositoryFolders() {
        return lastRepositoryFolders;
    }

    /**
     * @param lastRepositoryFolders
     *            the lastRepositoryFolders to set
     */
    public void setLastRepositoryFolders(List<String> lastRepositoryFolders) {
        this.lastRepositoryFolders = lastRepositoryFolders;
    }

    /**
     * @return the hideVariousArtistsAlbums
     */
    public boolean isHideVariousArtistsAlbums() {
        return hideVariousArtistsAlbums;
    }

    /**
     * @param hideVariousArtistsAlbums
     *            the hideVariousArtistsAlbums to set
     */
    public void setHideVariousArtistsAlbums(boolean hideVariousArtistsAlbums) {
        this.hideVariousArtistsAlbums = hideVariousArtistsAlbums;
    }

    /**
     * @return the minimumSongNumberPerAlbum
     */
    public int getMinimumSongNumberPerAlbum() {
        return minimumSongNumberPerAlbum;
    }

    /**
     * @param minimumSongNumberPerAlbum
     *            the minimumSongNumberPerAlbum to set
     */
    public void setMinimumSongNumberPerAlbum(int minimumSongNumberPerAlbum) {
        this.minimumSongNumberPerAlbum = minimumSongNumberPerAlbum;
    }

    /**
     * @return the openPlaylistPath
     */
    public String getLoadPlaylistPath() {
        return loadPlaylistPath;
    }

    /**
     * @param loadPlaylistPath
     *            the loadPlaylistPath to set
     */
    public void setLoadPlaylistPath(String loadPlaylistPath) {
        this.loadPlaylistPath = loadPlaylistPath;
    }

    /**
     * @return the savePlaylistPath
     */
    public String getSavePlaylistPath() {
        return savePlaylistPath;
    }

    /**
     * @param savePlaylistPath
     *            the savePlaylistPath to set
     */
    public void setSavePlaylistPath(String savePlaylistPath) {
        this.savePlaylistPath = savePlaylistPath;
    }

    /**
     * @return the stopPlayerOnPlayListSwitch
     */
    public boolean isStopPlayerOnPlayListSwitch() {
        return stopPlayerOnPlayListSwitch;
    }

    /**
     * @param stopPlayerOnPlayListSwitch
     *            the stopPlayerOnPlayListSwitch to set
     */
    public void setStopPlayerOnPlayListSwitch(boolean stopPlayerOnPlayListSwitch) {
        this.stopPlayerOnPlayListSwitch = stopPlayerOnPlayListSwitch;
    }

    /**
     * @return the stopPlayerOnPlayListClear
     */
    public boolean isStopPlayerOnPlayListClear() {
        return stopPlayerOnPlayListClear;
    }

    /**
     * @param stopPlayerOnPlayListClear
     *            the stopPlayerOnPlayListClear to set
     */
    public void setStopPlayerOnPlayListClear(boolean stopPlayerOnPlayListClear) {
        this.stopPlayerOnPlayListClear = stopPlayerOnPlayListClear;
    }

    /**
     * @return the importExportFileNamePattern
     */
    public String getImportExportFileNamePattern() {
        return importExportFileNamePattern;
    }

    /**
     * @param importExportFileNamePattern
     *            the importExportFileNamePattern to set
     */
    public void setImportExportFileNamePattern(String importExportFileNamePattern) {
        this.importExportFileNamePattern = importExportFileNamePattern;
    }

    /**
     * @return the importExportFolderPathPattern
     */
    public String getImportExportFolderPathPattern() {
        return importExportFolderPathPattern;
    }

    /**
     * @param importExportFolderPathPattern
     *            the importExportFolderPathPattern to set
     */
    public void setImportExportFolderPathPattern(String importExportFolderPathPattern) {
        this.importExportFolderPathPattern = importExportFolderPathPattern;
    }

    /**
     * @return the reviewTagsBeforeImport
     */
    public boolean isReviewTagsBeforeImport() {
        return reviewTagsBeforeImport;
    }

    /**
     * @param reviewTagsBeforeImport
     *            the reviewTagsBeforeImport to set
     */
    public void setReviewTagsBeforeImport(boolean reviewTagsBeforeImport) {
        this.reviewTagsBeforeImport = reviewTagsBeforeImport;
    }

    /**
     * @return the setTrackNumbersWhenImporting
     */
    public boolean isSetTrackNumbersWhenImporting() {
        return setTrackNumbersWhenImporting;
    }

    /**
     * @param setTrackNumbersWhenImporting
     *            the setTrackNumbersWhenImporting to set
     */
    public void setSetTrackNumbersWhenImporting(boolean setTrackNumbersWhenImporting) {
        this.setTrackNumbersWhenImporting = setTrackNumbersWhenImporting;
    }

    /**
     * @return the setTitlesWhenImporting
     */
    public boolean isSetTitlesWhenImporting() {
        return setTitlesWhenImporting;
    }

    /**
     * @param setTitlesWhenImporting
     *            the setTitlesWhenImporting to set
     */
    public void setSetTitlesWhenImporting(boolean setTitlesWhenImporting) {
        this.setTitlesWhenImporting = setTitlesWhenImporting;
    }

    /**
     * @return the applyChangesToSourceFilesBeforeImport
     */
    public boolean isApplyChangesToSourceFilesBeforeImport() {
        return applyChangesToSourceFilesBeforeImport;
    }

    /**
     * @param applyChangesToSourceFilesBeforeImport
     *            the applyChangesToSourceFilesBeforeImport to set
     */
    public void setApplyChangesToSourceFilesBeforeImport(boolean applyChangesToSourceFilesBeforeImport) {
        this.applyChangesToSourceFilesBeforeImport = applyChangesToSourceFilesBeforeImport;
    }

    /**
     * @return the oSDWidth
     */
    public int getOsdWidth() {
        return osdWidth;
    }

    /**
     * @param width
     *            the oSDWidth to set
     */
    public void setOsdWidth(int width) {
        osdWidth = width;
    }

    /**
     * @return the osdHorizontalAlignment
     */
    public int getOsdHorizontalAlignment() {
        return osdHorizontalAlignment;
    }

    /**
     * @param osdHorizontalAlignment
     *            the osdHorizontalAlignment to set
     */
    public void setOsdHorizontalAlignment(int osdHorizontalAlignment) {
        this.osdHorizontalAlignment = osdHorizontalAlignment;
    }

    /**
     * @return the osdVerticalAlignment
     */
    public int getOsdVerticalAlignment() {
        return osdVerticalAlignment;
    }

    /**
     * @param osdVerticalAlignment
     *            the osdVerticalAlignment to set
     */
    public void setOsdVerticalAlignment(int osdVerticalAlignment) {
        this.osdVerticalAlignment = osdVerticalAlignment;
    }

    /**
     * @return the hotkeysConfig
     */
    public HotkeysConfig getHotkeysConfig() {
        return hotkeysConfig;
    }

    /**
     * @param hotkeysConfig
     *            the hotkeysConfig to set
     */
    public void setHotkeysConfig(HotkeysConfig hotkeysConfig) {
        this.hotkeysConfig = hotkeysConfig;
    }

    /**
     * @return the autoLoveFavoriteSong
     */
    public boolean isAutoLoveFavoriteSong() {
        return autoLoveFavoriteSong;
    }

    /**
     * @param autoLoveFavoriteSong
     *            the autoLoveFavoriteSong to set
     */
    public void setAutoLoveFavoriteSong(boolean autoLoveFavoriteSong) {
        this.autoLoveFavoriteSong = autoLoveFavoriteSong;
    }

    /**
     * @return the recognitionPatterns
     */
    public List<String> getRecognitionPatterns() {
        return recognitionPatterns;
    }

    /**
     * @param recognitionPatterns
     *            the recognitionPatterns to set
     */
    public void setRecognitionPatterns(List<String> recognitionPatterns) {
        this.recognitionPatterns = recognitionPatterns;
    }

    /**
     * @return the massiveRecognitionPatterns
     */
    public List<String> getMassiveRecognitionPatterns() {
        return massiveRecognitionPatterns;
    }

    /**
     * @param massiveRecognitionPatterns
     *            the massiveRecognitionPatterns to set
     */
    public void setMassiveRecognitionPatterns(List<String> massiveRecognitionPatterns) {
        this.massiveRecognitionPatterns = massiveRecognitionPatterns;
    }

    /**
     * @return the commandBeforeAccessRepository
     */
    public String getCommandBeforeAccessRepository() {
        return commandBeforeAccessRepository;
    }

    /**
     * @param commandBeforeAccessRepository
     *            the commandBeforeAccessRepository to set
     */
    public void setCommandBeforeAccessRepository(String commandBeforeAccessRepository) {
        this.commandBeforeAccessRepository = commandBeforeAccessRepository;
    }

    /**
     * @return the commandAfterAccessRepository
     */
    public String getCommandAfterAccessRepository() {
        return commandAfterAccessRepository;
    }

    /**
     * @param commandAfterAccessRepository
     *            the commandAfterAccessRepository to set
     */
    public void setCommandAfterAccessRepository(String commandAfterAccessRepository) {
        this.commandAfterAccessRepository = commandAfterAccessRepository;
    }

    /**
     * @return the deviceFileNamePattern
     */
    public String getDeviceFileNamePattern() {
        return deviceFileNamePattern;
    }

    /**
     * @param deviceFileNamePattern
     *            the deviceFileNamePattern to set
     */
    public void setDeviceFileNamePattern(String deviceFileNamePattern) {
        this.deviceFileNamePattern = deviceFileNamePattern;
    }

    /**
     * @return the deviceFolderPathPattern
     */
    public String getDeviceFolderPathPattern() {
        return deviceFolderPathPattern;
    }

    /**
     * @param deviceFolderPathPattern
     *            the deviceFolderPathPattern to set
     */
    public void setDeviceFolderPathPattern(String deviceFolderPathPattern) {
        this.deviceFolderPathPattern = deviceFolderPathPattern;
    }

    /**
     * @return the allowCopyToDeviceSameSong
     */
    public boolean isAllowRepeatedSongsInDevice() {
        return allowRepeatedSongsInDevice;
    }

    /**
     * @param allowCopyToDeviceSameSong
     *            the allowCopyToDeviceSameSong to set
     */
    public void setAllowRepeatedSongsInDevice(boolean allowCopyToDeviceSameSong) {
        this.allowRepeatedSongsInDevice = allowCopyToDeviceSameSong;
    }

    public boolean isUseLibnotify() {
        return useLibnotify;
    }

    public void setUseLibnotify(boolean useLibnotify) {
        this.useLibnotify = useLibnotify;
    }

    public FontSettings getFontSettings() {
        return fontSettings;
    }

    public void setFontSettings(FontSettings fontSettings) {
        this.fontSettings = fontSettings;
    }

}
