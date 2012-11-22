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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.Dimension;

import javax.swing.Action;
import javax.swing.JButton;

import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

public final class NextButton extends JButton implements
		ILookAndFeelChangeListener {

	private static final long serialVersionUID = -4939372038840047335L;

	private Dimension previousNextButtonSize;

	private ILookAndFeelManager lookAndFeelManager;

	private IIconFactory nextIcon;

	/**
	 * @param nextIcon
	 */
	public void setNextIcon(IIconFactory nextIcon) {
		this.nextIcon = nextIcon;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param previousNextButtonSize
	 */
	public void setPreviousNextButtonSize(Dimension previousNextButtonSize) {
		this.previousNextButtonSize = previousNextButtonSize;
	}

	/**
	 * Instantiates a new next button. /**
	 * 
	 * @param action
	 */
	public NextButton(Action nextAction) {
		super(nextAction);
	}

	/**
	 * Initialize button
	 */
	public void initialize() {
		setPreferredSize(previousNextButtonSize);
		setMinimumSize(previousNextButtonSize);
		setMaximumSize(previousNextButtonSize);
		setFocusable(false);
		setText(null);
		updateIcon();
		lookAndFeelManager.getCurrentLookAndFeel().putClientProperties(this);
		lookAndFeelManager.addLookAndFeelChangeListener(this);
	}

	@Override
	public void lookAndFeelChanged() {
		updateIcon();
	}

	private void updateIcon() {
		setIcon(nextIcon.getIcon(lookAndFeelManager.getCurrentLookAndFeel()
				.getPaintForSpecialControls()));
	}
}
