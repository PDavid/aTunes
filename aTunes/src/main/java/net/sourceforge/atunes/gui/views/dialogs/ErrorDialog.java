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

import java.awt.Component;

import javax.swing.JOptionPane;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Shows different types of error messages
 * @author alex
 *
 */
public class ErrorDialog implements IErrorDialog {

	private IFrame frame;
	
	/**
	 * @param frame
	 */
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
	
    @Override
	public void showErrorDialog(final String message) {
    	GuiUtils.callInEventDispatchThread(new ShowMessageDialogRunnable(message, frame));
    }

    @Override
	public void showErrorDialog(String message, Component parent) {
        JOptionPane.showMessageDialog(parent, message, I18nUtils.getString("ERROR"), JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void initialize() {
    	// Do nothing
    }
    
    @Override
    public void hideDialog() {
    	throw new UnsupportedOperationException();
    }
    
    @Override
    public void showDialog() {
    	throw new UnsupportedOperationException();
    }
    
	@Override
	@Deprecated
	public void setTitle(String title) {
		// Not used
	}
}
