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

import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.IPlayList;

/**
 * Selects icon for play list
 * 
 * @author alex
 * 
 */
public class PlayListIconSelector {

	private IIconFactory playListIcon;

	private IIconFactory dynamicPlayListIcon;

	/**
	 * @param playListIcon
	 */
	public void setPlayListIcon(final IIconFactory playListIcon) {
		this.playListIcon = playListIcon;
	}

	/**
	 * @param dynamicPlayListIcon
	 */
	public void setDynamicPlayListIcon(final IIconFactory dynamicPlayListIcon) {
		this.dynamicPlayListIcon = dynamicPlayListIcon;
	}

	IColorMutableImageIcon getIcon(final IPlayList playList) {
		if (playList instanceof DynamicPlayList) {
			return this.dynamicPlayListIcon.getColorMutableIcon();
		} else {
			return this.playListIcon.getColorMutableIcon();
		}
	}
}
