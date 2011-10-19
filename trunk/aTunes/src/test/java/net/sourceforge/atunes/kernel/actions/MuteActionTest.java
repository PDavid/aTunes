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

package net.sourceforge.atunes.kernel.actions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.swing.AbstractAction;

import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IState;

import org.junit.Test;

public class MuteActionTest {

	@Test
	public void testMute() {
		MuteAction sut = new MuteAction();
		IPlayerHandler playerHandler = mock(IPlayerHandler.class);
		ILookAndFeelManager lookAndFeelManager = mock(ILookAndFeelManager.class);
		IState state = mock(IState.class);
		when(state.isMuteEnabled()).thenReturn(true);
		ILookAndFeel lookAndFeel = mock(ILookAndFeel.class);
		when(lookAndFeelManager.getCurrentLookAndFeel()).thenReturn(lookAndFeel);
		sut.setPlayerHandler(playerHandler);
		sut.setLookAndFeelManager(lookAndFeelManager);
		sut.setState(state);
		sut.putValue(AbstractAction.SELECTED_KEY, true);
		
		sut.executeAction();
		
		verify(playerHandler).applyMuteState(true);
	}

	@Test
	public void testNoMute() {
		MuteAction sut = new MuteAction();
		IPlayerHandler playerHandler = mock(IPlayerHandler.class);
		ILookAndFeelManager lookAndFeelManager = mock(ILookAndFeelManager.class);
		IState state = mock(IState.class);
		when(state.isMuteEnabled()).thenReturn(false);
		ILookAndFeel lookAndFeel = mock(ILookAndFeel.class);
		when(lookAndFeelManager.getCurrentLookAndFeel()).thenReturn(lookAndFeel);
		sut.setPlayerHandler(playerHandler);
		sut.setLookAndFeelManager(lookAndFeelManager);
		sut.setState(state);
		sut.putValue(AbstractAction.SELECTED_KEY, false);
		
		sut.executeAction();
		
		verify(playerHandler).applyMuteState(false);
	}

}
