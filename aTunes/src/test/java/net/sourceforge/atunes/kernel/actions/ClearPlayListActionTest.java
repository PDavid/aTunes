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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IPlayListHandler;

import org.junit.Test;

public class ClearPlayListActionTest {

	@Test
	public void testYes() {
		ClearPlayListAction sut = new ClearPlayListAction();
		IPlayListHandler playListHandler = mock(IPlayListHandler.class);
		final IConfirmationDialog dialog = mock(IConfirmationDialog.class);
		when(dialog.userAccepted()).thenReturn(true);
		IDialogFactory dialogFactory = mock(IDialogFactory.class);
		when(dialogFactory.newDialog(IConfirmationDialog.class)).thenReturn(dialog);
		sut.setDialogFactory(dialogFactory);
		sut.setPlayListHandler(playListHandler);
		
		sut.executeAction();
		
		verify(playListHandler).clearPlayList();
	}

	@Test
	public void testNo() {
		ClearPlayListAction sut = new ClearPlayListAction();
		IPlayListHandler playListHandler = mock(IPlayListHandler.class);
		final IConfirmationDialog dialog = mock(IConfirmationDialog.class);
		when(dialog.userAccepted()).thenReturn(false);
		IDialogFactory dialogFactory = mock(IDialogFactory.class);
		when(dialogFactory.newDialog(IConfirmationDialog.class)).thenReturn(dialog);
		sut.setDialogFactory(dialogFactory);
		sut.setPlayListHandler(playListHandler);
		
		sut.executeAction();
		
		verify(playListHandler, never()).clearPlayList();
	}
}
