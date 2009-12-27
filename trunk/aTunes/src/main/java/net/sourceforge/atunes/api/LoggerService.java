/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.api;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

import org.commonjukebox.plugins.PluginApi;

@PluginApi
public class LoggerService {
	
	private Logger logger;
	
	/**
	 * Creates a new logger service to be used by plugins
	 */
	public LoggerService() {
		this.logger = new Logger();
	}
	
	/**
	 * Logs a information message
	 * @param message
	 */
	public void info(String message) {
		this.logger.info(LogCategories.PLUGINS, message);
	}
	
	/**
	 * Logs a debug message
	 * @param message
	 */
	public void debug(String message) {
		this.logger.debug(LogCategories.PLUGINS, message);
	}
	
	/**
	 * Logs an error message
	 * @param message
	 */
	public void error(String message) {
		this.logger.error(LogCategories.PLUGINS, message);
	}

	/**
	 * Logs an exception
	 * @param exception
	 */
	public void error(Exception exception) {
		this.logger.error(LogCategories.PLUGINS, exception);
	}

}
