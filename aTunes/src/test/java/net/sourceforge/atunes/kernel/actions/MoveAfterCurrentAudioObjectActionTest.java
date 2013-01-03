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
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;

import org.junit.Assert;
import org.junit.Test;

public class MoveAfterCurrentAudioObjectActionTest {

	@Test
	public void testEnabled() {
		MoveAfterCurrentAudioObjectAction sut = new MoveAfterCurrentAudioObjectAction();
		IPlayListHandler playListHandler = mock(IPlayListHandler.class);
		
		// not filtered, active playlist visible and selection not empty
		when(playListHandler.isActivePlayListVisible()).thenReturn(true);
		when(playListHandler.isFiltered()).thenReturn(false);
		List<IAudioObject> selection = new ArrayList<IAudioObject>();
		selection.add(mock(IAudioObject.class));
		
		sut.setPlayListHandler(playListHandler);
		
		Assert.assertTrue(sut.isEnabledForPlayListSelection(selection));
	}
	
	@Test
	public void testDisabled1() {
		MoveAfterCurrentAudioObjectAction sut = new MoveAfterCurrentAudioObjectAction();
		IPlayListHandler playListHandler = mock(IPlayListHandler.class);
		
		// not filtered, NOT active playlist visible and selection not empty
		when(playListHandler.isActivePlayListVisible()).thenReturn(false);
		when(playListHandler.isFiltered()).thenReturn(false);
		List<IAudioObject> selection = new ArrayList<IAudioObject>();
		selection.add(mock(IAudioObject.class));
		
		sut.setPlayListHandler(playListHandler);
		
		Assert.assertFalse(sut.isEnabledForPlayListSelection(selection));
	}

	@Test
	public void testDisabled2() {
		MoveAfterCurrentAudioObjectAction sut = new MoveAfterCurrentAudioObjectAction();
		IPlayListHandler playListHandler = mock(IPlayListHandler.class);
		
		// FILTERED, active playlist visible and selection not empty
		when(playListHandler.isActivePlayListVisible()).thenReturn(true);
		when(playListHandler.isFiltered()).thenReturn(true);
		List<IAudioObject> selection = new ArrayList<IAudioObject>();
		selection.add(mock(IAudioObject.class));
		
		sut.setPlayListHandler(playListHandler);
		
		Assert.assertFalse(sut.isEnabledForPlayListSelection(selection));
	}
	
	@Test
	public void testDisabled3() {
		MoveAfterCurrentAudioObjectAction sut = new MoveAfterCurrentAudioObjectAction();
		IPlayListHandler playListHandler = mock(IPlayListHandler.class);
		
		// not filtered, active playlist visible and SELECTION EMPTY
		when(playListHandler.isActivePlayListVisible()).thenReturn(true);
		when(playListHandler.isFiltered()).thenReturn(true);
		List<IAudioObject> selection = new ArrayList<IAudioObject>();
		
		sut.setPlayListHandler(playListHandler);
		
		Assert.assertFalse(sut.isEnabledForPlayListSelection(selection));
	}


}
