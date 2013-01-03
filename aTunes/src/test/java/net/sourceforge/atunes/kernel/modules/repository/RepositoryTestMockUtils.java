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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Utility methods to create mocks for repository testing
 * 
 * @author alex
 * 
 */
public class RepositoryTestMockUtils {

	/**
	 * Creates a mock local audio object
	 * 
	 * @param albumArtist
	 * @param artist
	 * @param album
	 * @param title
	 * @return
	 */
	public static ILocalAudioObject createMockLocalAudioObject(
			final String albumArtist, final String artist, final String album,
			final String title) {
		ILocalAudioObject ao = mock(ILocalAudioObject.class);
		when(ao.getArtist(any(IUnknownObjectChecker.class))).thenReturn(artist);
		when(ao.getAlbum(any(IUnknownObjectChecker.class))).thenReturn(album);
		when(ao.getAlbumArtist(any(IUnknownObjectChecker.class))).thenReturn(
				albumArtist);
		when(ao.getTitle()).thenReturn(title);
		return ao;
	}

	/**
	 * Creates a mock local audio object with random data
	 * 
	 * @return
	 */
	public static ILocalAudioObject createRandomMockLocalAudioObject() {
		return createMockLocalAudioObject(UUID.randomUUID().toString(), UUID
				.randomUUID().toString(), UUID.randomUUID().toString(), UUID
				.randomUUID().toString());
	}

	/**
	 * Creates a mock artist
	 * 
	 * @param name
	 * @param albums
	 * @return
	 */
	public static IArtist createMockArtist(final String name,
			final IAlbum... albums) {
		IArtist artist = mock(IArtist.class);
		when(artist.getName()).thenReturn(name);
		for (IAlbum album : albums) {
			when(artist.getAlbum(album.getName())).thenReturn(album);
		}
		when(artist.getAudioObjects()).then(new Answer<List<IAudioObject>>() {
			@Override
			public List<IAudioObject> answer(final InvocationOnMock invocation)
					throws Throwable {
				List<IAudioObject> list = new ArrayList<IAudioObject>();
				for (IAlbum album : albums) {
					list.addAll(album.getAudioObjects());
				}
				return list;
			}
		});
		when(artist.size()).then(new Answer<Integer>() {
			@Override
			public Integer answer(final InvocationOnMock invocation)
					throws Throwable {
				return ((IArtist) invocation.getMock()).getAudioObjects()
						.size();
			}
		});

		return artist;
	}

	/**
	 * Creates a mock album
	 * 
	 * @param name
	 * @param aos
	 * @return
	 */
	public static IAlbum createMockAlbum(final String name,
			final ILocalAudioObject... aos) {
		return new MockAlbum(name, aos);
	}

	/**
	 * A mock album
	 * 
	 * @author alex
	 * 
	 */
	private static class MockAlbum implements IAlbum {

		/**
	 * 
	 */
		private static final long serialVersionUID = -4099551956188163038L;

		private final String name;

		private final List<ILocalAudioObject> aos = new ArrayList<ILocalAudioObject>();

		MockAlbum(final String name, final ILocalAudioObject... aos) {
			this.name = name;
			for (ILocalAudioObject ao : aos) {
				this.aos.add(ao);
			}
		}

		@Override
		public List<ILocalAudioObject> getAudioObjects() {
			return aos;
		}

		@Override
		public int size() {
			return aos.size();
		}

		@Override
		public int compareTo(final IAlbum o) {
			return 0;
		}

		@Override
		public void addAudioFile(final ILocalAudioObject file) {
			this.aos.add(file);
		}

		@Override
		public IArtist getArtist() {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void removeAudioFile(final ILocalAudioObject file) {
			this.aos.remove(file);
		}

		@Override
		public boolean isEmpty() {
			return this.aos.isEmpty();
		}
	}
}
