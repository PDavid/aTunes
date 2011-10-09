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
import net.sourceforge.atunes.kernel.modules.instances.MultipleInstancesHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Main class to launch aTunes.
 */
public final class Main {

    private Main() {
    }

    /**
     * Log some properties at start.
     * 
     * @param arguments
     * @param osManager
     */
    private static void logProgramProperties(List<String> arguments, IOSManager osManager) {
        // First line: version number
        Logger.info("Starting ", Constants.APP_NAME, " ", Constants.VERSION);

        // Second line: Java Virtual Machine Version
        Logger.info("Running in Java Virtual Machine ", System.getProperty("java.version"));

        // Third line: Application Arguments
        Logger.info("Arguments = ", arguments);

        // Fourth line: DEBUG mode
        Logger.info("Debug mode = ", Kernel.isDebug());

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
    	
    	Context.initialize("/settings/spring/state.xml", 
    			"/settings/spring/columnsets.xml", 
    			"/settings/spring/webservices.xml", 
    			"/settings/spring/navigationviews.xml", 
    			"/settings/spring/treecelldecorators.xml",
    			"/settings/spring/context.xml",
    			"/settings/spring/treegenerators.xml",
    			"/settings/spring/handlers.xml",
    			"/settings/spring/frame.xml",
    			"/settings/spring/dialogs.xml",
    			"/settings/spring/os.xml",
    			"/settings/spring/tasks.xml",
    			"/settings/spring/utils.xml",
    			"/settings/spring/lookandfeel.xml");
    	
        // Fetch arguments into a list
        List<String> arguments = StringUtils.fromStringArrayToList(args);

        // Set debug flag in kernel
        Kernel.setDebug(arguments.contains(ApplicationArguments.DEBUG));

        IOSManager osManager = Context.getBean(IOSManager.class);
        
        // Set log4j properties
        Logger.loadProperties(Kernel.isDebug(), osManager);

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
            osManager.setCustomConfigFolder(ApplicationArguments.getUserConfigFolder(arguments));

            // Set custom repository config folder if passed as argument
            osManager.setCustomRepositoryConfigFolder(ApplicationArguments.getRepositoryConfigFolder(arguments));

            // Enable uncaught exception catching
            try {
                Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        Logger.error(StringUtils.getString("Thread: ", t.getName()));
                        Logger.error(e);
                    }
                });
            } catch (Throwable t) {
                Logger.error(t);
            }

            // WE ARE CLOSING ERROR STREAM!!!
            // THIS IS DONE TO AVOID ANNOYING MESSAGES FROM SOME LIBRARIES
            System.err.close();

            // For detecting Swing threading violations
            if (Kernel.isDebug()) {
                RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
            }

            // Log program properties
            logProgramProperties(arguments, osManager);

            // Start the Kernel, which really starts application
            Kernel.startKernel(arguments);
        }

    }
}
