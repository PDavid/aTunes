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

import java.io.Serializable;

import net.sourceforge.atunes.model.ITag;

import org.joda.time.base.BaseDateTime;

/**
 * The abstract class for tags.
 * 
 * @author fleax
 */
public abstract class AbstractTag implements Serializable, ITag {

    private static final long serialVersionUID = -4044670497563446883L;

    /** The title. */
    private String title;

    /** The artist. */
    private String artist;

    /** The album. */
    private String album;

    /** The year. */
    private int year;

    /** The date. */
    private BaseDateTime date;

    /** The comment. */
    private String comment;

    /** The genre. */
    private String genre;

    /** The lyrics. */
    private String lyrics;

    /** The composer. */
    private String composer;

    /** The album artist. */
    private String albumArtist;

    /** The track number. */
    private int trackNumber;

    /** The disc number */
    private int discNumber;

    /** If this tag has an internal image */
    private boolean internalImage;

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#getAlbum()
	 */
    @Override
	public String getAlbum() {
        return album;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#getAlbumArtist()
	 */
    @Override
	public String getAlbumArtist() {
        return albumArtist;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#getArtist()
	 */
    @Override
	public String getArtist() {
        return artist;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#getComment()
	 */
    @Override
	public String getComment() {
        return comment;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#getComposer()
	 */
    @Override
	public String getComposer() {
        return composer;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#getGenre()
	 */
    @Override
	public String getGenre() {
        return genre;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#getLyrics()
	 */
    @Override
	public String getLyrics() {
        return lyrics;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setTagFromProperties(net.sourceforge.atunes.kernel.modules.tags.EditTagInfo, net.sourceforge.atunes.kernel.modules.tags.ITag)
	 */
    @Override
	public abstract ITag setTagFromProperties(EditTagInfo editTagInfo, ITag oldTag);

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#getTitle()
	 */
    @Override
	public String getTitle() {
        return title;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#getTrackNumber()
	 */
    @Override
	public int getTrackNumber() {
        return trackNumber >= 0 ? trackNumber : 0;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#getYear()
	 */
    @Override
	public int getYear() {
        if (date != null) {
        	return date.getYear();
        } else if (year >= 0) {
            return year;
        } else {
            return 0;
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#getDate()
	 */
    @Override
	public BaseDateTime getDate() {
        return date;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setAlbum(java.lang.String)
	 */
    @Override
	public void setAlbum(String album) {
        this.album = album != null ? album.trim() : "";
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setAlbumArtist(java.lang.String)
	 */
    @Override
	public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist != null ? albumArtist.trim() : "";
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setArtist(java.lang.String)
	 */
    @Override
	public void setArtist(String artist) {
        this.artist = artist != null ? artist.trim() : "";
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setComment(java.lang.String)
	 */
    @Override
	public void setComment(String comment) {
        this.comment = comment != null ? comment.trim() : "";
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setComposer(java.lang.String)
	 */
    @Override
	public void setComposer(String composer) {
        this.composer = composer != null ? composer.trim() : "";
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setGenre(java.lang.String)
	 */
    @Override
	public void setGenre(String genre) {
        this.genre = genre != null ? genre.trim() : "";
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setLyrics(java.lang.String)
	 */
    @Override
	public void setLyrics(String lyrics) {
        this.lyrics = lyrics != null ? lyrics : "";
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setTitle(java.lang.String)
	 */
    @Override
	public void setTitle(String title) {
        this.title = title != null ? title.trim() : "";
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setTrackNumber(int)
	 */
    @Override
	public void setTrackNumber(int tracknumber) {
        this.trackNumber = tracknumber;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setYear(int)
	 */
    @Override
	public void setYear(int year) {
        // only update year if it is not derived from the date
        if (date == null) {
            this.year = year;
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setDate(org.joda.time.base.BaseDateTime)
	 */
    @Override
	public void setDate(BaseDateTime date) {
        this.date = date;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#hasInternalImage()
	 */
    @Override
	public boolean hasInternalImage() {
        return internalImage;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setInternalImage(boolean)
	 */
    @Override
	public void setInternalImage(boolean internalImage) {
        this.internalImage = internalImage;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#getDiscNumber()
	 */
    @Override
	public int getDiscNumber() {
        return discNumber >= 0 ? discNumber : 0;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITag#setDiscNumber(int)
	 */
    @Override
	public void setDiscNumber(int discNumber) {
        this.discNumber = discNumber;
    }

}
