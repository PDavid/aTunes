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

import net.sourceforge.atunes.model.IControlButton;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;

/**
 * Next button
 * 
 * @author alex
 * 
 */
public final class NextButton extends PlayerControlButton implements
		ILookAndFeelChangeListener, IControlButton {

	private static final long serialVersionUID = -4939372038840047335L;

	private Dimension previousNextButtonSize;

	private IIconFactory nextIcon;

	/**
	 * @param nextIcon
	 */
	public void setNextIcon(final IIconFactory nextIcon) {
		this.nextIcon = nextIcon;
	}

	/**
	 * @param previousNextButtonSize
	 */
	public void setPreviousNextButtonSize(final Dimension previousNextButtonSize) {
		this.previousNextButtonSize = previousNextButtonSize;
	}

	/**
	 * Instantiates a new next button.
	 * 
	 * @param nextAction
	 */
	public NextButton(final Action nextAction) {
		super(nextAction);
	}

	@Override
	protected Dimension getButtonSize() {
		return this.previousNextButtonSize;
	}

	@Override
	public void setIcon() {
		updateIcon(this.nextIcon);
	}
}
