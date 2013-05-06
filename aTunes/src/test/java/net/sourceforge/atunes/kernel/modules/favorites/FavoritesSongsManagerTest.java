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
import net.sourceforge.atunes.kernel.modules.webservices.AddLovedSongBackgroundWorker;
import net.sourceforge.atunes.kernel.modules.webservices.RemoveLovedSongBackgroundWorker;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.utils.CollectionUtils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

public class FavoritesSongsManagerTest {

	private FavoritesSongsManager sut;

	private IStateContext stateContext;

	private AddLovedSongBackgroundWorker addWorker;

	private RemoveLovedSongBackgroundWorker removeWorker;

	private IRepositoryHandler repositoryHandler;

	@Before
	public void init() {
		this.sut = new FavoritesSongsManager();
		this.stateContext = mock(IStateContext.class);
		this.sut.setStateContext(this.stateContext);
		IBeanFactory beanFactory = mock(IBeanFactory.class);
		this.addWorker = mock(AddLovedSongBackgroundWorker.class);
		this.removeWorker = mock(RemoveLovedSongBackgroundWorker.class);
		when(beanFactory.getBean(AddLovedSongBackgroundWorker.class))
				.thenReturn(this.addWorker);
		when(beanFactory.getBean(RemoveLovedSongBackgroundWorker.class))
				.thenReturn(this.removeWorker);
		this.sut.setBeanFactory(beanFactory);
		this.repositoryHandler = mock(IRepositoryHandler.class);
		this.sut.setRepositoryHandler(this.repositoryHandler);
	}

	@Test
	public void testEmptyList() {
		IFavorites favorites = mock(IFavorites.class);
		assertFalse(this.sut.toggleFavoriteSongs(favorites, null));
		assertFalse(this.sut.toggleFavoriteSongs(favorites,
				new ArrayList<ILocalAudioObject>()));

		verify(favorites, never()).addSong(any(ILocalAudioObject.class));
		verify(favorites, never()).removeSong(any(ILocalAudioObject.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddSong() {
		IFavorites favorites = mock(IFavorites.class);

		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();

		final ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");

		when(favorites.containsSong(ao1)).thenReturn(false);
		CollectionUtils.fillCollectionWithElements(list, ao1);

		when(this.stateContext.isLastFmEnabled()).thenReturn(false);
		when(this.stateContext.isAutoLoveFavoriteSong()).thenReturn(false);

		assertTrue(this.sut.toggleFavoriteSongs(favorites, list));

		verify(favorites, times(1)).addSong(ao1);
		verify(favorites, never()).removeSong(any(ILocalAudioObject.class));

		verify(this.addWorker, never()).add(Matchers.anyCollection());
		verify(this.removeWorker, never()).remove(Matchers.anyCollection());
	}

	@Test
	public void testRemoveSong() {
		IFavorites favorites = mock(IFavorites.class);

		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();

		final ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");

		when(favorites.containsSong(ao1)).thenReturn(true);
		CollectionUtils.fillCollectionWithElements(list, ao1);

		when(this.stateContext.isLastFmEnabled()).thenReturn(false);
		when(this.stateContext.isAutoLoveFavoriteSong()).thenReturn(false);

		assertTrue(this.sut.toggleFavoriteSongs(favorites, list));

		verify(favorites, never()).addSong(any(ILocalAudioObject.class));
		verify(favorites, times(1)).removeSong(ao1);

		verify(this.addWorker, never()).add(Matchers.anyCollection());
		verify(this.removeWorker, never()).remove(Matchers.anyCollection());
	}

	@Test
	public void testAddSongCallLastFM() {
		IFavorites favorites = mock(IFavorites.class);

		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();

		final ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");

		when(favorites.containsSong(ao1)).thenReturn(false);
		CollectionUtils.fillCollectionWithElements(list, ao1);

		when(this.stateContext.isLastFmEnabled()).thenReturn(true);
		when(this.stateContext.isAutoLoveFavoriteSong()).thenReturn(true);

		assertTrue(this.sut.toggleFavoriteSongs(favorites, list));

		verify(favorites, times(1)).addSong(ao1);
		verify(favorites, never()).removeSong(any(ILocalAudioObject.class));

		verify(this.addWorker, times(1)).add(
				Matchers.argThat(new BaseMatcher<List<IAudioObject>>() {

					@Override
					public boolean matches(final Object arg) {
						List<IAudioObject> list = (List<IAudioObject>) arg;
						return list.size() == 1 && list.get(0).equals(ao1);
					}

					@Override
					public void describeTo(final Description arg0) {
					}

				}));
		verify(this.removeWorker, never()).remove(Matchers.anyCollection());
	}

	@Test
	public void testRemoveSongCallLastFM() {
		IFavorites favorites = mock(IFavorites.class);

		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();

		final ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");

		when(favorites.containsSong(ao1)).thenReturn(true);
		CollectionUtils.fillCollectionWithElements(list, ao1);

		when(this.stateContext.isLastFmEnabled()).thenReturn(true);
		when(this.stateContext.isAutoLoveFavoriteSong()).thenReturn(true);

		assertTrue(this.sut.toggleFavoriteSongs(favorites, list));

		verify(favorites, never()).addSong(any(ILocalAudioObject.class));
		verify(favorites, times(1)).removeSong(ao1);

		verify(this.addWorker, never()).add(Matchers.anyCollection());
		verify(this.removeWorker, times(1)).remove(
				Matchers.argThat(new BaseMatcher<List<IAudioObject>>() {

					@Override
					public boolean matches(final Object arg) {
						List<IAudioObject> list = (List<IAudioObject>) arg;
						return list.size() == 1 && list.get(0).equals(ao1);
					}

					@Override
					public void describeTo(final Description arg0) {
					}

				}));
	}

	@Test
	public void testCheckFavoriteSongs() {
		ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");
		ILocalAudioObject ao2 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 2");
		IFavorites favorites = mock(IFavorites.class);
		when(favorites.getFavoriteSongs()).thenReturn(
				CollectionUtils.fillCollectionWithElements(
						new ArrayList<ILocalAudioObject>(), ao1, ao2));

		when(this.repositoryHandler.existsFile(ao1)).thenReturn(true);
		when(this.repositoryHandler.existsFile(ao2)).thenReturn(false);

		assertTrue(this.sut.checkFavoriteSongs(favorites));

		verify(favorites, never()).removeSong(ao1);
		verify(favorites, times(1)).removeSong(ao2);
	}

	@Test
	public void testIsSongFavorite() {
		ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");
		ILocalAudioObject ao2 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 2", "Album 1",
						"Title 2");
		ILocalAudioObject ao3 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 2", "Album 1", null); // Null
																				// title
		IFavorites favorites = mock(IFavorites.class);
		when(favorites.getFavoriteSongs()).thenReturn(
				CollectionUtils.fillCollectionWithElements(
						new ArrayList<ILocalAudioObject>(), ao1, ao2, ao3));

		assertTrue(this.sut.isSongFavorite(favorites, "Artist 1", "Title 1"));
		assertTrue(this.sut.isSongFavorite(favorites, "ARTIST 2", "Title 2"));
		assertFalse(this.sut.isSongFavorite(favorites, "Artist 1", "Title 2"));
	}
}
