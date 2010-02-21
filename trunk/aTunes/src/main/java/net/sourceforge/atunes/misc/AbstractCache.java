/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.misc;

import java.net.URL;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.Kernel;

public abstract class AbstractCache {

    private CacheManager cacheManager;

    public AbstractCache(URL settings) {
        init(settings);
    }

    private void init(URL settings) {
        System.setProperty("ehcache.disk.store.dir", SystemProperties.getUserConfigFolder(Kernel.isDebug()) + SystemProperties.FILE_SEPARATOR + Constants.CACHE_DIR);
        this.cacheManager = new CacheManager(settings);
    }

    protected Cache getCache(String name) {
        return cacheManager.getCache(name);
    }
}
