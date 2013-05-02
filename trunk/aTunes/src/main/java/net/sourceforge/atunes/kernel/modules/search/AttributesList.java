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

package net.sourceforge.atunes.kernel.modules.search;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * List of attributes used for custom search
 * 
 * @author alex
 * 
 */
public class AttributesList {

	// Fields for indexing
	/** The Constant INDEX_FIELD_TITLE. */
	private static final String INDEX_FIELD_TITLE = "title";

	/** The Constant INDEX_FIELD_NUMBER. */
	private static final String INDEX_FIELD_TRACK_NUMBER = "track";

	/** The Constant INDEX_FIELD_ARTIST. */
	private static final String INDEX_FIELD_ARTIST = "artist";

	/** The Constant INDEX_FIELD_ALBUM_ARTIST. */
	private static final String INDEX_FIELD_ALBUM_ARTIST = "album_artist";

	/** The Constant INDEX_FIELD_ALBUM. */
	private static final String INDEX_FIELD_ALBUM = "album";

	/** The Constant INDEX_FIELD_COMPOSER. */
	private static final String INDEX_FIELD_COMPOSER = "composer";

	/** The Constant INDEX_FIELD_YEAR. */
	private static final String INDEX_FIELD_YEAR = "year";

	/** The Constant INDEX_FIELD_GENRE. */
	private static final String INDEX_FIELD_GENRE = "genre";

	/** The Constant INDEX_FIELD_DURATION. */
	private static final String INDEX_FIELD_DURATION = "duration";

	/** The Constant INDEX_FIELD_BITRATE. */
	private static final String INDEX_FIELD_BITRATE = "bitrate";

	/** The Constant INDEX_FIELD_FREQUENCY. */
	private static final String INDEX_FIELD_FREQUENCY = "frequency";

	/** The Constant INDEX_FIELD_STARS. */
	private static final String INDEX_FIELD_SCORE = "score";

	/** The Constant INDEX_FIELD_LYRICS. */
	private static final String INDEX_FIELD_LYRICS = "lyrics";

	/** The Constant INDEX_FIELD_FILENAME. */
	private static final String INDEX_FIELD_FILENAME = "file_name";

	/** The Constant INDEX_FIELD_URL. */
	private static final String INDEX_FIELD_URL = "url";

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	final List<String> getSearchableAttributes() {
		List<String> attributes = new ArrayList<String>();
		attributes.add(INDEX_FIELD_TITLE);
		attributes.add(INDEX_FIELD_TRACK_NUMBER);
		attributes.add(INDEX_FIELD_ARTIST);
		attributes.add(INDEX_FIELD_ALBUM_ARTIST);
		attributes.add(INDEX_FIELD_ALBUM);
		attributes.add(INDEX_FIELD_COMPOSER);
		attributes.add(INDEX_FIELD_YEAR);
		attributes.add(INDEX_FIELD_GENRE);
		attributes.add(INDEX_FIELD_DURATION);
		attributes.add(INDEX_FIELD_BITRATE);
		attributes.add(INDEX_FIELD_FREQUENCY);
		attributes.add(INDEX_FIELD_SCORE);
		attributes.add(INDEX_FIELD_FILENAME);
		attributes.add(INDEX_FIELD_LYRICS);
		attributes.add(INDEX_FIELD_URL);
		return attributes;
	}

