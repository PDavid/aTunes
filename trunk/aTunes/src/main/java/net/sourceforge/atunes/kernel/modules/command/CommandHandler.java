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

package net.sourceforge.atunes.kernel.modules.command;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.ApplicationArguments;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.PlayNextAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.PlayPreviousAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.ShowOSDAction;
import net.sourceforge.atunes.model.ICommand;
import net.sourceforge.atunes.model.ICommandHandler;

public final class CommandHandler extends AbstractHandler implements ICommandHandler {

    /**
     * Map of commands defined to be used
     */
    private Map<String, ICommand> commands;
    
    private ApplicationArguments applicationArguments;
    
    public void setApplicationArguments(ApplicationArguments arguments) {
		this.applicationArguments = arguments;
	}

    @Override
    public void allHandlersInitialized() {
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
        return isCommand(commandName) && commands.containsKey(commandName.replaceFirst(Constants.COMMAND_PREFIX, ""));
    }

    @Override
	public boolean isCommand(String commandName) {
        return commandName.startsWith(Constants.COMMAND_PREFIX);
    }

    @Override
	public void processAndRun(String commandName) {
        ICommand cmd = commands.get(commandName.replaceFirst(Constants.COMMAND_PREFIX, ""));
        if (cmd != null) {
            SwingUtilities.invokeLater(new RunCommandRunnable(cmd));
        }
    }

    @Override
    protected void initHandler() {
        initActions();
    }

    private void initActions() {
        getBean(PlayNextAudioObjectAction.class);
        getBean(PlayPreviousAudioObjectAction.class);
        Actions.getAction(ShowOSDAction.class);
    }

}
