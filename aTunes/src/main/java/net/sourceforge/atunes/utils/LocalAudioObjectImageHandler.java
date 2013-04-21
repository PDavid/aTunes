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

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectImageHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.ImageSize;

import org.apache.sanselan.ImageWriteException;

/**
 * This class gets images associated to audio files Images can be internal (like
 * in ID3v2) or external (in the same folder than audio file or other).
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
	public void setTagHandler(final ITagHandler tagHandler) {
		this.tagHandler = tagHandler;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
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
	private ImageIcon getExternalPicture(final IAudioObject audioObject,
			final int width, final int height) {
		if (!(audioObject instanceof ILocalAudioObject)) {
			return null;
		}

		ILocalAudioObject file = (ILocalAudioObject) audioObject;

		// Try first to get picture with file name "ARTIST_ALBUM_COVER" pattern
		String coverFileName = getFileNameForCoverRead(file);
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

		return this.tagHandler.getImage((ILocalAudioObject) audioObject, width,
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
	public void saveInternalPictureToFile(final ILocalAudioObject song,
			final File file) throws IOException, ImageWriteException {
		ImageIcon image = getInsidePicture(song, -1, -1);
		ImageUtils.writeImageToFile(image.getImage(),
				net.sourceforge.atunes.utils.FileUtils.getPath(file));
	}

	/**
	 * Returns true if application has saved cover image for given album
	 * 
	 * @param album
	 * 
	 * @return true, if checks for cover downloaded
	 */
	@Override
	public boolean hasCoverDownloaded(final IAlbum album) {
		return new File(
				getFileNameForCoverRead((album.getAudioObjects().get(0))))
				.exists();
	}

	/**
	 * Saves cover for this album
	 * 
	 * @param album
	 * @param albumImage
	 * @throws IOException
	 * @throws ImageWriteException
	 */
	@Override
	public void writeCover(final IAlbum album, final ImageIcon albumImage)
			throws IOException, ImageWriteException {
		if (album != null && !CollectionUtils.isEmpty(album.getAudioObjects())) {
			writeCover(album.getAudioObjects().get(0), albumImage);
		}
	}

	/**
	 * Saves cover for this file
	 * 
	 * @param file
	 * @param albumImage
	 * @throws IOException
	 * @throws ImageWriteException
	 */
	@Override
	public void writeCover(final ILocalAudioObject file,
			final ImageIcon albumImage) throws IOException, ImageWriteException {
		if (file != null && albumImage != null) {
			ImageUtils.writeImageToFile(albumImage.getImage(),
					getFileNameForCoverWrite(file));
		}
	}

	@Override
	public ImageIcon getImage(final IAudioObject audioObject,
			final ImageSize imageSize) {
		if (audioObject instanceof ILocalAudioObject) {
			ILocalAudioObject localAudioObject = (ILocalAudioObject) audioObject;

			ImageIcon result = getInsidePicture(localAudioObject,
					imageSize.getSize(), imageSize.getSize());
			if (result == null) {
				result = getExternalPicture(localAudioObject,
						imageSize.getSize(), imageSize.getSize());
			}

			return result;
		}
		return null;
	}

	@Override
	public ImageIcon getImage(final IAlbum album, final ImageSize imageSize) {
		if (album == null || album.getAudioObjects().isEmpty()) {
			return null;
		}
		return getImage(album.getAudioObjects().get(0), imageSize);
	}

	/**
	 * Returns a file name to read an external image associated to an audio
	 * file.
	 * 
	 * @param file
	 * @return
	 */
	private String getFileNameForCoverRead(final ILocalAudioObject file) {
		if (file == null) {
			return null;
		}
		// Normalize artist and album names to avoid using forbidden chars in
		// file system
		String artist = normalizeArtistName(file);
		String album = normalizeAlbumName(file);
		String fileNameForCoverInFileFolder = getFileNameForCoverInFileFolder(
				file, artist, album);

		if (new File(fileNameForCoverInFileFolder).exists()) {
			return fileNameForCoverInFileFolder;
		} else {
			// Try option in special covers folder
			return getFileNameForCoverInCoversFolder(file, artist, album);
		}
	}

	/**
	 * @param file
	 * @param normalizedArtist
	 * @param normalizedAlbum
	 * @return cover file name when is stored in covers folder
	 */
	private String getFileNameForCoverInCoversFolder(
			final ILocalAudioObject file, final String normalizedArtist,
			final String normalizedAlbum) {
		return StringUtils.getString(getCoversFolder(),
				this.osManager.getFileSeparator(),
				getCoverFileName(normalizedArtist, normalizedAlbum));
	}

	private String getCoversFolder() {
		return StringUtils.getString(this.osManager.getUserConfigFolder(),
				this.osManager.getFileSeparator(), "covers");
	}

	private String getCoverFileName(final String normalizedArtist,
			final String normalizedAlbum) {
		return StringUtils.getString(normalizedArtist, '_', normalizedAlbum,
				"_Cover.", ImageUtils.FILES_EXTENSION);
	}

	/**
	 * @param file
	 * @param normalizedArtist
	 * @param normalizedAlbum
	 * @return cover file name when is stored in the same folder than audio file
	 */
	private String getFileNameForCoverInFileFolder(
			final ILocalAudioObject file, final String normalizedArtist,
			final String normalizedAlbum) {
		return StringUtils.getString(this.fileManager.getFolderPath(file),
				this.osManager.getFileSeparator(),
				getCoverFileName(normalizedArtist, normalizedAlbum));
	}

	/**
	 * @param file
	 * @return
	 */
	private String normalizeAlbumName(final ILocalAudioObject file) {
		return FileNameUtils.getValidFileName(
				file.getAlbum(this.unknownObjectChecker), this.osManager);
	}

	/**
	 * @param file
	 * @return
	 */
	private String normalizeArtistName(final ILocalAudioObject file) {
		return FileNameUtils.getValidFileName(
				file.getArtist(this.unknownObjectChecker), this.osManager);
	}

	/**
	 * Returns a file name to save an external image associated to an audio
	 * file.
	 * 
	 * @param file
	 * @return
	 */
	private String getFileNameForCoverWrite(final ILocalAudioObject file) {
		if (file == null) {
			return null;
		}
		// Validate artist and album names to avoid using forbidden chars in
		// file system
		String artist = normalizeArtistName(file);
		String album = normalizeAlbumName(file);
		String fileNameForCoverInFileFolder = getFileNameForCoverInFileFolder(
				file, artist, album);

		if (FileUtils.canWriteFolder(new File(this.fileManager
				.getFolderPath(file)))) {
			return fileNameForCoverInFileFolder;
		} else {
			// Try option in special covers folder
			// Create folder if not exists
			File coversFolder = new File(getCoversFolder());
			if (!coversFolder.exists()) {
				coversFolder.mkdir();
			}
			return getFileNameForCoverInCoversFolder(file, artist, album);
		}
	}
}
