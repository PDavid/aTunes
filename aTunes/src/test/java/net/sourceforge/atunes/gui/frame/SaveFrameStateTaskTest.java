/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.gui.frame;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.sourceforge.atunes.model.IFrameState;
import net.sourceforge.atunes.model.IState;

import org.junit.Assert;
import org.junit.Test;

public class SaveFrameStateTaskTest {

	@Test
	public void test() {
		AbstractSingleFrame frame = mock(AbstractSingleFrame.class);
		when(frame.getExtendedState()).thenReturn(java.awt.Frame.MAXIMIZED_BOTH);
		IState state = mock(IState.class);
		IFrameState frameState = new FrameState();
		when(state.getFrameState(frame.getClass())).thenReturn(frameState);
		SaveFrameStateTask sut = new SaveFrameStateTask(frame, state, 1, 2, 3, 4);
		
		sut.run();
		
		verify(state).setFrameState(frame.getClass(), frameState);
		Assert.assertEquals(1, frameState.getWindowWidth());
		Assert.assertEquals(2, frameState.getWindowHeight());
		Assert.assertEquals(3, frameState.getXPosition());
		Assert.assertEquals(4, frameState.getYPosition());
		Assert.assertTrue(frameState.isMaximized());
	}
}
