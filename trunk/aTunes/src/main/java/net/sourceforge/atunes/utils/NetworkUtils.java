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
package net.sourceforge.atunes.utils;

import java.awt.Image;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

import org.commonjukebox.plugins.PluginApi;

/**
 * Utility methods for network access.
 */
@PluginApi
public final class NetworkUtils {

    private static Logger logger;

    private NetworkUtils() {
    }

    /**
     * Encodes a string in a format suitable to send a http request.
     * 
     * @param s
     *            The String that should be encoded
     * 
     * @return A suitable encoded String
     */
    public static String encodeString(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    /**
     * Returns a HttpURLConnection specified by a given URL and Proxy.
     * 
     * @param urlString
     *            A URL as String
     * @param proxy
     *            A proxy
     * 
     * @return A HttpURLConnection
     * 
     * @throws IOException
     *             If an IO exception occurs
     */
    public static URLConnection getConnection(String urlString, Proxy proxy) throws IOException {
        getLogger().debug(LogCategories.NETWORK, "Opening Connection With: ", urlString);

        URL url = new URL(urlString);

        URLConnection connection;
        if (proxy == null) {
            connection = url.openConnection();
        } else {
            connection = url.openConnection(proxy);
        }
        connection.setRequestProperty("User-agent", "");
        connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);
        return connection;
    }

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
    public static Image getImage(URLConnection connection) throws IOException {
        InputStream input = null;
        try {
            input = connection.getInputStream();
            return ImageIO.read(input);
        } finally {
            ClosingUtils.close(input);
        }
    }

    /**
     * Sends a HTTP-Post query to a given URLConnection and reads and returns
     * the answer.
     * 
     * @param connection
     *            A URLConnection
     * @param post
     *            The HTTP-Post query
     * 
     * @return The answer of the query
     * 
     * @throws IOException
     *             If an IO exception occurs
     */
    public static String readPostURL(HttpURLConnection connection, String post) throws IOException {
        DataOutputStream writer = null;
        InputStream input = null;
        try {
            OutputStream out = null;
            out = connection.getOutputStream();
            writer = new DataOutputStream(out);
            writer.writeBytes(post);
            writer.flush();

            if (connection.getResponseCode() != 200) {
                throw new IOException("Invalid HTTP return code");
            }

            StringBuilder builder = new StringBuilder();
            input = connection.getInputStream();
            byte[] array = new byte[1024];
            int read;
            while ((read = input.read(array)) > 0) {
                builder.append(new String(array, 0, read, "UTF-8"));
            }
            return builder.toString();
        } finally {
            ClosingUtils.close(writer);
            ClosingUtils.close(input);
        }
    }

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
    public static String readURL(URLConnection connection) throws IOException {
        InputStream input = null;
        try {
            StringBuilder builder = new StringBuilder();
            input = connection.getInputStream();
            byte[] array = new byte[1024];
            int read;
            while ((read = input.read(array)) > 0) {
                builder.append(new String(array, 0, read, "UTF-8"));
            }
            return builder.toString();
        } finally {
            ClosingUtils.close(input);
        }
    }

    /**
     * Reads a String of n bytes from a given URLConnection.
     * 
     * @param connection
     *            A URLConnection
     * 
     * @param n
     *            Number of bytes
     * 
     * @return A String read from a given URLConnection
     * 
     * @throws IOException
     *             If an IO exception occurs
     */
    public static String readURL(URLConnection connection, int n) throws IOException {
        InputStream input = null;
        try {
            input = connection.getInputStream();
            byte[] array = new byte[n];
            int read = input.read(array);
            return new String(array, 0, read, "UTF-8");
        } finally {
            ClosingUtils.close(input);
        }
    }

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
    public static String readURL(URLConnection connection, String charset) throws IOException {
        InputStream input = null;
        try {
            StringBuilder builder = new StringBuilder();
            input = connection.getInputStream();
            byte[] array = new byte[1024];
            int read;
            while ((read = input.read(array)) > 0) {
                builder.append(new String(array, 0, read, charset));
            }
            return builder.toString();
        } finally {
            ClosingUtils.close(input);
        }
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private static Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}
