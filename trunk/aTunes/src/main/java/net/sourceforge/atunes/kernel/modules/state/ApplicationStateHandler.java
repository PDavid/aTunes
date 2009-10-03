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
package net.sourceforge.atunes.kernel.modules.state;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.WindowConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.frame.StandardFrame;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.amazon.AmazonService;
import net.sourceforge.atunes.kernel.modules.columns.PlayListColumns;
import net.sourceforge.atunes.kernel.modules.favorites.Favorites;
import net.sourceforge.atunes.kernel.modules.hotkeys.HotkeyHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.player.Volume;
import net.sourceforge.atunes.kernel.modules.playlist.ListOfPlayLists;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.AudioFilesRemovedListener;
import net.sourceforge.atunes.kernel.modules.repository.Repository;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.beans.ProxyBean;
import net.sourceforge.atunes.kernel.modules.tray.SystemTrayHandler;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.Timer;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.CryptoUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

/**
 * This class is responsible of read, write and apply application state, and
 * caches.
 */
public final class ApplicationStateHandler extends Handler implements AudioFilesRemovedListener {

    /** The instance. */
    private static ApplicationStateHandler instance = new ApplicationStateHandler();

    /**
     * Listeners of the state of the application
     */
    private Set<ApplicationStateChangeListener> stateChangeListeners;

    protected void initHandler() {
        RepositoryHandler.getInstance().addAudioFilesRemovedListener(this);
    }

    /**
     * Gets the single instance of ApplicationDataHandler.
     * 
     * @return single instance of ApplicationDataHandler
     */
    public static ApplicationStateHandler getInstance() {
        return instance;
    }

    @Override
    public void applicationStarted() {
    }

    /**
     * Adds a new ApplicationStateChangeListener. This listener will be notified
     * when application state is changed
     * 
     * @param listener
     */
    public void addStateChangeListener(ApplicationStateChangeListener listener) {
        if (stateChangeListeners == null) {
            stateChangeListeners = new HashSet<ApplicationStateChangeListener>();
        }
        stateChangeListeners.add(listener);
    }

    /**
     * Removes an ApplicationStateChangeListener. This listener will not be
     * notified again when application state is changed
     * 
     * @param listener
     */
    public void removeStateChangeListener(ApplicationStateChangeListener listener) {
        if (stateChangeListeners == null) {
            return;
        }
        stateChangeListeners.remove(listener);
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        // Nothing to do
    }

    /**
     * Notifies all listeners of an application state change
     */
    public void notifyApplicationStateChanged() {
        for (ApplicationStateChangeListener listener : stateChangeListeners) {
            getLogger().debug(LogCategories.HANDLER, StringUtils.getString("Call to ApplicationStateChangeListener: ", listener.getClass().getName()));
            listener.applicationStateChanged(ApplicationState.getInstance());
        }
    }

    @Override
    public void applicationFinish() {
        storeState();
    }

