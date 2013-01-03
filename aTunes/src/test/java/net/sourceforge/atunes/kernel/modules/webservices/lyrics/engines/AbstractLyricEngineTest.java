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

package net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines;

import static org.mockito.Mockito.mock;

import java.util.UUID;

import net.sourceforge.atunes.kernel.modules.webservices.lyrics.AbstractLyricsEngine;
import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.ILyricsRetrieveOperation;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractLyricEngineTest {

    private String artist;
    private String song;
    private String lyricContent;
    private AbstractLyricsEngine testedObject;
    private String engineUrl;
    
    @Test
    public void testGetLyricsFor() {
        ILyrics lyrics = testedObject.getLyricsFor(artist, song, mock(ILyricsRetrieveOperation.class));
        Assert.assertNotNull(lyrics);
        Assert.assertTrue(lyrics.getUrl().contains(engineUrl));
        Assert.assertTrue(lyrics.getLyrics().toLowerCase().contains(lyricContent.toLowerCase()));
    }

    @Test
    public void testCaseInsensitivity() {
        ILyrics lyrics1 = testedObject.getLyricsFor(artist, song, mock(ILyricsRetrieveOperation.class));
        ILyrics lyrics2 = testedObject.getLyricsFor(artist.toLowerCase(), song, mock(ILyricsRetrieveOperation.class));
        ILyrics lyrics3 = testedObject.getLyricsFor(artist, song.toUpperCase(), mock(ILyricsRetrieveOperation.class));

        Assert.assertEquals(lyrics1, lyrics2);
        Assert.assertEquals(lyrics2, lyrics3);
        Assert.assertEquals(lyrics3, lyrics1);
    }

    @Test
    public void testGetLyricsForWhereArtistAndTitleNotExist() {
        ILyrics lyrics = testedObject.getLyricsFor(UUID.randomUUID().toString(), UUID.randomUUID().toString(), mock(ILyricsRetrieveOperation.class));
        Assert.assertEquals(null, lyrics);
    }

	/**
	 * @return the artist
	 */
	protected String getArtist() {
		return artist;
	}

	/**
	 * @param artist the artist to set
	 */
	protected void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * @return the song
	 */
	protected String getSong() {
		return song;
	}

	/**
	 * @param song the song to set
	 */
	protected void setSong(String song) {
		this.song = song;
	}

	/**
	 * @return the lyricContent
	 */
	protected String getLyricContent() {
		return lyricContent;
	}

	/**
	 * @param lyricContent the lyricContent to set
	 */
	protected void setLyricContent(String lyricContent) {
		this.lyricContent = lyricContent;
	}

	/**
	 * @return the testedObject
	 */
	protected AbstractLyricsEngine getTestedObject() {
		return testedObject;
	}

	/**
	 * @param testedObject the testedObject to set
	 */
	protected void setTestedObject(AbstractLyricsEngine testedObject) {
		this.testedObject = testedObject;
	}

	/**
	 * @return the engineUrl
	 */
	protected String getEngineUrl() {
		return engineUrl;
	}

	/**
	 * @param engineUrl the engineUrl to set
	 */
	protected void setEngineUrl(String engineUrl) {
		this.engineUrl = engineUrl;
	}
}
