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

package net.sourceforge.atunes.kernel.modules.tags;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.sourceforge.atunes.kernel.modules.repository.JAudiotaggerAudioPropertiesReader;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.ITagAdapter;
import net.sourceforge.atunes.model.LocalAudioObjectFormat;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.reference.PictureTypes;

/**
 * Tag adapter for JAudiotagger
 * 
 * @author alex
 * 
 */
public class JAudiotaggerTagAdapter implements ITagAdapter {

	private ILocalAudioObjectValidator localAudioObjectValidator;

	private JAudiotaggerTagCreator jAudiotaggerTagCreator;

	private JAudiotaggerFileReader jAudiotaggerFileReader;

	private JAudiotaggerAudioPropertiesReader jAudiotaggerAudioPropertiesReader;

	private IFileManager fileManager;

	private boolean storeRatingInFile;

	/**
	 * @param storeRatingInFile
	 */
	public void setStoreRatingInFile(final boolean storeRatingInFile) {
		this.storeRatingInFile = storeRatingInFile;
	}

	@Override
	public boolean isStoreRatingInFile() {
		return this.storeRatingInFile;
	}

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param jAudiotaggerAudioPropertiesReader
	 */
	public void setjAudiotaggerAudioPropertiesReader(
			final JAudiotaggerAudioPropertiesReader jAudiotaggerAudioPropertiesReader) {
		this.jAudiotaggerAudioPropertiesReader = jAudiotaggerAudioPropertiesReader;
	}

	/**
	 * @param jAudiotaggerFileReader
	 */
	public void setjAudiotaggerFileReader(
			final JAudiotaggerFileReader jAudiotaggerFileReader) {
		this.jAudiotaggerFileReader = jAudiotaggerFileReader;
	}

	/**
	 * @param jAudiotaggerTagCreator
	 */
	public void setjAudiotaggerTagCreator(
			final JAudiotaggerTagCreator jAudiotaggerTagCreator) {
		this.jAudiotaggerTagCreator = jAudiotaggerTagCreator;
	}

	/**
	 * @param localAudioObjectValidator
	 */
	public void setLocalAudioObjectValidator(
			final ILocalAudioObjectValidator localAudioObjectValidator) {
		this.localAudioObjectValidator = localAudioObjectValidator;
	}

	@Override
	public void modifyAlbum(final ILocalAudioObject file, final String album) {
		modifyTag(file, new JAudiotaggerAlbumTagModification(album));
	}

	@Override
	public void modifyGenre(final ILocalAudioObject file, final String genre) {
		modifyTag(file, new JAudiotaggerGenreTagModification(genre));
	}

	@Override
	public void modifyLyrics(final ILocalAudioObject file, final String lyrics) {
		modifyTag(file, new JAudiotaggerLyricsTagModification(lyrics));
	}

	@Override
	public void modifyTitle(final ILocalAudioObject file, final String newTitle) {
		modifyTag(file, new JAudiotaggerTitleTagModification(newTitle));
	}

	@Override
	public void modifyTrack(final ILocalAudioObject file, final Integer track) {
		modifyTag(file, new JAudiotaggerTrackNumberTagModification(track));
	}

	@Override
	public void modifyRating(final ILocalAudioObject audioObject,
			final String starsToRating) {
		Logger.debug("Writting ratings with JAudiotaggerTagAdapter");
		modifyTag(audioObject, new JAudiotaggerRatingTagModification(
				starsToRating));
	}

	@Override
	public void deleteTags(final ILocalAudioObject file) {
		try {
			// Be sure file is writable before setting info
			this.fileManager.makeWritable(file);
			org.jaudiotagger.audio.AudioFileIO.delete(readFile(file));
		} catch (CannotReadException e) {
			reportWriteError(file, e);
		} catch (CannotWriteException e) {
			reportWriteError(file, e);
		} catch (Exception e) {
			// We must catch any other exception to avoid throw exceptions
			// outside this method
			reportWriteError(file, e);
		}
	}

	private AudioFile readFile(final ILocalAudioObject file) {
		return this.jAudiotaggerFileReader
				.getAudioFile(new File(file.getUrl()));
	}

