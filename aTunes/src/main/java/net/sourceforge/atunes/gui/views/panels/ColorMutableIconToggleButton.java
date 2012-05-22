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

package net.sourceforge.atunes.gui.views.panels;

import javax.swing.Action;

import net.sourceforge.atunes.model.IColorMutableImageIcon;

class ColorMutableIconToggleButton {
	
	private String buttonName;
	
	private IColorMutableImageIcon icon;
	
	private String tooltip;
	
	private Action action;
	
	private Object userObject;

	ColorMutableIconToggleButton(String buttonName, IColorMutableImageIcon icon, String tooltip, Action action, Object userObject) {
		super();
		this.buttonName = buttonName;
		this.icon = icon;
		this.tooltip = tooltip;
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
	public IColorMutableImageIcon getIcon() {
		return icon;
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
	public Action getAction() {
		return action;
	}
	
	/**
	 * @return
	 */
	public Object getUserObject() {
		return userObject;
	}
}