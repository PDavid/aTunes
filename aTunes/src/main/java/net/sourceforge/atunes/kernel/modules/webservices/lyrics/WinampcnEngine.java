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

/**
 * Copyright Tehcon.org. All rights reserved.
 * 
 * tanacetum@gmail.com
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.ILyricsRetrieveOperation;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;


/**
 * A lyrics engine which retrieve lyrics from www.winamp.cn.
 * 
 * The query is a 2 phases process. <br/>
 * First, we send a quest using the artist name and song title, then the server
 * return an xml format search result include the all match lyrics. The xml look
 * like below:
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="gb2312"?&gt;
 * &lt;Lyric&gt;
 *   &lt;LyricUrl id="23598" Artist="xx" SongName="xx" downloadtimes="936" uploaduser="" album=""&gt;$lyricurl&lt;/LyricUrl&gt;
 *   &lt;LyricUrl id="111698" Artist="xx" SongName="xx" downloadtimes="562" uploaduser="" album=""&gt;$lyricurl&lt;/LyricUrl&gt;
 * &lt;/Lyric&gt;
 * </pre>
 * 
 * Then we can pick a lyric you want to download(this engine will download the
 * most downloaded lyric) using the $lyricurl. The lyric is in plain text format
 * and gbk encoding.
 * 
 * @author Taylor Tang
 */
public class WinampcnEngine extends AbstractLyricsEngine {

    private static final String QUERY_URL = "http://www.winampcn.com/lyrictransfer/get.aspx?song=%1&artist=%2&lsong=%3";
    private static final String LYRC_URL = "http://www.winampcn.com/lyrictransfer/lrc.aspx?id=%1&ti=%2";

    @Override
    public ILyrics getLyricsFor(String artist, String title, ILyricsRetrieveOperation operation) {
        String url = getUrl(artist, title);
        try {
            String xml = readURL(getConnection(url, operation), "gbk");
            String lyrcUrl = getMostPopularLyrcUrl(xml);
            if (lyrcUrl == null) {
                return null;
            }                

            String lyrics = readURL(getConnection(lyrcUrl, operation), "gbk");
            lyrics = lyrics.replaceAll("\\[.+\\]", "");
            return lyrics == null || lyrics.isEmpty() ? null : new Lyrics(lyrics, lyrcUrl);
        } catch (IOException e) {
        	Logger.error(StringUtils.getString(e.getClass().getCanonicalName(), " (", e.getMessage(), ")"));
            return null;
        }
    }

    /**
     * Pick the most downloaded lyric, and return its url.
     * 
     * @param xml
     * @return
     */
    private String getMostPopularLyrcUrl(String xml) {
        List<WinampcnEngineLyrcCandidate> lyrcCandidates = new WinampcnEngineLyrcXMLParser().parse(xml);
        WinampcnEngineLyrcCandidate tmp = null;
        for (WinampcnEngineLyrcCandidate candidate : lyrcCandidates) {
            if (tmp == null) {
                tmp = candidate;
            } else {
                tmp = candidate.getDownloadCount() > tmp.getDownloadCount() ? candidate : tmp;
            }
        }

        if (tmp != null) {
            return LYRC_URL.replace("%1", String.valueOf(tmp.getId())).replace("%2", encodeUrl(tmp.getSongName()));
        }

        return null;
    }

    /**
     * Construct a url from artist name and song title. We use this url to
     * request lyrics from server.
     * 
     * @param artist
     * @param title
     * @return
     */
    private String getUrl(String artist, String title) {
        return QUERY_URL.replace("%1", encodeUrl(title)).replace("%2", encodeUrl(artist)).replace("%3", encodeUrl(title));
    }

    /**
     * Encode the url using gbk encoding, because the server can only accept gbk
     * encoding.
     * 
     * @param title
     * @return
     */
    private CharSequence encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "gbk");
        } catch (UnsupportedEncodingException e) {
            return url;
        }
    }

    @Override
    public String getLyricsProviderName() {
        return "winampcn.com";
    }

    @Override
    public String getUrlForAddingNewLyrics(String artist, String title) {
        return "";
    }
}