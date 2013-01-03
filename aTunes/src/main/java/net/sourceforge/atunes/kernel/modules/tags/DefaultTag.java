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

import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

/**
 * The default tag. Can read tags from JAudiotagger and from properties.
 */
public class DefaultTag extends AbstractTag {

	private static final long serialVersionUID = 6200185803652819029L;

	/**
	 * Instantiates a new default tag.
	 */
	DefaultTag() {
		// Nothing to do
	}

	/**
	 * Stores tag so application can read them. Regular method. Uses
	 * JAudiotagger.
	 * 
	 * @param tag
	 * @param genres
	 * @param ratingsToStars
	 */
	DefaultTag(final org.jaudiotagger.tag.Tag tag, final Genres genres,
			final RatingsToStars ratingsToStars) {
		setAlbum(getFirstTagValue(tag, FieldKey.ALBUM));
		setArtist(getFirstTagValue(tag, FieldKey.ARTIST));
		setComment(getFirstTagValue(tag, FieldKey.COMMENT));
		setGenreFromTag(getFirstTagValue(tag, FieldKey.GENRE), genres);
		setTitle(getFirstTagValue(tag, FieldKey.TITLE));
		setTrackNumberFromTag(getFirstTagValue(tag, FieldKey.TRACK));
		setYearFromTag(getFirstTagValue(tag, FieldKey.YEAR));
		setLyrics(getFirstTagValue(tag, FieldKey.LYRICS));
		setComposer(getFirstTagValue(tag, FieldKey.COMPOSER));
		setAlbumArtist(getFirstTagValue(tag, FieldKey.ALBUM_ARTIST));
		setInternalImage(tag);
		setDate(tag);
		setDiscNumber(tag);
		setStars(ratingsToStars.ratingToStars(getFirstTagValue(tag,
				FieldKey.RATING)));
	}

	private String getFirstTagValue(final Tag tag, final FieldKey field) {
		if (tag != null && field != null) {
			try {
				return tag.getFirst(field);
			} catch (UnsupportedOperationException e) {
				Logger.info(e.getMessage());
			}
		}
		return null;
	}

