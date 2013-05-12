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
 * 
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
	public void setLyricsCache(final LyricsCache lyricsCache) {
		this.lyricsCache = lyricsCache;
	}

	/**
	 * @param artist
	 */
	public void setArtist(final String artist) {
		this.artist = artist;
	}

	/**
	 * @param song
	 */
	public void setSong(final String song) {
		this.song = song;
	}

	/**
	 * @param lyricsEngines
	 */
	public void setLyricsEngines(final List<AbstractLyricsEngine> lyricsEngines) {
		this.lyricsEngines = lyricsEngines;
	}

	@Override
	public void cancelRetrieve() {
		Logger.debug("Canceling lyrics retrieve operation");
		this.canceled = true;
	}

	@Override
	public ILyrics getLyrics() {
		ILyrics lyric = getLyricFromCache();

		if (lyric == null) {
			// If any engine is loaded
			if (this.lyricsEngines != null) {
				// Ask for lyrics until a lyric is found in some engine
				int i = 0;
				while (!this.canceled
						&& i < this.lyricsEngines.size()
						&& (lyric == null || lyric.getLyrics().trim().isEmpty())) {
					lyric = this.lyricsEngines.get(i).getLyricsFor(this.artist,
							this.song, this);
					if (lyric == null) {
						Logger.info(StringUtils.getString("Lyrics for: ",
								this.artist, "/", this.song,
								" not found with engine: ", this.lyricsEngines
										.get(i).getLyricsProviderName()));
					} else {
						Logger.debug("Engine: ", this.lyricsEngines.get(i)
								.getLyricsProviderName(),
								" returned lyrics for: ", this.artist, "/",
								this.song);
					}

					i++;
				}
			}

			if (!this.canceled) {
				fixLyrics(lyric);
				this.lyricsCache.storeLyric(this.artist, this.song, lyric);
			}
		}
		// Return lyric
		return lyric;
	}

	private ILyrics getLyricFromCache() {
		// Try to get from cache
		ILyrics lyric = this.lyricsCache.retrieveLyric(this.artist, this.song);

		// Discard stored lyrics containing HTML
		if (lyric != null && lyric.getLyrics().contains("<")
				&& lyric.getLyrics().contains(">")) {
			Logger.debug("Discarding lyrics. Seems to contain some HTML code: ");
			Logger.debug(lyric.getLyrics());
			lyric = null;
		}
		return lyric;
	}

	/**
	 * Applies several common string manipulation to improve lyrics
	 * 
	 * @param lyrics
	 */
	private void fixLyrics(final ILyrics lyrics) {
		if (lyrics != null) {
			String lyricsString = lyrics.getLyrics().replace("'", "\'")
					.replace("\n\n", "\n") // Remove duplicate \n
					.replaceAll("<.*>", "") // Remove HTML
					.trim();
			lyrics.setLyrics(lyricsString);
		}
	}
}
