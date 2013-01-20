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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	private List<String> attributes;

	private Set<String> numericalAttributes;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	final List<String> getSearchableAttributes() {
		if (this.attributes == null) {
			this.attributes = new ArrayList<String>();
			this.attributes.add(INDEX_FIELD_TITLE);
			this.attributes.add(INDEX_FIELD_TRACK_NUMBER);
			this.attributes.add(INDEX_FIELD_ARTIST);
			this.attributes.add(INDEX_FIELD_ALBUM_ARTIST);
			this.attributes.add(INDEX_FIELD_ALBUM);
			this.attributes.add(INDEX_FIELD_COMPOSER);
			this.attributes.add(INDEX_FIELD_YEAR);
			this.attributes.add(INDEX_FIELD_GENRE);
			this.attributes.add(INDEX_FIELD_DURATION);
			this.attributes.add(INDEX_FIELD_BITRATE);
			this.attributes.add(INDEX_FIELD_FREQUENCY);
			this.attributes.add(INDEX_FIELD_SCORE);
			this.attributes.add(INDEX_FIELD_FILENAME);
			this.attributes.add(INDEX_FIELD_LYRICS);
			this.attributes.add(INDEX_FIELD_URL);
		}
		return this.attributes;
	}

	final boolean isNumericAttribute(final String attribute) {
		if (this.numericalAttributes == null) {
			this.numericalAttributes = new HashSet<String>();
			this.numericalAttributes.add(INDEX_FIELD_TRACK_NUMBER);
			this.numericalAttributes.add(INDEX_FIELD_YEAR);
			this.numericalAttributes.add(INDEX_FIELD_BITRATE);
			this.numericalAttributes.add(INDEX_FIELD_FREQUENCY);
			this.numericalAttributes.add(INDEX_FIELD_SCORE);
		}
		return this.numericalAttributes.contains(attribute);
	}

	final void addFieldsToDocument(final Document d,
			final ILocalAudioObject audioFile) {
		d.add(new Field(INDEX_FIELD_TITLE, audioFile.getTitle(),
				Field.Store.YES, Field.Index.ANALYZED));
		d.add(new Field(INDEX_FIELD_TRACK_NUMBER, String.valueOf(audioFile
				.getTrackNumber()), Field.Store.YES, Field.Index.NOT_ANALYZED));
		d.add(new Field(INDEX_FIELD_ARTIST, audioFile
				.getArtist(this.unknownObjectChecker), Field.Store.YES,
				Field.Index.ANALYZED));
		d.add(new Field(INDEX_FIELD_ALBUM_ARTIST, audioFile
				.getAlbumArtist(this.unknownObjectChecker), Field.Store.YES,
				Field.Index.ANALYZED));
		d.add(new Field(INDEX_FIELD_ALBUM, audioFile
				.getAlbum(this.unknownObjectChecker), Field.Store.YES,
				Field.Index.ANALYZED));
		d.add(new Field(INDEX_FIELD_COMPOSER, audioFile.getComposer(),
				Field.Store.YES, Field.Index.ANALYZED));
		d.add(new Field(INDEX_FIELD_YEAR, audioFile.getYear(), Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		d.add(new Field(INDEX_FIELD_GENRE, audioFile
				.getGenre(this.unknownObjectChecker), Field.Store.YES,
				Field.Index.ANALYZED));
		d.add(new Field(INDEX_FIELD_DURATION, String.valueOf(audioFile
				.getDuration()), Field.Store.YES, Field.Index.NOT_ANALYZED));
		d.add(new Field(INDEX_FIELD_BITRATE, String.valueOf(audioFile
				.getBitrate()), Field.Store.YES, Field.Index.NOT_ANALYZED));
		d.add(new Field(INDEX_FIELD_FREQUENCY, String.valueOf(audioFile
				.getFrequency()), Field.Store.YES, Field.Index.NOT_ANALYZED));
		d.add(new Field(INDEX_FIELD_SCORE,
				String.valueOf(audioFile.getStars()), Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		d.add(new Field(INDEX_FIELD_LYRICS, audioFile.getLyrics(),
				Field.Store.YES, Field.Index.ANALYZED));
		d.add(new Field(INDEX_FIELD_FILENAME, audioFile
				.getNameWithoutExtension(), Field.Store.YES,
				Field.Index.ANALYZED));
		d.add(new Field(INDEX_FIELD_URL, audioFile.getUrl(), Field.Store.YES,
				Field.Index.NO));
	}

}
