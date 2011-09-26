/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.lang.reflect.InvocationTargetException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

public class ConfirmationDialog implements IConfirmationDialog {
	
	private IFrame frame;

	private boolean result;
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IConfirmationDialog#setFrame(net.sourceforge.atunes.model.IFrame)
	 */
	@Override
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IConfirmationDialog#showDialog(java.lang.String)
	 */
	@Override
	public boolean showDialog(final String message) {
		if (SwingUtilities.isEventDispatchThread()) {
			return JOptionPane.showConfirmDialog(frame.getFrame(), message, I18nUtils.getString("CONFIRMATION"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						result = JOptionPane.showConfirmDialog(frame.getFrame(), message, I18nUtils.getString("CONFIRMATION"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
					}
				});
			} catch (InterruptedException e) {
				Logger.error(e);
			} catch (InvocationTargetException e) {
				Logger.error(e);
			}
			return result;
		}
	}

}
