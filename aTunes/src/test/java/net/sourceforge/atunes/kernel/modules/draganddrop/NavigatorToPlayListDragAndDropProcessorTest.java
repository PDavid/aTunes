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

package net.sourceforge.atunes.kernel.modules.draganddrop;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryTestMockUtils;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.CollectionUtils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

public class NavigatorToPlayListDragAndDropProcessorTest {

	private NavigatorToPlayListDragAndDropProcessor sut;
	private INavigationHandler navigationHandler;
	private IPlayListHandler playListHandler;
	private IPlayListTable playListTable;
	private ListSelectionModel listSelectionModel;

	@Before
	public void init() {
		this.sut = new NavigatorToPlayListDragAndDropProcessor();
		this.navigationHandler = mock(INavigationHandler.class);
		this.playListHandler = mock(IPlayListHandler.class);
		this.playListTable = mock(IPlayListTable.class);
		listSelectionModel = mock(ListSelectionModel.class);
		when(this.playListTable.getSelectionModel()).thenReturn(
				listSelectionModel);
		this.sut.setNavigationHandler(navigationHandler);
		this.sut.setPlayListHandler(playListHandler);
		this.sut.setPlayListTable(playListTable);
	}

	@Test
	public void testDragFromNavigatorTree() {
		int dropRow = 10;
		final ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");
		final ILocalAudioObject ao2 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 2");

		ITreeNode node = mock(ITreeNode.class);
		List<IAudioObject> nodeAudioObjects = CollectionUtils
				.fillCollectionWithElements(new ArrayList<IAudioObject>(), ao1,
						ao2);
		when(this.navigationHandler.getAudioObjectsForTreeNode(node))
				.thenReturn(nodeAudioObjects);
		this.sut.dragFromNavigatorTree(dropRow, CollectionUtils
				.fillCollectionWithElements(new ArrayList<ITreeNode>(), node));

		verify(this.playListHandler).addToVisiblePlayList(Matchers.eq(dropRow),
				Matchers.argThat(new BaseMatcher<List<IAudioObject>>() {

					@Override
					public boolean matches(final Object arg) {
						@SuppressWarnings("unchecked")
						List<IAudioObject> list = (List<IAudioObject>) arg;
						return list.size() == 2 && list.get(0).equals(ao1)
								&& list.get(1).equals(ao2);
					}

					@Override
					public void describeTo(final Description arg0) {
					}

				}));

		verify(this.listSelectionModel).addSelectionInterval(dropRow,
				dropRow + nodeAudioObjects.size() - 1);
	}

	@Test
	public void testDragFromNavigatorTable() {
		int dropRow = 10;
		final ILocalAudioObject ao1 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 1");
		final ILocalAudioObject ao2 = RepositoryTestMockUtils
				.createMockLocalAudioObject(null, "Artist 1", "Album 1",
						"Title 2");

		List<Integer> rows = CollectionUtils.fillCollectionWithElements(
				new ArrayList<Integer>(), 1, 2);
		when(this.navigationHandler.getAudioObjectInNavigationTable(1))
				.thenReturn(ao1);
		when(this.navigationHandler.getAudioObjectInNavigationTable(2))
				.thenReturn(ao2);
		this.sut.dragFromNavigatorTable(dropRow, rows);

		verify(this.playListHandler).addToVisiblePlayList(Matchers.eq(dropRow),
				Matchers.argThat(new BaseMatcher<List<IAudioObject>>() {

					@Override
					public boolean matches(final Object arg) {
						@SuppressWarnings("unchecked")
						List<IAudioObject> list = (List<IAudioObject>) arg;
						return list.size() == 2 && list.get(0).equals(ao1)
								&& list.get(1).equals(ao2);
					}

					@Override
					public void describeTo(final Description arg0) {
					}

				}));

		verify(this.listSelectionModel).addSelectionInterval(dropRow,
				dropRow + rows.size() - 1);
	}

}
