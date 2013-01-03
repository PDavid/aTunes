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
import javax.swing.ImageIcon;
import javax.swing.JButton;

import net.sourceforge.atunes.gui.GuiUtils;

/**
 * Play / pause button in full screen mode
 * 
 * @author alex
 * 
 */
public final class FullScreenPlayPauseButton extends JButton {

	private static final long serialVersionUID = 4348041346542204394L;

	private boolean playing;

	private final ImageIcon playIcon;

	private final ImageIcon pauseIcon;

	/**
	 * Instantiates a new play pause button.
	 * 
	 * @param size
	 * @param playIcon
	 * @param pauseIcon
	 * @param action
	 */
	public FullScreenPlayPauseButton(final Dimension size,
			final ImageIcon playIcon, final ImageIcon pauseIcon,
			final Action action) {
		super(action);
		// Force size of button
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setFocusable(false);
		setText(null);

		this.playIcon = playIcon;
		this.pauseIcon = pauseIcon;

		setOpaque(false);
		setBorderPainted(false);
		setContentAreaFilled(false);

		setIcon();
	}

	/**
	 * Sets the playing.
	 * 
	 * @param playing
	 *            the new playing
	 */
	public void setPlaying(final boolean playing) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				setPlayingState(playing);
			}
		});
	}

	private void setPlayingState(final boolean playing) {
		this.playing = playing;
		setIcon();
	}

	/**
	 * Checks if is playing.
	 * 
	 * @return true, if is playing
	 */
	public boolean isPlaying() {
		return this.playing;
	}

	private void setIcon() {
		if (this.playing) {
			setIcon(this.pauseIcon);
		} else {
			setIcon(this.playIcon);
		}
	}
}
