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

package net.sourceforge.atunes.gui.views.dialogs;

import javax.swing.JOptionPane;

import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * A runnable wrapping a call to show an error message
 * @author alex
 *
 */
final class ShowMessageDialogRunnable implements Runnable {
	
	private final String message;
	private final IFrame frame;

	ShowMessageDialogRunnable(String message, IFrame frame) {
		this.message = message;
		this.frame = frame;
	}

	@Override
	public void run() {
		JOptionPane.showMessageDialog(frame.getFrame(), message, I18nUtils.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
	}
}