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
	public ILocaleBean getLocale();

	/**
	 * Locale
	 * 
	 * @param locale
	 */
	public void setLocale(ILocaleBean locale);

	/**
	 * Previous locale
	 * 
	 * @return
	 */
	public ILocaleBean getOldLocale();

	/**
	 * Previous locale
	 * 
	 * @param oldLocale
	 */
	public void setOldLocale(ILocaleBean oldLocale);

	/**
	 * Search engine used
	 * 
	 * @return
	 */
	public String getDefaultSearch();

	/**
	 * Search engine used
	 * 
	 * @param defaultSearch
	 */
	public void setDefaultSearch(String defaultSearch);

	/**
	 * Proxy used
	 * 
	 * @return
	 */
	public IProxyBean getProxy();

	/**
	 * Proxy used
	 * 
	 * @param proxy
	 */
	public void setProxy(IProxyBean proxy);

	/**
	 * Enable advanced search
	 * 
	 * @return
	 */
	public boolean isEnableAdvancedSearch();

	/**
	 * Enable advanced search
	 * 
	 * @param enableAdvancedSearch
	 */
	public void setEnableAdvancedSearch(boolean enableAdvancedSearch);

	/**
	 * Use hotkeys
	 * 
	 * @return
	 */
	public boolean isEnableHotkeys();

	/**
	 * Use hotkeys
	 * 
	 * @param enableHotkeys
	 */
	public void setEnableHotkeys(boolean enableHotkeys);

	/**
	 * Hotkeys
	 * 
	 * @return
	 */
	public IHotkeysConfig getHotkeysConfig();

	/**
	 * Hotkeys
	 * 
	 * @param hotkeysConfig
	 */
	public void setHotkeysConfig(IHotkeysConfig hotkeysConfig);

	/**
	 * Notification engine
	 * 
	 * @return
	 */
	public String getNotificationEngine();

	/**
	 * Notification engine
	 * 
	 * @param notificationEngine
	 */
	public void setNotificationEngine(String notificationEngine);

	/**
	 * Albums columns
	 * 
	 * @return
	 */
	public Map<String, ColumnBean> getAlbumsColumns();

	/**
	 * Albums columns
	 * 
	 * @param columnsConfiguration
	 */
	public void setAlbumColumns(Map<String, ColumnBean> columnsConfiguration);

	/**
	 * Plugins enabled
	 * 
	 * @param pluginsEnabled
	 */
	public void setPluginsEnabled(boolean pluginsEnabled);

	/**
	 * Plugins enabled
	 * 
	 * @return
	 */
	public boolean isPluginsEnabled();

}
