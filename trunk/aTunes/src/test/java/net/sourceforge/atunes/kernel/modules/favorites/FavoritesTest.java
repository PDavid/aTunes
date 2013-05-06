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

import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryTestMockUtils;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
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

	@Test
	public void testAlbums() {
		ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist", "Album 1", "Title");
		when(ao1.getUrl()).thenReturn("url1");
		ILocalAudioObject ao2 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist", "Album 2", "Title");
		when(ao2.getUrl()).thenReturn("url2");
		IAlbum album1 = RepositoryTestMockUtils.createMockAlbum("Album 1", ao1);
		IAlbum album2 = RepositoryTestMockUtils.createMockAlbum("Album 2", ao2);

		this.sut.addAlbum(album1);
		assertTrue(this.sut.containsAlbum(album1));
		assertTrue(this.sut.containsAlbum(album1.getName()));
		assertFalse(this.sut.containsAlbum(album2));
		assertFalse(this.sut.containsAlbum(album2.getName()));
		assertEquals(1, this.sut.getFavoriteAlbums().size());
		this.sut.removeAlbum(album1);
		this.sut.addAlbum(album1);
		this.sut.removeAlbum(album1.getName());
		this.sut.removeAlbum(album2);
		assertFalse(this.sut.containsAlbum(album1));
		assertFalse(this.sut.containsAlbum(album2));
		assertTrue(this.sut.getFavoriteAlbums().isEmpty());
	}

	@Test
	public void testArtists() {
		ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title");
		when(ao1.getUrl()).thenReturn("url1");
		ILocalAudioObject ao2 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 2", "Album 2",
						"Title");
		when(ao2.getUrl()).thenReturn("url2");
		IAlbum album1 = RepositoryTestMockUtils.createMockAlbum("Album 1", ao1);
		IAlbum album2 = RepositoryTestMockUtils.createMockAlbum("Album 2", ao2);
		IArtist artist1 = RepositoryTestMockUtils.createMockArtist("Artist 1",
				album1);
		IArtist artist2 = RepositoryTestMockUtils.createMockArtist("Artist 2",
				album2);

		this.sut.addArtist(artist1);
		assertTrue(this.sut.containsArtist(artist1));
		assertTrue(this.sut.containsArtist(artist1.getName()));
		assertFalse(this.sut.containsArtist(artist2));
		assertFalse(this.sut.containsArtist(artist2.getName()));
		assertEquals(1, this.sut.getFavoriteArtists().size());
		this.sut.removeArtist(artist1);
		this.sut.addArtist(artist1);
		this.sut.removeArtist(artist1.getName());
		this.sut.removeArtist(artist2);
		assertFalse(this.sut.containsArtist(artist1));
		assertFalse(this.sut.containsArtist(artist2));
		assertTrue(this.sut.getFavoriteArtists().isEmpty());
	}

	@Test
	public void testGetAllFavoriteSongs() {
		ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title");
		when(ao1.getUrl()).thenReturn("url1");
		ILocalAudioObject ao2 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 2", "Album 2",
						"Title");
		ILocalAudioObject ao3 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 3", "Album 4",
						"Title");
		when(ao2.getUrl()).thenReturn("url2");
		IAlbum album1 = RepositoryTestMockUtils.createMockAlbum("Album 1", ao1);
		IAlbum album2 = RepositoryTestMockUtils.createMockAlbum("Album 2", ao2);
		IArtist artist1 = RepositoryTestMockUtils.createMockArtist("Artist 1",
				album1);

		this.sut.addArtist(artist1);
		this.sut.addAlbum(album2);
		this.sut.addSong(ao3);

		List<ILocalAudioObject> list = this.sut.getAllFavoriteSongs();
		assertEquals(3, list.size());
		assertTrue(list.contains(ao1));
		assertTrue(list.contains(ao2));
		assertTrue(list.contains(ao3));
	}

}
