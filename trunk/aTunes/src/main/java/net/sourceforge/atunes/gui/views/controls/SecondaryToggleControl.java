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

import javax.swing.JToggleButton;

import net.sourceforge.atunes.kernel.actions.ActionWithColorMutableIcon;
import net.sourceforge.atunes.kernel.actions.ActionWithColorMutableIcon.IIconChangeHandler;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * A player toggle control for secondary functions
 * 
 * @author alex
 * 
 */
public class SecondaryToggleControl extends JToggleButton implements
		IIconChangeHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -124604413114002586L;

	private ILookAndFeelManager lookAndFeelManager;

	private final ActionWithColorMutableIcon action;

	/**
	 * @param a
	 * @param preferredSize
	 */
	public SecondaryToggleControl(final ActionWithColorMutableIcon a) {
		super(a);
		this.action = a;
		setText(null);
		setFocusable(false);
		this.action.registerIconChangeHandler(this);
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
		lookAndFeelManager.getCurrentLookAndFeel().putClientProperties(this);
	}

	@Override
	public void updateIcon(final IColorMutableImageIcon icon) {
		setIcon(icon.getIcon(this.lookAndFeelManager.getCurrentLookAndFeel()
				.getPaintForSpecialControls()));
		setRolloverIcon(icon.getIcon(this.lookAndFeelManager
				.getCurrentLookAndFeel().getPaintForSpecialControlsRollover()));
		setSelectedIcon(icon.getIcon(this.lookAndFeelManager
				.getCurrentLookAndFeel().getPaintForSpecialControlsRollover()));
	}
}
