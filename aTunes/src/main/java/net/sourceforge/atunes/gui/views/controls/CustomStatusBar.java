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

import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

import org.jdesktop.swingx.JXStatusBar;

/**
 * Custom JXStatusBar
 * 
 * @author alex
 * 
 */
public class CustomStatusBar extends JXStatusBar implements
		ILookAndFeelChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2037689111184073171L;

	private final ILookAndFeelManager lookAndFeelManager;

	/**
	 * @param lookAndFeelManager
	 */
	public CustomStatusBar(final ILookAndFeelManager lookAndFeelManager) {
		super();
		this.lookAndFeelManager = lookAndFeelManager;
		this.lookAndFeelManager.addLookAndFeelChangeListener(this);
		this.lookAndFeelManager.getCurrentLookAndFeel()
				.customizeStatusBar(this);
	}

	@Override
	public void lookAndFeelChanged() {
		this.lookAndFeelManager.getCurrentLookAndFeel()
				.customizeStatusBar(this);
	}
}
