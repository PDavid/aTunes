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

import java.util.Map;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITag;

/**
 * Manages creation of tags
 * 
 * @author alex
 * 
 */
public class TagFactory {

	static final String TITLE = "TITLE";
	static final String ARTIST = "ARTIST";
	static final String ALBUM = "ALBUM";
	static final String YEAR = "YEAR";
	static final String COMMENT = "COMMENT";
	static final String TRACK = "TRACK";
	static final String DISC_NUMBER = "DISC_NUMBER";
	static final String GENRE = "GENRE";
	static final String LYRICS = "LYRICS";
	static final String COMPOSER = "COMPOSER";
	static final String ALBUM_ARTIST = "ALBUM_ARTIST";
	static final String RATING = "RATING";

	private RatingsToStars ratingsToStars;

	/**
	 * @param ratingsToStars
	 */
	public void setRatingsToStars(final RatingsToStars ratingsToStars) {
		this.ratingsToStars = ratingsToStars;
	}

	/**
	 * Gets the new tag from file tag and given properties
	 * 
	 * @param file
	 * @param tagInformation
	 * @return
	 */
	public ITag getNewTag(final ILocalAudioObject file,
			final Map<String, Object> tagInformation) {
		return mergeTagWithProperties(file.getTag(), tagInformation);
	}

	/**
	 * Gets the new tag from given properties
	 * 
	 * @param tagInformation
	 * @return
	 */
	public ITag getNewTag(final Map<String, Object> tagInformation) {
		return mergeTagWithProperties(getNewTag(), tagInformation);
	}

	/**
	 * Creates a new empty tag
	 * 
	 * @return
	 */
	public ITag getNewTag() {
		return new DefaultTag();
	}

