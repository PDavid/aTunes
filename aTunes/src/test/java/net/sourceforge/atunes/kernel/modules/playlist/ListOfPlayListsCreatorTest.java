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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.IPlayList;

import org.junit.Before;
import org.junit.Test;

public class ListOfPlayListsCreatorTest {

	private ListOfPlayListsCreator sut;
	
	private IPlayList playList1;
	private IPlayList playList2;
	private IPlayList playList3;
	private IPlayList playList4;
	
	private List<IPlayList> list;
	
	@Before
	public void init() {
		sut = new ListOfPlayListsCreator();
		
		playList1 = mock(IPlayList.class);
		playList2 = mock(IPlayList.class);
		playList3 = mock(IPlayList.class);
		playList4 = mock(IPlayList.class);
		
		list = new ArrayList<IPlayList>();
		list.add(playList1);
		list.add(playList2);
		list.add(playList3);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNull() {
		sut.getListOfPlayLists(null);
	}	
	
	@Test(expected=IllegalArgumentException.class)
	public void testEmpty() {
		IPlayListsContainer container = mock(IPlayListsContainer.class);
		when(container.getPlayListsCount()).thenReturn(0);

		sut.getListOfPlayLists(container);
	}	
	
	@Test
	public void testWithoutFilter() {
		IPlayListsContainer container = mock(IPlayListsContainer.class);
		when(container.getPlayListsCount()).thenReturn(3);
		when(container.getPlayListAt(0)).thenReturn(playList1);
		when(container.getPlayListAt(1)).thenReturn(playList2);
		when(container.getPlayListAt(2)).thenReturn(playList3);
		when(container.getActivePlayListIndex()).thenReturn(1);

		IListOfPlayLists result = sut.getListOfPlayLists(container);

		assertEquals(list.size(), result.getPlayLists().size());
		assertEquals(playList1, result.getPlayLists().get(0));
		assertEquals(playList2, result.getPlayLists().get(1));
		assertEquals(playList3, result.getPlayLists().get(2));
		assertEquals(1, result.getSelectedPlayList());
	}

	@Test
	public void testWithFilter() {
		IPlayListsContainer container = mock(IPlayListsContainer.class);
		when(container.getPlayListsCount()).thenReturn(3);
		when(container.getPlayListAt(0)).thenReturn(playList1);
		when(container.getPlayListAt(1)).thenReturn(playList2);
		when(container.getPlayListAt(2)).thenReturn(playList3);
		when(container.getActivePlayListIndex()).thenReturn(1);
		when(container.isFiltered()).thenReturn(true);
		when(container.getNonFilteredPlayList()).thenReturn(playList4);

		IListOfPlayLists result = sut.getListOfPlayLists(container);

		assertEquals(list.size(), result.getPlayLists().size());
		assertEquals(playList1, result.getPlayLists().get(0));
		assertEquals(playList4, result.getPlayLists().get(1));
		assertEquals(playList3, result.getPlayLists().get(2));
	}
}
