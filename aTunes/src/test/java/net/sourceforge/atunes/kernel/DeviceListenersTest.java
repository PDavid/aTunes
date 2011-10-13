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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IDeviceListener;

import org.junit.Before;
import org.junit.Test;


public class DeviceListenersTest {

	private DeviceListeners sut;	
	private IDeviceListener mock1;
	private IDeviceListener mock2;
	
	@Before
	public void init() {
		sut = new DeviceListeners();
		List<IDeviceListener> listeners = new ArrayList<IDeviceListener>();
		mock1 = mock(IDeviceListener.class);
		mock2 = mock(IDeviceListener.class);
		listeners.add(mock1);
		listeners.add(mock2);
		sut.setListeners(listeners);
	}
	
	@Test
	public void deviceConnected() {
		String s = anyString(); 
		sut.deviceConnected(s);
		
		verify(mock1).deviceConnected(s);
		verify(mock2).deviceConnected(s);
	}

	@Test
	public void deviceDisconnected() {
		String s = anyString(); 
		sut.deviceDisconnected(s);
		
		verify(mock1).deviceDisconnected(s);
		verify(mock2).deviceDisconnected(s);
	}

	@Test
	public void deviceReady() {
		String s = anyString(); 
		sut.deviceReady(s);
		
		verify(mock1).deviceReady(s);
		verify(mock2).deviceReady(s);
	}

	
}
