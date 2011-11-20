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

package net.sourceforge.atunes.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.utils.Logger;

/**
 * Creates and manages a cache of icons created, grouped by color
 * @author alex
 *
 */
public abstract class CachedIconFactory implements ILookAndFeelChangeListener, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 667338765631229982L;

	private static final int MAX_ELEMENTS_IN_CACHE = 5;
	
	private Map<Color, ImageIcon> cachedIcons = new HashMap<Color, ImageIcon>(MAX_ELEMENTS_IN_CACHE);
	
	private IColorMutableImageIcon colorMutableIcon;

	public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		lookAndFeelManager.addLookAndFeelChangeListener(this);
	}
	
	@Override
	public final void lookAndFeelChanged() {
		Logger.debug(this.getClass().getName(), " clearing icon cache");
		cachedIcons.clear();
	}
	
	/**
	 * Returns icon for given color
	 * @param color
	 * @return
	 */
	public final ImageIcon getIcon(Color color) {
		ImageIcon icon = cachedIcons.get(color);
		if (icon == null) {
			if (cachedIcons.size() == MAX_ELEMENTS_IN_CACHE) {
				cachedIcons.clear();
			}
			Logger.debug("Creating icon: ", this.getClass().getName(), " with color: ", color);
			icon = createIcon(color);
			cachedIcons.put(color, icon);
		}
		return icon;
	}
	
	/**
	 * Returns a color mutable icon
	 * @return
	 */
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
