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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import net.sourceforge.atunes.model.IPlayList;

import org.junit.Before;
import org.junit.Test;

public class PlayListNameCreatorTest {

	private PlayListNameCreator sut;
	
	private IPlayListsContainer container;
	
	private IPlayList playList;
	
	@Before
	public void init() {
		sut = new PlayListNameCreator();
		container = mock(IPlayListsContainer.class);
		playList = mock(IPlayList.class);
		when(playList.getName()).thenReturn("PLAYLIST 1");
		when(container.getPlayListAt(0)).thenReturn(playList);
		when(container.getPlayListsCount()).thenReturn(1);
	}

	@Test
	public void testCreateName() {
		assertEquals("PLAYLIST 1", sut.getNameForPlaylist(null, null));
		assertEquals("PLAYLIST 2", sut.getNameForPlaylist(null, null));
	}

	@Test
	public void testCreateExistingName() {
		assertEquals("PLAYLIST 2", sut.getNameForPlaylist(container, null));
	}

	@Test
	public void testReturnName() {		
		assertEquals("PLAYLIST 1", sut.getNameForPlaylist(null, playList));
	}
}