	@Override
	public void writeTag(final ILocalAudioObject file,
			final boolean shouldEditCover, final byte[] cover,
			final String title, final String album, final String artist,
			final int year, final String comment, final String genre,
			final String lyrics, final String composer, final int track,
			final int discNumber, final String albumArtist) {
		try {
			writeTagToFile(file, shouldEditCover, cover, title, album, artist,
					year, comment, genre, lyrics, composer, track, discNumber,
					albumArtist);
		} catch (IOException e) {
			reportWriteError(file, e);
		} catch (CannotReadException e) {
			reportWriteError(file, e);
		} catch (TagException e) {
			reportWriteError(file, e);
		} catch (ReadOnlyFileException e) {
			reportWriteError(file, e);
		} catch (InvalidAudioFrameException e) {
			reportWriteError(file, e);
		} catch (CannotWriteException e) {
			reportWriteError(file, e);
		} catch (Exception e) {
			// We must catch any other exception to avoid throw exceptions
			// outside this method
			reportWriteError(file, e);
		}
	}

	/**
	 * @param file
	 * @param shouldEditCover
	 * @param cover
	 * @param title
	 * @param album
	 * @param artist
	 * @param year
	 * @param comment
	 * @param genre
	 * @param lyrics
	 * @param composer
	 * @param track
	 * @param discNumber
	 * @param albumArtist
	 * @throws CannotReadException
	 * @throws IOException
	 * @throws TagException
	 * @throws ReadOnlyFileException
	 * @throws InvalidAudioFrameException
	 * @throws ImageReadException
	 * @throws CannotWriteException
	 */
	private void writeTagToFile(final ILocalAudioObject file,
			final boolean shouldEditCover, final byte[] cover,
			final String title, final String album, final String artist,
			final int year, final String comment, final String genre,
			final String lyrics, final String composer, final int track,
			final int discNumber, final String albumArtist)
			throws CannotReadException, IOException, TagException,
			ReadOnlyFileException, InvalidAudioFrameException,
			ImageReadException, CannotWriteException {
		// Be sure file is writable before setting info
		this.fileManager.makeWritable(file);

		org.jaudiotagger.audio.AudioFile audioFile = readFile(file);
		org.jaudiotagger.tag.Tag newTag = audioFile
				.getTagOrCreateAndSetDefault();

		if (this.localAudioObjectValidator.isOneOfTheseFormats(file,
				LocalAudioObjectFormat.MP3)) {
			org.jaudiotagger.audio.mp3.MP3File mp3file = (org.jaudiotagger.audio.mp3.MP3File) audioFile;
			if (mp3file.hasID3v1Tag() && !mp3file.hasID3v2Tag()) {
				deleteTags(file);
				audioFile = readFile(file);
				newTag = audioFile.getTagOrCreateAndSetDefault();
			}
		}

		editCover(file, shouldEditCover, cover, newTag);

		// Workaround for mp4 files - strings outside genre list might not be
		// written otherwise
		if (this.localAudioObjectValidator.isOneOfTheseFormats(file,
				LocalAudioObjectFormat.MP4_1, LocalAudioObjectFormat.MP4_2)) {
			newTag.deleteField(FieldKey.GENRE);
		}

		setTagFields(file, title, album, artist, year, comment, genre, lyrics,
				composer, track, discNumber, albumArtist, newTag);

		audioFile.setTag(newTag);
		audioFile.commit();
	}

