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

import java.io.Serializable;
import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
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

	private String cacheName;

	private CacheManager cacheManager;

	private Cache cache;

	/**
	 * @param cacheName
	 */
	public void setCacheName(final String cacheName) {
		this.cacheName = cacheName;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param configFile
	 */
	public void setConfigFile(final String configFile) {
		this.configFile = configFile;
	}

	private void init(final IOSManager osManager, final URL settings) {
		System.setProperty("ehcache.disk.store.dir", StringUtils.getString(
				osManager.getUserConfigFolder(), osManager.getFileSeparator(),
				Constants.CACHE_DIR));
		this.cacheManager = new CacheManager(settings);
	}

	private synchronized Cache getCache() {
		if (this.cache == null) {
			return getCacheManager().getCache(this.cacheName);
		}
		return this.cache;
	}

	private synchronized CacheManager getCacheManager() {
		if (this.cacheManager == null) {
			Logger.debug("Initializing cache from file: ", this.configFile);
			init(this.osManager,
					AbstractCache.class.getResource(this.configFile));
		}
		return this.cacheManager;
	}

	protected void dispose() {
		getCache().dispose();
	}

	protected void flush() {
		getCache().flush();
	}

	protected void put(final Element element) {
		getCache().put(element);
	}

	protected Element get(final Serializable key) {
		return getCache().get(key);
	}

	protected boolean removeAll() {
		boolean exception = false;
		try {
			getCache().removeAll();
		} catch (IllegalStateException e) {
			Logger.info("Could not delete all files from ", this.cacheName,
					" cache");
			exception = true;
		} catch (CacheException e) {
			Logger.info("Could not delete all files from ", this.cacheName,
					" cache");
			exception = true;
		}
		return exception;
	}
}
