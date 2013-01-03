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

import java.util.Collections;
import java.util.Map;

import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.IHotkeysConfig;
import net.sourceforge.atunes.model.ILocaleBean;
import net.sourceforge.atunes.model.IProxyBean;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateCore implements IStateCore {

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
	public ILocaleBean getLocale() {
		return (ILocaleBean) this.stateStore.retrievePreference(
				Preferences.LOCALE, null);
	}

	@Override
	public void setLocale(final ILocaleBean locale) {
		this.stateStore.storePreference(Preferences.LOCALE, locale);
	}

	@Override
	public ILocaleBean getOldLocale() {
		return (ILocaleBean) this.stateStore.retrievePreference(
				Preferences.OLD_LOCALE, null);
	}

	@Override
	public void setOldLocale(final ILocaleBean oldLocale) {
		this.stateStore.storePreference(Preferences.OLD_LOCALE, oldLocale);
	}

	@Override
	public String getDefaultSearch() {
		return (String) this.stateStore.retrievePreference(
				Preferences.DEFAULT_SEARCH, null);
	}

	@Override
	public void setDefaultSearch(final String defaultSearch) {
		this.stateStore.storePreference(Preferences.DEFAULT_SEARCH,
				defaultSearch);
	}

	@Override
	public IProxyBean getProxy() {
		return (IProxyBean) this.stateStore.retrievePreference(
				Preferences.PROXY, null);
	}

	@Override
	public void setProxy(final IProxyBean proxy) {
		this.stateStore.storePreference(Preferences.PROXY, proxy);
	}

	@Override
	public boolean isEnableAdvancedSearch() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.ENABLE_ADVANCED_SEARCH, false);
	}

	@Override
	public void setEnableAdvancedSearch(final boolean enableAdvancedSearch) {
		this.stateStore.storePreference(Preferences.ENABLE_ADVANCED_SEARCH,
				enableAdvancedSearch);
	}

	@Override
	public boolean isEnableHotkeys() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.ENABLE_HOTKEYS, false);
	}

	@Override
	public void setEnableHotkeys(final boolean enableHotkeys) {
		this.stateStore.storePreference(Preferences.ENABLE_HOTKEYS,
				enableHotkeys);
	}

	@Override
	public IHotkeysConfig getHotkeysConfig() {
		return (IHotkeysConfig) this.stateStore.retrievePreference(
				Preferences.HOTKEYS_CONFIG, null);
	}

	@Override
	public void setHotkeysConfig(final IHotkeysConfig hotkeysConfig) {
		this.stateStore.storePreference(Preferences.HOTKEYS_CONFIG,
				hotkeysConfig);
	}

	@Override
	public String getNotificationEngine() {
		return (String) this.stateStore.retrievePreference(
				Preferences.NOTIFICATION_ENGINE, null);
	}

	@Override
	public void setNotificationEngine(final String notificationEngine) {
		this.stateStore.storePreference(Preferences.NOTIFICATION_ENGINE,
				notificationEngine);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, ColumnBean> getAlbumsColumns() {
		Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.stateStore
				.retrievePreference(Preferences.ALBUM_COLUMNS, null);
		return map != null ? Collections.unmodifiableMap(map) : null;
	}

	@Override
	public void setAlbumColumns(
			final Map<String, ColumnBean> columnsConfiguration) {
		this.stateStore.storePreference(Preferences.ALBUM_COLUMNS,
				columnsConfiguration);
	}

	@Override
	public void setPluginsEnabled(final boolean pluginsEnabled) {
		this.stateStore.storePreference(Preferences.PLUGINS_ENABLED,
				pluginsEnabled);

	}

	@Override
	public boolean isPluginsEnabled() {
		return (Boolean) this.stateStore.retrievePreference(
				Preferences.PLUGINS_ENABLED, false);
	}

	@Override
	public Map<String, String> describeState() {
		return ReflectionUtils.describe(this);
	}
}
