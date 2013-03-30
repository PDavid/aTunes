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

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IStateNavigation;

import org.junit.Test;

public class ShowNavigationTableActionTest {

	@Test
	public void test() {
		ShowNavigationTableAction sut = new ShowNavigationTableAction();
		INavigationHandler navigationHandler = mock(INavigationHandler.class);
		IStateNavigation stateNavigation = mock(IStateNavigation.class);
		sut.setNavigationHandler(navigationHandler);
		sut.setStateNavigation(stateNavigation);
		sut.putValue(AbstractAction.SELECTED_KEY, true);

		sut.executeAction();

		verify(navigationHandler).showNavigationTable(true);
	}

	@Test
	public void testDisableTreeAction() {
		Action showNavigationTreeAction = mock(Action.class);
		ShowNavigationTableAction sut = new ShowNavigationTableAction();
		INavigationHandler navigationHandler = mock(INavigationHandler.class);
		IStateNavigation stateNavigation = mock(IStateNavigation.class);
		sut.setNavigationHandler(navigationHandler);
		sut.setStateNavigation(stateNavigation);
		sut.setShowNavigationTreeAction(showNavigationTreeAction);

		// Not selecting this action must disable showNavigationTree
		sut.putValue(AbstractAction.SELECTED_KEY, false);

		sut.executeAction();

		verify(showNavigationTreeAction).setEnabled(false);
	}

	@Test
	public void testEnableTreeAction() {
		Action showNavigationTreeAction = mock(Action.class);
		ShowNavigationTableAction sut = new ShowNavigationTableAction();
		INavigationHandler navigationHandler = mock(INavigationHandler.class);
		IStateNavigation stateNavigation = mock(IStateNavigation.class);
		when(stateNavigation.isShowNavigationTree()).thenReturn(true);
		sut.setNavigationHandler(navigationHandler);
		sut.setStateNavigation(stateNavigation);
		sut.setShowNavigationTreeAction(showNavigationTreeAction);

		// Selecting this action must enable showNavigationTree if selected too
		sut.putValue(AbstractAction.SELECTED_KEY, true);

		sut.executeAction();

		verify(showNavigationTreeAction).setEnabled(true);
	}
}
