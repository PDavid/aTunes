/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.KeyStroke;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

public class PlayerMenu extends JMenu {

	private final class PlayActionFromMenuBar extends AbstractAction {
		private static final long serialVersionUID = -2392752580476710618L;

		private PlayActionFromMenuBar() {
			super(I18nUtils.getString("PLAY"));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, GuiUtils.getCtrlOrMetaActionEventMask()));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Need this action to pass null event
			playAction.actionPerformed(null);
		}
	}

	private static final long serialVersionUID = -3624790857729577320L;

	private Action playAction;
	private Action nextAction;
	private Action previousAction;
	private Action stopAction;
	
	/**
	 * @param i18nKey
	 */
	public PlayerMenu(String i18nKey) {
		super(I18nUtils.getString(i18nKey));
	}
	
	/**
	 * @param stopAction
	 */
	public void setStopAction(Action stopAction) {
		this.stopAction = stopAction;
	}

	/**
	 * @param playAction
	 */
	public void setPlayAction(Action playAction) {
		this.playAction = playAction;
	}
	
	/**
	 * @param nextAction
	 */
	public void setNextAction(Action nextAction) {
		this.nextAction = nextAction;
	}
	
	/**
	 * @param previousAction
	 */
	public void setPreviousAction(Action previousAction) {
		this.previousAction = previousAction;
	}
	
	/**
	 * Initializes menu
	 */
	public void initialize() {
		add(new PlayActionFromMenuBar());
		add(nextAction);
		add(previousAction);
		add(stopAction);
	}
}
