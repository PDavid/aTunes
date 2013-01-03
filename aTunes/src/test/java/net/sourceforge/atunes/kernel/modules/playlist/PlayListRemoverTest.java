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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;

import org.junit.Before;
import org.junit.Test;

public class PlayListRemoverTest {

	private PlayListRemover sut;
	
	private IPlayListHandler playListHandler;
	
	private IPlayListsContainer playListsContainer;
	
	private IPlayerHandler playerHandler;
	
	private IPlayListController playListController;
	
	private IPlayListTabController playListTabController;
	
	@Before
	public void init() {
		sut = new PlayListRemover();

		playListHandler = mock(IPlayListHandler.class);
		playListsContainer = mock(IPlayListsContainer.class);
		playerHandler = mock(IPlayerHandler.class);
		playListController = mock(IPlayListController.class);
		playListTabController = mock(IPlayListTabController.class);
		
		sut.setPlayerHandler(playerHandler);
		sut.setPlayListController(playListController);
		sut.setPlayListHandler(playListHandler);
		sut.setPlayListsContainer(playListsContainer);
		sut.setPlayListTabController(playListTabController);
		
	}
	
	@Test
	public void testRemoveActivePlayList() {
		playListsContainer.addPlayList(mock(IPlayList.class));
		playListsContainer.addPlayList(mock(IPlayList.class));
		when(playListsContainer.getVisiblePlayListIndex()).thenReturn(0);
		when(playListsContainer.getActivePlayListIndex()).thenReturn(1);
		when(playListsContainer.getPlayListsCount()).thenReturn(2);
		
		sut.removePlayList(1);
		
		verify(playListsContainer).removePlayList(1);
		
		verify(playListsContainer).setVisiblePlayListActive();
		verify(playerHandler).stopCurrentAudioObject(false);
	}

	@Test
	public void testRemoveNotActivePlayList() {
		playListsContainer.addPlayList(mock(IPlayList.class));
		playListsContainer.addPlayList(mock(IPlayList.class));
		playListsContainer.addPlayList(mock(IPlayList.class));
		when(playListsContainer.getVisiblePlayListIndex()).thenReturn(0);
		when(playListsContainer.getActivePlayListIndex()).thenReturn(2);
		when(playListsContainer.getPlayListsCount()).thenReturn(3);
		
		sut.removePlayList(1);
		
		verify(playListsContainer).removePlayList(1);
		
		verify(playListsContainer, never()).setVisiblePlayListActive();
		verify(playerHandler, never()).stopCurrentAudioObject(false);
	}

	@Test
	public void testRemoveNotActivePlayListAtLeft() {
		playListsContainer.addPlayList(mock(IPlayList.class));
		playListsContainer.addPlayList(mock(IPlayList.class));
		playListsContainer.addPlayList(mock(IPlayList.class));
		when(playListsContainer.getVisiblePlayListIndex()).thenReturn(1);
		when(playListsContainer.getActivePlayListIndex()).thenReturn(2);
		when(playListsContainer.getPlayListsCount()).thenReturn(3);
		
		sut.removePlayList(0);
		
		verify(playListsContainer).removePlayList(0);
		verify(playListsContainer).setVisiblePlayListIndex(0);
		
		verify(playListsContainer, never()).setVisiblePlayListActive();
		verify(playerHandler, never()).stopCurrentAudioObject(false);
	}

	@Test
	public void testRemoveActivePlayListAtLeft() {
		playListsContainer.addPlayList(mock(IPlayList.class));
		playListsContainer.addPlayList(mock(IPlayList.class));
		playListsContainer.addPlayList(mock(IPlayList.class));
		when(playListsContainer.getVisiblePlayListIndex()).thenReturn(2);
		when(playListsContainer.getActivePlayListIndex()).thenReturn(2);
		when(playListsContainer.getPlayListsCount()).thenReturn(3);
		
		sut.removePlayList(0);
		
		verify(playListsContainer).removePlayList(0);
		verify(playListsContainer).setVisiblePlayListIndex(1);
		verify(playListsContainer).setActivePlayListIndex(1);
		
		verify(playListsContainer, never()).setVisiblePlayListActive();
		verify(playerHandler, never()).stopCurrentAudioObject(false);
	}

}
