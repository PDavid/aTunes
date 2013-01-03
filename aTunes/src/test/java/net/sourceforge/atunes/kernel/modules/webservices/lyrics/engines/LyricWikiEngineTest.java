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

import net.sourceforge.atunes.kernel.modules.network.NetworkHandler;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.LyricWikiEngine;
import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.ILyricsRetrieveOperation;
import net.sourceforge.atunes.model.IStateCore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LyricWikiEngineTest {

    private LyricWikiEngine testedObject;

    @Before
    public void init() {
    	NetworkHandler networkHandler = new NetworkHandler();
    	networkHandler.setStateCore(mock(IStateCore.class));
        testedObject = new LyricWikiEngine();
        testedObject.setNetworkHandler(networkHandler);
    }

    @Test
    public void testGetLyricsFor() {
        ILyrics lyrics = testedObject.getLyricsFor("Louis Armstrong", "We Have All The Time In The World", mock(ILyricsRetrieveOperation.class));
        Assert.assertNotNull(lyrics);
        Assert.assertTrue(lyrics.getUrl().contains("lyrics.wikia.com"));
        Assert.assertTrue(lyrics.getLyrics().toLowerCase().contains("Time enough for life to unfold".toLowerCase()));
    }

    @Test
    public void testCaseInsensitivity() {
        ILyrics lyrics1 = testedObject.getLyricsFor("Louis Armstrong", "We Have All The Time In The World", mock(ILyricsRetrieveOperation.class));
        ILyrics lyrics2 = testedObject.getLyricsFor("Louis armstrong", "We Have All The Time In The World", mock(ILyricsRetrieveOperation.class));
        ILyrics lyrics3 = testedObject.getLyricsFor("Louis Armstrong", "We Have All the Time In the world", mock(ILyricsRetrieveOperation.class));

        Assert.assertEquals(lyrics1, lyrics2);
        Assert.assertEquals(lyrics2, lyrics3);
        Assert.assertEquals(lyrics3, lyrics1);
    }

    @Test
    public void testGetLyricsForWhereArtistAndTitleNotExist() {
        ILyrics lyrics = testedObject.getLyricsFor(UUID.randomUUID().toString(), UUID.randomUUID().toString(), mock(ILyricsRetrieveOperation.class));
        Assert.assertEquals(null, lyrics);
    }
}
