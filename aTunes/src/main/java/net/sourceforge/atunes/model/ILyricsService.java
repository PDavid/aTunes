package net.sourceforge.atunes.model;

import java.util.Map;

import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;

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
	public Lyrics getLyrics(String artist, String song);

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
	public Map<String, String> getUrlsForAddingNewLyrics(String artist,
			String title);

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