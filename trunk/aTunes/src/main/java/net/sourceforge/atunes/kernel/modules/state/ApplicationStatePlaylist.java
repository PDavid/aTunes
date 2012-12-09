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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStatePlaylist implements IStatePlaylist {

	/**
	 * Component responsible of store state
	 */
	private IStateStore stateStore;

	/**
	 * Sets state store
	 * 
	 * @param store
	 */
	public void setStateStore(final IStateStore store) {
		this.stateStore = store;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, ColumnBean> getColumns() {
		Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.stateStore
				.retrievePreference(Preferences.COLUMNS, null);
		return map != null ? Collections.unmodifiableMap(map) : null;
	}

	@Override
	public void setColumns(final Map<String, ColumnBean> columns) {
		if (getColumns() == null || !getColumns().equals(columns)) {
			this.stateStore.storePreference(Preferences.COLUMNS,
					new HashMap<String, ColumnBean>(columns));
		}
	}

	@Override
	public boolean isAutoScrollPlayListEnabled() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.AUTO_SCROLL_PLAYLIST, true);
	}

	@Override
	public void setAutoScrollPlayListEnabled(
			final boolean autoScrollPlayListEnabled) {
		this.stateStore.storePreference(Preferences.AUTO_SCROLL_PLAYLIST,
				autoScrollPlayListEnabled);
	}

	@Override
	public String getLoadPlaylistPath() {
		return (String) this.stateStore.retrievePreference(
				Preferences.LOAD_PLAYLIST_PATH, null);
	}

	@Override
	public void setLoadPlaylistPath(final String loadPlaylistPath) {
		this.stateStore.storePreference(Preferences.LOAD_PLAYLIST_PATH,
				loadPlaylistPath);
	}

	@Override
	public String getSavePlaylistPath() {
		return (String) this.stateStore.retrievePreference(
				Preferences.SAVE_PLAYLIST_PATH, null);
	}

	@Override
	public void setSavePlaylistPath(final String savePlaylistPath) {
		this.stateStore.storePreference(Preferences.SAVE_PLAYLIST_PATH,
				savePlaylistPath);
	}

	@Override
	public boolean isStopPlayerOnPlayListSwitch() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.STOP_PLAYER_ON_PLAYLIST_SWITCH, false);
	}

	@Override
	public void setStopPlayerOnPlayListSwitch(
			final boolean stopPlayerOnPlayListSwitch) {
		this.stateStore.storePreference(
				Preferences.STOP_PLAYER_ON_PLAYLIST_SWITCH,
				stopPlayerOnPlayListSwitch);
	}

	@Override
	public boolean isStopPlayerOnPlayListClear() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.STOP_PLAYER_ON_PLAYLIST_CLEAR, true);
	}

	@Override
	public void setStopPlayerOnPlayListClear(
			final boolean stopPlayerOnPlayListClear) {
		this.stateStore.storePreference(
				Preferences.STOP_PLAYER_ON_PLAYLIST_CLEAR,
				stopPlayerOnPlayListClear);
	}

	@Override
	public boolean isShowPlayListSelectorComboBox() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.SHOW_PLAYLIST_COMBO, false);
	}

	@Override
	public void setShowPlayListSelectorComboBox(
			final boolean showPlayListSelectorComboBox) {
		this.stateStore.storePreference(Preferences.SHOW_PLAYLIST_COMBO,
				showPlayListSelectorComboBox);
	}

	@Override
	public Map<String, String> describeState() {
		return ReflectionUtils.describe(this);
	}

}
