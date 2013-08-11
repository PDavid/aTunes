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

package net.sourceforge.atunes.kernel.modules.playlist;

import javax.swing.ImageIcon;

/**
 * Object of play list combo
 * 
 * @author alex
 * 
 */
class PlayListComboModelObject {

	private final String name;

	private final ImageIcon icon;

	/**
	 * @param name
	 * @param icon
	 */
	PlayListComboModelObject(final String name, final ImageIcon icon) {
		this.name = name;
		this.icon = icon;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the icon
	 */
	public ImageIcon getIcon() {
		return this.icon;
	}
}