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

package net.sourceforge.atunes.kernel.modules.playlist;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayList;

import org.junit.Assert;
import org.junit.Test;

public class PlayListLocalAudioObjectFilterTest {

	@Test
	public void test() {
		PlayListLocalAudioObjectFilter sut = new PlayListLocalAudioObjectFilter();
		
		IAudioObject ao1 = mock(IAudioObject.class);
		IAudioObject ao2 = mock(IAudioObject.class);
		ILocalAudioObject lao = mock(ILocalAudioObject.class);
		
		IPlayList playList = mock(IPlayList.class);
		when(playList.size()).thenReturn(3);
		when(playList.get(0)).thenReturn(ao1);
		when(playList.get(1)).thenReturn(ao2);
		when(playList.get(2)).thenReturn(lao);
		
		List<ILocalAudioObject> result = sut.getObjects(playList);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(lao, result.get(0));
	}
}
