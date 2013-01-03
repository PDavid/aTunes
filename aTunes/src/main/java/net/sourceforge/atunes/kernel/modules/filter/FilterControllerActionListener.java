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

package net.sourceforge.atunes.kernel.modules.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

final class FilterControllerActionListener implements ActionListener {
	
	private FilterController controller;
	
	/**
	 * @param controller
	 */
	public FilterControllerActionListener(FilterController controller) {
		this.controller = controller;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		controller.getComponentControlled().getFilterTextField().setText("");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!controller.isFilterApplied()) {
					controller.getComponentControlled().getFilterTextField().getDocument().removeDocumentListener(controller.getListener());
				}
			}
		});
	}
}