    /**
     * Apply state. Some properties (window maximized, position, etc) are
     * already setted in gui creation
     */
    public void applyState() {
        getLogger().debug(LogCategories.HANDLER);

        // System tray player
        if (ApplicationState.getInstance().isShowTrayPlayer()) {
            SystemTrayHandler.getInstance().initTrayPlayerIcons();
        }

        // System tray
        if (ApplicationState.getInstance().isShowSystemTray()) {
            SystemTrayHandler.getInstance().initTrayIcon();
        } else {
            VisualHandler.getInstance().setFrameDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }

        // Hotkeys
        if (ApplicationState.getInstance().isEnableHotkeys()) {
            HotkeyHandler.getInstance().enableHotkeys(ApplicationState.getInstance().getHotkeysConfig());
        } else {
            HotkeyHandler.getInstance().disableHotkeys();
        }

        // Tool Bar
        VisualHandler.getInstance().showToolBar(ApplicationState.getInstance().isShowToolBar());

        // Status Bar
        VisualHandler.getInstance().showStatusBar(ApplicationState.getInstance().isShowStatusBar());

        // Song properties visible
        VisualHandler.getInstance().showSongProperties(ApplicationState.getInstance().isShowAudioObjectProperties(), false);

        // Show navigation panel
        VisualHandler.getInstance().showNavigationPanel(ApplicationState.getInstance().isShowNavigationPanel(), false);

        // Show Context
        VisualHandler.getInstance().showContextPanel(ApplicationState.getInstance().isUseContext(), false);

        // Show navigation table
        VisualHandler.getInstance().showNavigationTable(ApplicationState.getInstance().isShowNavigationTable());

        // Navigation Panel View
        ControllerProxy.getInstance().getNavigationController().setNavigationView(ApplicationState.getInstance().getNavigationView());

        // Set volume on visual components
        Volume.setVolume(ApplicationState.getInstance().getVolume());

        // Mute
        PlayerHandler.getInstance().applyMuteState(ApplicationState.getInstance().isMuteEnabled());

        if (!VisualHandler.getInstance().isMultipleWindow()) {
            // Split panes divider location
            if (ApplicationState.getInstance().getLeftVerticalSplitPaneDividerLocation() != 0) {
                ((StandardFrame) VisualHandler.getInstance().getFrame()).setLeftVerticalSplitPaneDividerLocationAndSetWindowSize(ApplicationState.getInstance()
                        .getLeftVerticalSplitPaneDividerLocation());
            }
            if (ApplicationState.getInstance().getRightVerticalSplitPaneDividerLocation() != 0) {
                ((StandardFrame) VisualHandler.getInstance().getFrame()).setRightVerticalSplitPaneDividerLocationAndSetWindowSize(ApplicationState.getInstance()
                        .getRightVerticalSplitPaneDividerLocation());
            }
        }

        // Proxy
        AmazonService.getInstance().setProxyBean(ApplicationState.getInstance().getProxy());
    }

    /**
     * Gets file where state is stored.
     * 
     * @param useWorkDir
     *            if the working diretory should be used for storing the state
     * 
     * @return The file where state is stored
     */
    private String getPropertiesFile(boolean useWorkDir) {
        return StringUtils.getString(SystemProperties.getUserConfigFolder(useWorkDir), "/", Constants.PROPERTIES_FILE);
    }

