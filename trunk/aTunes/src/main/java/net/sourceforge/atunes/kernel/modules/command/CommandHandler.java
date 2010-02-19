/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.PlayNextAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.PlayPreviousAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.ShowOSDAction;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

public class CommandHandler extends Handler {

    /**
     * Singleton instance
     */
    private static CommandHandler instance;

    /**
     * Prefix used in all commands when invoked
     */
    public static final String COMMAND_PREFIX = "command:";

    /**
     * Map of commands defined to be used
     */
    private Map<String, Command> commands;

    /**
     * Singleton getter
     * 
     * @return
     */
    public static CommandHandler getInstance() {
        if (instance == null) {
            instance = new CommandHandler();
        }
        return instance;
    }

    /**
     * Default constructor made private
     */
    private CommandHandler() {
    }

    @Override
    public void applicationStarted() {
        runCommands(ApplicationArguments.getSavedCommands());
    }

    /**
     * Adds a command to the map of commands ready to be used
     * 
     * @param cmd
     */
    public void registerCommand(Command cmd) {
        if (commands == null) {
            commands = new HashMap<String, Command>();
        }
        commands.put(cmd.getCommandName(), cmd);
    }

    /**
     * Removed command from map of commands ready to be used
     * 
     * @param cmd
     */
    public void unregisterCommand(Command cmd) {
        if (commands != null) {
            commands.remove(cmd.getCommandName());
        }
    }

    /**
     * Parses commandline to search for commands and execute them. Return of
     * command is ignored This method is created to run commands without sending
     * it to another instance
     * 
     * @param commandline
     */
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

    /**
     * Returns <code>true</code> if the given string is a valid command: has
     * correct syntax and exists
     * 
     * @param commandName
     * @return
     */
    public boolean isValidCommand(String commandName) {
        return isCommand(commandName) && commands.containsKey(commandName.replaceFirst(COMMAND_PREFIX, ""));
    }

    /**
     * Returns <code>true</code> if the given string has correct command syntax
     * 
     * @param commandName
     * @return
     */
    public boolean isCommand(String commandName) {
        return commandName.startsWith(COMMAND_PREFIX);
    }

    /**
     * Identifies and executes given command
     * 
     * @param commandName
     */
    public void processAndRun(String commandName) {
        Command cmd = commands.get(commandName.replaceFirst(COMMAND_PREFIX, ""));
        if (cmd != null) {
            SwingUtilities.invokeLater(new RunCommandRunnable(cmd));
        }
    }

    @Override
    public void applicationFinish() {
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
    }

    @Override
    protected void initHandler() {
        initActions();
    }

    private void initActions() {
        Actions.getAction(PlayNextAudioObjectAction.class);
        Actions.getAction(PlayPreviousAudioObjectAction.class);
        Actions.getAction(ShowOSDAction.class);
    }
    
    private static class RunCommandRunnable implements Runnable {
    	
    	private Command command;
    	
    	public RunCommandRunnable(Command command) {
    		this.command = command;
    	}
    	
        @Override
        public void run() {
        	command.runCommand();
        }
    	
    }

}
