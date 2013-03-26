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

import net.sourceforge.atunes.model.IControlButton;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * Previous button
 * 
 * @author alex
 * 
 */
public final class PreviousButton extends JButton implements
		ILookAndFeelChangeListener, IControlButton {

	private static final long serialVersionUID = -5415683019365261871L;

	private ILookAndFeelManager lookAndFeelManager;

	private Dimension previousNextButtonSize;

	private IIconFactory previousIcon;

	/**
	 * @param previousIcon
	 */
	public void setPreviousIcon(final IIconFactory previousIcon) {
		this.previousIcon = previousIcon;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param previousNextButtonSize
	 */
	public void setPreviousNextButtonSize(final Dimension previousNextButtonSize) {
		this.previousNextButtonSize = previousNextButtonSize;
	}

	/**
	 * Instantiates a new previous button.
	 * 
	 * @param previousAction
	 */
	public PreviousButton(final Action previousAction) {
		super(previousAction);
	}

	/**
	 * Initialize button
	 */
	public void initialize() {
		setPreferredSize(this.previousNextButtonSize);
		setMinimumSize(this.previousNextButtonSize);
		setMaximumSize(this.previousNextButtonSize);
		setFocusable(false);
		setText(null);
		updateIcon();
		this.lookAndFeelManager.getCurrentLookAndFeel().putClientProperties(
				this);
		this.lookAndFeelManager.addLookAndFeelChangeListener(this);
	}

	@Override
	public void lookAndFeelChanged() {
		updateIcon();
	}

	private void updateIcon() {
		setIcon(this.previousIcon.getIcon(this.lookAndFeelManager
				.getCurrentLookAndFeel().getPaintForSpecialControls()));
	}
}
