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

/**
 * Responsible of build play lists based on statistical or context information
 * @author alex
 *
 */
public interface ISmartPlayListHandler extends IHandler {

	/**
	 * Gets n albums most played and adds to play list.
	 * 
	 * @param n
	 *            the n
	 */
	public void addAlbumsMostPlayed(int n);

	/**
	 * Gets n artists most played and adds to play list.
	 * 
	 * @param n
	 *            the n
	 */
	public void addArtistsMostPlayed(int n);

	/**
	 * Gets a number of random songs and adds to play list.
	 * 
	 * @param n
	 *            the n
	 */
	public void addRandomSongs(int n);

	/**
	 * Gets n songs most played and adds to play list.
	 * 
	 * @param n
	 *            the n
	 */
	public void addSongsMostPlayed(int n);

	/**
	 * Adds n unplayed songs to playlist.
	 * 
	 * @param n
	 *            the n
	 */
	public void addUnplayedSongs(int n);

}