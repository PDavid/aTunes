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

import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Main class to launch aTunes.
 */
public final class Main {
	
	private ApplicationArguments applicationArguments;
	private IOSManager osManager;
	private ApplicationPropertiesLogger applicationPropertiesLogger;
	private MultipleInstancesCheck multipleInstancesCheck;
	private ApplicationArgumentsSender applicationArgumentsSender;

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
	 * @param applicationPropertiesLogger
	 */
	public void setApplicationPropertiesLogger(ApplicationPropertiesLogger applicationPropertiesLogger) {
		this.applicationPropertiesLogger = applicationPropertiesLogger;
	}
	
	/**
	 * @param multipleInstancesCheck
	 */
	public void setMultipleInstancesCheck(MultipleInstancesCheck multipleInstancesCheck) {
		this.multipleInstancesCheck = multipleInstancesCheck;
	}
	
	/**
	 * @param applicationArgumentsSender
	 */
	public void setApplicationArgumentsSender(ApplicationArgumentsSender applicationArgumentsSender) {
		this.applicationArgumentsSender = applicationArgumentsSender;
	}
	
    /**
     * Main method for calling aTunes.
     * @param args
     */
    public static void main(String[] args) {
        // Enable uncaught exception catching
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());

        // Fetch arguments into a list
        List<String> arguments = StringUtils.fromStringArrayToList(args);
    	Context.initialize();
        // Save arguments, if application is restarted they will be necessary
    	Context.getBean(ApplicationArguments.class).saveArguments(arguments);
    	// Now start application
    	Context.getBean(Main.class).start(arguments);
    }
    
    private void start(List<String> arguments) {
        // For detecting Swing threading violations
        if (applicationArguments.isDebug()) {
//            RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
        }

        // Set log4j properties
        Logger.loadProperties(applicationArguments.isDebug(), osManager);

        // First, look up for other instances running
        if (!applicationArguments.isMultipleInstance() && !multipleInstancesCheck.isFirstInstance()) {
            // Is not first aTunes instance running, so send parameters and finalize
        	applicationArgumentsSender.sendArgumentsToFirstInstance(arguments);
        } else {
            // WE ARE CLOSING ERROR STREAM!!!
            // THIS IS DONE TO AVOID ANNOYING MESSAGES FROM SOME LIBRARIES
            System.err.close();

            // Log program properties
            applicationPropertiesLogger.logProgramProperties();

            // Start the Kernel, which really starts application
            Context.getBean(IKernel.class).start();
        }
    }
}
