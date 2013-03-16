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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITagAdapter;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FilenameUtils;
import org.apache.sanselan.ImageWriteException;

/**
 * Tag Adapter to read and write tags from properties files
 * 
 * @author alex
 * 
 */
public class PropertiesFileTagAdapter implements ITagAdapter {

	private RatingsToStars ratingsToStars;

	private TagFactory tagFactory;

	/**
	 * @param tagFactory
	 */
	public void setTagFactory(TagFactory tagFactory) {
		this.tagFactory = tagFactory;
	}

	/**
	 * @param ratingsToStars
	 */
	public void setRatingsToStars(RatingsToStars ratingsToStars) {
		this.ratingsToStars = ratingsToStars;
	}

	@Override
	public boolean isFormatSupported(ILocalAudioObject ao) {
		// All formats are supported by this adapter
		return true;
	}

	@Override
	public void deleteTags(ILocalAudioObject file) {
		File propertiesFile = new File(getPropertiesFile(file));
		boolean deleted = propertiesFile.delete();
		if (!deleted) {
			Logger.error(StringUtils.getString(
					propertiesFile.getAbsolutePath(), " not deleted"));
		}
	}

	@Override
	public ImageIcon getImage(ILocalAudioObject audioObject, int width,
			int height) {
		String coverFileName = getFileNameForCover(audioObject);
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

	private String getFileNameForCover(final ILocalAudioObject file) {
		if (file == null) {
			return null;
		}

		return StringUtils.getString(
				FilenameUtils.removeExtension(file.getUrl()), ".",
				ImageUtils.FILES_EXTENSION);
	}

	private void modifyField(ILocalAudioObject file, String field, Object value) {
		Properties properties = getProperties(file);
		properties.put(field, value);
		setProperties(file, properties);
	}

	@Override
	public void modifyAlbum(ILocalAudioObject file, String album) {
		modifyField(file, TagFactory.ALBUM, album);
	}

	@Override
	public void modifyGenre(ILocalAudioObject file, String genre) {
		modifyField(file, TagFactory.GENRE, genre);
	}

	@Override
	public void modifyLyrics(ILocalAudioObject file, String lyrics) {
		modifyField(file, TagFactory.LYRICS, lyrics);
	}

	@Override
	public void modifyStars(ILocalAudioObject file, String starsToRating) {
		modifyField(file, TagFactory.RATING, starsToRating);
	}

	@Override
	public void modifyTitle(ILocalAudioObject file, String newTitle) {
		modifyField(file, TagFactory.TITLE, newTitle);
	}

	@Override
	public void modifyTrack(ILocalAudioObject file, Integer track) {
		modifyField(file, TagFactory.TRACK, track);
	}

	@Override
	public void readTag(ILocalAudioObject ao, boolean readAudioProperties) {
		Map<String, Object> values = new HashMap<String, Object>();
		for (Entry<Object, Object> entry : getProperties(ao).entrySet()) {
			values.put((String) entry.getKey(), entry.getValue());
		}
		ao.setTag(tagFactory.getNewTag(values));
	}

	private Properties getProperties(ILocalAudioObject ao) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(getPropertiesFile(ao)));
		} catch (FileNotFoundException e) {
			// No properties found, it's ok
		} catch (IOException e) {
			reportError(ao, e);
		}
		return properties;
	}

	@Override
	public void writeTag(ILocalAudioObject file, boolean shouldEditCover,
			byte[] cover, String title, String album, String artist, int year,
			String comment, String genre, String lyrics, String composer,
			int track, int discNumber, String albumArtist) {
		Properties properties = new Properties();
		properties.put(TagFactory.TITLE, title);
		properties.put(TagFactory.ALBUM, album);
		properties.put(TagFactory.ARTIST, artist);
		properties.put(TagFactory.YEAR, Integer.toString(year));
		properties.put(TagFactory.COMMENT, comment);
		properties.put(TagFactory.GENRE, genre);
		properties.put(TagFactory.LYRICS, lyrics);
		properties.put(TagFactory.COMPOSER, composer);
		properties.put(TagFactory.TRACK, Integer.toString(track));
		properties.put(TagFactory.DISC_NUMBER, Integer.toString(discNumber));
		properties.put(TagFactory.ALBUM_ARTIST, albumArtist);
		properties.put(TagFactory.RATING,
				this.ratingsToStars.starsToRating(file.getStars()));

		setProperties(file, properties);

		if (shouldEditCover) {
			try {
				BufferedImage bi = ImageIO
						.read(new ByteArrayInputStream(cover));
				ImageUtils.writeImageToFile(bi, getFileNameForCover(file));
			} catch (IOException e) {
				reportError(file, e);
			} catch (ImageWriteException e) {
				reportError(file, e);
			}
		}
	}

	private void setProperties(ILocalAudioObject file, Properties properties) {
		try {
			properties.store(new FileOutputStream(getPropertiesFile(file)),
					"Metadata generated with aTunes");
		} catch (FileNotFoundException e) {
			reportError(file, e);
		} catch (IOException e) {
			reportError(file, e);
		}
	}

	private String getPropertiesFile(ILocalAudioObject file) {
		return StringUtils.getString(
				FilenameUtils.removeExtension(file.getUrl()), ".properties");
	}

	/**
	 * Logs error while writing
	 * 
	 * @param file
	 * @param e
	 */
	private final void reportError(final ILocalAudioObject file,
			final Exception e) {
		Logger.error(StringUtils.getString(
				"Could not read / write tag. File: ", file.getUrl(),
				" Error: ", e));
		Logger.error(e);
	}
}
