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
package net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines;

/**
 * Copyright Tehcon.org. All rights reserved.
 * 
 * tanacetum@gmail.com
 */
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
public class WinampcnEngine extends LyricsEngine {

    private static final String QUERY_URL = "http://www.winampcn.com/lyrictransfer/get.aspx?song=%1&artist=%2&lsong=%3";
    private static final String LYRC_URL = "http://www.winampcn.com/lyrictransfer/lrc.aspx?id=%1&ti=%2";

    private static Logger logger;

    public WinampcnEngine(Proxy proxy) {
        super(proxy);
    }

    @Override
    public Lyrics getLyricsFor(String artist, String title) {
        String url = getUrl(artist, title);
        try {
            String xml = readURL(getConnection(url), "gbk");
            String lyrcUrl = getMostPopularLyrcUrl(xml);
            if (lyrcUrl == null)
                return null;

            String lyrics = readURL(getConnection(lyrcUrl), "gbk");
            lyrics = lyrics.replaceAll("\\[.+\\]", "");
            return lyrics == null || lyrics.isEmpty() ? null : new Lyrics(lyrics, lyrcUrl);
        } catch (IOException e) {
            getLogger().error(LogCategories.SERVICE, "Cannot fetch lyrics for: " + artist + "/" + title);
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
        List<LyrcCandidate> lyrcCandidates = new LyrcXMLParser().parse(xml);
        LyrcCandidate tmp = null;
        for (LyrcCandidate candidate : lyrcCandidates) {
            if (tmp == null) {
                tmp = candidate;
            } else {
                tmp = candidate.downloadCount > tmp.downloadCount ? candidate : tmp;
            }
        }

        if (tmp != null) {
            return LYRC_URL.replace("%1", String.valueOf(tmp.id)).replace("%2", encodeUrl(tmp.songName));
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

    private static class LyrcCandidate {
        int id;
        int downloadCount;
        String songName;
        //String albumName;
    }

    private static class LyrcXMLParser extends DefaultHandler {
        List<LyrcCandidate> lyrcs = new ArrayList<LyrcCandidate>();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("LyricUrl")) {
                LyrcCandidate lyrc = new LyrcCandidate();
                lyrc.id = Integer.parseInt(attributes.getValue("id"));
                lyrc.songName = attributes.getValue("SongName");
                lyrc.downloadCount = Integer.parseInt(attributes.getValue("downloadtimes"));
                //lyrc.albumName = attributes.getValue("album");
                lyrcs.add(lyrc);
            }
        }

        public List<LyrcCandidate> parse(String xml) {
            try {
                SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
                parser.parse(new ByteArrayInputStream(xml.getBytes("gbk")), this);
            } catch (Exception e) {
                getLogger().error(LogCategories.SERVICE, "Cannot parse lyrics list from winampcn: " + e.getMessage());
            }
            return this.lyrcs;
        }
    }
    
    /**
     * Getter for logger
     * @return
     */
    private static Logger getLogger() {
    	if (logger == null) {
    		logger = new Logger();
    	}
    	return logger;
    }

}