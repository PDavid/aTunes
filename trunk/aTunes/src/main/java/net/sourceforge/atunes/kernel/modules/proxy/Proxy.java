/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.proxy;

import java.io.IOException;
import java.io.Serializable;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.Socket;
import java.net.UnknownHostException;

import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;

/**
 * The Class Proxy.
 */
public class Proxy extends java.net.Proxy implements Serializable {

    private static final long serialVersionUID = 7495084217081194366L;

    /** The url. */
    private String url;

    /** The port. */
    private int port;

    /** The user. */
    private String user;

    /** The password. */
    private String password;

    /**
     * Instantiates a new proxy.
     * 
     * @param type
     *            the type
     * @param url
     *            the url
     * @param port
     *            the port
     * @param user
     *            the user
     * @param password
     *            the password
     * 
     * @throws UnknownHostException
     *             the unknown host exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public Proxy(Type type, String url, int port, String user, String password) throws UnknownHostException, IOException {
        super(type, new Socket(url, port).getRemoteSocketAddress());
        this.url = url;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public static void initProxy(final Proxy proxy) {
        if (proxy != null) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxy.getUser(), proxy.getPassword().toCharArray());
                }
            });
        } else {
            Authenticator.setDefault(null);
        }

    }

    /**
     * Returns a net.sourceforge.atunes.kernel.modules.proxy.Proxy for a given
     * ProxyBean
     * 
     * @param proxy
     *            A ProxyBean
     * 
     * @return A net.sourceforge.atunes.kernel.modules.proxy.Proxy
     * 
     * @throws UnknownHostException
     *             If the host is unknown
     * @throws IOException
     *             If an IO exception occurs
     */
    public static Proxy getProxy(ProxyBean proxy) throws UnknownHostException, IOException {
        if (proxy == null) {
            return null;
        }
        return new Proxy(proxy.getType().equals(ProxyBean.HTTP_PROXY) ? Type.HTTP : Type.SOCKS, proxy.getUrl(), proxy.getPort(), proxy.getUser(), proxy.getPassword());
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the port.
     * 
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the url.
     * 
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the user.
     * 
     * @return the user
     */
    public String getUser() {
        return user;
    }

}
