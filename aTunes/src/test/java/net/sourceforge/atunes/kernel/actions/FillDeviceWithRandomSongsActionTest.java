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

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IInputDialog;

import org.junit.Assert;
import org.junit.Test;

public class FillDeviceWithRandomSongsActionTest {

	@Test
	public void test() {
		FillDeviceWithRandomSongsAction sut = new FillDeviceWithRandomSongsAction();
		IDeviceHandler deviceHandler = mock(IDeviceHandler.class);
		IDialogFactory dialogFactory = mock(IDialogFactory.class);
		IInputDialog inputDialog = mock(IInputDialog.class);
		when(inputDialog.getResult()).thenReturn("20");
		when(dialogFactory.newDialog(IInputDialog.class)).thenReturn(
				inputDialog);
		IErrorDialog errorDialog = mock(IErrorDialog.class);
		when(dialogFactory.newDialog(IErrorDialog.class)).thenReturn(
				errorDialog);

		sut.setDeviceHandler(deviceHandler);
		sut.setDialogFactory(dialogFactory);
		sut.setFreeMemory("20");

		sut.executeAction();

		verify(inputDialog).showDialog();
		verify(deviceHandler).fillWithRandomSongs(20);
	}

	@Test
	public void testNull() {
		FillDeviceWithRandomSongsAction sut = new FillDeviceWithRandomSongsAction();
		IDeviceHandler deviceHandler = mock(IDeviceHandler.class);
		IDialogFactory dialogFactory = mock(IDialogFactory.class);
		IInputDialog inputDialog = mock(IInputDialog.class);
		when(inputDialog.getResult()).thenReturn(null);
		when(dialogFactory.newDialog(IInputDialog.class)).thenReturn(
				inputDialog);
		IErrorDialog errorDialog = mock(IErrorDialog.class);
		when(dialogFactory.newDialog(IErrorDialog.class)).thenReturn(
				errorDialog);

		sut.setDeviceHandler(deviceHandler);
		sut.setDialogFactory(dialogFactory);
		sut.setFreeMemory("20");

		sut.executeAction();

		verify(inputDialog).showDialog();
		verify(deviceHandler, never()).fillWithRandomSongs(anyLong());
	}

	@Test
	public void testInputError() {
		FillDeviceWithRandomSongsAction sut = new FillDeviceWithRandomSongsAction();
		IDeviceHandler deviceHandler = mock(IDeviceHandler.class);
		IDialogFactory dialogFactory = mock(IDialogFactory.class);
		IInputDialog inputDialog = mock(IInputDialog.class);
		when(dialogFactory.newDialog(IInputDialog.class)).thenReturn(
				inputDialog);
		when(inputDialog.getResult()).thenReturn("not a number");
		IErrorDialog errorDialog = mock(IErrorDialog.class);
		when(dialogFactory.newDialog(IErrorDialog.class)).thenReturn(
				errorDialog);

		sut.setDeviceHandler(deviceHandler);
		sut.setDialogFactory(dialogFactory);
		sut.setFreeMemory("20");

		sut.executeAction();

		verify(errorDialog).showErrorDialog(anyString());
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
