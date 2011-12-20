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

package net.sourceforge.atunes.kernel.modules.state;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import net.sourceforge.atunes.ApplicationArguments;
import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.StateChangeListeners;
import net.sourceforge.atunes.kernel.modules.playlist.ListOfPlayLists;
import net.sourceforge.atunes.kernel.modules.repository.favorites.Favorites;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorBeanFactory;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.IFontBeanFactory;
import net.sourceforge.atunes.model.IHotkeyHandler;
import net.sourceforge.atunes.model.ILocaleBeanFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.INotificationsHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IPluginsHandler;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IProxyBeanFactory;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStatistics;
import net.sourceforge.atunes.model.InconsistentRepositoryException;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import net.sourceforge.atunes.utils.Timer;
import net.sourceforge.atunes.utils.XMLSerializerService;

/**
 * This class is responsible of read, write and apply application state, and
 * caches.
 */
public final class ApplicationStateHandler extends AbstractHandler implements IStateHandler {
	
	/**
	 * After all handlers have been initialized it's possible to persist play list, not before (to prevent saved play lists to be stored again)
	 */
	private boolean playListPersistAllowed = false;
	private ILookAndFeelManager lookAndFeelManager;
	private IPlayerHandler playerHandler;
	private IHotkeyHandler hotkeyHandler;
	private INotificationsHandler notificationsHandler;
	private IPluginsHandler pluginsHandler;
	private IDesktop desktop;
	private INetworkHandler networkHandler;
	private IColorBeanFactory colorBeanFactory;
	private IFontBeanFactory fontBeanFactory;
	private ILocaleBeanFactory localeBeanFactory;
	private IProxyBeanFactory proxyBeanFactory;
	private XMLSerializerService xmlSerializerService;
	
	/**
	 * @param xmlSerializerService
	 */
	public void setXmlSerializerService(XMLSerializerService xmlSerializerService) {
		this.xmlSerializerService = xmlSerializerService;
	}
	
	/**
	 * @param proxyBeanFactory
	 */
	public void setProxyBeanFactory(IProxyBeanFactory proxyBeanFactory) {
		this.proxyBeanFactory = proxyBeanFactory;
	}
	
	/**
	 * @param localeBeanFactory
	 */
	public void setLocaleBeanFactory(ILocaleBeanFactory localeBeanFactory) {
		this.localeBeanFactory = localeBeanFactory;
	}
	
	/**
	 * @param fontBeanFactory
	 */
	public void setFontBeanFactory(IFontBeanFactory fontBeanFactory) {
		this.fontBeanFactory = fontBeanFactory;
	}
	
