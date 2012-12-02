/*
 * aTunes 3.0.0
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

import java.util.List;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILyricsEngineInfo;
import net.sourceforge.atunes.model.IStateContext;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateContext implements IStateContext {

	/**
	 * Component responsible of store state
	 */
	private IStateStore stateStore;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Sets state store
	 * 
	 * @param store
	 */
	public void setStateStore(final IStateStore store) {
		this.stateStore = store;
	}

	@Override
	public boolean isUseContext() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.USE_CONTEXT, true);
	}

	@Override
	public void setUseContext(final boolean useContext) {
		if (isUseContext() != useContext) {
			this.stateStore
					.storePreference(Preferences.USE_CONTEXT, useContext);
		}
	}

	@Override
	public String getSelectedContextTab() {
		return (String) this.stateStore.retrievePreference(
				Preferences.SELECTED_CONTEXT_TAB, null);
	}

	@Override
	public void setSelectedContextTab(final String selectedContextTab) {
		if (getSelectedContextTab() == null
				|| !getSelectedContextTab().equals(selectedContextTab)) {
			this.stateStore.storePreference(Preferences.SELECTED_CONTEXT_TAB,
					selectedContextTab);
		}
	}

	@Override
	public boolean isSaveContextPicture() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.SAVE_CONTEXT_PICTURE, false);
	}

	@Override
	public void setSaveContextPicture(final boolean saveContextPicture) {
		this.stateStore.storePreference(Preferences.SAVE_CONTEXT_PICTURE,
				saveContextPicture);
	}

	@Override
	public boolean isLastFmEnabled() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.LASTFM_ENABLED, false);
	}

	@Override
	public void setLastFmEnabled(final boolean lastFmEnabled) {
		this.stateStore.storePreference(Preferences.LASTFM_ENABLED,
				lastFmEnabled);
	}

	@Override
	public String getLastFmUser() {
		return (String) this.stateStore.retrievePreference(
				Preferences.LASTFM_USER, null);
	}

	@Override
	public void setLastFmUser(final String lastFmUser) {
		this.stateStore.storePreference(Preferences.LASTFM_USER, lastFmUser);
	}

	@Override
	public String getLastFmPassword() {
		return this.stateStore
				.retrievePasswordPreference(Preferences.LASTFM_PASSWORD);
	}

	@Override
	public void setLastFmPassword(final String lastFmPassword) {
		this.stateStore.storePasswordPreference(Preferences.LASTFM_PASSWORD,
				lastFmPassword);
	}

	@Override
	public boolean isAutoLoveFavoriteSong() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.AUTO_LOVE_FAVORITE_SONG, false);
	}

	@Override
	public void setAutoLoveFavoriteSong(final boolean autoLoveFavoriteSong) {
		this.stateStore.storePreference(Preferences.AUTO_LOVE_FAVORITE_SONG,
				autoLoveFavoriteSong);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ILyricsEngineInfo> getLyricsEnginesInfo() {
		return (List<ILyricsEngineInfo>) this.stateStore.retrievePreference(
				Preferences.LYRICS_ENGINES_INFO, null);
	}

	@Override
	public void setLyricsEnginesInfo(
			final List<ILyricsEngineInfo> lyricsEnginesInfo) {
		this.stateStore.storePreference(Preferences.LYRICS_ENGINES_INFO,
				lyricsEnginesInfo);
	}

	@Override
	public boolean isShowContextAlbumsInGrid() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.SHOW_CONTEXT_ALBUMS_IN_GRID, true);
	}

	@Override
	public void setShowContextAlbumsInGrid(final boolean showContextAlbumsInGrid) {
		this.stateStore.storePreference(
				Preferences.SHOW_CONTEXT_ALBUMS_IN_GRID,
				showContextAlbumsInGrid);
	}

	@Override
	public String getSimilarArtistSearchQuery() {
		return (String) this.stateStore.retrievePreference(
				Preferences.SIMILAR_ARTIST_SEARCH_QUERY, this.beanFactory
						.getBean("similarArtistSearchQueryDefault",
								String.class));
	}

	@Override
	public void setSimilarArtistSearchQuery(final String query) {
		this.stateStore.storePreference(
				Preferences.SIMILAR_ARTIST_SEARCH_QUERY, query);
	}
}
