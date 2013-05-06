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
 * Engine for www.lyricsdir.com
 * 
 * @author alex
 * 
 */
public class LyricsDirectoryEngine extends AbstractLyricsEngine {

	/**
	 * Text showing lyrics are incomplete
	 */
	private static final String WE_ARE_CURRENTLY_UNABLE_TO_SHOW_YOU_THE_LYRICS = "We are currently unable to show you the lyrics";
	private static final String BASE_URL = "http://www.lyricsdir.com/";
	private static final String CHARSET = "UTF-8";

	@Override
	public ILyrics getLyricsFor(final String artist, final String title,
			final ILyricsRetrieveOperation operation) {
		StringBuilder sb = new StringBuilder();
		sb.append(BASE_URL);
		sb.append(encodeString(getString(artist, title)));
		sb.append("lyrics");
		sb.append(".html");

		try {
			String html = readURL(getConnection(sb.toString(), operation),
					CHARSET);

			String lyrics = null;
			Source source = new Source(html);
			List<Element> elements = source.getAllElements("div");
			for (Element element : elements) {
				if ("protected_lyrics".equals(element.getAttributeValue("id"))) {
					lyrics = element.getRenderer().toString();
				}
			}

			lyrics = reviewLyrics(lyrics);

			return lyrics != null && !lyrics.isEmpty() ? new Lyrics(lyrics,
					sb.toString()) : null;
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
	 * Test if lyrics are partial. If so, discard them
	 * 
	 * @param lyrics
	 * @return
	 */
	private String reviewLyrics(final String lyrics) {
		if (lyrics != null
				&& lyrics
						.contains(WE_ARE_CURRENTLY_UNABLE_TO_SHOW_YOU_THE_LYRICS)) {
			return null;
		}
		return lyrics;
	}

	private String getString(final String a, final String t) {
		StringBuilder sb = new StringBuilder();
		String artist = a.toLowerCase().trim();
		artist = artist.replaceAll("\\W", " ");
		String title = t.toLowerCase().trim();
		title = title.replaceAll("\\W", " ");
		for (String artistSplits : artist.split("\\s+")) {
			sb.append(artistSplits);
			sb.append("-");
		}
		for (String titleSplits : title.split(" ")) {
			sb.append(titleSplits);
			sb.append("-");
		}
		return sb.toString();
	}

	@Override
	public String getLyricsProviderName() {
		return "LyricsDirectory";
	}

	@Override
	public String getUrlForAddingNewLyrics(final String artist,
			final String title) {
		return "";
	}
}
