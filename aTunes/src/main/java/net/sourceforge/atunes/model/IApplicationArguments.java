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

import java.util.List;


/**
 * Handles arguments passed to application
 * @author alex
 *
 */
public interface IApplicationArguments {

	/**
	 * @return the debug
	 */
	public boolean isDebug();
	
	/**
	 * @return if log must be in debug level
	 */
	public boolean isDebugLevelLog();

	/**
	 * @return the ignoreLookAndFeel
	 */
	public boolean isIgnoreLookAndFeel();

	/**
	 * @return the noUpdate
	 */
	public boolean isNoUpdate();

	/**
	 * Finds USE_CONFIG_FOLDER at argument list and gets value.
	 * 
	 * @param args
	 *            the args
	 * 
	 * @return the user config folder
	 */
	public String getUserConfigFolder();

	/**
	 * Finds USE_REPOSITORY_CONFIG_FOLDER at argument list and gets value.
	 * 
	 * @param args
	 *            the args
	 * 
	 * @return the repository config folder
	 */
	public String getRepositoryConfigFolder();

	/**
	 * Returns if application received multiple instance argument
	 * @return
	 */
	public boolean isMultipleInstance();

	/**
	 * Save arguments. All arguments defined in this class must be saved.
	 * Commands are also saved but used separately
	 * 
	 * @param arguments
	 */
	public void saveArguments(List<String> arguments);

	/**
	 * Returns original arguments of application
	 * @return
	 */
	public List<String> getOriginalArguments();

	/**
	 * Returns a string with saved arguments (not commands)
	 * @param commandHandler
	 * @return
	 */
	public String getSavedArguments(ICommandHandler commandHandler);

	/**
	 * Returns a string with saved commands
	 * @param commandHandler
	 * @return
	 */
	public String getSavedCommands(ICommandHandler commandHandler);

	/**
	 * Returns if app must simulate cd ripper
	 * @return
	 */
	boolean isSimulateCD();

}