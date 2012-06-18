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

package net.sourceforge.atunes.kernel;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IApplicationLifeCycleListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

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
	
	@Test(expected=IllegalStateException.class)
	public void getUserInteraction1() {
		when(mock1.requestUserInteraction()).thenReturn(1);
		when(mock2.requestUserInteraction()).thenReturn(1);
		when(mock3.requestUserInteraction()).thenReturn(2);
		
		sut.getUserInteractionRequests();
	}

	@Test
	public void getUserInteraction2() {
		when(mock1.requestUserInteraction()).thenReturn(1);
		when(mock2.requestUserInteraction()).thenReturn(2);
		when(mock3.requestUserInteraction()).thenReturn(-1);
		
		Map<Integer, IApplicationLifeCycleListener> requests = sut.getUserInteractionRequests();
		assertEquals(2, requests.size());
		assertTrue(requests.containsKey(1));
		assertTrue(requests.containsKey(2));
		assertEquals(mock1, requests.get(1));
		assertEquals(mock2, requests.get(2));
	}
	
	@Test
	public void doUserInteraction() {
		Map<Integer, IApplicationLifeCycleListener> requests = new HashMap<Integer, IApplicationLifeCycleListener>();
		requests.put(1, mock2);
		requests.put(2, mock1);
		requests.put(-1, mock3);
		sut.doUserInteraction(requests);
		
		InOrder order = inOrder(mock2, mock1);
		order.verify(mock2).doUserInteraction();
		order.verify(mock1).doUserInteraction();
		verify(mock3, never()).doUserInteraction();
	}
}