	private String getFirstTagValue(final Tag tag, final String field) {
		if (tag != null && field != null) {
			try {
				return tag.getFirst(field);
			} catch (UnsupportedOperationException e) {
				Logger.info(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * Sets internal image
	 * 
	 * @param tag
	 */
	private void setInternalImage(final org.jaudiotagger.tag.Tag tag) {
		boolean hasImage = false;
		if (tag != null) {
			try {
				hasImage = tag.hasField(FieldKey.COVER_ART.name());
			} catch (UnsupportedOperationException e) {
				Logger.info(e.getMessage());
				// Sometimes image is not supported (ID3v1). It's not a problem
			}
		}
		setInternalImage(hasImage);
	}

	/**
	 * Sets disc number
	 * 
	 * @param tag
	 */
	private void setDiscNumber(final org.jaudiotagger.tag.Tag tag) {
		// Disc Number
		String discNumberStr = getFirstTagValue(tag, FieldKey.DISC_NO);
		if (discNumberStr != null && !discNumberStr.trim().equals("")) {
			// try to get disc number parsing string
			try {
				setDiscNumber(Integer.parseInt(discNumberStr));
			} catch (NumberFormatException e) {
				// Sometimes disc number appears as relative to overall disc
				// count: "1/2"
				if (discNumberStr.contains("/")) {
					int separatorPosition = discNumberStr.indexOf('/');
					try {
						setDiscNumber(Integer.parseInt(discNumberStr.substring(
								0, separatorPosition)));
					} catch (NumberFormatException e2) {
						// Disc number seems not valid
					}
				}
			}
		}
	}

	/**
	 * Sets year from tag
	 * 
	 * @param tag
	 */
	private void setYearFromTag(final String year) {
		if (StringUtils.isEmpty(year)) {
			setYear(-1);
		} else {
			try {
				setYear(Integer.parseInt(year));
			} catch (NumberFormatException e) {
				setYear(-1);
			}
		}
	}

	/**
	 * Sets track number from tag
	 * 
	 * @param tag
	 */
	private void setTrackNumberFromTag(final String track) {
		String result = track;
		try {
			if (StringUtils.isEmpty(result)) {
				result = "-1";
			}
			// Certain tags are in the form of track number/total number of
			// tracks so check for this:
			if (result.contains("/")) {
				int separatorPosition;
				separatorPosition = result.indexOf('/');
				setTrackNumber(Integer.parseInt(result.substring(0,
						separatorPosition)));
			} else {
				setTrackNumber(Integer.parseInt(result));
			}
		} catch (NumberFormatException e) {
			setTrackNumber(-1);
		}
	}

	/**
	 * Code taken from Jajuk http://jajuk.info - (Copyright (C) 2008) The Jajuk
	 * team Detects if Genre is a number and try to map the corresponding genre
	 * This should only happen with ID3 tags Sometimes, the style has this form
	 * : (nb)
	 * 
	 * @param genre
	 * @param genres
	 */
	private void setGenreFromTag(final String genre, final Genres genres) {
		String result = genre;
		if (!StringUtils.isEmpty(result) && result.matches("\\(.*\\).*")) {
			result = result.substring(1, result.indexOf(')'));
			try {
				result = genres.getGenreForCode(Integer.parseInt(result));
			} catch (Exception e) {
				setGenre(""); // error, return unknown
			}
		}
		// If genre is a number mapping a known style, use this style
		try {
			int number = Integer.parseInt(result);
			if (number >= 0
					&& !StringUtils.isEmpty(genres.getGenreForCode(number))) {
				result = genres.getGenreForCode(Integer.parseInt(result));
			}
		} catch (NumberFormatException e) {
			// nothing wrong here
		}
		setGenre(result);
	}

	@Override
	public ITag setTagFromProperties(final ITag tag,
			final Map<String, Object> properties) {
		setTitleFromProperties(properties, tag);
		setArtistFromProperties(properties, tag);
		setAlbumFromProperties(properties, tag);
		setYearFromProperties(properties, tag);
		setCommentFromProperties(properties, tag);
		setTrackNumberFromProperties(properties, tag);
		setDiscNumberFromProperties(properties, tag);
		setGenreFromProperties(properties, tag);
		setLyricsFromProperties(properties, tag);
		setComposerFromProperties(properties, tag);
		setAlbumArtistFromProperties(properties, tag);
		return this;
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 */
	private void setAlbumArtistFromProperties(
			final Map<String, Object> editTagInfo, final ITag oldTag) {
		if (editTagInfo.containsKey("ALBUM_ARTIST")) {
			setAlbumArtist((String) editTagInfo.get("ALBUM_ARTIST"));
		} else {
			setAlbumArtist(oldTag != null ? oldTag.getAlbumArtist() : null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 */
	private void setComposerFromProperties(
			final Map<String, Object> editTagInfo, final ITag oldTag) {
		if (editTagInfo.containsKey("COMPOSER")) {
			setComposer((String) editTagInfo.get("COMPOSER"));
		} else {
			setComposer(oldTag != null ? oldTag.getComposer() : null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 */
	private void setLyricsFromProperties(final Map<String, Object> editTagInfo,
			final ITag oldTag) {
		if (editTagInfo.containsKey("LYRICS")) {
			setLyrics((String) editTagInfo.get("LYRICS"));
		} else {
			setLyrics(oldTag != null ? oldTag.getLyrics() : null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 */
	private void setGenreFromProperties(final Map<String, Object> editTagInfo,
			final ITag oldTag) {
		if (editTagInfo.containsKey("GENRE")) {
			String genreString = (String) editTagInfo.get("GENRE");
			if (genreString == null) {
				setGenre("");
			} else {
				setGenre(genreString);
			}
		} else {
			setGenre(oldTag != null ? oldTag.getGenre() : null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 */
	private void setDiscNumberFromProperties(
			final Map<String, Object> editTagInfo, final ITag oldTag) {
		if (editTagInfo.containsKey("DISC_NUMBER")) {
			try {
				setDiscNumber(Integer.parseInt((String) editTagInfo
						.get("DISC_NUMBER")));
			} catch (NumberFormatException ex) {
				setDiscNumber(0);
			}
		} else {
			setDiscNumber(oldTag != null ? oldTag.getDiscNumber() : 1);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 */
	private void setTrackNumberFromProperties(
			final Map<String, Object> editTagInfo, final ITag oldTag) {
		if (editTagInfo.containsKey("TRACK")) {
			try {
				setTrackNumber(Integer.parseInt((String) editTagInfo
						.get("TRACK")));
			} catch (NumberFormatException ex) {
				setTrackNumber(-1);
			}
		} else {
			setTrackNumber(oldTag != null ? oldTag.getTrackNumber() : 0);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 */
	private void setCommentFromProperties(
			final Map<String, Object> editTagInfo, final ITag oldTag) {
		if (editTagInfo.containsKey("COMMENT")) {
			setComment((String) editTagInfo.get("COMMENT"));
		} else {
			setComment(oldTag != null ? oldTag.getComment() : null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 */
	private void setYearFromProperties(final Map<String, Object> editTagInfo,
			final ITag oldTag) {
		if (editTagInfo.containsKey("YEAR")) {
			try {
				setYear(Integer.parseInt((String) editTagInfo.get("YEAR")));
			} catch (NumberFormatException ex) {
				setYear(-1);
			}
		} else {
			setYear(oldTag != null ? oldTag.getYear() : 0);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 */
	private void setAlbumFromProperties(final Map<String, Object> editTagInfo,
			final ITag oldTag) {
		if (editTagInfo.containsKey("ALBUM")) {
			setAlbum((String) editTagInfo.get("ALBUM"));
		} else {
			setAlbum(oldTag != null ? oldTag.getAlbum() : null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 */
	private void setArtistFromProperties(final Map<String, Object> editTagInfo,
			final ITag oldTag) {
		if (editTagInfo.containsKey("ARTIST")) {
			setArtist((String) editTagInfo.get("ARTIST"));
		} else {
			setArtist(oldTag != null ? oldTag.getArtist() : null);
		}
	}

	/**
	 * @param editTagInfo
	 * @param oldTag
	 */
	private void setTitleFromProperties(final Map<String, Object> editTagInfo,
			final ITag oldTag) {
		if (editTagInfo.containsKey("TITLE")) {
			setTitle((String) editTagInfo.get("TITLE"));
		} else {
			setTitle(oldTag != null ? oldTag.getTitle() : null);
		}
	}

	/**
	 * Sets date from tag
	 * 
	 * @param tag
	 */
	private void setDate(final org.jaudiotagger.tag.Tag tag) {
		if (tag instanceof org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag
				|| tag instanceof org.jaudiotagger.tag.flac.FlacTag) {
			setDate(DateUtils.parseRFC3339Date(getFirstTagValue(tag, "DATE")));
		} else if (tag instanceof org.jaudiotagger.tag.id3.ID3v24Tag) {
			setDate(DateUtils.parseRFC3339Date(getFirstTagValue(tag, "TDRC")));
		} else if (tag instanceof org.jaudiotagger.tag.id3.ID3v23Tag) {
			setDateFromID3v23Tag(tag);
		} else {
			setDate((DateTime) null);
		}
	}

	/**
	 * @param tag
	 */
	private void setDateFromID3v23Tag(final org.jaudiotagger.tag.Tag tag) {
		// Set date from fields tag TYER and date/month tag TDAT
		DateMidnight c = null;
		String yearPart = getFirstTagValue(tag, "TYER");
		if (!StringUtils.isEmpty(yearPart)) {
			try {
				c = new DateMidnight().withYear(Integer.parseInt(yearPart))
						.withMonthOfYear(1).withDayOfMonth(1);
				String dateMonthPart = getFirstTagValue(tag, "TDAT");
				if (!StringUtils.isEmpty(dateMonthPart)
						&& dateMonthPart.length() >= 4) {
					c = c.withMonthOfYear(
							Integer.parseInt(dateMonthPart.substring(2, 4)))
							.withDayOfMonth(
									Integer.parseInt(dateMonthPart.substring(0,
											2)));
				}
			} catch (NumberFormatException e) {
				// Skip this date
			} catch (IllegalArgumentException e) {
				// Skip this date
			}
		}

		if (c != null) {
			setDate(c);
		}
	}

}
