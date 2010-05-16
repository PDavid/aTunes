/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.util.UUID;

import net.sourceforge.atunes.kernel.modules.webservices.lyrics.Lyrics;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LyrDBEngineTest {

    private LyrDBEngine testedObject;

    private static final String ARTIST = "The Beatles";
    private static final String SONG = "Yesterday";
    private static final String LYRIC_CONTENT = "all my troubles seemed so far away";
    
    @Before
    public void init() {
        testedObject = new LyrDBEngine(null);
    }

    @Test
    public void testGetLyricsFor() {
        Lyrics lyrics = testedObject.getLyricsFor(ARTIST, SONG);
        Assert.assertNotNull(lyrics);
        Assert.assertTrue(lyrics.getUrl().contains("lyrdb.com"));
        Assert.assertTrue(lyrics.getLyrics().toLowerCase().contains(LYRIC_CONTENT.toLowerCase()));
    }

    @Test
    public void testCaseInsensitivity() {
        Lyrics lyrics1 = testedObject.getLyricsFor(ARTIST, SONG);
        Lyrics lyrics2 = testedObject.getLyricsFor(ARTIST.toLowerCase(), SONG);
        Lyrics lyrics3 = testedObject.getLyricsFor(ARTIST, SONG.toUpperCase());

        Assert.assertEquals(lyrics1, lyrics2);
        Assert.assertEquals(lyrics2, lyrics3);
        Assert.assertEquals(lyrics3, lyrics1);
    }

    @Test
    public void testGetLyricsForWhereArtistAndTitleNotExist() {
        Lyrics lyrics = testedObject.getLyricsFor(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        Assert.assertEquals(null, lyrics);
    }
}
