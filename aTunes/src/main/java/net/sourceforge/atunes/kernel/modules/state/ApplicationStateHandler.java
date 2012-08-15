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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.IObjectDataStore;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.model.IStatistics;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
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
	
	private XMLSerializerService xmlSerializerService;
	
	private IStatePlayer statePlayer;
	
	private IObjectDataStore<IRepository> repositoryObjectDataStore;
	
	private IObjectDataStore<IRepository> deviceObjectDataStore;
	
	private IObjectDataStore<IListOfPlayLists> playListDefinitionObjectDataStore;
	
	private IObjectDataStore<List<List<IAudioObject>>> playListContentsObjectDataStore;
	
	private IObjectDataStore<IFavorites> favoritesObjectDataStore;
	
	private IObjectDataStore<IStatistics> statisticsObjectDataStore;
	
	/**
	 * @param statisticsObjectDataStore
	 */
	public void setStatisticsObjectDataStore(IObjectDataStore<IStatistics> statisticsObjectDataStore) {
		this.statisticsObjectDataStore = statisticsObjectDataStore;
	}
	
	/**
	 * @param favoritesObjectDataStore
	 */
	public void setFavoritesObjectDataStore(IObjectDataStore<IFavorites> favoritesObjectDataStore) {
		this.favoritesObjectDataStore = favoritesObjectDataStore;
	}
	
	/**
	 * @param deviceObjectDataStore
	 */
	public void setDeviceObjectDataStore(IObjectDataStore<IRepository> deviceObjectDataStore) {
		this.deviceObjectDataStore = deviceObjectDataStore;
	}
	
	/**
	 * @param playListContentsObjectDataStore
	 */
	public void setPlayListContentsObjectDataStore(IObjectDataStore<List<List<IAudioObject>>> playListContentsObjectDataStore) {
		this.playListContentsObjectDataStore = playListContentsObjectDataStore;
	}
	
	/**
	 * @param playListDefinitionObjectDataStore
	 */
	public void setPlayListDefinitionObjectDataStore(IObjectDataStore<IListOfPlayLists> playListDefinitionObjectDataStore) {
		this.playListDefinitionObjectDataStore = playListDefinitionObjectDataStore;
	}
	
	/**
	 * @param repositoryObjectDataStore
	 */
	public void setRepositoryObjectDataStore(IObjectDataStore<IRepository> repositoryObjectDataStore) {
		this.repositoryObjectDataStore = repositoryObjectDataStore;
	}
	
	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}
	
	/**
	 * @param xmlSerializerService
	 */
	public void setXmlSerializerService(XMLSerializerService xmlSerializerService) {
		this.xmlSerializerService = xmlSerializerService;
	}
	
	@Override
	public void allHandlersInitialized() {
		playListPersistAllowed = true;
	}

    @Override
	public void persistFavoritesCache(IFavorites favorites) {
    	favoritesObjectDataStore.write(favorites);
    }

    @Override
	public void persistStatisticsCache(IStatistics statistics) {
    	statisticsObjectDataStore.write(statistics);
    }

    @Override
	public void persistPlayListsDefinition(IListOfPlayLists listOfPlayLists) {
    	if (!playListPersistAllowed) {
    		Logger.debug("Persist play list definition not allowed yet");
    	} else {
    		playListDefinitionObjectDataStore.write(listOfPlayLists);
    	}
    }

    @Override
	public void persistPlayListsContents(List<List<IAudioObject>> playListsContents) {
    	playListContentsObjectDataStore.write(playListsContents);
    }

    @Override
	public void persistPodcastFeedCache(List<IPodcastFeed> podcastFeeds) {
        try {
            xmlSerializerService.writeObjectToFile(podcastFeeds, StringUtils.getString(getUserConfigFolder(), "/", Constants.PODCAST_FEED_CACHE));
        } catch (IOException e) {
            Logger.error("Could not persist podcast feeds");
            Logger.debug(e);
        }
    }

    @Override
	public void persistRadioCache(List<IRadio> radios) {
        try {
            xmlSerializerService.writeObjectToFile(radios, StringUtils.getString(getUserConfigFolder(), "/", Constants.RADIO_CACHE));
        } catch (IOException e) {
            Logger.error("Could not persist radios");
            Logger.debug(e);
        }
    }

    @Override
	public void persistPresetRadioCache(List<IRadio> radios) {
        try {
            xmlSerializerService.writeObjectToFile(radios, StringUtils.getString(getUserConfigFolder(), "/", Constants.PRESET_RADIO_CACHE));
        } catch (IOException e) {
            Logger.error("Could not persist radios");
            Logger.debug(e);
        }
    }

    @Override
	public void persistRepositoryCache(IRepository repository) {
    	repositoryObjectDataStore.write(repository);
    }

    @Override
	public void persistDeviceCache(String deviceId, IRepository deviceRepository) {
    	deviceObjectDataStore.write(deviceId, deviceRepository);
    }

    @Override
	public IFavorites retrieveFavoritesCache() {
    	return favoritesObjectDataStore.read();
    }
    
    @Override
	public IStatistics retrieveStatisticsCache() {
    	return statisticsObjectDataStore.read();
    }

    @Override
    public IListOfPlayLists retrievePlayListsCache() {
    	// First get list of playlists
    	IListOfPlayLists listOfPlayLists = playListDefinitionObjectDataStore.read();
    	if (listOfPlayLists != null) {
    		Logger.info(StringUtils.getString("List of playlists loaded"));

    		// Then read contents
    		List<List<IAudioObject>> contents = playListContentsObjectDataStore.read();
    		Logger.info(StringUtils.getString("Playlists contents loaded"));
    		listOfPlayLists.setContents(contents, statePlayer);
    	}
    	return listOfPlayLists;
    }

    @SuppressWarnings("unchecked")
	@Override
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

    @SuppressWarnings("unchecked")
	@Override
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

    @SuppressWarnings("unchecked")
	@Override
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

    @Override
	public IRepository retrieveRepositoryCache() {
    	return repositoryObjectDataStore.read();
    }
    
    @Override
	public IRepository retrieveDeviceCache(String deviceId) {
    	return deviceObjectDataStore.read(deviceId);
    }

    private String getUserConfigFolder() {
        return getOsManager().getUserConfigFolder();
    }
    
    @Override
	public void editPreferences() {
    	getBean(EditPreferencesDialogController.class).start();
    }
}
