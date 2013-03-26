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

package net.sourceforge.atunes.model;

import java.awt.Component;


/**
 * A dialog to show messages
 * @author alex
 *
 */
public interface IMessageDialog extends IDialog {

	/**
	 * Show message for principal frame
	 * @param message
	 */
	void showMessage(String message);

	/**
	 * Show message
	 * @param message
	 * @param owner
	 */
	void showMessage(String message, Component owner);

	/**
	 * Shows a custom message dialog.
	 * @param message
	 * @param title
	 * @param messageType the JOptionPane integer constant which determines type of message
	 * @param options array of objects to be shown on buttons
	 * @return Selected object
	 */
	Object showMessage(String message, String title, int messageType, Object[] options);

}