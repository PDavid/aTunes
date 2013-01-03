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

import java.util.ArrayList;
import java.util.Arrays;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectPropertiesDialog;
import net.sourceforge.atunes.model.IAudioObjectPropertiesDialogFactory;
import net.sourceforge.atunes.model.INavigationHandler;

import org.junit.Assert;
import org.junit.Test;

public class ShowNavigatorTableItemInfoActionTest {

    @Test
    public void test() {
	ShowNavigatorTableItemInfoAction sut = new ShowNavigatorTableItemInfoAction();
	IAudioObjectPropertiesDialogFactory audioObjectPropertiesDialogFactory = mock(IAudioObjectPropertiesDialogFactory.class);
	IAudioObjectPropertiesDialog dialog = mock(IAudioObjectPropertiesDialog.class);
	INavigationHandler navigationHandler = mock(INavigationHandler.class);
	IAudioObject ao = mock(IAudioObject.class);
	when(navigationHandler.getSelectedAudioObjectInNavigationTable())
		.thenReturn(ao);
	when(audioObjectPropertiesDialogFactory.newInstance(ao)).thenReturn(
		dialog);
	sut.setNavigationHandler(navigationHandler);
	sut.setAudioObjectPropertiesDialogFactory(audioObjectPropertiesDialogFactory);

	sut.executeAction();

	verify(dialog).showDialog();
    }

    @Test
    public void testEnabled() {
	ShowNavigatorTableItemInfoAction sut = new ShowNavigatorTableItemInfoAction();
	Assert.assertFalse(sut.isEnabledForNavigationTableSelection(null));
	Assert.assertFalse(sut
		.isEnabledForNavigationTableSelection(new ArrayList<IAudioObject>()));
	Assert.assertTrue(sut.isEnabledForNavigationTableSelection(Arrays
		.asList(mock(IAudioObject.class))));
    }
}
