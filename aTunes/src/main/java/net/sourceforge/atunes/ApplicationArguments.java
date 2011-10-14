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

package net.sourceforge.atunes;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.ICommandHandler;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * This class defines accepted arguments by application.
 */
@PluginApi
public final class ApplicationArguments {

    /** 
     * Debug constant This argument makes a big log file. 
     */
    public static final String DEBUG = "debug";

    /**
     * Ignore look and feel constant. This argument makes application use OS default Look And Feel.
     */
    public static final String IGNORE_LOOK_AND_FEEL = "ignore-look-and-feel";

    /** 
     * Disable multiple instances control. 
     */
    public static final String ALLOW_MULTIPLE_INSTANCE = "multiple-instance";

    /** 
     * Argument to define a custom folder from which to read configuration. 
     */
    public static final String USE_CONFIG_FOLDER = "use-config-folder=";

    /**
     * Argument to define a custom folder from which to read repository
     * configuration (useful to share a repository configuration) This parameter
     * has priority over USE_CONFIG_FOLDER
     */
    public static final String USE_REPOSITORY_CONFIG_FOLDER = "use-repository-config-folder=";

    /** 
     * Do not try to update the application (useful for Linux packages). 
     */
    public static final String NO_UPDATE = "no-update";
    
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

    /** Defines if aTunes will ignore look and feel. */
    private boolean ignoreLookAndFeel;

    /** Defines if aTunes should not try to update (for Linux packages). */
    private boolean noUpdate;
    
    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @return the ignoreLookAndFeel
     */
    public boolean isIgnoreLookAndFeel() {
        return ignoreLookAndFeel;
    }

    /**
     * @return the noUpdate
     */
    public boolean isNoUpdate() {
        return noUpdate;
    }

    /**
     * Finds USE_CONFIG_FOLDER at argument list and gets value.
     * 
     * @param args
     *            the args
     * 
     * @return the user config folder
     */
    public String getUserConfigFolder() {
    	return getArgument(originalArguments, USE_CONFIG_FOLDER);
    }

    /**
     * Finds USE_REPOSITORY_CONFIG_FOLDER at argument list and gets value.
     * 
     * @param args
     *            the args
     * 
     * @return the repository config folder
     */
    public String getRepositoryConfigFolder() {
    	return getArgument(originalArguments, USE_REPOSITORY_CONFIG_FOLDER);
    }
    
    private String getArgument(List<String> arguments, String argument) {
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
    public void saveArguments(List<String> arguments) {
    	if (arguments == null) {
    		throw new IllegalArgumentException();
    	}
    	originalArguments = arguments;
        savedArguments = new ArrayList<String>();
        checkAndSave(arguments, DEBUG);
        checkAndSave(arguments, IGNORE_LOOK_AND_FEEL);
        checkAndSave(arguments, ALLOW_MULTIPLE_INSTANCE);
        checkAndSave(arguments, USE_CONFIG_FOLDER);
        checkAndSave(arguments, USE_REPOSITORY_CONFIG_FOLDER);
        checkAndSave(arguments, NO_UPDATE);
        checkAndSave(arguments, Constants.COMMAND_PREFIX);
        
        // Set debug
        debug = arguments.contains(ApplicationArguments.DEBUG);
        // Set ignore look and feel
    	ignoreLookAndFeel = arguments.contains(ApplicationArguments.IGNORE_LOOK_AND_FEEL);
        // Set no update
    	noUpdate = arguments.contains(ApplicationArguments.NO_UPDATE);
    }

    /**
     * Checks if list of arguments contains given arg and saves it
     * 
     * @param arguments
     * @param arg
     */
    private void checkAndSave(List<String> arguments, String arg) {
        if (arguments != null) {
            for (String argument : arguments) {
                if (argument.toLowerCase().startsWith(arg.toLowerCase())) {
                    savedArguments.add(argument);
                }
            }
        }
    }

    /**
     * Returns original arguments of application
     * @return
     */
    public List<String> getOriginalArguments() {
		return originalArguments;
	}
    
    /**
     * Returns a string with saved arguments (not commands)
     * @param commandHandler
     * @return
     */
    public String getSavedArguments(ICommandHandler commandHandler) {
    	return getSavedArguments(commandHandler, false);
    }

    /**
     * Returns a string with saved commands
     * @param commandHandler
     * @return
     */
    public String getSavedCommands(ICommandHandler commandHandler) {
    	return getSavedArguments(commandHandler, true);
    }
    
    /**
     * Returns commands or arguments
     * @param commandHandler
     * @param commands
     * @return
     */
    private String getSavedArguments(ICommandHandler commandHandler, boolean commands) {
        StringBuilder sb = new StringBuilder();
        for (String arg : savedArguments) {
            if (commands && commandHandler.isValidCommand(arg) || !commands && !commandHandler.isValidCommand(arg)) {
                sb.append(arg);
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }
}
