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

import net.sourceforge.atunes.kernel.modules.hotkeys.HotkeysConfig;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.kernel.modules.tags.TagAttribute;

/**
 * Abstract state mock allows to override necessary methods
 * @author alex
 *
 */
public abstract class AbstractStateMock implements IState {

	@Override
	public boolean isShowAllRadioStations() {
		return false;
	}

	@Override
	public void setShowAllRadioStations(boolean showAllRadioStations) {
	}

	@Override
	public boolean isShowNavigationTable() {
		return false;
	}

	@Override
	public void setShowNavigationTable(boolean showNavigationTable) {
	}

	@Override
	public boolean isKeyAlwaysCaseSensitiveInRepositoryStructure() {
		return false;
	}

	@Override
	public void setKeyAlwaysCaseSensitiveInRepositoryStructure(
			boolean caseSensitiveRepositoryStructureKeys) {
	}

	@Override
	public boolean isShowAudioObjectProperties() {
		return false;
	}

	@Override
	public void setShowAudioObjectProperties(boolean showAudioObjectProperties) {
		
	}

	@Override
	public boolean isShowStatusBar() {
		return false;
	}

	@Override
	public void setShowStatusBar(boolean showStatusBar) {
	}

	@Override
	public boolean isShowOSD() {
		return false;
	}

	@Override
	public void setShowOSD(boolean showOSD) {
	}

	@Override
	public boolean isShuffle() {
		return false;
	}

	@Override
	public void setShuffle(boolean shuffle) {
	}

	@Override
	public boolean isRepeat() {
		return false;
	}

	@Override
	public void setRepeat(boolean repeat) {
	}

	@Override
	public boolean isShowSystemTray() {
		return false;
	}

	@Override
	public void setShowSystemTray(boolean showSystemTray) {
	}

	@Override
	public boolean isShowTrayPlayer() {
		return false;
	}

	@Override
	public void setShowTrayPlayer(boolean showTrayPlayer) {
	}

	@Override
	public String getNavigationView() {
		return null;
	}

	@Override
	public void setNavigationView(String navigationView) {
	}

	@Override
	public ViewMode getViewMode() {
		return null;
	}

	@Override
	public void setViewMode(ViewMode viewMode) {
	}

	@Override
	public LocaleBean getLocale() {
		return null;
	}

	@Override
	public void setLocale(LocaleBean locale) {
	}

	@Override
	public LocaleBean getOldLocale() {
		return null;
	}

	@Override
	public void setOldLocale(LocaleBean oldLocale) {
	}

	@Override
	public String getDefaultSearch() {
		return null;
	}

	@Override
	public void setDefaultSearch(String defaultSearch) {
	}

	@Override
	public boolean isUseContext() {
		return false;
	}

	@Override
	public void setUseContext(boolean useContext) {
	}

	@Override
	public String getSelectedContextTab() {
		return null;
	}

	@Override
	public void setSelectedContextTab(String selectedContextTab) {
		
		
	}

	@Override
	public Class<? extends IFrame> getFrameClass() {
		
		return null;
	}

	@Override
	public void setFrameClass(Class<? extends IFrame> frameClass) {
		
		
	}

	@Override
	public IProxy getProxy() {
		
		return null;
	}

	@Override
	public void setProxy(IProxy proxy) {
		
		
	}

	@Override
	public LookAndFeelBean getLookAndFeel() {
		
		return null;
	}

	@Override
	public void setLookAndFeel(LookAndFeelBean lookAndFeel) {
		
		
	}

	@Override
	public FontSettings getFontSettings() {
		
		return null;
	}

	@Override
	public void setFontSettings(FontSettings fontSettings) {
		
		
	}

	@Override
	public boolean isPlayAtStartup() {
		
		return false;
	}

	@Override
	public void setPlayAtStartup(boolean playAtStartup) {
		
		
	}

	@Override
	public boolean isCacheFilesBeforePlaying() {
		
		return false;
	}

