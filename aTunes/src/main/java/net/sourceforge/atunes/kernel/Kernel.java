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
import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.TempFolder;
import net.sourceforge.atunes.misc.Timer;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.StringUtils;

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
        Logger.debug(LogCategories.START, "Starting Kernel");
        
        timer = new Timer();
        timer.start();

        LanguageSelector.setLanguage();
        ColorDefinitions.initColors();
        // Init proxy settings
        try {
            Proxy.initProxy(Proxy.getProxy(ApplicationState.getInstance().getProxy()));
        } catch (UnknownHostException e) {
            Logger.error(LogCategories.START, e);
        } catch (IOException e) {
            Logger.error(LogCategories.START, e);
        }


        try {
        	// Call invokeAndWait to wait until splash screen is visible
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    LookAndFeelSelector.getInstance().setLookAndFeel(ApplicationState.getInstance().getLookAndFeel());
                }
            });
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
            
                    // Show title dialog
                    GuiHandler.getInstance().showSplashScreen();
                }
            });
        } catch (InvocationTargetException e) {
            Logger.error(LogCategories.START, e);
            Logger.error(LogCategories.START, e.getCause());
        } catch (InterruptedException e) {
            Logger.error(LogCategories.START, e);
            Logger.error(LogCategories.START, e.getCause());
		}

        // Register and initialize handlers
        AbstractHandler.registerAndInitializeHandlers();

        // Find for audio files on arguments
        final List<String> songs = new ArrayList<String>();
        for (String arg : args) {
            if (AudioFile.isValidAudioFile(arg)) {
                songs.add(arg);
            } else if (PlayListIO.isValidPlayList(arg)) {
                songs.addAll(PlayListIO.read(new File(arg)));
            }
        }

        SwingUtilities.invokeLater(new Runnable() {
        	@Override
        	public void run() {
        		// Start component creation
        		startCreation();

            	callActionsAfterStart(PlayListIO.getAudioObjectsFromFileNamesList(songs));
            	Logger.info(LogCategories.START, StringUtils.getString("Application started (", StringUtils.toString(timer.stop(), 3), " seconds)"));
            	timer = null;
        	}
        });
    }

    /**
     * Called when closing application
     */
    public static void finish() {
        Timer timer = new Timer();
        try {
            timer.start();
            Logger.info(LogCategories.END, StringUtils.getString("Closing ", Constants.APP_NAME, " ", Constants.VERSION.toString()));
            TempFolder.getInstance().removeAllFiles();
            
            ApplicationLifeCycleListeners.applicationFinish();
        } finally {
            Logger.info(LogCategories.END, StringUtils.getString("Application finished (", StringUtils.toString(timer.stop(), 3), " seconds)"));
            Logger.info(LogCategories.END, "Goodbye!!");
            // Exit normally
            System.exit(0);
        }
    }

    /**
     * Call actions after start.
     */
    static void callActionsAfterStart(List<AudioObject> playList) {
    	ApplicationLifeCycleListeners.applicationStarted(playList);
    	ApplicationLifeCycleListeners.allHandlersInitialized();
    }

    /**
     * Creates all objects of aTunes: visual objects, controllers, and handlers.
     */
    static void startCreation() {
        Logger.debug(LogCategories.START, "Starting components");
        GuiHandler.getInstance().startVisualization();
        GuiHandler.getInstance().setTitleBar("");
    }

    /**
     * Called when restarting application
     */
    public static void restart() {
        try {
            // Store all configuration and finish all active modules
        	ApplicationLifeCycleListeners.applicationFinish();

            // Build a process builder with OS-specific command and saved arguments
            ProcessBuilder pb = new ProcessBuilder(OsManager.getLaunchCommand(), ApplicationArguments.getSavedArguments());

            System.out.println(pb.command().toString());

            // Start new application instance
            pb.start();

        } catch (IOException e) {
            Logger.error(LogCategories.KERNEL, e);
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
