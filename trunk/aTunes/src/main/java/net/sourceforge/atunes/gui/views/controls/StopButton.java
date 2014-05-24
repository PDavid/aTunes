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
 * The Class StopButton.
 */
public final class StopButton extends PlayerControlButton implements
		ILookAndFeelChangeListener, IControlButton {

	private static final long serialVersionUID = 6007885049773560874L;

	private Dimension stopMuteButtonSize;

	private IIconFactory stopIcon;

	/**
	 * @param stopIcon
	 */
	public void setStopIcon(final IIconFactory stopIcon) {
		this.stopIcon = stopIcon;
	}

	/**
	 * @param stopMuteButtonSize
	 */
	public void setStopMuteButtonSize(final Dimension stopMuteButtonSize) {
		this.stopMuteButtonSize = stopMuteButtonSize;
	}

	/**
	 * Instantiates a new stop button.
	 * 
	 * @param stopAction
	 */
	public StopButton(final Action stopAction) {
		super(stopAction);
	}

	@Override
	protected Dimension getButtonSize() {
		return this.stopMuteButtonSize;
	}

	@Override
	protected void setIcon() {
		updateIcon(this.stopIcon);
	}
}
