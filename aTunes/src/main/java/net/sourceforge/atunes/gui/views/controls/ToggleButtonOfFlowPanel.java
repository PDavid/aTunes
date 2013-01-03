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

package net.sourceforge.atunes.gui.views.controls;

import javax.swing.Action;

import net.sourceforge.atunes.model.IColorMutableImageIcon;

class ToggleButtonOfFlowPanel {
	
	private String buttonName;
	
	private IColorMutableImageIcon icon;

	private String tooltip;
	
	private Action action;
	
	private Object userObject;

	/**
	 * @param buttonName
	 * @param tooltip
	 * @param icon
	 * @param action
	 * @param userObject
	 */
	ToggleButtonOfFlowPanel(String buttonName, String tooltip, IColorMutableImageIcon icon, Action action, Object userObject) {
		super();
		this.buttonName = buttonName;
		this.tooltip = tooltip;
		this.icon = icon;
		this.action = action;
		this.userObject = userObject;
	}
	
	/**
	 * @return
	 */
	public String getButtonName() {
		return buttonName;
	}
	
	/**
	 * @return
	 */
	public String getTooltip() {
		return tooltip;
	}
	
	/**
	 * @return
	 */
	public IColorMutableImageIcon getIcon() {
		return icon;
	}
	
	/**
	 * @return
	 */
	public Action getAction() {
		return action;
	}
	
	/**
	 * @return
	 */
	public Object getUserObject() {
		return userObject;
	}
	
	/**
	 * @param buttonName
	 */
	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}
}