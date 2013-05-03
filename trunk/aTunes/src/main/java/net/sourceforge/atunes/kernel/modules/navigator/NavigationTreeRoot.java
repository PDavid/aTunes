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

package net.sourceforge.atunes.kernel.modules.navigator;

import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.INavigationTreeRoot;

/**
 * Root of a navigation tree
 * 
 * @author alex
 * 
 */
public class NavigationTreeRoot implements INavigationTreeRoot {

	private String text;

	private IColorMutableImageIcon icon;

	/**
	 * @param text
	 */
	public NavigationTreeRoot(String text, IColorMutableImageIcon icon) {
		this.text = text;
		this.icon = icon;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public IColorMutableImageIcon getIcon() {
		return this.icon;
	}
}
