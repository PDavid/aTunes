/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.kernel.modules.state.beans;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.util.Arrays;

import net.sourceforge.atunes.kernel.modules.proxy.Proxy;

/**
 * Bean for net.sourceforge.atunes.kernel.modules.proxy.Proxy
 */
public class ProxyBean {

    public static final String HTTP_PROXY = "HTTP_PROXY";
    public static final String SOCKS_PROXY = "SOCKS_PROXY";

    private String type;
    private String url;
    private int port;
    private String user;
    private String password;
    private byte[] encryptedPassword;

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
     * @param encryptedPassword
     *            the encrypted password
     */
    @ConstructorProperties( { "type", "url", "port", "user", "encryptedPassword" })
    public ProxyBean(String type, String url, int port, String user, byte[] encryptedPassword) {
        this.type = type;
        this.url = url;
        this.port = port;
        this.encryptedPassword = encryptedPassword != null ? Arrays.copyOf(encryptedPassword, encryptedPassword.length) : null;
    }

    /**
     * Gets the encrypted password.
     * 
     * @return the encrypted password
     */
    public byte[] getEncryptedPassword() {
        return encryptedPassword != null ? Arrays.copyOf(encryptedPassword, encryptedPassword.length) : null;
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
     * Sets the encrypted password.
     * 
     * @param encryptedPassword
     *            the new encrypted password
     */
    public void setEncryptedPassword(byte[] encryptedPassword) {
        this.encryptedPassword = Arrays.copyOf(encryptedPassword, encryptedPassword.length);
    }

    /**
     * Sets the password.
     * 
     * @param password
     *            the new password
     */
    public void setPassword(String password) {
        this.password = password;
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
    public static Proxy getProxy(ProxyBean bean) throws IOException {
        if (bean == null) {
            return null;
        }
        return new Proxy(bean.getType().equals(ProxyBean.HTTP_PROXY) ? Proxy.Type.HTTP : Proxy.Type.SOCKS, bean.getUrl(), bean.getPort(), bean.getUser(), bean.getPassword());
    }

}
