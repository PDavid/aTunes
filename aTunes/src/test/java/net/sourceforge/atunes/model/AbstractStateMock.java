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

/**
 * Abstract state mock allows to override necessary methods
 * @author alex
 *
 */
public abstract class AbstractStateMock implements IState {

	@Override
	public boolean isShowNavigationTable() {
		return false;
	}

	@Override
	public void setShowNavigationTable(boolean showNavigationTable) {
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
	public boolean isAutoScrollPlayListEnabled() {
		
		return false;
	}

	@Override
	public void setAutoScrollPlayListEnabled(boolean autoScrollPlayListEnabled) {
		
		
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
	public String getDeviceFileNamePattern() {
		
		return null;
	}

	@Override
	public void setDeviceFileNamePattern(String deviceFileNamePattern) {
		
		
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
	public boolean isShowNavigationTree() {
		
		return false;
	}

	@Override
	public void setShowNavigationTree(boolean showNavigationTree) {
		
		
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
	public ArtistViewMode getArtistViewMode() {
		return ArtistViewMode.BOTH;
	}

	@Override
	public void setArtistViewMode(ArtistViewMode artistViewMode) {
		
	}

}
