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

package net.sourceforge.atunes.gui.images;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IIconCache;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.utils.Logger;

/**
 * Creates and manages a cache of icons created, grouped by color
 * @author alex
 *
 */
public abstract class CachedIconFactory implements Serializable, IIconFactory {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 667338765631229982L;

	private IColorMutableImageIcon colorMutableIcon;
	
	private IIconCache iconCache;
	
	/**
	 * @param iconCache
	 */
	public void setIconCache(IIconCache iconCache) {
		this.iconCache = iconCache;
	}
	
	/**
	 * Returns icon for given color
	 * @param color
	 * @return
	 */
	@Override
	public final ImageIcon getIcon(Color color) {
		ImageIcon icon = iconCache.readIcon(this, color);
		if (icon == null) {
			Logger.debug("Creating icon: ", this.getClass().getName(), " with color: ", color);
			icon = createIcon(color);
			iconCache.storeIcon(this, color, icon);
		}
		return icon;
	}

	/**
	 * Returns a color mutable icon
	 * @return
	 */
	@Override
	public final IColorMutableImageIcon getColorMutableIcon() {
		if (colorMutableIcon == null) {
			colorMutableIcon = new ColorMutableIconFromCachedIconFactory(this);
		}
		return colorMutableIcon;
	}
	
	/**
	 * Creates icon for given color
	 * @param color
	 * @return
	 */
	protected abstract ImageIcon createIcon(Color color);

}
