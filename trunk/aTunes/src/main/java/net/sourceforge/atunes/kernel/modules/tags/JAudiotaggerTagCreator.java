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

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

/**
 * Creates tags by reading with JAudiotagger
 * 
 * @author alex
 * 
 */
public class JAudiotaggerTagCreator {

	private Genres genresHelper;

	private RatingsToStars ratingsToStars;

	private TagFactory tagFactory;

	/**
	 * @param tagFactory
	 */
	public void setTagFactory(final TagFactory tagFactory) {
		this.tagFactory = tagFactory;
	}

	/**
	 * @param ratingsToStars
	 */
	public void setRatingsToStars(final RatingsToStars ratingsToStars) {
		this.ratingsToStars = ratingsToStars;
	}

	/**
	 * @param genresHelper
	 */
	public void setGenresHelper(final Genres genresHelper) {
		this.genresHelper = genresHelper;
	}

	/**
	 * Creates tag for given audio file
	 * 
	 * @param ao
	 * @param file
	 * @param readRating
	 * @return
	 */
	ITag createTag(final ILocalAudioObject ao, final AudioFile file,
			boolean readRating) {
		ITag iTag = this.tagFactory.getNewTag();
		if (file != null) {
			Tag tag = file.getTag();
			iTag.setAlbum(getFirstTagValue(tag, FieldKey.ALBUM));
			iTag.setArtist(getFirstTagValue(tag, FieldKey.ARTIST));
			iTag.setComment(getFirstTagValue(tag, FieldKey.COMMENT));
			setGenreFromTag(iTag, getFirstTagValue(tag, FieldKey.GENRE),
					this.genresHelper);
			iTag.setTitle(getFirstTagValue(tag, FieldKey.TITLE));
			setTrackNumberFromTag(iTag, getFirstTagValue(tag, FieldKey.TRACK));
			setYearFromTag(iTag, getFirstTagValue(tag, FieldKey.YEAR));
			iTag.setLyrics(getFirstTagValue(tag, FieldKey.LYRICS));
			iTag.setComposer(getFirstTagValue(tag, FieldKey.COMPOSER));
			iTag.setAlbumArtist(getFirstTagValue(tag, FieldKey.ALBUM_ARTIST));
			setInternalImage(iTag, tag);
			setDate(iTag, tag);
			setDiscNumber(iTag, tag);
			if (readRating) {
				iTag.setStars(this.ratingsToStars
						.ratingToStars(getFirstTagValue(tag, FieldKey.RATING)));
			}
		}
		return iTag;
	}

	/**
	 * Sets tag in given audio object
	 * 
	 * @param ao
	 * @param file
	 * @return
	 */
	public void setRatingInTag(ILocalAudioObject ao, AudioFile file) {
		if (ao != null && ao.getTag() != null && file != null) {
			Tag tag = file.getTag();
			ao.getTag().setStars(
					this.ratingsToStars.ratingToStars(getFirstTagValue(tag,
							FieldKey.RATING)));
		}
	}

	/**
	 * Sets date from tag
	 * 
	 * @param iTag
	 * @param tag
	 */
	private void setDate(final ITag iTag, final org.jaudiotagger.tag.Tag tag) {
		if (tag instanceof org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag
				|| tag instanceof org.jaudiotagger.tag.flac.FlacTag) {
			iTag.setDate(DateUtils.parseRFC3339Date(getFirstTagValue(tag,
					"DATE")));
		} else if (tag instanceof org.jaudiotagger.tag.id3.ID3v24Tag) {
			iTag.setDate(DateUtils.parseRFC3339Date(getFirstTagValue(tag,
					"TDRC")));
		} else if (tag instanceof org.jaudiotagger.tag.id3.ID3v23Tag) {
			setDateFromID3v23Tag(iTag, tag);
		} else {
			iTag.setDate((DateTime) null);
		}
	}

