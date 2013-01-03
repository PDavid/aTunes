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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.IPlayListHandler;

import org.junit.Test;

public class RenamePlaylistActionTest {

	@Test
	public void test() {
		RenamePlaylistAction sut = new RenamePlaylistAction();
		IPlayListHandler playListHandler = mock(IPlayListHandler.class);
		sut.setPlayListHandler(playListHandler);

		IDialogFactory dialogFactory = mock(IDialogFactory.class);
		IInputDialog dialog = mock(IInputDialog.class);
		when(dialogFactory.newDialog(IInputDialog.class)).thenReturn(dialog);
		sut.setDialogFactory(dialogFactory);

		sut.executeAction();

		verify(playListHandler).renameCurrentVisiblePlayList(any(String.class));
	}
}
