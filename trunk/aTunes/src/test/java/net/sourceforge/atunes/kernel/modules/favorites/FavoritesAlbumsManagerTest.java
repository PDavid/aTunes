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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryTestMockUtils;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.CollectionUtils;

import org.junit.Before;
import org.junit.Test;

public class FavoritesAlbumsManagerTest {

	private FavoritesAlbumsManager sut;

	private IRepositoryHandler repositoryHandler;

	@Before
	public void init() {
		this.sut = new FavoritesAlbumsManager();
		this.repositoryHandler = mock(IRepositoryHandler.class);
		this.sut.setRepositoryHandler(this.repositoryHandler);
	}

	@Test
	public void testEmptyList() {
		IFavorites favorites = mock(IFavorites.class);
		assertFalse(this.sut.toggleFavoriteAlbums(favorites, null));
		assertFalse(this.sut.toggleFavoriteAlbums(favorites,
				new ArrayList<ILocalAudioObject>()));

		verify(favorites, never()).addAlbum(any(IAlbum.class));
		verify(favorites, never()).removeAlbum(any(IAlbum.class));
	}

	@Test
	public void testRemoveAlbum() {
		IFavorites favorites = mock(IFavorites.class);

		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();

		final ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");

		IAlbum album1 = RepositoryTestMockUtils.createMockAlbum("Album 1", ao1);
		IArtist artist1 = RepositoryTestMockUtils.createMockArtist("Artist 1",
				album1);

		when(this.repositoryHandler.getArtist("Artist 1")).thenReturn(artist1);
		when(favorites.containsAlbum(album1)).thenReturn(true);

		CollectionUtils.fillCollectionWithElements(list, ao1);

		assertTrue(this.sut.toggleFavoriteAlbums(favorites, list));

		verify(favorites, never()).addAlbum(any(IAlbum.class));
		verify(favorites, times(1)).removeAlbum(album1);
	}

	@Test
	public void testCallWithTwoFilesSameAlbum() {
		IFavorites favorites = mock(IFavorites.class);

		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();

		final ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");

		final ILocalAudioObject ao2 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 2");

		IAlbum album1 = RepositoryTestMockUtils.createMockAlbum("Album 1", ao1,
				ao2);
		IArtist artist1 = RepositoryTestMockUtils.createMockArtist("Artist 1",
				album1);

		when(this.repositoryHandler.getArtist("Artist 1")).thenReturn(artist1);

		CollectionUtils.fillCollectionWithElements(list, ao1, ao2);

		assertTrue(this.sut.toggleFavoriteAlbums(favorites, list));

		verify(favorites, times(1)).addAlbum(album1);
		verify(favorites, never()).removeAlbum(any(IAlbum.class));
	}

	@Test
	public void testCheckFavoriteAlbums() {
		IAlbum album1 = RepositoryTestMockUtils.createMockAlbum("Album 1");
		IAlbum album2 = RepositoryTestMockUtils.createMockAlbum("Album 2");
		IFavorites favorites = mock(IFavorites.class);
		when(favorites.getFavoriteAlbums()).thenReturn(
				CollectionUtils.fillCollectionWithElements(
						new ArrayList<IAlbum>(), album1, album2));

		when(this.repositoryHandler.existsAlbum(album1)).thenReturn(false);
		when(this.repositoryHandler.existsAlbum(album2)).thenReturn(true);

		assertTrue(this.sut.checkFavoriteAlbums(favorites));

		verify(favorites, times(1)).removeAlbum(album1);
		verify(favorites, never()).removeAlbum(album2);
	}
}
