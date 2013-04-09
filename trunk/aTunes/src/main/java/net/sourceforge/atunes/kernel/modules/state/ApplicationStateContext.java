/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILyricsEngineInfo;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateContext implements IStateContext {

	private PreferenceHelper preferenceHelper;

	private IBeanFactory beanFactory;

	/**
	 * @param preferenceHelper
	 */
	public void setPreferenceHelper(final PreferenceHelper preferenceHelper) {
		this.preferenceHelper = preferenceHelper;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public boolean isUseContext() {
		return this.preferenceHelper.getPreference(Preferences.USE_CONTEXT,
				Boolean.class, true);
	}

	@Override
	public void setUseContext(final boolean useContext) {
		this.preferenceHelper
				.setPreference(Preferences.USE_CONTEXT, useContext);
	}

	@Override
	public String getSelectedContextTab() {
		return this.preferenceHelper.getPreference(
				Preferences.SELECTED_CONTEXT_TAB, String.class, null);
	}

	@Override
	public void setSelectedContextTab(final String selectedContextTab) {
		this.preferenceHelper.setPreference(Preferences.SELECTED_CONTEXT_TAB,
				selectedContextTab);
	}

	@Override
	public boolean isSaveContextPicture() {
		return this.preferenceHelper.getPreference(
				Preferences.SAVE_CONTEXT_PICTURE, Boolean.class, false);
	}

	@Override
	public void setSaveContextPicture(final boolean saveContextPicture) {
		this.preferenceHelper.setPreference(Preferences.SAVE_CONTEXT_PICTURE,
				saveContextPicture);
	}

	@Override
	public boolean isLastFmEnabled() {
		return this.preferenceHelper.getPreference(Preferences.LASTFM_ENABLED,
				Boolean.class, false);
	}

	@Override
	public void setLastFmEnabled(final boolean lastFmEnabled) {
		this.preferenceHelper.setPreference(Preferences.LASTFM_ENABLED,
				lastFmEnabled);
	}

	@Override
	public String getLastFmUser() {
		return this.preferenceHelper.getPreference(Preferences.LASTFM_USER,
				String.class, null);
	}

	@Override
	public void setLastFmUser(final String lastFmUser) {
		this.preferenceHelper
				.setPreference(Preferences.LASTFM_USER, lastFmUser);
	}

	@Override
	public String getLastFmPassword() {
		return this.preferenceHelper
				.getPasswordPreference(Preferences.LASTFM_PASSWORD);
	}

	@Override
	public void setLastFmPassword(final String lastFmPassword) {
		this.preferenceHelper.setPasswordPreference(
				Preferences.LASTFM_PASSWORD, lastFmPassword);
	}

	@Override
	public boolean isAutoLoveFavoriteSong() {
		return this.preferenceHelper.getPreference(
				Preferences.AUTO_LOVE_FAVORITE_SONG, Boolean.class, false);
	}

	@Override
	public void setAutoLoveFavoriteSong(final boolean autoLoveFavoriteSong) {
		this.preferenceHelper.setPreference(
				Preferences.AUTO_LOVE_FAVORITE_SONG, autoLoveFavoriteSong);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ILyricsEngineInfo> getLyricsEnginesInfo() {
		return this.preferenceHelper.getPreference(
				Preferences.LYRICS_ENGINES_INFO, List.class, null);
	}

	@Override
	public void setLyricsEnginesInfo(
			final List<ILyricsEngineInfo> lyricsEnginesInfo) {
		this.preferenceHelper.setPreference(Preferences.LYRICS_ENGINES_INFO,
				lyricsEnginesInfo);
	}

	@Override
	public boolean isShowContextAlbumsInGrid() {
		return this.preferenceHelper.getPreference(
				Preferences.SHOW_CONTEXT_ALBUMS_IN_GRID, Boolean.class, true);
	}

	@Override
	public void setShowContextAlbumsInGrid(final boolean showContextAlbumsInGrid) {
		this.preferenceHelper.setPreference(
				Preferences.SHOW_CONTEXT_ALBUMS_IN_GRID,
				showContextAlbumsInGrid);
	}

	@Override
	public String getSimilarArtistSearchQuery() {
		return this.preferenceHelper.getPreference(
				Preferences.SIMILAR_ARTIST_SEARCH_QUERY, String.class,
				this.beanFactory.getBean("similarArtistSearchQueryDefault",
						String.class));
	}

	@Override
	public void setSimilarArtistSearchQuery(final String query) {
		this.preferenceHelper.setPreference(
				Preferences.SIMILAR_ARTIST_SEARCH_QUERY, query);
	}

	@Override
	public boolean isCacheLastFmContent() {
		return this.preferenceHelper.getPreference(
				Preferences.CACHE_LASTFM_CONTENT, Boolean.class, true);
	}

	@Override
	public void setCacheLastFmContent(final boolean cache) {
		this.preferenceHelper.setPreference(Preferences.CACHE_LASTFM_CONTENT,
				cache);
	}

	@Override
	public Map<String, String> describeState() {
		// EXPLICITLY REMOVE USER AND PASSWORD FROM DESCRIPTION
		Map<String, String> state = ReflectionUtils.describe(this);
		state.remove("lastFmPassword");
		state.remove("lastFmUser");
		return state;
	}
}
