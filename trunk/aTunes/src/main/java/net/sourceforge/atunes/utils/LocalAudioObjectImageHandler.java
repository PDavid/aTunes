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

package net.sourceforge.atunes.utils;

import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectImageHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

import org.apache.sanselan.ImageWriteException;

/**
 * This class gets images associated to audio files Images can be internal (like
 * in ID3v2) or external (in the same folder than audio file).
 * 
 * @author fleax
 */
public final class LocalAudioObjectImageHandler implements
		ILocalAudioObjectImageHandler {

	private IOSManager osManager;

	private IUnknownObjectChecker unknownObjectChecker;

	private IFileManager fileManager;

	private ITagHandler tagHandler;

	/**
	 * @param tagHandler
	 */
	public void setTagHandler(ITagHandler tagHandler) {
		this.tagHandler = tagHandler;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param fileManager
	 */
	public void setFileManager(IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * Gets the external picture.
	 * 
	 * @param audioObject
	 * @param width
	 * @param height
	 * @return
	 */
	@Override
	public ImageIcon getExternalPicture(final IAudioObject audioObject,
			final int width, final int height) {
		if (!(audioObject instanceof ILocalAudioObject)) {
			return null;
		}

		ILocalAudioObject file = (ILocalAudioObject) audioObject;

		// Try first to get picture with file name "ARTIST_ALBUM_COVER" pattern
		String coverFileName = getFileNameForCover(file);
		ImageIcon image = null;
		if (coverFileName != null && new File(coverFileName).exists()) {
			image = new ImageIcon(coverFileName);
		}
		if (image != null) {
			if (width == -1 || height == -1) {
				return image;
			}
			int maxSize = (image.getIconWidth() > image.getIconHeight()) ? image
					.getIconWidth() : image.getIconHeight();
			int newWidth = (int) ((float) image.getIconWidth()
					/ (float) maxSize * width);
			int newHeight = (int) ((float) image.getIconHeight()
					/ (float) maxSize * height);
			return ImageUtils.scaleImageBicubic(image.getImage(), newWidth,
					newHeight);
		}
		return image;
	}

	/**
	 * Returns a file name to save an external image associated to an audio
	 * file.
	 * 
	 * @param file
	 * @return
	 */
	@Override
	public String getFileNameForCover(final ILocalAudioObject file) {
		if (file == null) {
			return null;
		}
		// Validate artist and album names to avoid using forbidden chars in
		// file system
		String artist = FileNameUtils.getValidFileName(
				file.getArtist(unknownObjectChecker), osManager);
		String album = FileNameUtils.getValidFileName(
				file.getAlbum(unknownObjectChecker), osManager);
		return StringUtils.getString(fileManager.getFolderPath(file),
				osManager.getFileSeparator(), artist, '_', album, "_Cover.",
				ImageUtils.FILES_EXTENSION);
	}

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
	@Override
	public ImageIcon getInsidePicture(final IAudioObject audioObject,
			final int width, final int height) {
		if (!(audioObject instanceof ILocalAudioObject)) {
			return null;
		}

		return tagHandler.getImage((ILocalAudioObject) audioObject, width,
				height);
	}

	/**
	 * Saves and internal image of an audio file to a file.
	 * 
	 * @param song
	 *            the song
	 * @param file
	 *            the file
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ImageWriteException
	 */
	@Override
	public void savePictureToFile(final ILocalAudioObject song, final File file)
			throws IOException, ImageWriteException {
		ImageIcon image = getInsidePicture(song, -1, -1);
		ImageUtils.writeImageToFile(image.getImage(),
				net.sourceforge.atunes.utils.FileUtils.getPath(file));
	}
}
