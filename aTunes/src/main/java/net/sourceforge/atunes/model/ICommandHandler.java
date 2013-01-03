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


/**
 * Handles and executes commands received
 * @author alex
 *
 */
public interface ICommandHandler extends IHandler {

	/**
	 * Adds a command to the map of commands ready to be used
	 * 
	 * @param cmd
	 */
	public void registerCommand(ICommand cmd);

	/**
	 * Removed command from map of commands ready to be used
	 * 
	 * @param cmd
	 */
	public void unregisterCommand(ICommand cmd);

	/**
	 * Parses commandline to search for commands and execute them. Return of
	 * command is ignored This method is created to run commands without sending
	 * it to another instance
	 * 
	 * @param commandline
	 */
	public void runCommands(String commandline);

	/**
	 * Returns <code>true</code> if the given string is a valid command: has
	 * correct syntax and exists
	 * 
	 * @param commandName
	 * @return
	 */
	public boolean isValidCommand(String commandName);

	/**
	 * Returns <code>true</code> if the given string has correct command syntax
	 * 
	 * @param commandName
	 * @return
	 */
	public boolean isCommand(String commandName);

	/**
	 * Identifies and executes given command
	 * 
	 * @param commandName
         * @return command output
	 */
	public String processAndRun(String commandName);

}