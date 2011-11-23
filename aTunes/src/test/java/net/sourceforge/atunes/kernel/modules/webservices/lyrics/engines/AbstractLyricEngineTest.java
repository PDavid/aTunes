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

package net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines;

import java.util.UUID;

import net.sourceforge.atunes.kernel.modules.network.NetworkHandler;
import net.sourceforge.atunes.model.ILyrics;

import org.junit.Assert;
import org.junit.Test;

public abstract class AbstractLyricEngineTest {

    protected String artist;
    protected String song;
    protected String lyricContent;
    protected AbstractLyricsEngine testedObject;
    protected String engineUrl;
    
    @Test
    public void testGetLyricsFor() {
        ILyrics lyrics = testedObject.getLyricsFor(artist, song);
        Assert.assertNotNull(lyrics);
        Assert.assertTrue(lyrics.getUrl().contains(engineUrl));
        Assert.assertTrue(lyrics.getLyrics().toLowerCase().contains(lyricContent.toLowerCase()));
    }

    @Test
    public void testCaseInsensitivity() {
        ILyrics lyrics1 = testedObject.getLyricsFor(artist, song);
        ILyrics lyrics2 = testedObject.getLyricsFor(artist.toLowerCase(), song);
        ILyrics lyrics3 = testedObject.getLyricsFor(artist, song.toUpperCase());

        Assert.assertEquals(lyrics1, lyrics2);
        Assert.assertEquals(lyrics2, lyrics3);
        Assert.assertEquals(lyrics3, lyrics1);
    }

    @Test
    public void testGetLyricsForWhereArtistAndTitleNotExist() {
        ILyrics lyrics = testedObject.getLyricsFor(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        Assert.assertEquals(null, lyrics);
    }
    
}
