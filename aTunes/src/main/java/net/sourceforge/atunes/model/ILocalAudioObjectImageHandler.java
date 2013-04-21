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

import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.apache.sanselan.ImageWriteException;

/**
 * Handles images of local audio objects
 * 
 * @author alex
 * 
 */
public interface ILocalAudioObjectImageHandler {

	/**
	 * Returns image stored into audio file, if exists.
	 * 
	 * @param audioObject
	 *            the audioObject
	 * @param width
	 *            Width in pixels or -1 to keep original width
	 * @param height
	 *            Height in pixels or -1 to keep original height
	 * 
	 * @return the inside picture
	 */
	ImageIcon getInsidePicture(final IAudioObject audioObject, final int width,
			final int height);

	/**
	 * Saves and internal image of an audio file to a file.
	 * 
	 * @param song
	 * @param file
	 * @throws IOException
	 * @throws ImageWriteException
	 */
	void saveInternalPictureToFile(final ILocalAudioObject song, final File file)
			throws IOException, ImageWriteException;

	/**
	 * Returns true if application has saved cover image for given album
	 * 
	 * @param album
	 * 
	 * @return true, if checks for cover downloaded
	 */
	boolean hasCoverDownloaded(final IAlbum album);

	/**
	 * Saves cover for this album
	 * 
	 * @param album
	 * @param albumImage
	 * @throws IOException
	 * @throws ImageWriteException
	 */
	void writeCover(final IAlbum album, final ImageIcon albumImage)
			throws IOException, ImageWriteException;

	/**
	 * Saves cover for this file
	 * 
	 * @param file
	 * @param albumImage
	 * @throws IOException
	 * @throws ImageWriteException
	 */
	void writeCover(ILocalAudioObject file, ImageIcon albumImage)
			throws IOException, ImageWriteException;

	/**
	 * Returns image for audio object and given size
	 * 
	 * @param localAudioObject
	 * @param imageSize
	 * @return
	 */
	ImageIcon getImage(IAudioObject localAudioObject, ImageSize imageSize);

	/**
	 * Returns image for album and given size
	 * 
	 * @param album
	 * @param imageSize
	 * @return
	 */
	ImageIcon getImage(IAlbum album, ImageSize imageSize);

}
