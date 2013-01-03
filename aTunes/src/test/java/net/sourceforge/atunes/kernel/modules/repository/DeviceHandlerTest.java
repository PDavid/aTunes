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

package net.sourceforge.atunes.kernel.modules.repository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import junit.framework.Assert;
import net.sourceforge.atunes.model.IBeanFactory;

import org.junit.Test;

public class DeviceHandlerTest {

	@Test
	public void deviceNotRefreshedIfNotConnected() {
		DeviceHandler handler = new DeviceHandler();
		
		IBeanFactory beanFactory = mock(IBeanFactory.class);
		RepositoryRefreshLoader loader = mock(RepositoryRefreshLoader.class);
		when(beanFactory.getBean(RepositoryRefreshLoader.class)).thenReturn(loader);
		handler.setBeanFactory(beanFactory);
		
		handler.refreshDevice();
		
		verifyZeroInteractions(loader);
	}
	
	@Test
	public void testFileInDevicePathIfNotConnected() {
		DeviceHandler handler = new DeviceHandler();
		// device is not connected
		
		Assert.assertFalse(handler.isDevicePath(null));
		Assert.assertFalse(handler.isDevicePath(""));
		Assert.assertFalse(handler.isDevicePath("file"));
	}
}
