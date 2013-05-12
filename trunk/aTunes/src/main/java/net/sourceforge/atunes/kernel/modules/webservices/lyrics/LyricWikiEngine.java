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
import java.net.UnknownHostException;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.ILyricsRetrieveOperation;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The lyric engine for LyricWiki.
 */
public class LyricWikiEngine extends AbstractLyricsEngine {

	/**
	 * Text showing lyrics are incomplete
	 */
	private static final String UNFORTUNATELY_WE_ARE_NOT_LICENSED_TO_DISPLAY_THE_FULL_LYRICS = "Unfortunately, we are not licensed to display the full lyrics";

	private static final String ARTIST_PATTERN = "%artist";
	private static final String TITLE_PATTERN = "%title";
	/**
	 * URL pattern to retrieve lyrics
	 */
	private static final String URL = "http://lyrics.wikia.com/"
			+ ARTIST_PATTERN + ":" + TITLE_PATTERN;
	private static final String LYRICS_ELEMENT = "div";
	private static final String LYRICS_ELEMENT_ATTRIBUTE = "class";
	private static final String LYRICS_ELEMENT_ATTRIBUTE_VALUE = "lyricbox";
	private static final String RESPONSE_ENCODING = "UTF-8";

	/**
	 * Artists and songs with spaces are replaced with an underscore "_" and the
	 * first character of every word is converted to uppercase.
	 * 
	 * For more information on this see:
	 * http://lyrics.wikia.com/LyricWiki:Page_Names#Capitalization
	 * 
	 * @param str
	 *            the string to convert
	 * 
	 * @return the string for the wiki url
	 */
	private static String getStringForWikiURL(final String str) {
		StringBuilder b = new StringBuilder();
		for (String s : str.split(" ")) {
			b.append(StringUtils.convertFirstCharacterToUppercase(s));
			b.append(" ");
		}
		return b.toString().trim().replace(" ", "_");
	}

	/**
	 * Return the url of a audio file for LyricWiki
	 * 
	 * @param artist
	 *            the artist
	 * @param title
	 *            the title
	 * @return the url of a audio file for LyricWiki
	 */
	private String getURL(final String artist, final String title) {
		String queryString = URL;

		// This provider waits for '_' instead of regular '+' for spaces in URL
		String formattedArtist = getStringForWikiURL(artist);
		String formattedTitle = getStringForWikiURL(title);

		queryString = queryString.replace(ARTIST_PATTERN,
				encodeString(formattedArtist));
		queryString = queryString.replace(TITLE_PATTERN,
				encodeString(formattedTitle));

		return queryString;
	}

	@Override
	public ILyrics getLyricsFor(final String artist, final String title,
			final ILyricsRetrieveOperation operation) {
		try {
			String url = getURL(artist, title);
			String html = readURL(getConnection(url, operation),
					RESPONSE_ENCODING);
			String lyrics = extractLyrics(html);

			lyrics = reviewLyrics(lyrics);

			return lyrics != null && !lyrics.isEmpty() ? new Lyrics(lyrics, url)
					: null;
		} catch (UnknownHostException e) {
			Logger.error(StringUtils.getString(e.getClass().getCanonicalName(),
					" (", e.getMessage(), ")"));
			return null;
		} catch (IOException e) {
			Logger.error(StringUtils.getString(e.getClass().getCanonicalName(),
					" (", e.getMessage(), ")"));
			return null;
		}

	}

	/**
	 * Extracts lyrics from the HTML page.
	 * 
	 * @return the extracted lyrics
	 */
	private String extractLyrics(final String html) {
		String lyrics = null;
		Source source = new Source(html);
		List<Element> elements = source.getAllElements(LYRICS_ELEMENT);
		for (Element element : elements) {
			if (LYRICS_ELEMENT_ATTRIBUTE_VALUE.equals(element
					.getAttributeValue(LYRICS_ELEMENT_ATTRIBUTE))) {
				// Get all content
				lyrics = element.getRenderer().toString();
				List<Element> subElements = element.getChildElements();
				for (Element element2 : subElements) {
					// Remove ring tones links
					// element toString is slightly different from main element
					// toString (it contains a white space after ">" so
					// in order to remove ocurrences from main element we must
					// first replace "> " with ">"
					if ("rtMatcher".equals(element2
							.getAttributeValue(LYRICS_ELEMENT_ATTRIBUTE))) {
						lyrics = lyrics.replace(element2.getRenderer()
								.toString().replace("> ", ">").trim(), "");
					}
				}
				return lyrics;
			}
		}
		return lyrics;
	}

	/**
	 * Test if lyrics are partial. If so, discard them
	 * 
	 * @param lyrics
	 * @return
	 */
	private String reviewLyrics(final String lyrics) {
		if (lyrics != null
				&& lyrics
						.contains(UNFORTUNATELY_WE_ARE_NOT_LICENSED_TO_DISPLAY_THE_FULL_LYRICS)) {
			return null;
		}
		return lyrics;
	}

	@Override
	public String getLyricsProviderName() {
		return "LyricWiki";
	}

	@Override
	public String getUrlForAddingNewLyrics(final String artist,
			final String title) {
		return getURL(artist, title);
	}
}
