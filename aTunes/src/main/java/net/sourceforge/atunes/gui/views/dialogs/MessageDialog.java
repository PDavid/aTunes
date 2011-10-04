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

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IMessageDialog;

public class MessageDialog implements IMessageDialog {

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IMessageDialog#showMessage(java.lang.String, net.sourceforge.atunes.model.IFrame)
	 */
    @Override
	public void showMessage(String message, IFrame frame) {
        showMessage(message, frame.getFrame());
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IMessageDialog#showMessage(java.lang.String, java.awt.Component)
	 */
    @Override
	public void showMessage(String message, Component owner) {
        JOptionPane.showMessageDialog(owner, message);
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IMessageDialog#showMessage(net.sourceforge.atunes.model.IFrame, java.lang.String, java.lang.String, int, java.lang.Object[])
	 */
    @Override
	public Object showMessage(IFrame frame, String message, String title, int messageType, Object[] options) {
        JOptionPane pane = new JOptionPane(message, messageType, JOptionPane.OK_CANCEL_OPTION, null, options);
        JDialog dialog = pane.createDialog(frame.getFrame(), title);
        dialog.setLocationRelativeTo(frame.getFrame());
        dialog.setVisible(true);
        return pane.getValue();
    }


}
