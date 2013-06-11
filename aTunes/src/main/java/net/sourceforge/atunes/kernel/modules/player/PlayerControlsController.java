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

package net.sourceforge.atunes.kernel.modules.player;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.VolumeSlider;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IPlayerControlsPanel;

final class PlayerControlsController extends
		AbstractSimpleController<PlayerControlsPanel> {

	private IPlayerControlsPanel playerControls;

	private VolumeSlider volumeSlider;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param volumeSlider
	 */
	public void setVolumeSlider(final VolumeSlider volumeSlider) {
		this.volumeSlider = volumeSlider;
	}

	/**
	 * @param playerControls
	 */
	public void setPlayerControls(final IPlayerControlsPanel playerControls) {
		this.playerControls = playerControls;
	}

	/**
	 * Instantiates a new player controls controller.
	 * 
	 * @param panel
	 * @param state
	 * @param playerHandler
	 */
	public void initialize() {
		setComponentControlled((PlayerControlsPanel) this.playerControls
				.getSwingComponent());
		addBindings();
		addStateBindings();
	}

	@Override
	public void addBindings() {
		ProgressBarSeekListener seekListener = this.beanFactory
				.getBean(ProgressBarSeekListener.class);
		seekListener.bindToProgressBar(getComponentControlled()
				.getProgressSlider());
		// Add volume behavior
		this.volumeSlider.addMouseWheelListener(this.beanFactory
				.getBean(VolumeSliderMouseWheelListener.class));
		this.volumeSlider.addMouseListener(this.beanFactory
				.getBean(VolumeSliderChangeListener.class));
	}

	/**
	 * Sets the max duration.
	 * 
	 * @param length
	 *            the new length
	 */
	void setAudioObjectLength(final long length) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				getComponentControlled().getProgressSlider().setMaximum(
						(int) length);
			}
		});
	}

	/**
	 * Sets the playing.
	 * 
	 * @param playing
	 *            the new playing
	 */
	void setPlaying(final boolean playing) {
		getComponentControlled().setPlaying(playing);
	}

	/**
	 * Sets the time.
	 * 
	 * @param timePlayed
	 * @param totalTime
	 * @param fading
	 */
	void setCurrentAudioObjectTimePlayed(final long timePlayed,
			final long totalTime, final boolean fading) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				setCurrentAudioObjectTimePlayedEDT(timePlayed, totalTime,
						fading);
			}
		});
	}

	private void setCurrentAudioObjectTimePlayedEDT(final long timePlayed,
			final long totalTime, final boolean fading) {
		long remainingTime = totalTime > 0 ? totalTime - timePlayed : 0;
		getComponentControlled().setProgress(timePlayed,
				timePlayed == 0 ? 0 : remainingTime, fading);
		getComponentControlled().getProgressSlider().setValue((int) timePlayed);
	}

	/**
	 * Sets the volume.
	 * 
	 * @param value
	 *            the new volume
	 */
	void setVolume(final int value) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				getComponentControlled().setVolume(value);
			}
		});
	}
}
