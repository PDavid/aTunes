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

package net.sourceforge.atunes.kernel.modules.os;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.IUIHandler;

import org.junit.Before;
import org.junit.Test;

public class MacOSXInitializerTest {

	private MacOSXInitializer sut;
	private IMacOSXApplication app;

	@Before
	public void init() {
		this.sut = new MacOSXInitializer();
		this.sut.setKernel(mock(IKernel.class));
		this.sut.setStateService(mock(IStateService.class));
		this.sut.setUiHandler(mock(IUIHandler.class));
	}

	@Test
	public void testNotInitialized() {
		this.app = mock(IMacOSXApplication.class);
		when(this.app.initialize()).thenReturn(false);
		this.sut.setMacOsApplication(this.app);

		IFrame frame = mock(IFrame.class);
		this.sut.initialize(frame);

		verify(this.app, never()).addDockIconMenu();
		verify(this.app, never()).registerAbout(any(MacOSXAboutHandler.class));
		verify(this.app, never()).registerAppReOpenedListener(
				any(MacOSXAppReOpenedListener.class));
		verify(this.app, never()).registerPreferences(
				any(MacOSXPreferencesHandler.class));
		verify(this.app, never()).registerQuit(any(MacOSXQuitHandler.class));
	}

	@Test
	public void testInitialized() {
		this.app = mock(IMacOSXApplication.class);
		when(this.app.initialize()).thenReturn(true);
		this.sut.setMacOsApplication(this.app);

		IFrame frame = mock(IFrame.class);
		this.sut.initialize(frame);

		verify(this.app).addDockIconMenu();
		verify(this.app).registerAbout(any(MacOSXAboutHandler.class));
		verify(this.app).registerAppReOpenedListener(
				any(MacOSXAppReOpenedListener.class));
		verify(this.app).registerPreferences(
				any(MacOSXPreferencesHandler.class));
		verify(this.app).registerQuit(any(MacOSXQuitHandler.class));
	}
}
