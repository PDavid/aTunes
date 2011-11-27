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

package net.sourceforge.atunes.kernel.modules.search;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchableObject;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * This class represents common methods for all searchable objects that contain
 * AudioFiles
 * 
 * @author fleax
 * 
 */
public abstract class AbstractCommonAudioFileSearchableObject implements ISearchableObject {

    // Fields for indexing
    /** The Constant INDEX_FIELD_TITLE. */
    protected static final String INDEX_FIELD_TITLE = "title";

    /** The Constant INDEX_FIELD_NUMBER. */
    protected static final String INDEX_FIELD_TRACK_NUMBER = "track";

    /** The Constant INDEX_FIELD_ARTIST. */
    protected static final String INDEX_FIELD_ARTIST = "artist";

    /** The Constant INDEX_FIELD_ALBUM_ARTIST. */
    protected static final String INDEX_FIELD_ALBUM_ARTIST = "album_artist";

    /** The Constant INDEX_FIELD_ALBUM. */
    protected static final String INDEX_FIELD_ALBUM = "album";

    /** The Constant INDEX_FIELD_COMPOSER. */
    protected static final String INDEX_FIELD_COMPOSER = "composer";

    /** The Constant INDEX_FIELD_YEAR. */
    protected static final String INDEX_FIELD_YEAR = "year";

    /** The Constant INDEX_FIELD_GENRE. */
    protected static final String INDEX_FIELD_GENRE = "genre";

    /** The Constant INDEX_FIELD_DURATION. */
    protected static final String INDEX_FIELD_DURATION = "duration";

    /** The Constant INDEX_FIELD_BITRATE. */
    protected static final String INDEX_FIELD_BITRATE = "bitrate";

    /** The Constant INDEX_FIELD_FREQUENCY. */
    protected static final String INDEX_FIELD_FREQUENCY = "frequency";

    /** The Constant INDEX_FIELD_STARS. */
    protected static final String INDEX_FIELD_SCORE = "score";

    /** The Constant INDEX_FIELD_LYRICS. */
    protected static final String INDEX_FIELD_LYRICS = "lyrics";

    /** The Constant INDEX_FIELD_FILENAME. */
    protected static final String INDEX_FIELD_FILENAME = "file_name";

    /** The Constant INDEX_FIELD_URL. */
    protected static final String INDEX_FIELD_URL = "url";

    /*
     * (non-Javadoc)
     * 
     * @seenet.sourceforge.atunes.kernel.modules.search.SearchableObject#
     * getSearchableAttributes()
     */
    @Override
    public List<String> getSearchableAttributes() {
        List<String> result = new ArrayList<String>();
        result.add(INDEX_FIELD_TITLE);
        result.add(INDEX_FIELD_TRACK_NUMBER);
        result.add(INDEX_FIELD_ARTIST);
        result.add(INDEX_FIELD_ALBUM_ARTIST);
        result.add(INDEX_FIELD_ALBUM);
        result.add(INDEX_FIELD_COMPOSER);
        result.add(INDEX_FIELD_YEAR);
        result.add(INDEX_FIELD_GENRE);
        result.add(INDEX_FIELD_DURATION);
        result.add(INDEX_FIELD_BITRATE);
        result.add(INDEX_FIELD_FREQUENCY);
        result.add(INDEX_FIELD_SCORE);
        result.add(INDEX_FIELD_FILENAME);
        result.add(INDEX_FIELD_LYRICS);
        result.add(INDEX_FIELD_URL);
        return result;
    }

    @Override
    public Document getDocumentForElement(IAudioObject audioObject) {
    	if (audioObject instanceof AudioFile) {
    		AudioFile audioFile = (AudioFile) audioObject;

    		Document d = new Document();

    		d.add(new Field(INDEX_FIELD_TITLE, audioFile.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
    		d.add(new Field(INDEX_FIELD_TRACK_NUMBER, String.valueOf(audioFile.getTrackNumber()), Field.Store.YES, Field.Index.NOT_ANALYZED));
    		d.add(new Field(INDEX_FIELD_ARTIST, audioFile.getArtist(), Field.Store.YES, Field.Index.ANALYZED));
    		d.add(new Field(INDEX_FIELD_ALBUM_ARTIST, audioFile.getAlbumArtist(), Field.Store.YES, Field.Index.ANALYZED));
    		d.add(new Field(INDEX_FIELD_ALBUM, audioFile.getAlbum(), Field.Store.YES, Field.Index.ANALYZED));
    		d.add(new Field(INDEX_FIELD_COMPOSER, audioFile.getComposer(), Field.Store.YES, Field.Index.ANALYZED));
    		d.add(new Field(INDEX_FIELD_YEAR, audioFile.getYear(), Field.Store.YES, Field.Index.NOT_ANALYZED));
    		d.add(new Field(INDEX_FIELD_GENRE, audioFile.getGenre(), Field.Store.YES, Field.Index.ANALYZED));
    		d.add(new Field(INDEX_FIELD_DURATION, String.valueOf(audioFile.getDuration()), Field.Store.YES, Field.Index.NOT_ANALYZED));
    		d.add(new Field(INDEX_FIELD_BITRATE, String.valueOf(audioFile.getBitrate()), Field.Store.YES, Field.Index.NOT_ANALYZED));
    		d.add(new Field(INDEX_FIELD_FREQUENCY, String.valueOf(audioFile.getFrequency()), Field.Store.YES, Field.Index.NOT_ANALYZED));
    		d.add(new Field(INDEX_FIELD_SCORE, String.valueOf(audioFile.getStars()), Field.Store.YES, Field.Index.NOT_ANALYZED));
    		d.add(new Field(INDEX_FIELD_LYRICS, audioFile.getLyrics(), Field.Store.YES, Field.Index.ANALYZED));
    		d.add(new Field(INDEX_FIELD_FILENAME, audioFile.getNameWithoutExtension(), Field.Store.YES, Field.Index.ANALYZED));
    		d.add(new Field(INDEX_FIELD_URL, audioFile.getUrl(), Field.Store.YES, Field.Index.NO));
    		/*
    		 * All important fields
    		 */
    		d.add(new Field(ISearchHandler.DEFAULT_INDEX, StringUtils.getString(audioFile.getTitle(), " ", audioFile.getTrackNumber(), " ", audioFile.getArtist(), " ", audioFile
    				.getAlbumArtist(), " ", audioFile.getAlbum(), " ", audioFile.getComposer(), " ", audioFile.getYear(), " ", audioFile.getGenre()), Field.Store.YES,
    				Field.Index.ANALYZED));

    		return d;
    	}
    	return null;
    }
}
