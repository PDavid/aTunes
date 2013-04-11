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

import javax.swing.ListCellRenderer;

/**
 * Dialog to select an option from a list
 * 
 * @author alex
 * 
 */
public interface ISelectorDialog extends IDialog {

	/**
	 * @param cellRenderer
	 */
	public void setCellRenderer(ListCellRenderer cellRenderer);

	/**
	 * @param options
	 */
	public void setOptions(String[] options);

	/**
	 * Gets the selection.
	 * 
	 * @return the selection
	 */
	public String getSelection();

}