	/**
	 * @param colorBeanFactory
	 */
	public void setColorBeanFactory(IColorBeanFactory colorBeanFactory) {
		this.colorBeanFactory = colorBeanFactory;
	}
	
	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}
	
	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
	
	/**
	 * @param playerHandler
	 */
	public void setPlayerHandler(IPlayerHandler playerHandler) {
		this.playerHandler = playerHandler;
	}
	
	/**
	 * @param hotkeyHandler
	 */
	public void setHotkeyHandler(IHotkeyHandler hotkeyHandler) {
		this.hotkeyHandler = hotkeyHandler;
	}
	
	/**
	 * @param notificationsHandler
	 */
	public void setNotificationsHandler(
			INotificationsHandler notificationsHandler) {
		this.notificationsHandler = notificationsHandler;
	}
	
	/**
	 * @param pluginsHandler
	 */
	public void setPluginsHandler(IPluginsHandler pluginsHandler) {
		this.pluginsHandler = pluginsHandler;
	}
	
	/**
	 * @param desktop
	 */
	public void setDesktop(IDesktop desktop) {
		this.desktop = desktop;
	}
	
	@Override
	public void allHandlersInitialized() {
		super.allHandlersInitialized();
		playListPersistAllowed = true;
	}

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#persistFavoritesCache(net.sourceforge.atunes.kernel.modules.repository.favorites.Favorites)
	 */
    @Override
	public void persistFavoritesCache(IFavorites favorites) {
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
                xmlSerializerService.writeObjectToFile(favorites, StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_FAVORITES_NAME));
                Logger.info("Storing favorites information...");
            } catch (Exception e) {
                Logger.error("Could not write favorites");
                Logger.debug(e);
            }
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#persistStatisticsCache(net.sourceforge.atunes.kernel.modules.statistics.Statistics)
	 */
    @Override
	public void persistStatisticsCache(IStatistics statistics) {
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
                xmlSerializerService.writeObjectToFile(statistics, StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_STATISTICS_NAME));
                Logger.info("Storing statistics information...");
            } catch (Exception e) {
                Logger.error("Could not write statistics");
                Logger.debug(e);
            }
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#persistPlayListsDefinition(net.sourceforge.atunes.kernel.modules.playlist.ListOfPlayLists)
	 */
    @Override
	public void persistPlayListsDefinition(ListOfPlayLists listOfPlayLists) {
    	if (!playListPersistAllowed) {
    		Logger.debug("Persist play list definition not allowed yet");
    	} else {
    		try {
    			xmlSerializerService.writeObjectToFile(listOfPlayLists, StringUtils.getString(getUserConfigFolder(), "/", Constants.PLAYLISTS_FILE));
    			Logger.info("Playlists definition saved");
    		} catch (IOException e) {
    			Logger.error("Could not persist playlists definition");
    			Logger.debug(e);
    		}
    	}
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#persistPlayListsContents(java.util.List)
	 */
    @Override
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#persistPodcastFeedCache(java.util.List)
	 */
    @Override
	public void persistPodcastFeedCache(List<IPodcastFeed> podcastFeeds) {
        try {
            xmlSerializerService.writeObjectToFile(podcastFeeds, StringUtils.getString(getUserConfigFolder(), "/", Constants.PODCAST_FEED_CACHE));
        } catch (IOException e) {
            Logger.error("Could not persist podcast feeds");
            Logger.debug(e);
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#persistRadioCache(java.util.List)
	 */
    @Override
	public void persistRadioCache(List<IRadio> radios) {
        try {
            xmlSerializerService.writeObjectToFile(radios, StringUtils.getString(getUserConfigFolder(), "/", Constants.RADIO_CACHE));
        } catch (IOException e) {
            Logger.error("Could not persist radios");
            Logger.debug(e);
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#persistPresetRadioCache(java.util.List)
	 */
    @Override
	public void persistPresetRadioCache(List<IRadio> radios) {
        try {
            xmlSerializerService.writeObjectToFile(radios, StringUtils.getString(getUserConfigFolder(), "/", Constants.PRESET_RADIO_CACHE));
        } catch (IOException e) {
            Logger.error("Could not persist radios");
            Logger.debug(e);
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#persistRepositoryCache(net.sourceforge.atunes.model.Repository, boolean)
	 */

    @Override
	public void persistRepositoryCache(IRepository repository, boolean asXmlIfEnabled) {
        String folder = getBean(IRepositoryHandler.class).getRepositoryConfigurationFolder();

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
                xmlSerializerService.writeObjectToFile(repository, StringUtils.getString(folder, "/", Constants.XML_CACHE_REPOSITORY_NAME));
                Logger.info(StringUtils.getString("DONE (", timer.stop(), " seconds)"));
            } catch (IOException e) {
                Logger.error("Could not write repository as xml");
                Logger.debug(e);
            }
        }        
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#persistDeviceCache(java.lang.String, net.sourceforge.atunes.model.Repository)
	 */
    @Override
	public void persistDeviceCache(String deviceId, IRepository deviceRepository) {
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#retrieveFavoritesCache()
	 */
    @Override
	public IFavorites retrieveFavoritesCache() {
        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.CACHE_FAVORITES_NAME)));
            Logger.info("Reading serialized favorites cache");
            return (IFavorites) stream.readObject();
        } catch (FileNotFoundException e) {
            Logger.info("No serialized favorites info found");
        } catch (InvalidClassException e) {
            Logger.info("No serialized favorites info found");
        } catch (IOException e) {
            Logger.info("No serialized favorites info found");
        } catch (ClassNotFoundException e) {
            Logger.info("No serialized favorites info found");
        } catch (ClassCastException e) {
            Logger.info("No serialized favorites info found");
        } finally {
            ClosingUtils.close(stream);
        }
        return retrieveFavoritesCacheFromXML();
    }
    
    private Favorites retrieveFavoritesCacheFromXML() {
        if (getState().isSaveRepositoryAsXml()) {
            try {
                Logger.info("Reading xml favorites cache");
                return (Favorites) xmlSerializerService.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_FAVORITES_NAME));
            } catch (IOException e1) {
                Logger.info("No xml favorites info found");
            } catch (InstantiationError e) {
                Logger.info("No xml favorites info found");
            }
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#retrieveStatisticsCache()
	 */
    @Override
	public IStatistics retrieveStatisticsCache() {
        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.CACHE_STATISTICS_NAME)));
            Logger.info("Reading serialized statistics cache");
            return (IStatistics) stream.readObject();
        } catch (InvalidClassException e) {
            Logger.error(e);
        } catch (ClassCastException e) {
            Logger.error(e);
        } catch (IOException e) {
            Logger.info("No serialized statistics info found");
            if (getState().isSaveRepositoryAsXml()) {
                try {
                    Logger.info("Reading xml statistics cache");
                    return (IStatistics) xmlSerializerService.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_STATISTICS_NAME));
                } catch (IOException e1) {
                    Logger.info("No xml statistics info found");
                }
            }
        } catch (ClassNotFoundException e) {
            Logger.info("No serialized statistics info found");
            if (getState().isSaveRepositoryAsXml()) {
                try {
                    Logger.info("Reading xml statistics cache");
                    return (IStatistics) xmlSerializerService.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.XML_CACHE_STATISTICS_NAME));
                } catch (IOException e1) {
                    Logger.info("No xml statistics info found");
                }
            }
        } finally {
            ClosingUtils.close(stream);
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#retrievePlayListsCache()
	 */
    @Override
	@SuppressWarnings("unchecked")
    public ListOfPlayLists retrievePlayListsCache() {
        ObjectInputStream stream = null;
        try {
            // First get list of playlists
            ListOfPlayLists listOfPlayLists = (ListOfPlayLists) xmlSerializerService.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.PLAYLISTS_FILE));
            if (listOfPlayLists != null) {
            	listOfPlayLists.setState(getState());
            	Logger.info(StringUtils.getString("List of playlists loaded"));

            	// Then read contents
            	stream = new ObjectInputStream(new FileInputStream(StringUtils.getString(getUserConfigFolder(), "/", Constants.PLAYLISTS_CONTENTS_FILE)));
            	List<List<IAudioObject>> contents = (List<List<IAudioObject>>) stream.readObject();
            	Logger.info(StringUtils.getString("Playlists contents loaded"));
            	listOfPlayLists.setContents(contents, getState());
            	return listOfPlayLists;
            }
        } catch (FileNotFoundException e) {
            Logger.info("No playlist information found");
        } catch (IOException e) {
            Logger.error(e);
        } catch (ClassNotFoundException e) {
            Logger.error(e);
        } catch (ClassCastException e) {
            Logger.error(e);
        } finally {
            ClosingUtils.close(stream);
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#retrievePodcastFeedCache()
	 */
    @Override
	@SuppressWarnings("unchecked")
    public List<IPodcastFeed> retrievePodcastFeedCache() {
        try {
            return (List<IPodcastFeed>) xmlSerializerService.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.PODCAST_FEED_CACHE));
        } catch (FileNotFoundException e) {
        	Logger.info(e.getMessage());
        } catch (IOException e) {
            Logger.error(e);
        }
    	return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#retrieveRadioCache()
	 */
    @Override
	@SuppressWarnings("unchecked")
    public List<IRadio> retrieveRadioCache() {
        try {
            return (List<IRadio>) xmlSerializerService.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.RADIO_CACHE));
        } catch (FileNotFoundException e) {
        	Logger.info(e.getMessage());
        } catch (IOException e) {
        	Logger.error(e);
        }
    	return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#retrieveRadioPreset()
	 */
    @Override
	@SuppressWarnings("unchecked")
    public List<IRadio> retrieveRadioPreset() {
        try {
            // First try user settings folder
            return (List<IRadio>) xmlSerializerService.readObjectFromFile(StringUtils.getString(getUserConfigFolder(), "/", Constants.PRESET_RADIO_CACHE));
        } catch (IOException e) {
            try {
                // Otherwise use list in application folder
                return (List<IRadio>) xmlSerializerService.readObjectFromFile(ApplicationStateHandler.class.getResourceAsStream(StringUtils.getString("/settings/", Constants.PRESET_RADIO_CACHE)));
            } catch (IOException e2) {
            	Logger.error(e2);
            }
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#retrieveRepositoryCache()
	 */
    @Override
	public IRepository retrieveRepositoryCache() {
        String folder = getBean(IRepositoryHandler.class).getRepositoryConfigurationFolder();

        ObjectInputStream ois = null;
        try {
            FileInputStream fis = new FileInputStream(StringUtils.getString(folder, "/", Constants.CACHE_REPOSITORY_NAME));
            ois = new ObjectInputStream(fis);
            Logger.info("Reading serialized repository cache");
            Timer timer = new Timer();
            timer.start();
            IRepository result = (IRepository) ois.readObject();
            result.setState(getState());

            // Check repository integrity
            result.validateRepository();

            Logger.info(StringUtils.getString("Reading repository cache done (", timer.stop(), " seconds)"));
            return result;
        } catch (FileNotFoundException e) {
        	Logger.info(e.getMessage());
        } catch (InvalidClassException e) {
        	Logger.error(e);
        } catch (IOException e) {
        	Logger.error(e);
        } catch (ClassNotFoundException e) {
        	Logger.error(e);
        } catch (InconsistentRepositoryException e) {
        	Logger.error(e);
        } finally {
            ClosingUtils.close(ois);
        }
    	return readRepositoryFromXml(folder);
    }
    
    private IRepository readRepositoryFromXml(String folder) {
        Logger.info("No serialized repository info found");
        if (getState().isSaveRepositoryAsXml()) {
            try {
                Logger.info("Reading xml repository cache");
                long t0 = System.currentTimeMillis();
                IRepository repository = (IRepository) xmlSerializerService.readObjectFromFile(StringUtils.getString(folder, "/", Constants.XML_CACHE_REPOSITORY_NAME));
                if (repository != null) {
                	repository.setState(getState());

                	// Check repository integrity
                	repository.validateRepository();

                	long t1 = System.currentTimeMillis();
                	Logger.info(StringUtils.getString("Reading repository cache done (", (t1 - t0) / 1000.0, " seconds)"));

                	return repository;
                }
            } catch (IOException e1) {
                Logger.info("No xml repository info found");
            } catch (InconsistentRepositoryException e1) {
                Logger.info("No xml repository info found");
            }
        }
        return null;    	
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#retrieveDeviceCache(java.lang.String)
	 */
    @Override
	public IRepository retrieveDeviceCache(String deviceId) {
        ObjectInputStream ois = null;
        try {
            FileInputStream fis = new FileInputStream(StringUtils.getString(getUserConfigFolder(), getOsManager().getFileSeparator(), Constants.DEVICE_CACHE_FILE_PREFIX, deviceId));
            ois = new ObjectInputStream(fis);
            Logger.info("Reading serialized device cache");
            long t0 = System.currentTimeMillis();
            IRepository result = (IRepository) ois.readObject();
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
        return getOsManager().getUserConfigFolder(getBean(ApplicationArguments.class).isDebug());
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.state.IStateHandler#editPreferences()
	 */
    @Override
	public void editPreferences() {
    	EditPreferencesDialog dialog = getBean(EditPreferencesDialog.class);
    	new EditPreferencesDialogController(dialog, getState(), getOsManager(), getFrame(), getBean(StateChangeListeners.class), lookAndFeelManager, 
    			playerHandler, hotkeyHandler, notificationsHandler, 
    			pluginsHandler, desktop, networkHandler, colorBeanFactory, fontBeanFactory, localeBeanFactory, proxyBeanFactory).start();
    }
}
