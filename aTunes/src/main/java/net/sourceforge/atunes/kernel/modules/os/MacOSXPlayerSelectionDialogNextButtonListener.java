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

package net.sourceforge.atunes.kernel.modules.os;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Called when next button pressed. Will call one method or other depending on user selection
 * @author alex
 *
 */
final class MacOSXPlayerSelectionDialogNextButtonListener implements ActionListener {
	
	private MacOSXPlayerSelectionDialog dialog;
	
	private String enterPathPanel;

	/**
	 * @param dialog
	 * @param enterPathPanel
	 */
	public MacOSXPlayerSelectionDialogNextButtonListener(MacOSXPlayerSelectionDialog dialog, String enterPathPanel) {
		this.dialog = dialog;
		this.enterPathPanel = enterPathPanel;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (dialog.automaticSearchSelected()) {
			dialog.searchPlayerEngine();
		} else {
			((CardLayout)dialog.getPanelContainer().getLayout()).show(dialog.getPanelContainer(), enterPathPanel);
		}
	}
}