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
package net.sourceforge.atunes.kernel.modules.state;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.LookAndFeelSelector;
import net.sourceforge.atunes.gui.frame.Frame;
import net.sourceforge.atunes.gui.frame.FrameState;
import net.sourceforge.atunes.gui.views.dialogs.FontChooserDialog.FontSettings;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController.ViewMode;
import net.sourceforge.atunes.kernel.modules.columns.ColumnBean;
import net.sourceforge.atunes.kernel.modules.hotkeys.HotkeysConfig;
import net.sourceforge.atunes.kernel.modules.navigator.RepositoryNavigationView;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.repository.tags.HighlightFoldersByIncompleteTags;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.TagAttribute;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines.LyricsEngineInfo;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationState {

    /**
     * Singleton instance
     */
    private static ApplicationState instance;

    private boolean showAllRadioStations = true;
    private boolean showNavigationTable = true;
    private boolean showNavigationTree = true;
    private boolean showAudioObjectProperties;
    private boolean showStatusBar = true;
    private boolean showOSD;
    private boolean shuffle;
    private boolean repeat;
    private boolean showSystemTray;
    private boolean showTrayPlayer;
    private String navigationView = RepositoryNavigationView.class.getName();
    private ViewMode viewMode = ViewMode.ARTIST;
    private LocaleBean locale;
    private String defaultSearch;
    private boolean useContext = true;
    private int selectedContextTab;
    private Class<? extends Frame> frameClass;
    // Split panes divider location
    private Map<Class<? extends Frame>, FrameState> frameStates = new HashMap<Class<? extends Frame>, FrameState>();
    private boolean showPlaylistControls = true;
    private ProxyBean proxy;
    private String skin = LookAndFeelSelector.DEFAULT_SKIN;
    private FontSettings fontSettings;
    private boolean playAtStartup;
    private boolean cacheFilesBeforePlaying = false;

    // MPlayer options
    private boolean useNormalisation;
    private boolean karaoke;
    private boolean useShortPathNames = true;
    private float[] equalizerSettings;
    private boolean useFadeAway;
    private boolean showTicks;
    private boolean readInfoFromRadioStream = true;
    private boolean enableAdvancedSearch;
    private boolean enableHotkeys;
    private boolean showTitle = true;
    private int osdDuration = 2; // In seconds
    private int volume = 50;
    /**
     * Muted property: <code>true</code> if sound is disabled
     */
    private boolean muteEnabled;

    private int autoRepositoryRefreshTime = 60; // In minutes
    private boolean saveRepositoryAsXml = true;
    private boolean showFavoritesInNavigator = true;
    private boolean useCdErrorCorrection = false;
    private boolean useSmartTagViewSorting;
    private boolean usePersonNamesArtistTagViewSorting;
    private boolean saveContextPicture;
    private boolean showExtendedTooltip = true;
    private int extendedTooltipDelay = 1; // In seconds

    /**
     * The last fm enabled. It is not enabled until user sets login user and
     * password
     */
    private boolean lastFmEnabled = false;
    private String lastFmUser;
    private String lastFmPassword;
    /** Option to add loved songs in LastFM when adding a song as favorite */
    private boolean autoLoveFavoriteSong = false;
    private byte[] encryptedLastFmPassword;
    private List<LyricsEngineInfo> lyricsEnginesInfo;
    private String fullScreenBackground;
    /**
     * The window x position. Default -1: No window position
     */
    private int windowXPosition = -1;
    /**
     * The window y position. Default -1: No window position
     */
    private int windowYPosition = -1;
    private boolean maximized;
    private int windowWidth;
    private int windowHeight;
    private int multipleViewXPosition;
    private int multipleViewYPosition;
    private int multipleViewWidth;
    private int multipleViewHeight;
    private int nagDialogCounter;

    private String encoder = "OGG";
    private String encoderQuality = "5";
    private String mp3EncoderQuality = "medium";
    private String flacEncoderQuality = "-5";
    private String cdRipperFileNamePattern;

    // Columns config
    private Map<String, ColumnBean> columns;
    private Map<String, ColumnBean> defaultNavigatorColumns;
    private Map<String, ColumnBean> searchResultsColumns;
    private Map<String, Map<String, ColumnBean>> customNavigatorColumns;

    /**
     * Default location where device is plugged. Used to connect device
     * automatically
     */
    private String defaultDeviceLocation;
    private long podcastFeedEntriesRetrievalInterval = PodcastFeedHandler.DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL;
    private String podcastFeedEntryDownloadPath;
    private boolean useDownloadedPodcastFeedEntries = true;
    private boolean removePodcastFeedEntriesRemovedFromPodcastFeed;
    /**
     * Property to show navigator tabs at left (true) or not (false)
     */
    private boolean showNavigatorTabsAtLeft = true;
    /**
     * Property to show navigator tabs text or not
     */
    private boolean showNavigatorTabsText = true;
    /**
     * Property to show context tabs text or not
     */
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
    /**
     * Property to show tool bar or not
     */
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
    private boolean showContextAlbumsInGrid;

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

    public boolean isShowAllRadioStations() {
        return showAllRadioStations;
    }

    public void setShowAllRadioStations(boolean showAllRadioStations) {
        this.showAllRadioStations = showAllRadioStations;
    }

    public boolean isShowNavigationTable() {
        return showNavigationTable;
    }

    public void setShowNavigationTable(boolean showNavigationTable) {
        this.showNavigationTable = showNavigationTable;
    }

    public boolean isShowAudioObjectProperties() {
        return showAudioObjectProperties;
    }

    public void setShowAudioObjectProperties(boolean showAudioObjectProperties) {
        this.showAudioObjectProperties = showAudioObjectProperties;
    }

    public boolean isShowStatusBar() {
        return showStatusBar;
    }

    public void setShowStatusBar(boolean showStatusBar) {
        this.showStatusBar = showStatusBar;
    }

    public boolean isShowOSD() {
        return showOSD;
    }

    public void setShowOSD(boolean showOSD) {
        this.showOSD = showOSD;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isShowSystemTray() {
        return showSystemTray;
    }

    public void setShowSystemTray(boolean showSystemTray) {
        this.showSystemTray = showSystemTray;
    }

    public boolean isShowTrayPlayer() {
        return showTrayPlayer;
    }

    public void setShowTrayPlayer(boolean showTrayPlayer) {
        this.showTrayPlayer = showTrayPlayer;
    }

    public String getNavigationView() {
        return navigationView;
    }

    public void setNavigationView(String navigationView) {
        this.navigationView = navigationView;
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    public LocaleBean getLocale() {
        return locale;
    }

    public void setLocale(LocaleBean locale) {
        this.locale = locale;
    }

    public String getDefaultSearch() {
        return defaultSearch;
    }

    public void setDefaultSearch(String defaultSearch) {
        this.defaultSearch = defaultSearch;
    }

    public boolean isUseContext() {
        return useContext;
    }

    public void setUseContext(boolean useContext) {
        this.useContext = useContext;
    }

    public int getSelectedContextTab() {
        return selectedContextTab;
    }

    public void setSelectedContextTab(int selectedContextTab) {
        this.selectedContextTab = selectedContextTab;
    }

    public Class<? extends Frame> getFrameClass() {
        return frameClass;
    }

    public void setFrameClass(Class<? extends Frame> frameClass) {
        this.frameClass = frameClass;
    }

    public boolean isShowPlaylistControls() {
        return showPlaylistControls;
    }

    public void setShowPlaylistControls(boolean showPlaylistControls) {
        this.showPlaylistControls = showPlaylistControls;
    }

    public ProxyBean getProxy() {
        return proxy;
    }

    public void setProxy(ProxyBean proxy) {
        this.proxy = proxy;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public FontSettings getFontSettings() {
        return fontSettings;
    }

    public void setFontSettings(FontSettings fontSettings) {
        this.fontSettings = fontSettings;
    }

    public boolean isPlayAtStartup() {
        return playAtStartup;
    }

    public void setPlayAtStartup(boolean playAtStartup) {
        this.playAtStartup = playAtStartup;
    }

    public boolean isCacheFilesBeforePlaying() {
        return cacheFilesBeforePlaying;
    }

    public void setCacheFilesBeforePlaying(boolean cacheFilesBeforePlaying) {
        this.cacheFilesBeforePlaying = cacheFilesBeforePlaying;
    }

    public boolean isUseNormalisation() {
        return useNormalisation;
    }

    public void setUseNormalisation(boolean useNormalisation) {
        this.useNormalisation = useNormalisation;
    }

    public boolean isKaraoke() {
        return karaoke;
    }

    public void setKaraoke(boolean karaoke) {
        this.karaoke = karaoke;
    }

    public boolean isUseShortPathNames() {
        return useShortPathNames;
    }

    public void setUseShortPathNames(boolean useShortPathNames) {
        this.useShortPathNames = useShortPathNames;
    }

    public float[] getEqualizerSettings() {
        return equalizerSettings;
    }

    public void setEqualizerSettings(float[] equalizerSettings) {
        this.equalizerSettings = equalizerSettings;
    }

    public boolean isUseFadeAway() {
        return useFadeAway;
    }

    public void setUseFadeAway(boolean useFadeAway) {
        this.useFadeAway = useFadeAway;
    }

    public boolean isShowTicks() {
        return showTicks;
    }

    public void setShowTicks(boolean showTicks) {
        this.showTicks = showTicks;
    }

    public boolean isReadInfoFromRadioStream() {
        return readInfoFromRadioStream;
    }

    public void setReadInfoFromRadioStream(boolean readInfoFromRadioStream) {
        this.readInfoFromRadioStream = readInfoFromRadioStream;
    }

    public boolean isEnableAdvancedSearch() {
        return enableAdvancedSearch;
    }

    public void setEnableAdvancedSearch(boolean enableAdvancedSearch) {
        this.enableAdvancedSearch = enableAdvancedSearch;
    }

    public boolean isEnableHotkeys() {
        return enableHotkeys;
    }

    public void setEnableHotkeys(boolean enableHotkeys) {
        this.enableHotkeys = enableHotkeys;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }

    public int getOsdDuration() {
        return osdDuration;
    }

    public void setOsdDuration(int osdDuration) {
        this.osdDuration = osdDuration;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public boolean isMuteEnabled() {
        return muteEnabled;
    }

    public void setMuteEnabled(boolean muteEnabled) {
        this.muteEnabled = muteEnabled;
    }

    public int getAutoRepositoryRefreshTime() {
        return autoRepositoryRefreshTime;
    }

    public void setAutoRepositoryRefreshTime(int autoRepositoryRefreshTime) {
        this.autoRepositoryRefreshTime = autoRepositoryRefreshTime;
    }

    public boolean isSaveRepositoryAsXml() {
        return saveRepositoryAsXml;
    }

    public void setSaveRepositoryAsXml(boolean saveRepositoryAsXml) {
        this.saveRepositoryAsXml = saveRepositoryAsXml;
    }

    public boolean isShowFavoritesInNavigator() {
        return showFavoritesInNavigator;
    }

    public void setShowFavoritesInNavigator(boolean showFavoritesInNavigator) {
        this.showFavoritesInNavigator = showFavoritesInNavigator;
    }

    public boolean isUseCdErrorCorrection() {
        return useCdErrorCorrection;
    }

    public void setUseCdErrorCorrection(boolean useCdErrorCorrection) {
        this.useCdErrorCorrection = useCdErrorCorrection;
    }

    public boolean isUseSmartTagViewSorting() {
        return useSmartTagViewSorting;
    }

    public void setUseSmartTagViewSorting(boolean useSmartTagViewSorting) {
        this.useSmartTagViewSorting = useSmartTagViewSorting;
    }

    public boolean isUsePersonNamesArtistTagViewSorting() {
        return usePersonNamesArtistTagViewSorting;
    }

    public void setUsePersonNamesArtistTagViewSorting(boolean usePersonNamesArtistTagViewSorting) {
        this.usePersonNamesArtistTagViewSorting = usePersonNamesArtistTagViewSorting;
    }

    public boolean isSaveContextPicture() {
        return saveContextPicture;
    }

    public void setSaveContextPicture(boolean saveContextPicture) {
        this.saveContextPicture = saveContextPicture;
    }

    public boolean isShowExtendedTooltip() {
        return showExtendedTooltip;
    }

    public void setShowExtendedTooltip(boolean showExtendedTooltip) {
        this.showExtendedTooltip = showExtendedTooltip;
    }

    public int getExtendedTooltipDelay() {
        return extendedTooltipDelay;
    }

    public void setExtendedTooltipDelay(int extendedTooltipDelay) {
        this.extendedTooltipDelay = extendedTooltipDelay;
    }

    public boolean isLastFmEnabled() {
        return lastFmEnabled;
    }

    public void setLastFmEnabled(boolean lastFmEnabled) {
        this.lastFmEnabled = lastFmEnabled;
    }

    public String getLastFmUser() {
        return lastFmUser;
    }

    public void setLastFmUser(String lastFmUser) {
        this.lastFmUser = lastFmUser;
    }

    public String getLastFmPassword() {
        return lastFmPassword;
    }

    public void setLastFmPassword(String lastFmPassword) {
        this.lastFmPassword = lastFmPassword;
    }

    public boolean isAutoLoveFavoriteSong() {
        return autoLoveFavoriteSong;
    }

    public void setAutoLoveFavoriteSong(boolean autoLoveFavoriteSong) {
        this.autoLoveFavoriteSong = autoLoveFavoriteSong;
    }

    public byte[] getEncryptedLastFmPassword() {
        return encryptedLastFmPassword;
    }

    public void setEncryptedLastFmPassword(byte[] encryptedLastFmPassword) {
        this.encryptedLastFmPassword = encryptedLastFmPassword;
    }

    public List<LyricsEngineInfo> getLyricsEnginesInfo() {
        return lyricsEnginesInfo;
    }

    public void setLyricsEnginesInfo(List<LyricsEngineInfo> lyricsEnginesInfo) {
        this.lyricsEnginesInfo = lyricsEnginesInfo;
    }

    public String getFullScreenBackground() {
        return fullScreenBackground;
    }

    public void setFullScreenBackground(String fullScreenBackground) {
        this.fullScreenBackground = fullScreenBackground;
    }

    public int getWindowXPosition() {
        return windowXPosition;
    }

    public void setWindowXPosition(int windowXPosition) {
        this.windowXPosition = windowXPosition;
    }

    public int getWindowYPosition() {
        return windowYPosition;
    }

    public void setWindowYPosition(int windowYPosition) {
        this.windowYPosition = windowYPosition;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMaximized(boolean maximized) {
        this.maximized = maximized;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public int getMultipleViewXPosition() {
        return multipleViewXPosition;
    }

    public void setMultipleViewXPosition(int multipleViewXPosition) {
        this.multipleViewXPosition = multipleViewXPosition;
    }

    public int getMultipleViewYPosition() {
        return multipleViewYPosition;
    }

    public void setMultipleViewYPosition(int multipleViewYPosition) {
        this.multipleViewYPosition = multipleViewYPosition;
    }

    public int getMultipleViewWidth() {
        return multipleViewWidth;
    }

    public void setMultipleViewWidth(int multipleViewWidth) {
        this.multipleViewWidth = multipleViewWidth;
    }

    public int getMultipleViewHeight() {
        return multipleViewHeight;
    }

    public void setMultipleViewHeight(int multipleViewHeight) {
        this.multipleViewHeight = multipleViewHeight;
    }

    public int getNagDialogCounter() {
        return nagDialogCounter;
    }

    public void setNagDialogCounter(int nagDialogCounter) {
        this.nagDialogCounter = nagDialogCounter;
    }

    public String getEncoder() {
        return encoder;
    }

    public void setEncoder(String encoder) {
        this.encoder = encoder;
    }

    public String getEncoderQuality() {
        return encoderQuality;
    }

    public void setEncoderQuality(String encoderQuality) {
        this.encoderQuality = encoderQuality;
    }

    public String getMp3EncoderQuality() {
        return mp3EncoderQuality;
    }

    public void setMp3EncoderQuality(String mp3EncoderQuality) {
        this.mp3EncoderQuality = mp3EncoderQuality;
    }

    public String getFlacEncoderQuality() {
        return flacEncoderQuality;
    }

    public void setFlacEncoderQuality(String flacEncoderQuality) {
        this.flacEncoderQuality = flacEncoderQuality;
    }

    public String getCdRipperFileNamePattern() {
        return cdRipperFileNamePattern;
    }

    public void setCdRipperFileNamePattern(String cdRipperFileNamePattern) {
        this.cdRipperFileNamePattern = cdRipperFileNamePattern;
    }

    public Map<String, ColumnBean> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, ColumnBean> columns) {
        this.columns = columns;
    }

    public Map<String, ColumnBean> getDefaultNavigatorColumns() {
        return defaultNavigatorColumns;
    }

    public void setDefaultNavigatorColumns(Map<String, ColumnBean> navigatorColumns) {
        this.defaultNavigatorColumns = navigatorColumns;
    }

    public Map<String, ColumnBean> getSearchResultsColumns() {
        return searchResultsColumns;
    }

    public void setSearchResultsColumns(Map<String, ColumnBean> searchResultsColumns) {
        this.searchResultsColumns = searchResultsColumns;
    }

    public Map<Class<? extends Frame>, FrameState> getFrameStates() {
        return frameStates;
    }

    public void setFrameStates(Map<Class<? extends Frame>, FrameState> frameStates) {
        this.frameStates = frameStates;
    }

    public String getDefaultDeviceLocation() {
        return defaultDeviceLocation;
    }

    public void setDefaultDeviceLocation(String defaultDeviceLocation) {
        this.defaultDeviceLocation = defaultDeviceLocation;
    }

    public long getPodcastFeedEntriesRetrievalInterval() {
        return podcastFeedEntriesRetrievalInterval;
    }

    public void setPodcastFeedEntriesRetrievalInterval(long podcastFeedEntriesRetrievalInterval) {
        this.podcastFeedEntriesRetrievalInterval = podcastFeedEntriesRetrievalInterval;
    }

    public String getPodcastFeedEntryDownloadPath() {
        return podcastFeedEntryDownloadPath;
    }

    public void setPodcastFeedEntryDownloadPath(String podcastFeedEntryDownloadPath) {
        this.podcastFeedEntryDownloadPath = podcastFeedEntryDownloadPath;
    }

    public boolean isUseDownloadedPodcastFeedEntries() {
        return useDownloadedPodcastFeedEntries;
    }

    public void setUseDownloadedPodcastFeedEntries(boolean useDownloadedPodcastFeedEntries) {
        this.useDownloadedPodcastFeedEntries = useDownloadedPodcastFeedEntries;
    }

    public boolean isRemovePodcastFeedEntriesRemovedFromPodcastFeed() {
        return removePodcastFeedEntriesRemovedFromPodcastFeed;
    }

    public void setRemovePodcastFeedEntriesRemovedFromPodcastFeed(boolean removePodcastFeedEntriesRemovedFromPodcastFeed) {
        this.removePodcastFeedEntriesRemovedFromPodcastFeed = removePodcastFeedEntriesRemovedFromPodcastFeed;
    }

    public boolean isShowNavigatorTabsAtLeft() {
        return showNavigatorTabsAtLeft;
    }

    public void setShowNavigatorTabsAtLeft(boolean showNavigatorTabsAtLeft) {
        this.showNavigatorTabsAtLeft = showNavigatorTabsAtLeft;
    }

    public boolean isShowNavigatorTabsText() {
        return showNavigatorTabsText;
    }

    public void setShowNavigatorTabsText(boolean showNavigatorTabsText) {
        this.showNavigatorTabsText = showNavigatorTabsText;
    }

    public boolean isShowContextTabsText() {
        return showContextTabsText;
    }

    public void setShowContextTabsText(boolean showContextTabsText) {
        this.showContextTabsText = showContextTabsText;
    }

    public boolean isHideVariousArtistsAlbums() {
        return hideVariousArtistsAlbums;
    }

    public void setHideVariousArtistsAlbums(boolean hideVariousArtistsAlbums) {
        this.hideVariousArtistsAlbums = hideVariousArtistsAlbums;
    }

    public int getMinimumSongNumberPerAlbum() {
        return minimumSongNumberPerAlbum;
    }

    public void setMinimumSongNumberPerAlbum(int minimumSongNumberPerAlbum) {
        this.minimumSongNumberPerAlbum = minimumSongNumberPerAlbum;
    }

    public boolean isHighlightIncompleteTagFolders() {
        return highlightIncompleteTagFolders;
    }

    public void setHighlightIncompleteTagFolders(boolean highlightIncompleteTagFolders) {
        this.highlightIncompleteTagFolders = highlightIncompleteTagFolders;
    }

    public List<TagAttribute> getHighlightIncompleteTagFoldersAttributes() {
        return highlightIncompleteTagFoldersAttributes;
    }

    public void setHighlightIncompleteTagFoldersAttributes(List<TagAttribute> highlightIncompleteTagFoldersAttributes) {
        this.highlightIncompleteTagFoldersAttributes = highlightIncompleteTagFoldersAttributes;
    }

    public boolean isShowToolBar() {
        return showToolBar;
    }

    public void setShowToolBar(boolean showToolBar) {
        this.showToolBar = showToolBar;
    }

    public String getPlayerEngine() {
        return playerEngine;
    }

    public void setPlayerEngine(String playerEngine) {
        this.playerEngine = playerEngine;
    }

    public boolean isAutoScrollPlayListEnabled() {
        return autoScrollPlayListEnabled;
    }

    public void setAutoScrollPlayListEnabled(boolean autoScrollPlayListEnabled) {
        this.autoScrollPlayListEnabled = autoScrollPlayListEnabled;
    }

    public List<String> getLastRepositoryFolders() {
        return lastRepositoryFolders;
    }

    public void setLastRepositoryFolders(List<String> lastRepositoryFolders) {
        this.lastRepositoryFolders = lastRepositoryFolders;
    }

    public String getLoadPlaylistPath() {
        return loadPlaylistPath;
    }

    public void setLoadPlaylistPath(String loadPlaylistPath) {
        this.loadPlaylistPath = loadPlaylistPath;
    }

    public String getSavePlaylistPath() {
        return savePlaylistPath;
    }

    public void setSavePlaylistPath(String savePlaylistPath) {
        this.savePlaylistPath = savePlaylistPath;
    }

    public boolean isStopPlayerOnPlayListSwitch() {
        return stopPlayerOnPlayListSwitch;
    }

    public void setStopPlayerOnPlayListSwitch(boolean stopPlayerOnPlayListSwitch) {
        this.stopPlayerOnPlayListSwitch = stopPlayerOnPlayListSwitch;
    }

    public boolean isStopPlayerOnPlayListClear() {
        return stopPlayerOnPlayListClear;
    }

    public void setStopPlayerOnPlayListClear(boolean stopPlayerOnPlayListClear) {
        this.stopPlayerOnPlayListClear = stopPlayerOnPlayListClear;
    }

    public String getImportExportFileNamePattern() {
        return importExportFileNamePattern;
    }

    public void setImportExportFileNamePattern(String importExportFileNamePattern) {
        this.importExportFileNamePattern = importExportFileNamePattern;
    }

    public String getDeviceFileNamePattern() {
        return deviceFileNamePattern;
    }

    public void setDeviceFileNamePattern(String deviceFileNamePattern) {
        this.deviceFileNamePattern = deviceFileNamePattern;
    }

    public String getImportExportFolderPathPattern() {
        return importExportFolderPathPattern;
    }

    public void setImportExportFolderPathPattern(String importExportFolderPathPattern) {
        this.importExportFolderPathPattern = importExportFolderPathPattern;
    }

    public String getDeviceFolderPathPattern() {
        return deviceFolderPathPattern;
    }

    public void setDeviceFolderPathPattern(String deviceFolderPathPattern) {
        this.deviceFolderPathPattern = deviceFolderPathPattern;
    }

    public boolean isAllowRepeatedSongsInDevice() {
        return allowRepeatedSongsInDevice;
    }

    public void setAllowRepeatedSongsInDevice(boolean allowRepeatedSongsInDevice) {
        this.allowRepeatedSongsInDevice = allowRepeatedSongsInDevice;
    }

    public boolean isReviewTagsBeforeImport() {
        return reviewTagsBeforeImport;
    }

    public void setReviewTagsBeforeImport(boolean reviewTagsBeforeImport) {
        this.reviewTagsBeforeImport = reviewTagsBeforeImport;
    }

    public boolean isApplyChangesToSourceFilesBeforeImport() {
        return applyChangesToSourceFilesBeforeImport;
    }

    public void setApplyChangesToSourceFilesBeforeImport(boolean applyChangesToSourceFilesBeforeImport) {
        this.applyChangesToSourceFilesBeforeImport = applyChangesToSourceFilesBeforeImport;
    }

    public boolean isSetTrackNumbersWhenImporting() {
        return setTrackNumbersWhenImporting;
    }

    public void setSetTrackNumbersWhenImporting(boolean setTrackNumbersWhenImporting) {
        this.setTrackNumbersWhenImporting = setTrackNumbersWhenImporting;
    }

    public boolean isSetTitlesWhenImporting() {
        return setTitlesWhenImporting;
    }

    public void setSetTitlesWhenImporting(boolean setTitlesWhenImporting) {
        this.setTitlesWhenImporting = setTitlesWhenImporting;
    }

    public int getOsdWidth() {
        return osdWidth;
    }

    public void setOsdWidth(int osdWidth) {
        this.osdWidth = osdWidth;
    }

    public int getOsdHorizontalAlignment() {
        return osdHorizontalAlignment;
    }

    public void setOsdHorizontalAlignment(int osdHorizontalAlignment) {
        this.osdHorizontalAlignment = osdHorizontalAlignment;
    }

    public int getOsdVerticalAlignment() {
        return osdVerticalAlignment;
    }

    public void setOsdVerticalAlignment(int osdVerticalAlignment) {
        this.osdVerticalAlignment = osdVerticalAlignment;
    }

    public HotkeysConfig getHotkeysConfig() {
        return hotkeysConfig;
    }

    public void setHotkeysConfig(HotkeysConfig hotkeysConfig) {
        this.hotkeysConfig = hotkeysConfig;
    }

    public List<String> getRecognitionPatterns() {
        return recognitionPatterns;
    }

    public void setRecognitionPatterns(List<String> recognitionPatterns) {
        this.recognitionPatterns = recognitionPatterns;
    }

    public List<String> getMassiveRecognitionPatterns() {
        return massiveRecognitionPatterns;
    }

    public void setMassiveRecognitionPatterns(List<String> massiveRecognitionPatterns) {
        this.massiveRecognitionPatterns = massiveRecognitionPatterns;
    }

    public String getCommandBeforeAccessRepository() {
        return commandBeforeAccessRepository;
    }

    public void setCommandBeforeAccessRepository(String commandBeforeAccessRepository) {
        this.commandBeforeAccessRepository = commandBeforeAccessRepository;
    }

    public String getCommandAfterAccessRepository() {
        return commandAfterAccessRepository;
    }

    public void setCommandAfterAccessRepository(String commandAfterAccessRepository) {
        this.commandAfterAccessRepository = commandAfterAccessRepository;
    }

    public boolean isUseLibnotify() {
        return useLibnotify;
    }

    public void setUseLibnotify(boolean useLibnotify) {
        this.useLibnotify = useLibnotify;
    }

    public boolean isShowContextAlbumsInGrid() {
        return showContextAlbumsInGrid;
    }

    public void setShowContextAlbumsInGrid(boolean showContextAlbumsInGrid) {
        this.showContextAlbumsInGrid = showContextAlbumsInGrid;
    }

    public boolean isShowNavigationTree() {
        return showNavigationTree;
    }

    public void setShowNavigationTree(boolean showNavigationTree) {
        this.showNavigationTree = showNavigationTree;
    }

	/**
	 * @return the customNavigatorColumns
	 */
	public Map<String, Map<String, ColumnBean>> getCustomNavigatorColumns() {
		return customNavigatorColumns;
	}

	/**
	 * @param customNavigatorColumns the customNavigatorColumns to set
	 */
	public void setCustomNavigatorColumns(
			Map<String, Map<String, ColumnBean>> customNavigatorColumns) {
		this.customNavigatorColumns = customNavigatorColumns;
	}

}
