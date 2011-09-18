package net.sourceforge.atunes.model;

import java.io.Serializable;

import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;

import org.joda.time.DateTime;

/**
 * An entry of a podcast feed
 * @author alex
 *
 */
public interface IPodcastFeedEntry extends IAudioObject, Serializable, Comparable<PodcastFeedEntry> {

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription();

	/**
	 * Sets the old.
	 * 
	 * @param old
	 *            the new old
	 */
	public void setOld(boolean old);

	/**
	 * Gets the podcast feed.
	 * 
	 * @return The corresponding podcast feed
	 */
	public IPodcastFeed getPodcastFeed();

	/**
	 * Checks if is listened.
	 * 
	 * @return if the podcast feed entry was already listened
	 */
	public boolean isListened();

	/**
	 * Checks if is downloaded.
	 * 
	 * @return true, if is downloaded
	 */
	public boolean isDownloaded();

	/**
	 * Checks if is old.
	 * 
	 * @return true, if is old
	 */
	public boolean isOld();

	/**
	 * Sets the date.
	 * 
	 * @param date
	 *            the date to set
	 */
	public void setDate(DateTime date);

	/**
	 * Sets the description.
	 * 
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description);

	/**
	 * Sets the downloaded.
	 * 
	 * @param downloaded
	 *            the new downloaded
	 */
	public void setDownloaded(boolean downloaded);

	/**
	 * Sets the listened.
	 * 
	 * @param listened
	 *            if the podcast feed entry was already listened
	 */
	public void setListened(boolean listened);

	/**
	 * Sets the podcast feed.
	 * 
	 * @param podcastFeed
	 *            the corresponding podcast feed to set
	 */
	public void setPodcastFeed(IPodcastFeed podcastFeed);

	/**
	 * Sets the url.
	 * 
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url);

}