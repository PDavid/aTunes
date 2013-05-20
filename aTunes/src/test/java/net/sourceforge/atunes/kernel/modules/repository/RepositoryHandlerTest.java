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

package net.sourceforge.atunes.kernel.modules.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.ITrackInfo;

import org.junit.Before;
import org.junit.Test;

public class RepositoryHandlerTest {

	private RepositoryHandler sut;

	@Before
	public void init() {
		this.sut = new RepositoryHandler();
		this.sut.setNavigationHandler(mock(INavigationHandler.class));

		// Repository
		IRepository repository = mock(IRepository.class);

		// Artist 1 with songs
		IArtist artist1 = mock(IArtist.class);
		ILocalAudioObject ao1 = mock(ILocalAudioObject.class);
		when(ao1.getTitle()).thenReturn("ao1");
		ILocalAudioObject ao2 = mock(ILocalAudioObject.class);
		when(ao2.getTitle()).thenReturn("ao2");
		ILocalAudioObject ao3 = mock(ILocalAudioObject.class);
		when(ao3.getTitle()).thenReturn("ao3");
		List<ILocalAudioObject> aos1 = new ArrayList<ILocalAudioObject>();
		aos1.add(ao1);
		aos1.add(ao2);
		aos1.add(ao3);
		when(artist1.getAudioObjects()).thenReturn(aos1);

		// Artist 2 with songs
		IArtist artist2 = mock(IArtist.class);
		ILocalAudioObject ao12 = mock(ILocalAudioObject.class);
		when(ao12.getTitle()).thenReturn("ao12");
		ILocalAudioObject ao22 = mock(ILocalAudioObject.class);
		when(ao22.getTitle()).thenReturn("ao22");
		ILocalAudioObject ao32 = mock(ILocalAudioObject.class);
		when(ao32.getTitle()).thenReturn("ao3");
		List<ILocalAudioObject> aos2 = new ArrayList<ILocalAudioObject>();
		aos2.add(ao12);
		aos2.add(ao22);
		aos2.add(ao32);
		when(artist2.getAudioObjects()).thenReturn(aos2);

		when(repository.getArtist("Artist 1")).thenReturn(artist1);
		when(repository.getArtist("Artist 2")).thenReturn(artist2);

		this.sut.setRepository(repository);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetAudioObjectsByTitle1() {
		this.sut.getAudioObjectsByTitle(null, new ArrayList<String>());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetAudioObjectsByTitle2() {
		this.sut.getAudioObjectsByTitle("", new ArrayList<String>());
	}

	@Test
	public void testGetAudioObjectsByTitle3() {
		assertTrue(this.sut.getAudioObjectsByTitle("Artist", null).isEmpty());
	}

	@Test
	public void testGetAudioObjectsByTitle4() {
		// non existent artist
		assertTrue(this.sut.getAudioObjectsByTitle("Artist", null).isEmpty());
	}

	@Test
	public void testGetAudioObjectsByTitle5() {
		List<String> titles = new ArrayList<String>();
		titles.add("AO1");
		titles.add("AO2");
		titles.add("AOS1");

		// Match of artist
		List<ILocalAudioObject> result = this.sut.getAudioObjectsByTitle(
				"Artist 1", titles);
		assertEquals(2, result.size());
	}

	@Test
	public void testGetAudioObjectsByTitle6() {
		List<String> titles = new ArrayList<String>();
		titles.add("AO1");
		titles.add("AO2");
		titles.add("AOS1");

		// Match of artist
		List<ILocalAudioObject> result = this.sut.getAudioObjectsByTitle(
				"Artist 2", titles);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testCheckAvailability() {
		ITrackInfo t1 = mock(ITrackInfo.class);
		when(t1.getTitle()).thenReturn("AO1");
		ITrackInfo t2 = mock(ITrackInfo.class);
		when(t2.getTitle()).thenReturn("UNKNOWN");

		List<ITrackInfo> tracks = new ArrayList<ITrackInfo>();
		tracks.add(t1);
		tracks.add(t2);

		this.sut.checkAvailability("Artist 1", tracks);

		verify(t1).setAvailable(true);
		verify(t2).setAvailable(false);
	}
}
