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

	private PreferenceHelper preferenceHelper;

	/**
	 * @param preferenceHelper
	 */
	public void setPreferenceHelper(final PreferenceHelper preferenceHelper) {
		this.preferenceHelper = preferenceHelper;
	}

	@Override
	public ILocaleBean getLocale() {
		return this.preferenceHelper.getPreference(Preferences.LOCALE,
				ILocaleBean.class, null);
	}

	@Override
	public void setLocale(final ILocaleBean locale) {
		this.preferenceHelper.setPreference(Preferences.LOCALE, locale);
	}

	@Override
	public ILocaleBean getOldLocale() {
		return this.preferenceHelper.getPreference(Preferences.OLD_LOCALE,
				ILocaleBean.class, null);
	}

	@Override
	public void setOldLocale(final ILocaleBean oldLocale) {
		this.preferenceHelper.setPreference(Preferences.OLD_LOCALE, oldLocale);
	}

	@Override
	public String getDefaultSearch() {
		return this.preferenceHelper.getPreference(Preferences.DEFAULT_SEARCH,
				String.class, null);
	}

	@Override
	public void setDefaultSearch(final String defaultSearch) {
		this.preferenceHelper.setPreference(Preferences.DEFAULT_SEARCH,
				defaultSearch);
	}

	@Override
	public IProxyBean getProxy() {
		return this.preferenceHelper.getPreference(Preferences.PROXY,
				IProxyBean.class, null);
	}

	@Override
	public void setProxy(final IProxyBean proxy) {
		this.preferenceHelper.setPreference(Preferences.PROXY, proxy);
	}

	@Override
	public boolean isEnableHotkeys() {
		return this.preferenceHelper.getPreference(Preferences.ENABLE_HOTKEYS,
				Boolean.class, false);
	}

	@Override
	public void setEnableHotkeys(final boolean enableHotkeys) {
		this.preferenceHelper.setPreference(Preferences.ENABLE_HOTKEYS,
				enableHotkeys);
	}

	@Override
	public IHotkeysConfig getHotkeysConfig() {
		return this.preferenceHelper.getPreference(Preferences.HOTKEYS_CONFIG,
				IHotkeysConfig.class, null);
	}

	@Override
	public void setHotkeysConfig(final IHotkeysConfig hotkeysConfig) {
		this.preferenceHelper.setPreference(Preferences.HOTKEYS_CONFIG,
				hotkeysConfig);
	}

	@Override
	public String getNotificationEngine() {
		return this.preferenceHelper.getPreference(
				Preferences.NOTIFICATION_ENGINE, String.class, null);
	}

	@Override
	public void setNotificationEngine(final String notificationEngine) {
		this.preferenceHelper.setPreference(Preferences.NOTIFICATION_ENGINE,
				notificationEngine);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, ColumnBean> getAlbumsColumns() {
		Map<String, ColumnBean> map = this.preferenceHelper.getPreference(
				Preferences.ALBUM_COLUMNS, Map.class, null);
		return map != null ? Collections.unmodifiableMap(map) : null;
	}

	@Override
	public void setAlbumColumns(
			final Map<String, ColumnBean> columnsConfiguration) {
		this.preferenceHelper.setPreference(Preferences.ALBUM_COLUMNS,
				columnsConfiguration);
	}

	@Override
	public String getErrorReportsResponseMail() {
		return this.preferenceHelper.getPreference(
				Preferences.ERROR_REPORTS_MAIL, String.class, null);
	}

	@Override
	public void setErrorReportsResponseMail(final String responseMail) {
		this.preferenceHelper.setPreference(Preferences.ERROR_REPORTS_MAIL,
				responseMail);
	}

	@Override
	public Map<String, String> describeState() {
		return ReflectionUtils.describe(this);
	}
}
