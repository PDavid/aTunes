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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.ICommandHandler;

/**
 * This class defines accepted arguments by application.
 */

public final class ApplicationArguments implements Serializable,
		IApplicationArguments {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8482627877309499076L;

	/**
	 * Debug constant This argument makes a big log file.
	 */
	static final String DEBUG = "debug";

	/**
	 * Debug constant This argument makes a big log file.
	 */
	private static final String DEBUG_LOG = "debug-log";

	/**
	 * Ignore look and feel constant. This argument makes application use OS
	 * default Look And Feel.
	 */
	private static final String IGNORE_LOOK_AND_FEEL = "ignore-look-and-feel";

	/**
	 * Disable multiple instances control.
	 */
	private static final String ALLOW_MULTIPLE_INSTANCE = "multiple-instance";

	/**
	 * Argument to define a custom folder from which to read configuration.
	 */
	private static final String USE_CONFIG_FOLDER = "use-config-folder=";

	/**
	 * Argument to define a custom folder from which to read repository
	 * configuration (useful to share a repository configuration) This parameter
	 * has priority over USE_CONFIG_FOLDER
	 */
	private static final String USE_REPOSITORY_CONFIG_FOLDER = "use-repository-config-folder=";

	/**
	 * Do not try to update the application (useful for Linux packages).
	 */
	private static final String NO_UPDATE = "no-update";

	/**
	 * Simulates CD ripper process (useful to test application in Mac OS X where
	 * cd ripping is not supported at this moment)
	 */
	private static final String SIMULATE_CD = "simulate-cd";

	/**
	 * Original arguments passed to application
	 */
	private List<String> originalArguments;

	/**
	 * Saved arguments
	 */
	private List<String> savedArguments;

	/**
	 * Defines if aTunes is running in debug mode.
	 */
	private boolean debug;

	/**
	 * Defines if aTunes is running in debug log level
	 */
	private boolean debugLevel;

	/** Defines if aTunes will ignore look and feel. */
	private boolean ignoreLookAndFeel;

	/** Defines if aTunes should not try to update (for Linux packages). */
	private boolean noUpdate;

	/**
	 * Defines if aTunes will simulate cd ripper
	 */
	private boolean simulateCD;

	/**
	 * @return the debug
	 */
	@Override
	public boolean isDebug() {
		return this.debug;
	}

	/**
	 * @return the ignoreLookAndFeel
	 */
	@Override
	public boolean isIgnoreLookAndFeel() {
		return this.ignoreLookAndFeel;
	}

	/**
	 * @return the noUpdate
	 */
	@Override
	public boolean isNoUpdate() {
		return this.noUpdate;
	}

	/**
	 * @return the simulateCd argument
	 */
	@Override
	public boolean isSimulateCD() {
		return this.simulateCD;
	}

	/**
	 * Finds USE_CONFIG_FOLDER at argument list and gets value.
	 * 
	 * @param args
	 *            the args
	 * 
	 * @return the user config folder
	 */
	@Override
	public String getUserConfigFolder() {
		return getArgument(this.originalArguments, USE_CONFIG_FOLDER);
	}

	/**
	 * Finds USE_REPOSITORY_CONFIG_FOLDER at argument list and gets value.
	 * 
	 * @param args
	 *            the args
	 * 
	 * @return the repository config folder
	 */
	@Override
	public String getRepositoryConfigFolder() {
		return getArgument(this.originalArguments, USE_REPOSITORY_CONFIG_FOLDER);
	}

	/**
	 * Returns if application received multiple instance argument
	 * 
	 * @return
	 */
	@Override
	public boolean isMultipleInstance() {
		return this.originalArguments.contains(ALLOW_MULTIPLE_INSTANCE);
	}

	private String getArgument(final List<String> arguments,
			final String argument) {
		String value = null;
		if (arguments != null) {
			for (String arg : arguments) {
				if (arg.toLowerCase().startsWith(argument)) {
					value = arg.substring(argument.length());
				}
			}
		}
		return value;
	}

	/**
	 * Save arguments. All arguments defined in this class must be saved.
	 * Commands are also saved but used separately
	 * 
	 * @param arguments
	 */
	@Override
	public void saveArguments(final List<String> arguments) {
		if (arguments == null) {
			throw new IllegalArgumentException();
		}
		this.originalArguments = arguments;
		this.savedArguments = new ArrayList<String>();
		checkAndSave(arguments, DEBUG);
		checkAndSave(arguments, DEBUG_LOG);
		checkAndSave(arguments, IGNORE_LOOK_AND_FEEL);
		checkAndSave(arguments, ALLOW_MULTIPLE_INSTANCE);
		checkAndSave(arguments, USE_CONFIG_FOLDER);
		checkAndSave(arguments, USE_REPOSITORY_CONFIG_FOLDER);
		checkAndSave(arguments, NO_UPDATE);
		checkAndSave(arguments, SIMULATE_CD);
		checkAndSave(arguments, Constants.COMMAND_PREFIX);

		// Set debug
		this.debug = arguments.contains(ApplicationArguments.DEBUG);
		// Set debug level
		this.debugLevel = arguments.contains(ApplicationArguments.DEBUG_LOG);
		// Set ignore look and feel
		this.ignoreLookAndFeel = arguments
				.contains(ApplicationArguments.IGNORE_LOOK_AND_FEEL);
		// Set no update
		this.noUpdate = arguments.contains(ApplicationArguments.NO_UPDATE);
		// Set simulate cd
		this.simulateCD = arguments.contains(ApplicationArguments.SIMULATE_CD);
	}

	/**
	 * Checks if list of arguments contains given arg and saves it
	 * 
	 * @param arguments
	 * @param arg
	 */
	private void checkAndSave(final List<String> arguments, final String arg) {
		if (arguments != null) {
			for (String argument : arguments) {
				if (argument.toLowerCase().startsWith(arg.toLowerCase())) {
					this.savedArguments.add(argument);
				}
			}
		}
	}

	/**
	 * Returns original arguments of application
	 * 
	 * @return
	 */
	@Override
	public List<String> getOriginalArguments() {
		return this.originalArguments;
	}

	/**
	 * Returns a string with saved arguments (not commands)
	 * 
	 * @param commandHandler
	 * @return
	 */
	@Override
	public String getSavedArguments(final ICommandHandler commandHandler) {
		return getSavedArguments(commandHandler, false);
	}

	/**
	 * Returns a string with saved commands
	 * 
	 * @param commandHandler
	 * @return
	 */
	@Override
	public String getSavedCommands(final ICommandHandler commandHandler) {
		return getSavedArguments(commandHandler, true);
	}

	/**
	 * Returns commands or arguments
	 * 
	 * @param commandHandler
	 * @param commands
	 * @return
	 */
	private String getSavedArguments(final ICommandHandler commandHandler,
			final boolean commands) {
		StringBuilder sb = new StringBuilder();
		for (String arg : this.savedArguments) {
			if (commands && commandHandler.isValidCommand(arg) || !commands
					&& !commandHandler.isValidCommand(arg)) {
				sb.append(arg);
				sb.append(" ");
			}
		}
		return sb.toString().trim();
	}

	@Override
	public boolean isDebugLevelLog() {
		return this.debugLevel;
	}
}
