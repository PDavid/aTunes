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

package net.sourceforge.atunes.kernel.modules.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.RemoteAction;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.ICommand;
import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.utils.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Responsible of managing remote commands
 * 
 * @author alex
 * 
 */
public final class CommandHandler extends AbstractHandler implements
		ICommandHandler, ApplicationContextAware {

	/**
	 * Map of commands defined to be used
	 */
	private Map<String, ICommand> commands;

	private IApplicationArguments applicationArguments;

	private ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.context = applicationContext;
	}

	/**
	 * @param arguments
	 */
	public void setApplicationArguments(IApplicationArguments arguments) {
		this.applicationArguments = arguments;
	}

	@Override
	public void deferredInitialization() {
		Map<String, RemoteAction> actions = context
				.getBeansOfType(RemoteAction.class);
		for (Map.Entry<String, RemoteAction> action : actions.entrySet()) {
			Logger.debug("Initializing command: ", action.getKey());
		}
		runCommands(applicationArguments.getSavedCommands(this));
	}

	@Override
	public void registerCommand(ICommand cmd) {
		if (commands == null) {
			commands = new HashMap<String, ICommand>();
		}
		commands.put(cmd.getCommandName(), cmd);
	}

	@Override
	public void unregisterCommand(ICommand cmd) {
		if (commands != null) {
			commands.remove(cmd.getCommandName());
		}
	}

	@Override
	public void runCommands(String commandline) {
		if (commands != null) {
			String[] tokens = commandline.split(" ");
			for (String token : tokens) {
				if (isValidCommand(token)) {
					processAndRun(token);
				}
			}
		}
	}

	@Override
	public boolean isValidCommand(String commandName) {
		return isCommand(commandName)
				&& commands.containsKey(commandName.replaceFirst(
						Constants.COMMAND_PREFIX, ""));
	}

	@Override
	public boolean isCommand(String commandName) {
		return commandName.startsWith(Constants.COMMAND_PREFIX);
	}

	@Override
	public String processAndRun(String commandName) {
		ICommand cmd = commands.get(commandName.replaceFirst(
				Constants.COMMAND_PREFIX, "").split(" ")[0]);
		if (cmd != null) {
			List<String> parameters = new ArrayList<String>(
					Arrays.asList(commandName.split(" ")));
			parameters.remove(0);

			if (!cmd.isSynchronousResponse()) {
				SwingUtilities.invokeLater(new RunCommandRunnable(cmd,
						parameters));
				return "OK"; // the command is async, we dont need do wait for a
								// responce
			} else {
				return cmd.runCommand(parameters);
			}
		} else {
			return "Bad command name of format, type \"command:help\" for assistance.";
		}
	}
}
