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

package net.sourceforge.atunes;

/**
 * Represents an exception that don't want to show to user
 * 
 * @author alex
 * 
 */
public class KnownException {

	private String exceptionClass;

	private String message;

	/**
	 * @return the exceptionClass
	 */
	public String getExceptionClass() {
		return this.exceptionClass;
	}

	/**
	 * @param exceptionClass
	 *            the exceptionClass to set
	 */
	public void setExceptionClass(final String exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}
}
