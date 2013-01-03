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

package net.sourceforge.atunes.model;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.LocalAudioObjectFilter;

import org.junit.Assert;
import org.junit.Test;

public class LocalAudioObjectFilterTest {

	@Test
	public void testEmpty() {
		ILocalAudioObjectFilter sut = new LocalAudioObjectFilter();
		Assert.assertTrue(sut.getLocalAudioObjects(null).isEmpty());
		Assert.assertTrue(sut.getLocalAudioObjects(new ArrayList<IAudioObject>()).isEmpty());
	}

	@Test
	public void testNotEmpty() {
		ILocalAudioObjectFilter sut = new LocalAudioObjectFilter();
		List<IAudioObject> list = new ArrayList<IAudioObject>();
		ILocalAudioObject lao1 = mock(ILocalAudioObject.class);
		ILocalAudioObject lao2 = mock(ILocalAudioObject.class);
		list.add(lao1);
		list.add(mock(IAudioObject.class));
		list.add(mock(IRadio.class));
		list.add(mock(IPodcastFeedEntry.class));
		list.add(lao2);
		Assert.assertEquals(2, sut.getLocalAudioObjects(list).size());
		Assert.assertEquals(lao1, sut.getLocalAudioObjects(list).get(0));
		Assert.assertEquals(lao2, sut.getLocalAudioObjects(list).get(1));
	}

	@Test
	public void testFilterRepeatedObjects() {
		LocalAudioObjectFilter sut = new LocalAudioObjectFilter();
		IUnknownObjectChecker unknownObjectChecker = mock(IUnknownObjectChecker.class);
		sut.setUnknownObjectChecker(unknownObjectChecker);
		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();
		ILocalAudioObject lao1 = mock(ILocalAudioObject.class);
		ILocalAudioObject lao2 = mock(ILocalAudioObject.class);
		ILocalAudioObject lao3 = mock(ILocalAudioObject.class);
		ILocalAudioObject lao4 = mock(ILocalAudioObject.class);
		list.add(lao1);
		list.add(lao2);
		list.add(lao3);
		list.add(lao4);
		when(lao1.getAlbumArtist(unknownObjectChecker)).thenReturn("artist1");
		when(lao2.getAlbumArtist(unknownObjectChecker)).thenReturn("artist1");
		when(lao3.getArtist(unknownObjectChecker)).thenReturn("artist1");
		when(lao4.getArtist(unknownObjectChecker)).thenReturn("artist2");
		when(lao1.getTitle()).thenReturn("title1");
		when(lao2.getTitle()).thenReturn("title2");
		when(lao3.getTitle()).thenReturn("title2");
		when(lao4.getTitle()).thenReturn("title1");

		List<ILocalAudioObject> result = sut.filterRepeatedObjects(list);
		Assert.assertTrue(result.contains(lao1));
		Assert.assertTrue(result.contains(lao2));
		Assert.assertFalse(result.contains(lao3));
		Assert.assertTrue(result.contains(lao4));
	}

	@Test
	public void testFilterRepeatedObjectsWithAlbums() {
		LocalAudioObjectFilter sut = new LocalAudioObjectFilter();
		IUnknownObjectChecker unknownObjectChecker = mock(IUnknownObjectChecker.class);
		sut.setUnknownObjectChecker(unknownObjectChecker);
		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();
		ILocalAudioObject lao1 = mock(ILocalAudioObject.class);
		ILocalAudioObject lao2 = mock(ILocalAudioObject.class);
		ILocalAudioObject lao3 = mock(ILocalAudioObject.class);
		ILocalAudioObject lao4 = mock(ILocalAudioObject.class);
		list.add(lao1);
		list.add(lao2);
		list.add(lao3);
		list.add(lao4);
		when(lao1.getAlbumArtist(unknownObjectChecker)).thenReturn("artist1");
		when(lao2.getAlbumArtist(unknownObjectChecker)).thenReturn("artist1");
		when(lao3.getArtist(unknownObjectChecker)).thenReturn("artist1");
		when(lao4.getArtist(unknownObjectChecker)).thenReturn("artist1");
		when(lao1.getTitle()).thenReturn("title1");
		when(lao2.getTitle()).thenReturn("title2");
		when(lao3.getTitle()).thenReturn("title2");
		when(lao4.getTitle()).thenReturn("title1");
		when(lao1.getAlbum(unknownObjectChecker)).thenReturn("album1");
		when(lao2.getAlbum(unknownObjectChecker)).thenReturn("album2");
		when(lao3.getAlbum(unknownObjectChecker)).thenReturn("album2");
		when(lao4.getAlbum(unknownObjectChecker)).thenReturn("album2");

		List<ILocalAudioObject> result = sut.filterRepeatedObjectsWithAlbums(list);
		Assert.assertTrue(result.contains(lao1));
		Assert.assertTrue(result.contains(lao2));
		Assert.assertFalse(result.contains(lao3));
		Assert.assertTrue(result.contains(lao4));
	}

}
