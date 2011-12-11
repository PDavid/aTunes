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

package net.sourceforge.atunes.model;

import java.util.List;

import net.sourceforge.atunes.kernel.modules.playlist.ListOfPlayLists;

/**
 * Responsible of managing application state
 * @author alex
 *
 */
public interface IStateHandler extends IHandler {

	/**
	 * Stores favorites cache.
	 * 
	 * @param favorites
	 *            Favorites that should be persisted
	 */
	public void persistFavoritesCache(IFavorites favorites);

	/**
	 * Stores statistics cache.
	 * 
	 * @param statistics
	 *            Statistics that should be persisted
	 */
	public void persistStatisticsCache(IStatistics statistics);

	/**
	 * Stores play lists definition
	 */
	public void persistPlayListsDefinition(ListOfPlayLists listOfPlayLists);

	/**
	 * Stores play lists contents
	 * 
	 * @param playListsContents
	 */
	public void persistPlayListsContents(
			List<List<IAudioObject>> playListsContents);

	/**
	 * Stores podcast feeds.
	 * 
	 * @param podcastFeeds
	 *            Podcast feeds that should be persist
	 */
	public void persistPodcastFeedCache(List<IPodcastFeed> podcastFeeds);

	/**
	 * Stores radios.
	 * 
	 * @param radios
	 *            Radios that should be persisted
	 */
	public void persistRadioCache(List<IRadio> radios);

	/**
	 * Persist preset radio cache.
	 * 
	 * @param radios
	 *            the radios
	 */
	public void persistPresetRadioCache(List<IRadio> radios);

	/**
	 * Stores repository cache.
	 * 
	 * @param repository
	 *            The retrieved repository
	 */
	public void persistRepositoryCache(IRepository repository, boolean asXmlIfEnabled);

	/**
	 * Stores device cache for a device ID
	 * @param deviceId
	 * @param deviceRepository
	 */
	public void persistDeviceCache(String deviceId, IRepository deviceRepository);

	/**
	 * Reads favorites cache.
	 * 
	 * @return The retrieved favorites
	 */
	public IFavorites retrieveFavoritesCache();

	/**
	 * Reads statistics cache.
	 * 
	 * @return The retrieved favorites
	 */
	public IStatistics retrieveStatisticsCache();

	/**
	 * Reads playlists cache.
	 * 
	 * @return The retrieved playlists
	 */

	public ListOfPlayLists retrievePlayListsCache();

	/**
	 * Reads podcast feed cache.
	 * 
	 * @return The retrieved podcast feeds
	 */
	public List<IPodcastFeed> retrievePodcastFeedCache();

	/**
	 * Reads radio cache.
	 * 
	 * @return The retrieved radios
	 */
	public List<IRadio> retrieveRadioCache();

	/**
	 * Reads radio cache. Preset stations. This file is not meant to be edited.
	 * 
	 * @return The retrieved radios
	 */
	public List<IRadio> retrieveRadioPreset();

	/**
	 * Reads repository cache.
	 * 
	 * @return The retrieved repository
	 */
	public IRepository retrieveRepositoryCache();

	/**
	 * Reads device cache.
	 * 
	 * @return The retrieved device
	 */
	public IRepository retrieveDeviceCache(String deviceId);

	/**
	 * Opens preferences dialog
	 * 
	 * NOTE: This method is called from MacOSXAdapter using reflection. Refactoring will break code!
	 */
	public void editPreferences();

}