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

package net.sourceforge.atunes.kernel.modules.covernavigator;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Listener for selecting artists in cover navigator
 * 
 * @author alex
 * 
 */
public final class CoverNavigationListSelectionListener implements
		ListSelectionListener {

	private CoverNavigatorController coverNavigatorController;

	/**
	 * @param coverNavigatorController
	 */
	public void setCoverNavigatorController(
			final CoverNavigatorController coverNavigatorController) {
		this.coverNavigatorController = coverNavigatorController;
	}

	@Override
	public void valueChanged(final ListSelectionEvent e) {
		if (!((JList) e.getSource()).getValueIsAdjusting()) {
			this.coverNavigatorController.updateCovers();
		}
	}
}