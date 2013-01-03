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

import java.util.List;

import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.ILyricsRetrieveOperation;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Operation to retrieve lyrics
 * @author alex
 *
 */
public class LyricsRetrieveOperation implements ILyricsRetrieveOperation {

	private LyricsCache lyricsCache;
	
    private List<AbstractLyricsEngine> lyricsEngines;
	
	private String artist;
	
	private String song;
	
	private boolean canceled;

	/**
	 * @param lyricsCache
	 */
	public void setLyricsCache(LyricsCache lyricsCache) {
		this.lyricsCache = lyricsCache;
	}
	
	/**
	 * @param artist
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	/**
	 * @param song
	 */
	public void setSong(String song) {
		this.song = song;
	}
	
	/**
	 * @param lyricsEngines
	 */
	public void setLyricsEngines(List<AbstractLyricsEngine> lyricsEngines) {
		this.lyricsEngines = lyricsEngines;
	}
	
	@Override
	public void cancelRetrieve() {
		Logger.debug("Canceling lyrics retrieve operation");
		canceled = true;
	}
	
	@Override
	public ILyrics getLyrics() {
        ILyrics lyric = getLyricFromCache();
        
        if (lyric == null) {
            // If any engine is loaded
            if (lyricsEngines != null) {
                // Ask for lyrics until a lyric is found in some engine
                int i = 0;
                while (!canceled && i < lyricsEngines.size() && (lyric == null || lyric.getLyrics().trim().isEmpty())) {
                    lyric = lyricsEngines.get(i).getLyricsFor(artist, song, this);
                    if (lyric == null) {
                    	Logger.info(StringUtils.getString("Lyrics for: ", artist, "/", song, " not found with engine: ", lyricsEngines.get(i).getLyricsProviderName()));
                    } else {
                    	Logger.debug("Engine: ", lyricsEngines.get(i).getLyricsProviderName(), " returned lyrics for: ", artist, "/", song);
                    }
                    
                    i++;
                }
            }
            
            if (!canceled) {
            	fixLyrics(lyric);            
            	lyricsCache.storeLyric(artist, song, lyric);
            }
        }
        // Return lyric
        return lyric;
	}

	private ILyrics getLyricFromCache() {
		// Try to get from cache
        ILyrics lyric = lyricsCache.retrieveLyric(artist, song);
        
        // Discard stored lyrics containing HTML
        if (lyric != null && lyric.getLyrics().contains("<") && lyric.getLyrics().contains(">")) {
        	Logger.debug("Discarding lyrics. Seems to contain some HTML code: ");
        	Logger.debug(lyric.getLyrics());
        	lyric = null;
        }
		return lyric;
	}
	
    /**
     * Applies several common string manipulation to improve lyrics
     * @param lyrics
     */
    private void fixLyrics(ILyrics lyrics) {
        if (lyrics != null) {
        	String lyricsString = lyrics.getLyrics()
            						.replaceAll("'", "\'")
            						.replaceAll("\n\n", "\n") // Remove duplicate \n            	
            						.replaceAll("<.*>", "")   // Remove HTML
            						.trim();
            lyrics.setLyrics(lyricsString);
        }
    }
}
