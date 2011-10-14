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

import java.util.List;

import javax.swing.RepaintManager;

import net.sourceforge.atunes.gui.debug.CheckThreadViolationRepaintManager;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.model.IMultipleInstancesHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Main class to launch aTunes.
 */
public final class Main {

	private ApplicationArguments applicationArguments;
	
	private IOSManager osManager;
	
	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(ApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
    /**
     * Log some properties at start.
     * 
     * @param arguments
     * @param osManager
     */
    private static void logProgramProperties(ApplicationArguments arguments, IOSManager osManager) {
        // First line: version number
        Logger.info("Starting ", Constants.APP_NAME, " ", Constants.VERSION);

        // Second line: Java Virtual Machine Version
        Logger.info("Running in Java Virtual Machine ", System.getProperty("java.version"));

        // Third line: Application Arguments
        Logger.info("Arguments = ", arguments.getOriginalArguments());

        // Fourth line: DEBUG mode
        Logger.info("Debug mode = ", arguments.isDebug());

        // Fifth line: Execution path
        Logger.info("Execution path = ", osManager.getWorkingDirectory());
    }

    /**
     * Main method for calling aTunes.
     * 
     * @param args
     *            the args
     */
    public static void main(String[] args) {
    	Context.initialize("/settings/spring/");
    	Context.getBean(Main.class).start(args);
    }
    
    private void start(String[] args) {
        // Fetch arguments into a list
        List<String> arguments = StringUtils.fromStringArrayToList(args);

        // Save arguments, if application is restarted they will be necessary
    	applicationArguments.saveArguments(arguments);

        // For detecting Swing threading violations
        if (applicationArguments.isDebug()) {
            RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
        }

        // Set log4j properties
        Logger.loadProperties(applicationArguments.isDebug(), osManager);

        // First, look up for other instances running
        if (!arguments.contains(ApplicationArguments.ALLOW_MULTIPLE_INSTANCE) && !Context.getBean(IMultipleInstancesHandler.class).isFirstInstance()) {
            // Is not first aTunes instance running, so send parameters and finalize
        	Context.getBean(IMultipleInstancesHandler.class).sendArgumentsToFirstInstance(arguments);
        } else {
            // NORMAL APPLICATION STARTUP

            // Set custom config folder if passed as argument
            osManager.setCustomConfigFolder(applicationArguments.getUserConfigFolder(arguments));

            // Set custom repository config folder if passed as argument
            osManager.setCustomRepositoryConfigFolder(applicationArguments.getRepositoryConfigFolder(arguments));

            // Enable uncaught exception catching
            Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());

            // WE ARE CLOSING ERROR STREAM!!!
            // THIS IS DONE TO AVOID ANNOYING MESSAGES FROM SOME LIBRARIES
            System.err.close();

            // Log program properties
            logProgramProperties(applicationArguments, osManager);

            // Start the Kernel, which really starts application
            Context.getBean(Kernel.class).start();
        }
    }
}
