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
import net.sourceforge.atunes.gui.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.modules.command.CommandHandler;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.device.DeviceConnectionMonitor;
import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.instances.MultipleInstancesHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.plugins.PluginsHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.proxy.Proxy;
import net.sourceforge.atunes.kernel.modules.radio.RadioHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.kernel.modules.updates.UpdateHandler;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
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

    /** Logger. */
    static Logger logger = new Logger();

    /**
     * Unique instance of Kernel. To access Kernel, Kernel.getInstance() must be
     * called
     */
    static Kernel instance;

    /** Defines if aTunes is running in debug mode. */
    public static boolean DEBUG;

    /** Defines if aTunes will ignore look and feel. */
    public static boolean IGNORE_LOOK_AND_FEEL;

    /** Defines if aTunes should not try to update (for Linux packages). */
    public static boolean NO_UPDATE;

    /**
     * List of finish listeners
     */
    private List<ApplicationFinishListener> finishListeners;

    /**
     * Constructor of Kernel.
     */
    protected Kernel() {
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
        logger.debug(LogCategories.START, "Starting Kernel");

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    instance = new Kernel();

                    // Init proxy settings
                    try {
                        Proxy.initProxy(Proxy.getProxy(ApplicationState.getInstance().getProxy()));
                    } catch (UnknownHostException e) {
                        logger.error(LogCategories.START, e);
                    } catch (IOException e) {
                        logger.error(LogCategories.START, e);
                    }
                    // Add application finish listener
                    Kernel.getInstance().addFinishListener(MultipleInstancesHandler.getInstance());
                    
                    // Set font smoothing
                    Fonts.setFontSmoothing();
                    // Set look and feel
                    LookAndFeelSelector.setLookAndFeel(ApplicationState.getInstance().getSkin());
                    ColorDefinitions.initColors();
                    // Set language
                    LanguageSelector.setLanguage();
                    // Init fonts
                    Fonts.initializeFonts();
                }
            });
        } catch (Exception e) {
            logger.error(LogCategories.START, e);
            logger.error(LogCategories.START, e.getCause());
        }

        // Show title dialog
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    VisualHandler.getInstance().showSplashScreen();
                }
            });
        } catch (Exception e) {
            logger.error(LogCategories.START, e);
            logger.error(LogCategories.START, e.getCause());
        }

        // Read caches
        Scheduler.runInParallel(true, RadioHandler.getInstance().getReadRadiosRunnable(), PodcastFeedHandler.getInstance().getReadPodcastFeedsRunnable(), FavoritesHandler
                .getInstance().getReadFavotiresRunnable(), PlayListHandler.getInstance().getReadPlayListsRunnable(), RepositoryHandler.getInstance()
                .getReadRepositoryFromCacheRunnable());

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
                    // Init plugins
                    PluginsHandler.getInstance().initPlugins();                    
                    // Start component creation
                    instance.startCreation();
                    // Start bussiness
                    instance.start(PlayListIO.getAudioObjectsFromFileNamesList(songs));
                }
            });
        } catch (Exception e) {
            logger.error(LogCategories.START, e);
            logger.error(LogCategories.START, e.getCause());
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
                logger.error(LogCategories.END, e);
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
            logger.info(LogCategories.END, StringUtils.getString("Closing ", Constants.APP_NAME, " ", Constants.VERSION.toString()));
            // Stop must be called explicitely to avoid playback after user closed app
            PlayerHandler.getInstance().stopCurrentAudioObject(true);
            // Call actions needed before closing application
            callActionsBeforeEnd();
        } finally {
            logger.info(LogCategories.END, StringUtils.getString("Application finished (", StringUtils.toString(timer.stop(), 3), " seconds)"));
            logger.info(LogCategories.END, "Goodbye!!");
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
    void start(final List<AudioObject> playList) {
        try {
            Timer timer = new Timer();
            timer.start();

            ApplicationStateHandler.getInstance().applyState();

            VisualHandler.getInstance().setFullFrameVisible(true);
            //Hide title dialog
            VisualHandler.getInstance().hideSplashScreen();

            // Just after all events in EDT are done set repository and play lists, then call post-init actions
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    RepositoryHandler.getInstance().setRepository();

                    PlayerHandler.getInstance().initHandler();

                    PlayListHandler.getInstance().setPlayLists();
                    if (!playList.isEmpty()) {
                        PlayListHandler.getInstance().addToPlayListAndPlay(playList);
                        ControllerProxy.getInstance().getPlayListController().refreshPlayList();
                    }
                    
                    CommandHandler.getInstance().initActions();
                    
                    callActionsAfterStart();
                }
            });

            logger.info(LogCategories.START, StringUtils.getString("Application started (", StringUtils.toString(timer.stop(), 3), " seconds)"));

        } catch (Exception e) {
            logger.error(LogCategories.KERNEL, e);
        }
    }

    /**
     * Call actions after start.
     */
    void callActionsAfterStart() {
        // Check commands found in application arguments and execute them
        Scheduler.scheduleTaskAfter("Execute commands", new Runnable() {
            @Override
            public void run() {
                CommandHandler.getInstance().runCommands(ApplicationArguments.getSavedCommands());
            }
        }, 1);

        if (ApplicationState.getInstance().isPlayAtStartup()) {
            Scheduler.scheduleTaskAfter("Start Play", PlayListHandler.getInstance().getStartToPlayRunnable(), 3);
        }

        Scheduler.scheduleTaskAfter("Repository Post Init Actions", RepositoryHandler.getInstance().getAfterStartActionsRunnable(), 3);
        Scheduler.scheduleTaskAfter("Favorite Post Init Actions", FavoritesHandler.getInstance().getAfterStartActionsRunnable(), 3);

        ContextHandler.getInstance().submitCacheToLastFm();

        Scheduler.scheduleTaskAfter("Device Monitor", new Runnable() {
            @Override
            public void run() {
                // Start device monitor
                DeviceConnectionMonitor.startMonitor();
                DeviceConnectionMonitor.addListener(DeviceHandler.getInstance());
            }
        }, 10);

        Scheduler.scheduleTaskAfter("Podcast Retriever", new Runnable() {
            @Override
            public void run() {
                // Start podcast retriever
                PodcastFeedHandler.getInstance().startPodcastFeedEntryDownloadChecker();
                PodcastFeedHandler.getInstance().startPodcastFeedEntryRetriever();
            }
        }, 15);
        if (!NO_UPDATE) {
            Scheduler.scheduleTaskAfter("Check Updates", new Runnable() {
                @Override
                public void run() {
                    // Check for updates
                    UpdateHandler.getInstance().checkUpdates(ApplicationState.getInstance().getProxy(), false, false);
                }
            }, 20);
        }
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
        logger.debug(LogCategories.START, "Starting components");

        startVisualization();
        startControllers();
        VisualHandler.getInstance().setTitleBar("");
    }

    /**
     * Starts visual objects.
     */
    private void startVisualization() {
        VisualHandler.getInstance().startVisualization();
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
            logger.error(LogCategories.KERNEL, e);
        } finally {
            // Exit normally
            System.exit(0);
        }
    }
}
