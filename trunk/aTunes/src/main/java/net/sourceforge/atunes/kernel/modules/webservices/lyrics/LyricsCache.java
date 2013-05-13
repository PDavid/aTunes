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

package net.sourceforge.atunes.kernel.modules.webservices.lyrics;

import net.sf.ehcache.Element;
import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.utils.AbstractCache;
import net.sourceforge.atunes.utils.Logger;

/**
 * Cache to store lyrics
 * 
 * @author alex
 * 
 */
public class LyricsCache extends AbstractCache {

	/**
	 * Clears the cache.
	 * 
	 * @return If an Exception occurred during clearing
	 */
	public synchronized boolean clearCache() {
		return removeAll();
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
	public synchronized ILyrics retrieveLyric(final String artist,
			final String title) {
		Element element = get(id(artist, title));
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
	public synchronized void storeLyric(final String artist,
			final String title, final ILyrics lyric) {
		if (artist == null || title == null || lyric == null) {
			return;
		}
		Element element = new Element(id(artist, title), lyric);
		put(element);
		Logger.debug("Stored lyric for ", title);
	}

	private static String id(final String artist, final String title) {
		return artist + title;
	}

	/**
	 * Shutdown cache
	 */
	public void shutdown() {
		dispose();
	}
}
