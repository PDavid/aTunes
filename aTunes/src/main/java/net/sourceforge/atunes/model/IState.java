/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.model;

import java.util.List;
import java.util.Map;

public interface IState {

	public boolean isShowAllRadioStations();

	public void setShowAllRadioStations(boolean showAllRadioStations);

	public boolean isShowNavigationTable();

	public void setShowNavigationTable(boolean showNavigationTable);

	/**
	 * used in RepositoryFiller to build repository structure keys case sensitive or not
	 * @return true if case structures genre and artist handled sensitive, default = false for convenience
	 */
	public boolean isKeyAlwaysCaseSensitiveInRepositoryStructure();

	/**
	 * enable case sensitive tree structure of artist and genre or merge keys case insensitive
	 * @param caseSensitiveRepositoryStructureKeys
	 */
	public void setKeyAlwaysCaseSensitiveInRepositoryStructure(
			boolean caseSensitiveRepositoryStructureKeys);

	public boolean isShowAudioObjectProperties();

	public void setShowAudioObjectProperties(boolean showAudioObjectProperties);

	public boolean isShowStatusBar();

	public void setShowStatusBar(boolean showStatusBar);

	public boolean isShowOSD();

	public void setShowOSD(boolean showOSD);

	public boolean isShuffle();

	public void setShuffle(boolean shuffle);

	public boolean isRepeat();

	public void setRepeat(boolean repeat);

	public boolean isShowSystemTray();

	public void setShowSystemTray(boolean showSystemTray);

	public boolean isShowTrayPlayer();

	public void setShowTrayPlayer(boolean showTrayPlayer);

	public String getNavigationView();

	public void setNavigationView(String navigationView);

	public ViewMode getViewMode();

	public void setViewMode(ViewMode viewMode);

	public ILocaleBean getLocale();

	public void setLocale(ILocaleBean locale);

	public ILocaleBean getOldLocale();

	public void setOldLocale(ILocaleBean oldLocale);

	public String getDefaultSearch();

	public void setDefaultSearch(String defaultSearch);

	public boolean isUseContext();

	public void setUseContext(boolean useContext);

	public String getSelectedContextTab();

	public void setSelectedContextTab(String selectedContextTab);


	public Class<? extends IFrame> getFrameClass();

	public void setFrameClass(Class<? extends IFrame> frameClass);

	public IProxyBean getProxy();

	public void setProxy(IProxyBean proxy);

	public LookAndFeelBean getLookAndFeel();

	public void setLookAndFeel(LookAndFeelBean lookAndFeel);

	public FontSettings getFontSettings();

	public void setFontSettings(FontSettings fontSettings);

	public boolean isPlayAtStartup();

	public void setPlayAtStartup(boolean playAtStartup);

	public boolean isCacheFilesBeforePlaying();

	public void setCacheFilesBeforePlaying(boolean cacheFilesBeforePlaying);

	public boolean isUseNormalisation();

	public void setUseNormalisation(boolean useNormalisation);

	public boolean isUseShortPathNames();

	public void setUseShortPathNames(boolean useShortPathNames);

	public float[] getEqualizerSettings();

	public void setEqualizerSettings(float[] equalizerSettings);

	public boolean isUseFadeAway();

	public void setUseFadeAway(boolean useFadeAway);

	public boolean isShowAdvancedPlayerControls();

	public void setShowAdvancedPlayerControls(boolean show);

	public boolean isReadInfoFromRadioStream();

	public void setReadInfoFromRadioStream(boolean readInfoFromRadioStream);

	public boolean isEnableAdvancedSearch();

	public void setEnableAdvancedSearch(boolean enableAdvancedSearch);

	public boolean isEnableHotkeys();

	public void setEnableHotkeys(boolean enableHotkeys);

	public int getOsdDuration();

	public void setOsdDuration(int osdDuration);

	public int getVolume();

	public void setVolume(int volume);

	public boolean isMuteEnabled();

	public void setMuteEnabled(boolean muteEnabled);

	public int getAutoRepositoryRefreshTime();

	public void setAutoRepositoryRefreshTime(int autoRepositoryRefreshTime);

	public boolean isShowFavoritesInNavigator();

	public void setShowFavoritesInNavigator(boolean showFavoritesInNavigator);

