/*
 * aTunes 2.1.0-SNAPSHOT
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
import java.util.Calendar;
import java.util.Date;

import net.sourceforge.atunes.utils.DateUtils;

/**
 * The abstract class for tags.
 * 
 * @author fleax
 */
public abstract class AbstractTag implements Serializable {

    private static final long serialVersionUID = -4044670497563446883L;

    public static final String[] genres = { "Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other",
            "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop",
            "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk",
            "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream",
            "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave",
            "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk",
            "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock",
            "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Brass",
            "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock",
            "Drum Solo", "A Capela", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat",
            "Christian Gangsta Rap", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "JPop",
            "SynthPop" };

    /** The title. */
    private String title;

    /** The artist. */
    private String artist;

    /** The album. */
    private String album;

    /** The year. */
    private int year;

    /** The date. */
    private Date date;

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

    /**
     * Tries to find the string provided in the table and returns the
     * corresponding int code if successful. Returns -1 if the genres is not
     * found in the table.
     * 
     * @param str
     *            the genre to search for
     * 
     * @return the integer code for the genre or -1 if the genre is not found
     */
    public static int getGenre(String str) {
        int retval = -1;

        for (int i = 0; (i < genres.length); i++) {
            if (genres[i].equalsIgnoreCase(str)) {
                retval = i;
                break;
            }
        }

        return retval;
    }

    /**
     * Gets the genre for code.
     * 
     * @param code
     *            the code
     * 
     * @return the genre for code
     */
    public static final String getGenreForCode(int code) {
        return code >= 0 ? genres[code] : "";
    }

    /**
     * Gets the album.
     * 
     * @return the album
     */
    public String getAlbum() {
        return album;
    }

    /**
     * Gets the album artist.
     * 
     * @return the album artist
     */
    public String getAlbumArtist() {
        return albumArtist;
    }

    /**
     * Gets the artist.
     * 
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Gets the comment.
     * 
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Gets the composer.
     * 
     * @return the composer
     */
    public String getComposer() {
        return composer;
    }

    /**
     * Gets the genre.
     * 
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Gets the lyrics.
     * 
     * @return the lyrics
     */
    public String getLyrics() {
        return lyrics;
    }

    /**
     * Gets the tag from properties.
     * 
     * @param properties
     *            the properties
     * 
     * @return the tag from properties
     */
    public abstract AbstractTag getTagFromProperties(EditTagInfo editTagInfo, AbstractTag oldTag);

    /**
     * Gets the title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the track number.
     * 
     * @return the track number
     */
    public int getTrackNumber() {
        return trackNumber >= 0 ? trackNumber : 0;
    }

    /**
     * Gets the year.
     * 
     * @return the year
     */
    public int getYear() {
        if (date != null) {
            Calendar calendar = DateUtils.getCalendar();
            calendar.setTime(date);
            return calendar.get(Calendar.YEAR);
        } else if (year >= 0) {
            return year;
        } else {
            return 0;
        }
    }

    /**
     * Gets the date.
     * 
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the album.
     * 
     * @param album
     *            the new album
     */
    public void setAlbum(String album) {
        this.album = album != null ? album.trim() : "";
    }

    /**
     * Sets the album artist.
     * 
     * @param albumArtist
     *            the new album artist
     */
    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist != null ? albumArtist.trim() : "";
    }

    /**
     * Sets the artist.
     * 
     * @param artist
     *            the new artist
     */
    public void setArtist(String artist) {
        this.artist = artist != null ? artist.trim() : "";
    }

    /**
     * Sets the comment.
     * 
     * @param comment
     *            the new comment
     */
    public void setComment(String comment) {
        this.comment = comment != null ? comment.trim() : "";
    }

    /**
     * Sets the composer.
     * 
     * @param composer
     *            the new composer
     */
    public void setComposer(String composer) {
        this.composer = composer != null ? composer.trim() : "";
    }

    /**
     * Sets the genre.
     * 
     * @param genre
     *            the new genre
     */
    public void setGenre(int genre) {
        this.genre = getGenreForCode(genre);
    }

    /**
     * Sets the genre.
     * 
     * @param genre
     *            the new genre
     */
    public void setGenre(String genre) {
        this.genre = genre != null ? genre.trim() : "";
    }

    /**
     * Sets the lyrics.
     * 
     * @param lyrics
     *            the new lyrics
     */
    public void setLyrics(String lyrics) {
        this.lyrics = lyrics != null ? lyrics : "";
    }

    /**
     * Sets the title.
     * 
     * @param title
     *            the new title
     */
    public void setTitle(String title) {
        this.title = title != null ? title.trim() : "";
    }

    /**
     * Sets the track number.
     * 
     * @param tracknumber
     *            the new track number
     */
    public void setTrackNumber(int tracknumber) {
        this.trackNumber = tracknumber;
    }

    /**
     * Sets the year.
     * 
     * @param year
     *            the new year
     */
    public void setYear(int year) {
        // only update year if it is not derived from the date
        if (date == null) {
            this.year = year;
        }
    }

    /**
     * Sets the date.
     * 
     * @param date
     *            the new date
     */
    public void setDate(Date date) {
        this.date = date != null ? new Date(date.getTime()) : null;
    }

    /**
     * Returns true if this tag has an internal image, false otherwise
     * 
     * @return true if this tag has an internal image, false otherwise
     */
    public boolean hasInternalImage() {
        return internalImage;
    }

    /**
     * Sets if this tag has an internal image
     * 
     * @param internalImage
     *            if this tag has an internal image
     */
    public void setInternalImage(boolean internalImage) {
        this.internalImage = internalImage;
    }

    /**
     * @return the discNumber
     */
    public int getDiscNumber() {
        return discNumber >= 0 ? discNumber : 0;
    }

    /**
     * @param discNumber
     *            the discNumber to set
     */
    public void setDiscNumber(int discNumber) {
        this.discNumber = discNumber;
    }

}