	@Override
	public void setCacheFilesBeforePlaying(boolean cacheFilesBeforePlaying) {
		
		
	}

	@Override
	public boolean isUseNormalisation() {
		
		return false;
	}

	@Override
	public void setUseNormalisation(boolean useNormalisation) {
		
		
	}

	@Override
	public boolean isUseShortPathNames() {
		
		return false;
	}

	@Override
	public void setUseShortPathNames(boolean useShortPathNames) {
		
		
	}

	@Override
	public float[] getEqualizerSettings() {
		
		return null;
	}

	@Override
	public void setEqualizerSettings(float[] equalizerSettings) {
		
		
	}

	@Override
	public boolean isUseFadeAway() {
		
		return false;
	}

	@Override
	public void setUseFadeAway(boolean useFadeAway) {
		
		
	}

	@Override
	public boolean isShowTicks() {
		
		return false;
	}

	@Override
	public void setShowTicks(boolean showTicks) {
		
		
	}

	@Override
	public boolean isShowAdvancedPlayerControls() {
		
		return false;
	}

	@Override
	public void setShowAdvancedPlayerControls(boolean show) {
		
		
	}

	@Override
	public boolean isReadInfoFromRadioStream() {
		
		return false;
	}

	@Override
	public void setReadInfoFromRadioStream(boolean readInfoFromRadioStream) {
		
		
	}

	@Override
	public boolean isEnableAdvancedSearch() {
		
		return false;
	}

	@Override
	public void setEnableAdvancedSearch(boolean enableAdvancedSearch) {
		
		
	}

	@Override
	public boolean isEnableHotkeys() {
		
		return false;
	}

	@Override
	public void setEnableHotkeys(boolean enableHotkeys) {
		
		
	}

	@Override
	public int getOsdDuration() {
		
		return 0;
	}

	@Override
	public void setOsdDuration(int osdDuration) {
		
		
	}

	@Override
	public int getVolume() {
		
		return 0;
	}

	@Override
	public void setVolume(int volume) {
		
		
	}

	@Override
	public boolean isMuteEnabled() {
		
		return false;
	}

	@Override
	public void setMuteEnabled(boolean muteEnabled) {
		
		
	}

	@Override
	public int getAutoRepositoryRefreshTime() {
		
		return 0;
	}

	@Override
	public void setAutoRepositoryRefreshTime(int autoRepositoryRefreshTime) {
		
		
	}

	@Override
	public boolean isSaveRepositoryAsXml() {
		
		return false;
	}

	@Override
	public void setSaveRepositoryAsXml(boolean saveRepositoryAsXml) {
		
		
	}

	@Override
	public boolean isShowFavoritesInNavigator() {
		
		return false;
	}

	@Override
	public void setShowFavoritesInNavigator(boolean showFavoritesInNavigator) {
		
		
	}

	@Override
	public boolean isUseCdErrorCorrection() {
		
		return false;
	}

	@Override
	public void setUseCdErrorCorrection(boolean useCdErrorCorrection) {
		
		
	}

	@Override
	public boolean isUseSmartTagViewSorting() {
		
		return false;
	}

	@Override
	public void setUseSmartTagViewSorting(boolean useSmartTagViewSorting) {
		
		
	}

	@Override
	public boolean isUsePersonNamesArtistTagViewSorting() {
		
		return false;
	}

	@Override
	public void setUsePersonNamesArtistTagViewSorting(
			boolean usePersonNamesArtistTagViewSorting) {
		
		
	}

	@Override
	public boolean isSaveContextPicture() {
		
		return false;
	}

	@Override
	public void setSaveContextPicture(boolean saveContextPicture) {
		
		
	}

	@Override
	public boolean isShowExtendedTooltip() {
		
		return false;
	}

	@Override
	public void setShowExtendedTooltip(boolean showExtendedTooltip) {
		
		
	}

	@Override
	public int getExtendedTooltipDelay() {
		
		return 0;
	}

	@Override
	public void setExtendedTooltipDelay(int extendedTooltipDelay) {
		
		
	}

