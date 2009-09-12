package net.sourceforge.atunes.kernel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.cdripper.RipperHandler;
import net.sourceforge.atunes.kernel.modules.command.CommandHandler;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.favorites.FavoritesHandler;
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

    private static List<Thread> initializationTasks;

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
        initializationTasks = new ArrayList<Thread>();
        for (Handler handler : handlers) {
            Runnable task = handler.getPreviousInitializationTask();
            if (task != null) {
                Thread thread = new Thread(task);
                initializationTasks.add(thread);
                thread.start();
            }
        }
    }

    /**
     * Initialize handlers
     */
    static void initHandlers() {
        // First join to all previous initialization tasks
        for (Thread thread : initializationTasks) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                getLogger().error(LogCategories.HANDLER, e);
            }
        }
        initializationTasks = null;

        // Initialize handlers
        for (Handler handler : handlers) {
            handler.initHandler();
            getLogger().debug(LogCategories.HANDLER, StringUtils.getString("Initialized handler: ", handler.getClass().getName()));
        }

    }
}
