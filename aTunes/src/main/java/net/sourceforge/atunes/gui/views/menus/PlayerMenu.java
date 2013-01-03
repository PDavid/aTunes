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

import javax.swing.Action;
import javax.swing.JMenu;

import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * "Player" menu
 * 
 * @author alex
 * 
 */
public class PlayerMenu extends JMenu {

	private static final long serialVersionUID = -3624790857729577320L;

	private Action playAction;
	private Action nextAction;
	private Action previousAction;
	private Action stopAction;

	private IOSManager osManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param i18nKey
	 */
	public PlayerMenu(final String i18nKey) {
		super(I18nUtils.getString(i18nKey));
	}

	/**
	 * @param stopAction
	 */
	public void setStopAction(final Action stopAction) {
		this.stopAction = stopAction;
	}

	/**
	 * @param playAction
	 */
	public void setPlayAction(final Action playAction) {
		this.playAction = playAction;
	}

	/**
	 * @param nextAction
	 */
	public void setNextAction(final Action nextAction) {
		this.nextAction = nextAction;
	}

	/**
	 * @param previousAction
	 */
	public void setPreviousAction(final Action previousAction) {
		this.previousAction = previousAction;
	}

	/**
	 * Initializes menu
	 */
	public void initialize() {
		add(new PlayActionFromMenuBar(this.playAction, this.osManager));
		add(this.nextAction);
		add(this.previousAction);
		add(this.stopAction);
	}
}