    /**
     * Stores favorites cache.
     * 
     * @param favorites
     *            Favorites that should be persisted
     */
    public void persistFavoritesCache(Favorites favorites) {
        getLogger().debug(LogCategories.HANDLER);

        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(new FileOutputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.CACHE_FAVORITES_NAME)));
            getLogger().info(LogCategories.HANDLER, "Storing favorites information...");
            stream.writeObject(favorites);
        } catch (Exception e) {
            getLogger().error(LogCategories.HANDLER, "Could not write favorites");
            getLogger().debug(LogCategories.HANDLER, e);
        } finally {
            ClosingUtils.close(stream);
        }

        if (ApplicationState.getInstance().isSaveRepositoryAsXml()) {
            try {
                XMLUtils.writeObjectToFile(favorites, StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.XML_CACHE_FAVORITES_NAME));
                getLogger().info(LogCategories.HANDLER, "Storing favorites information...");
            } catch (Exception e) {
                getLogger().error(LogCategories.HANDLER, "Could not write favorites");
                getLogger().debug(LogCategories.HANDLER, e);
            }
        }
    }

    /**
     * Stores play lists.
     */
    public void persistPlayList() {
        getLogger().debug(LogCategories.HANDLER);

        //		try {
        //			XMLUtils.writeObjectToFile(p, StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.LAST_PLAYLIST_FILE));
        //			getLogger().info(LogCategories.HANDLER, "Play list saved");
        //		} catch (Exception e) {
        //			getLogger().error(LogCategories.HANDLER, "Could not persist playlist");
        //			getLogger().debug(LogCategories.HANDLER, e);
        //		}

        ListOfPlayLists listOfPlayLists = PlayListHandler.getInstance().getListOfPlayLists();

        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(new FileOutputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.LAST_PLAYLIST_FILE)));
            stream.writeObject(listOfPlayLists);
            getLogger().info(LogCategories.HANDLER, "Play lists saved");
        } catch (Exception e) {
            getLogger().error(LogCategories.HANDLER, "Could not persist playlists");
            getLogger().debug(LogCategories.HANDLER, e);
        } finally {
            ClosingUtils.close(stream);
        }
    }

    /**
     * Stores podcast feeds.
     * 
     * @param podcastFeeds
     *            Podcast feeds that should be persist
     */
    public void persistPodcastFeedCache(List<PodcastFeed> podcastFeeds) {
        getLogger().debug(LogCategories.HANDLER);

        try {
            XMLUtils.writeObjectToFile(podcastFeeds, StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.PODCAST_FEED_CACHE));
        } catch (Exception e) {
            getLogger().error(LogCategories.HANDLER, "Could not persist podcast feeds");
            getLogger().debug(LogCategories.HANDLER, e);
        }
    }

    /**
     * Stores radios.
     * 
     * @param radios
     *            Radios that should be persisted
     */
    public void persistRadioCache(List<Radio> radios) {
        getLogger().debug(LogCategories.HANDLER);

        try {
            XMLUtils.writeObjectToFile(radios, StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.RADIO_CACHE));
        } catch (Exception e) {
            getLogger().error(LogCategories.HANDLER, "Could not persist radios");
            getLogger().debug(LogCategories.HANDLER, e);
        }
    }

    /**
     * Persist preset radio cache.
     * 
     * @param radios
     *            the radios
     */
    public void persistPresetRadioCache(List<Radio> radios) {
        getLogger().debug(LogCategories.HANDLER);

        try {
            XMLUtils.writeObjectToFile(radios, StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.PRESET_RADIO_CACHE));
        } catch (Exception e) {
            getLogger().error(LogCategories.HANDLER, "Could not persist radios");
            getLogger().debug(LogCategories.HANDLER, e);
        }
    }

    /**
     * Stores repository cache.
     * 
     * @param repository
     *            The retrieved repository
     */

    public void persistRepositoryCache(Repository repository, boolean asXmlIfEnabled) {
        getLogger().debug(LogCategories.HANDLER);

        ObjectOutputStream oos = null;
        try {
            FileOutputStream fout = new FileOutputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.CACHE_REPOSITORY_NAME));
            oos = new ObjectOutputStream(fout);
            getLogger().info(LogCategories.HANDLER, "Serialize repository information...");
            Timer timer = new Timer();
            timer.start();
            oos.writeObject(repository);
            getLogger().info(LogCategories.HANDLER, StringUtils.getString("DONE (", timer.stop(), " seconds)"));
        } catch (Exception e) {
            getLogger().error(LogCategories.HANDLER, "Could not write serialized repository");
            getLogger().debug(LogCategories.HANDLER, e);
        } finally {
            ClosingUtils.close(oos);
        }

        if (asXmlIfEnabled && ApplicationState.getInstance().isSaveRepositoryAsXml()) {
            try {
                getLogger().info(LogCategories.HANDLER, "Storing repository information as xml...");
                Timer timer = new Timer();
                timer.start();
                XMLUtils.writeObjectToFile(repository, StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.XML_CACHE_REPOSITORY_NAME));
                getLogger().info(LogCategories.HANDLER, StringUtils.getString("DONE (", timer.stop(), " seconds)"));
            } catch (Exception e) {
                getLogger().error(LogCategories.HANDLER, "Could not write repository as xml");
                getLogger().debug(LogCategories.HANDLER, e);
            }
        }
    }

    public void persistDeviceCache(String deviceId, Repository deviceRepository) {
        getLogger().debug(LogCategories.HANDLER);

        ObjectOutputStream oos = null;
        try {
            FileOutputStream fout = new FileOutputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.FILE_SEPARATOR,
                    Constants.DEVICE_CACHE_FILE_PREFIX, deviceId));
            oos = new ObjectOutputStream(fout);
            getLogger().info(LogCategories.HANDLER, "Serialize device information...");
            long t0 = System.currentTimeMillis();
            oos.writeObject(deviceRepository);
            long t1 = System.currentTimeMillis();
            getLogger().info(LogCategories.HANDLER, StringUtils.getString("DONE (", (t1 - t0) / 1000.0, " seconds)"));
        } catch (Exception e) {
            getLogger().error(LogCategories.HANDLER, "Could not write serialized device");
            getLogger().debug(LogCategories.HANDLER, e);
        } finally {
            ClosingUtils.close(oos);
        }
    }

    /**
     * Read state stored and returns it. If there is any error trying to read
     * state then returns an object with default state
     * 
     * @return the state
     */
    public ApplicationState readState() {
        getLogger().debug(LogCategories.HANDLER);

        try {
            ApplicationState state = (ApplicationState) XMLUtils.readBeanFromFile(getPropertiesFile(Kernel.DEBUG));

            // Decrypt passwords
            if (state.getProxy() != null && state.getProxy().getEncryptedPassword() != null && state.getProxy().getEncryptedPassword().length > 0) {
                state.getProxy().setPassword(new String(CryptoUtils.decrypt(state.getProxy().getEncryptedPassword())));
            }
            if (state.getEncryptedLastFmPassword() != null && state.getEncryptedLastFmPassword().length > 0) {
                state.setLastFmPassword(new String(CryptoUtils.decrypt(state.getEncryptedLastFmPassword())));
            }
            return state;
        } catch (IOException e) {
            getLogger().info(LogCategories.HANDLER, "Could not read application state");
            return new ApplicationState();
        } catch (GeneralSecurityException e) {
            getLogger().info(LogCategories.HANDLER, "Could not decrypt passord");
            return new ApplicationState();
        } catch (Exception e) {
            getLogger().info(LogCategories.HANDLER, "Could not read application state");
            return new ApplicationState();
        }
    }

    /**
     * Reads repository cache.
     * 
     * @return The retrieved favorites
     */
    public Favorites retrieveFavoritesCache() {
        getLogger().debug(LogCategories.HANDLER);

        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.CACHE_FAVORITES_NAME)));
            getLogger().info(LogCategories.HANDLER, "Reading serialized favorites cache");
            Favorites result = (Favorites) stream.readObject();
            return result;
        } catch (InvalidClassException e) {
            //TODO remove in next version
            getLogger().error(LogCategories.HANDLER, e);
            return new Favorites();
        } catch (Exception e) {
            getLogger().info(LogCategories.HANDLER, "No serialized favorites info found");
            if (ApplicationState.getInstance().isSaveRepositoryAsXml()) {
                try {
                    getLogger().info(LogCategories.HANDLER, "Reading xml favorites cache");
                    Favorites result = (Favorites) XMLUtils.readObjectFromFile(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/",
                            Constants.XML_CACHE_FAVORITES_NAME));
                    return result;
                } catch (Exception e1) {
                    getLogger().info(LogCategories.HANDLER, "No xml favorites info found");
                    return new Favorites();
                }
            }
            return new Favorites();
        } finally {
            ClosingUtils.close(stream);
        }
    }

    /**
     * Reads play list cache.
     * 
     * @return The retrieved play list
     */

    public ListOfPlayLists retrievePlayListCache() {
        getLogger().debug(LogCategories.HANDLER);

        //		try {
        //			PlayList obj = (PlayList) XMLUtils.readObjectFromFile(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.LAST_PLAYLIST_FILE));
        //			getLogger().info(LogCategories.HANDLER, StringUtils.getString("Play list loaded (", obj.size(), " songs)"));
        //			return obj;
        //		} catch (Exception e) {
        //			return new PlayList();
        //		}

        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.LAST_PLAYLIST_FILE)));
            ListOfPlayLists obj = (ListOfPlayLists) stream.readObject();
            getLogger().info(LogCategories.HANDLER, StringUtils.getString("Play lists loaded (", obj.getPlayLists().size(), " playlists)"));
            if (obj.getPlayLists().size() == 0) {
                return ListOfPlayLists.getEmptyPlayList();
            } else {
                return obj;
            }
        } catch (FileNotFoundException e) {
            getLogger().info(LogCategories.HANDLER, "No playlist information found");
            return ListOfPlayLists.getEmptyPlayList();
        } catch (Exception e) {
            getLogger().error(LogCategories.HANDLER, e);
            return ListOfPlayLists.getEmptyPlayList();
        } finally {
            ClosingUtils.close(stream);
        }
    }

    /**
     * Reads podcast feed cache.
     * 
     * @return The retrieved podcast feeds
     */
    @SuppressWarnings("unchecked")
    public List<PodcastFeed> retrievePodcastFeedCache() {
        getLogger().debug(LogCategories.HANDLER);

        try {
            return (List<PodcastFeed>) XMLUtils.readObjectFromFile(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.PODCAST_FEED_CACHE));
        } catch (Exception e) {
            /*
             * java.util.concurrent.CopyOnWriteArrayList instead of e.g.
             * java.util.ArrayList to avoid ConcurrentModificationException
             */
            return new CopyOnWriteArrayList<PodcastFeed>();
        }
    }

    /**
     * Reads radio cache.
     * 
     * @return The retrieved radios
     */
    @SuppressWarnings("unchecked")
    public List<Radio> retrieveRadioCache() {
        getLogger().debug(LogCategories.HANDLER);
        try {
            return (List<Radio>) XMLUtils.readObjectFromFile(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.RADIO_CACHE));
        } catch (Exception e) {
            return new ArrayList<Radio>();
        }

    }

    /**
     * Reads radio cache. Preset stations. This file is not meant to be edited.
     * 
     * @return The retrieved radios
     */
    @SuppressWarnings("unchecked")
    public List<Radio> retrieveRadioPreset() {
        getLogger().debug(LogCategories.HANDLER);
        try {
            // First try user settings folder
            return (List<Radio>) XMLUtils.readObjectFromFile(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.PRESET_RADIO_CACHE));
        } catch (Exception e) {
            try {
                // Otherwise use list in application folder
                return (List<Radio>) XMLUtils.readObjectFromFile(ApplicationStateHandler.class.getResourceAsStream("/settings/" + Constants.PRESET_RADIO_CACHE));
            } catch (Exception e2) {
                return new ArrayList<Radio>();
            }
        }
    }

    /**
     * Reads repository cache.
     * 
     * @return The retrieved repository
     */
    public Repository retrieveRepositoryCache() {
        getLogger().debug(LogCategories.HANDLER);

        ObjectInputStream ois = null;
        try {
            FileInputStream fis = new FileInputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/", Constants.CACHE_REPOSITORY_NAME));
            ois = new ObjectInputStream(fis);
            getLogger().info(LogCategories.HANDLER, "Reading serialized repository cache");
            Timer timer = new Timer();
            timer.start();
            Repository result = (Repository) ois.readObject();
            getLogger().info(LogCategories.HANDLER, StringUtils.getString("Reading repository cache done (", timer.stop(), " seconds)"));
            return result;
        } catch (InvalidClassException e) {
            //TODO remove in next version
            getLogger().error(LogCategories.HANDLER, e);
            return null;
        } catch (Exception e) {
            getLogger().info(LogCategories.HANDLER, "No serialized repository info found");
            if (ApplicationState.getInstance().isSaveRepositoryAsXml()) {
                try {
                    getLogger().info(LogCategories.HANDLER, "Reading xml repository cache");
                    long t0 = System.currentTimeMillis();
                    Repository repository = (Repository) XMLUtils.readObjectFromFile(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), "/",
                            Constants.XML_CACHE_REPOSITORY_NAME));
                    long t1 = System.currentTimeMillis();
                    getLogger().info(LogCategories.HANDLER, StringUtils.getString("Reading repository cache done (", (t1 - t0) / 1000.0, " seconds)"));
                    return repository;
                } catch (Exception e1) {
                    getLogger().info(LogCategories.HANDLER, "No xml repository info found");
                    return null;
                }
            }
            return null;
        } finally {
            ClosingUtils.close(ois);
        }
    }

    /**
     * Reads device cache.
     * 
     * @return The retrieved device
     */

    public Repository retrieveDeviceCache(String deviceId) {
        getLogger().debug(LogCategories.HANDLER);

        ObjectInputStream ois = null;
        try {
            FileInputStream fis = new FileInputStream(StringUtils.getString(SystemProperties.getUserConfigFolder(Kernel.DEBUG), SystemProperties.FILE_SEPARATOR,
                    Constants.DEVICE_CACHE_FILE_PREFIX, deviceId));
            ois = new ObjectInputStream(fis);
            getLogger().info(LogCategories.HANDLER, "Reading serialized device cache");
            long t0 = System.currentTimeMillis();
            Repository result = (Repository) ois.readObject();
            long t1 = System.currentTimeMillis();
            getLogger().info(LogCategories.HANDLER, StringUtils.getString("Reading device cache done (", (t1 - t0) / 1000.0, " seconds)"));
            return result;
        } catch (Exception e) {
            getLogger().info(LogCategories.HANDLER, StringUtils.getString("No serialized device info found for deviceId: ", deviceId));
            return null;
        } finally {
            ClosingUtils.close(ois);
        }
    }

    /**
     * Stores state.
     */
    private void storeState() {
        getLogger().debug(LogCategories.HANDLER);

        if (!VisualHandler.getInstance().isMultipleWindow()) {
            // Set window location on state
            ApplicationState.getInstance().setWindowXPosition(VisualHandler.getInstance().getWindowLocation().x);
            ApplicationState.getInstance().setWindowYPosition(VisualHandler.getInstance().getWindowLocation().y);
            // Window full maximized
            ApplicationState.getInstance().setMaximized(VisualHandler.getInstance().isMaximized());
            // Set window size
            ApplicationState.getInstance().setWindowWidth(VisualHandler.getInstance().getWindowSize().width);
            ApplicationState.getInstance().setWindowHeight(VisualHandler.getInstance().getWindowSize().height);
            // Set split panes divider location
            ApplicationState.getInstance().setLeftVerticalSplitPaneDividerLocation(
                    ((StandardFrame) VisualHandler.getInstance().getFrame()).getLeftVerticalSplitPane().getDividerLocation());
            ApplicationState.getInstance().setRightVerticalSplitPaneDividerLocation(
                    ((StandardFrame) VisualHandler.getInstance().getFrame()).getRightVerticalSplitPane().getDividerLocation());
        } else {
            // Set window location on state
            ApplicationState.getInstance().setMultipleViewXPosition(VisualHandler.getInstance().getWindowLocation().x);
            ApplicationState.getInstance().setMultipleViewYPosition(VisualHandler.getInstance().getWindowLocation().y);
            // Set window size
            ApplicationState.getInstance().setMultipleViewWidth(VisualHandler.getInstance().getWindowSize().width);
            ApplicationState.getInstance().setMultipleViewHeight(VisualHandler.getInstance().getWindowSize().height);
        }
        // Set horizontal split pane divider location
        ApplicationState.getInstance().setLeftHorizontalSplitPaneDividerLocation(VisualHandler.getInstance().getFrame().getNavigationPanel().getSplitPane().getDividerLocation());

        // Equalizer
        ApplicationState.getInstance().setEqualizerSettings(PlayerHandler.getInstance().getEqualizer().getEqualizerValues());

        // Save column settings
        PlayListColumns.storeCurrentColumnSettings();

        // Encrypt passwords
        try {
            ApplicationState.getInstance().setEncryptedLastFmPassword(null);
            if (ApplicationState.getInstance().getLastFmPassword() != null && !ApplicationState.getInstance().getLastFmPassword().isEmpty()) {
                ApplicationState.getInstance().setEncryptedLastFmPassword(CryptoUtils.encrypt(ApplicationState.getInstance().getLastFmPassword().getBytes()));
            }
            if (ApplicationState.getInstance().getProxy() != null && ApplicationState.getInstance().getProxy().getPassword() != null
                    && !ApplicationState.getInstance().getProxy().getPassword().isEmpty()) {
                ApplicationState.getInstance().getProxy().setEncryptedPassword(CryptoUtils.encrypt(ApplicationState.getInstance().getProxy().getPassword().getBytes()));
            }
        } catch (GeneralSecurityException e) {
            getLogger().error(LogCategories.HANDLER, e);
        } catch (IOException e) {
            getLogger().error(LogCategories.HANDLER, e);
        }

        try {
            // Make not encrypted password transient
            BeanInfo stateInfo = Introspector.getBeanInfo(ApplicationState.class);
            for (PropertyDescriptor pd : stateInfo.getPropertyDescriptors()) {
                if (pd.getName().equals("lastFmPassword")) {
                    pd.setValue("transient", Boolean.TRUE);
                }
            }

            // Make not encrypted password transient
            BeanInfo proxyInfo = Introspector.getBeanInfo(ProxyBean.class);
            for (PropertyDescriptor pd : proxyInfo.getPropertyDescriptors()) {
                if (pd.getName().equals("password")) {
                    pd.setValue("transient", Boolean.TRUE);
                }
            }

            XMLUtils.writeBeanToFile(ApplicationState.getInstance(), getPropertiesFile(Kernel.DEBUG));
        } catch (IOException e) {
            getLogger().error(LogCategories.HANDLER, "Error storing application state");
            getLogger().error(LogCategories.HANDLER, e);
        } catch (IntrospectionException e) {
            getLogger().error(LogCategories.HANDLER, "Error storing application state");
            getLogger().error(LogCategories.HANDLER, e);
        }
    }

    @Override
    public void audioFilesRemoved(List<AudioFile> audioFiles) {
        persistPlayList();
    }
}
