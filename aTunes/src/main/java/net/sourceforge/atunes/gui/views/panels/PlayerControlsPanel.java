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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import net.sourceforge.atunes.gui.views.controls.MuteButton;
import net.sourceforge.atunes.gui.views.controls.NextButton;
import net.sourceforge.atunes.gui.views.controls.PlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.PreviousButton;
import net.sourceforge.atunes.gui.views.controls.SecondaryControl;
import net.sourceforge.atunes.gui.views.controls.SecondaryToggleControl;
import net.sourceforge.atunes.gui.views.controls.StopButton;
import net.sourceforge.atunes.gui.views.controls.VolumeSlider;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayerControlsPanel;
import net.sourceforge.atunes.model.IProgressSlider;

/**
 * The player controls panel.
 */
/**
 * @author alex
 * 
 */
public final class PlayerControlsPanel extends JPanel implements
		IPlayerControlsPanel {

	private static final long serialVersionUID = -8647737014195638177L;

	private SecondaryControl equalizerButton;
	private SecondaryToggleControl normalizeButton;
	private SecondaryToggleControl similarModeButton;
	private PlayPauseButton playButton;
	private VolumeSlider volumeSlider;
	private JPanel secondaryControls;

	private ILookAndFeelManager lookAndFeelManager;

	private IProgressSlider playerControlsProgressSlider;

	private MuteButton volumeButton;

	private SecondaryPlayerControlsBuilder secondaryPlayerControlsBuilder;

	private Dimension secondaryControlSize;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * Instantiates a new player controls panel.
	 */
	public PlayerControlsPanel() {
		super(new GridBagLayout());
	}

	/**
	 * @param secondaryControlSize
	 */
	public void setSecondaryControlSize(final Dimension secondaryControlSize) {
		this.secondaryControlSize = secondaryControlSize;
	}

	/**
	 * @param secondaryPlayerControlsBuilder
	 */
	public void setSecondaryPlayerControlsBuilder(
			final SecondaryPlayerControlsBuilder secondaryPlayerControlsBuilder) {
		this.secondaryPlayerControlsBuilder = secondaryPlayerControlsBuilder;
	}

	/**
	 * @param equalizerButton
	 */
	public void setEqualizerButton(final SecondaryControl equalizerButton) {
		this.equalizerButton = equalizerButton;
	}

	/**
	 * @param normalizeButton
	 */
	public void setNormalizeButton(final SecondaryToggleControl normalizeButton) {
		this.normalizeButton = normalizeButton;
	}

	/**
	 * @param similarModeButton
	 */
	public void setSimilarModeButton(
			final SecondaryToggleControl similarModeButton) {
		this.similarModeButton = similarModeButton;
	}

	/**
	 * @param volumeSlider
	 */
	public void setVolumeSlider(final VolumeSlider volumeSlider) {
		this.volumeSlider = volumeSlider;
	}

	/**
	 * @param volumeButton
	 */
	public void setVolumeButton(final MuteButton volumeButton) {
		this.volumeButton = volumeButton;
	}

	/**
	 * @param playerControlsProgressSlider
	 */
	public void setPlayerControlsProgressSlider(
			final IProgressSlider playerControlsProgressSlider) {
		this.playerControlsProgressSlider = playerControlsProgressSlider;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * Adds the content.
	 */
	public void initialize() {
		JPanel progressSliderContainer = new JPanel(new BorderLayout());
		progressSliderContainer.add(
				this.playerControlsProgressSlider.getSwingComponent(),
				BorderLayout.CENTER);

		JPanel mainControls = getMainControlsPanel();
		JPanel secondaryControlsPanel = getSecondaryControls();
		adjustControlsSize(mainControls, secondaryControlsPanel);

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.insets = new Insets(5, 10, 5, 0);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.VERTICAL;
		add(mainControls, c);

		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(10, 10, 8, 10);
		c.fill = GridBagConstraints.BOTH;
		add(progressSliderContainer, c);

		c.gridx = 2;
		c.weightx = 0;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.VERTICAL;
		c.insets = new Insets(5, 0, 5, 10);
		add(secondaryControlsPanel, c);

		this.controlsBuilder.applyComponentOrientation(this);
	}

	/**
	 * @param mainControls
	 * @param secondaryControlsPanel
	 */
	private void adjustControlsSize(final JPanel mainControls,
			final JPanel secondaryControlsPanel) {
		secondaryControlsPanel.setMinimumSize(mainControls.getPreferredSize());
		secondaryControlsPanel
				.setPreferredSize(mainControls.getPreferredSize());
		secondaryControlsPanel.setMaximumSize(mainControls.getPreferredSize());
		mainControls.setMaximumSize(mainControls.getPreferredSize());
	}

	@Override
	public IProgressSlider getProgressSlider() {
		return this.playerControlsProgressSlider;
	}

	@Override
	public void setProgress(final long time, final long remainingTime,
			final boolean fading) {
		this.playerControlsProgressSlider.setProgress(time, remainingTime,
				fading);
	}

	/**
	 * Updates volume controls with the volume level
	 * 
	 * @param volume
	 */
	@Override
	public void setVolume(final int volume) {
		this.volumeSlider.setValue(volume);
		this.volumeButton.updateIcon();
	}

	@Override
	public void setPlaying(final boolean playing) {
		this.playButton.setPlaying(playing);
	}

	private JPanel getMainControlsPanel() {
		PreviousButton previousButton = (PreviousButton) this.controlsBuilder
				.createPreviousButton();
		this.playButton = (PlayPauseButton) this.controlsBuilder
				.createPlayPauseButton();
		StopButton stopButton = (StopButton) this.controlsBuilder
				.createStopButton();
		NextButton nextButton = (NextButton) this.controlsBuilder
				.createNextButton();
		this.volumeButton.setText("");
		JPanel panel = getPanelWithPlayerControls(stopButton, previousButton,
				this.playButton, nextButton, this.volumeButton,
				this.volumeSlider, this.lookAndFeelManager);
		// add a small border to separate from other components
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
		return panel;
	}

	/**
	 * Return a panel with all player buttons. This method is shared with full
	 * screen window
	 * 
	 * @param stopButton
	 * @param previousButton
	 * @param playButton
	 * @param nextButton
	 * @param volumeButton
	 * @param volumeSlider
	 * @param lookAndFeelManager
	 * @return
	 */
	private JPanel getPanelWithPlayerControls(final StopButton stopButton,
			final PreviousButton previousButton,
			final PlayPauseButton playButton, final NextButton nextButton,
			final MuteButton volumeButton, final VolumeSlider volumeSlider,
			final ILookAndFeelManager lookAndFeelManager) {
		if (lookAndFeelManager.getCurrentLookAndFeel()
				.isCustomPlayerControlsSupported()) {
			return new CustomPlayerControlsBuilder().getCustomPlayerControls(
					stopButton, previousButton, playButton, nextButton,
					volumeButton, volumeSlider);
		} else {
			return new StandardPlayerControlsBuilder()
					.getStandardPlayerControls(stopButton, previousButton,
							playButton, nextButton, volumeButton, volumeSlider);
		}
	}

	private JPanel getSecondaryControls() {
		if (this.secondaryControls == null) {
			this.secondaryControls = this.secondaryPlayerControlsBuilder
					.getSecondaryControls();
		}
		return this.secondaryControls;
	}

	/**
	 * Adds a secondary control
	 * 
	 * @param button
	 */
	@Override
	public void addSecondaryControl(final Action action) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = getSecondaryControls().getComponentCount();
		c.gridy = 0;
		c.insets = new Insets(0, 1, 0, 0);
		JButton button = new SecondaryControl(action);
		button.setPreferredSize(this.secondaryControlSize);
		getSecondaryControls().add(button, c);
		getSecondaryControls().repaint();
	}

	/**
	 * Adds a secondary toggle control
	 * 
	 * @param button
	 */
	@Override
	public void addSecondaryToggleControl(final Action action) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = getSecondaryControls().getComponentCount();
		c.gridy = 0;
		c.insets = new Insets(0, 1, 0, 0);
		JToggleButton button = new SecondaryToggleControl(action);
		button.setPreferredSize(this.secondaryControlSize);
		getSecondaryControls().add(button, c);
		getSecondaryControls().repaint();
	}

	/**
	 * Hides or shows advanced controls
	 * 
	 * @param show
	 */
	@Override
	public void showAdvancedPlayerControls(final boolean show) {
		this.equalizerButton.setVisible(show);
		this.normalizeButton.setVisible(show);
		this.similarModeButton.setVisible(show);
	}

	@Override
	public JPanel getSwingComponent() {
		return this;
	}
}