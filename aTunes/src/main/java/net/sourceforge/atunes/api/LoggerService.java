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

package net.sourceforge.atunes.api;

import net.sourceforge.atunes.utils.Logger;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * Access logger service
 * @author alex
 *
 */
@PluginApi
public class LoggerService {

	/**
	 * Creates a new logger service to be used by plugins
	 */
	public LoggerService() {
	}

	/**
	 * Logs a information message
	 * 
	 * @param message
	 */
	public void info(final String message) {
		Logger.info(message);
	}

	/**
	 * Logs a debug message
	 * 
	 * @param message
	 */
	public void debug(final String message) {
		Logger.debug(message);
	}

	/**
	 * Logs an error message
	 * 
	 * @param message
	 */
	public void error(final String message) {
		Logger.error(message);
	}

	/**
	 * Logs an exception
	 * 
	 * @param exception
	 */
	public void error(final Exception exception) {
		Logger.error(exception);
	}
}
