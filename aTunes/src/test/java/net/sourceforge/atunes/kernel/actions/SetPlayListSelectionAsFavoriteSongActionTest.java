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

package net.sourceforge.atunes.kernel.actions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.LocalAudioObjectFilter;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;

import org.junit.Assert;
import org.junit.Test;

public class SetPlayListSelectionAsFavoriteSongActionTest {

	@Test
	public void test() {
		SetPlayListSelectionAsFavoriteSongAction sut = new SetPlayListSelectionAsFavoriteSongAction();
		IFavoritesHandler favoritesHandler = mock(IFavoritesHandler.class);
		IPlayListHandler playListHandler = mock(IPlayListHandler.class);
		List<IAudioObject> selectedObjects = new ArrayList<IAudioObject>();
		ILocalAudioObject lao1 = mock(ILocalAudioObject.class);
		IAudioObject ao = mock(IAudioObject.class);
		selectedObjects.add(lao1);
		selectedObjects.add(ao);
		when(playListHandler.getSelectedAudioObjects()).thenReturn(
				selectedObjects);
		sut.setFavoritesHandler(favoritesHandler);
		sut.setPlayListHandler(playListHandler);

		IBeanFactory beanFactory = mock(IBeanFactory.class);
		when(beanFactory.getBean(ILocalAudioObjectFilter.class)).thenReturn(
				new LocalAudioObjectFilter());
		sut.setBeanFactory(beanFactory);

		sut.executeAction();

		verify(favoritesHandler).toggleFavoriteSongs(
				Collections.singletonList(lao1));
	}

	@Test
	public void testDisabled() {
		SetPlayListSelectionAsFavoriteAlbumAction sut = new SetPlayListSelectionAsFavoriteAlbumAction();
		Assert.assertFalse(sut.isEnabledForPlayListSelection(null));
		Assert.assertFalse(sut
				.isEnabledForPlayListSelection(new ArrayList<IAudioObject>()));
		List<IAudioObject> list = new ArrayList<IAudioObject>();
		list.add(mock(IRadio.class));
		Assert.assertFalse(sut.isEnabledForPlayListSelection(list));
		list = new ArrayList<IAudioObject>();
		list.add(mock(IPodcastFeedEntry.class));
		Assert.assertFalse(sut.isEnabledForPlayListSelection(list));
	}

	@Test
	public void testEnabled() {
		SetPlayListSelectionAsFavoriteAlbumAction sut = new SetPlayListSelectionAsFavoriteAlbumAction();
		List<IAudioObject> list = new ArrayList<IAudioObject>();
		list.add(mock(ILocalAudioObject.class));
		Assert.assertTrue(sut.isEnabledForPlayListSelection(list));
	}
}
