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

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

final class HideMouseActionListener implements ActionListener {
	
	private FullScreenWindow fullScreenWindow;
	
	private JPanel controlsPanel;
	
	private JPopupMenu options;
	
	/**
	 * @param fullScreenWindow
	 * @param controlsPanel
	 * @param options
	 */
	public HideMouseActionListener(FullScreenWindow fullScreenWindow, JPanel controlsPanel, JPopupMenu options) {
		this.fullScreenWindow = fullScreenWindow;
		this.controlsPanel = controlsPanel;
		this.options = options;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	    controlsPanel.setVisible(false);
	    fullScreenWindow.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR), new Point(0, 0), "invisibleCursor"));
	    if (options.isVisible()) {
	        options.setVisible(false);
	    }
	}
}