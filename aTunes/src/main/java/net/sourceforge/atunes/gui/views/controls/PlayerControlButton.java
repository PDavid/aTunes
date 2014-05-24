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

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JButton;

import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * Player control button
 * 
 * @author alex
 * 
 */
public abstract class PlayerControlButton extends JButton implements
		ILookAndFeelChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3249835769674931918L;
	private ILookAndFeelManager lookAndFeelManager;

	/**
	 * @param action
	 */
	public PlayerControlButton(final Action action) {
		super(action);
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	protected void updateIcon(final IIconFactory icon) {
		setIcon(icon.getIcon(this.lookAndFeelManager.getCurrentLookAndFeel()
				.getPaintForSpecialControls()));
		setRolloverIcon(icon.getIcon(this.lookAndFeelManager
				.getCurrentLookAndFeel().getPaintForSpecialControlsRollover()));
	}

	@Override
	public void lookAndFeelChanged() {
		setIcon();
	}

	/**
	 * Initializes button
	 */
	public void initialize() {
		// Force size of button
		setPreferredSize(getButtonSize());
		setMinimumSize(getButtonSize());
		setMaximumSize(getButtonSize());
		setFocusable(false);
		setText(null);

		setIcon();

		this.lookAndFeelManager.getCurrentLookAndFeel().putClientProperties(
				this);
		this.lookAndFeelManager.addLookAndFeelChangeListener(this);
	}

	protected abstract Dimension getButtonSize();

	protected abstract void setIcon();
}
