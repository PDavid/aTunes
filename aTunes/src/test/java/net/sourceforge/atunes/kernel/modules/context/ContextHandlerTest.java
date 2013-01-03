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

package net.sourceforge.atunes.kernel.modules.context;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.AbstractStateContextMock;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextPanel;
import net.sourceforge.atunes.model.IContextPanelsContainer;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IStateContext;

import org.junit.Before;
import org.junit.Test;

public class ContextHandlerTest {

	private IFrame frame;
	private IStateContext state;
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
		this.frame = mock(IFrame.class);
		this.osManager = mock(IOSManager.class);

		this.p1 = mock(IContextPanel.class);
		when(this.p1.getContextPanelName()).thenReturn("PANEL1");
		when(
				this.p1.panelNeedsToBeUpdated(any(IAudioObject.class),
						any(IAudioObject.class))).thenReturn(true);

		this.p2 = mock(IContextPanel.class);
		when(this.p2.getContextPanelName()).thenReturn("PANEL2");
		when(
				this.p2.panelNeedsToBeUpdated(any(IAudioObject.class),
						any(IAudioObject.class))).thenReturn(true);

		this.panels = new ArrayList<IContextPanel>();
		this.panels.add(this.p1);
		this.panels.add(this.p2);

		this.ao = mock(IAudioObject.class);

		this.container = mock(IContextPanelsContainer.class);
		this.playListHandler = mock(IPlayListHandler.class);
		when(this.playListHandler.getCurrentAudioObjectFromVisiblePlayList())
				.thenReturn(this.ao);

		this.sut = new ContextHandler();
		this.sut.setFrame(this.frame);
		this.sut.setPlayListHandler(this.playListHandler);
		this.sut.setOsManager(this.osManager);
		this.sut.setContextPanelsContainer(this.container);
		this.sut.setContextPanels(this.panels);
	}

	@Test
	public void testInitializationWithUseContextTrue() {
		// Prepare state
		this.state = mock(IStateContext.class);
		when(this.state.getSelectedContextTab()).thenReturn("PANEL2");
		when(this.state.isUseContext()).thenReturn(true);
		this.sut.setStateContext(this.state);

		// Act
		this.sut.applicationStarted();
		this.sut.allHandlersInitialized();
		this.sut.deferredInitialization();

		// Verify

		// Context panels added
		verify(this.container, times(1)).addContextPanel(this.p1);
		verify(this.container, times(1)).addContextPanel(this.p2);

		// Context panel selected
		verify(this.container, times(1)).setSelectedContextPanel("PANEL2");
	}

	@Test
	public void testInitializationWithUseContextFalse() {
		// Prepare
		this.state = mock(IStateContext.class);
		when(this.state.getSelectedContextTab()).thenReturn("PANEL2");
		when(this.state.isUseContext()).thenReturn(false);
		this.sut.setStateContext(this.state);

		// Act
		this.sut.applicationStarted();
		this.sut.allHandlersInitialized();
		this.sut.deferredInitialization();

		// Verify

		// Context panels added
		verify(this.container, times(1)).addContextPanel(this.p1);
		verify(this.container, times(1)).addContextPanel(this.p2);

		// Context panel selected
		verify(this.container, times(1)).setSelectedContextPanel("PANEL2");

		// Context information NOT updated
		verify(this.p1, times(0)).updateContextPanel(this.ao, false);
		verify(this.p2, times(0)).updateContextPanel(this.ao, false);
	}

	@Test
	public void testShowContextPanel() {
		// Prepare
		this.state = new StateMock();

		// Act
		this.sut.setStateContext(this.state);
		this.sut.applicationStarted();
		this.sut.allHandlersInitialized();
		this.sut.deferredInitialization();
		this.sut.showContextPanel(true);

		// Verify

		// Context information updated
		verify(this.p1, times(0)).updateContextPanel(this.ao, false);
		verify(this.p2, times(1)).updateContextPanel(this.ao, false);
	}

	private static class StateMock extends AbstractStateContextMock {

		boolean useContext = false; // Initially false

		@Override
		public boolean isUseContext() {
			return this.useContext;
		}

		@Override
		public void setUseContext(final boolean useContext) {
			this.useContext = useContext;
		}

		@Override
		public String getSelectedContextTab() {
			return "PANEL2";
		}

		@Override
		public Map<String, String> describeState() {
			return null;
		}
	}
}
