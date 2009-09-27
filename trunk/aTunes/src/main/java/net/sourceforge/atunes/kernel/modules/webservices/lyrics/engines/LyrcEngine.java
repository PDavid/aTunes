/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class LyrcEngine.
 */
public class LyrcEngine extends LyricsEngine {

    private static Logger logger = new Logger();

    private static final String ARTIST_WILDCARD = "(%ARTIST%)";
    private static final String SONG_WILDCARD = "(%SONG%)";

    /** The base url. */
    private static final String BASE_URL = StringUtils.getString("http://www.lyrc.com.ar/en/tema1en.php?artist=", ARTIST_WILDCARD, "&songname=", SONG_WILDCARD);

    /** The suggestions url. */
    private static final String SUGGESTIONS_URL = "http://www.lyrc.com.ar/en/";

    /** The lyrics adding url. */
    private static final String ADD_LYRICS_URL = StringUtils.getString("http://www.lyrc.com.ar/en/add/add.php?tema=", SONG_WILDCARD, "&grupo=", ARTIST_WILDCARD);

    public LyrcEngine(Proxy proxy) {
        super(proxy);
    }

    /**
     * Returns if a string is composed only by letters.
     * 
     * @param t
     *            the t
     * 
     * @return true, if valid token
     */
    private static boolean validToken(String t) {
        return t.matches("[A-Za-z]+");
    }

    private String getLyrics(String url, String artist, String title) {
        try {
            // read html return
            String html = readURL(getConnection(url), "ISO-8859-1");

            if (html.contains("Suggestions : <br>")) { // More than one posibility, find the best one
                logger.debug(LogCategories.SERVICE, new String[] { "Suggestions found" });

                html = html.substring(html.indexOf("Suggestions : <br>"));
                html = html.substring(0, html.indexOf("<br><br"));

                // Find suggestions and add to a map
                Map<String, String> suggestions = new HashMap<String, String>();
                while (html.indexOf("href=\"") != -1) {
                    // Parse uri from html tag <a href="....
                    String uri = html.substring(html.indexOf("href=\"") + 6);
                    uri = uri.substring(0, uri.indexOf("\">"));
                    // Parse suggestion text font color='white'>TEXT</font>
                    String text = html.substring(html.indexOf("'white'>") + 8);
                    text = text.substring(0, text.indexOf("</font>"));
                    suggestions.put(text, uri);

                    // Skip element
                    html = html.substring(html.indexOf("</font>") + 11);
                }

                // Get tokens from artist and song names
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

                // Now find at map, a string that contains all artist and song tokens. This will be the selected lyric
                for (Map.Entry<String, String> suggestion : suggestions.entrySet()) {
                    boolean matches = true;
                    for (String t : tokensToFind) {
                        if (!suggestion.getKey().toLowerCase().contains(t.toLowerCase())) {
                            matches = false;
                            break;
                        }
                    }
                    if (matches) {
                        // We have found it, build url and call again
                        logger.debug(LogCategories.SERVICE, new String[] { "Found suggestion", suggestion.getKey() });

                        String auxUrl = SUGGESTIONS_URL.concat(suggestion.getValue());
                        return getLyrics(auxUrl, artist, title);
                    }
                }

                logger.debug(LogCategories.SERVICE, new String[] { "No suitable suggestion found" });
                // If we reach this code, no suggestion was found, so return null
                return null;
            }

            // Remove html before lyrics
            html = html.substring(html.indexOf("</table>") + 8);

            // Remove html after lyrics
            int pPos = html.indexOf("<p>");
            int brPos = html.indexOf("<br>");

            if (pPos == -1) {
                pPos = Integer.MAX_VALUE;
            }

            if (brPos == -1) {
                brPos = Integer.MAX_VALUE;
            }

            html = html.substring(0, pPos < brPos ? pPos : brPos);

            // Remove <br/>
            html = html.replaceAll("<br />", "");

            // Bad parsing....
            if (html.contains("<head>")) {
                return null;
            }

            return html;
        } catch (Exception e) {
            logger.error(LogCategories.SERVICE, e);

            return null;
        }

    }

    @Override
    public Lyrics getLyricsFor(String artist, String title) {
        // Build url
        String urlString = BASE_URL.replace(ARTIST_WILDCARD, encodeString(artist)).replace(SONG_WILDCARD, encodeString(title));
        // Call method to find lyrics
        String lyrics = getLyrics(urlString, artist, title);
        return lyrics != null ? new Lyrics(lyrics, urlString) : null;
    }

    @Override
    public String getLyricsProviderName() {
        return "LyrcEngine";
    }

    @Override
    public String getUrlForAddingNewLyrics(String artist, String title) {
        return ADD_LYRICS_URL.replace(SONG_WILDCARD, encodeString(title)).replace(ARTIST_WILDCARD, encodeString(artist));
    }

}
