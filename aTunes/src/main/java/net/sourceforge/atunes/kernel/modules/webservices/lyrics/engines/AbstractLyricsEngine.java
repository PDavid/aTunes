/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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
import java.net.URLConnection;
import java.net.UnknownHostException;

import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;
import net.sourceforge.atunes.utils.NetworkUtils;

/**
 * The base class for lyrics engines.
 */
public abstract class AbstractLyricsEngine {

    private Proxy proxy;

    public AbstractLyricsEngine(Proxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Encode string.
     * 
     * @param str
     *            the str
     * 
     * @return the string
     */
    protected final static String encodeString(String str) {
        return NetworkUtils.encodeString(str);
    }

    /**
     * Gets the connection.
     * 
     * @param url
     *            the url
     * 
     * @return the connection
     * 
     * @throws UnknownHostException
     *             the unknown host exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected final URLConnection getConnection(String url) throws UnknownHostException, IOException {
        return NetworkUtils.getConnection(url, proxy);
    }

    /**
     * Read url.
     * 
     * @param connection
     *            the connection
     * @param charset
     *            the charset
     * 
     * @return the string
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    protected final static String readURL(URLConnection connection, String charset) throws IOException {
        return NetworkUtils.readURL(connection, charset);
    }

    /**
     * Gets the lyrics for.
     * 
     * @param artist
     *            the artist
     * @param title
     *            the title
     * 
     * @return the lyrics for
     */
    public abstract Lyrics getLyricsFor(String artist, String title);

    /**
     * Returns the name of this lyrics provider
     * 
     * @return the name of this lyrics provider
     */
    public abstract String getLyricsProviderName();

    /**
     * Returns the url for adding new lyrics
     * 
     * @param artist
     *            the artist
     * @param title
     *            the title
     * @return the url for adding new lyrics
     */
    public abstract String getUrlForAddingNewLyrics(String artist, String title);

}
