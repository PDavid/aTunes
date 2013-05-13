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

package net.sourceforge.atunes.utils;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IOSManager;

/**
 * A cache using ehcache
 * 
 * @author alex
 * 
 */
public abstract class AbstractCache {

	private IOSManager osManager;

	private String configFile;

	private CacheManager cacheManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param configFile
	 */
	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	private void init(final IOSManager osManager, final URL settings) {
		System.setProperty("ehcache.disk.store.dir", StringUtils.getString(
				osManager.getUserConfigFolder(), osManager.getFileSeparator(),
				Constants.CACHE_DIR));
		this.cacheManager = new CacheManager(settings);
	}

	protected Cache getCache(final String name) {
		return getCacheManager().getCache(name);
	}

	private synchronized CacheManager getCacheManager() {
		if (cacheManager == null) {
			init(osManager, AbstractCache.class.getResource(configFile));
		}
		return cacheManager;
	}
}
