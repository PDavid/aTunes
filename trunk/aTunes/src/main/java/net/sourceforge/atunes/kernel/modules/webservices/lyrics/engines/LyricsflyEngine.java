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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;

import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.CryptoUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

import org.w3c.dom.Document;

/**
 * See API documentation: <a
 * href="http://www.lyricsfly.com/api/">http://www.lyricsfly.com/api/</a>
 */
public class LyricsflyEngine extends LyricsEngine {

    private static final int RETRY_COUNT = 3;
    private static final int SLEEP_TIME = 1500;
    private static final String LIMITED_TIME_STATUS = "402";

    private Logger logger;

    public LyricsflyEngine(Proxy proxy) {
        super(proxy);
    }

    @Override
    public Lyrics getLyricsFor(String artist, String title) {
        try {
            Document xmlDocument = connectAndRead(artist, title);
            if (xmlDocument != null) {
                String lyrics = XMLUtils.getChildElementContent(xmlDocument.getDocumentElement(), "tx");
                lyrics = lyrics.replace("[br]", "\n");
                return lyrics.trim().isEmpty() ? null : new Lyrics(lyrics, "http://lyricsfly.com/");
            } else {
                return null;
            }
        } catch (UnknownHostException e) {
            getLogger().error(LogCategories.SERVICE, e);
        } catch (IOException e) {
            getLogger().error(LogCategories.SERVICE, e);
        } catch (GeneralSecurityException e) {
            getLogger().error(LogCategories.SERVICE, e);
        }
        return null;
    }

    private Document connectAndRead(String artist, String title) throws GeneralSecurityException, IOException, UnknownHostException {
        String status;
        int c = 0;
        HttpURLConnection connection = null;
        Document xmlResponse = null;
        do {
            if (connection != null) {
                connection.disconnect();
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    getLogger().error(LogCategories.SERVICE, e);
                }
            }
            String url = getUrl(artist, title);
            connection = (HttpURLConnection) getConnection(url);

            xmlResponse = XMLUtils.getXMLDocument(readURL(connection, "UTF-8"));
            status = extractStatus(xmlResponse);
        } while (LIMITED_TIME_STATUS.equals(status) && c++ < RETRY_COUNT);
        return xmlResponse;
    }

    private String extractStatus(Document xmlDocument) {
        if (xmlDocument == null) {
            return "-1";
        } else {
            String status = XMLUtils.evaluateXPathExpressionAndReturnString("//status/text()", xmlDocument);
            if (status != null) {
                status = status.trim();
            }
            return status;
        }
    }

    private String getUrl(String artist, String title) throws GeneralSecurityException, IOException {
        return StringUtils.getString("http://api.lyricsfly.com/api/api.php?i=" + new String(CryptoUtils.decrypt(C)), "&a=", encodeString(artist), "&t=", encodeString(title));
    }

    @Override
    public String getLyricsProviderName() {
        return "Lyricsfly";
    }

    @Override
    public String getUrlForAddingNewLyrics(String artist, String title) {
        return "http://lyricsfly.com/submit/";
    }

    /*
     * DO NOT USE THIS KEY FOR OTHER APPLICATIONS THAN aTunes!
     */
    private static final byte[] C = { -113, 48, 87, 26, 59, 127, 85, -7, -72, 92, 78, -96, -25, 46, -16, 112, -104, 20, -78, 126, 26, 13, -40, 46 };

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}
