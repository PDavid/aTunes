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
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayerHandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlayActionTest {

	private PlayAction sut;

	@Before
	public void init() {
		sut = new PlayAction();
	}

	@Test
	public void testActionAlwaysEnabled() {
		Assert.assertTrue(sut.isEnabledForPlayListSelection(null));
		Assert.assertTrue(sut.isEnabledForPlayListSelection(new ArrayList<IAudioObject>()));
		List<IAudioObject> list = new ArrayList<IAudioObject>();
		list.add(mock(IAudioObject.class));
		Assert.assertTrue(sut.isEnabledForPlayListSelection(list));
	}

	@Test
	public void testEnginePlayingCallResumeOrPause() {
		IPlayerHandler playerHandler = mock(IPlayerHandler.class);
		when(playerHandler.isEnginePlaying()).thenReturn(true);
		sut.setPlayerHandler(playerHandler);

		sut.executeAction();

		verify(playerHandler).resumeOrPauseCurrentAudioObject();
	}

	@Test
	public void testEnginePausedCallResumeOrPause() {
		IPlayerHandler playerHandler = mock(IPlayerHandler.class);
		when(playerHandler.isEnginePaused()).thenReturn(true);
		sut.setPlayerHandler(playerHandler);

		sut.executeAction();

		verify(playerHandler).resumeOrPauseCurrentAudioObject();
	}

	@Test
	public void testEngineNotPlayingCallStartPlaying() {
		IPlayerHandler playerHandler = mock(IPlayerHandler.class);
		when(playerHandler.isEnginePlaying()).thenReturn(false);
		when(playerHandler.isEnginePaused()).thenReturn(false);
		sut.setPlayerHandler(playerHandler);

		sut.executeAction();

		verify(playerHandler).startPlayingAudioObjectInActivePlayList();
	}
}
