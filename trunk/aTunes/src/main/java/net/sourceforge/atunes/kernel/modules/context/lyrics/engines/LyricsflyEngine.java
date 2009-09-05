/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.kernel.modules.context.lyrics.engines;

import java.io.IOException;
import java.net.UnknownHostException;

import net.sourceforge.atunes.kernel.modules.context.Lyrics;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.w3c.dom.Document;

/**
 * See API documentation: <a
 * href="http://www.lyricsfly.com/api/">http://www.lyricsfly.com/api/</a>
 */
public class LyricsflyEngine extends LyricsEngine {

    static Logger logger = new Logger();

    /*
     * DO NOT USE THIS KEY FOR OTHER APPLICATIONS THAN aTunes!
     */
    private static final String USER_ID = "d34b3dc1dc7faf2a4-atunes.org";

    public LyricsflyEngine(Proxy proxy) {
        super(proxy);
    }

    @Override
    public Lyrics getLyricsFor(String artist, String title) {
        try {
            String xml = readURL(getConnection(getUrl(artist, title)), "UTF-8");
            Document xmlDocument = XMLUtils.getXMLDocument(xml);
            if (xmlDocument == null) {
                return null;
            }
            String lyrics = XMLUtils.getChildElementContent(xmlDocument.getDocumentElement(), "tx");
            lyrics = lyrics.replace("[br]", "\n");
            return lyrics.trim().isEmpty() ? null : new Lyrics(lyrics, "http://lyricsfly.com/");
        } catch (UnknownHostException e) {
            logger.error(LogCategories.SERVICE, e);
        } catch (IOException e) {
            logger.error(LogCategories.SERVICE, e);
        }
        return null;
    }

    private String getUrl(String artist, String title) {
        return StringUtils.getString("http://lyricsfly.com/api/api.php?i=" + USER_ID, "&a=", encodeString(artist), "&t=", encodeString(title));
    }

    @Override
    public String getLyricsProviderName() {
        return "Lyricsfly";
    }

    @Override
    public String getUrlForAddingNewLyrics(String artist, String title) {
        return "http://lyricsfly.com/submit/";
    }

}
