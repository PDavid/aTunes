package net.sourceforge.atunes.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents statistics about an audio object
 * @author alex
 *
 */
public interface IAudioObjectStatistics extends Serializable {

	/**
	 * Gets the last played.
	 * 
	 * @return the last played
	 */
	public Date getLastPlayed();

	/**
	 * Gets the times played.
	 * 
	 * @return the times played
	 */
	public int getTimesPlayed();

	/**
	 * Increase times played.
	 */
	public void increaseTimesPlayed();

	/**
	 * Reset.
	 */
	public void reset();

	/**
	 * Sets the last played.
	 * 
	 * @param lastPlayed
	 *            the new last played
	 */
	public void setLastPlayed(Date lastPlayed);

}