	@Override
	public boolean isLastFmEnabled() {
		
		return false;
	}

	@Override
	public void setLastFmEnabled(boolean lastFmEnabled) {
		
		
	}

	@Override
	public String getLastFmUser() {
		
		return null;
	}

	@Override
	public void setLastFmUser(String lastFmUser) {
		
		
	}

	@Override
	public String getLastFmPassword() {
		
		return null;
	}

	@Override
	public void setLastFmPassword(String lastFmPassword) {
		
		
	}

	@Override
	public boolean isAutoLoveFavoriteSong() {
		
		return false;
	}

	@Override
	public void setAutoLoveFavoriteSong(boolean autoLoveFavoriteSong) {
		
		
	}

	@Override
	public List<ILyricsEngineInfo> getLyricsEnginesInfo() {
		
		return null;
	}

	@Override
	public void setLyricsEnginesInfo(List<ILyricsEngineInfo> lyricsEnginesInfo) {
		
		
	}

	@Override
	public String getFullScreenBackground() {
		
		return null;
	}

	@Override
	public void setFullScreenBackground(String fullScreenBackground) {
		
		
	}

	@Override
	public String getEncoder() {
		
		return null;
	}

	@Override
	public void setEncoder(String encoder) {
		
		
	}

	@Override
	public String getEncoderQuality() {
		
		return null;
	}

	@Override
	public void setEncoderQuality(String encoderQuality) {
		
		
	}

	@Override
	public String getMp3EncoderQuality() {
		
		return null;
	}

	@Override
	public void setMp3EncoderQuality(String mp3EncoderQuality) {
		
		
	}

	@Override
	public String getFlacEncoderQuality() {
		
		return null;
	}

	@Override
	public void setFlacEncoderQuality(String flacEncoderQuality) {
		
		
	}

	@Override
	public String getCdRipperFileNamePattern() {
		
		return null;
	}

	@Override
	public void setCdRipperFileNamePattern(String cdRipperFileNamePattern) {
		
		
	}

	@Override
	public Map<String, ColumnBean> getColumns() {
		
		return null;
	}

	@Override
	public void setColumns(Map<String, ColumnBean> columns) {
		
		
	}

	@Override
	public Map<String, ColumnBean> getNavigatorColumns() {
		
		return null;
	}

	@Override
	public void setNavigatorColumns(Map<String, ColumnBean> navigatorColumns) {
		
		
	}

	@Override
	public Map<String, ColumnBean> getSearchResultsColumns() {
		
		return null;
	}

	@Override
	public void setSearchResultsColumns(
			Map<String, ColumnBean> searchResultsColumns) {
		
		
	}

	@Override
	public IFrameState getFrameState(Class<? extends IFrame> frame) {
		
		return null;
	}

	@Override
	public void setFrameState(Class<? extends IFrame> frame, IFrameState fs) {
		
		
	}

	@Override
	public String getDefaultDeviceLocation() {
		
		return null;
	}

	@Override
	public void setDefaultDeviceLocation(String defaultDeviceLocation) {
		
		
	}

	@Override
	public long getPodcastFeedEntriesRetrievalInterval() {
		
		return 0;
	}

	@Override
	public void setPodcastFeedEntriesRetrievalInterval(
			long podcastFeedEntriesRetrievalInterval) {
		
		
	}

	@Override
	public String getPodcastFeedEntryDownloadPath() {
		
		return null;
	}

	@Override
	public void setPodcastFeedEntryDownloadPath(
			String podcastFeedEntryDownloadPath) {
		
		
	}

	@Override
	public boolean isUseDownloadedPodcastFeedEntries() {
		
		return false;
	}

	@Override
	public void setUseDownloadedPodcastFeedEntries(
			boolean useDownloadedPodcastFeedEntries) {
		
		
	}

	@Override
	public boolean isRemovePodcastFeedEntriesRemovedFromPodcastFeed() {
		
		return false;
	}

