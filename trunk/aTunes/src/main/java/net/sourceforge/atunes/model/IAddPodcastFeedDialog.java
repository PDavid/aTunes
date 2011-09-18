package net.sourceforge.atunes.model;


/**
 * A dialog to let user add a new podcast feed
 * @author alex
 *
 */
public interface IAddPodcastFeedDialog {

	/**
	 * Gets the podcast feed.
	 * 
	 * @return the podcast feed
	 */
	public IPodcastFeed getPodcastFeed();

	/**
	 * Shows dialog
	 */
	public void showDialog();

}