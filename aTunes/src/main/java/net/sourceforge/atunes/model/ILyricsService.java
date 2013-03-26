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

package net.sourceforge.atunes.model;

import java.util.Map;


/**
 * Represents a component used to retrieve (or add) lyrics
 * @author alex
 *
 */
public interface ILyricsService {

	/**
	 * Updates service after a configuration change
	 */
	public void updateService();

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
	public ILyrics getLyrics(String artist, String song);
	
	/**
	 * Returns a lyrics retrieve operation to get lyrics
	 * @param artist
	 * @param song
	 * @return
	 */
	public ILyricsRetrieveOperation getLyricsRetrieveOperation(String artist, String song);
	
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
	public Map<String, String> getUrlsForAddingNewLyrics(String artist, String title);

	/**
	 * Delegate method to clear cache
	 * 
	 * @return
	 */
	public boolean clearCache();

	/**
	 * Finishes service
	 */
	public void finishService();

}