	@Override
	public void setRemovePodcastFeedEntriesRemovedFromPodcastFeed(
			boolean removePodcastFeedEntriesRemovedFromPodcastFeed) {
		
		
	}

	@Override
	public boolean isHideVariousArtistsAlbums() {
		
		return false;
	}

	@Override
	public void setHideVariousArtistsAlbums(boolean hideVariousArtistsAlbums) {
		
		
	}

	@Override
	public int getMinimumSongNumberPerAlbum() {
		
		return 0;
	}

	@Override
	public void setMinimumSongNumberPerAlbum(int minimumSongNumberPerAlbum) {
		
		
	}

	@Override
	public boolean isHighlightIncompleteTagElements() {
		
		return false;
	}

	@Override
	public void setHighlightIncompleteTagElements(
			boolean highlightIncompleteTagElements) {
		
		
	}

	@Override
	public List<TagAttribute> getHighlightIncompleteTagFoldersAttributes() {
		
		return null;
	}

	@Override
	public void setHighlightIncompleteTagFoldersAttributes(
			List<TagAttribute> highlightIncompleteTagFoldersAttributes) {
		
		
	}

	@Override
	public String getPlayerEngine() {
		
		return null;
	}

	@Override
	public void setPlayerEngine(String playerEngine) {
		
		
	}

	@Override
	public boolean isAutoScrollPlayListEnabled() {
		
		return false;
	}

	@Override
	public void setAutoScrollPlayListEnabled(boolean autoScrollPlayListEnabled) {
		
		
	}

	@Override
	public List<String> getLastRepositoryFolders() {
		
		return null;
	}

	@Override
	public void setLastRepositoryFolders(List<String> lastRepositoryFolders) {
		
		
	}

	@Override
	public String getLoadPlaylistPath() {
		
		return null;
	}

	@Override
	public void setLoadPlaylistPath(String loadPlaylistPath) {
		
		
	}

	@Override
	public String getSavePlaylistPath() {
		
		return null;
	}

	@Override
	public void setSavePlaylistPath(String savePlaylistPath) {
		
		
	}

	@Override
	public boolean isStopPlayerOnPlayListSwitch() {
		
		return false;
	}

	@Override
	public void setStopPlayerOnPlayListSwitch(boolean stopPlayerOnPlayListSwitch) {
		
		
	}

	@Override
	public boolean isStopPlayerOnPlayListClear() {
		
		return false;
	}

	@Override
	public void setStopPlayerOnPlayListClear(boolean stopPlayerOnPlayListClear) {
		
		
	}

	@Override
	public String getImportExportFileNamePattern() {
		
		return null;
	}

	@Override
	public void setImportExportFileNamePattern(
			String importExportFileNamePattern) {
		
		
	}

	@Override
	public String getDeviceFileNamePattern() {
		
		return null;
	}

	@Override
	public void setDeviceFileNamePattern(String deviceFileNamePattern) {
		
		
	}

	@Override
	public String getImportExportFolderPathPattern() {
		
		return null;
	}

	@Override
	public void setImportExportFolderPathPattern(
			String importExportFolderPathPattern) {
		
		
	}

	@Override
	public String getDeviceFolderPathPattern() {
		
		return null;
	}

	@Override
	public void setDeviceFolderPathPattern(String deviceFolderPathPattern) {
		
		
	}

	@Override
	public boolean isAllowRepeatedSongsInDevice() {
		
		return false;
	}

	@Override
	public void setAllowRepeatedSongsInDevice(boolean allowRepeatedSongsInDevice) {
		
		
	}

	@Override
	public boolean isReviewTagsBeforeImport() {
		
		return false;
	}

	@Override
	public void setReviewTagsBeforeImport(boolean reviewTagsBeforeImport) {
		
		
	}

	@Override
	public boolean isApplyChangesToSourceFilesBeforeImport() {
		
		return false;
	}

	@Override
	public void setApplyChangesToSourceFilesBeforeImport(
			boolean applyChangesToSourceFilesBeforeImport) {
		
		
	}