	/**
	 * @param file
	 * @param title
	 * @param album
	 * @param artist
	 * @param year
	 * @param comment
	 * @param genre
	 * @param lyrics
	 * @param composer
	 * @param track
	 * @param discNumber
	 * @param albumArtist
	 * @param newTag
	 * @throws FieldDataInvalidException
	 */
	private void setTagFields(final ILocalAudioObject file, final String title,
			final String album, final String artist, final int year,
			final String comment, final String genre, final String lyrics,
			final String composer, final int track, final int discNumber,
			final String albumArtist, final org.jaudiotagger.tag.Tag newTag)
			throws FieldDataInvalidException {
		setStringTagField(file, newTag, FieldKey.ALBUM, album);
		setStringTagField(file, newTag, FieldKey.ARTIST, artist);
		setStringTagField(file, newTag, FieldKey.COMMENT, comment);
		setStringTagField(file, newTag, FieldKey.GENRE, genre);
		setStringTagField(file, newTag, FieldKey.TITLE, title);
		setNumberTagField(file, newTag, FieldKey.YEAR, year);
		setNumberTagField(file, newTag, FieldKey.TRACK, track);
		if (discNumber <= 0) {
			newTag.deleteField(FieldKey.DISC_NO);
		} else {
			// only set the discnumber if we have a useful one. remove previous
			// one to avoid errors
			String discno = newTag.getFirst(FieldKey.DISC_NO);
			if (!StringUtils.isEmpty(discno)) {
				newTag.deleteField(FieldKey.DISC_NO);
			}
			newTag.setField(FieldKey.DISC_NO, Integer.toString(discNumber));
		}
		setStringTagField(file, newTag, FieldKey.LYRICS, lyrics);
		setStringTagField(file, newTag, FieldKey.ALBUM_ARTIST, albumArtist);
		setStringTagField(file, newTag, FieldKey.COMPOSER, composer);
	}

	/**
	 * Logs error while writing
	 * 
	 * @param file
	 * @param e
	 */
	private final void reportWriteError(final ILocalAudioObject file,
			final Exception e) {
		Logger.error(StringUtils.getString("Could not edit tag. File: ",
				file.getUrl(), " Error: ", e));
		Logger.error(e);
	}

	/**
	 * @param file
	 * @param shouldEditCover
	 * @param cover
	 * @param newTag
	 * @throws ImageReadException
	 * @throws IOException
	 * @throws FieldDataInvalidException
	 */
	private void editCover(final ILocalAudioObject file,
			final boolean shouldEditCover, final byte[] cover,
			final org.jaudiotagger.tag.Tag newTag) throws ImageReadException,
			IOException, FieldDataInvalidException {
		if (shouldEditCover) {
			// Remove old images
			try {
				newTag.deleteArtworkField();
			} catch (KeyNotFoundException e) {
				Logger.error(StringUtils.getString(
						"Could not delte artwork field. File: ", file.getUrl(),
						" Error: ", e));
			}

			if (cover != null) {
				Artwork artwork = new Artwork();
				artwork.setBinaryData(cover);
				artwork.setDescription("cover");
				artwork.setPictureType(PictureTypes.DEFAULT_ID);
				artwork.setLinked(false);
				artwork.setMimeType(Sanselan.getImageInfo(cover).getMimeType());
				newTag.setField(newTag.createField(artwork));
			}
		}
	}

	private void setStringTagField(final ILocalAudioObject file,
			final org.jaudiotagger.tag.Tag tag, final FieldKey fieldKey,
			final String fieldValue) {
		if (fieldValue == null || fieldValue.isEmpty()) {
			// Delete tag field if value is empty
			tag.deleteField(fieldKey);
		} else {
			try {
				tag.setField(tag.createField(fieldKey, fieldValue));
			} catch (FieldDataInvalidException e) {
				reportWriteFieldError(file, fieldKey, fieldValue, e);
			} catch (KeyNotFoundException e) {
				reportWriteFieldError(file, fieldKey, fieldValue, e);
			}
		}
	}

	private void setNumberTagField(final ILocalAudioObject file,
			final org.jaudiotagger.tag.Tag tag, final FieldKey fieldKey,
			final int fieldValue) {
		if (fieldValue == -1) {
			tag.deleteField(fieldKey);
		} else {
			try {
				tag.setField(fieldKey, String.valueOf(fieldValue));
			} catch (KeyNotFoundException e) {
				reportWriteFieldError(file, fieldKey,
						Integer.toString(fieldValue), e);
			} catch (FieldDataInvalidException e) {
				reportWriteFieldError(file, fieldKey,
						Integer.toString(fieldValue), e);

			}
		}
	}

