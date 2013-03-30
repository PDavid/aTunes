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

import javax.swing.Action;

import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Show or hide navigation table
 * 
 * @author fleax
 * 
 */
public class ShowNavigationTableAction extends CustomAbstractAction {

	private static final long serialVersionUID = -3275592274940501407L;

	private INavigationHandler navigationHandler;

	private IStateNavigation stateNavigation;

	private Action showNavigationTreeAction;

	/**
	 * @param showNavigationTreeAction
	 */
	public void setShowNavigationTreeAction(
			final Action showNavigationTreeAction) {
		this.showNavigationTreeAction = showNavigationTreeAction;
	}

	/**
	 * @param stateNavigation
	 */
	public void setStateNavigation(final IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * Default constructor
	 */
	public ShowNavigationTableAction() {
		super(I18nUtils.getString("SHOW_NAVIGATION_TABLE"));
	}

	@Override
	protected void initialize() {
		super.initialize();
		putValue(SELECTED_KEY, this.stateNavigation.isShowNavigationTable());
		checkOptionsState(this.stateNavigation.isShowNavigationTable());
	}

	@Override
	protected void executeAction() {
		this.navigationHandler
				.showNavigationTable((Boolean) getValue(SELECTED_KEY));
		checkOptionsState((Boolean) getValue(SELECTED_KEY));
	}

	private void checkOptionsState(final boolean show) {
		// When one option is not selected, then disable the other one
		if (!show) {
			this.showNavigationTreeAction.setEnabled(false);
		} else if (!this.stateNavigation.isShowNavigationTree()) {
			this.setEnabled(false);
		} else {
			this.showNavigationTreeAction.setEnabled(true);
			this.setEnabled(true);
		}
	}
}
