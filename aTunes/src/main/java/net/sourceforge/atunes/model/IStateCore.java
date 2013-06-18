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

package net.sourceforge.atunes.model;

import java.util.Map;

/**
 * @author alex
 * 
 */
public interface IStateCore extends IState {

	/**
	 * Locale
	 * 
	 * @return
	 */
	ILocaleBean getLocale();

	/**
	 * Locale
	 * 
	 * @param locale
	 */
	void setLocale(ILocaleBean locale);

	/**
	 * Previous locale
	 * 
	 * @return
	 */
	ILocaleBean getOldLocale();

	/**
	 * Previous locale
	 * 
	 * @param oldLocale
	 */
	void setOldLocale(ILocaleBean oldLocale);

	/**
	 * Search engine used
	 * 
	 * @return
	 */
	String getDefaultSearch();

	/**
	 * Search engine used
	 * 
	 * @param defaultSearch
	 */
	void setDefaultSearch(String defaultSearch);

	/**
	 * Proxy used
	 * 
	 * @return
	 */
	IProxyBean getProxy();

	/**
	 * Proxy used
	 * 
	 * @param proxy
	 */
	void setProxy(IProxyBean proxy);

	/**
	 * Use hotkeys
	 * 
	 * @return
	 */
	boolean isEnableHotkeys();

	/**
	 * Use hotkeys
	 * 
	 * @param enableHotkeys
	 */
	void setEnableHotkeys(boolean enableHotkeys);

	/**
	 * Hotkeys
	 * 
	 * @return
	 */
	IHotkeysConfig getHotkeysConfig();

	/**
	 * Hotkeys
	 * 
	 * @param hotkeysConfig
	 */
	void setHotkeysConfig(IHotkeysConfig hotkeysConfig);

	/**
	 * Notification engine
	 * 
	 * @return
	 */
	String getNotificationEngine();

	/**
	 * Notification engine
	 * 
	 * @param notificationEngine
	 */
	void setNotificationEngine(String notificationEngine);

	/**
	 * Albums columns
	 * 
	 * @return
	 */
	Map<String, ColumnBean> getAlbumsColumns();

	/**
	 * Albums columns
	 * 
	 * @param columnsConfiguration
	 */
	void setAlbumColumns(Map<String, ColumnBean> columnsConfiguration);

	/**
	 * @param responseMail
	 */
	void setErrorReportsResponseMail(String responseMail);

	/**
	 * @return response mail for error reports
	 */
	String getErrorReportsResponseMail();

}
