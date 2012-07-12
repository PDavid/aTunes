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

package net.sourceforge.atunes.gui.views.dialogs;

import javax.swing.JOptionPane;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

public class ConfirmationDialog implements IConfirmationDialog {

	private IFrame frame;

	private boolean result;
	
	private String message;
	
	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}

	@Override
	public void showDialog() {
		GuiUtils.callInEventDispatchThreadAndWait(new Runnable() {
			@Override
			public void run() {
				result = JOptionPane.showConfirmDialog(frame.getFrame(), message, I18nUtils.getString("CONFIRMATION"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
			}
		});
	}
	
	@Override
	public boolean userAccepted() {
		return result;
	}

	@Override
	public void hideDialog() {
		// Not used
	}
	
	@Override
	public void initialize() {
		// Not used
	}
	
	@Override
	@Deprecated
	public void setTitle(String title) {
		// Not used
	}
}
