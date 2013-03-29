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

import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Show or hide navigation table
 * 
 * @author fleax
 * 
 */
public class ShowNavigationTableFilterAction extends CustomAbstractAction {

	private static final long serialVersionUID = -3275592274940501407L;

	private INavigationHandler navigationHandler;

	private IStateNavigation stateNavigation;

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
	public ShowNavigationTableFilterAction() {
		super(I18nUtils.getString("SHOW_NAVIGATION_TABLE_FILTER"));
	}

	@Override
	protected void initialize() {
		super.initialize();
		putValue(SELECTED_KEY, stateNavigation.isShowNavigationTableFilter());
	}

	@Override
	protected void executeAction() {
		navigationHandler
				.showNavigationTableFilter((Boolean) getValue(SELECTED_KEY));
	}
}
