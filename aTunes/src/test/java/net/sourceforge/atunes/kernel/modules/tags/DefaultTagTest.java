/*
 * aTunes 3.1.0
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jaudiotagger.tag.Tag;
import org.junit.Test;

public class DefaultTagTest {

	@Test
	public void testNullInConstructor() {
		DefaultTag tag = new DefaultTag(null, null, new RatingsToStars());
		assertEquals("", tag.getAlbum());
		assertEquals("", tag.getAlbumArtist());
		assertEquals("", tag.getArtist());
		assertEquals("", tag.getComment());
		assertEquals("", tag.getComposer());
		assertNull(tag.getDate());
		assertEquals(0, tag.getDiscNumber());
		assertEquals("", tag.getGenre());
		assertEquals("", tag.getLyrics());
		assertEquals("", tag.getTitle());
		assertEquals(0, tag.getTrackNumber());
		assertEquals(0, tag.getYear());
		assertEquals(0, tag.getStars());
	}

	@Test
	public void testUnsupportedOperationException() {
		Tag wrongTag = new WrongTag();
		DefaultTag tag = new DefaultTag(wrongTag, null, new RatingsToStars());
		assertEquals("", tag.getAlbum());
		assertEquals("", tag.getAlbumArtist());
		assertEquals("", tag.getArtist());
		assertEquals("", tag.getComment());
		assertEquals("", tag.getComposer());
		assertNull(tag.getDate());
		assertEquals(0, tag.getDiscNumber());
		assertEquals("", tag.getGenre());
		assertEquals("", tag.getLyrics());
		assertEquals("", tag.getTitle());
		assertEquals(0, tag.getTrackNumber());
		assertEquals(0, tag.getYear());
		assertEquals(0, tag.getStars());
	}
}
