/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.kernel.modules.os;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IUIHandler;

import org.junit.Before;
import org.junit.Test;

public class MacOSXInitializerTest {

	private MacOSXInitializer sut;
	private IMacOSXApplication app;
	
	@Before
	public void init() {
		sut = new MacOSXInitializer();
		sut.setKernel(mock(IKernel.class));
		sut.setStateHandler(mock(IStateHandler.class));
		sut.setUiHandler(mock(IUIHandler.class));
	}
	
	@Test
	public void testNotInitialized() {
		app = mock(IMacOSXApplication.class);
		when(app.initialize()).thenReturn(false);
		sut.setMacOsApplication(app);
		
		sut.initialize();
		
		verify(app, never()).addDockIconMenu();
		verify(app, never()).registerAbout(any(MacOSXAboutHandler.class));
		verify(app, never()).registerAppReOpenedListener(any(MacOSXAppReOpenedListener.class));
		verify(app, never()).registerPreferences(any(MacOSXPreferencesHandler.class));
		verify(app, never()).registerQuit(any(MacOSXQuitHandler.class));
	}

	@Test
	public void testInitialized() {
		app = mock(IMacOSXApplication.class);
		when(app.initialize()).thenReturn(true);
		sut.setMacOsApplication(app);
		
		sut.initialize();
		
		verify(app).addDockIconMenu();
		verify(app).registerAbout(any(MacOSXAboutHandler.class));
		verify(app).registerAppReOpenedListener(any(MacOSXAppReOpenedListener.class));
		verify(app).registerPreferences(any(MacOSXPreferencesHandler.class));
		verify(app).registerQuit(any(MacOSXQuitHandler.class));
	}
}
