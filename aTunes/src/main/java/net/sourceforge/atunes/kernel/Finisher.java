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

package net.sourceforge.atunes.kernel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Restarts or finishes application
 * 
 * @author alex
 * 
 */
public final class Finisher {

	private ApplicationLifeCycleListeners applicationLifeCycleListeners;

	private IOSManager osManager;

	private IApplicationArguments applicationArguments;

	private ICommandHandler commandHandler;

	/**
	 * @param commandHandler
	 */
	public void setCommandHandler(final ICommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(
			final IApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param applicationLifeCycleListeners
	 */
	public void setApplicationLifeCycleListeners(
			final ApplicationLifeCycleListeners applicationLifeCycleListeners) {
		this.applicationLifeCycleListeners = applicationLifeCycleListeners;
	}

	final void finish() {
		finish(false);
	}

	final void restart() {
		finish(true);
	}

	private final void finish(final boolean restart) {
		try {
			Logger.info(StringUtils.getString(restart ? "Restarting "
					: "Closing ", Constants.APP_NAME, " ", Constants.VERSION
					.toString()));
			// Store all configuration and finish all active modules
			this.applicationLifeCycleListeners.applicationFinish();

			if (restart) {
				Runtime.getRuntime().addShutdownHook(new Thread() {
					@Override
					public void run() {
						String command = getRestartCommand();
						Logger.info("Restart command: ", command);
						// Start new application instance
						try {
							Runtime.getRuntime().exec(command);
						} catch (IOException e) {
							Logger.error(e);
						}
					}
				});
			}
		} catch (Throwable t) {
			Logger.error(t);
		} finally {
			Logger.info("Application finished");
			// Exit normally
			System.exit(0);
		}
	}

	private String getRestartCommand() {
		String parameters = this.osManager.getLaunchParameters();
		String savedArguments = this.applicationArguments
				.getSavedArguments(this.commandHandler);
		List<String> command = new ArrayList<String>();
		command.add(this.osManager.getLaunchCommand());
		if (!StringUtils.isEmpty(parameters)) {
			command.add(parameters);
		}
		if (!StringUtils.isEmpty(savedArguments)) {
			// command.add(savedArguments);
		}
		return org.apache.commons.lang.StringUtils.join(command, " ");
	}
}
