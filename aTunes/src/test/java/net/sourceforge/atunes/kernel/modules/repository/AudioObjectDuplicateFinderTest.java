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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AudioObjectDuplicateFinderTest {

	private AudioObjectDuplicateFinder sut;

	private IUnknownObjectChecker unknownObjectChecker;

	@Before
	public void init() {
		sut = new AudioObjectDuplicateFinder();
		unknownObjectChecker = new UnknownObjectChecker();
		sut.setUnknownObjectChecker(unknownObjectChecker);
	}

	@Test
	public void testEmpty() {
		Assert.assertTrue(sut.findDuplicates(null).isEmpty());
		Assert.assertTrue(sut.findDuplicates(new ArrayList<IAudioObject>()).isEmpty());
	}

	@Test
	public void test1() {
		List<IAudioObject> list = new ArrayList<IAudioObject>();
		list.add(mock(ILocalAudioObject.class));
		list.add(mock(ILocalAudioObject.class));
		list.add(mock(IRadio.class));
		list.add(mock(IRadio.class));
		list.add(mock(IPodcastFeedEntry.class));
		list.add(mock(IPodcastFeedEntry.class));
		Assert.assertTrue(sut.findDuplicates(list).isEmpty());
	}

	@Test
	public void testLocalAudioObject() {
		ILocalAudioObject lao1 = mock(ILocalAudioObject.class);
		when(lao1.getTitle()).thenReturn("T1");
		when(lao1.getArtist(unknownObjectChecker)).thenReturn("A1");

		ILocalAudioObject lao2 = mock(ILocalAudioObject.class);
		when(lao2.getTitle()).thenReturn("t1");
		when(lao2.getArtist(unknownObjectChecker)).thenReturn("a1");

		ILocalAudioObject lao3 = mock(ILocalAudioObject.class);
		when(lao3.getTitle()).thenReturn("T2");
		when(lao3.getArtist(unknownObjectChecker)).thenReturn("A1");

		ILocalAudioObject lao4 = mock(ILocalAudioObject.class);
		when(lao4.getTitle()).thenReturn("T1");
		when(lao4.getArtist(unknownObjectChecker)).thenReturn("A1");

		ILocalAudioObject lao5 = mock(ILocalAudioObject.class);
		when(lao5.getTitle()).thenReturn("T1");
		when(lao5.getArtist(unknownObjectChecker)).thenReturn("A5");

		List<IAudioObject> list = new ArrayList<IAudioObject>();
		list.add(lao1);
		list.add(lao2);
		list.add(lao3);
		list.add(lao4);

		List<IAudioObject> duplicates = sut.findDuplicates(list);
		Assert.assertEquals(2, duplicates.size());

		Assert.assertEquals(lao2, duplicates.get(0));
		Assert.assertEquals(lao4, duplicates.get(1));
	}

	@Test
	public void testLocalAudioObjectWithUnknownArtist() {
		ILocalAudioObject lao1 = mock(ILocalAudioObject.class);
		when(lao1.getTitle()).thenReturn("T1");
		when(lao1.getArtist(unknownObjectChecker)).thenReturn(unknownObjectChecker.getUnknownArtist());

		ILocalAudioObject lao2 = mock(ILocalAudioObject.class);
		when(lao2.getTitle()).thenReturn("T1");
		when(lao2.getArtist(unknownObjectChecker)).thenReturn(unknownObjectChecker.getUnknownArtist());

		List<IAudioObject> list = new ArrayList<IAudioObject>();
		list.add(lao1);
		list.add(lao2);

		List<IAudioObject> duplicates = sut.findDuplicates(list);
		Assert.assertTrue(duplicates.isEmpty());
	}

	@Test
	public void testRadio() {
		IRadio r1 = mock(IRadio.class);
		when(r1.getUrl()).thenReturn("R1");

		IRadio r2 = mock(IRadio.class);
		when(r2.getUrl()).thenReturn("R2");

		IRadio r3 = mock(IRadio.class);
		when(r3.getUrl()).thenReturn("r1");

		IRadio r4 = mock(IRadio.class);
		when(r4.getUrl()).thenReturn("R2");

		List<IAudioObject> list = new ArrayList<IAudioObject>();
		list.add(r1);
		list.add(r2);
		list.add(r3);
		list.add(r4);

		List<IAudioObject> duplicates = sut.findDuplicates(list);
		Assert.assertEquals(2, duplicates.size());

		Assert.assertEquals(r3, duplicates.get(0));
		Assert.assertEquals(r4, duplicates.get(1));
	}

	@Test
	public void testPodcast() {
		IPodcastFeedEntry p1 = mock(IPodcastFeedEntry.class);
		when(p1.getTitle()).thenReturn("P1");
		when(p1.getUrl()).thenReturn("U1");

		IPodcastFeedEntry p2 = mock(IPodcastFeedEntry.class);
		when(p2.getTitle()).thenReturn("P2");
		when(p2.getUrl()).thenReturn("U1");

		IPodcastFeedEntry p3 = mock(IPodcastFeedEntry.class);
		when(p3.getTitle()).thenReturn("P1");
		when(p3.getUrl()).thenReturn("U2");

		IPodcastFeedEntry p4 = mock(IPodcastFeedEntry.class);
		when(p4.getTitle()).thenReturn("p1");
		when(p4.getUrl()).thenReturn("u1");

		List<IAudioObject> list = new ArrayList<IAudioObject>();
		list.add(p1);
		list.add(p2);
		list.add(p3);
		list.add(p4);

		List<IAudioObject> duplicates = sut.findDuplicates(list);
		Assert.assertEquals(1, duplicates.size());

		Assert.assertEquals(p4, duplicates.get(0));
	}
}
