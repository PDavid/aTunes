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

package net.sourceforge.atunes.gui.views.menus;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * "View" menu
 * 
 * @author alex
 * 
 */
public class ViewMenu extends JMenu {

	private static final long serialVersionUID = -3624790857729577320L;

	private INavigationHandler navigationHandler;

	private Action showStatusBarAction;
	private Action showNavigatorAction;
	private Action showContextAction;
	private Action toggleOSDSettingAction;
	private Action fullScreenAction;

	/**
	 * @param i18nKey
	 */
	public ViewMenu(final String i18nKey) {
		super(I18nUtils.getString(i18nKey));
	}

	/**
	 * @param showStatusBarAction
	 */
	public void setShowStatusBarAction(final Action showStatusBarAction) {
		this.showStatusBarAction = showStatusBarAction;
	}

	/**
	 * @param showNavigatorAction
	 */
	public void setShowNavigatorAction(Action showNavigatorAction) {
		this.showNavigatorAction = showNavigatorAction;
	}

	/**
	 * @param showContextAction
	 */
	public void setShowContextAction(final Action showContextAction) {
		this.showContextAction = showContextAction;
	}

	/**
	 * @param toggleOSDSettingAction
	 */
	public void setToggleOSDSettingAction(final Action toggleOSDSettingAction) {
		this.toggleOSDSettingAction = toggleOSDSettingAction;
	}

	/**
	 * @param fullScreenAction
	 */
	public void setFullScreenAction(final Action fullScreenAction) {
		this.fullScreenAction = fullScreenAction;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * Initializes menu
	 */
	public void initialize() {
		// Add dinamically actions to show each navigation view loaded
		int acceleratorIndex = 1;
		for (INavigationView navigationView : navigationHandler
				.getNavigationViews()) {
			Action action = navigationView.getActionToShowView();
			// The first 9 views will have an accelerator key ALT + index
			if (acceleratorIndex < 10) {
				action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
						KeyEvent.VK_0 + acceleratorIndex, ActionEvent.ALT_MASK));
			}
			acceleratorIndex++;
			add(action);
		}

		add(new JSeparator());
		add(new JCheckBoxMenuItem(showStatusBarAction));
		add(new JCheckBoxMenuItem(showNavigatorAction));
		add(new JCheckBoxMenuItem(showContextAction));
		add(new JCheckBoxMenuItem(toggleOSDSettingAction));
		add(new JSeparator());
		add(fullScreenAction);
	}
}
