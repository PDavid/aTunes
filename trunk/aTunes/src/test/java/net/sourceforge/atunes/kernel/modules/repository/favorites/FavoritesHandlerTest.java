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

package net.sourceforge.atunes.kernel.modules.repository.favorites;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sourceforge.atunes.kernel.modules.favorites.FavoritesHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.IStateService;

import org.junit.Before;
import org.junit.Test;

public class FavoritesHandlerTest {

	private final IStateService stateService = mock(IStateService.class);

	private FavoritesHandler sut;

	@Before
	public void init() {
		this.sut = new FavoritesHandler();
		this.sut.setStateContext(mock(IStateContext.class));
		this.sut.setStateService(this.stateService);
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
	public void testToggleFavoriteSongs() {
		// Prepare
		ILocalAudioObject ao1 = mock(ILocalAudioObject.class);
		when(ao1.getUrl()).thenReturn(UUID.randomUUID().toString());
		ILocalAudioObject ao2 = mock(ILocalAudioObject.class);
		when(ao2.getUrl()).thenReturn(UUID.randomUUID().toString());

		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();
		list.add(ao1);
		list.add(ao2);
		list.add(ao2); // Added two times

		// Act
		this.sut.toggleFavoriteSongs(list);

		// Verify
		assertTrue(this.sut.getFavoriteSongs().contains(ao1));
		assertTrue(this.sut.getFavoriteSongs().contains(ao2));
	}

	@Test
	public void testToggleFavoriteSongsTwice() {
		// Prepare
		ILocalAudioObject ao1 = mock(ILocalAudioObject.class);
		when(ao1.getUrl()).thenReturn(UUID.randomUUID().toString());

		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();
		list.add(ao1);

		// Act
		this.sut.toggleFavoriteSongs(list);
		this.sut.toggleFavoriteSongs(list); // Toggle twice -> added and removed
											// from favorite

		// Verify
		assertFalse(this.sut.getFavoriteSongs().contains(ao1));
	}

	@Test
	public void testAddFavoriteSongs() {
		// Prepare
		ILocalAudioObject ao1 = mock(ILocalAudioObject.class);
		when(ao1.getUrl()).thenReturn(UUID.randomUUID().toString());
		ILocalAudioObject ao2 = mock(ILocalAudioObject.class);
		when(ao2.getUrl()).thenReturn(UUID.randomUUID().toString());

		List<ILocalAudioObject> list1 = new ArrayList<ILocalAudioObject>();
		list1.add(ao1);

		List<ILocalAudioObject> list2 = new ArrayList<ILocalAudioObject>();
		list1.add(ao1);
		list1.add(ao2);

		// Act
		this.sut.toggleFavoriteSongs(list1); // With this call ao1 is favorite
		this.sut.addFavoriteSongs(list2, false); // Now ao1 is added again

		// Verify
		assertTrue(this.sut.getFavoriteSongs().contains(ao1));
		assertTrue(this.sut.getFavoriteSongs().contains(ao2));
	}

	@Test
	public void updateFavoritesWhenRemovedFromRepository() {
		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();
		IRepository repository = mock(IRepository.class);
		ILocalAudioObject ao1 = mock(ILocalAudioObject.class);
		when(ao1.getUrl()).thenReturn(UUID.randomUUID().toString());
		ILocalAudioObject ao2 = mock(ILocalAudioObject.class);
		when(ao1.getUrl()).thenReturn(UUID.randomUUID().toString());
		when(repository.getFiles()).thenReturn(list);
		list.add(ao1);
		list.add(ao2);
		this.sut.toggleFavoriteSongs(list);

		// A favorite song is removed from repository
		list.remove(ao2);
		this.sut.updateFavorites(repository);

		// Should be removed from favorites
		assertFalse(this.sut.getFavoriteSongs().contains(ao2));
	}

	// TODO: Add test for toggle favorite artists and albums, as need to
	// refactor all handlers before

}
