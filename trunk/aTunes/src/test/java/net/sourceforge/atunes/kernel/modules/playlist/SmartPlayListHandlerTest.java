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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryTestMockUtils;
import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author alex
 * 
 */
public class SmartPlayListHandlerTest {

	private SmartPlayListHandler sut;

	private IPlayListHandler playListHandler;

	private IRepositoryHandler repositoryHandler;

	@Before
	public void init() {
		sut = new SmartPlayListHandler();
		repositoryHandler = Mockito.mock(IRepositoryHandler.class);

		sut.setRepositoryHandler(repositoryHandler);

		IBeanFactory beanFactory = Mockito.mock(IBeanFactory.class);
		Mockito.when(beanFactory.getBean(IAudioObjectComparator.class))
				.thenReturn(Mockito.mock(IAudioObjectComparator.class));
		sut.setBeanFactory(beanFactory);

		playListHandler = Mockito.mock(IPlayListHandler.class);
		sut.setPlayListHandler(playListHandler);
	}

	@Test
	public void testAddRandomSongs() {
		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();
		for (int i = 0; i < 10; i++) {
			list.add(RepositoryTestMockUtils.createRandomMockLocalAudioObject());
		}

		Mockito.when(repositoryHandler.getAudioFilesList()).thenReturn(list);

		sut.addRandomSongs(10);
		ArgumentCaptor<ArrayList> argument = ArgumentCaptor
				.forClass(ArrayList.class);
		Mockito.verify(playListHandler)
				.addToVisiblePlayList(argument.capture());
		Assert.assertEquals(10, argument.getValue().size());
	}

	@Test
	public void testAddRandomSongsEmpty() {
		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();
		Mockito.when(repositoryHandler.getAudioFilesList()).thenReturn(list);

		sut.addRandomSongs(10);
		ArgumentCaptor<ArrayList> argument = ArgumentCaptor
				.forClass(ArrayList.class);
		Mockito.verify(playListHandler)
				.addToVisiblePlayList(argument.capture());
		Assert.assertEquals(0, argument.getValue().size());
	}

	@Test
	public void testAddRandomSongsNull() {
		Mockito.when(repositoryHandler.getAudioFilesList()).thenReturn(null);

		sut.addRandomSongs(10);
		ArgumentCaptor<ArrayList> argument = ArgumentCaptor
				.forClass(ArrayList.class);
		Mockito.verify(playListHandler)
				.addToVisiblePlayList(argument.capture());
		Assert.assertEquals(0, argument.getValue().size());
	}

}
