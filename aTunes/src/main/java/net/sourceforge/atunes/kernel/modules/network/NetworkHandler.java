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

package net.sourceforge.atunes.kernel.modules.network;

import java.awt.Image;
import java.awt.color.CMMException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IProxyBean;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.IOUtils;

import de.umass.lastfm.Caller;

/**
 * Responsible of handling network connections
 * 
 * @author alex
 * 
 */
/**
 * @author alex
 * 
 */
public class NetworkHandler extends AbstractHandler implements INetworkHandler {

	private IStateCore stateCore;

	private int connectTimeoutInSeconds;

	private int readTimeoutInSeconds;

	/**
	 * @param connectTimeoutInSeconds
	 */
	public void setConnectTimeoutInSeconds(final int connectTimeoutInSeconds) {
		this.connectTimeoutInSeconds = connectTimeoutInSeconds;
	}

	/**
	 * @param readTimeoutInSeconds
	 */
	public void setReadTimeoutInSeconds(final int readTimeoutInSeconds) {
		this.readTimeoutInSeconds = readTimeoutInSeconds;
	}

	/**
	 * @param stateCore
	 */
	public void setStateCore(final IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	@Override
	protected void initHandler() {
		updateProxy(stateCore.getProxy());
	}

	/**
	 * @param proxy
	 */
	@Override
	public void updateProxy(final IProxyBean proxy) {
		try {
			ExtendedProxy extendedProxy = ExtendedProxy.getProxy(proxy);
			initProxy(extendedProxy);

			// Necessary for last.fm
			Caller.getInstance().setProxy(extendedProxy);
		} catch (UnknownHostException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	/**
	 * Initializes proxy authenticator
	 * 
	 * @param proxy
	 */
	private void initProxy(final ExtendedProxy proxy) {
		if (proxy != null) {
			Authenticator.setDefault(new ProxyAuthenticator(proxy));
		} else {
			Authenticator.setDefault(null);
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
	public String encodeString(final String s) {
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
	public URLConnection getConnection(final String urlString)
			throws IOException {
		Logger.debug("Opening Connection With: ", urlString);

		URL url = new URL(urlString);

		URLConnection connection;

		ExtendedProxy proxy = ExtendedProxy.getProxy(stateCore.getProxy());
		if (proxy == null) {
			connection = url.openConnection();
		} else {
			connection = url.openConnection(proxy);
		}
		connection.setRequestProperty("User-agent", Constants.APP_NAME);
		connection.setConnectTimeout(connectTimeoutInSeconds * 1000);
		connection.setReadTimeout(readTimeoutInSeconds * 1000);
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
	public Image getImage(final URLConnection connection) throws IOException {
		InputStream input = null;
		try {
			input = connection.getInputStream();
			return ImageIO.read(input);
		} catch (CMMException e) {
			// Exception reading image format
			Logger.error(e.getMessage());
			return null;
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
	public String readURL(final URLConnection connection) throws IOException {
		InputStream input = null;
		try {
			input = connection.getInputStream();
			return IOUtils.toString(input);
		} finally {
			ClosingUtils.close(input);
		}
	}

	@Override
	public String readURL(final URLConnection connection, final int bytes)
			throws IOException {
		BufferedInputStream bis = null;
		try {
			InputStream input = connection.getInputStream();
			bis = new BufferedInputStream(input);
			byte[] array = new byte[bytes];
			int read = bis.read(array);
			return read != -1 ? new String(array) : null;
		} finally {
			ClosingUtils.close(bis);
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
	public String readURL(final URLConnection connection, final String charset)
			throws IOException {
		InputStream input = null;
		try {
			input = connection.getInputStream();
			return IOUtils.toString(input, charset);
		} finally {
			ClosingUtils.close(input);
		}
	}

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
	@Override
	public String readURL(final String requestURL,
			final Map<String, String> params, final String charset)
			throws IOException {
		URLConnection connection = getConnection(requestURL);
		connection.setUseCaches(false);
		connection.setDoInput(true);

		if (params != null && params.size() > 0) {
			connection.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(
					connection.getOutputStream());
			writer.write(formatPOSTParameters(params));
			writer.flush();
		}

		InputStream input = null;
		try {
			input = connection.getInputStream();
			return IOUtils.toString(input, charset);
		} finally {
			ClosingUtils.close(input);
		}
	}

	private String formatPOSTParameters(final Map<String, String> parameters)
			throws UnsupportedEncodingException {
		List<String> requestParams = new ArrayList<String>();

		for (Map.Entry<String, String> parameter : parameters.entrySet()) {
			requestParams.add(formatPOSTParameter(parameter));
		}

		return org.apache.commons.lang.StringUtils.join(requestParams, '&');
	}

	private String formatPOSTParameter(final Map.Entry<String, String> parameter)
			throws UnsupportedEncodingException {
		return StringUtils.getString(
				URLEncoder.encode(parameter.getKey(), "UTF-8"), "=",
				URLEncoder.encode(parameter.getValue(), "UTF-8"));
	}
}
