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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.ILyricsRetrieveOperation;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Engine to retrieve lyrics from LyrDB: http://www.lyrdb.com/ by Flavio
 * González Vázquez
 * 
 * @author fleax
 * 
 */
public class LyrDBEngine extends AbstractLyricsEngine {

	private static final String QUERY_WILDCARD = "(%QUERY%)";
	private static final String LYRICS_ID_WILDCARD = "(%ID%)";
	private static final String AGENT = Constants.APP_NAME;

	private static final String FOOTER = "\nLyrics provided by LyrDB (www.lyrdb.com) and Flavio González Vázquez";

	/** The search url. */
	private static final String SEARCH_URL = StringUtils.getString(
			"http://www.lyrdb.com/lookup.php?q=", QUERY_WILDCARD,
			"&for=match&agent=", AGENT);

	/** The url to retrieve a lyric */
	private static final String LYRIC_URL = StringUtils.getString(
			"http://www.lyrdb.com/getlyr.php?q=", LYRICS_ID_WILDCARD);

	@Override
	public ILyrics getLyricsFor(String artist, String title,
			ILyricsRetrieveOperation operation) {
		ILyrics lyrics = null;
		try {
			// Build url and search
			String urlString = SEARCH_URL.replace(QUERY_WILDCARD, StringUtils
					.getString(encodeString(artist), "|", encodeString(title))); // "|"
																					// can't
																					// be
																					// encoded
			String searchResult = readURL(getConnection(urlString, operation),
					"UTF-8");

			// Parse result
			BufferedReader br = new BufferedReader(new StringReader(
					searchResult));
			String line = br.readLine();
			while (line != null && lyrics == null) {
				lyrics = retrieveSearchResult(line, operation);
				line = br.readLine();
			}
			br.close();

		} catch (IOException e) {
			Logger.error(StringUtils.getString(e.getClass().getCanonicalName(),
					" (", e.getMessage(), ")"));
		}
		return lyrics;
	}

	/**
	 * Retrieves lyric for a search result
	 * 
	 * @param searchResult
	 * @param operation
	 * @return
	 * @throws IOException
	 */
	private ILyrics retrieveSearchResult(String searchResult,
			ILyricsRetrieveOperation operation) throws IOException {
		int firstSlash = searchResult.indexOf('\\');
		if (firstSlash != -1) {
			String id = searchResult.substring(0, firstSlash);
			// Build url for ID
			String urlString = LYRIC_URL.replace(LYRICS_ID_WILDCARD, id);
			String lyric = readURL(getConnection(urlString, operation), "UTF-8");
			if (lyric != null) {
				// Remove carriage return
				lyric = lyric.replace("\r\r", "");
				lyric = appendFooter(lyric);
				return new Lyrics(lyric, urlString);
			}
		}
		return null;
	}

	private String appendFooter(String lyric) {
		return StringUtils.getString(lyric, FOOTER);
	}

	@Override
	public String getLyricsProviderName() {
		return "LyrDB";
	}

	@Override
	public String getUrlForAddingNewLyrics(String artist, String title) {
		return "http://www.lyrdb.com/submit.php";
	}
}
