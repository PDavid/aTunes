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

package net.sourceforge.atunes.kernel;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IApplicationLifeCycleListener;

import org.junit.Before;
import org.junit.Test;

public class ApplicationLifeCycleListenersTest {

	private ApplicationLifeCycleListeners sut;
	private IApplicationLifeCycleListener mock1;
	private IApplicationLifeCycleListener mock2;
	private IApplicationLifeCycleListener mock3;
	
	@Before
	public void init() {
		sut = new ApplicationLifeCycleListeners();
		List<IApplicationLifeCycleListener> listeners = new ArrayList<IApplicationLifeCycleListener>();
		mock1 = mock(IApplicationLifeCycleListener.class);
		mock2 = mock(IApplicationLifeCycleListener.class);
		mock3 = mock(IApplicationLifeCycleListener.class);
		listeners.add(mock1);
		listeners.add(mock2);
		listeners.add(mock3);
		sut.setListeners(listeners);
	}
	
	@Test
	public void applicationStarted() {
		sut.applicationStarted();
		
		verify(mock1).applicationStarted();
		verify(mock2).applicationStarted();
		verify(mock3).applicationStarted();
	}
	
	@Test
	public void allHandlersInitialized() {
		sut.allHandlersInitialized();
		
		verify(mock1).allHandlersInitialized();
		verify(mock2).allHandlersInitialized();
		verify(mock3).allHandlersInitialized();
	}
	
	@Test
	public void applicationFinish() {
		sut.applicationFinish();
		
		verify(mock1).applicationFinish();
		verify(mock2).applicationFinish();
		verify(mock3).applicationFinish();
	}
}
