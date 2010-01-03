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
package net.sourceforge.atunes;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.command.CommandHandler;

/**
 * This class defines accepted arguments by application.
 */
public final class ApplicationArguments {

    private ApplicationArguments() {
    }

    /** Debug constant This argument makes a big log file. */
    public static final String DEBUG = "debug";

    /**
     * Ignore look and feel constant This argument makes application use OS
     * default Look And Feel.
     */
    public static final String IGNORE_LOOK_AND_FEEL = "ignore-look-and-feel";

    /** Disable multiple instances control. */
    public static final String ALLOW_MULTIPLE_INSTANCE = "multiple-instance";

    /** Argument to define a custom folder from which to read configuration. */
    public static final String USE_CONFIG_FOLDER = "use-config-folder=";
    
    /** 
     * Argument to define a custom folder from which to read repository configuration (useful to share a repository configuration)
     * This parameter has priority over USE_CONFIG_FOLDER 
     * */ 
    public static final String USE_REPOSITORY_CONFIG_FOLDER = "use-repository-config-folder=";

    /** Do not try to update the application (useful for Linux packages). */
    public static final String NO_UPDATE = "no-update";

    /**
     * Saved arguments
     */
    private static List<String> savedArguments = new ArrayList<String>();

    /**
     * Finds USE_CONFIG_FOLDER at argument list and gets value.
     * 
     * @param args
     *            the args
     * 
     * @return the user config folder
     */
    public static String getUserConfigFolder(List<String> args) {
        String configFolder = null;

        for (String arg : args) {
            if (arg.toLowerCase().startsWith(USE_CONFIG_FOLDER)) {
                configFolder = arg.substring(USE_CONFIG_FOLDER.length());
            }
        }

        return configFolder;
    }

    /**
     * Finds USE_REPOSITORY_CONFIG_FOLDER at argument list and gets value.
     * 
     * @param args
     *            the args
     * 
     * @return the repository config folder
     */
    public static String getRepositoryConfigFolder(List<String> args) {
        String configFolder = null;

        for (String arg : args) {
            if (arg.toLowerCase().startsWith(USE_REPOSITORY_CONFIG_FOLDER)) {
                configFolder = arg.substring(USE_REPOSITORY_CONFIG_FOLDER.length());
            }
        }

        return configFolder;
    }

    /**
     * Save arguments. All arguments defined in this class must be saved.
     * Commands are also saved but used separately
     * 
     * @param arguments
     */
    public static void saveArguments(List<String> arguments) {
        checkAndSave(arguments, DEBUG);
        checkAndSave(arguments, IGNORE_LOOK_AND_FEEL);
        checkAndSave(arguments, ALLOW_MULTIPLE_INSTANCE);
        checkAndSave(arguments, USE_CONFIG_FOLDER);
        checkAndSave(arguments, USE_REPOSITORY_CONFIG_FOLDER);
        checkAndSave(arguments, NO_UPDATE);
        checkAndSave(arguments, CommandHandler.COMMAND_PREFIX);
    }

    /**
     * Checks if list of arguments contains given arg and saves it
     * 
     * @param arguments
     * @param arg
     */
    private static void checkAndSave(List<String> arguments, String arg) {
        for (String argument : arguments) {
            if (argument.toLowerCase().startsWith(arg.toLowerCase())) {
                savedArguments.add(argument);
            }
        }
    }

    /**
     * Returns a string with saved arguments (not commands)
     * 
     * @return
     */
    public static String getSavedArguments() {
        StringBuilder sb = new StringBuilder();
        for (String arg : savedArguments) {
            if (!CommandHandler.getInstance().isValidCommand(arg)) {
                sb.append(arg);
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }

    /**
     * Returns a string with saved commands
     */
    public static String getSavedCommands() {
        StringBuilder sb = new StringBuilder();
        for (String arg : savedArguments) {
            if (CommandHandler.getInstance().isValidCommand(arg)) {
                sb.append(arg);
                sb.append(" ");
            }
        }
        return sb.toString().trim();
    }
}