	private ITag mergeTagWithProperties(final ITag tag,
			final Map<String, Object> properties) {
		ITag newTag = getNewTag();
		setTitleFromProperties(properties, tag, newTag);
		setArtistFromProperties(properties, tag, newTag);
		setAlbumFromProperties(properties, tag, newTag);
		setYearFromProperties(properties, tag, newTag);
		setCommentFromProperties(properties, tag, newTag);
		setTrackNumberFromProperties(properties, tag, newTag);
		setDiscNumberFromProperties(properties, tag, newTag);
		setGenreFromProperties(properties, tag, newTag);
		setLyricsFromProperties(properties, tag, newTag);
		setComposerFromProperties(properties, tag, newTag);
		setAlbumArtistFromProperties(properties, tag, newTag);
		setRatingFromProperties(properties, tag, newTag);
		return newTag;
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 * @param newTag
	 */
	private void setAlbumArtistFromProperties(
			final Map<String, Object> editTagInfo, final ITag oldTag,
			final ITag newTag) {
		if (editTagInfo.containsKey(ALBUM_ARTIST)) {
			newTag.setAlbumArtist((String) editTagInfo.get(ALBUM_ARTIST));
		} else {
			newTag.setAlbumArtist(oldTag != null ? oldTag.getAlbumArtist()
					: null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 * @param newTag
	 */
	private void setRatingFromProperties(final Map<String, Object> editTagInfo,
			final ITag oldTag, final ITag newTag) {
		if (newTag != null) {
			if (editTagInfo.containsKey(RATING)
					&& editTagInfo.get(RATING) != null) {
				newTag.setStars(this.ratingsToStars
						.ratingToStars((String) editTagInfo.get(RATING)));
			} else {
				newTag.setStars(oldTag != null ? oldTag.getStars() : null);
			}
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 * @param newTag
	 */
	private void setComposerFromProperties(
			final Map<String, Object> editTagInfo, final ITag oldTag,
			final ITag newTag) {
		if (editTagInfo.containsKey(COMPOSER)) {
			newTag.setComposer((String) editTagInfo.get(COMPOSER));
		} else {
			newTag.setComposer(oldTag != null ? oldTag.getComposer() : null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 * @param newTag
	 */
	private void setLyricsFromProperties(final Map<String, Object> editTagInfo,
			final ITag oldTag, final ITag newTag) {
		if (editTagInfo.containsKey(LYRICS)) {
			newTag.setLyrics((String) editTagInfo.get(LYRICS));
		} else {
			newTag.setLyrics(oldTag != null ? oldTag.getLyrics() : null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 * @param newTag
	 */
	private void setGenreFromProperties(final Map<String, Object> editTagInfo,
			final ITag oldTag, final ITag newTag) {
		if (editTagInfo.containsKey(GENRE)) {
			String genreString = (String) editTagInfo.get(GENRE);
			if (genreString == null) {
				newTag.setGenre("");
			} else {
				newTag.setGenre(genreString);
			}
		} else {
			newTag.setGenre(oldTag != null ? oldTag.getGenre() : null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 * @param newTag
	 */
	private void setDiscNumberFromProperties(
			final Map<String, Object> editTagInfo, final ITag oldTag,
			final ITag newTag) {
		if (editTagInfo.containsKey(DISC_NUMBER)) {
			try {
				newTag.setDiscNumber(Integer.parseInt((String) editTagInfo
						.get(DISC_NUMBER)));
			} catch (NumberFormatException ex) {
				newTag.setDiscNumber(0);
			}
		} else {
			newTag.setDiscNumber(oldTag != null ? oldTag.getDiscNumber() : 1);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 * @param newTag
	 */
	private void setTrackNumberFromProperties(
			final Map<String, Object> editTagInfo, final ITag oldTag,
			final ITag newTag) {
		if (editTagInfo.containsKey(TRACK)) {
			try {
				newTag.setTrackNumber(Integer.parseInt((String) editTagInfo
						.get(TRACK)));
			} catch (NumberFormatException ex) {
				newTag.setTrackNumber(-1);
			}
		} else {
			newTag.setTrackNumber(oldTag != null ? oldTag.getTrackNumber() : 0);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 * @param newTag
	 */
	private void setCommentFromProperties(
			final Map<String, Object> editTagInfo, final ITag oldTag,
			final ITag newTag) {
		if (editTagInfo.containsKey(COMMENT)) {
			newTag.setComment((String) editTagInfo.get(COMMENT));
		} else {
			newTag.setComment(oldTag != null ? oldTag.getComment() : null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 * @param newTag
	 */
	private void setYearFromProperties(final Map<String, Object> editTagInfo,
			final ITag oldTag, final ITag newTag) {
		if (editTagInfo.containsKey(YEAR)) {
			try {
				newTag.setYear(Integer.parseInt((String) editTagInfo.get(YEAR)));
			} catch (NumberFormatException ex) {
				newTag.setYear(-1);
			}
		} else {
			newTag.setYear(oldTag != null ? oldTag.getYear() : 0);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 * @param newTag
	 */
	private void setAlbumFromProperties(final Map<String, Object> editTagInfo,
			final ITag oldTag, final ITag newTag) {
		if (editTagInfo.containsKey(ALBUM)) {
			newTag.setAlbum((String) editTagInfo.get(ALBUM));
		} else {
			newTag.setAlbum(oldTag != null ? oldTag.getAlbum() : null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 * @param newTag
	 */
	private void setArtistFromProperties(final Map<String, Object> editTagInfo,
			final ITag oldTag, final ITag newTag) {
		if (editTagInfo.containsKey(ARTIST)) {
			newTag.setArtist((String) editTagInfo.get(ARTIST));
		} else {
			newTag.setArtist(oldTag != null ? oldTag.getArtist() : null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 * @param newTag
	 */
	private void setTitleFromProperties(final Map<String, Object> editTagInfo,
			final ITag oldTag, final ITag newTag) {
		if (editTagInfo.containsKey(TITLE)) {
			newTag.setTitle((String) editTagInfo.get(TITLE));
		} else {
			newTag.setTitle(oldTag != null ? oldTag.getTitle() : null);
		}
	}
}
