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

package net.sourceforge.atunes.kernel;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.ApplicationArguments;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.misc.SystemProperties;
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

    private static Logger logger;

    /**
     * Unique instance of Kernel. To access Kernel, Kernel.getInstance() must be
     * called
     */
    private static Kernel instance;

    /** Defines if aTunes is running in debug mode. */
    private static boolean debug;

    /** Defines if aTunes will ignore look and feel. */
    private static boolean ignoreLookAndFeel;

    /** Defines if aTunes should not try to update (for Linux packages). */
    private static boolean noUpdate;
    
    /** Timer used to measure start time */
    private static Timer timer;

    /**
     * List of start listeners
     */
    private List<ApplicationStartListener> startListeners;

    /**
     * List of finish listeners
     */
    private List<ApplicationFinishListener> finishListeners;

    /**
     * Constructor of Kernel.
     */
    protected Kernel() {
        startListeners = new ArrayList<ApplicationStartListener>();
        finishListeners = new ArrayList<ApplicationFinishListener>();
    }

    /**
     * Getter of the Kernel instance.
     * 
     * @return Kernel
     */
    public static Kernel getInstance() {
        return instance;
    }

    /**
     * Adds a start listener to list of listeners. All classes that implements
     * ApplicationStartListener must call this method in order to be notified by
     * Kernel when application has started
     * 
     * @param listener
     */
    public void addStartListener(ApplicationStartListener listener) {
        this.startListeners.add(listener);
    }

    /**
     * Adds a finish listener to list of listeners. All classes that implements
     * ApplicationFinishListener must call this method in order to be notified
     * by Kernel when application is finishing
     * 
     * @param listener
     */
    public void addFinishListener(ApplicationFinishListener listener) {
        this.finishListeners.add(listener);
    }

    /**
     * Static method to create the Kernel instance. This method starts the
     * application, so should be called from the main method of the application.
     * 
     * @param args
     *            the args
     */
    public static void startKernel(final List<String> args) {
        getLogger().debug(LogCategories.START, "Starting Kernel");
        
        timer = new Timer();
        timer.start();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    LanguageSelector.setLanguage();
                    Fonts.setFontSmoothing();
                    LookAndFeelSelector.getInstance().setLookAndFeel(ApplicationState.getInstance().getLookAndFeel());
                    Fonts.initializeFonts();
                    ColorDefinitions.initColors();

                    // Show title dialog
                    GuiHandler.getInstance().showSplashScreen();
                }
            });
        } catch (Exception e) {
            getLogger().error(LogCategories.START, e);
            getLogger().error(LogCategories.START, e.getCause());
        }

        // Create kernel
        instance = new Kernel();

        // Register handlers
        AbstractHandler.registerHandlers();

        // Initialize handlers
        AbstractHandler.initHandlers();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {

                    // Init proxy settings
                    try {
                        Proxy.initProxy(Proxy.getProxy(ApplicationState.getInstance().getProxy()));
                    } catch (UnknownHostException e) {
                        getLogger().error(LogCategories.START, e);
                    } catch (IOException e) {
                        getLogger().error(LogCategories.START, e);
                    }

                }
            });
        } catch (Exception e) {
            getLogger().error(LogCategories.START, e);
            getLogger().error(LogCategories.START, e.getCause());
        }

        // Find for audio files on arguments
        final List<String> songs = new ArrayList<String>();
        for (String arg : args) {
            if (AudioFile.isValidAudioFile(arg)) {
                songs.add(arg);
            } else if (PlayListIO.isValidPlayList(arg)) {
                songs.addAll(PlayListIO.read(new File(arg)));
            }
        }

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    // Start component creation
                    instance.startCreation();
                }
            });
        } catch (Exception e) {
            getLogger().error(LogCategories.START, e);
            getLogger().error(LogCategories.START, e.getCause());
        }

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    // Start bussiness
                    instance.start(PlayListIO.getAudioObjectsFromFileNamesList(songs));
                }
            });
        } catch (Exception e) {
            getLogger().error(LogCategories.START, e);
            getLogger().error(LogCategories.START, e.getCause());
        }

    }

    /**
     * Executes actions needed before closing application, finished all
     * necessary modules and writes configuration.
     */
    private void callActionsBeforeEnd() {
        // Call all ApplicationFinishListener instances to finish
        for (ApplicationFinishListener finishListener : finishListeners) {
            try {
                if (finishListener != null) {
                    finishListener.applicationFinish();
                }
            } catch (Exception e) {
                getLogger().error(LogCategories.END, e);
            }
        }
    }

    /**
     * Called when closing application
     */
    public void finish() {
        Timer timer = new Timer();
        try {
            timer.start();
            getLogger().info(LogCategories.END, StringUtils.getString("Closing ", Constants.APP_NAME, " ", Constants.VERSION.toString()));
            // Call actions needed before closing application
            callActionsBeforeEnd();
        } finally {
            getLogger().info(LogCategories.END, StringUtils.getString("Application finished (", StringUtils.toString(timer.stop(), 3), " seconds)"));
            getLogger().info(LogCategories.END, "Goodbye!!");
            // Exit normally
            System.exit(0);
        }
    }

    /**
     * Once all application is loaded, it's time to load data (repository,
     * playlist, ...)
     * 
     * @param playList
     *            the play list
     */
    void start(List<AudioObject> playList) {
        try {
            ApplicationStateHandler.getInstance().applyState();

            GuiHandler.getInstance().setFullFrameVisible(true);
            //Hide title dialog
            GuiHandler.getInstance().hideSplashScreen();

            // Just after all events in EDT are done set repository and play lists, then call post-init actions
            SwingUtilities.invokeLater(new RepositoryAndPlayListLoadRunnable(playList));

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    callActionsAfterStart();
                }
            });

            getLogger().info(LogCategories.START, StringUtils.getString("Application started (", StringUtils.toString(timer.stop(), 3), " seconds)"));
            timer = null;
            
        } catch (Exception e) {
            getLogger().error(LogCategories.KERNEL, e);
        }
    }

    /**
     * Call actions after start.
     */
    void callActionsAfterStart() {
        // Call all ApplicationStartListener instances to finish
        for (ApplicationStartListener startListener : startListeners) {
            try {
                if (startListener != null) {
                    startListener.applicationStarted();
                }
            } catch (Exception e) {
                getLogger().error(LogCategories.START, e);
            }
        }
        // TODO: Move this to webservices handler
        LastFmService.getInstance().submitCacheToLastFm();
    }

    /**
     * Starts controllers associated to visual classes.
     */
    private void startControllers() {
        ControllerProxy.getInstance();
    }

    /**
     * Creates all objects of aTunes: visual objects, controllers, and handlers.
     */
    void startCreation() {
        getLogger().debug(LogCategories.START, "Starting components");

        startVisualization();
        startControllers();
        GuiHandler.getInstance().setTitleBar("");
    }

    /**
     * Starts visual objects.
     */
    private void startVisualization() {
        GuiHandler.getInstance().startVisualization();
    }

    /**
     * Called when restarting application
     */
    public void restart() {
        try {
            // Store all configuration and finish all active modules
            callActionsBeforeEnd();

            // Build a process builder with OS-specific command and saved arguments
            ProcessBuilder pb = new ProcessBuilder(SystemProperties.OS.getLaunchCommand(), ApplicationArguments.getSavedArguments());

            System.out.println(pb.command().toString());

            // Start new application instance
            pb.start();

        } catch (IOException e) {
            getLogger().error(LogCategories.KERNEL, e);
        } finally {
            // Exit normally
            System.exit(0);
        }
    }

    private static class RepositoryAndPlayListLoadRunnable implements Runnable {

        private List<AudioObject> playList;

        public RepositoryAndPlayListLoadRunnable(List<AudioObject> playList) {
            this.playList = playList;
        }

        @Override
        public void run() {
            RepositoryHandler.getInstance().applyRepository();
            PlayListHandler.getInstance().setPlayLists();

            if (!playList.isEmpty()) {
                PlayListHandler.getInstance().addToPlayListAndPlay(playList);
                ControllerProxy.getInstance().getPlayListController().refreshPlayList();
            }
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

    private static Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }
}
