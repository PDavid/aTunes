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

import java.util.Arrays;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectPropertiesDialog;
import net.sourceforge.atunes.model.IAudioObjectPropertiesDialogFactory;
import net.sourceforge.atunes.model.IPlayListHandler;

import org.junit.Test;

public class ShowPlayListItemInfoActionTest {

    @Test
    public void test() {
	ShowPlayListItemInfoAction sut = new ShowPlayListItemInfoAction();
	IPlayListHandler playListHandler = mock(IPlayListHandler.class);
	sut.setPlayListHandler(playListHandler);
	IAudioObject ao = mock(IAudioObject.class);
	when(playListHandler.getSelectedAudioObjects()).thenReturn(
		Arrays.asList(ao));
	IAudioObjectPropertiesDialogFactory audioObjectPropertiesDialogFactory = mock(IAudioObjectPropertiesDialogFactory.class);
	sut.setAudioObjectPropertiesDialogFactory(audioObjectPropertiesDialogFactory);
	IAudioObjectPropertiesDialog dialog = mock(IAudioObjectPropertiesDialog.class);
	when(audioObjectPropertiesDialogFactory.newInstance(ao)).thenReturn(
		dialog);

	sut.executeAction();

	verify(dialog).showDialog();
    }
}
