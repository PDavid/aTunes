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

package net.sourceforge.atunes.kernel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlaybackStateListener;
import net.sourceforge.atunes.model.PlaybackState;

import org.junit.Before;
import org.junit.Test;


public class PlaybackStateListenersTest {

	private PlaybackStateListeners sut;	
	private IPlaybackStateListener mock1;
	private IPlaybackStateListener mock2;
	private IAudioObject audioObject;
	
	@Before
	public void init() {
		sut = new PlaybackStateListeners();
		mock1 = mock(IPlaybackStateListener.class);
		mock2 = mock(IPlaybackStateListener.class);
		sut.addPlaybackStateListener(mock1);
		sut.addPlaybackStateListener(mock2);
		audioObject = mock(IAudioObject.class);
	}

	@Test
	public void playbackStateChanged() {
		sut.playbackStateChanged(PlaybackState.PLAYING, audioObject);
		
		verify(mock1).playbackStateChanged(PlaybackState.PLAYING, audioObject);
		verify(mock2).playbackStateChanged(PlaybackState.PLAYING, audioObject);
	}
}
