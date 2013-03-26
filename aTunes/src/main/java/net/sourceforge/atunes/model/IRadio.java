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

package net.sourceforge.atunes.model;

import java.io.Serializable;


/**
 * A radio station
 * @author alex
 *
 */
public interface IRadio extends IAudioObject, Serializable, ITreeObject<IRadio>, Comparable<IRadio> {

	/**
	 * Delete song info.
	 */
	public void deleteSongInfo();

	/**
	 * Gets the name.
	 * 
	 * @return Radio station name
	 */
	public String getName();

	/**
	 * Checks if is removed.
	 * 
	 * @return true, if is removed
	 */
	public boolean isRemoved();

	/**
	 * Checks if is song info available.
	 * 
	 * @return true, if is song info available
	 */
	public boolean isSongInfoAvailable();

	/**
	 * Sets the artist.
	 * 
	 * @param artist
	 *            the new artist
	 */
	public void setArtist(String artist);

	/**
	 * Sets the bitrate.
	 * 
	 * @param bitrate
	 *            the new bitrate
	 */
	public void setBitrate(long bitrate);

	/**
	 * Sets the frequency.
	 * 
	 * @param frequency
	 *            the new frequency
	 */
	public void setFrequency(int frequency);

	/**
	 * Sets the genre.
	 * 
	 * @param genre
	 *            the new genre
	 */
	public void setGenre(String genre);

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name);

	/**
	 * Set a preset station as removed. Set true for removing.
	 * 
	 * @param isRemoved
	 *            the is removed
	 */
	public void setRemoved(boolean isRemoved);

	/**
	 * Sets the song info available.
	 * 
	 * @param songInfoAvailable
	 *            the new song info available
	 */
	public void setSongInfoAvailable(boolean songInfoAvailable);

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the new title
	 */
	public void setTitle(String title);

	/**
	 * Sets the url.
	 * 
	 * @param url
	 *            the new url
	 */
	public void setUrl(String url);

	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	public String getLabel();

	/**
	 * Sets the label.
	 * 
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label);

}