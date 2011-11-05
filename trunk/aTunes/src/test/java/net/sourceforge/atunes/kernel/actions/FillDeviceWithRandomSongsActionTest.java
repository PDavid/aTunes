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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IInputDialog;

import org.junit.Assert;
import org.junit.Test;

public class FillDeviceWithRandomSongsActionTest {

	@Test
	public void test() {
		FillDeviceWithRandomSongsAction sut = new FillDeviceWithRandomSongsAction();
		IDeviceHandler deviceHandler = mock(IDeviceHandler.class);
		IInputDialog inputDialog = mock(IInputDialog.class);
		when(inputDialog.getResult()).thenReturn("20");
		IErrorDialogFactory errorDialogFactory = mock(IErrorDialogFactory.class);
		IErrorDialog errorDialog = mock(IErrorDialog.class);
		when(errorDialogFactory.getDialog()).thenReturn(errorDialog);
		IFrame frame = mock(IFrame.class);
		
		sut.setDeviceHandler(deviceHandler);
		sut.setInputDialog(inputDialog);
		sut.setErrorDialogFactory(errorDialogFactory);
		sut.setFrame(frame);
		sut.setFreeMemory("20");
		
		sut.executeAction();

		verify(inputDialog).showDialog("20");
		verify(deviceHandler).fillWithRandomSongs(20);
	}

	@Test
	public void testInputError() {
		FillDeviceWithRandomSongsAction sut = new FillDeviceWithRandomSongsAction();
		IDeviceHandler deviceHandler = mock(IDeviceHandler.class);
		IInputDialog inputDialog = mock(IInputDialog.class);
		when(inputDialog.getResult()).thenReturn("not a number");
		IErrorDialogFactory errorDialogFactory = mock(IErrorDialogFactory.class);
		IErrorDialog errorDialog = mock(IErrorDialog.class);
		when(errorDialogFactory.getDialog()).thenReturn(errorDialog);
		IFrame frame = mock(IFrame.class);
		
		sut.setDeviceHandler(deviceHandler);
		sut.setInputDialog(inputDialog);
		sut.setErrorDialogFactory(errorDialogFactory);
		sut.setFrame(frame);
		sut.setFreeMemory("20");
		
		sut.executeAction();

		verify(errorDialog).showErrorDialog(any(IFrame.class), anyString());
		verify(deviceHandler, never()).fillWithRandomSongs(anyLong());
	}
	
	@Test
	public void testEnabled() {
		FillDeviceWithRandomSongsAction sut = new FillDeviceWithRandomSongsAction();
		IDeviceHandler deviceHandler = mock(IDeviceHandler.class);
		when(deviceHandler.isDeviceConnected()).thenReturn(true);
		sut.setDeviceHandler(deviceHandler);
		Assert.assertTrue(sut.isEnabledForNavigationTreeSelection(false, null));
	}

	@Test
	public void testDisabled() {
		FillDeviceWithRandomSongsAction sut = new FillDeviceWithRandomSongsAction();
		IDeviceHandler deviceHandler = mock(IDeviceHandler.class);
		when(deviceHandler.isDeviceConnected()).thenReturn(false);
		sut.setDeviceHandler(deviceHandler);
		Assert.assertFalse(sut.isEnabledForNavigationTreeSelection(false, null));
	}
}
