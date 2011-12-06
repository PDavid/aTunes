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
import java.util.Map;

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

    @Override
	public String getAlbum() {
        return album;
    }

    @Override
	public String getAlbumArtist() {
        return albumArtist;
    }

    @Override
	public String getArtist() {
        return artist;
    }

    @Override
	public String getComment() {
        return comment;
    }

    @Override
	public String getComposer() {
        return composer;
    }

    @Override
	public String getGenre() {
        return genre;
    }

    @Override
	public String getLyrics() {
        return lyrics;
    }

    @Override
	public abstract ITag setTagFromProperties(ITag oldTag, Map<String, Object> properties);

    @Override
	public String getTitle() {
        return title;
    }

    @Override
	public int getTrackNumber() {
        return trackNumber >= 0 ? trackNumber : 0;
    }

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

    @Override
	public BaseDateTime getDate() {
        return date;
    }

    @Override
	public void setAlbum(String album) {
        this.album = album != null ? album.trim() : "";
    }

    @Override
	public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist != null ? albumArtist.trim() : "";
    }

    @Override
	public void setArtist(String artist) {
        this.artist = artist != null ? artist.trim() : "";
    }

    @Override
	public void setComment(String comment) {
        this.comment = comment != null ? comment.trim() : "";
    }

    @Override
	public void setComposer(String composer) {
        this.composer = composer != null ? composer.trim() : "";
    }

    @Override
	public void setGenre(String genre) {
        this.genre = genre != null ? genre.trim() : "";
    }

    @Override
	public void setLyrics(String lyrics) {
        this.lyrics = lyrics != null ? lyrics : "";
    }

    @Override
	public void setTitle(String title) {
        this.title = title != null ? title.trim() : "";
    }

    @Override
	public void setTrackNumber(int tracknumber) {
        this.trackNumber = tracknumber;
    }

    @Override
	public void setYear(int year) {
        // only update year if it is not derived from the date
        if (date == null) {
            this.year = year;
        }
    }

    @Override
	public void setDate(BaseDateTime date) {
        this.date = date;
    }

    @Override
	public boolean hasInternalImage() {
        return internalImage;
    }

    @Override
	public void setInternalImage(boolean internalImage) {
        this.internalImage = internalImage;
    }

    @Override
	public int getDiscNumber() {
        return discNumber >= 0 ? discNumber : 0;
    }

    @Override
	public void setDiscNumber(int discNumber) {
        this.discNumber = discNumber;
    }
}
