/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmLovedTrack;
import net.sourceforge.atunes.utils.NetworkUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class retrieves LastFm loved tracks page and parses it to get a list of
 * loved tracks
 * 
 * @author fleax
 * 
 */
public final class LastFmLovedTracks {

    private static final String USER_WILDCARD = "(%USER%)";

    private static final String LOVED_TRACKS_BASE_URL = StringUtils.getString("http://www.lastfm.es/user/", USER_WILDCARD, "/library/loved");

    private static final String LOVED_TRACK_TD_START = "<td class=\"subjectCell\">";

    private static final String LOVED_TRACK_TD_END = "</td>";

    private static final String A = "<a href=\".*\">";

    private static final String A_START = A.substring(0, 2);

    private static final String A_END = "</a>";

    private static final String NEXT_LINK_CLASS = "class=\"nextlink\"";

    private LastFmLovedTracks() {

    }

    /**
     * Returns a list of loved tracks of given user. Params are used for
     * pagination and must be null in first call
     * 
     * @param user
     * @param params
     * @param proxy
     * @return
     * @throws IOException
     */
    static List<LastFmLovedTrack> getLovedTracks(String user, String params, Proxy proxy) throws IOException {
        List<LastFmLovedTrack> result = new ArrayList<LastFmLovedTrack>();
        String url = LOVED_TRACKS_BASE_URL.replace(USER_WILDCARD, user);
        // If params is not null then concatenate string
        if (params != null && !params.trim().equals("")) {
            url = StringUtils.getString(url, "?", params);
        }
        // Get content
        String content = NetworkUtils.readURL(NetworkUtils.getConnection(url, proxy));

        // Start parsing page
        int cursor = 0;
        // Parse while there is some TD element containing a loved track
        while (content.indexOf(LOVED_TRACK_TD_START, cursor) != -1) {
            // Move to the start of TD
            cursor = content.indexOf(LOVED_TRACK_TD_START, cursor);

            // TD contains two A tags...

            // First A tag is artist name
            cursor = content.indexOf(A_START, cursor);
            // Get full A tag, then get content
            String artist = content.substring(cursor, content.indexOf(A_END, cursor));
            artist = artist.replaceFirst(A, "");
            cursor = content.indexOf(A_END, cursor);

            // Second A tag is title
            cursor = content.indexOf(A_START, cursor);
            // Get full A tag, then get content
            String title = content.substring(cursor, content.indexOf(A_END, cursor));
            title = title.replaceFirst(A, "");
            cursor = content.indexOf(A_END, cursor);

            // Add loved track to list and move cursor
            result.add(new LastFmLovedTrack(processString(artist), processString(title)));
            cursor = content.indexOf(LOVED_TRACK_TD_END, cursor);
        }

        // End of page: move until "pagination" and try to follow "next" link

        if (content.indexOf(NEXT_LINK_CLASS, cursor) != -1) {
            // Start searching a tags until some contains "nextlink"
            String link = null;
            do {
                int aStartIndex = content.indexOf(A_START, cursor);
                int aEndIndex = content.indexOf(A_END, aStartIndex);
                if (aStartIndex >= 0 && aEndIndex >= 0) {
                    link = content.substring(aStartIndex, aEndIndex);
                    cursor = aEndIndex;
                } else {
                    link = null;
                }
            } while (link != null && !link.contains(NEXT_LINK_CLASS));
            if (link != null) {
                // If we found a link to next page follow it
                int paramStart = link.indexOf('?');
                link = link.substring(paramStart + 1, link.indexOf('"', paramStart));
                link = StringUtils.unescapeHTML(link, 0);
                result.addAll(getLovedTracks(user, link, proxy));
            }
        }
        return result;
    }

    /**
     * Removes \n \r \t from a string
     * 
     * @param str
     * @return
     */
    private static String processString(String str) {
        return str.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim();
    }
}
