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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.gui.views.controls.CustomFileChooser;
import net.sourceforge.atunes.model.IOSManager;

/**
 * Saves player engine path and closes dialog
 * @author alex
 *
 */
final class MacOSXPlayerSelectionDialogEnterPlayerEngineFinishButtonListener implements ActionListener {
	
	private final MacOSXPlayerSelectionDialog dialog;
	
	private final CustomFileChooser locationFileChooser;

	private final IOSManager osManager;
	
	/**
	 * @param dialog
	 * @param osManager
	 * @param locationFileChooser
	 */
	public MacOSXPlayerSelectionDialogEnterPlayerEngineFinishButtonListener(MacOSXPlayerSelectionDialog dialog, IOSManager osManager, CustomFileChooser locationFileChooser) {
		this.dialog = dialog;
		this.locationFileChooser = locationFileChooser;
		this.osManager = osManager;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		osManager.setOSProperty(MacOSXOperatingSystem.MPLAYER_COMMAND, locationFileChooser.getResult());
		dialog.dispose();
		osManager.playerEngineFound();
	}
}