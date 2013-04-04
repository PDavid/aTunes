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
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateContext;

import org.junit.Test;

public class ShowContextActionTest {

	@Test
	public void testShow() {
		ShowContextAction sut = new ShowContextAction();
		IStateContext state = mock(IStateContext.class);
		when(state.isUseContext()).thenReturn(true);
		IContextHandler contextHandler = mock(IContextHandler.class);
		IOSManager osManager = mock(IOSManager.class);
		sut.setOsManager(osManager);
		sut.setContextHandler(contextHandler);
		sut.setStateContext(state);
		sut.initialize();

		sut.executeAction();

		verify(contextHandler).showContextPanel(true);
	}

	@Test
	public void testHide() {
		ShowContextAction sut = new ShowContextAction();
		IStateContext state = mock(IStateContext.class);
		when(state.isUseContext()).thenReturn(false);
		IContextHandler contextHandler = mock(IContextHandler.class);
		IOSManager osManager = mock(IOSManager.class);
		sut.setOsManager(osManager);
		sut.setContextHandler(contextHandler);
		sut.setStateContext(state);
		sut.initialize();

		sut.executeAction();

		verify(contextHandler).showContextPanel(false);
	}
}
