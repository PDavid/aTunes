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
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IRipperHandler;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test of RipCdAction
 * @author alex
 *
 */
public class RipCDActionTest {

	@Test
	public void test() {
		RipCDAction sut = new RipCDAction();
		IRipperHandler ripperHandler = mock(IRipperHandler.class);
		sut.setRipperHandler(ripperHandler);
		
		sut.executeAction();
		
		verify(ripperHandler).startCdRipper();
	}
	
	@Test
	public void testEnabled() {
		RipCDAction sut = new RipCDAction();
		IRipperHandler ripperHandler = mock(IRipperHandler.class);
		when(ripperHandler.isRipSupported()).thenReturn(true);
		IApplicationArguments arguments = mock(IApplicationArguments.class);
		when(arguments.isSimulateCD()).thenReturn(false);
		sut.setRipperHandler(ripperHandler);
		sut.setApplicationArguments(arguments);
		Assert.assertTrue(sut.isEnabled());
	}

	@Test
	public void testEnabled2() {
		RipCDAction sut = new RipCDAction();
		IRipperHandler ripperHandler = mock(IRipperHandler.class);
		when(ripperHandler.isRipSupported()).thenReturn(false);
		IApplicationArguments arguments = mock(IApplicationArguments.class);
		when(arguments.isSimulateCD()).thenReturn(true);
		sut.setRipperHandler(ripperHandler);
		sut.setApplicationArguments(arguments);
		Assert.assertTrue(sut.isEnabled());
	}
	
	@Test
	public void testDisabled() {
		RipCDAction sut = new RipCDAction();
		IRipperHandler ripperHandler = mock(IRipperHandler.class);
		when(ripperHandler.isRipSupported()).thenReturn(false);
		IApplicationArguments arguments = mock(IApplicationArguments.class);
		when(arguments.isSimulateCD()).thenReturn(false);
		sut.setRipperHandler(ripperHandler);
		sut.setApplicationArguments(arguments);
		Assert.assertFalse(sut.isEnabled());
	}
}
