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

package net.sourceforge.atunes.gui;

import java.awt.Component;

import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IState;

public abstract class AbstractTreeCellDecorator {
	
	protected IState state;
	
	protected ILookAndFeelManager lookAndFeelManager;
	
	/**
	 * @param state
	 */
	public void setState(IState state) {
		this.state = state;
	}
	
	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
	
	/**
	 * @return current look and feel
	 */
	protected ILookAndFeel getLookAndFeel() {
		return lookAndFeelManager.getCurrentLookAndFeel();
	}
	
    /**
     * Decorates a tree cell component in some way given the user object
     * @param component
     * @param userObject
     * @param isSelected
     * @return
     */
    public abstract Component decorateTreeCellComponent(Component component, Object userObject, boolean isSelected);

}
