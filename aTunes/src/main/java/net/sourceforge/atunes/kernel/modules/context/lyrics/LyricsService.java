/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.kernel.modules.context.lyrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.context.Lyrics;
import net.sourceforge.atunes.kernel.modules.context.lyrics.engines.LyricsEngine;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

/**
 * The Class LyricsService.
 */
public class LyricsService {

    private static Logger logger = new Logger();

    /** Contains a list of LyricsEngine to get lyrics. */
    private List<LyricsEngine> lyricsEngines;
    private LyricsCache lyricsCache;

    public LyricsService(List<LyricsEngine> lyricsEngines, LyricsCache lyricsCache) {
        this.lyricsEngines = lyricsEngines;
        this.lyricsCache = lyricsCache;
    }

    /**
     * Public method to retrieve lyrics for a song.
     * 
     * @param artist
     *            the artist
     * @param song
     *            the song
     * 
     * @return the lyrics
     */
    public Lyrics getLyrics(String artist, String song) {
        logger.debug(LogCategories.SERVICE, artist, song);

        // Try to get from cache
        Lyrics lyric = lyricsCache.retrieveLyric(artist, song);
        if (lyric == null) {
            // If any engine is loaded
            if (lyricsEngines != null) {
                // Ask for lyrics until a lyric is found in some engine
                int i = 0;
                while (i < lyricsEngines.size() && (lyric == null || lyric.getLyrics().trim().isEmpty())) {
                    lyric = lyricsEngines.get(i++).getLyricsFor(artist, song);
                }
            }
            lyricsCache.storeLyric(artist, song, lyric);
        }
        // Return lyric
        return lyric;
    }

    /**
     * Returns a map with lyric provider names and urls for adding new lyrics
     * for the specified title and artist
     * 
     * @param artist
     *            the artist
     * @param title
     *            the title
     * @return a map with lyric provider names and urls for adding new lyrics
     */
    public Map<String, String> getUrlsForAddingNewLyrics(String artist, String title) {
        Map<String, String> result = new HashMap<String, String>();
        for (LyricsEngine lyricsEngine : lyricsEngines) {
            result.put(lyricsEngine.getLyricsProviderName(), lyricsEngine.getUrlForAddingNewLyrics(artist, title));
        }
        return result;
    }

    /**
     * @param lyricsEngines
     *            the lyricsEngines to set
     */
    public void setLyricsEngines(List<LyricsEngine> lyricsEngines) {
        this.lyricsEngines = lyricsEngines;
    }

}
