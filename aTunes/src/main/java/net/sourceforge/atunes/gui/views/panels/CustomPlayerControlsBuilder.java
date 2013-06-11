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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import net.sourceforge.atunes.gui.views.controls.MuteButton;
import net.sourceforge.atunes.gui.views.controls.NextButton;
import net.sourceforge.atunes.gui.views.controls.PlayPauseButton;
import net.sourceforge.atunes.gui.views.controls.PreviousButton;
import net.sourceforge.atunes.gui.views.controls.StopButton;
import net.sourceforge.atunes.gui.views.controls.VolumeSlider;

/**
 * Builds player controls panel
 * 
 * @author alex
 * 
 */
public class CustomPlayerControlsBuilder {

	/**
	 * Returns custom panel with controls (used by Substance LAF)
	 * 
	 * @param stopButton
	 * @param previousButton
	 * @param playButton
	 * @param nextButton
	 * @param volumeButton
	 * @param volumeSlider
	 * @return
	 */
	public JPanel getCustomPlayerControls(final StopButton stopButton,
			final PreviousButton previousButton,
			final PlayPauseButton playButton, final NextButton nextButton,
			final MuteButton volumeButton, final VolumeSlider volumeSlider) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		panel.add(stopButton, c);
		c.gridx = 1;
		c.insets = new Insets(0, -6, 0, 0);
		panel.add(previousButton, c);
		c.gridx = 2;
		c.insets = new Insets(-1, -16, 0, 0);
		panel.add(playButton, c);
		c.gridx = 3;
		c.insets = new Insets(0, -16, 0, 0);
		panel.add(nextButton, c);
		if (volumeButton != null && volumeSlider != null) {
			c.gridx = 4;
			c.insets = new Insets(0, -7, 0, 0);
			panel.add(volumeButton, c);
			c.gridx = 5;
			c.insets = new Insets(0, 10, 0, 0);
			panel.add(volumeSlider, c);
		}
		return panel;
	}

}
