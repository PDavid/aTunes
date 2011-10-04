/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.webservices.lyrics;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.utils.AbstractCache;
import net.sourceforge.atunes.utils.Logger;

public class LyricsCache extends AbstractCache {

    private static final String LYRICS = "lyrics";

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
            getCache().removeAll();
            getCache().flush();
        } catch (Exception e) {
            Logger.info("Could not delete all files from lyrics cache");
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
    public synchronized ILyrics retrieveLyric(String artist, String title) {
        Element element = getCache().get(id(artist, title));
        if (element != null) {
            return (ILyrics) element.getValue();
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
    public synchronized void storeLyric(String artist, String title, ILyrics lyric) {
        if (artist == null || title == null || lyric == null) {
            return;
        }
        Element element = new Element(id(artist, title), lyric);
        getCache().put(element);
        Logger.debug("Stored lyric for ", title);
    }

    private static String id(String artist, String title) {
        return artist + title;
    }

    private Cache getCache() {
        return getCache(LYRICS);
    }

    public void shutdown() {
        getCache().dispose();
    }
}
