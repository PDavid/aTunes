/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.frame.Frame;
import net.sourceforge.atunes.gui.frame.FrameState;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelBean;
import net.sourceforge.atunes.gui.views.dialogs.FontChooserDialog.FontSettings;
import net.sourceforge.atunes.kernel.modules.columns.ColumnBean;
import net.sourceforge.atunes.kernel.modules.hotkeys.HotkeysConfig;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler.ViewMode;
import net.sourceforge.atunes.kernel.modules.navigator.RepositoryNavigationView;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.modules.tags.IncompleteTagsChecker;
import net.sourceforge.atunes.kernel.modules.tags.TagAttribute;
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

    private PreferencesCache cache;
    
    /**
     * Instantiates a new application state.
     */
    private ApplicationState() {
        this.cache = new PreferencesCache();
    }

    /**
     * @return the instance
     */
    public static ApplicationState getInstance() {
        if (instance == null) {
            instance = new ApplicationState();
        }
        return instance;
    }
    
    
    
    public boolean isShowAllRadioStations() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SHOW_ALL_RADIO_STATIONS, true);
    }

    public void setShowAllRadioStations(boolean showAllRadioStations) {
        this.cache.storePreference(Preferences.SHOW_ALL_RADIO_STATIONS, showAllRadioStations);
    }
    
    

    public boolean isShowNavigationTable() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SHOW_NAVIGATION_TABLE, true);
    }

    public void setShowNavigationTable(boolean showNavigationTable) {
        this.cache.storePreference(Preferences.SHOW_NAVIGATION_TABLE, showNavigationTable);
    }
    
    

    public boolean isShowAudioObjectProperties() {
        return (Boolean) this.cache.retrievePreference(Preferences.SHOW_AUDIO_OBJECT_PROPERTIES, false);
    }

    public void setShowAudioObjectProperties(boolean showAudioObjectProperties) {
        this.cache.storePreference(Preferences.SHOW_AUDIO_OBJECT_PROPERTIES, showAudioObjectProperties);
    }
    
    

    public boolean isShowStatusBar() {
        return (Boolean) this.cache.retrievePreference(Preferences.SHOW_STATUS_BAR, true);
    }

    public void setShowStatusBar(boolean showStatusBar) {
        this.cache.storePreference(Preferences.SHOW_STATUS_BAR, showStatusBar);
    }
    
    

    public boolean isShowOSD() {
        return (Boolean) this.cache.retrievePreference(Preferences.SHOW_OSD, true);
    }

    public void setShowOSD(boolean showOSD) {
    	this.cache.storePreference(Preferences.SHOW_OSD, showOSD);
    }
    
    

    public boolean isShuffle() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SHUFFLE, false);
    }

    public void setShuffle(boolean shuffle) {
        this.cache.storePreference(Preferences.SHUFFLE, shuffle);
    }
    
    

    public boolean isRepeat() {
        return (Boolean) this.cache.retrievePreference(Preferences.REPEAT, false);
    }

    public void setRepeat(boolean repeat) {
        this.cache.storePreference(Preferences.REPEAT, repeat);
    }
    
    

    public boolean isShowSystemTray() {
        return (Boolean) this.cache.retrievePreference(Preferences.SHOW_SYSTEM_TRAY, false);
    }

    public void setShowSystemTray(boolean showSystemTray) {
    	this.cache.storePreference(Preferences.SHOW_SYSTEM_TRAY, showSystemTray);
    }
    
    

    public boolean isShowTrayPlayer() {
        return (Boolean) this.cache.retrievePreference(Preferences.SHOW_TRAY_PLAYER, false);
    }

    public void setShowTrayPlayer(boolean showTrayPlayer) {
    	this.cache.storePreference(Preferences.SHOW_TRAY_PLAYER, showTrayPlayer);
    }
    
    

    public String getNavigationView() {
        return (String) this.cache.retrievePreference(Preferences.NAVIGATION_VIEW, RepositoryNavigationView.class.getName());
    }

    public void setNavigationView(String navigationView) {
        this.cache.storePreference(Preferences.NAVIGATION_VIEW, navigationView);
    }
    
    

    public ViewMode getViewMode() {
        return (ViewMode) this.cache.retrievePreference(Preferences.VIEW_MODE, ViewMode.ARTIST);
    }

    public void setViewMode(ViewMode viewMode) {
        this.cache.storePreference(Preferences.VIEW_MODE, viewMode);
    }
    
    

    public LocaleBean getLocale() {
    	return (LocaleBean) this.cache.retrievePreference(Preferences.LOCALE, null);
    }

    public void setLocale(LocaleBean locale) {
        this.cache.storePreference(Preferences.LOCALE, locale);
    }
    
    

    public LocaleBean getOldLocale() {
        return (LocaleBean) this.cache.retrievePreference(Preferences.OLD_LOCALE, null);
    }

    public void setOldLocale(LocaleBean oldLocale) {
        this.cache.storePreference(Preferences.OLD_LOCALE, oldLocale);
    }
    
    

    public String getDefaultSearch() {
        return (String) this.cache.retrievePreference(Preferences.DEFAULT_SEARCH, null);
    }

    public void setDefaultSearch(String defaultSearch) {
    	this.cache.storePreference(Preferences.DEFAULT_SEARCH, defaultSearch);
    }
    
    

    public boolean isUseContext() {
        return (Boolean) this.cache.retrievePreference(Preferences.USE_CONTEXT, true);
    }

    public void setUseContext(boolean useContext) {
        this.cache.storePreference(Preferences.USE_CONTEXT, useContext);
    }
    
    

    public int getSelectedContextTab() {
        return (Integer) this.cache.retrievePreference(Preferences.SELECTED_CONTEXT_TAB, 0);
    }

    public void setSelectedContextTab(int selectedContextTab) {
    	this.cache.storePreference(Preferences.SELECTED_CONTEXT_TAB, selectedContextTab);
    }
    
    

    @SuppressWarnings("unchecked")
	public Class<? extends Frame> getFrameClass() {
        return (Class<? extends Frame>) this.cache.retrievePreference(Preferences.FRAME_CLASS, null);
    }

    public void setFrameClass(Class<? extends Frame> frameClass) {
    	this.cache.storePreference(Preferences.FRAME_CLASS, frameClass);
    }
    
    

    public boolean isShowPlaylistControls() {
        return (Boolean) this.cache.retrievePreference(Preferences.SHOW_PLAYLIST_CONTROLS, true);
    }

    public void setShowPlaylistControls(boolean showPlaylistControls) {
    	this.cache.storePreference(Preferences.SHOW_PLAYLIST_CONTROLS, showPlaylistControls);
    }
    
    

    public ProxyBean getProxy() {
        return (ProxyBean) this.cache.retrievePreference(Preferences.PROXY, null);
    }

    public void setProxy(ProxyBean proxy) {
        this.cache.storePreference(Preferences.PROXY, proxy);
    }
    
    

    public LookAndFeelBean getLookAndFeel() {
        return (LookAndFeelBean) this.cache.retrievePreference(Preferences.LOOK_AND_FEEL, null);
    }

    public void setLookAndFeel(LookAndFeelBean lookAndFeel) {
    	this.cache.storePreference(Preferences.LOOK_AND_FEEL, lookAndFeel);
    }
    
    

    public FontSettings getFontSettings() {
        return (FontSettings) this.cache.retrievePreference(Preferences.FONT_SETTINGS, null);
    }

    public void setFontSettings(FontSettings fontSettings) {
    	this.cache.storePreference(Preferences.FONT_SETTINGS, fontSettings);
    }
    
    

    public boolean isPlayAtStartup() {
        return (Boolean) this.cache.retrievePreference(Preferences.PLAY_AT_STARTUP, false);
    }

    public void setPlayAtStartup(boolean playAtStartup) {
    	this.cache.storePreference(Preferences.PLAY_AT_STARTUP, playAtStartup);
    }
    
    

    public boolean isCacheFilesBeforePlaying() {
    	return (Boolean) this.cache.retrievePreference(Preferences.CACHE_FILES_BEFORE_PLAYING, false);
    }

    public void setCacheFilesBeforePlaying(boolean cacheFilesBeforePlaying) {
    	this.cache.storePreference(Preferences.CACHE_FILES_BEFORE_PLAYING, cacheFilesBeforePlaying);
    }
    
    

    public boolean isUseNormalisation() {
    	return (Boolean) this.cache.retrievePreference(Preferences.USE_NORMALIZATION, false);
    }

    public void setUseNormalisation(boolean useNormalisation) {
    	this.cache.storePreference(Preferences.USE_NORMALIZATION, useNormalisation);
    }
    
    

    public boolean isKaraoke() {
    	return (Boolean) this.cache.retrievePreference(Preferences.KARAOKE, false);
    }

    public void setKaraoke(boolean karaoke) {
    	this.cache.storePreference(Preferences.KARAOKE, karaoke);
    }
    
    

    public boolean isUseShortPathNames() {
    	return (Boolean) this.cache.retrievePreference(Preferences.USE_SHORT_PATH_NAMES, true);
    }

    public void setUseShortPathNames(boolean useShortPathNames) {
    	this.cache.storePreference(Preferences.USE_SHORT_PATH_NAMES, useShortPathNames);
    }
    
    

    public float[] getEqualizerSettings() {
    	return (float[]) this.cache.retrievePreference(Preferences.EQUALIZER_SETTINGS, null);
    }

    public void setEqualizerSettings(float[] equalizerSettings) {
    	this.cache.storePreference(Preferences.EQUALIZER_SETTINGS, equalizerSettings);
    }
    
    

    public boolean isUseFadeAway() {
    	return (Boolean) this.cache.retrievePreference(Preferences.USE_FADE_AWAY, false);
    }

    public void setUseFadeAway(boolean useFadeAway) {
    	this.cache.storePreference(Preferences.USE_FADE_AWAY, useFadeAway);
    }
    
    

    public boolean isShowTicks() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SHOW_TICKS, false);
    }

    public void setShowTicks(boolean showTicks) {
    	this.cache.storePreference(Preferences.SHOW_TICKS, showTicks);
    }
    
    
    
    public boolean isShowAdvancedPlayerControls() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SHOW_ADVANCED_PLAYER_CONTROLS, false);
    }
    
    public void setShowAdvancedPlayerControls(boolean show) {
    	this.cache.storePreference(Preferences.SHOW_ADVANCED_PLAYER_CONTROLS, show);
    }
    
    

    public boolean isReadInfoFromRadioStream() {
    	return (Boolean) this.cache.retrievePreference(Preferences.READ_INFO_FROM_RADIO_STREAM, true);
    }

    public void setReadInfoFromRadioStream(boolean readInfoFromRadioStream) {
    	this.cache.storePreference(Preferences.READ_INFO_FROM_RADIO_STREAM, readInfoFromRadioStream);
    }
    
    

    public boolean isEnableAdvancedSearch() {
    	return (Boolean) this.cache.retrievePreference(Preferences.ENABLE_ADVANCED_SEARCH, false);
    }

    public void setEnableAdvancedSearch(boolean enableAdvancedSearch) {
    	this.cache.storePreference(Preferences.ENABLE_ADVANCED_SEARCH, enableAdvancedSearch);
    }
    
    

    public boolean isEnableHotkeys() {
    	return (Boolean) this.cache.retrievePreference(Preferences.ENABLE_HOTKEYS, false);
    }

    public void setEnableHotkeys(boolean enableHotkeys) {
    	this.cache.storePreference(Preferences.ENABLE_HOTKEYS, enableHotkeys);
    }
    
    

    public boolean isShowTitle() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SHOW_TITLE, true);
    }

    public void setShowTitle(boolean showTitle) {
    	this.cache.storePreference(Preferences.SHOW_TITLE, showTitle);
    }
    
    

    public int getOsdDuration() {
    	return (Integer) this.cache.retrievePreference(Preferences.OSD_DURATION, 2);
    }

    public void setOsdDuration(int osdDuration) {
    	this.cache.storePreference(Preferences.OSD_DURATION, osdDuration);
    }
    
    

    public int getVolume() {
    	return (Integer) this.cache.retrievePreference(Preferences.VOLUME, 50);
    }

    public void setVolume(int volume) {
    	this.cache.storePreference(Preferences.VOLUME, volume);
    }
    
    

    public boolean isMuteEnabled() {
    	return (Boolean) this.cache.retrievePreference(Preferences.MUTE, false);
    }

    public void setMuteEnabled(boolean muteEnabled) {
    	this.cache.storePreference(Preferences.MUTE, muteEnabled);
    }
    
    

    public int getAutoRepositoryRefreshTime() {
    	return (Integer) this.cache.retrievePreference(Preferences.AUTO_REPOSITORY_REFRESH_TIME, 60);
    }

    public void setAutoRepositoryRefreshTime(int autoRepositoryRefreshTime) {
    	this.cache.storePreference(Preferences.AUTO_REPOSITORY_REFRESH_TIME, autoRepositoryRefreshTime);
    }
    
    

    public boolean isSaveRepositoryAsXml() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SAVE_REPOSITORY_AS_XML, true);
    }

    public void setSaveRepositoryAsXml(boolean saveRepositoryAsXml) {
    	this.cache.storePreference(Preferences.SAVE_REPOSITORY_AS_XML, saveRepositoryAsXml);
    }
    
    

    public boolean isShowFavoritesInNavigator() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SHOW_FAVORITES_IN_NAVIGATOR, true);
    }

    public void setShowFavoritesInNavigator(boolean showFavoritesInNavigator) {
    	this.cache.storePreference(Preferences.SHOW_FAVORITES_IN_NAVIGATOR, showFavoritesInNavigator);
    }
    
    

    public boolean isUseCdErrorCorrection() {
    	return (Boolean) this.cache.retrievePreference(Preferences.USE_CD_ERROR_CORRECTION, false);
    }

    public void setUseCdErrorCorrection(boolean useCdErrorCorrection) {
    	this.cache.storePreference(Preferences.USE_CD_ERROR_CORRECTION, useCdErrorCorrection);
    }
    
    

    public boolean isUseSmartTagViewSorting() {
    	return (Boolean) this.cache.retrievePreference(Preferences.USE_SMART_TAG_VIEW_SORTING, false);
    }

    public void setUseSmartTagViewSorting(boolean useSmartTagViewSorting) {
    	this.cache.storePreference(Preferences.USE_SMART_TAG_VIEW_SORTING, useSmartTagViewSorting);
    }
    
    

    public boolean isUsePersonNamesArtistTagViewSorting() {
    	return (Boolean) this.cache.retrievePreference(Preferences.USE_PERSON_NAMES_ARTIST_TAG_SORTING, false);
    }

    public void setUsePersonNamesArtistTagViewSorting(boolean usePersonNamesArtistTagViewSorting) {
    	this.cache.storePreference(Preferences.USE_PERSON_NAMES_ARTIST_TAG_SORTING, usePersonNamesArtistTagViewSorting);
    }
    
    

    public boolean isSaveContextPicture() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SAVE_CONTEXT_PICTURE, false);
    }

    public void setSaveContextPicture(boolean saveContextPicture) {
    	this.cache.storePreference(Preferences.SAVE_CONTEXT_PICTURE, saveContextPicture);
    }
    
    

    public boolean isShowExtendedTooltip() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SHOW_EXTENDED_TOOLTIP, true);
    }

    public void setShowExtendedTooltip(boolean showExtendedTooltip) {
    	this.cache.storePreference(Preferences.SHOW_EXTENDED_TOOLTIP, showExtendedTooltip);
    }
    
    

    public int getExtendedTooltipDelay() {
    	return (Integer) this.cache.retrievePreference(Preferences.EXTENDED_TOOLTIP_DELAY, 1);
    }

    public void setExtendedTooltipDelay(int extendedTooltipDelay) {
    	this.cache.storePreference(Preferences.EXTENDED_TOOLTIP_DELAY, extendedTooltipDelay);
    }
    
    

    public boolean isLastFmEnabled() {
    	return (Boolean) this.cache.retrievePreference(Preferences.LASTFM_ENABLED, false);
    }

    public void setLastFmEnabled(boolean lastFmEnabled) {
    	this.cache.storePreference(Preferences.LASTFM_ENABLED, lastFmEnabled);
    }
    
    

    public String getLastFmUser() {
    	return (String) this.cache.retrievePreference(Preferences.LASTFM_USER, null);
    }

    public void setLastFmUser(String lastFmUser) {
    	this.cache.storePreference(Preferences.LASTFM_USER, lastFmUser);
    }
    
    

    public String getLastFmPassword() {
		return this.cache.retrievePasswordPreference(Preferences.LASTFM_PASSWORD);
    }

    public void setLastFmPassword(String lastFmPassword) {
		this.cache.storePasswordPreference(Preferences.LASTFM_PASSWORD, lastFmPassword);
    }
    
    

    public boolean isAutoLoveFavoriteSong() {
    	return (Boolean) this.cache.retrievePreference(Preferences.AUTO_LOVE_FAVORITE_SONG, false);
    }

    public void setAutoLoveFavoriteSong(boolean autoLoveFavoriteSong) {
    	this.cache.storePreference(Preferences.AUTO_LOVE_FAVORITE_SONG, autoLoveFavoriteSong);
    }
    
    

    @SuppressWarnings("unchecked")
	public List<LyricsEngineInfo> getLyricsEnginesInfo() {
    	return (List<LyricsEngineInfo>) this.cache.retrievePreference(Preferences.LYRICS_ENGINES_INFO, null);
    }

    public void setLyricsEnginesInfo(List<LyricsEngineInfo> lyricsEnginesInfo) {
    	this.cache.storePreference(Preferences.LYRICS_ENGINES_INFO, lyricsEnginesInfo);
    }
    
    

    public String getFullScreenBackground() {
    	return (String) this.cache.retrievePreference(Preferences.FULL_SCREEN_BACKGROUND, null);
    }

    public void setFullScreenBackground(String fullScreenBackground) {
    	this.cache.storePreference(Preferences.FULL_SCREEN_BACKGROUND, fullScreenBackground);
    }
    
   
    
    public String getEncoder() {
    	return (String) this.cache.retrievePreference(Preferences.ENCODER, "OGG");
    }

    public void setEncoder(String encoder) {
    	this.cache.storePreference(Preferences.ENCODER, encoder);
    }

    
    
    public String getEncoderQuality() {
    	return (String) this.cache.retrievePreference(Preferences.ENCODER_QUALITY, "5");
    }

    public void setEncoderQuality(String encoderQuality) {
    	this.cache.storePreference(Preferences.ENCODER_QUALITY, encoderQuality);
    }

    
    
    public String getMp3EncoderQuality() {
    	return (String) this.cache.retrievePreference(Preferences.MP3_ENCODER_QUALITY, "medium");
    }

    public void setMp3EncoderQuality(String mp3EncoderQuality) {
    	this.cache.storePreference(Preferences.MP3_ENCODER_QUALITY, mp3EncoderQuality);
    }
    
    

    public String getFlacEncoderQuality() {
    	return (String) this.cache.retrievePreference(Preferences.FLAC_ENCODER_QUALITY, "-5");
    }

    public void setFlacEncoderQuality(String flacEncoderQuality) {
    	this.cache.storePreference(Preferences.FLAC_ENCODER_QUALITY, flacEncoderQuality);
    }

    
    
    public String getCdRipperFileNamePattern() {
    	return (String) this.cache.retrievePreference(Preferences.CD_RIPPER_FILENAME_PATTERN, null);
    }

    public void setCdRipperFileNamePattern(String cdRipperFileNamePattern) {
    	this.cache.storePreference(Preferences.CD_RIPPER_FILENAME_PATTERN, cdRipperFileNamePattern);
    }
    
    

    @SuppressWarnings("unchecked")
	public Map<String, ColumnBean> getColumns() {
    	Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.cache.retrievePreference(Preferences.COLUMNS, null);
    	return map != null ? Collections.unmodifiableMap(map) : null;
    }

    public void setColumns(Map<String, ColumnBean> columns) {
    	this.cache.storePreference(Preferences.COLUMNS, columns);
    }
    
    

    @SuppressWarnings("unchecked")
	public Map<String, ColumnBean> getNavigatorColumns() {
    	Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.cache.retrievePreference(Preferences.NAVIGATOR_COLUMNS, null);
    	return map != null ? Collections.unmodifiableMap(map) : null;
    }

    public void setNavigatorColumns(Map<String, ColumnBean> navigatorColumns) {
    	this.cache.storePreference(Preferences.NAVIGATOR_COLUMNS, navigatorColumns);
    }

    
    
    @SuppressWarnings("unchecked")
	public Map<String, ColumnBean> getSearchResultsColumns() {
    	Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.cache.retrievePreference(Preferences.SEARCH_RESULTS_COLUMNS, null);
    	return map != null ? Collections.unmodifiableMap(map) : null;
    }

    public void setSearchResultsColumns(Map<String, ColumnBean> searchResultsColumns) {
    	this.cache.storePreference(Preferences.SEARCH_RESULTS_COLUMNS, searchResultsColumns);
    }
    
    

    public FrameState getFrameState(Class<? extends Frame> frame) {
    	// Map creation is controlled in this class to avoid modification without persistence 
    	@SuppressWarnings("unchecked")
		Map<Class<? extends Frame>, FrameState> state = (Map<Class<? extends Frame>, FrameState>) this.cache.retrievePreference(Preferences.FRAME_STATES, null);
    	if (state == null) {
    		state = new HashMap<Class<? extends Frame>, FrameState>();
    		this.cache.storePreference(Preferences.FRAME_STATES, state);
    	}
    	return state.get(frame);
    }

    public void setFrameState(Class<? extends Frame> frame, FrameState frameState) {
    	@SuppressWarnings("unchecked")
		Map<Class<? extends Frame>, FrameState> state = (Map<Class<? extends Frame>, FrameState>) this.cache.retrievePreference(Preferences.FRAME_STATES, null);
    	if (state == null) {
    		state = new HashMap<Class<? extends Frame>, FrameState>();
    	}
    	state.put(frame, frameState);
        this.cache.storePreference(Preferences.FRAME_STATES, state);
    }
    
    

    public String getDefaultDeviceLocation() {
    	return (String) this.cache.retrievePreference(Preferences.DEFAULT_DEVICE_LOCATION, null);
    }

    public void setDefaultDeviceLocation(String defaultDeviceLocation) {
    	this.cache.storePreference(Preferences.DEFAULT_DEVICE_LOCATION, defaultDeviceLocation);
    }
    
    

    public long getPodcastFeedEntriesRetrievalInterval() {
    	return (Long) this.cache.retrievePreference(Preferences.PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL, PodcastFeedHandler.DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL);        
    }

    public void setPodcastFeedEntriesRetrievalInterval(long podcastFeedEntriesRetrievalInterval) {
    	this.cache.storePreference(Preferences.PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL, podcastFeedEntriesRetrievalInterval);
    }
    
    

    public String getPodcastFeedEntryDownloadPath() {
    	return (String) this.cache.retrievePreference(Preferences.PODCAST_FEED_ENTRY_DOWNLOAD_PATH, null);
    }

    public void setPodcastFeedEntryDownloadPath(String podcastFeedEntryDownloadPath) {
    	this.cache.storePreference(Preferences.PODCAST_FEED_ENTRY_DOWNLOAD_PATH, podcastFeedEntryDownloadPath);
    }
    
    

    public boolean isUseDownloadedPodcastFeedEntries() {
    	return (Boolean) this.cache.retrievePreference(Preferences.USE_DOWNLOADED_PODCAST_FEED_ENTRIES, true);
    }

    public void setUseDownloadedPodcastFeedEntries(boolean useDownloadedPodcastFeedEntries) {
    	this.cache.storePreference(Preferences.USE_DOWNLOADED_PODCAST_FEED_ENTRIES, useDownloadedPodcastFeedEntries);
    }
    
    

    public boolean isRemovePodcastFeedEntriesRemovedFromPodcastFeed() {
    	return (Boolean) this.cache.retrievePreference(Preferences.REMOVE_PODCAST_FEED_ENTRIES_REMOVED_FROM_PODCAST_FEED, false);
    }

    public void setRemovePodcastFeedEntriesRemovedFromPodcastFeed(boolean removePodcastFeedEntriesRemovedFromPodcastFeed) {
    	this.cache.storePreference(Preferences.REMOVE_PODCAST_FEED_ENTRIES_REMOVED_FROM_PODCAST_FEED, removePodcastFeedEntriesRemovedFromPodcastFeed);
    }
    
    

    public boolean isShowNavigatorTabsAtLeft() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SHOW_NAVIGATOR_TABS_AT_LEFT, false);
    }

    public void setShowNavigatorTabsAtLeft(boolean showNavigatorTabsAtLeft) {
    	this.cache.storePreference(Preferences.SHOW_NAVIGATOR_TABS_AT_LEFT, showNavigatorTabsAtLeft);
    }
    
    

    public boolean isShowNavigatorTabsText() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SHOW_NAVIGATOR_TABS_TEXT, false);
    }

    public void setShowNavigatorTabsText(boolean showNavigatorTabsText) {
    	this.cache.storePreference(Preferences.SHOW_NAVIGATOR_TABS_TEXT, showNavigatorTabsText);
    }
    
    

    public boolean isShowContextTabsText() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SHOW_CONTEXT_TABS_TEXT, false);
    }

    public void setShowContextTabsText(boolean showContextTabsText) {
    	this.cache.storePreference(Preferences.SHOW_CONTEXT_TABS_TEXT, showContextTabsText);
    }
    
    

    public boolean isHideVariousArtistsAlbums() {
    	return (Boolean) this.cache.retrievePreference(Preferences.HIDE_VARIOUS_ARTISTS_ALBUMS, true);
    }

    public void setHideVariousArtistsAlbums(boolean hideVariousArtistsAlbums) {
    	this.cache.storePreference(Preferences.HIDE_VARIOUS_ARTISTS_ALBUMS, hideVariousArtistsAlbums);
    }
    
    

    public int getMinimumSongNumberPerAlbum() {
    	return (Integer) this.cache.retrievePreference(Preferences.MINIMUM_SONG_NUMER_PER_ALBUM, 0);
    }

    public void setMinimumSongNumberPerAlbum(int minimumSongNumberPerAlbum) {
    	this.cache.storePreference(Preferences.MINIMUM_SONG_NUMER_PER_ALBUM, minimumSongNumberPerAlbum);
    }
    
    

    public boolean isHighlightIncompleteTagElements() {
    	return (Boolean) this.cache.retrievePreference(Preferences.HIGHLIGHT_INCOMPLETE_TAG_ELEMENTS, true);
    }

    public void setHighlightIncompleteTagElements(boolean highlightIncompleteTagElements) {
    	this.cache.storePreference(Preferences.HIGHLIGHT_INCOMPLETE_TAG_ELEMENTS, highlightIncompleteTagElements);
    }
    
    

    @SuppressWarnings("unchecked")
	public List<TagAttribute> getHighlightIncompleteTagFoldersAttributes() {
    	return (List<TagAttribute> ) this.cache.retrievePreference(Preferences.HIGHLIGHT_INCOMPLETE_TAG_FOLDERS_ATTRIBUTES, IncompleteTagsChecker.getDefaultTagAttributesToHighlightFolders());
    }

    public void setHighlightIncompleteTagFoldersAttributes(List<TagAttribute> highlightIncompleteTagFoldersAttributes) {
    	this.cache.storePreference(Preferences.HIGHLIGHT_INCOMPLETE_TAG_FOLDERS_ATTRIBUTES, highlightIncompleteTagFoldersAttributes);
    }
    
    

    public boolean isShowToolBar() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SHOW_TOOL_BAR, true);
    }

    public void setShowToolBar(boolean showToolBar) {
    	this.cache.storePreference(Preferences.SHOW_TOOL_BAR, showToolBar);
    }
    
    

    public String getPlayerEngine() {
    	return (String) this.cache.retrievePreference(Preferences.PLAYER_ENGINE, PlayerHandler.DEFAULT_ENGINE);        
    }

    public void setPlayerEngine(String playerEngine) {
    	this.cache.storePreference(Preferences.PLAYER_ENGINE, playerEngine);
    }
    
    

    public boolean isAutoScrollPlayListEnabled() {
    	return (Boolean) this.cache.retrievePreference(Preferences.AUTO_SCROLL_PLAYLIST, true);
    }

    public void setAutoScrollPlayListEnabled(boolean autoScrollPlayListEnabled) {
    	this.cache.storePreference(Preferences.AUTO_SCROLL_PLAYLIST, autoScrollPlayListEnabled);
    }
    
    

    @SuppressWarnings("unchecked")
	public List<String> getLastRepositoryFolders() {
    	return (List<String>) this.cache.retrievePreference(Preferences.LAST_REPOSITORY_FOLDERS, null);
    }

    public void setLastRepositoryFolders(List<String> lastRepositoryFolders) {
    	this.cache.storePreference(Preferences.LAST_REPOSITORY_FOLDERS, lastRepositoryFolders);
    }
    
    

    public String getLoadPlaylistPath() {
    	return (String) this.cache.retrievePreference(Preferences.LOAD_PLAYLIST_PATH, null); 
    }

    public void setLoadPlaylistPath(String loadPlaylistPath) {
    	this.cache.storePreference(Preferences.LOAD_PLAYLIST_PATH, loadPlaylistPath);
    }
    
    

    public String getSavePlaylistPath() {
    	return (String) this.cache.retrievePreference(Preferences.SAVE_PLAYLIST_PATH, null);
    }

    public void setSavePlaylistPath(String savePlaylistPath) {
    	this.cache.storePreference(Preferences.SAVE_PLAYLIST_PATH, savePlaylistPath);
    }
    
    

    public boolean isStopPlayerOnPlayListSwitch() {
    	return (Boolean) this.cache.retrievePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_SWITCH, false);
    }

    public void setStopPlayerOnPlayListSwitch(boolean stopPlayerOnPlayListSwitch) {
    	this.cache.storePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_SWITCH, stopPlayerOnPlayListSwitch);
    }
    
    

    public boolean isStopPlayerOnPlayListClear() {
    	return (Boolean) this.cache.retrievePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_CLEAR, true);
    }

    public void setStopPlayerOnPlayListClear(boolean stopPlayerOnPlayListClear) {
    	this.cache.storePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_CLEAR, stopPlayerOnPlayListClear);
    }
    
    

    public String getImportExportFileNamePattern() {
    	return (String) this.cache.retrievePreference(Preferences.IMPORT_EXPORT_FILENAME_PATTERN, null);
    }

    public void setImportExportFileNamePattern(String importExportFileNamePattern) {
    	this.cache.storePreference(Preferences.IMPORT_EXPORT_FILENAME_PATTERN, importExportFileNamePattern);
    }
    
    

    public String getDeviceFileNamePattern() {
    	return (String) this.cache.retrievePreference(Preferences.DEVICE_FILENAME_PATTERN, null);
    }

    public void setDeviceFileNamePattern(String deviceFileNamePattern) {
    	this.cache.storePreference(Preferences.DEVICE_FILENAME_PATTERN, deviceFileNamePattern);
    }
    
    

    public String getImportExportFolderPathPattern() {
    	return (String) this.cache.retrievePreference(Preferences.IMPORT_EXPORT_FOLDER_PATH_PATTERN, null);
    }

    public void setImportExportFolderPathPattern(String importExportFolderPathPattern) {
    	this.cache.storePreference(Preferences.IMPORT_EXPORT_FOLDER_PATH_PATTERN, importExportFolderPathPattern);
    }
    
    

    public String getDeviceFolderPathPattern() {
    	return (String) this.cache.retrievePreference(Preferences.DEVICE_FOLDER_PATH_PATTERN, null);
    }

    public void setDeviceFolderPathPattern(String deviceFolderPathPattern) {
    	this.cache.storePreference(Preferences.DEVICE_FOLDER_PATH_PATTERN, deviceFolderPathPattern);
    }
    
    

    public boolean isAllowRepeatedSongsInDevice() {
    	return (Boolean) this.cache.retrievePreference(Preferences.ALLOW_REPEATED_SONGS_IN_DEVICE, true);
    }

    public void setAllowRepeatedSongsInDevice(boolean allowRepeatedSongsInDevice) {
    	this.cache.storePreference(Preferences.ALLOW_REPEATED_SONGS_IN_DEVICE, allowRepeatedSongsInDevice);
    }
    
    

    public boolean isReviewTagsBeforeImport() {
    	return (Boolean) this.cache.retrievePreference(Preferences.REVIEW_TAGS_BEFORE_IMPORT, true);
    }

    public void setReviewTagsBeforeImport(boolean reviewTagsBeforeImport) {
    	this.cache.storePreference(Preferences.REVIEW_TAGS_BEFORE_IMPORT, reviewTagsBeforeImport);
    }
    
    

    public boolean isApplyChangesToSourceFilesBeforeImport() {
    	return (Boolean) this.cache.retrievePreference(Preferences.APPLY_CHANGES_TO_SOURCE_FILES_BEFORE_IMPORT, false);
    }

    public void setApplyChangesToSourceFilesBeforeImport(boolean applyChangesToSourceFilesBeforeImport) {
    	this.cache.storePreference(Preferences.APPLY_CHANGES_TO_SOURCE_FILES_BEFORE_IMPORT, applyChangesToSourceFilesBeforeImport);
    }
    
    

    public boolean isSetTrackNumbersWhenImporting() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SET_TRACK_NUMBERS_WHEN_IMPORTING, true);
    }

    public void setSetTrackNumbersWhenImporting(boolean setTrackNumbersWhenImporting) {
    	this.cache.storePreference(Preferences.SET_TRACK_NUMBERS_WHEN_IMPORTING, setTrackNumbersWhenImporting);
    }
    
    

    public boolean isSetTitlesWhenImporting() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SET_TITLES_WHEN_IMPORTING, true);
    }

    public void setSetTitlesWhenImporting(boolean setTitlesWhenImporting) {
    	this.cache.storePreference(Preferences.SET_TITLES_WHEN_IMPORTING, setTitlesWhenImporting);
    }
    
    

    public int getOsdWidth() {
    	return (Integer) this.cache.retrievePreference(Preferences.OSD_WIDTH, 300);
    }

    public void setOsdWidth(int osdWidth) {
    	this.cache.storePreference(Preferences.OSD_WIDTH, osdWidth);
    }
    
    

    public int getOsdHorizontalAlignment() {
    	return (Integer) this.cache.retrievePreference(Preferences.OSD_HORIZONTAL_ALINGMENT, SwingConstants.RIGHT);        
    }

    public void setOsdHorizontalAlignment(int osdHorizontalAlignment) {
    	this.cache.storePreference(Preferences.OSD_HORIZONTAL_ALINGMENT, osdHorizontalAlignment);
    }
    
    

    public int getOsdVerticalAlignment() {
    	return (Integer) this.cache.retrievePreference(Preferences.OSD_VERTICAL_ALINGMENT, SwingConstants.BOTTOM);        
    }

    public void setOsdVerticalAlignment(int osdVerticalAlignment) {
    	this.cache.storePreference(Preferences.OSD_VERTICAL_ALINGMENT, osdVerticalAlignment);
    }
    
    

    public HotkeysConfig getHotkeysConfig() {
    	return (HotkeysConfig) this.cache.retrievePreference(Preferences.HOTKEYS_CONFIG, null);
    }

    public void setHotkeysConfig(HotkeysConfig hotkeysConfig) {
    	this.cache.storePreference(Preferences.HOTKEYS_CONFIG, hotkeysConfig);
    }
    
    

    @SuppressWarnings("unchecked")
	public List<String> getRecognitionPatterns() {
    	return (List<String>) this.cache.retrievePreference(Preferences.RECOGNITION_PATTERNS, null);
    }

    public void setRecognitionPatterns(List<String> recognitionPatterns) {
    	this.cache.storePreference(Preferences.RECOGNITION_PATTERNS, recognitionPatterns);
    }
    
    

    @SuppressWarnings("unchecked")
	public List<String> getMassiveRecognitionPatterns() {
    	return (List<String>) this.cache.retrievePreference(Preferences.MASSIVE_RECOGNITION_PATTERNS, null);
    }

    public void setMassiveRecognitionPatterns(List<String> massiveRecognitionPatterns) {
    	this.cache.storePreference(Preferences.MASSIVE_RECOGNITION_PATTERNS, massiveRecognitionPatterns);
    }
    
    

    public String getCommandBeforeAccessRepository() {
    	return (String) this.cache.retrievePreference(Preferences.COMMAND_BEFORE_ACCESS_REPOSITORY, null);
    }

    public void setCommandBeforeAccessRepository(String commandBeforeAccessRepository) {
    	this.cache.storePreference(Preferences.COMMAND_BEFORE_ACCESS_REPOSITORY, commandBeforeAccessRepository);
    }
    
    

    public String getCommandAfterAccessRepository() {
    	return (String) this.cache.retrievePreference(Preferences.COMMAND_AFTER_ACCESS_REPOSITORY, null);
    }

    public void setCommandAfterAccessRepository(String commandAfterAccessRepository) {
    	this.cache.storePreference(Preferences.COMMAND_AFTER_ACCESS_REPOSITORY, commandAfterAccessRepository);
    }
    
    

    public boolean isUseLibnotify() {
    	return (Boolean) this.cache.retrievePreference(Preferences.USE_LIBNOTIFY, false);
    }

    public void setUseLibnotify(boolean useLibnotify) {
    	this.cache.storePreference(Preferences.USE_LIBNOTIFY, useLibnotify);
    }
    
    

    public boolean isShowContextAlbumsInGrid() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SHOW_CONTEXT_ALBUMS_IN_GRID, false);
    }

    public void setShowContextAlbumsInGrid(boolean showContextAlbumsInGrid) {
    	this.cache.storePreference(Preferences.SHOW_CONTEXT_ALBUMS_IN_GRID, showContextAlbumsInGrid);
    }
    
    

    public boolean isShowNavigationTree() {
        return (Boolean) this.cache.retrievePreference(Preferences.SHOW_NAVIGATION_TREE, true);
    }

    public void setShowNavigationTree(boolean showNavigationTree) {
        this.cache.storePreference(Preferences.SHOW_NAVIGATION_TREE, showNavigationTree);
    }
    
    

    public boolean isSimilarArtistsMode() {
    	return (Boolean) this.cache.retrievePreference(Preferences.SIMILAR_ARTISTS_MODE, false);
    }

    public void setSimilarArtistsMode(boolean isSimilarArtistsMode) {
    	this.cache.storePreference(Preferences.SIMILAR_ARTISTS_MODE, isSimilarArtistsMode);
    }
    
    

    @SuppressWarnings("unchecked")
	public Map<String, Map<String, ColumnBean>> getCustomNavigatorColumns() {
    	// This map is not unmodifiable
    	return (Map<String, Map<String, ColumnBean>>) this.cache.retrievePreference(Preferences.CUSTOM_NAVIGATOR_COLUMNS, null); 
    }

    public void setCustomNavigatorColumns(Map<String, Map<String, ColumnBean>> customNavigatorColumns) {
    	this.cache.storePreference(Preferences.CUSTOM_NAVIGATOR_COLUMNS, customNavigatorColumns);
    }
}