	public boolean isUseCdErrorCorrection();

	public void setUseCdErrorCorrection(boolean useCdErrorCorrection);

	public boolean isUseSmartTagViewSorting();

	public void setUseSmartTagViewSorting(boolean useSmartTagViewSorting);

	public boolean isUsePersonNamesArtistTagViewSorting();

	public void setUsePersonNamesArtistTagViewSorting(
			boolean usePersonNamesArtistTagViewSorting);

	public boolean isSaveContextPicture();

	public void setSaveContextPicture(boolean saveContextPicture);

	public boolean isShowExtendedTooltip();

	public void setShowExtendedTooltip(boolean showExtendedTooltip);

	public int getExtendedTooltipDelay();

	public void setExtendedTooltipDelay(int extendedTooltipDelay);

	public boolean isLastFmEnabled();

	public void setLastFmEnabled(boolean lastFmEnabled);

	public String getLastFmUser();

	public void setLastFmUser(String lastFmUser);

	public String getLastFmPassword();

	public void setLastFmPassword(String lastFmPassword);

	public boolean isAutoLoveFavoriteSong();

	public void setAutoLoveFavoriteSong(boolean autoLoveFavoriteSong);


	public List<ILyricsEngineInfo> getLyricsEnginesInfo();

	public void setLyricsEnginesInfo(List<ILyricsEngineInfo> lyricsEnginesInfo);

	public String getFullScreenBackground();

	public void setFullScreenBackground(String fullScreenBackground);

	public String getEncoder();

	public void setEncoder(String encoder);

	public String getEncoderQuality();

	public void setEncoderQuality(String encoderQuality);

	public String getMp3EncoderQuality();

	public void setMp3EncoderQuality(String mp3EncoderQuality);

	public String getFlacEncoderQuality();

	public void setFlacEncoderQuality(String flacEncoderQuality);

	public String getCdRipperFileNamePattern();

	public void setCdRipperFileNamePattern(String cdRipperFileNamePattern);


	public Map<String, ColumnBean> getColumns();

	public void setColumns(Map<String, ColumnBean> columns);


	public Map<String, ColumnBean> getNavigatorColumns();

	public void setNavigatorColumns(Map<String, ColumnBean> navigatorColumns);


	public Map<String, ColumnBean> getSearchResultsColumns();

	public void setSearchResultsColumns(
			Map<String, ColumnBean> searchResultsColumns);

	public IFrameState getFrameState(Class<? extends IFrame> frame);

	public void setFrameState(Class<? extends IFrame> frame, IFrameState fs);

	public String getDefaultDeviceLocation();

	public void setDefaultDeviceLocation(String defaultDeviceLocation);

	public long getPodcastFeedEntriesRetrievalInterval();

	public void setPodcastFeedEntriesRetrievalInterval(
			long podcastFeedEntriesRetrievalInterval);

	public String getPodcastFeedEntryDownloadPath();

	public void setPodcastFeedEntryDownloadPath(
			String podcastFeedEntryDownloadPath);

	public boolean isUseDownloadedPodcastFeedEntries();

	public void setUseDownloadedPodcastFeedEntries(
			boolean useDownloadedPodcastFeedEntries);

	public boolean isRemovePodcastFeedEntriesRemovedFromPodcastFeed();

	public void setRemovePodcastFeedEntriesRemovedFromPodcastFeed(
			boolean removePodcastFeedEntriesRemovedFromPodcastFeed);

	public boolean isHideVariousArtistsAlbums();

	public void setHideVariousArtistsAlbums(boolean hideVariousArtistsAlbums);

	public int getMinimumSongNumberPerAlbum();

	public void setMinimumSongNumberPerAlbum(int minimumSongNumberPerAlbum);

	public boolean isHighlightIncompleteTagElements();

	public void setHighlightIncompleteTagElements(
			boolean highlightIncompleteTagElements);


	public List<TagAttribute> getHighlightIncompleteTagFoldersAttributes();

	public void setHighlightIncompleteTagFoldersAttributes(
			List<TagAttribute> highlightIncompleteTagFoldersAttributes);

	public String getPlayerEngine();

	public void setPlayerEngine(String playerEngine);

	public boolean isAutoScrollPlayListEnabled();

