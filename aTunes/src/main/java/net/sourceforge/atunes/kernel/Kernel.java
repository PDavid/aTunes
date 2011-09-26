/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.ApplicationArguments;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.proxy.ExtendedProxy;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IAudioObject;
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
public class Kernel {

    /** Defines if aTunes is running in debug mode. */
    private static boolean debug;

    /** Defines if aTunes will ignore look and feel. */
    private static boolean ignoreLookAndFeel;

    /** Defines if aTunes should not try to update (for Linux packages). */
    private static boolean noUpdate;
    
    /** Timer used to measure start time */
    private static Timer timer;
    
    private static IState state;

	/**
	 * True to enable plugins
	 */
	private static boolean enablePlugins;

    /**
     * Constructor of Kernel.
     */
    private Kernel() {}

    /**
     * Static method to create the Kernel instance. This method starts the
     * application, so should be called from the main method of the application.
     * 
     * @param args
     *            the args
     */
    public static void startKernel(final List<String> args) {
        Logger.debug("Starting Kernel");
        
        timer = new Timer();
        timer.start();

        state = (IState) Context.getBean(IState.class);
        
        LanguageSelector.setLanguage(state);
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

        // Find for audio files on arguments
        final List<String> songs = new ArrayList<String>();
        for (String arg : args) {
            if (AudioFile.isValidAudioFile(arg)) {
                songs.add(arg);
            } else if (PlayListIO.isValidPlayList(arg)) {
                songs.addAll(PlayListIO.read(new File(arg), Context.getBean(IOSManager.class)));
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
        	@Override
        	public void run() {
        		// Start component creation
        		startCreation();

            	callActionsAfterStart(PlayListIO.getAudioObjectsFromFileNamesList(songs));
            	Logger.info(StringUtils.getString("Application started (", StringUtils.toString(timer.stop(), 3), " seconds)"));
            	timer = null;
        	}
        });

        // Call user interaction
        ApplicationLifeCycleListeners.doUserInteraction(ApplicationLifeCycleListeners.getUserInteractionRequests());
    }

    /**
     * Called when closing application
     */
    public static void finish() {
        Timer timer = new Timer();
        try {
            timer.start();
            Logger.info(StringUtils.getString("Closing ", Constants.APP_NAME, " ", Constants.VERSION.toString()));
            Context.getBean(ITemporalDiskStorage.class).removeAll();
            
            ApplicationLifeCycleListeners.applicationFinish();
            
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
    static void callActionsAfterStart(List<IAudioObject> playList) {
    	ApplicationLifeCycleListeners.applicationStarted(playList);
    	ApplicationLifeCycleListeners.allHandlersInitialized();
    }

    /**
     * Creates all objects of aTunes: visual objects, controllers, and handlers.
     */
    static void startCreation() {
        Logger.debug("Starting components");
        Context.getBean(IUIHandler.class).startVisualization();
    }

    /**
     * Called when restarting application
     */
    public static void restart() {
        try {
            // Store all configuration and finish all active modules
        	ApplicationLifeCycleListeners.applicationFinish();

        	IOSManager osManager = Context.getBean(IOSManager.class);
        	
            // Build a process builder with OS-specific command and saved arguments
        	String parameters = osManager.getLaunchParameters();
            ProcessBuilder pb = null;
            if (parameters != null && !parameters.trim().isEmpty()) {
            	pb = new ProcessBuilder(osManager.getLaunchCommand(), parameters, ApplicationArguments.getSavedArguments());
            } else {
            	pb = new ProcessBuilder(osManager.getLaunchCommand(), ApplicationArguments.getSavedArguments());

            }

            System.out.println(pb.command().toString());

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
    public static boolean isDebug() {
        return debug;
    }

    /**
     * @param debug
     *            the debug to set
     */
    public static void setDebug(boolean debug) {
        Kernel.debug = debug;
    }

    /**
     * @return the ignoreLookAndFeel
     */
    public static boolean isIgnoreLookAndFeel() {
        return ignoreLookAndFeel;
    }

    /**
     * @param ignoreLookAndFeel
     *            the ignoreLookAndFeel to set
     */
    public static void setIgnoreLookAndFeel(boolean ignoreLookAndFeel) {
        Kernel.ignoreLookAndFeel = ignoreLookAndFeel;
    }

    /**
     * @return the noUpdate
     */
    public static boolean isNoUpdate() {
        return noUpdate;
    }

    /**
     * @param noUpdate
     *            the noUpdate to set
     */
    public static void setNoUpdate(boolean noUpdate) {
        Kernel.noUpdate = noUpdate;
    }
    
    /**
     * @param enable
     */
    public static void setEnablePlugins(boolean enable) {
    	Kernel.enablePlugins = enable;
    }

	/**
	 * @return the enablePlugins
	 */
	public static boolean isEnablePlugins() {
		return enablePlugins;
	}
}
