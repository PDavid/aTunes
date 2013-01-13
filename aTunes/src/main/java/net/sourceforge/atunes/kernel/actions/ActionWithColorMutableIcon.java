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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;

import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * An action with an icon. Icon color changes with look and feel
 * 
 * @author alex
 * 
 */
public abstract class ActionWithColorMutableIcon extends CustomAbstractAction
		implements ILookAndFeelChangeListener {

	private static final long serialVersionUID = 6248901947210263210L;

	private ILookAndFeelManager lookAndFeelManager;

	/**
	 * Creates a new action
	 * 
	 * @param text
	 */
	public ActionWithColorMutableIcon(final String text) {
		super(text);
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
		this.lookAndFeelManager.addLookAndFeelChangeListener(this);
	}

	@Override
	protected void initialize() {
		super.initialize();
		lookAndFeelChanged(); // Initialize icon
	}

	/**
	 * @return
	 */
	public ILookAndFeelManager getLookAndFeelManager() {
		return this.lookAndFeelManager;
	}

	/**
	 * Returns the current look and feel
	 * 
	 * @return
	 */
	public ILookAndFeel getLookAndFeel() {
		return this.lookAndFeelManager.getCurrentLookAndFeel();
	}

	@Override
	public final void lookAndFeelChanged() {
		putValue(
				SMALL_ICON,
				getIcon(getLookAndFeel()).getIcon(
						getLookAndFeel().getPaintForSpecialControls()));
	}

	@Override
	public final void actionPerformed(final ActionEvent e) {
		super.actionPerformed(e);
	}

	/**
	 * Returns color mutable icon for given look and feel
	 * 
	 * @param lookAndFeel
	 * @return
	 */
	public abstract IColorMutableImageIcon getIcon(ILookAndFeel lookAndFeel);
}
