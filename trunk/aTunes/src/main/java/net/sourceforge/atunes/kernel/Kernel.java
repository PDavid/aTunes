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

package net.sourceforge.atunes.kernel;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.ApplicationArguments;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.debug.CheckThreadViolationRepaintManager;
import net.sourceforge.atunes.kernel.modules.proxy.ExtendedProxy;
import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.ITemporalDiskStorage;
import net.sourceforge.atunes.model.IUIHandler;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;

/**
 * The Kernel is the class responsible of create and interconnect all modules of
 * aTunes.
 */
public final class Kernel {

    /** Defines if aTunes is running in debug mode. */
    private boolean debug;

    /** Defines if aTunes will ignore look and feel. */
    private boolean ignoreLookAndFeel;

    /** Defines if aTunes should not try to update (for Linux packages). */
    private boolean noUpdate;
    
    /** Timer used to measure start time */
    private Timer timer;
    
    private IState state;
    
    public void initialize(List<String> arguments) {
        // Set debug flag in kernel
        setDebug(arguments.contains(ApplicationArguments.DEBUG));
        // Set ignore look and feel flag in kernel
    	setIgnoreLookAndFeel(arguments.contains(ApplicationArguments.IGNORE_LOOK_AND_FEEL));
        // Set no update flag in kernel
    	setNoUpdate(arguments.contains(ApplicationArguments.NO_UPDATE));
        // For detecting Swing threading violations
        if (isDebug()) {
            RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
        }
    }
    
    /**
     * Static method to create the Kernel instance. This method starts the
     * application, so should be called from the main method of the application.
     */
    public void startKernel() {
        Logger.debug("Starting Kernel");
        
        timer = new Timer();
        timer.start();

        state = (IState) Context.getBean(IState.class);
        
        new LanguageSelector().setLanguage(state);
        
        ColorDefinitions.initColors();
        // Init proxy settings
        try {
            ExtendedProxy.initProxy(ExtendedProxy.getProxy(state.getProxy()));
        } catch (UnknownHostException e) {
            Logger.error(e);
        } catch (IOException e) {
            Logger.error(e);
        }


        try {
        	// Call invokeAndWait to wait until splash screen is visible
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    Context.getBean(ILookAndFeelManager.class).setLookAndFeel(state.getLookAndFeel(), state, Context.getBean(IOSManager.class));
                    
                    IFrame frame = Context.getBean(IFrame.class);
                    AbstractHandler.setFrameForHandlers(frame);
                }
            });
        } catch (InvocationTargetException e) {
            Logger.error(e);
            Logger.error(e.getCause());
        } catch (InterruptedException e) {
            Logger.error(e);
            Logger.error(e.getCause());
		}

        // Register and initialize handlers
        AbstractHandler.registerAndInitializeHandlers(state);

        SwingUtilities.invokeLater(new Runnable() {
        	@Override
        	public void run() {
        		// Start component creation
        		startCreation();

            	callActionsAfterStart();
            	Logger.info(StringUtils.getString("Application started (", StringUtils.toString(timer.stop(), 3), " seconds)"));
            	timer = null;
        	}
        });

        // Call user interaction
        Context.getBean(ApplicationLifeCycleListeners.class).doUserInteraction(
        		Context.getBean(ApplicationLifeCycleListeners.class).getUserInteractionRequests());
    }

    /**
     * Called when closing application
     */
    public void finish() {
        Timer timer = new Timer();
        try {
            timer.start();
            Logger.info(StringUtils.getString("Closing ", Constants.APP_NAME, " ", Constants.VERSION.toString()));
            Context.getBean(ITemporalDiskStorage.class).removeAll();
            
            Context.getBean(ApplicationLifeCycleListeners.class).applicationFinish();
            
            Context.getBean(ITaskService.class).shutdownService();
            
        } finally {
            Logger.info(StringUtils.getString("Application finished (", StringUtils.toString(timer.stop(), 3), " seconds)"));
            Logger.info("Goodbye!!");
            // Exit normally
            System.exit(0);
        }
    }

    /**
     * Call actions after start.
     */
    void callActionsAfterStart() {
    	Context.getBean(ApplicationLifeCycleListeners.class).applicationStarted();
    	Context.getBean(ApplicationLifeCycleListeners.class).allHandlersInitialized();
    }

    /**
     * Creates all objects of aTunes: visual objects, controllers, and handlers.
     */
    private void startCreation() {
        Logger.debug("Starting components");
        Context.getBean(IUIHandler.class).startVisualization();
    }

    /**
     * Called when restarting application
     */
    public void restart() {
        try {
            // Store all configuration and finish all active modules
        	Context.getBean(ApplicationLifeCycleListeners.class).applicationFinish();

        	IOSManager osManager = Context.getBean(IOSManager.class);
        	
            // Build a process builder with OS-specific command and saved arguments
        	String parameters = osManager.getLaunchParameters();
            ProcessBuilder pb = null;
            if (parameters != null && !parameters.trim().isEmpty()) {
            	pb = new ProcessBuilder(osManager.getLaunchCommand(), parameters, Context.getBean(ApplicationArguments.class).getSavedArguments(Context.getBean(ICommandHandler.class)));
            } else {
            	pb = new ProcessBuilder(osManager.getLaunchCommand(), Context.getBean(ApplicationArguments.class).getSavedArguments(Context.getBean(ICommandHandler.class)));

            }

            // Start new application instance
            pb.start();

        } catch (IOException e) {
            Logger.error(e);
        } finally {
            // Exit normally
            System.exit(0);
        }
    }

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug
     *            the debug to set
     */
    private void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * @return the ignoreLookAndFeel
     */
    public boolean isIgnoreLookAndFeel() {
        return ignoreLookAndFeel;
    }

    /**
     * @param ignoreLookAndFeel
     *            the ignoreLookAndFeel to set
     */
    private void setIgnoreLookAndFeel(boolean ignoreLookAndFeel) {
    	this.ignoreLookAndFeel = ignoreLookAndFeel;
    }

    /**
     * @return the noUpdate
     */
    public boolean isNoUpdate() {
        return noUpdate;
    }

    /**
     * @param noUpdate
     *            the noUpdate to set
     */
    private void setNoUpdate(boolean noUpdate) {
    	this.noUpdate = noUpdate;
    }
}
