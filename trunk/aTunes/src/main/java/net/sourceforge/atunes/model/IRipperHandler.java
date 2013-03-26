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

import java.util.Set;

/**
 * Responsible of ripping process (copy audio from CD)
 * @author alex
 *
 */
public interface IRipperHandler extends IHandler {

	/**
	 * Cancel process.
	 */
	public void cancelProcess();

	/**
	 * Fill songs titles
	 * 
	 * @param artist
	 *            the artist
	 * @param album
	 *            the album
	 */
	public void fillSongTitles(final String artist, final String album);

	/**
	 * Gets format name of the encoder which was used for ripping CD's. If
	 * encoder is not available then get one of the available
	 * 
	 * @return Return the format name of the encoder used the previous time or
	 *         default one if it's available
	 */
	public String getEncoderName();

	/**
	 * Returns available qualities for given format name
	 * 
	 * @param formatName
	 * @return
	 */
	public String[] getEncoderQualities(String formatName);

	/**
	 * Returns default quality for given format name
	 * 
	 * @param formatName
	 * @return
	 */
	public String getEncoderDefaultQuality(String formatName);

	/**
	 * Returns names of available encoders
	 * @return
	 */
	public Set<String> getAvailableEncodersNames();

	/**
	 * Controls the import process for ripping audio CD's.
	 * @param metadata
	 * @param format
	 * @param quality1
	 * @param useParanoia
	 */
	public void importSongs(CDMetadata metadata,
			final String format, final String quality1,
			final boolean useParanoia);

	/**
	 * Start cd ripper.
	 */
	public void startCdRipper();

	/**
	 * Returns true if rip CDs is supported in current system
	 * @return
	 */
	public boolean isRipSupported();
}