/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IObjectDataStore;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.IStatistics;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.XMLSerializerService;

/**
 * This class is responsible of read, write and apply application state, and
 * caches.
 */
public final class ApplicationStateService implements IStateService {

	private XMLSerializerService xmlSerializerService;

	private IObjectDataStore<IRepository> repositoryObjectDataStore;

	private IObjectDataStore<IRepository> deviceObjectDataStore;

	private IObjectDataStore<IListOfPlayLists> playListObjectDataStore;

	private IObjectDataStore<IFavorites> favoritesObjectDataStore;

	private IObjectDataStore<IStatistics> statisticsObjectDataStore;

	private IObjectDataStore<List<IPodcastFeed>> podcastObjectDataStore;

	private IOSManager osManager;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param podcastObjectDataStore
	 */
	public void setPodcastObjectDataStore(
			final IObjectDataStore<List<IPodcastFeed>> podcastObjectDataStore) {
		this.podcastObjectDataStore = podcastObjectDataStore;
	}

	/**
	 * @param statisticsObjectDataStore
	 */
	public void setStatisticsObjectDataStore(
			final IObjectDataStore<IStatistics> statisticsObjectDataStore) {
		this.statisticsObjectDataStore = statisticsObjectDataStore;
	}

	/**
	 * @param favoritesObjectDataStore
	 */
	public void setFavoritesObjectDataStore(
			final IObjectDataStore<IFavorites> favoritesObjectDataStore) {
		this.favoritesObjectDataStore = favoritesObjectDataStore;
	}

	/**
	 * @param deviceObjectDataStore
	 */
	public void setDeviceObjectDataStore(
			final IObjectDataStore<IRepository> deviceObjectDataStore) {
		this.deviceObjectDataStore = deviceObjectDataStore;
	}

	/**
	 * @param playListObjectDataStore
	 */
	public void setPlayListObjectDataStore(
			final IObjectDataStore<IListOfPlayLists> playListObjectDataStore) {
		this.playListObjectDataStore = playListObjectDataStore;
	}

	/**
	 * @param repositoryObjectDataStore
	 */
	public void setRepositoryObjectDataStore(
			final IObjectDataStore<IRepository> repositoryObjectDataStore) {
		this.repositoryObjectDataStore = repositoryObjectDataStore;
	}

	/**
	 * @param xmlSerializerService
	 */
	public void setXmlSerializerService(
			final XMLSerializerService xmlSerializerService) {
		this.xmlSerializerService = xmlSerializerService;
	}

	@Override
	public void persistFavoritesCache(final IFavorites favorites) {
		this.favoritesObjectDataStore.write(favorites);
	}

	@Override
	public void persistStatisticsCache(final IStatistics statistics) {
		this.statisticsObjectDataStore.write(statistics);
	}

	@Override
	public void persistPlayLists(final IListOfPlayLists listOfPlayLists) {
		this.playListObjectDataStore.write(listOfPlayLists);
	}

	@Override
	public void persistPodcastFeedCache(final List<IPodcastFeed> podcastFeeds) {
		this.podcastObjectDataStore.write(podcastFeeds);
	}

	@Override
	public void persistRadioCache(final List<IRadio> radios) {
		try {
			this.xmlSerializerService.writeObjectToFile(radios, this.osManager
					.getFilePath(getUserConfigFolder(), Constants.RADIO_CACHE));
		} catch (IOException e) {
			Logger.error("Could not persist radios");
			Logger.debug(e);
		}
	}

	@Override
	public void persistRepositoryCache(final IRepository repository) {
		this.repositoryObjectDataStore.write(repository);
	}

	@Override
	public void persistDeviceCache(final String deviceId,
			final IRepository deviceRepository) {
		this.deviceObjectDataStore.write(deviceId, deviceRepository);
	}

	@Override
	public IFavorites retrieveFavoritesCache() {
		return this.favoritesObjectDataStore.read();
	}

	@Override
	public IStatistics retrieveStatisticsCache() {
		return this.statisticsObjectDataStore.read();
	}

	@Override
	public IListOfPlayLists retrievePlayListsCache() {
		return this.playListObjectDataStore.read();
	}

	@Override
	public List<IPodcastFeed> retrievePodcastFeedCache() {
		return this.podcastObjectDataStore.read();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IRadio> retrieveRadioCache() {
		try {
			return (List<IRadio>) this.xmlSerializerService
					.readObjectFromFile(this.osManager.getFilePath(
							getUserConfigFolder(), Constants.RADIO_CACHE));
		} catch (FileNotFoundException e) {
			Logger.info(e.getMessage());
		} catch (IOException e) {
			Logger.error(e);
		}
		return null;
	}

	@Override
	public IRepository retrieveRepositoryCache() {
		return this.repositoryObjectDataStore.read();
	}

	@Override
	public IRepository retrieveDeviceCache(final String deviceId) {
		return this.deviceObjectDataStore.read(deviceId);
	}

	private String getUserConfigFolder() {
		return this.osManager.getUserConfigFolder();
	}

	@Override
	public void editPreferences() {
		this.beanFactory.getBean(EditPreferencesDialogController.class).start();
	}
}
