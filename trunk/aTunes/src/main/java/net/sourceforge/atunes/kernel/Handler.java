/*
 * aTunes 2.0.0-SNAPSHOT
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.sourceforge.atunes.kernel.modules.cdripper.RipperHandler;
import net.sourceforge.atunes.kernel.modules.command.CommandHandler;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.hotkeys.HotkeyHandler;
import net.sourceforge.atunes.kernel.modules.instances.MultipleInstancesHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.notify.NotifyHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.plugins.PluginsHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.radio.RadioHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.favorites.FavoritesHandler;
import net.sourceforge.atunes.kernel.modules.search.SearchHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateChangeListener;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateHandler;
import net.sourceforge.atunes.kernel.modules.updates.UpdateHandler;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.StringUtils;

public abstract class Handler implements ApplicationStartListener, ApplicationFinishListener, ApplicationStateChangeListener {

    /**
     * Logger for handlers
     */
    private static Logger logger;

    private static List<Class<? extends Handler>> handlerClasses;

    private static ExecutorService executorService = Executors.newFixedThreadPool(3);

    static {
        // TODO: Add here every new Handler
        handlerClasses = new ArrayList<Class<? extends Handler>>();
        handlerClasses.add(ApplicationStateHandler.class);
        handlerClasses.add(PodcastFeedHandler.class);
        handlerClasses.add(ContextHandler.class);
        handlerClasses.add(RipperHandler.class);
        handlerClasses.add(CommandHandler.class);
        handlerClasses.add(DeviceHandler.class);
        handlerClasses.add(FavoritesHandler.class);
        handlerClasses.add(HotkeyHandler.class);
        handlerClasses.add(MultipleInstancesHandler.class);
        handlerClasses.add(NavigationHandler.class);
        handlerClasses.add(NotifyHandler.class);
        handlerClasses.add(PlayerHandler.class);
        handlerClasses.add(PlayListHandler.class);
        handlerClasses.add(PluginsHandler.class);
        handlerClasses.add(RadioHandler.class);
        handlerClasses.add(RepositoryHandler.class);
        handlerClasses.add(SearchHandler.class);
        handlerClasses.add(UpdateHandler.class);
        handlerClasses.add(VisualHandler.class);
    }

    private static List<Handler> handlers = new ArrayList<Handler>();

    /**
     * Initializes handler
     */
    protected abstract void initHandler();

    /**
     * Returns a task to be executed before initialize handler By default
     * handlers do not define any task
     * 
     * @return
     */
    protected Runnable getPreviousInitializationTask() {
        return null;
    }

    /**
     * Registers handler
     * 
     * @param handler
     */
    private static final void registerHandler(Handler handler) {
        Kernel.getInstance().addStartListener(handler);
        Kernel.getInstance().addFinishListener(handler);
        ApplicationStateHandler.getInstance().addStateChangeListener(handler);
    }

    /**
     * Getter for logger
     * 
     * @return Logger
     */
    protected final static Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    /**
     * Creates and registers all defined handlers
     */
    static void registerHandlers() {
        // Instance handlers
        for (Class<? extends Handler> handlerClass : handlerClasses) {
            try {
                Method method = handlerClass.getMethod("getInstance", new Class[] {});
                handlers.add((Handler) method.invoke(null, new Object[] {}));
            } catch (SecurityException e) {
                getLogger().error(LogCategories.HANDLER, e);
            } catch (NoSuchMethodException e) {
                getLogger().error(LogCategories.HANDLER, e);
            } catch (IllegalArgumentException e) {
                getLogger().error(LogCategories.HANDLER, e);
            } catch (IllegalAccessException e) {
                getLogger().error(LogCategories.HANDLER, e);
            } catch (InvocationTargetException e) {
                getLogger().error(LogCategories.HANDLER, e);
            }
        }

        // Register handlers
        for (Handler handler : handlers) {
            registerHandler(handler);
            getLogger().debug(LogCategories.HANDLER, StringUtils.getString("Registered handler: ", handler.getClass().getName()));
        }

        // Execute previous initialization tasks
        for (Handler handler : handlers) {
            Runnable task = handler.getPreviousInitializationTask();
            if (task != null) {
                executorService.submit(task);
            }
        }
        executorService.shutdown();
    }

    /**
     * Initialize handlers
     */
    static void initHandlers() {
        // First join to all previous initialization tasks
        try {
            executorService.awaitTermination(1000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error(LogCategories.HANDLER, e);
        }

        // Initialize handlers
        for (Handler handler : handlers) {
            handler.initHandler();
            getLogger().debug(LogCategories.HANDLER, StringUtils.getString("Initialized handler: ", handler.getClass().getName()));
        }

    }
}
