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

import java.io.Serializable;

/**
 * Represents configuration of a proxy
 * 
 * @author alex
 * 
 */
public interface IProxyBean extends Serializable {

    /**
     * An http proxy
     */
    public static final String HTTP_PROXY = "HTTP_PROXY";
    /**
     * A socks proxy
     */
    public static final String SOCKS_PROXY = "SOCKS_PROXY";

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword();

    /**
     * Gets the port.
     * 
     * @return the port
     */
    public int getPort();

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType();

    /**
     * Gets the url.
     * 
     * @return the url
     */
    public String getUrl();

    /**
     * Gets the user.
     * 
     * @return the user
     */
    public String getUser();

    /**
     * Sets the password.
     * 
     * @param password
     *            the new password
     */
    public void setPassword(String password);

    /**
     * Sets the port.
     * 
     * @param port
     *            the new port
     */
    public void setPort(int port);

    /**
     * Sets the type.
     * 
     * @param type
     *            the new type
     */
    public void setType(String type);

    /**
     * Sets the url.
     * 
     * @param url
     *            the new url
     */
    public void setUrl(String url);

    /**
     * Sets the user.
     * 
     * @param user
     *            the new user
     */
    public void setUser(String user);

}