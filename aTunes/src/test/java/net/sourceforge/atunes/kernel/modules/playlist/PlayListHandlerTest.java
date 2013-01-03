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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.IPlayList;

import org.junit.Test;
import org.mockito.Mockito;

public class PlayListHandlerTest {

	@Test
	public void testRenamePlayListPersists() {
		PlayListHandler sut = new PlayListHandler();
		PlayListTabController controller = mock(PlayListTabController.class);
		PlayListPersistor persistor = mock(PlayListPersistor.class);
		IPlayListsContainer container = mock(IPlayListsContainer.class);
		Mockito.when(container.getPlayListAt(0)).thenReturn(mock(IPlayList.class));
		Mockito.when(container.getPlayListsCount()).thenReturn(1);
		ListOfPlayListsCreator listOfPlayLists = new ListOfPlayListsCreator();
		sut.setPlayListsContainer(container);
		sut.setPlayListTabController(controller);
		sut.setPlayListPersistor(persistor);
		sut.setListOfPlayListsCreator(listOfPlayLists);

		sut.renameCurrentVisiblePlayList("new name");

		verify(persistor).persistPlayLists(any(IListOfPlayLists.class));
	}
}