	public void setAutoScrollPlayListEnabled(boolean autoScrollPlayListEnabled);


	public List<String> getLastRepositoryFolders();

	public void setLastRepositoryFolders(List<String> lastRepositoryFolders);

	public String getLoadPlaylistPath();

	public void setLoadPlaylistPath(String loadPlaylistPath);

	public String getSavePlaylistPath();

	public void setSavePlaylistPath(String savePlaylistPath);

	public boolean isStopPlayerOnPlayListSwitch();

	public void setStopPlayerOnPlayListSwitch(boolean stopPlayerOnPlayListSwitch);

	public boolean isStopPlayerOnPlayListClear();

	public void setStopPlayerOnPlayListClear(boolean stopPlayerOnPlayListClear);

	public String getImportExportFileNamePattern();

	public void setImportExportFileNamePattern(
			String importExportFileNamePattern);

	public String getDeviceFileNamePattern();

	public void setDeviceFileNamePattern(String deviceFileNamePattern);

	public String getImportExportFolderPathPattern();

	public void setImportExportFolderPathPattern(
			String importExportFolderPathPattern);

	public String getDeviceFolderPathPattern();

	public void setDeviceFolderPathPattern(String deviceFolderPathPattern);

	public boolean isAllowRepeatedSongsInDevice();

	public void setAllowRepeatedSongsInDevice(boolean allowRepeatedSongsInDevice);

	public boolean isReviewTagsBeforeImport();

	public void setReviewTagsBeforeImport(boolean reviewTagsBeforeImport);

	public boolean isApplyChangesToSourceFilesBeforeImport();

	public void setApplyChangesToSourceFilesBeforeImport(
			boolean applyChangesToSourceFilesBeforeImport);

	public boolean isSetTrackNumbersWhenImporting();

	public void setSetTrackNumbersWhenImporting(
			boolean setTrackNumbersWhenImporting);

	public boolean isSetTitlesWhenImporting();

	public void setSetTitlesWhenImporting(boolean setTitlesWhenImporting);

	public int getOsdWidth();

	public void setOsdWidth(int osdWidth);

	public int getOsdHorizontalAlignment();

	public void setOsdHorizontalAlignment(int osdHorizontalAlignment);

	public int getOsdVerticalAlignment();

	public void setOsdVerticalAlignment(int osdVerticalAlignment);

	public IHotkeysConfig getHotkeysConfig();

	public void setHotkeysConfig(IHotkeysConfig hotkeysConfig);


	public List<String> getRecognitionPatterns();

	public void setRecognitionPatterns(List<String> recognitionPatterns);


	public List<String> getMassiveRecognitionPatterns();

	public void setMassiveRecognitionPatterns(
			List<String> massiveRecognitionPatterns);

	public String getCommandBeforeAccessRepository();

	public void setCommandBeforeAccessRepository(
			String commandBeforeAccessRepository);

	public String getCommandAfterAccessRepository();

	public void setCommandAfterAccessRepository(
			String commandAfterAccessRepository);

	public String getNotificationEngine();

	public void setNotificationEngine(String notificationEngine);

	public boolean isShowContextAlbumsInGrid();

	public void setShowContextAlbumsInGrid(boolean showContextAlbumsInGrid);

	public boolean isShowNavigationTree();

	public void setShowNavigationTree(boolean showNavigationTree);

	public boolean isSimilarArtistsMode();

	public void setSimilarArtistsMode(boolean isSimilarArtistsMode);


	public Map<String, Map<String, ColumnBean>> getCustomNavigatorColumns();

	public void setCustomNavigatorColumns(
			Map<String, Map<String, ColumnBean>> customNavigatorColumns);

	public IColorBean getTrayPlayerIconsColor();

	public void setTrayPlayerIconsColor(IColorBean color);

	public boolean isShowPlayerControlsOnTop();

	public void setShowPlayerControlsOnTop(boolean onTop);

	public Map<String, ColumnBean> getAlbumsColumns();

	public void setAlbumColumns(Map<String, ColumnBean> columnsConfiguration);

	public void setPluginsEnabled(boolean pluginsEnabled);

	public boolean isPluginsEnabled();

	public ArtistViewMode getArtistViewMode();

	void setArtistViewMode(ArtistViewMode artistViewMode); 
	


}