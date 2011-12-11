/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.network;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IProxyBean;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;

import org.apache.commons.io.IOUtils;

import de.umass.lastfm.Caller;

/**
 * Responsible of handling network connections
 * @author alex
 *
 */
public class NetworkHandler extends AbstractHandler implements INetworkHandler {

	@Override
	protected void initHandler() {
		updateProxy(getState().getProxy());
	}
	
	/**
	 * @param proxy
	 */
	@Override
	public void updateProxy(IProxyBean proxy) {
        try {
    		ExtendedProxy extendedProxy = ExtendedProxy.getProxy(proxy);
            ExtendedProxy.initProxy(extendedProxy);
            
            // Necessary for last.fm
            Caller.getInstance().setProxy(extendedProxy);
        } catch (UnknownHostException e) {
            Logger.error(e);
        } catch (IOException e) {
            Logger.error(e);
        }
	}

    /**
     * Encodes a string in a format suitable to send a http request.
     * 
     * @param s
     *            The String that should be encoded
     * 
     * @return A suitable encoded String
     */
    @Override
	public String encodeString(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

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
    @Override
	public URLConnection getConnection(String urlString) throws IOException {
        Logger.debug("Opening Connection With: ", urlString);

        URL url = new URL(urlString);

        URLConnection connection;
        
        ExtendedProxy proxy = ExtendedProxy.getProxy(getState().getProxy());
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
    @Override
	public Image getImage(URLConnection connection) throws IOException {
        InputStream input = null;
        try {
            input = connection.getInputStream();
            return ImageIO.read(input);
        } finally {
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
    @Override
	public String readURL(URLConnection connection) throws IOException {
        InputStream input = null;
        try {
            input = connection.getInputStream();
            return IOUtils.toString(input);            
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
    @Override
	public String readURL(URLConnection connection, String charset) throws IOException {
        InputStream input = null;
        try {
            input = connection.getInputStream();
            return IOUtils.toString(input, charset);
        } finally {
            ClosingUtils.close(input);
        }
    }
}
