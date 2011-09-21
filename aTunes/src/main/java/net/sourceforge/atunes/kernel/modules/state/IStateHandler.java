package net.sourceforge.atunes.kernel.modules.state;

import java.util.List;

import net.sourceforge.atunes.kernel.modules.playlist.ListOfPlayLists;
import net.sourceforge.atunes.kernel.modules.repository.favorites.Favorites;
import net.sourceforge.atunes.kernel.modules.statistics.Statistics;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IHandler;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.Repository;

/**
 * Responsible of managing application state
 * @author alex
 *
 */
public interface IStateHandler extends IHandler {

	/**
	 * Adds a new ApplicationStateChangeListener. This listener will be notified
	 * when application state is changed
	 * 
	 * @param listener
	 */
	public void addStateChangeListener(ApplicationStateChangeListener listener);

	/**
	 * Removes an ApplicationStateChangeListener. This listener will not be
	 * notified again when application state is changed
	 * 
	 * @param listener
	 */
	public void removeStateChangeListener(
			ApplicationStateChangeListener listener);

	/**
	 * Notifies all listeners of an application state change
	 */
	public void notifyApplicationStateChanged();

	/**
	 * Stores favorites cache.
	 * 
	 * @param favorites
	 *            Favorites that should be persisted
	 */
	public void persistFavoritesCache(Favorites favorites);

	/**
	 * Stores statistics cache.
	 * 
	 * @param statistics
	 *            Statistics that should be persisted
	 */
	public void persistStatisticsCache(Statistics statistics);

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

	public void persistRepositoryCache(Repository repository,
			boolean asXmlIfEnabled);

	public void persistDeviceCache(String deviceId, Repository deviceRepository);

	/**
	 * Reads favorites cache.
	 * 
	 * @return The retrieved favorites
	 */
	public Favorites retrieveFavoritesCache();

	/**
	 * Reads statistics cache.
	 * 
	 * @return The retrieved favorites
	 */
	public Statistics retrieveStatisticsCache();

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
	public Repository retrieveRepositoryCache();

	/**
	 * Reads device cache.
	 * 
	 * @return The retrieved device
	 */
	public Repository retrieveDeviceCache(String deviceId);

	/**
	 * Opens preferences dialog
	 * 
	 * NOTE: This method is called from MacOSXAdapter using reflection. Refactoring will break code!
	 */
	public void editPreferences();

}