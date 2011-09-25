/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.gui.frame.FrameState;
import net.sourceforge.atunes.gui.views.dialogs.FontChooserDialog.FontSettings;
import net.sourceforge.atunes.kernel.modules.hotkeys.HotkeysConfig;
import net.sourceforge.atunes.kernel.modules.navigator.RepositoryNavigationView;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.state.beans.ColorBean;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.modules.tags.IncompleteTagsChecker;
import net.sourceforge.atunes.kernel.modules.tags.TagAttribute;
import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IFrameState;
import net.sourceforge.atunes.model.ILyricsEngineInfo;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStateStore;
import net.sourceforge.atunes.model.LookAndFeelBean;
import net.sourceforge.atunes.model.ViewMode;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationState implements IState {

    /**
     * Component responsible of store state
     */
    private IStateStore stateStore;
    
    /**
     * Sets state store
     * @param store
     */
    public void setStateStore(IStateStore store) {
		this.stateStore = store;
	}

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowAllRadioStations()
	 */
    @Override
	public boolean isShowAllRadioStations() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_ALL_RADIO_STATIONS, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowAllRadioStations(boolean)
	 */
    @Override
	public void setShowAllRadioStations(boolean showAllRadioStations) {
        this.stateStore.storePreference(Preferences.SHOW_ALL_RADIO_STATIONS, showAllRadioStations);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowNavigationTable()
	 */
    @Override
	public boolean isShowNavigationTable() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_NAVIGATION_TABLE, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowNavigationTable(boolean)
	 */
    @Override
	public void setShowNavigationTable(boolean showNavigationTable) {
        this.stateStore.storePreference(Preferences.SHOW_NAVIGATION_TABLE, showNavigationTable);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isKeyAlwaysCaseSensitiveInRepositoryStructure()
	 */
    @Override
	public boolean isKeyAlwaysCaseSensitiveInRepositoryStructure(){
        return (Boolean) this.stateStore.retrievePreference(Preferences.CASE_SENSITIVE_REPOSITORY_STRUCTURE_KEYS, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setKeyAlwaysCaseSensitiveInRepositoryStructure(boolean)
	 */
    @Override
	public void setKeyAlwaysCaseSensitiveInRepositoryStructure(boolean caseSensitiveRepositoryStructureKeys){
        this.stateStore.storePreference(Preferences.CASE_SENSITIVE_REPOSITORY_STRUCTURE_KEYS, caseSensitiveRepositoryStructureKeys);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowAudioObjectProperties()
	 */
    @Override
	public boolean isShowAudioObjectProperties() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_AUDIO_OBJECT_PROPERTIES, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowAudioObjectProperties(boolean)
	 */
    @Override
	public void setShowAudioObjectProperties(boolean showAudioObjectProperties) {
        this.stateStore.storePreference(Preferences.SHOW_AUDIO_OBJECT_PROPERTIES, showAudioObjectProperties);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowStatusBar()
	 */
    @Override
	public boolean isShowStatusBar() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_STATUS_BAR, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowStatusBar(boolean)
	 */
    @Override
	public void setShowStatusBar(boolean showStatusBar) {
        this.stateStore.storePreference(Preferences.SHOW_STATUS_BAR, showStatusBar);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowOSD()
	 */
    @Override
	public boolean isShowOSD() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_OSD, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowOSD(boolean)
	 */
    @Override
	public void setShowOSD(boolean showOSD) {
    	this.stateStore.storePreference(Preferences.SHOW_OSD, showOSD);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShuffle()
	 */
    @Override
	public boolean isShuffle() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHUFFLE, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShuffle(boolean)
	 */
    @Override
	public void setShuffle(boolean shuffle) {
        this.stateStore.storePreference(Preferences.SHUFFLE, shuffle);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isRepeat()
	 */
    @Override
	public boolean isRepeat() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.REPEAT, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setRepeat(boolean)
	 */
    @Override
	public void setRepeat(boolean repeat) {
        this.stateStore.storePreference(Preferences.REPEAT, repeat);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowSystemTray()
	 */
    @Override
	public boolean isShowSystemTray() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_SYSTEM_TRAY, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowSystemTray(boolean)
	 */
    @Override
	public void setShowSystemTray(boolean showSystemTray) {
    	this.stateStore.storePreference(Preferences.SHOW_SYSTEM_TRAY, showSystemTray);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowTrayPlayer()
	 */
    @Override
	public boolean isShowTrayPlayer() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_TRAY_PLAYER, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowTrayPlayer(boolean)
	 */
    @Override
	public void setShowTrayPlayer(boolean showTrayPlayer) {
    	this.stateStore.storePreference(Preferences.SHOW_TRAY_PLAYER, showTrayPlayer);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getNavigationView()
	 */
    @Override
	public String getNavigationView() {
        return (String) this.stateStore.retrievePreference(Preferences.NAVIGATION_VIEW, RepositoryNavigationView.class.getName());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setNavigationView(java.lang.String)
	 */
    @Override
	public void setNavigationView(String navigationView) {
        this.stateStore.storePreference(Preferences.NAVIGATION_VIEW, navigationView);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getViewMode()
	 */
    @Override
	public ViewMode getViewMode() {
        return (ViewMode) this.stateStore.retrievePreference(Preferences.VIEW_MODE, ViewMode.ARTIST);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setViewMode(net.sourceforge.atunes.model.ViewMode)
	 */
    @Override
	public void setViewMode(ViewMode viewMode) {
        this.stateStore.storePreference(Preferences.VIEW_MODE, viewMode);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getLocale()
	 */
    @Override
	public LocaleBean getLocale() {
    	return (LocaleBean) this.stateStore.retrievePreference(Preferences.LOCALE, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLocale(net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean)
	 */
    @Override
	public void setLocale(LocaleBean locale) {
        this.stateStore.storePreference(Preferences.LOCALE, locale);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getOldLocale()
	 */
    @Override
	public LocaleBean getOldLocale() {
        return (LocaleBean) this.stateStore.retrievePreference(Preferences.OLD_LOCALE, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setOldLocale(net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean)
	 */
    @Override
	public void setOldLocale(LocaleBean oldLocale) {
        this.stateStore.storePreference(Preferences.OLD_LOCALE, oldLocale);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getDefaultSearch()
	 */
    @Override
	public String getDefaultSearch() {
        return (String) this.stateStore.retrievePreference(Preferences.DEFAULT_SEARCH, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setDefaultSearch(java.lang.String)
	 */
    @Override
	public void setDefaultSearch(String defaultSearch) {
    	this.stateStore.storePreference(Preferences.DEFAULT_SEARCH, defaultSearch);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUseContext()
	 */
    @Override
	public boolean isUseContext() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.USE_CONTEXT, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUseContext(boolean)
	 */
    @Override
	public void setUseContext(boolean useContext) {
    	if (isUseContext() != useContext) {
    		this.stateStore.storePreference(Preferences.USE_CONTEXT, useContext);
    	}
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getSelectedContextTab()
	 */
    @Override
	public String getSelectedContextTab() {
        return (String) this.stateStore.retrievePreference(Preferences.SELECTED_CONTEXT_TAB, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setSelectedContextTab(java.lang.String)
	 */
    @Override
	public void setSelectedContextTab(String selectedContextTab) {
    	if (getSelectedContextTab() == null || !getSelectedContextTab().equals(selectedContextTab)) {
    		this.stateStore.storePreference(Preferences.SELECTED_CONTEXT_TAB, selectedContextTab);
    	}
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getFrameClass()
	 */
    @Override
	@SuppressWarnings("unchecked")
	public Class<? extends IFrame> getFrameClass() {
        return (Class<? extends IFrame>) this.stateStore.retrievePreference(Preferences.FRAME_CLASS, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setFrameClass(java.lang.Class)
	 */
    @Override
	public void setFrameClass(Class<? extends IFrame> frameClass) {
    	this.stateStore.storePreference(Preferences.FRAME_CLASS, frameClass);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getProxy()
	 */
    @Override
	public ProxyBean getProxy() {
        return (ProxyBean) this.stateStore.retrievePreference(Preferences.PROXY, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setProxy(net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean)
	 */
    @Override
	public void setProxy(ProxyBean proxy) {
        this.stateStore.storePreference(Preferences.PROXY, proxy);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getLookAndFeel()
	 */
    @Override
	public LookAndFeelBean getLookAndFeel() {
        return (LookAndFeelBean) this.stateStore.retrievePreference(Preferences.LOOK_AND_FEEL, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLookAndFeel(net.sourceforge.atunes.gui.lookandfeel.LookAndFeelBean)
	 */
    @Override
	public void setLookAndFeel(LookAndFeelBean lookAndFeel) {
    	this.stateStore.storePreference(Preferences.LOOK_AND_FEEL, lookAndFeel);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getFontSettings()
	 */
    @Override
	public FontSettings getFontSettings() {
        return (FontSettings) this.stateStore.retrievePreference(Preferences.FONT_SETTINGS, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setFontSettings(net.sourceforge.atunes.gui.views.dialogs.FontChooserDialog.FontSettings)
	 */
    @Override
	public void setFontSettings(FontSettings fontSettings) {
    	this.stateStore.storePreference(Preferences.FONT_SETTINGS, fontSettings);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isPlayAtStartup()
	 */
    @Override
	public boolean isPlayAtStartup() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.PLAY_AT_STARTUP, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setPlayAtStartup(boolean)
	 */
    @Override
	public void setPlayAtStartup(boolean playAtStartup) {
    	this.stateStore.storePreference(Preferences.PLAY_AT_STARTUP, playAtStartup);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isCacheFilesBeforePlaying()
	 */
    @Override
	public boolean isCacheFilesBeforePlaying() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.CACHE_FILES_BEFORE_PLAYING, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setCacheFilesBeforePlaying(boolean)
	 */
    @Override
	public void setCacheFilesBeforePlaying(boolean cacheFilesBeforePlaying) {
    	this.stateStore.storePreference(Preferences.CACHE_FILES_BEFORE_PLAYING, cacheFilesBeforePlaying);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUseNormalisation()
	 */
    @Override
	public boolean isUseNormalisation() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_NORMALIZATION, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUseNormalisation(boolean)
	 */
    @Override
	public void setUseNormalisation(boolean useNormalisation) {
    	this.stateStore.storePreference(Preferences.USE_NORMALIZATION, useNormalisation);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUseShortPathNames()
	 */
    @Override
	public boolean isUseShortPathNames() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_SHORT_PATH_NAMES, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUseShortPathNames(boolean)
	 */
    @Override
	public void setUseShortPathNames(boolean useShortPathNames) {
    	this.stateStore.storePreference(Preferences.USE_SHORT_PATH_NAMES, useShortPathNames);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getEqualizerSettings()
	 */
    @Override
	public float[] getEqualizerSettings() {
    	return (float[]) this.stateStore.retrievePreference(Preferences.EQUALIZER_SETTINGS, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setEqualizerSettings(float[])
	 */
    @Override
	public void setEqualizerSettings(float[] equalizerSettings) {
    	this.stateStore.storePreference(Preferences.EQUALIZER_SETTINGS, equalizerSettings);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUseFadeAway()
	 */
    @Override
	public boolean isUseFadeAway() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_FADE_AWAY, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUseFadeAway(boolean)
	 */
    @Override
	public void setUseFadeAway(boolean useFadeAway) {
    	this.stateStore.storePreference(Preferences.USE_FADE_AWAY, useFadeAway);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowTicks()
	 */
    @Override
	public boolean isShowTicks() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_TICKS, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowTicks(boolean)
	 */
    @Override
	public void setShowTicks(boolean showTicks) {
    	this.stateStore.storePreference(Preferences.SHOW_TICKS, showTicks);
    }
    
    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowAdvancedPlayerControls()
	 */
    @Override
	public boolean isShowAdvancedPlayerControls() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_ADVANCED_PLAYER_CONTROLS, false);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowAdvancedPlayerControls(boolean)
	 */
    @Override
	public void setShowAdvancedPlayerControls(boolean show) {
    	this.stateStore.storePreference(Preferences.SHOW_ADVANCED_PLAYER_CONTROLS, show);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isReadInfoFromRadioStream()
	 */
    @Override
	public boolean isReadInfoFromRadioStream() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.READ_INFO_FROM_RADIO_STREAM, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setReadInfoFromRadioStream(boolean)
	 */
    @Override
	public void setReadInfoFromRadioStream(boolean readInfoFromRadioStream) {
    	this.stateStore.storePreference(Preferences.READ_INFO_FROM_RADIO_STREAM, readInfoFromRadioStream);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isEnableAdvancedSearch()
	 */
    @Override
	public boolean isEnableAdvancedSearch() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.ENABLE_ADVANCED_SEARCH, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setEnableAdvancedSearch(boolean)
	 */
    @Override
	public void setEnableAdvancedSearch(boolean enableAdvancedSearch) {
    	this.stateStore.storePreference(Preferences.ENABLE_ADVANCED_SEARCH, enableAdvancedSearch);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isEnableHotkeys()
	 */
    @Override
	public boolean isEnableHotkeys() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.ENABLE_HOTKEYS, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setEnableHotkeys(boolean)
	 */
    @Override
	public void setEnableHotkeys(boolean enableHotkeys) {
    	this.stateStore.storePreference(Preferences.ENABLE_HOTKEYS, enableHotkeys);
    }
    
        

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getOsdDuration()
	 */
    @Override
	public int getOsdDuration() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.OSD_DURATION, 2);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setOsdDuration(int)
	 */
    @Override
	public void setOsdDuration(int osdDuration) {
    	this.stateStore.storePreference(Preferences.OSD_DURATION, osdDuration);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getVolume()
	 */
    @Override
	public int getVolume() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.VOLUME, 50);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setVolume(int)
	 */
    @Override
	public void setVolume(int volume) {
    	this.stateStore.storePreference(Preferences.VOLUME, volume);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isMuteEnabled()
	 */
    @Override
	public boolean isMuteEnabled() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.MUTE, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setMuteEnabled(boolean)
	 */
    @Override
	public void setMuteEnabled(boolean muteEnabled) {
    	this.stateStore.storePreference(Preferences.MUTE, muteEnabled);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getAutoRepositoryRefreshTime()
	 */
    @Override
	public int getAutoRepositoryRefreshTime() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.AUTO_REPOSITORY_REFRESH_TIME, 60);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setAutoRepositoryRefreshTime(int)
	 */
    @Override
	public void setAutoRepositoryRefreshTime(int autoRepositoryRefreshTime) {
    	this.stateStore.storePreference(Preferences.AUTO_REPOSITORY_REFRESH_TIME, autoRepositoryRefreshTime);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isSaveRepositoryAsXml()
	 */
    @Override
	public boolean isSaveRepositoryAsXml() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SAVE_REPOSITORY_AS_XML, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setSaveRepositoryAsXml(boolean)
	 */
    @Override
	public void setSaveRepositoryAsXml(boolean saveRepositoryAsXml) {
    	this.stateStore.storePreference(Preferences.SAVE_REPOSITORY_AS_XML, saveRepositoryAsXml);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowFavoritesInNavigator()
	 */
    @Override
	public boolean isShowFavoritesInNavigator() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_FAVORITES_IN_NAVIGATOR, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowFavoritesInNavigator(boolean)
	 */
    @Override
	public void setShowFavoritesInNavigator(boolean showFavoritesInNavigator) {
    	this.stateStore.storePreference(Preferences.SHOW_FAVORITES_IN_NAVIGATOR, showFavoritesInNavigator);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUseCdErrorCorrection()
	 */
    @Override
	public boolean isUseCdErrorCorrection() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_CD_ERROR_CORRECTION, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUseCdErrorCorrection(boolean)
	 */
    @Override
	public void setUseCdErrorCorrection(boolean useCdErrorCorrection) {
    	this.stateStore.storePreference(Preferences.USE_CD_ERROR_CORRECTION, useCdErrorCorrection);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUseSmartTagViewSorting()
	 */
    @Override
	public boolean isUseSmartTagViewSorting() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_SMART_TAG_VIEW_SORTING, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUseSmartTagViewSorting(boolean)
	 */
    @Override
	public void setUseSmartTagViewSorting(boolean useSmartTagViewSorting) {
    	this.stateStore.storePreference(Preferences.USE_SMART_TAG_VIEW_SORTING, useSmartTagViewSorting);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUsePersonNamesArtistTagViewSorting()
	 */
    @Override
	public boolean isUsePersonNamesArtistTagViewSorting() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_PERSON_NAMES_ARTIST_TAG_SORTING, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUsePersonNamesArtistTagViewSorting(boolean)
	 */
    @Override
	public void setUsePersonNamesArtistTagViewSorting(boolean usePersonNamesArtistTagViewSorting) {
    	this.stateStore.storePreference(Preferences.USE_PERSON_NAMES_ARTIST_TAG_SORTING, usePersonNamesArtistTagViewSorting);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isSaveContextPicture()
	 */
    @Override
	public boolean isSaveContextPicture() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SAVE_CONTEXT_PICTURE, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setSaveContextPicture(boolean)
	 */
    @Override
	public void setSaveContextPicture(boolean saveContextPicture) {
    	this.stateStore.storePreference(Preferences.SAVE_CONTEXT_PICTURE, saveContextPicture);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowExtendedTooltip()
	 */
    @Override
	public boolean isShowExtendedTooltip() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_EXTENDED_TOOLTIP, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowExtendedTooltip(boolean)
	 */
    @Override
	public void setShowExtendedTooltip(boolean showExtendedTooltip) {
    	this.stateStore.storePreference(Preferences.SHOW_EXTENDED_TOOLTIP, showExtendedTooltip);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getExtendedTooltipDelay()
	 */
    @Override
	public int getExtendedTooltipDelay() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.EXTENDED_TOOLTIP_DELAY, 1);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setExtendedTooltipDelay(int)
	 */
    @Override
	public void setExtendedTooltipDelay(int extendedTooltipDelay) {
    	this.stateStore.storePreference(Preferences.EXTENDED_TOOLTIP_DELAY, extendedTooltipDelay);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isLastFmEnabled()
	 */
    @Override
	public boolean isLastFmEnabled() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.LASTFM_ENABLED, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLastFmEnabled(boolean)
	 */
    @Override
	public void setLastFmEnabled(boolean lastFmEnabled) {
    	this.stateStore.storePreference(Preferences.LASTFM_ENABLED, lastFmEnabled);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getLastFmUser()
	 */
    @Override
	public String getLastFmUser() {
    	return (String) this.stateStore.retrievePreference(Preferences.LASTFM_USER, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLastFmUser(java.lang.String)
	 */
    @Override
	public void setLastFmUser(String lastFmUser) {
    	this.stateStore.storePreference(Preferences.LASTFM_USER, lastFmUser);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getLastFmPassword()
	 */
    @Override
	public String getLastFmPassword() {
		return this.stateStore.retrievePasswordPreference(Preferences.LASTFM_PASSWORD);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLastFmPassword(java.lang.String)
	 */
    @Override
	public void setLastFmPassword(String lastFmPassword) {
		this.stateStore.storePasswordPreference(Preferences.LASTFM_PASSWORD, lastFmPassword);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isAutoLoveFavoriteSong()
	 */
    @Override
	public boolean isAutoLoveFavoriteSong() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.AUTO_LOVE_FAVORITE_SONG, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setAutoLoveFavoriteSong(boolean)
	 */
    @Override
	public void setAutoLoveFavoriteSong(boolean autoLoveFavoriteSong) {
    	this.stateStore.storePreference(Preferences.AUTO_LOVE_FAVORITE_SONG, autoLoveFavoriteSong);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getLyricsEnginesInfo()
	 */
    @Override
	@SuppressWarnings("unchecked")
	public List<ILyricsEngineInfo> getLyricsEnginesInfo() {
    	return (List<ILyricsEngineInfo>) this.stateStore.retrievePreference(Preferences.LYRICS_ENGINES_INFO, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLyricsEnginesInfo(java.util.List)
	 */
    @Override
	public void setLyricsEnginesInfo(List<ILyricsEngineInfo> lyricsEnginesInfo) {
    	this.stateStore.storePreference(Preferences.LYRICS_ENGINES_INFO, lyricsEnginesInfo);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getFullScreenBackground()
	 */
    @Override
	public String getFullScreenBackground() {
    	return (String) this.stateStore.retrievePreference(Preferences.FULL_SCREEN_BACKGROUND, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setFullScreenBackground(java.lang.String)
	 */
    @Override
	public void setFullScreenBackground(String fullScreenBackground) {
    	this.stateStore.storePreference(Preferences.FULL_SCREEN_BACKGROUND, fullScreenBackground);
    }
    
   
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getEncoder()
	 */
    @Override
	public String getEncoder() {
    	return (String) this.stateStore.retrievePreference(Preferences.ENCODER, "OGG");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setEncoder(java.lang.String)
	 */
    @Override
	public void setEncoder(String encoder) {
    	this.stateStore.storePreference(Preferences.ENCODER, encoder);
    }

    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getEncoderQuality()
	 */
    @Override
	public String getEncoderQuality() {
    	return (String) this.stateStore.retrievePreference(Preferences.ENCODER_QUALITY, "5");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setEncoderQuality(java.lang.String)
	 */
    @Override
	public void setEncoderQuality(String encoderQuality) {
    	this.stateStore.storePreference(Preferences.ENCODER_QUALITY, encoderQuality);
    }

    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getMp3EncoderQuality()
	 */
    @Override
	public String getMp3EncoderQuality() {
    	return (String) this.stateStore.retrievePreference(Preferences.MP3_ENCODER_QUALITY, "medium");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setMp3EncoderQuality(java.lang.String)
	 */
    @Override
	public void setMp3EncoderQuality(String mp3EncoderQuality) {
    	this.stateStore.storePreference(Preferences.MP3_ENCODER_QUALITY, mp3EncoderQuality);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getFlacEncoderQuality()
	 */
    @Override
	public String getFlacEncoderQuality() {
    	return (String) this.stateStore.retrievePreference(Preferences.FLAC_ENCODER_QUALITY, "-5");
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setFlacEncoderQuality(java.lang.String)
	 */
    @Override
	public void setFlacEncoderQuality(String flacEncoderQuality) {
    	this.stateStore.storePreference(Preferences.FLAC_ENCODER_QUALITY, flacEncoderQuality);
    }

    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getCdRipperFileNamePattern()
	 */
    @Override
	public String getCdRipperFileNamePattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.CD_RIPPER_FILENAME_PATTERN, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setCdRipperFileNamePattern(java.lang.String)
	 */
    @Override
	public void setCdRipperFileNamePattern(String cdRipperFileNamePattern) {
    	this.stateStore.storePreference(Preferences.CD_RIPPER_FILENAME_PATTERN, cdRipperFileNamePattern);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getColumns()
	 */
    @Override
	@SuppressWarnings("unchecked")
	public Map<String, ColumnBean> getColumns() {
    	Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.stateStore.retrievePreference(Preferences.COLUMNS, null);
    	return map != null ? Collections.unmodifiableMap(map) : null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setColumns(java.util.Map)
	 */
    @Override
	public void setColumns(Map<String, ColumnBean> columns) {
    	if (getColumns() == null || !getColumns().equals(columns)) {
    		this.stateStore.storePreference(Preferences.COLUMNS, new HashMap<String, ColumnBean>(columns));
    	}
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getNavigatorColumns()
	 */
    @Override
	@SuppressWarnings("unchecked")
	public Map<String, ColumnBean> getNavigatorColumns() {
    	Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.stateStore.retrievePreference(Preferences.NAVIGATOR_COLUMNS, null);
    	return map != null ? Collections.unmodifiableMap(map) : null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setNavigatorColumns(java.util.Map)
	 */
    @Override
	public void setNavigatorColumns(Map<String, ColumnBean> navigatorColumns) {
    	if (getNavigatorColumns() == null || !getNavigatorColumns().equals(navigatorColumns)) {
    		this.stateStore.storePreference(Preferences.NAVIGATOR_COLUMNS, navigatorColumns);
    	}
    }

    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getSearchResultsColumns()
	 */
    @Override
	@SuppressWarnings("unchecked")
	public Map<String, ColumnBean> getSearchResultsColumns() {
    	Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.stateStore.retrievePreference(Preferences.SEARCH_RESULTS_COLUMNS, null);
    	return map != null ? Collections.unmodifiableMap(map) : null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setSearchResultsColumns(java.util.Map)
	 */
    @Override
	public void setSearchResultsColumns(Map<String, ColumnBean> searchResultsColumns) {
    	if (getSearchResultsColumns() == null || !getSearchResultsColumns().equals(searchResultsColumns)) {
    		this.stateStore.storePreference(Preferences.SEARCH_RESULTS_COLUMNS, searchResultsColumns);
    	}
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getFrameState(java.lang.Class)
	 */
    @Override
	public FrameState getFrameState(Class<? extends IFrame> frame) {
    	// Map creation is controlled in this class to avoid modification without persistence 
    	@SuppressWarnings("unchecked")
		Map<Class<? extends IFrame>, FrameState> state = (Map<Class<? extends IFrame>, FrameState>) this.stateStore.retrievePreference(Preferences.FRAME_STATES, null);
    	if (state == null) {
    		state = new HashMap<Class<? extends IFrame>, FrameState>();
    		this.stateStore.storePreference(Preferences.FRAME_STATES, state);
    	}
    	// Clone object to be sure changes made by application to frame state are not made over object in cache
    	return state.containsKey(frame) ? new FrameState(state.get(frame)) : null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setFrameState(java.lang.Class, net.sourceforge.atunes.gui.frame.FrameState)
	 */
    @Override
	public void setFrameState(Class<? extends IFrame> frame, IFrameState fs) {
    	// Clone object to be sure changes made by application to frame state are not made over object in cache
    	FrameState frameState = new FrameState(fs);
    	if (getFrameState(frame) == null || !getFrameState(frame).equals(frameState)) {
        	@SuppressWarnings("unchecked")
    		Map<Class<? extends IFrame>, FrameState> state = (Map<Class<? extends IFrame>, FrameState>) this.stateStore.retrievePreference(Preferences.FRAME_STATES, null);
    		if (state == null) {
    			state = new HashMap<Class<? extends IFrame>, FrameState>();
    		}
    		state.put(frame, frameState);
    		this.stateStore.storePreference(Preferences.FRAME_STATES, state);
    	}
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getDefaultDeviceLocation()
	 */
    @Override
	public String getDefaultDeviceLocation() {
    	return (String) this.stateStore.retrievePreference(Preferences.DEFAULT_DEVICE_LOCATION, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setDefaultDeviceLocation(java.lang.String)
	 */
    @Override
	public void setDefaultDeviceLocation(String defaultDeviceLocation) {
    	this.stateStore.storePreference(Preferences.DEFAULT_DEVICE_LOCATION, defaultDeviceLocation);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getPodcastFeedEntriesRetrievalInterval()
	 */
    @Override
	public long getPodcastFeedEntriesRetrievalInterval() {
    	return (Long) this.stateStore.retrievePreference(Preferences.PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL, PodcastFeedHandler.DEFAULT_PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL);        
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setPodcastFeedEntriesRetrievalInterval(long)
	 */
    @Override
	public void setPodcastFeedEntriesRetrievalInterval(long podcastFeedEntriesRetrievalInterval) {
    	this.stateStore.storePreference(Preferences.PODCAST_FEED_ENTRIES_RETRIEVAL_INTERVAL, podcastFeedEntriesRetrievalInterval);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getPodcastFeedEntryDownloadPath()
	 */
    @Override
	public String getPodcastFeedEntryDownloadPath() {
    	return (String) this.stateStore.retrievePreference(Preferences.PODCAST_FEED_ENTRY_DOWNLOAD_PATH, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setPodcastFeedEntryDownloadPath(java.lang.String)
	 */
    @Override
	public void setPodcastFeedEntryDownloadPath(String podcastFeedEntryDownloadPath) {
    	this.stateStore.storePreference(Preferences.PODCAST_FEED_ENTRY_DOWNLOAD_PATH, podcastFeedEntryDownloadPath);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isUseDownloadedPodcastFeedEntries()
	 */
    @Override
	public boolean isUseDownloadedPodcastFeedEntries() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.USE_DOWNLOADED_PODCAST_FEED_ENTRIES, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setUseDownloadedPodcastFeedEntries(boolean)
	 */
    @Override
	public void setUseDownloadedPodcastFeedEntries(boolean useDownloadedPodcastFeedEntries) {
    	this.stateStore.storePreference(Preferences.USE_DOWNLOADED_PODCAST_FEED_ENTRIES, useDownloadedPodcastFeedEntries);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isRemovePodcastFeedEntriesRemovedFromPodcastFeed()
	 */
    @Override
	public boolean isRemovePodcastFeedEntriesRemovedFromPodcastFeed() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.REMOVE_PODCAST_FEED_ENTRIES_REMOVED_FROM_PODCAST_FEED, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setRemovePodcastFeedEntriesRemovedFromPodcastFeed(boolean)
	 */
    @Override
	public void setRemovePodcastFeedEntriesRemovedFromPodcastFeed(boolean removePodcastFeedEntriesRemovedFromPodcastFeed) {
    	this.stateStore.storePreference(Preferences.REMOVE_PODCAST_FEED_ENTRIES_REMOVED_FROM_PODCAST_FEED, removePodcastFeedEntriesRemovedFromPodcastFeed);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isHideVariousArtistsAlbums()
	 */
    @Override
	public boolean isHideVariousArtistsAlbums() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.HIDE_VARIOUS_ARTISTS_ALBUMS, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setHideVariousArtistsAlbums(boolean)
	 */
    @Override
	public void setHideVariousArtistsAlbums(boolean hideVariousArtistsAlbums) {
    	this.stateStore.storePreference(Preferences.HIDE_VARIOUS_ARTISTS_ALBUMS, hideVariousArtistsAlbums);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getMinimumSongNumberPerAlbum()
	 */
    @Override
	public int getMinimumSongNumberPerAlbum() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.MINIMUM_SONG_NUMER_PER_ALBUM, 0);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setMinimumSongNumberPerAlbum(int)
	 */
    @Override
	public void setMinimumSongNumberPerAlbum(int minimumSongNumberPerAlbum) {
    	this.stateStore.storePreference(Preferences.MINIMUM_SONG_NUMER_PER_ALBUM, minimumSongNumberPerAlbum);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isHighlightIncompleteTagElements()
	 */
    @Override
	public boolean isHighlightIncompleteTagElements() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.HIGHLIGHT_INCOMPLETE_TAG_ELEMENTS, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setHighlightIncompleteTagElements(boolean)
	 */
    @Override
	public void setHighlightIncompleteTagElements(boolean highlightIncompleteTagElements) {
    	this.stateStore.storePreference(Preferences.HIGHLIGHT_INCOMPLETE_TAG_ELEMENTS, highlightIncompleteTagElements);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getHighlightIncompleteTagFoldersAttributes()
	 */
    @Override
	@SuppressWarnings("unchecked")
	public List<TagAttribute> getHighlightIncompleteTagFoldersAttributes() {
    	return (List<TagAttribute> ) this.stateStore.retrievePreference(Preferences.HIGHLIGHT_INCOMPLETE_TAG_FOLDERS_ATTRIBUTES, IncompleteTagsChecker.getDefaultTagAttributesToHighlightFolders());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setHighlightIncompleteTagFoldersAttributes(java.util.List)
	 */
    @Override
	public void setHighlightIncompleteTagFoldersAttributes(List<TagAttribute> highlightIncompleteTagFoldersAttributes) {
    	this.stateStore.storePreference(Preferences.HIGHLIGHT_INCOMPLETE_TAG_FOLDERS_ATTRIBUTES, highlightIncompleteTagFoldersAttributes);
    }
    
       

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getPlayerEngine()
	 */
    @Override
	public String getPlayerEngine() {
    	return (String) this.stateStore.retrievePreference(Preferences.PLAYER_ENGINE, PlayerHandler.DEFAULT_ENGINE);        
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setPlayerEngine(java.lang.String)
	 */
    @Override
	public void setPlayerEngine(String playerEngine) {
    	this.stateStore.storePreference(Preferences.PLAYER_ENGINE, playerEngine);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isAutoScrollPlayListEnabled()
	 */
    @Override
	public boolean isAutoScrollPlayListEnabled() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.AUTO_SCROLL_PLAYLIST, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setAutoScrollPlayListEnabled(boolean)
	 */
    @Override
	public void setAutoScrollPlayListEnabled(boolean autoScrollPlayListEnabled) {
    	this.stateStore.storePreference(Preferences.AUTO_SCROLL_PLAYLIST, autoScrollPlayListEnabled);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getLastRepositoryFolders()
	 */
    @Override
	@SuppressWarnings("unchecked")
	public List<String> getLastRepositoryFolders() {
    	return (List<String>) this.stateStore.retrievePreference(Preferences.LAST_REPOSITORY_FOLDERS, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLastRepositoryFolders(java.util.List)
	 */
    @Override
	public void setLastRepositoryFolders(List<String> lastRepositoryFolders) {
    	this.stateStore.storePreference(Preferences.LAST_REPOSITORY_FOLDERS, lastRepositoryFolders);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getLoadPlaylistPath()
	 */
    @Override
	public String getLoadPlaylistPath() {
    	return (String) this.stateStore.retrievePreference(Preferences.LOAD_PLAYLIST_PATH, null); 
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setLoadPlaylistPath(java.lang.String)
	 */
    @Override
	public void setLoadPlaylistPath(String loadPlaylistPath) {
    	this.stateStore.storePreference(Preferences.LOAD_PLAYLIST_PATH, loadPlaylistPath);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getSavePlaylistPath()
	 */
    @Override
	public String getSavePlaylistPath() {
    	return (String) this.stateStore.retrievePreference(Preferences.SAVE_PLAYLIST_PATH, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setSavePlaylistPath(java.lang.String)
	 */
    @Override
	public void setSavePlaylistPath(String savePlaylistPath) {
    	this.stateStore.storePreference(Preferences.SAVE_PLAYLIST_PATH, savePlaylistPath);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isStopPlayerOnPlayListSwitch()
	 */
    @Override
	public boolean isStopPlayerOnPlayListSwitch() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_SWITCH, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setStopPlayerOnPlayListSwitch(boolean)
	 */
    @Override
	public void setStopPlayerOnPlayListSwitch(boolean stopPlayerOnPlayListSwitch) {
    	this.stateStore.storePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_SWITCH, stopPlayerOnPlayListSwitch);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isStopPlayerOnPlayListClear()
	 */
    @Override
	public boolean isStopPlayerOnPlayListClear() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_CLEAR, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setStopPlayerOnPlayListClear(boolean)
	 */
    @Override
	public void setStopPlayerOnPlayListClear(boolean stopPlayerOnPlayListClear) {
    	this.stateStore.storePreference(Preferences.STOP_PLAYER_ON_PLAYLIST_CLEAR, stopPlayerOnPlayListClear);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getImportExportFileNamePattern()
	 */
    @Override
	public String getImportExportFileNamePattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.IMPORT_EXPORT_FILENAME_PATTERN, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setImportExportFileNamePattern(java.lang.String)
	 */
    @Override
	public void setImportExportFileNamePattern(String importExportFileNamePattern) {
    	this.stateStore.storePreference(Preferences.IMPORT_EXPORT_FILENAME_PATTERN, importExportFileNamePattern);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getDeviceFileNamePattern()
	 */
    @Override
	public String getDeviceFileNamePattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.DEVICE_FILENAME_PATTERN, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setDeviceFileNamePattern(java.lang.String)
	 */
    @Override
	public void setDeviceFileNamePattern(String deviceFileNamePattern) {
    	this.stateStore.storePreference(Preferences.DEVICE_FILENAME_PATTERN, deviceFileNamePattern);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getImportExportFolderPathPattern()
	 */
    @Override
	public String getImportExportFolderPathPattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.IMPORT_EXPORT_FOLDER_PATH_PATTERN, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setImportExportFolderPathPattern(java.lang.String)
	 */
    @Override
	public void setImportExportFolderPathPattern(String importExportFolderPathPattern) {
    	this.stateStore.storePreference(Preferences.IMPORT_EXPORT_FOLDER_PATH_PATTERN, importExportFolderPathPattern);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getDeviceFolderPathPattern()
	 */
    @Override
	public String getDeviceFolderPathPattern() {
    	return (String) this.stateStore.retrievePreference(Preferences.DEVICE_FOLDER_PATH_PATTERN, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setDeviceFolderPathPattern(java.lang.String)
	 */
    @Override
	public void setDeviceFolderPathPattern(String deviceFolderPathPattern) {
    	this.stateStore.storePreference(Preferences.DEVICE_FOLDER_PATH_PATTERN, deviceFolderPathPattern);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isAllowRepeatedSongsInDevice()
	 */
    @Override
	public boolean isAllowRepeatedSongsInDevice() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.ALLOW_REPEATED_SONGS_IN_DEVICE, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setAllowRepeatedSongsInDevice(boolean)
	 */
    @Override
	public void setAllowRepeatedSongsInDevice(boolean allowRepeatedSongsInDevice) {
    	this.stateStore.storePreference(Preferences.ALLOW_REPEATED_SONGS_IN_DEVICE, allowRepeatedSongsInDevice);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isReviewTagsBeforeImport()
	 */
    @Override
	public boolean isReviewTagsBeforeImport() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.REVIEW_TAGS_BEFORE_IMPORT, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setReviewTagsBeforeImport(boolean)
	 */
    @Override
	public void setReviewTagsBeforeImport(boolean reviewTagsBeforeImport) {
    	this.stateStore.storePreference(Preferences.REVIEW_TAGS_BEFORE_IMPORT, reviewTagsBeforeImport);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isApplyChangesToSourceFilesBeforeImport()
	 */
    @Override
	public boolean isApplyChangesToSourceFilesBeforeImport() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.APPLY_CHANGES_TO_SOURCE_FILES_BEFORE_IMPORT, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setApplyChangesToSourceFilesBeforeImport(boolean)
	 */
    @Override
	public void setApplyChangesToSourceFilesBeforeImport(boolean applyChangesToSourceFilesBeforeImport) {
    	this.stateStore.storePreference(Preferences.APPLY_CHANGES_TO_SOURCE_FILES_BEFORE_IMPORT, applyChangesToSourceFilesBeforeImport);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isSetTrackNumbersWhenImporting()
	 */
    @Override
	public boolean isSetTrackNumbersWhenImporting() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SET_TRACK_NUMBERS_WHEN_IMPORTING, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setSetTrackNumbersWhenImporting(boolean)
	 */
    @Override
	public void setSetTrackNumbersWhenImporting(boolean setTrackNumbersWhenImporting) {
    	this.stateStore.storePreference(Preferences.SET_TRACK_NUMBERS_WHEN_IMPORTING, setTrackNumbersWhenImporting);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isSetTitlesWhenImporting()
	 */
    @Override
	public boolean isSetTitlesWhenImporting() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SET_TITLES_WHEN_IMPORTING, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setSetTitlesWhenImporting(boolean)
	 */
    @Override
	public void setSetTitlesWhenImporting(boolean setTitlesWhenImporting) {
    	this.stateStore.storePreference(Preferences.SET_TITLES_WHEN_IMPORTING, setTitlesWhenImporting);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getOsdWidth()
	 */
    @Override
	public int getOsdWidth() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.OSD_WIDTH, 300);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setOsdWidth(int)
	 */
    @Override
	public void setOsdWidth(int osdWidth) {
    	this.stateStore.storePreference(Preferences.OSD_WIDTH, osdWidth);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getOsdHorizontalAlignment()
	 */
    @Override
	public int getOsdHorizontalAlignment() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.OSD_HORIZONTAL_ALINGMENT, SwingConstants.RIGHT);        
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setOsdHorizontalAlignment(int)
	 */
    @Override
	public void setOsdHorizontalAlignment(int osdHorizontalAlignment) {
    	this.stateStore.storePreference(Preferences.OSD_HORIZONTAL_ALINGMENT, osdHorizontalAlignment);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getOsdVerticalAlignment()
	 */
    @Override
	public int getOsdVerticalAlignment() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.OSD_VERTICAL_ALINGMENT, SwingConstants.BOTTOM);        
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setOsdVerticalAlignment(int)
	 */
    @Override
	public void setOsdVerticalAlignment(int osdVerticalAlignment) {
    	this.stateStore.storePreference(Preferences.OSD_VERTICAL_ALINGMENT, osdVerticalAlignment);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getHotkeysConfig()
	 */
    @Override
	public HotkeysConfig getHotkeysConfig() {
    	return (HotkeysConfig) this.stateStore.retrievePreference(Preferences.HOTKEYS_CONFIG, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setHotkeysConfig(net.sourceforge.atunes.kernel.modules.hotkeys.HotkeysConfig)
	 */
    @Override
	public void setHotkeysConfig(HotkeysConfig hotkeysConfig) {
    	this.stateStore.storePreference(Preferences.HOTKEYS_CONFIG, hotkeysConfig);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getRecognitionPatterns()
	 */
    @Override
	@SuppressWarnings("unchecked")
	public List<String> getRecognitionPatterns() {
    	return (List<String>) this.stateStore.retrievePreference(Preferences.RECOGNITION_PATTERNS, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setRecognitionPatterns(java.util.List)
	 */
    @Override
	public void setRecognitionPatterns(List<String> recognitionPatterns) {
    	this.stateStore.storePreference(Preferences.RECOGNITION_PATTERNS, recognitionPatterns);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getMassiveRecognitionPatterns()
	 */
    @Override
	@SuppressWarnings("unchecked")
	public List<String> getMassiveRecognitionPatterns() {
    	return (List<String>) this.stateStore.retrievePreference(Preferences.MASSIVE_RECOGNITION_PATTERNS, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setMassiveRecognitionPatterns(java.util.List)
	 */
    @Override
	public void setMassiveRecognitionPatterns(List<String> massiveRecognitionPatterns) {
    	this.stateStore.storePreference(Preferences.MASSIVE_RECOGNITION_PATTERNS, massiveRecognitionPatterns);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getCommandBeforeAccessRepository()
	 */
    @Override
	public String getCommandBeforeAccessRepository() {
    	return (String) this.stateStore.retrievePreference(Preferences.COMMAND_BEFORE_ACCESS_REPOSITORY, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setCommandBeforeAccessRepository(java.lang.String)
	 */
    @Override
	public void setCommandBeforeAccessRepository(String commandBeforeAccessRepository) {
    	this.stateStore.storePreference(Preferences.COMMAND_BEFORE_ACCESS_REPOSITORY, commandBeforeAccessRepository);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getCommandAfterAccessRepository()
	 */
    @Override
	public String getCommandAfterAccessRepository() {
    	return (String) this.stateStore.retrievePreference(Preferences.COMMAND_AFTER_ACCESS_REPOSITORY, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setCommandAfterAccessRepository(java.lang.String)
	 */
    @Override
	public void setCommandAfterAccessRepository(String commandAfterAccessRepository) {
    	this.stateStore.storePreference(Preferences.COMMAND_AFTER_ACCESS_REPOSITORY, commandAfterAccessRepository);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getNotificationEngine()
	 */
    @Override
	public String getNotificationEngine() {
    	return (String) this.stateStore.retrievePreference(Preferences.NOTIFICATION_ENGINE, null);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setNotificationEngine(java.lang.String)
	 */
    @Override
	public void setNotificationEngine(String notificationEngine) {
    	this.stateStore.storePreference(Preferences.NOTIFICATION_ENGINE, notificationEngine);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowContextAlbumsInGrid()
	 */
    @Override
	public boolean isShowContextAlbumsInGrid() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_CONTEXT_ALBUMS_IN_GRID, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowContextAlbumsInGrid(boolean)
	 */
    @Override
	public void setShowContextAlbumsInGrid(boolean showContextAlbumsInGrid) {
    	this.stateStore.storePreference(Preferences.SHOW_CONTEXT_ALBUMS_IN_GRID, showContextAlbumsInGrid);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowNavigationTree()
	 */
    @Override
	public boolean isShowNavigationTree() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_NAVIGATION_TREE, true);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowNavigationTree(boolean)
	 */
    @Override
	public void setShowNavigationTree(boolean showNavigationTree) {
    	if (isShowNavigationTree() != showNavigationTree) {
    		this.stateStore.storePreference(Preferences.SHOW_NAVIGATION_TREE, showNavigationTree);
    	}
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isSimilarArtistsMode()
	 */
    @Override
	public boolean isSimilarArtistsMode() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SIMILAR_ARTISTS_MODE, false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setSimilarArtistsMode(boolean)
	 */
    @Override
	public void setSimilarArtistsMode(boolean isSimilarArtistsMode) {
    	this.stateStore.storePreference(Preferences.SIMILAR_ARTISTS_MODE, isSimilarArtistsMode);
    }
    
    

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getCustomNavigatorColumns()
	 */
    @Override
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, ColumnBean>> getCustomNavigatorColumns() {
    	// This map is not unmodifiable
    	return (Map<String, Map<String, ColumnBean>>) this.stateStore.retrievePreference(Preferences.CUSTOM_NAVIGATOR_COLUMNS, null); 
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setCustomNavigatorColumns(java.util.Map)
	 */
    @Override
	public void setCustomNavigatorColumns(Map<String, Map<String, ColumnBean>> customNavigatorColumns) {
    	this.stateStore.storePreference(Preferences.CUSTOM_NAVIGATOR_COLUMNS, customNavigatorColumns);
    }

    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getTrayPlayerIconsColor()
	 */
    @Override
	public ColorBean getTrayPlayerIconsColor() {
    	return (ColorBean) this.stateStore.retrievePreference(Preferences.TRAY_PLAYER_ICONS_COLOR, new ColorBean());
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setTrayPlayerIconsColor(net.sourceforge.atunes.kernel.modules.state.beans.ColorBean)
	 */
    @Override
	public void setTrayPlayerIconsColor(ColorBean color) {
    	this.stateStore.storePreference(Preferences.TRAY_PLAYER_ICONS_COLOR, color);
    }
    
    
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#isShowPlayerControlsOnTop()
	 */
    @Override
	public boolean isShowPlayerControlsOnTop() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_PLAYER_CONTROLS_ON_TOP, true);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setShowPlayerControlsOnTop(boolean)
	 */
    @Override
	public void setShowPlayerControlsOnTop(boolean onTop) {
    	this.stateStore.storePreference(Preferences.SHOW_PLAYER_CONTROLS_ON_TOP, onTop);
    }
    
    
        
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#getAlbumsColumns()
	 */
    @Override
	@SuppressWarnings("unchecked")
    public Map<String, ColumnBean> getAlbumsColumns() {
    	Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.stateStore.retrievePreference(Preferences.ALBUM_COLUMNS, null);
    	return map != null ? Collections.unmodifiableMap(map) : null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IState#setAlbumColumns(java.util.Map)
	 */
    @Override
	public void setAlbumColumns(Map<String, ColumnBean> columnsConfiguration) {
    	this.stateStore.storePreference(Preferences.ALBUM_COLUMNS, columnsConfiguration);
    }
}
