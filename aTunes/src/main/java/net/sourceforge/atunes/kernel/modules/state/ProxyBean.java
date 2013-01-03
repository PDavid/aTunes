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

package net.sourceforge.atunes.kernel.modules.state;

import net.sourceforge.atunes.model.IProxyBean;

/**
 * Bean for net.sourceforge.atunes.kernel.modules.proxy.Proxy
 */
public final class ProxyBean implements IProxyBean {

    /**
	 * 
	 */
	private static final long serialVersionUID = -9038681249825140374L;
	
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
        this.user = user;
        this.password = password != null ? new PasswordPreference(password) : null;
    }

    @Override
	public String getPassword() {
        return password.getPassword();        
    }

    @Override
	public int getPort() {
        return port;
    }

    @Override
	public String getType() {
        return type;
    }

    @Override
	public String getUrl() {
        return url;
    }

    @Override
	public String getUser() {
        return user;
    }

    @Override
	public void setPassword(String password) {
        this.password = password != null ? new PasswordPreference(password) : null;
    }

    @Override
	public void setPort(int port) {
        this.port = port;
    }

    @Override
	public void setType(String type) {
        this.type = type;
    }

    @Override
	public void setUrl(String url) {
        this.url = url;
    }

    @Override
	public void setUser(String user) {
        this.user = user;
    }
}
