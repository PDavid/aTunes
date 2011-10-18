package net.sourceforge.atunes.model;

import java.io.Serializable;

/**
 * Represents configuration of a proxy
 * @author alex
 *
 */
public interface IProxy extends Serializable {

	public static final String HTTP_PROXY = "HTTP_PROXY";
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