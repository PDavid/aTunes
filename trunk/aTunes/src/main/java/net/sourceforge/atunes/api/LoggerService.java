package net.sourceforge.atunes.api;

import org.commonjukebox.plugins.PluginApi;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

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
