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
import net.sourceforge.atunes.model.IPlayList;

import org.junit.Before;
import org.junit.Test;

public class PlayListsContainerTest {

	private static final int VISIBLE_PLAYLIST = 2;
	private static final int ACTIVE_PLAYLIST = 1;
	private PlayListsContainer sut;
	
	private IPlayList playList1;
	private IPlayList playList2;
	private IPlayList playList3;
	
	@Before
	public void init() {
		sut = new PlayListsContainer();
		
		playList1 = mock(IPlayList.class);
		playList2 = mock(IPlayList.class);
		playList3 = mock(IPlayList.class);
		
		sut.addPlayList(playList1);
		sut.addPlayList(playList2);
		sut.addPlayList(playList3);

		sut.setActivePlayListIndex(ACTIVE_PLAYLIST);
		sut.setVisiblePlayListIndex(VISIBLE_PLAYLIST);
	}
	
	@Test
	public void testSetVisiblePlayListActive() {
		sut.setVisiblePlayListActive();
		assertEquals(VISIBLE_PLAYLIST, sut.getActivePlayListIndex());
	}

	@Test
	public void testGetVisiblePlayListIndex() {
		assertEquals(VISIBLE_PLAYLIST, sut.getVisiblePlayListIndex());
	}

	@Test
	public void testGetActivePlayListIndex() {
		assertEquals(ACTIVE_PLAYLIST, sut.getActivePlayListIndex());
	}

	@Test
	public void testRemovePlayList() {
		sut.removePlayList(2);
		assertEquals(2, sut.getPlayListsCount());
		assertEquals(playList1, sut.getPlayListAt(0));
		assertEquals(playList2, sut.getPlayListAt(1));
	}

	@Test
	public void testGetCurrentPlayList() {
		assertEquals(playList2, sut.getActivePlayList());
		assertEquals(playList3, sut.getVisiblePlayList());
	}

	@Test
	public void testClear() {
		sut.clear();
		assertEquals(0, sut.getPlayListsCount());
	}

	@Test
	public void testAddPlayListIntIPlayList() {
		IPlayList playList0 = mock(IPlayList.class);
		sut.addPlayList(0, playList0);
		assertEquals(4, sut.getPlayListsCount());
		assertEquals(playList0, sut.getPlayListAt(0));
		assertEquals(playList1, sut.getPlayListAt(1));
		assertEquals(playList2, sut.getPlayListAt(2));
		assertEquals(playList3, sut.getPlayListAt(3));
	}
}
