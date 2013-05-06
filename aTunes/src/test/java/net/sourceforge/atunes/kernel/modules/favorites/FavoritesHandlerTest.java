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

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryTestMockUtils;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.utils.CollectionUtils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

public class FavoritesHandlerTest {

	private final IStateService stateService = mock(IStateService.class);

	private FavoritesHandler sut;

	private IBeanFactory beanFactory;

	private FavoritesAlbumsManager albumsManager;

	private FavoritesArtistsManager artistsManager;

	private FavoritesSongsManager songsManager;

	private IRepositoryHandler repositoryHandler;

	@Before
	public void init() {
		this.sut = new FavoritesHandler();
		this.sut.setStateService(this.stateService);
		this.beanFactory = mock(IBeanFactory.class);
		this.sut.setBeanFactory(this.beanFactory);
		this.albumsManager = mock(FavoritesAlbumsManager.class);
		this.artistsManager = mock(FavoritesArtistsManager.class);
		this.songsManager = mock(FavoritesSongsManager.class);
		when(this.beanFactory.getBean(FavoritesAlbumsManager.class))
				.thenReturn(this.albumsManager);
		when(this.beanFactory.getBean(FavoritesArtistsManager.class))
				.thenReturn(this.artistsManager);
		when(this.beanFactory.getBean(FavoritesSongsManager.class)).thenReturn(
				this.songsManager);
		this.repositoryHandler = mock(IRepositoryHandler.class);
		this.sut.setRepositoryHandler(this.repositoryHandler);
	}

	@Test
	public void testInitialization() {
		// Verify
		assertTrue(this.sut.getFavoriteAlbums().isEmpty());
		assertTrue(this.sut.getFavoriteArtists().isEmpty());
		assertTrue(this.sut.getFavoriteSongs().isEmpty());
		assertTrue(this.sut.getAllFavoriteSongs().isEmpty());
	}

	@Test
	public void testAudioFilesRemoved() {
		final ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");

		IAlbum album1 = RepositoryTestMockUtils.createMockAlbum("Album 1", ao1);
		IArtist artist1 = RepositoryTestMockUtils.createMockArtist("Artist 1",
				album1);

		when(this.repositoryHandler.getArtist("Artist 1")).thenReturn(artist1);

		this.sut.audioFilesRemoved(Collections.singletonList(ao1));

		verify(this.songsManager, times(1)).removeSongs(any(IFavorites.class),
				Matchers.argThat(new BaseMatcher<List<ILocalAudioObject>>() {

					@Override
					public boolean matches(final Object obj) {
						@SuppressWarnings("unchecked")
						List<ILocalAudioObject> list = (List<ILocalAudioObject>) obj;
						return list.size() == 1 && list.get(0).equals(ao1);
					}

					@Override
					public void describeTo(final Description arg0) {
					}
				}), Matchers.eq(false));
		verify(this.albumsManager, never()).removeAlbum(any(IFavorites.class),
				Matchers.eq("Album 1"));
		verify(this.artistsManager, never()).removeArtist(
				any(IFavorites.class), Matchers.eq("Artist 1"));
	}

	@Test
	public void testAudioFilesRemovedAlbumRemoved() {
		final ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");

		IArtist artist1 = RepositoryTestMockUtils.createMockArtist("Artist 1");

		when(this.repositoryHandler.getArtist("Artist 1")).thenReturn(artist1);

		this.sut.audioFilesRemoved(Collections.singletonList(ao1));

		verify(this.songsManager, times(1)).removeSongs(any(IFavorites.class),
				Matchers.argThat(new BaseMatcher<List<ILocalAudioObject>>() {

					@Override
					public boolean matches(final Object obj) {
						@SuppressWarnings("unchecked")
						List<ILocalAudioObject> list = (List<ILocalAudioObject>) obj;
						return list.size() == 1 && list.get(0).equals(ao1);
					}

					@Override
					public void describeTo(final Description arg0) {
					}
				}), Matchers.eq(false));
		verify(this.albumsManager, times(1)).removeAlbum(any(IFavorites.class),
				Matchers.eq("Album 1"));
		verify(this.artistsManager, never()).removeArtist(
				any(IFavorites.class), Matchers.eq("Artist 1"));
	}

	@Test
	public void testAudioFilesRemovedArtistAndAlbumRemoved() {
		final ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");

		this.sut.audioFilesRemoved(Collections.singletonList(ao1));

		verify(this.songsManager, times(1)).removeSongs(any(IFavorites.class),
				Matchers.argThat(new BaseMatcher<List<ILocalAudioObject>>() {

					@Override
					public boolean matches(final Object obj) {
						@SuppressWarnings("unchecked")
						List<ILocalAudioObject> list = (List<ILocalAudioObject>) obj;
						return list.size() == 1 && list.get(0).equals(ao1);
					}

					@Override
					public void describeTo(final Description arg0) {
					}
				}), Matchers.eq(false));
		verify(this.albumsManager, times(1)).removeAlbum(any(IFavorites.class),
				Matchers.eq("Album 1"));
		verify(this.artistsManager, times(1)).removeArtist(
				any(IFavorites.class), Matchers.eq("Artist 1"));
	}

	@Test
	public void testCheckFavorites() {
		ITrackInfo track1 = mock(ITrackInfo.class);
		when(track1.getArtist()).thenReturn("Artist");
		when(track1.getTitle()).thenReturn("Title");
		ITrackInfo track2 = mock(ITrackInfo.class);
		when(track2.getArtist()).thenReturn("Artist 2");
		when(track2.getTitle()).thenReturn("Title");
		when(
				this.songsManager.isSongFavorite(
						Matchers.any(IFavorites.class), Matchers.eq("Artist"),
						Matchers.eq("Title"))).thenReturn(true);
		when(
				this.songsManager.isSongFavorite(
						Matchers.any(IFavorites.class),
						Matchers.eq("Artist 2"), Matchers.eq("Title")))
				.thenReturn(false);
		this.sut.checkFavorites(CollectionUtils.fillCollectionWithElements(
				new ArrayList<ITrackInfo>(), track1, track2));
		verify(track1, times(1)).setFavorite(true);
		verify(track2, never()).setFavorite(Matchers.anyBoolean());

	}
}