	/**
	 * Sets internal image
	 * 
	 * @param iTag
	 * @param tag
	 */
	private void setInternalImage(final ITag iTag,
			final org.jaudiotagger.tag.Tag tag) {
		boolean hasImage = false;
		if (tag != null) {
			try {
				hasImage = tag.hasField(FieldKey.COVER_ART.name());
			} catch (UnsupportedOperationException e) {
				Logger.info(e.getMessage());
				// Sometimes image is not supported (ID3v1). It's not a problem
			}
		}
		iTag.setInternalImage(hasImage);
	}

	/**
	 * Sets year from tag
	 * 
	 * @param tag
	 */
	private void setYearFromTag(final ITag iTag, final String year) {
		if (StringUtils.isEmpty(year)) {
			iTag.setYear(-1);
		} else {
			try {
				iTag.setYear(Integer.parseInt(year));
			} catch (NumberFormatException e) {
				iTag.setYear(-1);
			}
		}
	}

	/**
	 * Sets track number from tag
	 * 
	 * @param iTag
	 * @param track
	 */
	private void setTrackNumberFromTag(final ITag iTag, final String track) {
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
				iTag.setTrackNumber(Integer.parseInt(result.substring(0,
						separatorPosition)));
			} else {
				iTag.setTrackNumber(Integer.parseInt(result));
			}
		} catch (NumberFormatException e) {
			iTag.setTrackNumber(-1);
		}
	}

	/**
	 * Code taken from Jajuk http://jajuk.info - (Copyright (C) 2008) The Jajuk
	 * team Detects if Genre is a number and try to map the corresponding genre
	 * This should only happen with ID3 tags Sometimes, the style has this form
	 * : (nb)
	 * 
	 * @param iTag
	 * @param genre
	 * @param genres
	 */
	private void setGenreFromTag(final ITag iTag, final String genre,
			final Genres genres) {
		String result = genre;
		if (!StringUtils.isEmpty(result) && result.matches("\\(.*\\).*")) {
			result = result.substring(1, result.indexOf(')'));
			try {
				result = genres.getGenreForCode(Integer.parseInt(result));
			} catch (Exception e) {
				iTag.setGenre(""); // error, return unknown
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
		iTag.setGenre(result);
	}

	/**
	 * @param iTag
	 * @param tag
	 */
	private void setDateFromID3v23Tag(final ITag iTag,
			final org.jaudiotagger.tag.Tag tag) {
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
			iTag.setDate(c);
		}
	}

	/**
	 * Sets disc number
	 * 
	 * @param iTag
	 * @param tag
	 */
	private void setDiscNumber(final ITag iTag,
			final org.jaudiotagger.tag.Tag tag) {
		// Disc Number
		String discNumberStr = getFirstTagValue(tag, FieldKey.DISC_NO);
		if (discNumberStr != null && !discNumberStr.trim().equals("")) {
			// try to get disc number parsing string
			try {
				iTag.setDiscNumber(Integer.parseInt(discNumberStr));
			} catch (NumberFormatException e) {
				// Sometimes disc number appears as relative to overall disc
				// count: "1/2"
				if (discNumberStr.contains("/")) {
					int separatorPosition = discNumberStr.indexOf('/');
					try {
						iTag.setDiscNumber(Integer.parseInt(discNumberStr
								.substring(0, separatorPosition)));
					} catch (NumberFormatException e2) {
						// Disc number seems not valid
					}
				}
			}
		}
	}

	private String getFirstTagValue(final Tag tag, final String field) {
		if (tag != null && field != null) {
			try {
				return tag.getFirst(field);
			} catch (UnsupportedOperationException e) {
				Logger.info(e.getMessage());
			} catch (Exception e) {
				// Avoid any error caused by underlying tag reader
				Logger.error(e.getMessage());
			}
		}
		return null;
	}

	private String getFirstTagValue(final Tag tag, final FieldKey field) {
		if (tag != null && field != null) {
			try {
				return tag.getFirst(field);
			} catch (UnsupportedOperationException e) {
				Logger.info(e.getMessage());
			} catch (Exception e) {
				// Avoid any error caused by underlying tag reader
				Logger.error(e.getMessage());
			}
		}
		return null;
	}
}
