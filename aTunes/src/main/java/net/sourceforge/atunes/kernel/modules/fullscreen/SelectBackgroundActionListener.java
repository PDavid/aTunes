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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import net.sourceforge.atunes.model.IControlsBuilder;

final class SelectBackgroundActionListener implements ActionListener {

	private final FullScreenController fullScreenController;

	private final IControlsBuilder controlsBuilder;

	/**
	 * @param fullScreenController
	 */
	SelectBackgroundActionListener(
			final FullScreenController fullScreenController,
			final IControlsBuilder controlsBuilder) {
		this.fullScreenController = fullScreenController;
		this.controlsBuilder = controlsBuilder;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		this.fullScreenController.getComponentControlled().setVisible(false);
		JFileChooser fileChooser = this.controlsBuilder.getFileChooser();
		fileChooser.setFileFilter(new FullScreenBackgroundFileFilter());
		if (fileChooser.showOpenDialog(this.fullScreenController
				.getComponentControlled()) == JFileChooser.APPROVE_OPTION) {
			this.fullScreenController.setBackground(fileChooser
					.getSelectedFile());
			this.fullScreenController.getComponentControlled().invalidate();
			this.fullScreenController.getComponentControlled().repaint();
		}
		this.fullScreenController.getComponentControlled().setVisible(true);
	}
}