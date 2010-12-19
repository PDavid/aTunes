/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.internetsearch;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.atunes.utils.StringUtils;

/**
 * A factory for creating Search objects.
 */
public final class SearchFactory {

    /** The searches. */
    private static HashMap<String, Search> searches;

    /** The you tube search. */
    private static Search youTubeSearch = new Search() {
        @Override
        public URL getURL(String query) throws MalformedURLException {
            String auxQuery = query.replaceAll(" +", "+");
            return new URL(StringUtils.getString("http://www.youtube.com/results?search_query=", auxQuery, "&search=Search"));
        }

        @Override
        public String toString() {
            return "YouTube";
        }
    };

    /** The wikipedia en search. */
    private static Search wikipediaENSearch = new Search() {
        @Override
        public URL getURL(String query) throws MalformedURLException {
            String auxQuery = query.replaceAll(" +", "_");
            return new URL(StringUtils.getString("http://en.wikipedia.org/wiki/", auxQuery));
        }

        @Override
        public String toString() {
            return "Wikipedia (English)";
        }
    };

    /** The free db search. */
    private static Search freeDBSearch = new Search() {
        @Override
        public URL getURL(String query) throws MalformedURLException {
            String auxQuery = query.replaceAll(" +", "+");
            return new URL(StringUtils.getString("http://www.freedb.org/freedb_search.php?words=", auxQuery));
        }

        @Override
        public String toString() {
            return "FreeDB";
        }
    };

    /** The music brainz search. */
    private static Search musicBrainzSearch = new Search() {
        @Override
        public URL getURL(String query) throws MalformedURLException {
            String auxQuery = query.replaceAll(" +", "+");
            String quotedQuery = StringUtils.getString("%22", auxQuery, "%22");
            return new URL(StringUtils.getString("http://musicbrainz.org/search/textsearch.html?query=", quotedQuery, "&type=artist&limit=&an=1&as=1&handlearguments=1"));
        }

        @Override
        public String toString() {
            return "MusicBrainz";
        }
    };

    /** The google video search. */
    private static Search googleVideoSearch = new Search() {
        @Override
        public URL getURL(String query) throws MalformedURLException {
            String auxQuery = query.replaceAll(" +", "+");
            return new URL(StringUtils.getString("http://video.google.com/videosearch?q=", auxQuery));
        }

        @Override
        public String toString() {
            return "Google Video";
        }
    };

    private SearchFactory() {

    }

    /**
     * Gets the searches.
     * 
     * @return the searches
     */
    public static List<Search> getSearches() {
        if (searches == null) {
            searches = new HashMap<String, Search>();
            searches.put(freeDBSearch.toString(), freeDBSearch);
            searches.put(googleVideoSearch.toString(), googleVideoSearch);
            searches.put(musicBrainzSearch.toString(), musicBrainzSearch);
            searches.put(wikipediaENSearch.toString(), wikipediaENSearch);
            searches.put(youTubeSearch.toString(), youTubeSearch);
        }
        return new ArrayList<Search>(searches.values());
    }

    /**
     * Gets the search for name.
     * 
     * @param searchName
     *            the search name
     * 
     * @return the search for name
     */
    public static Search getSearchForName(String searchName) {
        if (searches == null) {
            getSearches();
        }
        return searches.get(searchName);
    }
}
