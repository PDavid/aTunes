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

package net.sourceforge.atunes.model;

import java.awt.Image;
import java.io.IOException;
import java.net.URLConnection;
import java.util.Map;

/**
 * Responsible of managing network connections
 * 
 * @author alex
 * 
 */
public interface INetworkHandler extends IHandler {

    /**
     * @param proxy
     */
    public void updateProxy(IProxyBean proxy);

    /**
     * Encodes a string in a format suitable to send a http request.
     * 
     * @param s
     *            The String that should be encoded
     * 
     * @return A suitable encoded String
     */
    public String encodeString(String s);

    /**
     * Returns a HttpURLConnection specified by a given URL
     * 
     * @param urlString
     *            A URL as String
     * 
     * @return A HttpURLConnection
     * 
     * @throws IOException
     *             If an IO exception occurs
     */
    public URLConnection getConnection(String urlString) throws IOException;

    /**
     * Reads a Image from a given URLConnection.
     * 
     * @param connection
     *            A URLConnection
     * 
     * @return The Image that was read from the URLConnection
     * 
     * @throws IOException
     *             If an IO exception occurs
     */
    public Image getImage(URLConnection connection) throws IOException;

    /**
     * Reads a String from a given URLConnection.
     * 
     * @param connection
     *            A URLConnection
     * 
     * @return A String read from a given URLConnection
     * 
     * @throws IOException
     *             If an IO exception occurs
     */
    public String readURL(URLConnection connection) throws IOException;

    /**
     * Reads a given number of bytes and returns a String from a given
     * URLConnection.
     * 
     * @param connection
     * @param bytes
     * @return
     * @throws IOException
     */
    public String readURL(URLConnection connection, int bytes)
	    throws IOException;

    /**
     * Reads a String from a given URLConnection with a given charset encoding.
     * 
     * @param connection
     *            A URLConnection
     * @param charset
     *            A charset as String, e.g. "UTF-8"
     * 
     * @return A String read from a given URLConnection
     * 
     * @throws IOException
     *             If an IO exception occurs
     */
    public String readURL(URLConnection connection, String charset)
	    throws IOException;

    /**
     * Sends a POST request with given parameters and receives response with
     * given charset
     * 
     * @param requestURL
     * @param params
     * @param charset
     * @return
     * @throws IOException
     */
    public String readURL(final String requestURL,
	    final Map<String, String> params, final String charset)
	    throws IOException;
}