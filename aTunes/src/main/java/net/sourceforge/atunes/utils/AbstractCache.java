/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.utils;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IOSManager;

/**
 * A cache using ehcache
 * @author alex
 *
 */
public abstract class AbstractCache {

	private CacheManager cacheManager;

	/**
	 * @param osManager
	 * @param settings
	 */
	public AbstractCache(final IOSManager osManager, final URL settings) {
		init(osManager, settings);
	}

	private void init(final IOSManager osManager, final URL settings) {
		System.setProperty("ehcache.disk.store.dir", StringUtils.getString(osManager.getUserConfigFolder(), osManager.getFileSeparator(), Constants.CACHE_DIR));
		this.cacheManager = new CacheManager(settings);
	}

	protected Cache getCache(final String name) {
		return cacheManager.getCache(name);
	}
}
