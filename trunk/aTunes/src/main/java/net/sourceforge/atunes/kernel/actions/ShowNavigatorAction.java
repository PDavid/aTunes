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

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action shows or hides navigator.
 * 
 * @author fleax
 */
public class ShowNavigatorAction extends CustomAbstractAction {

	private static final long serialVersionUID = 5137162733978906000L;

	private INavigationHandler navigationHandler;

	private IStateNavigation stateNavigation;

	private IOSManager osManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
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
	public ShowNavigatorAction() {
		super(I18nUtils.getString("SHOW_NAVIGATOR"));
	}

	@Override
	protected void initialize() {
		super.initialize();
		putValue(
				ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_N,
						GuiUtils.getCtrlOrMetaActionEventMask(this.osManager)));
		putValue(SELECTED_KEY, this.stateNavigation.isShowNavigator());
	}

	@Override
	protected void executeAction() {
		this.navigationHandler.showNavigator((Boolean) getValue(SELECTED_KEY));
	}
}