	@Override
	public boolean isSetTrackNumbersWhenImporting() {
		
		return false;
	}

	@Override
	public void setSetTrackNumbersWhenImporting(
			boolean setTrackNumbersWhenImporting) {
		
		
	}

	@Override
	public boolean isSetTitlesWhenImporting() {
		
		return false;
	}

	@Override
	public void setSetTitlesWhenImporting(boolean setTitlesWhenImporting) {
		
		
	}

	@Override
	public int getOsdWidth() {
		
		return 0;
	}

	@Override
	public void setOsdWidth(int osdWidth) {
		
		
	}

	@Override
	public int getOsdHorizontalAlignment() {
		
		return 0;
	}

	@Override
	public void setOsdHorizontalAlignment(int osdHorizontalAlignment) {
		
		
	}

	@Override
	public int getOsdVerticalAlignment() {
		
		return 0;
	}

	@Override
	public void setOsdVerticalAlignment(int osdVerticalAlignment) {
		
		
	}

	@Override
	public HotkeysConfig getHotkeysConfig() {
		
		return null;
	}

	@Override
	public void setHotkeysConfig(IHotkeysConfig hotkeysConfig) {
		
		
	}

	@Override
	public List<String> getRecognitionPatterns() {
		
		return null;
	}

	@Override
	public void setRecognitionPatterns(List<String> recognitionPatterns) {
		
		
	}

	@Override
	public List<String> getMassiveRecognitionPatterns() {
		
		return null;
	}

	@Override
	public void setMassiveRecognitionPatterns(
			List<String> massiveRecognitionPatterns) {
		
		
	}

	@Override
	public String getCommandBeforeAccessRepository() {
		
		return null;
	}

	@Override
	public void setCommandBeforeAccessRepository(
			String commandBeforeAccessRepository) {
		
		
	}

	@Override
	public String getCommandAfterAccessRepository() {
		
		return null;
	}

	@Override
	public void setCommandAfterAccessRepository(
			String commandAfterAccessRepository) {
		
		
	}

	@Override
	public String getNotificationEngine() {
		
		return null;
	}

	@Override
	public void setNotificationEngine(String notificationEngine) {
		
		
	}

	@Override
	public boolean isShowContextAlbumsInGrid() {
		
		return false;
	}

	@Override
	public void setShowContextAlbumsInGrid(boolean showContextAlbumsInGrid) {
		
		
	}

	@Override
	public boolean isShowNavigationTree() {
		
		return false;
	}

	@Override
	public void setShowNavigationTree(boolean showNavigationTree) {
		
		
	}

	@Override
	public boolean isSimilarArtistsMode() {
		
		return false;
	}

	@Override
	public void setSimilarArtistsMode(boolean isSimilarArtistsMode) {
		
		
	}

	@Override
	public Map<String, Map<String, ColumnBean>> getCustomNavigatorColumns() {
		
		return null;
	}

	@Override
	public void setCustomNavigatorColumns(
			Map<String, Map<String, ColumnBean>> customNavigatorColumns) {
		
		
	}

	@Override
	public IColorBean getTrayPlayerIconsColor() {
		
		return null;
	}

	@Override
	public void setTrayPlayerIconsColor(IColorBean color) {
		
		
	}

	@Override
	public boolean isShowPlayerControlsOnTop() {
		
		return false;
	}

	@Override
	public void setShowPlayerControlsOnTop(boolean onTop) {
		
		
	}

	@Override
	public Map<String, ColumnBean> getAlbumsColumns() {
		
		return null;
	}

	@Override
	public void setAlbumColumns(Map<String, ColumnBean> columnsConfiguration) {
		
		
	}

	@Override
	public void setPluginsEnabled(boolean pluginsEnabled) {
		
	}

	@Override
	public boolean isPluginsEnabled() {
		return false;
	}

	@Override
	public ArtistViewMode getArtistViewMode() {
		return ArtistViewMode.BOTH;
	}

	@Override
	public void setArtistViewMode(ArtistViewMode artistViewMode) {
		
	}

}
