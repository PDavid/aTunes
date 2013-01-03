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

import javax.swing.Action;

import net.sourceforge.atunes.model.IRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class RepositoryActionsHelperTest {

	private RepositoryActionsHelper sut;
	private Action addFolderToRepositoryAction;
	private Action refreshRepositoryAction;
	private Action importToRepositoryAction;
	private Action connectDeviceAction;
	private Action ripCDAction;
	private Action refreshFolderFromNavigatorAction;

	@Before
	public void init() {
		sut = new RepositoryActionsHelper();
		addFolderToRepositoryAction = Mockito.mock(Action.class);
		refreshRepositoryAction = Mockito.mock(Action.class);
		importToRepositoryAction = Mockito.mock(Action.class);
		connectDeviceAction = Mockito.mock(Action.class);
		ripCDAction = Mockito.mock(Action.class);
		refreshFolderFromNavigatorAction = Mockito.mock(Action.class);
		sut.setAddFolderToRepositoryAction(addFolderToRepositoryAction);
		sut.setConnectDeviceAction(connectDeviceAction);
		sut.setImportToRepositoryAction(importToRepositoryAction);
		sut.setRefreshFolderFromNavigatorAction(refreshFolderFromNavigatorAction);
		sut.setRefreshRepositoryAction(refreshRepositoryAction);
		sut.setRipCDAction(ripCDAction);
	}

	@Test
	public void testDisable() {
		sut.disableAllRepositoryActions();

		Mockito.verify(addFolderToRepositoryAction).setEnabled(false);
		Mockito.verify(refreshRepositoryAction).setEnabled(false);
		Mockito.verify(importToRepositoryAction).setEnabled(false);
		Mockito.verify(connectDeviceAction).setEnabled(false);
		Mockito.verify(ripCDAction).setEnabled(false);
		Mockito.verify(refreshFolderFromNavigatorAction).setEnabled(false);
	}

	@Test
	public void testEnable() {
		sut.enableRepositoryActions();

		Mockito.verify(addFolderToRepositoryAction).setEnabled(true);
		Mockito.verify(refreshRepositoryAction).setEnabled(true);
		Mockito.verify(importToRepositoryAction).setEnabled(true);
		Mockito.verify(connectDeviceAction).setEnabled(true);
		Mockito.verify(ripCDAction).setEnabled(true);
		Mockito.verify(refreshFolderFromNavigatorAction).setEnabled(true);
	}

	@Test
	public void testOnlyAllowSelectFolder() {
		sut.onlyAllowAddFolderToRepository();

		Mockito.verify(addFolderToRepositoryAction).setEnabled(true);
		Mockito.verify(refreshRepositoryAction).setEnabled(false);
		Mockito.verify(importToRepositoryAction).setEnabled(false);
		Mockito.verify(connectDeviceAction).setEnabled(false);
		Mockito.verify(ripCDAction).setEnabled(false);
		Mockito.verify(refreshFolderFromNavigatorAction).setEnabled(false);
	}

	@Test
	public void testEnableActionsDependingOnRepositoryNull() {
		sut.enableActionsDependingOnRepository(null);

		Mockito.verify(addFolderToRepositoryAction).setEnabled(true);
		Mockito.verify(refreshRepositoryAction).setEnabled(false);
		Mockito.verify(importToRepositoryAction).setEnabled(false);
		Mockito.verify(connectDeviceAction).setEnabled(false);
		Mockito.verify(ripCDAction).setEnabled(false);
		Mockito.verify(refreshFolderFromNavigatorAction).setEnabled(false);
	}

	@Test
	public void testEnableActionsDependingOnRepositoryVoid() {
		sut.enableActionsDependingOnRepository(new VoidRepository());

		Mockito.verify(addFolderToRepositoryAction).setEnabled(true);
		Mockito.verify(refreshRepositoryAction).setEnabled(false);
		Mockito.verify(importToRepositoryAction).setEnabled(false);
		Mockito.verify(connectDeviceAction).setEnabled(false);
		Mockito.verify(ripCDAction).setEnabled(false);
		Mockito.verify(refreshFolderFromNavigatorAction).setEnabled(false);
	}

	@Test
	public void testEnableActionsDependingOnRepository() {
		sut.enableActionsDependingOnRepository(Mockito.mock(IRepository.class));

		Mockito.verify(addFolderToRepositoryAction).setEnabled(true);
		Mockito.verify(refreshRepositoryAction).setEnabled(true);
		Mockito.verify(importToRepositoryAction).setEnabled(true);
		Mockito.verify(connectDeviceAction).setEnabled(true);
		Mockito.verify(ripCDAction).setEnabled(true);
		Mockito.verify(refreshFolderFromNavigatorAction).setEnabled(true);
	}
}
