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

package net.sourceforge.atunes.kernel.modules.repository.favorites;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.IStateHandler;

import org.junit.Before;
import org.junit.Test;

public class FavoritesHandlerTest {

	private IStateHandler stateHandler = mock(IStateHandler.class);
	private IState state = mock(IState.class);
	
	private FavoritesHandler sut;
	
	@Before
	public void init() {
		sut = new FavoritesHandler();
		sut.setState(state);
		sut.setStateContext(mock(IStateContext.class));
		sut.setStateHandler(stateHandler);
		sut.getPreviousInitializationTask().run();
	}
	
	@Test
	public void testInitialization() {
		// Verify
		assertTrue(sut.getFavoriteAlbumsInfo().isEmpty());
		assertTrue(sut.getFavoriteArtistsInfo().isEmpty());
		assertTrue(sut.getFavoriteSongs().isEmpty());
		assertTrue(sut.getFavoriteSongsInfo().isEmpty());
		assertTrue(sut.getFavoriteSongsMap().isEmpty());
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
		sut.toggleFavoriteSongs(list);
		
		// Verify
		assertTrue(sut.getFavoriteSongs().contains(ao1));
		assertTrue(sut.getFavoriteSongs().contains(ao2));
	}
	
	@Test
	public void testToggleFavoriteSongsTwice() {
		// Prepare
		ILocalAudioObject ao1 = mock(ILocalAudioObject.class);
		when(ao1.getUrl()).thenReturn(UUID.randomUUID().toString());
		
		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();
		list.add(ao1);
		
		// Act
		sut.toggleFavoriteSongs(list);
		sut.toggleFavoriteSongs(list); // Toggle twice -> added and removed from favorite
		
		// Verify
		assertFalse(sut.getFavoriteSongs().contains(ao1));
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
		sut.toggleFavoriteSongs(list1); // With this call ao1 is favorite
		sut.addFavoriteSongs(list2, false); // Now ao1 is added again
		
		// Verify
		assertTrue(sut.getFavoriteSongs().contains(ao1));
		assertTrue(sut.getFavoriteSongs().contains(ao2));
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
		sut.toggleFavoriteSongs(list);
		
		// A favorite song is removed from repository
		list.remove(ao2);
		sut.updateFavorites(repository);
		
		// Should be removed from favorites
		assertFalse(sut.getFavoriteSongs().contains(ao2));
	}
	
	
	// TODO: Add test for toggle favorite artists and albums, as need to refactor all handlers before

}
