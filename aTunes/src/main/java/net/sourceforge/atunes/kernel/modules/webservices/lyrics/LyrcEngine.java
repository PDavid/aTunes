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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.ILyricsRetrieveOperation;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class LyrcEngine.
 */
public class LyrcEngine extends AbstractLyricsEngine {

	private static final String ARTIST_WILDCARD = "(%ARTIST%)";
	private static final String SONG_WILDCARD = "(%SONG%)";

	/** The base url. */
	private static final String BASE_URL = StringUtils.getString(
			"http://www.lyrc.com.ar/en/tema1en.php?artist=", ARTIST_WILDCARD,
			"&songname=", SONG_WILDCARD);

	/** The suggestions url. */
	private static final String SUGGESTIONS_URL = "http://www.lyrc.com.ar/en/";

	/** The lyrics adding url. */
	private static final String ADD_LYRICS_URL = StringUtils.getString(
			"http://www.lyrc.com.ar/en/add/add.php?tema=", SONG_WILDCARD,
			"&grupo=", ARTIST_WILDCARD);

	/**
	 * Returns if a string is composed only by letters.
	 * 
	 * @param t
	 *            the t
	 * 
	 * @return true, if valid token
	 */
	private static boolean validToken(final String t) {
		return t.matches("[A-Za-z]+");
	}

	private String getLyrics(final String url, final String artist,
			final String title, final ILyricsRetrieveOperation operation) {
		try {
			// read html return
			String html = readURL(getConnection(url, operation), "ISO-8859-1");

			if (html.contains("Suggestions : <br>")) { // More than one
														// possibility, find the
														// best one
				return getSuggestionsAndSelectOne(artist, title, html,
						operation);
			} else {
				return removeHtmlAndReturnLyrics(html);
			}
		} catch (IOException e) {
			Logger.error(StringUtils.getString(e.getClass().getCanonicalName(),
					" (", e.getMessage(), ")"));
			return null;
		}
	}

	/**
	 * @param html
	 * @return
	 */
	private String removeHtmlAndReturnLyrics(final String html) {
		// Remove html before lyrics
		String htmlProcessed = html.substring(html.indexOf("</table>") + 8);

		// Remove html after lyrics
		int pPos = htmlProcessed.indexOf("<p>");
		int brPos = htmlProcessed.indexOf("<br>");

		if (pPos == -1) {
			pPos = Integer.MAX_VALUE;
		}

		if (brPos == -1) {
			brPos = Integer.MAX_VALUE;
		}

		htmlProcessed = htmlProcessed.substring(0, pPos < brPos ? pPos : brPos);

		// Remove <br/>
		htmlProcessed = htmlProcessed.replace("<br />", "");

		// Bad parsing....
		if (htmlProcessed.contains("<head>")) {
			return null;
		}

		return htmlProcessed;
	}

	/**
	 * @param artist
	 * @param title
	 * @param html
	 * @param operation
	 * @return
	 */
	private String getSuggestionsAndSelectOne(final String artist,
			final String title, final String html,
			final ILyricsRetrieveOperation operation) {
		String htmlProcessed = html.substring(html
				.indexOf("Suggestions : <br>"));
		htmlProcessed = htmlProcessed.substring(0,
				htmlProcessed.indexOf("<br><br"));

		// Find suggestions and add to a map
		Map<String, String> suggestions = new HashMap<String, String>();
		findSuggestions(htmlProcessed, suggestions);

		// Get tokens from artist and song names
		List<String> tokensToFind = getArtistAndTitleTokens(artist, title);

		// Now find at map, a string that contains all artist and song tokens.
		// This will be the selected lyric
		return selectFromSuggestions(suggestions, tokensToFind, artist, title,
				operation);
	}

	private String selectFromSuggestions(final Map<String, String> suggestions,
			final List<String> tokensToFind, final String artist,
			final String title, final ILyricsRetrieveOperation operation) {
		for (Map.Entry<String, String> suggestion : suggestions.entrySet()) {
			boolean matches = checkSuggestion(tokensToFind, suggestion);
			if (matches) {
				// We have found it, build url and call again
				String auxUrl = SUGGESTIONS_URL.concat(suggestion.getValue());
				return getLyrics(auxUrl, artist, title, operation);
			}
		}
		// If we reach this code, no suggestion was found, so return null
		return null;
	}

	/**
	 * @param html
	 * @param suggestions
	 * @return
	 */
	private String findSuggestions(final String html,
			final Map<String, String> suggestions) {
		String htmlProcessed = html;
		while (htmlProcessed.indexOf("href=\"") != -1) {
			// Parse uri from html tag <a href="....
			String uri = htmlProcessed.substring(htmlProcessed
					.indexOf("href=\"") + 6);
			uri = uri.substring(0, uri.indexOf("\">"));
			// Parse suggestion text font color='white'>TEXT</font>
			String text = htmlProcessed.substring(htmlProcessed
					.indexOf("'white'>") + 8);
			text = text.substring(0, text.indexOf("</font>"));
			suggestions.put(text, uri);

			// Skip element
			htmlProcessed = htmlProcessed.substring(htmlProcessed
					.indexOf("</font>") + 11);
		}
		return htmlProcessed;
	}

	/**
	 * @param tokensToFind
	 * @param suggestion
	 * @return
	 */
	private boolean checkSuggestion(final List<String> tokensToFind,
			final Map.Entry<String, String> suggestion) {
		boolean matches = true;
		for (String t : tokensToFind) {
			if (!suggestion.getKey().toLowerCase().contains(t.toLowerCase())) {
				matches = false;
				break;
			}
		}
		return matches;
	}

	/**
	 * @param artist
	 * @param title
	 * @return
	 */
	private List<String> getArtistAndTitleTokens(final String artist,
			final String title) {
		List<String> tokensToFind = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(artist, " ");
		while (st.hasMoreTokens()) {
			String t = st.nextToken();
			if (validToken(t)) {
				tokensToFind.add(t);
			}
		}
		st = new StringTokenizer(title, " ");
		while (st.hasMoreTokens()) {
			String t = st.nextToken();
			if (validToken(t)) {
				tokensToFind.add(t);
			}
		}
		return tokensToFind;
	}

	@Override
	public ILyrics getLyricsFor(final String artist, final String title,
			final ILyricsRetrieveOperation operation) {
		// Build url
		String urlString = BASE_URL.replace(ARTIST_WILDCARD,
				encodeString(artist)).replace(SONG_WILDCARD,
				encodeString(title));
		// Call method to find lyrics
		String lyrics = getLyrics(urlString, artist, title, operation);
		return lyrics != null ? new Lyrics(lyrics, urlString) : null;
	}

	@Override
	public String getLyricsProviderName() {
		return "LyrcEngine";
	}

	@Override
	public String getUrlForAddingNewLyrics(final String artist,
			final String title) {
		return ADD_LYRICS_URL.replace(SONG_WILDCARD, encodeString(title))
				.replace(ARTIST_WILDCARD, encodeString(artist));
	}
}
