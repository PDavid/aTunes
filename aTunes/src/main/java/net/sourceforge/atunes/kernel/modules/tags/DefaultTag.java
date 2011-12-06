/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jaudiotagger.tag.FieldKey;
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
     *            JAudiotagger type tag must be passed
     */
    DefaultTag(org.jaudiotagger.tag.Tag tag) {
        setAlbum(tag.getFirst(FieldKey.ALBUM));
        setArtist(tag.getFirst(FieldKey.ARTIST));
        setComment(tag.getFirst(FieldKey.COMMENT));
        setGenre(tag);        
        setTitle(tag.getFirst(FieldKey.TITLE));
        setTrackNumber(tag);
        setYear(tag);        
        setLyrics(tag.getFirst(FieldKey.LYRICS));
        setComposer(tag.getFirst(FieldKey.COMPOSER));
        setAlbumArtist(tag.getFirst(FieldKey.ALBUM_ARTIST));
        setInternalImage(tag);
        setDate(tag);
        setDiscNumber(tag);
    }
    
    /**
     * Sets internal image
     * @param tag
     */
    private void setInternalImage(org.jaudiotagger.tag.Tag tag) {
    	boolean hasImage = false;
    	try {
    		hasImage = tag.hasField(FieldKey.COVER_ART.name());
    	} catch (UnsupportedOperationException e) {
    		// Sometimes image is not supported (ID3v1). It's not a problem
    	}
		setInternalImage(hasImage);
    }
    
    /**
     * Sets disc number
     * @param tag
     */
    private void setDiscNumber(org.jaudiotagger.tag.Tag tag) {
        // Disc Number
        String discNumberStr = tag.getFirst(FieldKey.DISC_NO);
        if (discNumberStr != null && !discNumberStr.trim().equals("")) {
            // try to get disc number parsing string
            try {
                setDiscNumber(Integer.parseInt(discNumberStr));
            } catch (NumberFormatException e) {
                // Sometimes disc number appears as relative to overall disc count: "1/2"
                if (discNumberStr.contains("/")) {
                    int separatorPosition = discNumberStr.indexOf('/');
                    try {
                        setDiscNumber(Integer.parseInt(discNumberStr.substring(0, separatorPosition)));
                    } catch (NumberFormatException e2) {
                        // Disc number seems not valid 
                    }
                }
            }
        }
    }
    
    /**
     * Sets year from tag
     * @param tag
     */
    private void setYear(org.jaudiotagger.tag.Tag tag) {
        try {
            setYear(Integer.parseInt(tag.getFirst(FieldKey.YEAR)));
        } catch (NumberFormatException e) {
            setYear(-1);
        }
    }
    
    /**
     * Sets track number from tag
     * @param tag
     */
    private void setTrackNumber(org.jaudiotagger.tag.Tag tag) {
    	String result = null;
        try {
            // We must catch Exception when file has ID3v1.0 tag - This tag format has no track number
            try {
                result = tag.getFirst(FieldKey.TRACK);
            } catch (UnsupportedOperationException e) {
                result = "-1";
            }
            //  Certain tags are in the form of track number/total number of tracks so check for this:
            if (result.contains("/")) {
                int separatorPosition;
                separatorPosition = result.indexOf('/');
                setTrackNumber(Integer.parseInt(result.substring(0, separatorPosition)));
            } else {
                setTrackNumber(Integer.parseInt(result));
            }
        } catch (NumberFormatException e) {
            setTrackNumber(-1);
        }
    }
    
    /**
     * Code taken from Jajuk http://jajuk.info - (Copyright (C) 2008) The Jajuk team
     * Detects if Genre is a number and try to map the corresponding genre
     * This should only happen with ID3 tags
     * Sometimes, the style has this form : (nb)
     * @param tag
     */
    private void setGenre(org.jaudiotagger.tag.Tag tag) {
        String result = tag.getFirst(FieldKey.GENRE);
        if (result.matches("\\(.*\\).*")) {
            result = result.substring(1, result.indexOf(')'));
            try {
                result = Context.getBean(Genres.class).getGenreForCode(Integer.parseInt(result));
            } catch (Exception e) {
                setGenre(""); // error, return unknown
            }
        }
        // If genre is a number mapping a known style, use this style
        try {
            int number = Integer.parseInt(result);
            if (number >= 0 && !StringUtils.isEmpty(Context.getBean(Genres.class).getGenreForCode(number))) {
                result = Context.getBean(Genres.class).getGenreForCode(Integer.parseInt(result));
            }
        } catch (NumberFormatException e) {
            // nothing wrong here
        }
        setGenre(result);
    }

    @Override
    public ITag setTagFromProperties(ITag tag, Map<String, Object> properties) {
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
	private void setAlbumArtistFromProperties(Map<String, Object> editTagInfo, ITag oldTag) {
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
	private void setComposerFromProperties(Map<String, Object> editTagInfo, ITag oldTag) {
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
	private void setLyricsFromProperties(Map<String, Object> editTagInfo, ITag oldTag) {
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
	private void setGenreFromProperties(Map<String, Object> editTagInfo, ITag oldTag) {
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
	private void setDiscNumberFromProperties(Map<String, Object> editTagInfo,
			ITag oldTag) {
		if (editTagInfo.containsKey("DISC_NUMBER")) {
            try {
                setDiscNumber(Integer.parseInt((String) editTagInfo.get("DISC_NUMBER")));
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
	private void setTrackNumberFromProperties(Map<String, Object> editTagInfo,
			ITag oldTag) {
		if (editTagInfo.containsKey("TRACK")) {
            try {
                setTrackNumber(Integer.parseInt((String) editTagInfo.get("TRACK")));
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
	private void setCommentFromProperties(Map<String, Object> editTagInfo, ITag oldTag) {
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
	private void setYearFromProperties(Map<String, Object> editTagInfo, ITag oldTag) {
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
	private void setAlbumFromProperties(Map<String, Object> editTagInfo, ITag oldTag) {
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
	private void setArtistFromProperties(Map<String, Object> editTagInfo, ITag oldTag) {
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
	private void setTitleFromProperties(Map<String, Object> editTagInfo, ITag oldTag) {
		if (editTagInfo.containsKey("TITLE")) {
            setTitle((String) editTagInfo.get("TITLE"));
        } else {
            setTitle(oldTag != null ? oldTag.getTitle() : null);
        }
	}

    /**
     * Sets date from tag
     * @param tag
     */
    private void setDate(org.jaudiotagger.tag.Tag tag) {
        if (tag instanceof org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag || tag instanceof org.jaudiotagger.tag.flac.FlacTag) {
        	setDate(DateUtils.parseRFC3339Date(tag.getFirst("DATE")));
        } else if (tag instanceof org.jaudiotagger.tag.id3.ID3v24Tag) {
        	setDate(DateUtils.parseRFC3339Date(tag.getFirst("TDRC")));
        } else if (tag instanceof org.jaudiotagger.tag.id3.ID3v23Tag) {
        	// Set date from fields tag TYER and date/month tag TDAT
        	DateMidnight c = null;
            String yearPart = tag.getFirst("TYER");
            if (!yearPart.isEmpty()) {
            	try {
            		c = new DateMidnight().withYear(Integer.parseInt(yearPart)).withMonthOfYear(1).withDayOfMonth(1);
                    String dateMonthPart = tag.getFirst("TDAT");
                    if (dateMonthPart.length() >= 4) {
                    	c = c.withMonthOfYear(Integer.parseInt(dateMonthPart.substring(2, 4)))
                    		 .withDayOfMonth(Integer.parseInt(dateMonthPart.substring(0, 2)));
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
        } else {
            setDate((DateTime)null);
        }
    }

}
