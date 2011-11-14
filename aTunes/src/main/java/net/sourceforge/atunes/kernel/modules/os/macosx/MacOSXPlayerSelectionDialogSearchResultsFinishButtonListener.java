/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.os.macosx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;

import net.sourceforge.atunes.model.IOSManager;

/**
 * Saves player engine path after selecting one from automatic search
 * @author alex
 *
 */
final class MacOSXPlayerSelectionDialogSearchResultsFinishButtonListener implements ActionListener {
	
	private MacOSXPlayerSelectionDialog dialog;
	
	private IOSManager osManager;
	
	private JList list;
	
	public MacOSXPlayerSelectionDialogSearchResultsFinishButtonListener(MacOSXPlayerSelectionDialog dialog, IOSManager osManager, JList list) {
		this.dialog = dialog;
		this.osManager = osManager;
		this.list = list;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		osManager.setOSProperty(MacOSXOperatingSystem.MPLAYER_COMMAND, (String)list.getSelectedValue());
		dialog.dispose();
		osManager.playerEngineFound();
	}
}