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

public class FavoritesArtistsManagerTest {

	private FavoritesArtistsManager sut;

	private IRepositoryHandler repositoryHandler;

	@Before
	public void init() {
		this.sut = new FavoritesArtistsManager();
		this.repositoryHandler = mock(IRepositoryHandler.class);
		this.sut.setRepositoryHandler(this.repositoryHandler);
	}

	@Test
	public void testEmptyList() {
		IFavorites favorites = mock(IFavorites.class);
		assertFalse(this.sut.toggleFavoriteArtists(favorites, null));
		assertFalse(this.sut.toggleFavoriteArtists(favorites,
				new ArrayList<ILocalAudioObject>()));

		verify(favorites, never()).addArtist(any(IArtist.class));
		verify(favorites, never()).removeArtist(any(IArtist.class));
	}

	@Test
	public void testRemoveArtist() {
		IFavorites favorites = mock(IFavorites.class);

		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();

		final ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");

		IAlbum album1 = RepositoryTestMockUtils.createMockAlbum("Album 1", ao1);
		IArtist artist1 = RepositoryTestMockUtils.createMockArtist("Artist 1",
				album1);

		when(this.repositoryHandler.getArtist("Artist 1")).thenReturn(artist1);
		when(favorites.containsArtist(artist1)).thenReturn(true);

		CollectionUtils.fillCollectionWithElements(list, ao1);

		assertTrue(this.sut.toggleFavoriteArtists(favorites, list));

		verify(favorites, never()).addArtist(any(IArtist.class));
		verify(favorites, times(1)).removeArtist(artist1);
	}

	@Test
	public void testCallWithTwoFilesSameArtist() {
		IFavorites favorites = mock(IFavorites.class);

		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();

		final ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");

		final ILocalAudioObject ao2 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 2",
						"Title 2");

		IAlbum album1 = RepositoryTestMockUtils.createMockAlbum("Album 1", ao1);
		IAlbum album2 = RepositoryTestMockUtils.createMockAlbum("Album 2", ao2);
		IArtist artist1 = RepositoryTestMockUtils.createMockArtist("Artist 1",
				album1, album2);

		when(this.repositoryHandler.getArtist("Artist 1")).thenReturn(artist1);

		CollectionUtils.fillCollectionWithElements(list, ao1, ao2);

		assertTrue(this.sut.toggleFavoriteArtists(favorites, list));

		verify(favorites, times(1)).addArtist(artist1);
		verify(favorites, never()).removeArtist(any(IArtist.class));
	}

	@Test
	public void testCheckFavoriteArtists() {
		IArtist artist1 = RepositoryTestMockUtils.createMockArtist("Artist 1");
		IArtist artist2 = RepositoryTestMockUtils.createMockArtist("Artist 2");
		IFavorites favorites = mock(IFavorites.class);
		when(favorites.getFavoriteArtists()).thenReturn(
				CollectionUtils.fillCollectionWithElements(
						new ArrayList<IArtist>(), artist1, artist2));

		when(this.repositoryHandler.existsArtist(artist1)).thenReturn(true);
		when(this.repositoryHandler.existsArtist(artist2)).thenReturn(false);

		assertTrue(this.sut.checkFavoriteArtists(favorites));

		verify(favorites, never()).removeArtist(artist1);
		verify(favorites, times(1)).removeArtist(artist2);
	}
}
