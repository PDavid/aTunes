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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRepositoryHandler;

import org.junit.Test;

public class RenameAudioFileInNavigationTableActionTest {

	@Test
	public void test() {
		RenameAudioFileInNavigationTableAction sut = new RenameAudioFileInNavigationTableAction();
		IRepositoryHandler repositoryHandler = mock(IRepositoryHandler.class);
		IDialogFactory dialogFactory = mock(IDialogFactory.class);
		sut.setRepositoryHandler(repositoryHandler);
		INavigationHandler navigationHandler = mock(INavigationHandler.class);
		sut.setNavigationHandler(navigationHandler);
		sut.setDialogFactory(dialogFactory);
		IInputDialog inputDialog = mock(IInputDialog.class);
		when(dialogFactory.newDialog(IInputDialog.class)).thenReturn(
				inputDialog);
		when(inputDialog.getResult()).thenReturn("new");
		ILocalAudioObject ao = mock(ILocalAudioObject.class);
		IFileManager fileManager = mock(IFileManager.class);
		sut.setFileManager(fileManager);
		when(fileManager.getPath(ao)).thenReturn("old");
		List<IAudioObject> list = new ArrayList<IAudioObject>();
		list.add(ao);
		when(navigationHandler.getFilesSelectedInNavigator()).thenReturn(list);

		sut.executeAction();

		verify(repositoryHandler).rename(ao, "new");
	}

	@Test
	public void testNoLocalAudioObject() {
		RenameAudioFileInNavigationTableAction sut = new RenameAudioFileInNavigationTableAction();
		IRepositoryHandler repositoryHandler = mock(IRepositoryHandler.class);
		IDialogFactory dialogFactory = mock(IDialogFactory.class);
		sut.setRepositoryHandler(repositoryHandler);
		INavigationHandler navigationHandler = mock(INavigationHandler.class);
		sut.setNavigationHandler(navigationHandler);
		sut.setDialogFactory(dialogFactory);
		IInputDialog inputDialog = mock(IInputDialog.class);
		when(dialogFactory.newDialog(IInputDialog.class)).thenReturn(
				inputDialog);
		when(inputDialog.getResult()).thenReturn("new");
		IRadio radio = mock(IRadio.class);
		List<IAudioObject> list = new ArrayList<IAudioObject>();
		list.add(radio);
		when(navigationHandler.getFilesSelectedInNavigator()).thenReturn(list);

		sut.executeAction();

		verify(repositoryHandler, never()).rename(any(ILocalAudioObject.class),
				any(String.class));
	}

	@Test
	public void testNoInput() {
		RenameAudioFileInNavigationTableAction sut = new RenameAudioFileInNavigationTableAction();
		IRepositoryHandler repositoryHandler = mock(IRepositoryHandler.class);
		IDialogFactory dialogFactory = mock(IDialogFactory.class);
		sut.setRepositoryHandler(repositoryHandler);
		INavigationHandler navigationHandler = mock(INavigationHandler.class);
		sut.setNavigationHandler(navigationHandler);
		sut.setDialogFactory(dialogFactory);
		IInputDialog inputDialog = mock(IInputDialog.class);
		when(dialogFactory.newDialog(IInputDialog.class)).thenReturn(
				inputDialog);
		when(inputDialog.getResult()).thenReturn(null);
		ILocalAudioObject ao = mock(ILocalAudioObject.class);
		IFileManager fileManager = mock(IFileManager.class);
		sut.setFileManager(fileManager);
		when(fileManager.getPath(ao)).thenReturn("old");
		List<IAudioObject> list = new ArrayList<IAudioObject>();
		list.add(ao);
		when(navigationHandler.getFilesSelectedInNavigator()).thenReturn(list);

		sut.executeAction();

		verify(repositoryHandler, never()).rename(any(ILocalAudioObject.class),
				any(String.class));
	}
}
