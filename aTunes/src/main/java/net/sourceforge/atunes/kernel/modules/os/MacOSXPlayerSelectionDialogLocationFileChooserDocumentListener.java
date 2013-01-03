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

import java.io.File;

import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.atunes.gui.views.controls.CustomFileChooser;

/**
 * Updates finish button depending on content of player engine location file chooser
 * @author alex
 *
 */
final class MacOSXPlayerSelectionDialogLocationFileChooserDocumentListener implements DocumentListener {
	
	private final JButton finishButton;
	private final CustomFileChooser locationFileChooser;

	/**
	 * @param finishButton
	 * @param locationFileChooser
	 */
	public MacOSXPlayerSelectionDialogLocationFileChooserDocumentListener(JButton finishButton, CustomFileChooser locationFileChooser) {
		this.finishButton = finishButton;
		this.locationFileChooser = locationFileChooser;
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		updateFinishButton();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		updateFinishButton();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		updateFinishButton();
	}

	private void updateFinishButton() {
		if (!locationFileChooser.getResult().isEmpty()) {
			File file = new File(locationFileChooser.getResult());
			finishButton.setEnabled(file.exists() && !file.isDirectory());
		} else {
			finishButton.setEnabled(false);
		}
	}
}