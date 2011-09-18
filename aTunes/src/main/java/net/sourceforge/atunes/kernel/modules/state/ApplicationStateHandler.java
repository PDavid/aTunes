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

package net.sourceforge.atunes.kernel.modules.state;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.dialogs.editPreferences.EditPreferencesDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.playlist.ListOfPlayLists;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.exception.InconsistentRepositoryException;
import net.sourceforge.atunes.kernel.modules.repository.favorites.Favorites;
import net.sourceforge.atunes.kernel.modules.statistics.Statistics;
import net.sourceforge.atunes.misc.Timer;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.Repository;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.XMLUtils;

/**
 * This class is responsible of read, write and apply application state, and
 * caches.
 */
public final class ApplicationStateHandler extends AbstractHandler {

	/** The instance. */
    private static ApplicationStateHandler instance = new ApplicationStateHandler();

    /**
     * Listeners of the state of the application
     */
    private Set<ApplicationStateChangeListener> stateChangeListeners;

    @Override
    protected void initHandler() {
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
    public void applicationStarted(List<IAudioObject> playList) {
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
    public void applicationStateChanged(IState newState) {
        // Nothing to do
    }

    /**
     * Notifies all listeners of an application state change
     */
    public void notifyApplicationStateChanged() {
        try {
            for (ApplicationStateChangeListener listener : stateChangeListeners) {
                Logger.debug("Call to ApplicationStateChangeListener: ", listener.getClass().getName());
                listener.applicationStateChanged(getState());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void applicationFinish() {
    }

    /**
     * Stores favorites cache.
     * 
     * @param favorites
     *            Favorites that should be persisted
     */
    public void persistFavoritesCache(Favorites favorites) {
        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(new FileOutputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.CACHE_FAVORITES_NAME)));
            Logger.info("Storing favorites information...");
            stream.writeObject(favorites);
        } catch (IOException e) {
            Logger.error("Could not write favorites");
            Logger.debug(e);
        } finally {
            ClosingUtils.close(stream);
        }

        if (getState().isSaveRepositoryAsXml()) {
            try {
                XMLUtils.writeObjectToFile(favorites, StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_FAVORITES_NAME));
                Logger.info("Storing favorites information...");
            } catch (Exception e) {
                Logger.error("Could not write favorites");
                Logger.debug(e);
            }
        }
    }

    /**
     * Stores statistics cache.
     * 
     * @param statistics
     *            Statistics that should be persisted
     */
    public synchronized void persistStatisticsCache(Statistics statistics) {
        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(new FileOutputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.CACHE_STATISTICS_NAME)));
            Logger.info("Storing statistics information...");
            stream.writeObject(statistics);
        } catch (IOException e) {
            Logger.error("Could not write statistics");
            Logger.debug(e);
        } finally {
            ClosingUtils.close(stream);
        }

        if (getState().isSaveRepositoryAsXml()) {
            try {
                XMLUtils.writeObjectToFile(statistics, StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_STATISTICS_NAME));
                Logger.info("Storing statistics information...");
            } catch (Exception e) {
                Logger.error("Could not write statistics");
                Logger.debug(e);
            }
        }
    }

    /**
     * Stores play lists definition
     */
    public void persistPlayListsDefinition(ListOfPlayLists listOfPlayLists) {
        try {
            XMLUtils.writeObjectToFile(listOfPlayLists, StringUtils.getString(getUserConfigFolder(), "/", Constants.PLAYLISTS_FILE));
            Logger.info("Playlists definition saved");
        } catch (IOException e) {
            Logger.error("Could not persist playlists definition");
            Logger.debug(e);
        }
    }

    /**
     * Stores play lists contents
     * 
     * @param playListsContents
     */
    public void persistPlayListsContents(List<List<IAudioObject>> playListsContents) {
        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(new FileOutputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.PLAYLISTS_CONTENTS_FILE)));
            stream.writeObject(playListsContents);
            Logger.info("Playlists contents saved");
        } catch (IOException e) {
            Logger.error("Could not persist playlists contents");
            Logger.debug(e);
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
    public void persistPodcastFeedCache(List<IPodcastFeed> podcastFeeds) {
        try {
            XMLUtils.writeObjectToFile(podcastFeeds, StringUtils.getString(getUserConfigFolder(), "/", Constants.PODCAST_FEED_CACHE));
        } catch (IOException e) {
            Logger.error("Could not persist podcast feeds");
            Logger.debug(e);
        }
    }

    /**
     * Stores radios.
     * 
     * @param radios
     *            Radios that should be persisted
     */
    public void persistRadioCache(List<IRadio> radios) {
        try {
            XMLUtils.writeObjectToFile(radios, StringUtils.getString(getUserConfigFolder(), "/", Constants.RADIO_CACHE));
        } catch (IOException e) {
            Logger.error("Could not persist radios");
            Logger.debug(e);
        }
    }

    /**
     * Persist preset radio cache.
     * 
     * @param radios
     *            the radios
     */
    public void persistPresetRadioCache(List<IRadio> radios) {
        try {
            XMLUtils.writeObjectToFile(radios, StringUtils.getString(getUserConfigFolder(), "/", Constants.PRESET_RADIO_CACHE));
        } catch (IOException e) {
            Logger.error("Could not persist radios");
            Logger.debug(e);
        }
    }

    /**
     * Stores repository cache.
     * 
     * @param repository
     *            The retrieved repository
     */

    public void persistRepositoryCache(Repository repository, boolean asXmlIfEnabled) {
        String folder = RepositoryHandler.getInstance().getRepositoryConfigurationFolder();

        ObjectOutputStream oos = null;
        try {
            FileOutputStream fout = new FileOutputStream(StringUtils.getString(folder, "/", Constants.CACHE_REPOSITORY_NAME));
            oos = new ObjectOutputStream(fout);
            Logger.info("Serialize repository information...");
            Timer timer = new Timer();
            timer.start();
            oos.writeObject(repository);
            Logger.info(StringUtils.getString("DONE (", timer.stop(), " seconds)"));
        } catch (IOException e) {
            Logger.error("Could not write serialized repository");
            Logger.debug(e);
        } finally {
            ClosingUtils.close(oos);
        }

        if (asXmlIfEnabled && getState().isSaveRepositoryAsXml()) {
            try {
                Logger.info("Storing repository information as xml...");
                Timer timer = new Timer();
                timer.start();
                XMLUtils.writeObjectToFile(repository, StringUtils.getString(folder, "/", Constants.XML_CACHE_REPOSITORY_NAME));
                Logger.info(StringUtils.getString("DONE (", timer.stop(), " seconds)"));
            } catch (IOException e) {
                Logger.error("Could not write repository as xml");
                Logger.debug(e);
            }
        }        
    }

    public void persistDeviceCache(String deviceId, Repository deviceRepository) {
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fout = new FileOutputStream(StringUtils
                    .getString(getUserConfigFolder(), getOsManager().getFileSeparator(), Constants.DEVICE_CACHE_FILE_PREFIX, deviceId));
            oos = new ObjectOutputStream(fout);
            Logger.info("Serialize device information...");
            long t0 = System.currentTimeMillis();
            oos.writeObject(deviceRepository);
            long t1 = System.currentTimeMillis();
            Logger.info(StringUtils.getString("DONE (", (t1 - t0) / 1000.0, " seconds)"));
        } catch (IOException e) {
            Logger.error("Could not write serialized device");
            Logger.debug(e);
        } finally {
            ClosingUtils.close(oos);
        }
    }

    /**
     * Reads favorites cache.
     * 
     * @return The retrieved favorites
     */
    public Favorites retrieveFavoritesCache() {
        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.CACHE_FAVORITES_NAME)));
            Logger.info("Reading serialized favorites cache");
            return (Favorites) stream.readObject();
        } catch (InvalidClassException e) {
            Logger.info("No serialized favorites info found");
            Favorites xml = retrieveFavoritesCacheFromXML();
            return xml != null ? xml : new Favorites();
        } catch (IOException e) {
            Logger.info("No serialized favorites info found");
            Favorites xml = retrieveFavoritesCacheFromXML();
            return xml != null ? xml : new Favorites();
        } catch (ClassNotFoundException e) {
            Logger.info("No serialized favorites info found");
            Favorites xml = retrieveFavoritesCacheFromXML();
            return xml != null ? xml : new Favorites();
        } catch (ClassCastException e) {
            Logger.info("No serialized favorites info found");
            Favorites xml = retrieveFavoritesCacheFromXML();
            return xml != null ? xml : new Favorites();        	
        } finally {
            ClosingUtils.close(stream);
        }
    }
    
    private Favorites retrieveFavoritesCacheFromXML() {
        if (getState().isSaveRepositoryAsXml()) {
            try {
                Logger.info("Reading xml favorites cache");
                return (Favorites) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_FAVORITES_NAME));
            } catch (IOException e1) {
                Logger.info("No xml favorites info found");
            } catch (InstantiationError e) {
                Logger.info("No xml favorites info found");
            }
        }
        return null;
    }

    /**
     * Reads statistics cache.
     * 
     * @return The retrieved favorites
     */
    public Statistics retrieveStatisticsCache() {
        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.CACHE_STATISTICS_NAME)));
            Logger.info("Reading serialized statistics cache");
            return (Statistics) stream.readObject();
        } catch (InvalidClassException e) {
            Logger.error(e);
        } catch (ClassCastException e) {
            Logger.error(e);
        } catch (IOException e) {
            Logger.info("No serialized statistics info found");
            if (getState().isSaveRepositoryAsXml()) {
                try {
                    Logger.info("Reading xml statistics cache");
                    return (Statistics) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_STATISTICS_NAME));
                } catch (IOException e1) {
                    Logger.info("No xml statistics info found");
                }
            }
        } catch (ClassNotFoundException e) {
            Logger.info("No serialized statistics info found");
            if (getState().isSaveRepositoryAsXml()) {
                try {
                    Logger.info("Reading xml statistics cache");
                    return (Statistics) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_STATISTICS_NAME));
                } catch (IOException e1) {
                    Logger.info("No xml statistics info found");
                }
            }
        } finally {
            ClosingUtils.close(stream);
        }
        // If some 
        return new Statistics();
    }

    /**
     * Reads playlists cache.
     * 
     * @return The retrieved playlists
     */

    @SuppressWarnings("unchecked")
    public ListOfPlayLists retrievePlayListsCache() {
        ObjectInputStream stream = null;
        try {
            // First get list of playlists
            ListOfPlayLists listOfPlayLists = (ListOfPlayLists) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.PLAYLISTS_FILE));
            listOfPlayLists.setState(getState());
            Logger.info(StringUtils.getString("List of playlists loaded"));

            // Then read contents
            stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.PLAYLISTS_CONTENTS_FILE)));
            List<List<IAudioObject>> contents = (List<List<IAudioObject>>) stream.readObject();
            Logger.info(StringUtils.getString("Playlists contents loaded"));
            listOfPlayLists.setContents(contents, getState());
            return listOfPlayLists;
        } catch (FileNotFoundException e) {
            Logger.info("No playlist information found");
            return ListOfPlayLists.getEmptyPlayList(getState());
        } catch (IOException e) {
            Logger.error(e);
            return ListOfPlayLists.getEmptyPlayList(getState());
        } catch (ClassNotFoundException e) {
            Logger.error(e);
            return ListOfPlayLists.getEmptyPlayList(getState());
        } catch (ClassCastException e) {
            Logger.error(e);
            return ListOfPlayLists.getEmptyPlayList(getState());
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
    public List<IPodcastFeed> retrievePodcastFeedCache() {
        try {
            return (List<IPodcastFeed>) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.PODCAST_FEED_CACHE));
        } catch (IOException e) {
            /*
             * java.util.concurrent.CopyOnWriteArrayList instead of e.g.
             * java.util.ArrayList to avoid ConcurrentModificationException
             */
            return new CopyOnWriteArrayList<IPodcastFeed>();
        }
    }

    /**
     * Reads radio cache.
     * 
     * @return The retrieved radios
     */
    @SuppressWarnings("unchecked")
    public List<IRadio> retrieveRadioCache() {
        try {
            return (List<IRadio>) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.RADIO_CACHE));
        } catch (IOException e) {
            return new ArrayList<IRadio>();
        }

    }

    /**
     * Reads radio cache. Preset stations. This file is not meant to be edited.
     * 
     * @return The retrieved radios
     */
    @SuppressWarnings("unchecked")
    public List<IRadio> retrieveRadioPreset() {
        try {
            // First try user settings folder
            return (List<IRadio>) XMLUtils.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.PRESET_RADIO_CACHE));
        } catch (IOException e) {
            try {
                // Otherwise use list in application folder
                return (List<IRadio>) XMLUtils.readObjectFromFile(ApplicationStateHandler.class.getResourceAsStream("/settings/" + Constants.PRESET_RADIO_CACHE));
            } catch (IOException e2) {
                return new ArrayList<IRadio>();
            }
        }
    }

    /**
     * Reads repository cache.
     * 
     * @return The retrieved repository
     */
    public Repository retrieveRepositoryCache() {
        String folder = RepositoryHandler.getInstance().getRepositoryConfigurationFolder();

        ObjectInputStream ois = null;
        try {
            FileInputStream fis = new FileInputStream(StringUtils.getString(folder, "/", Constants.CACHE_REPOSITORY_NAME));
            ois = new ObjectInputStream(fis);
            Logger.info("Reading serialized repository cache");
            Timer timer = new Timer();
            timer.start();
            Repository result = (Repository) ois.readObject();
            result.setState(getState());

            // Check repository integrity
            result.validateRepository();

            Logger.info(StringUtils.getString("Reading repository cache done (", timer.stop(), " seconds)"));
            return result;
        } catch (InvalidClassException e) {
            //TODO remove in next version
            Logger.error(e);
            return null;
        } catch (IOException e) {
        	return exceptionReadingRepository(folder);
        } catch (ClassNotFoundException e) {
        	return exceptionReadingRepository(folder);
        } catch (InconsistentRepositoryException e) {
        	return exceptionReadingRepository(folder);
        } finally {
            ClosingUtils.close(ois);
        }
    }
    
    private Repository exceptionReadingRepository(String folder) {
        Logger.info("No serialized repository info found");
        if (getState().isSaveRepositoryAsXml()) {
            try {
                Logger.info("Reading xml repository cache");
                long t0 = System.currentTimeMillis();
                Repository repository = (Repository) XMLUtils.readObjectFromFile(StringUtils.getString(folder, "/", Constants.XML_CACHE_REPOSITORY_NAME));
                repository.setState(getState());

                // Check repository integrity
                repository.validateRepository();

                long t1 = System.currentTimeMillis();
                Logger.info(StringUtils.getString("Reading repository cache done (", (t1 - t0) / 1000.0, " seconds)"));
                
                return repository;
            } catch (IOException e1) {
                Logger.info("No xml repository info found");
                return null;
            } catch (InconsistentRepositoryException e1) {
                Logger.info("No xml repository info found");
                return null;
            }
        }
        return null;    	
    }

    /**
     * Reads device cache.
     * 
     * @return The retrieved device
     */
    public Repository retrieveDeviceCache(String deviceId) {
        ObjectInputStream ois = null;
        try {
            FileInputStream fis = new FileInputStream(StringUtils.getString(getUserConfigFolder(), getOsManager().getFileSeparator(), Constants.DEVICE_CACHE_FILE_PREFIX, deviceId));
            ois = new ObjectInputStream(fis);
            Logger.info("Reading serialized device cache");
            long t0 = System.currentTimeMillis();
            Repository result = (Repository) ois.readObject();
            result.setState(getState());
            long t1 = System.currentTimeMillis();
            Logger.info(StringUtils.getString("Reading device cache done (", (t1 - t0) / 1000.0, " seconds)"));
            return result;
        } catch (IOException e) {
            Logger.info(StringUtils.getString("No serialized device info found for deviceId: ", deviceId));
            return null;
        } catch (ClassNotFoundException e) {
            Logger.info(StringUtils.getString("No serialized device info found for deviceId: ", deviceId));
            return null;
        } catch (ClassCastException e) {
        	Logger.error(e);
            Logger.info(StringUtils.getString("No serialized device info found for deviceId: ", deviceId));
            return null;        	
        } finally {
            ClosingUtils.close(ois);
        }
    }

    private String getUserConfigFolder() {
        return getOsManager().getUserConfigFolder(Kernel.isDebug());
    }
    
    /**
     * Opens preferences dialog
     * 
     * NOTE: This method is called from MacOSXAdapter using reflection. Refactoring will break code!
     */
    public void editPreferences() {
    	EditPreferencesDialog dialog = new EditPreferencesDialog(getFrame().getFrame());
    	new EditPreferencesDialogController(dialog, getState(), getOsManager(), getFrame()).start();
    }

	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}

}
