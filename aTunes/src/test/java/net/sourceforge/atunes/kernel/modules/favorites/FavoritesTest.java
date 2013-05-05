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

package net.sourceforge.atunes.kernel.modules.favorites;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryTestMockUtils;
import net.sourceforge.atunes.model.ILocalAudioObject;

import org.junit.Before;
import org.junit.Test;

public class FavoritesTest {

	private Favorites sut;

	@Before
	public void init() {
		this.sut = new Favorites();
	}

	@Test
	public void testEmpty() {
		assertTrue(this.sut.getFavoriteArtists().isEmpty());
		assertTrue(this.sut.getFavoriteAlbums().isEmpty());
		assertTrue(this.sut.getFavoriteSongs().isEmpty());
	}

	@Test
	public void testSongs() {
		ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist", "Album", "Title");
		when(ao1.getUrl()).thenReturn("url1");
		ILocalAudioObject ao2 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist", "Album", "Title 2");
		when(ao2.getUrl()).thenReturn("url2");
		this.sut.addSong(ao1);
		assertTrue(this.sut.containsSong(ao1));
		assertFalse(this.sut.containsSong(ao2));
		assertEquals(1, this.sut.getFavoriteSongs().size());
		this.sut.removeSong(ao1);
		this.sut.removeSong(ao2);
		assertFalse(this.sut.containsSong(ao1));
		assertFalse(this.sut.containsSong(ao2));
		assertTrue(this.sut.getFavoriteSongs().isEmpty());
	}
}
