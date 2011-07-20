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

package net.sourceforge.atunes.kernel.modules.state.beans;

import java.io.IOException;
import java.io.Serializable;

import net.sourceforge.atunes.kernel.modules.proxy.ExtendedProxy;
import net.sourceforge.atunes.kernel.modules.state.PasswordPreference;

/**
 * Bean for net.sourceforge.atunes.kernel.modules.proxy.Proxy
 */
public final class ProxyBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -9038681249825140374L;
	
	public static final String HTTP_PROXY = "HTTP_PROXY";
    public static final String SOCKS_PROXY = "SOCKS_PROXY";

    private String type;
    private String url;
    private int port;
    private String user;
    private PasswordPreference password;

    /**
     * Instantiates a new proxy bean.
     */
    public ProxyBean() {
        // Nothing to do
    }

    /**
     * Instantiates a new proxy bean.
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
     */
    public ProxyBean(String type, String url, int port, String user, String password) {
        this.type = type;
        this.url = url;
        this.port = port;
        this.password = password != null ? new PasswordPreference(password) : null;
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password.getPassword();        
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
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return type;
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

    /**
     * Sets the password.
     * 
     * @param password
     *            the new password
     */
    public void setPassword(String password) {
        this.password = password != null ? new PasswordPreference(password) : null;
    }

    /**
     * Sets the port.
     * 
     * @param port
     *            the new port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets the type.
     * 
     * @param type
     *            the new type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Sets the url.
     * 
     * @param url
     *            the new url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Sets the user.
     * 
     * @param user
     *            the new user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Returns a proxy object from a bean
     * 
     * @param bean
     * @return
     * @throws IOException
     */
    public static ExtendedProxy getProxy(ProxyBean bean) throws IOException {
        if (bean == null) {
            return null;
        }
        return ExtendedProxy.getProxy(bean);
    }

}
