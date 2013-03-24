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

package net.sourceforge.atunes.gui.frame;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Dimension;

import net.sourceforge.atunes.model.IFrameSize;
import net.sourceforge.atunes.model.IStateUI;

import org.junit.Assert;
import org.junit.Test;

public class SaveFrameSizeTaskTest {

	@Test
	public void testMaximized() {
		AbstractSingleFrame frame = mock(AbstractSingleFrame.class);
		when(frame.getExtendedState())
				.thenReturn(java.awt.Frame.MAXIMIZED_BOTH);
		IStateUI stateUI = mock(IStateUI.class);
		IFrameSize frameSize = new FrameSize();
		when(stateUI.getFrameSize()).thenReturn(frameSize);
		SaveFrameSizeTask sut = new SaveFrameSizeTask();
		sut.setFrame(frame);
		sut.setStateUI(stateUI);

		// One dimension equals to screen size
		sut.setWidth(1024);
		sut.setHeight(2);
		sut.setScreenSize(new Dimension(1024, 768));

		sut.run();

		verify(stateUI).setFrameSize(frameSize);
		Assert.assertEquals(1024, frameSize.getWindowWidth());
		Assert.assertEquals(2, frameSize.getWindowHeight());
		Assert.assertTrue(frameSize.isMaximized());
	}
}
