/*
 * aTunes 2.0.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.webservices.lyrics;

import net.sf.ehcache.Element;
import net.sourceforge.atunes.misc.AbstractCache;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;

public class LyricsCache extends AbstractCache {

    private static final String LYRICS = "lyrics";

    private Logger logger = new Logger();

    public LyricsCache() {
        super(LyricsCache.class.getResource("/settings/ehcache-lyrics.xml"));
    }

    /**
     * Clears the cache.
     * 
     * @return If an Exception occurred during clearing
     */
    public synchronized boolean clearCache() {
        try {
            getCache(LYRICS).removeAll();
            getCache(LYRICS).flush();
        } catch (Exception e) {
            logger.info(LogCategories.FILE_DELETE, "Could not delete all files from lyricsr cache");
            return true;
        }
        return false;
    }

    /**
     * Retrieves lyrics from cache.
     * 
     * @param artist
     *            the artist
     * @param title
     *            the title
     * 
     * @return the string
     */
    public synchronized Lyrics retrieveLyric(String artist, String title) {
        Element element = getCache(LYRICS).get(artist + title);
        if (element != null) {
            return (Lyrics) element.getValue();
        } else {
            return null;
        }
    }

    /**
     * Stores lyrics in cache.
     * 
     * @param artist
     *            the artist
     * @param title
     *            the title
     * @param lyric
     *            the lyric
     */
    public synchronized void storeLyric(String artist, String title, Lyrics lyric) {
        if (artist == null || title == null || lyric == null) {
            return;
        }
        Element element = new Element(artist + title, lyric);
        getCache(LYRICS).put(element);
        logger.debug(LogCategories.CACHE, StringUtils.getString("Stored lyric for ", title));
    }

    public void shutdown() {
        getCache(LYRICS).dispose();
    }
}
