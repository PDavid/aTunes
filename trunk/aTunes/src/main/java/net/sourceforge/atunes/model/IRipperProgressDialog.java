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

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

/**
 * Shows progress of ripper tasks
 * @author alex
 *
 */
public interface IRipperProgressDialog extends IDialog {

	/**
	 * Adds a cancel action to the cancel button.
	 * 
	 * @param action
	 *            The action that should be added
	 */
	public void addCancelAction(ActionListener action);

	/**
	 * Sets the artist and album.
	 * 
	 * @param artist
	 *            the artist
	 * @param album
	 *            the album
	 */
	public void setArtistAndAlbum(String artist, String album);

	/**
	 * Sets the cover.
	 * 
	 * @param img
	 *            the new cover
	 */
	public void setCover(ImageIcon img);

	/**
	 * Sets the decode progress value.
	 * 
	 * @param value
	 *            the new decode progress value
	 */
	public void setDecodeProgressValue(int value);

	/**
	 * Sets the decode progress value.
	 * 
	 * @param value
	 *            the new decode progress value
	 */
	public void setDecodeProgressValue(String value);

	/**
	 * Sets the encode progress value.
	 * 
	 * @param value
	 *            the new encode progress value
	 */
	public void setEncodeProgressValue(int value);

	/**
	 * Sets the encode progress value.
	 * 
	 * @param value
	 *            the new encode progress value
	 */
	public void setEncodeProgressValue(String value);

	/**
	 * Sets the total progress bar limits.
	 * 
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 */
	public void setTotalProgressBarLimits(int min, int max);

	/**
	 * Sets the total progress value.
	 * 
	 * @param value
	 *            the new total progress value
	 */
	public void setTotalProgressValue(int value);
}