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

package net.sourceforge.atunes.kernel.modules.os;

import java.awt.MenuItem;
import java.awt.PopupMenu;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * Menu of dock icon
 * @author alex
 *
 */
public class MacOSXDockMenu {

	private Action playAction;
	
	private Action stopAction;
	
	private Action previousAction;
	
	private Action nextAction;
	
	/**
	 * @param playAction
	 */
	public void setPlayAction(Action playAction) {
		this.playAction = playAction;
	}
	
	/**
	 * @param stopAction
	 */
	public void setStopAction(Action stopAction) {
		this.stopAction = stopAction;
	}

	/**
	 * @param previousAction
	 */
	public void setPreviousAction(Action previousAction) {
		this.previousAction = previousAction;
	}
	
	/**
	 * @param nextAction
	 */
	public void setNextAction(Action nextAction) {
		this.nextAction = nextAction;
	}
	
    /**
     * Returns menu for dock icon
     * @return
     */
    PopupMenu getDockMenu() {
    	PopupMenu menu = new PopupMenu();
    	menu.add(getMenuItemForAction(playAction));
    	menu.add(getMenuItemForAction(stopAction));
    	menu.add(getMenuItemForAction(previousAction));
    	menu.add(getMenuItemForAction(nextAction));
    	return menu;
    }
    
    /**
     * Returns a MenuItem that executes given action class
     * @param action
     * @return
     */
    private MenuItem getMenuItemForAction(final Action action) {
    	MenuItem menuItem = new MenuItem((String)action.getValue(AbstractAction.NAME));
    	menuItem.addActionListener(new MacOSXDockMenuItemActionListener(action));
    	return menuItem;
    }    
}