	final void addFieldsToDocument(final Document d,
			final ILocalAudioObject audioFile) {
		indexTitle(d, audioFile);
		indexTrackNumber(d, audioFile);
		indexArtist(d, audioFile);
		indexAlbumArtist(d, audioFile);
		indexAlbum(d, audioFile);
		indexComposer(d, audioFile);
		indexYear(d, audioFile);
		indexGenre(d, audioFile);
		indexDuration(d, audioFile);
		indexBitrate(d, audioFile);
		indexFrequency(d, audioFile);
		indexScore(d, audioFile);
		indexLyrics(d, audioFile);
		indexFilename(d, audioFile);
		indexUrl(d, audioFile);
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexUrl(final Document d, final ILocalAudioObject audioFile) {
		d.add(new Field(INDEX_FIELD_URL, audioFile.getUrl(), Field.Store.YES,
				Field.Index.NO));
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexFilename(final Document d,
			final ILocalAudioObject audioFile) {
		d.add(new Field(INDEX_FIELD_FILENAME, audioFile
				.getNameWithoutExtension(), Field.Store.YES,
				Field.Index.ANALYZED));
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexLyrics(final Document d, final ILocalAudioObject audioFile) {
		d.add(new Field(INDEX_FIELD_LYRICS, audioFile.getLyrics(),
				Field.Store.YES, Field.Index.ANALYZED));
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexScore(final Document d, final ILocalAudioObject audioFile) {
		d.add(new Field(INDEX_FIELD_SCORE,
				String.valueOf(audioFile.getStars()), Field.Store.YES,
				Field.Index.NOT_ANALYZED));
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexFrequency(final Document d,
			final ILocalAudioObject audioFile) {
		d.add(new Field(INDEX_FIELD_FREQUENCY, String.valueOf(audioFile
				.getFrequency()), Field.Store.YES, Field.Index.NOT_ANALYZED));
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexBitrate(final Document d,
			final ILocalAudioObject audioFile) {
		d.add(new Field(INDEX_FIELD_BITRATE, String.valueOf(audioFile
				.getBitrate()), Field.Store.YES, Field.Index.NOT_ANALYZED));
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexDuration(final Document d,
			final ILocalAudioObject audioFile) {
		d.add(new Field(INDEX_FIELD_DURATION, String.valueOf(audioFile
				.getDuration()), Field.Store.YES, Field.Index.NOT_ANALYZED));
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexGenre(final Document d, final ILocalAudioObject audioFile) {
		indexField(d, INDEX_FIELD_GENRE,
				audioFile.getGenre(this.unknownObjectChecker));
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexYear(final Document d, final ILocalAudioObject audioFile) {
		d.add(new Field(INDEX_FIELD_YEAR, audioFile
				.getYear(this.unknownObjectChecker), Field.Store.YES,
				Field.Index.NOT_ANALYZED));
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexComposer(final Document d,
			final ILocalAudioObject audioFile) {
		indexField(d, INDEX_FIELD_COMPOSER, audioFile.getComposer());
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexAlbum(final Document d, final ILocalAudioObject audioFile) {
		indexField(d, INDEX_FIELD_ALBUM,
				audioFile.getAlbum(this.unknownObjectChecker));
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexAlbumArtist(final Document d,
			final ILocalAudioObject audioFile) {
		indexField(d, INDEX_FIELD_ALBUM_ARTIST,
				audioFile.getAlbumArtist(this.unknownObjectChecker));
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexArtist(final Document d, final ILocalAudioObject audioFile) {
		indexField(d, INDEX_FIELD_ARTIST,
				audioFile.getArtist(this.unknownObjectChecker));
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexTrackNumber(final Document d,
			final ILocalAudioObject audioFile) {
		d.add(new Field(INDEX_FIELD_TRACK_NUMBER, String.valueOf(audioFile
				.getTrackNumber()), Field.Store.YES, Field.Index.NOT_ANALYZED));
	}

	/**
	 * @param d
	 * @param audioFile
	 */
	private void indexTitle(final Document d, final ILocalAudioObject audioFile) {
		indexField(d, INDEX_FIELD_TITLE, audioFile.getTitle());
	}

	/**
	 * Indexes a field using both analyzed and not analyzed forms This is
	 * necessary to allow search numbers in text fields TODO: Find or create a
	 * Lucene analyzer to avoid this
	 * 
	 * @param d
	 * @param fieldName
	 * @param fieldValue
	 */
	private void indexField(final Document d, final String fieldName,
			final String fieldValue) {
		d.add(new Field(fieldName, fieldValue, Field.Store.YES,
				Field.Index.ANALYZED));
		d.add(new Field(fieldName, fieldValue, Field.Store.YES,
				Field.Index.NOT_ANALYZED));
	}
}
