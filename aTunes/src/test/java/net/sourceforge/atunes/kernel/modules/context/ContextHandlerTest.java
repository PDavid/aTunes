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

package net.sourceforge.atunes.kernel.modules.context;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.AbstractStateMock;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextPanel;
import net.sourceforge.atunes.model.IContextPanelsContainer;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IState;

import org.junit.Before;
import org.junit.Test;

public class ContextHandlerTest {

	private IFrame frame;
	private IState state;
	private IOSManager osManager;
	private IContextPanel p1;
	private IContextPanel p2;
	private IAudioObject ao;
	private IContextPanelsContainer container;
	private IPlayListHandler playListHandler;
	private List<IContextPanel> panels;
	
	private ContextHandler sut;

	@Before
	public void init() {
		frame = mock(IFrame.class);
		osManager = mock(IOSManager.class);
		
		p1 = mock(IContextPanel.class);
		when(p1.getContextPanelName()).thenReturn("PANEL1");
		when(p1.panelNeedsToBeUpdated(any(IAudioObject.class), any(IAudioObject.class))).thenReturn(true);
	
		p2 = mock(IContextPanel.class);
		when(p2.getContextPanelName()).thenReturn("PANEL2");
		when(p2.panelNeedsToBeUpdated(any(IAudioObject.class), any(IAudioObject.class))).thenReturn(true);

		panels = new ArrayList<IContextPanel>();
		panels.add(p1);
		panels.add(p2);

		ao = mock(IAudioObject.class);
		
		container = mock(IContextPanelsContainer.class);
		playListHandler = mock(IPlayListHandler.class);
		when(playListHandler.getCurrentAudioObjectFromVisiblePlayList()).thenReturn(ao);
		
		
		sut = new ContextHandler();
		sut.setFrame(frame);
		sut.setPlayListHandler(playListHandler);
		sut.setOsManager(osManager);
		sut.setContextPanelsContainer(container);
		sut.setContextPanels(panels);		
	}
	
	@Test
	public void testInitializationWithUseContextTrue() {
		// Prepare state
		state = mock(IState.class);
		when(state.getSelectedContextTab()).thenReturn("PANEL2");
		when(state.isUseContext()).thenReturn(true);
		sut.setState(state);

		// Act
		sut.applicationStarted();		
		sut.allHandlersInitialized();
		
		// Verify

		// Context panels added
		verify(container, times(1)).addContextPanel(p1);
		verify(container, times(1)).addContextPanel(p2);
		
		// Context panel selected
		verify(container, times(1)).setSelectedContextPanel("PANEL2");
		
		// Context information updated
		verify(playListHandler, times(1)).getCurrentAudioObjectFromVisiblePlayList();
		verify(p1, times(0)).updateContextPanel(ao, false);
		verify(p2, times(1)).updateContextPanel(ao, false);
	}
	
	@Test
	public void testInitializationWithUseContextFalse() {
		// Prepare		
		state = mock(IState.class);
		when(state.getSelectedContextTab()).thenReturn("PANEL2");
		when(state.isUseContext()).thenReturn(false);
		sut.setState(state);
		
		// Act
		sut.applicationStarted();		
		sut.allHandlersInitialized();
		
		// Verify

		// Context panels added
		verify(container, times(1)).addContextPanel(p1);
		verify(container, times(1)).addContextPanel(p2);
		
		// Context panel selected
		verify(container, times(1)).setSelectedContextPanel("PANEL2");
		
		// Context information NOT updated
		verify(playListHandler, times(0)).getCurrentAudioObjectFromVisiblePlayList();
		verify(p1, times(0)).updateContextPanel(ao, false);
		verify(p2, times(0)).updateContextPanel(ao, false);
	}

	@Test
	public void testShowContextPanel() {
		// Prepare		
		state = new StateMock();
		
		// Act
		sut.setState(state);
		sut.applicationStarted();		
		sut.allHandlersInitialized();
		sut.showContextPanel(true);
		
		// Verify

		// Context information updated
		verify(playListHandler, times(1)).getCurrentAudioObjectFromVisiblePlayList();
		verify(p1, times(0)).updateContextPanel(ao, false);
		verify(p2, times(1)).updateContextPanel(ao, false);		
	}
	
	private static class StateMock extends AbstractStateMock {
		
		boolean useContext = false; // Initially false
		
		@Override
		public boolean isUseContext() {
			return useContext;
		}
		
		@Override
		public void setUseContext(boolean useContext) {
			this.useContext = useContext;
		}
		
		@Override
		public String getSelectedContextTab() {
			return "PANEL2";
		}
	}
}
