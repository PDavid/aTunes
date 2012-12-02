/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;


final class SelectBackgroundActionListener implements ActionListener {
	
	private FullScreenController fullScreenController;
	
	/**
	 * @param fullScreenController
	 */
	SelectBackgroundActionListener(FullScreenController fullScreenController) {
		this.fullScreenController = fullScreenController;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		fullScreenController.getComponentControlled().setVisible(false);
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setFileFilter(new FullScreenBackgroundFileFilter());
	    if (fileChooser.showOpenDialog(fullScreenController.getComponentControlled()) == JFileChooser.APPROVE_OPTION) {
	    	fullScreenController.setBackground(fileChooser.getSelectedFile());
	    	fullScreenController.getComponentControlled().invalidate();
	    	fullScreenController.getComponentControlled().repaint();
	    }
	    fullScreenController.getComponentControlled().setVisible(true);
	}
}