	/**
	 * Logs error while writing a field
	 * 
	 * @param file
	 * @param fieldKey
	 * @param fieldValue
	 */
	private final void reportWriteFieldError(final ILocalAudioObject file,
			final FieldKey fieldKey, final String fieldValue, final Exception e) {
		Logger.error(StringUtils.getString("Could not edit tag field ",
				fieldKey.name(), " with value \"", fieldValue, "\" for file: ",
				file.getUrl(), " Error: ", e));
	}

	/**
	 * Changes file with given modification
	 * 
	 * @param file
	 * @param modification
	 */
	private void modifyTag(final ILocalAudioObject file,
			final IJAudiotaggerTagModification modification) {
		// Be sure file is writable before setting info
		try {
			this.fileManager.makeWritable(file);
			org.jaudiotagger.audio.AudioFile audioFile = readFile(file);
			org.jaudiotagger.tag.Tag newTag = audioFile
					.getTagOrCreateAndSetDefault();
			modification.modify(newTag);
			audioFile.commit();
		} catch (TagException e) {
			reportWriteError(file, e);
		} catch (CannotWriteException e) {
			reportWriteError(file, e);
		} catch (Exception e) {
			// We must catch any other exception to avoid throw exceptions
			// outside this method
			reportWriteError(file, e);
		}
	}

	@Override
	public void readData(final ILocalAudioObject ao, final boolean readRating,
			final boolean readAudioProperties) {
		AudioFile file = readFile(ao);
		ao.setTag(this.jAudiotaggerTagCreator.createTag(ao, file, readRating));
		this.jAudiotaggerAudioPropertiesReader.readAudioProperties(ao, file);
	}

	@Override
	public void readRating(final ILocalAudioObject ao) {
		this.jAudiotaggerTagCreator.setRatingInTag(ao, readFile(ao));
	}

	@Override
	public ImageIcon getImage(final ILocalAudioObject file, final int width,
			final int height) {
		Logger.debug("Getting internal image to file: ",
				this.fileManager.getPath(file));
		try {
			org.jaudiotagger.tag.Tag tag = readFile(file).getTag();
			if (tag != null) {
				Artwork artwork = tag.getFirstArtwork();
				byte[] imageRawData = artwork != null ? artwork.getBinaryData()
						: null;

				if (imageRawData != null) {
					return processInternalPicture(width, height, imageRawData);
				}
			}
		} catch (FileNotFoundException e) {
			Logger.error(StringUtils.getString("File not found: ",
					this.fileManager.getPath(file)));
		} catch (Exception e) {
			Logger.error(e);
		}
		return null;
	}

	/**
	 * @param width
	 * @param height
	 * @param imageRawData
	 * @throws IOException
	 */
	private ImageIcon processInternalPicture(final int width, final int height,
			final byte[] imageRawData) throws IOException {
		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(imageRawData));
		if (bi != null) {
			ImageIcon imageIcon = new ImageIcon(bi);
			if (width != -1 || height != -1) {
				int maxSize = (imageIcon.getIconWidth() > imageIcon
						.getIconHeight()) ? imageIcon.getIconWidth()
						: imageIcon.getIconHeight();
				int newWidth = (int) ((float) imageIcon.getIconWidth()
						/ (float) maxSize * width);
				int newHeight = (int) ((float) imageIcon.getIconHeight()
						/ (float) maxSize * height);

				BufferedImage resizedImage = ImageUtils
						.toBufferedImage(ImageUtils.scaleImageBicubic(
								imageIcon.getImage(), newWidth, newHeight)
								.getImage());
				if (resizedImage != null) {
					return new ImageIcon(resizedImage);
				}
			} else {
				return new ImageIcon(bi);
			}
		}
		return null;
	}

	@Override
	public boolean isFormatSupported(final ILocalAudioObject ao) {
		return this.localAudioObjectValidator.isOneOfTheseFormats(ao,
				LocalAudioObjectFormat.MP3, LocalAudioObjectFormat.MP4_1,
				LocalAudioObjectFormat.MP4_2, LocalAudioObjectFormat.OGG,
				LocalAudioObjectFormat.FLAC, LocalAudioObjectFormat.WMA);
	}
}
