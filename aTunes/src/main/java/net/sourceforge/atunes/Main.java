/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

    static Logger logger = new Logger();

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
        logger.info(LogCategories.START, firstLine);

        // Second line: Java Virtual Machine Version
        logger.info(LogCategories.START, StringUtils.getString("Running in Java Virtual Machine ", System.getProperty("java.version")));

        // Third line: Application Arguments
        logger.info(LogCategories.START, StringUtils.getString("Arguments = ", arguments));

        // Fourth line: DEBUG mode
        logger.info(LogCategories.START, StringUtils.getString("Debug mode = ", Kernel.DEBUG));

        // Fifth line: Execution path
        logger.info(LogCategories.START, StringUtils.getString("Execution path = ", SystemProperties.getWorkingDirectory()));
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
        Kernel.DEBUG = arguments.contains(ApplicationArguments.DEBUG);

        // Save arguments, if application is restarted they will be necessary
        ApplicationArguments.saveArguments(arguments);

        // First, look up for other instances running
        if (!arguments.contains(ApplicationArguments.ALLOW_MULTIPLE_INSTANCE) && !MultipleInstancesHandler.getInstance().isFirstInstance()) {
            // Is not first aTunes instance running, so send parameters and finalize
            MultipleInstancesHandler.getInstance().sendArgumentsToFirstInstance(arguments);
        } else {
            // NORMAL APPLICATION STARTUP

            // Set ignore look and feel flag in kernel
            Kernel.IGNORE_LOOK_AND_FEEL = arguments.contains(ApplicationArguments.IGNORE_LOOK_AND_FEEL);

            // Set no update flag in kernel
            Kernel.NO_UPDATE = arguments.contains(ApplicationArguments.NO_UPDATE);

            // Set custom config folder if passed as argument
            SystemProperties.setCustomConfigFolder(ApplicationArguments.getUserConfigFolder(arguments));

            // Set log4j properties
            Log4jPropertiesLoader.loadProperties(Kernel.DEBUG);

            // Enable uncaught exception catching
            try {
                Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        logger.error(LogCategories.UNEXPEXTED_ERROR, StringUtils.getString("Thread: ", t.getName()));
                        logger.error(LogCategories.UNEXPEXTED_ERROR, e);
                    }
                });
            } catch (Throwable t) {
                logger.error(LogCategories.UNEXPEXTED_ERROR, t);
            }

            // WE ARE CLOSING ERROR STREAM!!!
            // THIS IS DONE TO AVOID ANNOYING MESSAGES FROM SOME LIBRARIES
            System.err.close();

            // For detecting Swing threading violations
            if (Kernel.DEBUG) {
                RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
            }

            // Log program properties
            logProgramProperties(arguments);

            // Start the Kernel, which really starts application
            Kernel.startKernel(arguments);
        }

    }

}
