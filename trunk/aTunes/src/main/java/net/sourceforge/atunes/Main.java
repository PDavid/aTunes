/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.util.List;

import javax.swing.RepaintManager;

import net.sourceforge.atunes.gui.debug.CheckThreadViolationRepaintManager;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.instances.MultipleInstancesHandler;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.log.Log4jPropertiesLoader;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Main class to launch aTunes.
 */
public final class Main {

    private static Logger logger;

    private Main() {
    }

    /**
     * Log some properties at start.
     * 
     * @param arguments
     *            the arguments
     */
    private static void logProgramProperties(List<String> arguments) {
        // First line: version number
        String firstLine = StringUtils.getString("Starting ", Constants.APP_NAME, " ", Constants.VERSION);
        getLogger().info(LogCategories.START, firstLine);

        // Second line: Java Virtual Machine Version
        getLogger().info(LogCategories.START, StringUtils.getString("Running in Java Virtual Machine ", System.getProperty("java.version")));

        // Third line: Application Arguments
        getLogger().info(LogCategories.START, StringUtils.getString("Arguments = ", arguments));

        // Fourth line: DEBUG mode
        getLogger().info(LogCategories.START, StringUtils.getString("Debug mode = ", Kernel.isDebug()));

        // Fifth line: Execution path
        getLogger().info(LogCategories.START, StringUtils.getString("Execution path = ", SystemProperties.getWorkingDirectory()));
    }

    /**
     * Main method for calling aTunes.
     * 
     * @param args
     *            the args
     */
    public static void main(String[] args) {

        // Fetch arguments into a list
        List<String> arguments = StringUtils.fromStringArrayToList(args);

        // Set debug flag in kernel
        Kernel.setDebug(arguments.contains(ApplicationArguments.DEBUG));

        // Save arguments, if application is restarted they will be necessary
        ApplicationArguments.saveArguments(arguments);

        // First, look up for other instances running
        if (!arguments.contains(ApplicationArguments.ALLOW_MULTIPLE_INSTANCE) && !MultipleInstancesHandler.getInstance().isFirstInstance()) {
            // Is not first aTunes instance running, so send parameters and finalize
            MultipleInstancesHandler.getInstance().sendArgumentsToFirstInstance(arguments);
        } else {
            // NORMAL APPLICATION STARTUP

            // Set ignore look and feel flag in kernel
            Kernel.setIgnoreLookAndFeel(arguments.contains(ApplicationArguments.IGNORE_LOOK_AND_FEEL));

            // Set no update flag in kernel
            Kernel.setNoUpdate(arguments.contains(ApplicationArguments.NO_UPDATE));

            // Set custom config folder if passed as argument
            SystemProperties.setCustomConfigFolder(ApplicationArguments.getUserConfigFolder(arguments));

            // Set custom repository config folder if passed as argument
            SystemProperties.setCustomRepositoryConfigFolder(ApplicationArguments.getRepositoryConfigFolder(arguments));

            // Set log4j properties
            Log4jPropertiesLoader.loadProperties(Kernel.isDebug());

            // Enable uncaught exception catching
            try {
                Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        getLogger().error(LogCategories.UNEXPEXTED_ERROR, StringUtils.getString("Thread: ", t.getName()));
                        getLogger().error(LogCategories.UNEXPEXTED_ERROR, e);
                    }
                });
            } catch (Throwable t) {
                getLogger().error(LogCategories.UNEXPEXTED_ERROR, t);
            }

            // WE ARE CLOSING ERROR STREAM!!!
            // THIS IS DONE TO AVOID ANNOYING MESSAGES FROM SOME LIBRARIES
            System.err.close();

            // For detecting Swing threading violations
            if (Kernel.isDebug()) {
                RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
            }

            // Log program properties
            logProgramProperties(arguments);

            // Start the Kernel, which really starts application
            Kernel.startKernel(arguments);
        }

    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private static Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}
