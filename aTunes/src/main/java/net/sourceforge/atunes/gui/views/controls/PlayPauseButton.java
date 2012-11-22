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

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.images.PauseImageIcon;
import net.sourceforge.atunes.gui.images.PlayImageIcon;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * @author xe01965
 * 
 */
public final class PlayPauseButton extends JButton implements
		ILookAndFeelChangeListener {

	private static final long serialVersionUID = 4348041346542204394L;

	private boolean playing;

	private ILookAndFeelManager lookAndFeelManager;

	private Dimension playButtonSize;

	private PauseImageIcon pauseIcon;

	private PlayImageIcon playIcon;

	/**
	 * @param pauseIcon
	 */
	public void setPauseIcon(PauseImageIcon pauseIcon) {
		this.pauseIcon = pauseIcon;
	}

	/**
	 * @param playIcon
	 */
	public void setPlayIcon(PlayImageIcon playIcon) {
		this.playIcon = playIcon;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * Instantiates a new play pause button.
	 * 
	 * @param playAction
	 */
	public PlayPauseButton(Action playAction) {
		super(playAction);
	}

	/**
	 * Initialize button
	 */
	public void initialize() {
		// Force size of button
		setPreferredSize(playButtonSize);
		setMinimumSize(playButtonSize);
		setMaximumSize(playButtonSize);
		setFocusable(false);
		setText(null);

		setIcon();

		lookAndFeelManager.getCurrentLookAndFeel().putClientProperties(this);
		lookAndFeelManager.addLookAndFeelChangeListener(this);
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

	private void setPlayingState(boolean playing) {
		this.playing = playing;
		setIcon();
	}

	/**
	 * Checks if is playing.
	 * 
	 * @return true, if is playing
	 */
	public boolean isPlaying() {
		return playing;
	}

	@Override
	public void lookAndFeelChanged() {
		setIcon();
	}

	private void setIcon() {
		if (playing) {
			pauseIcon.setSize(playButtonSize);
			setIcon(pauseIcon.getIcon(lookAndFeelManager
					.getCurrentLookAndFeel().getPaintForSpecialControls()));
		} else {
			playIcon.setSize(playButtonSize);
			setIcon(playIcon.getIcon(lookAndFeelManager.getCurrentLookAndFeel()
					.getPaintForSpecialControls()));
		}
	}
}
