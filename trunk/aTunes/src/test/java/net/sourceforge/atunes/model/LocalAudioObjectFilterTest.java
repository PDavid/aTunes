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

package net.sourceforge.atunes.model;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class LocalAudioObjectFilterTest {

	@Test
	public void testEmpty() {
		LocalAudioObjectFilter sut = new LocalAudioObjectFilter();
		Assert.assertTrue(sut.getLocalAudioObjects(null).isEmpty());
		Assert.assertTrue(sut.getLocalAudioObjects(new ArrayList<IAudioObject>()).isEmpty());
	}

	@Test
	public void testNotEmpty() {
		LocalAudioObjectFilter sut = new LocalAudioObjectFilter();
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
}
