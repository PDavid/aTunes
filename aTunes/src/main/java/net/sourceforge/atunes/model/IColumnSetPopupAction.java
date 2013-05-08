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


/**
 * Action to perform with a selected column of a table
 * 
 * @author alex
 * 
 */
public interface IColumnSetPopupAction {

	/**
	 * @return text of action
	 */
	String getText();

	/**
	 * Executes when action fired over given column, column model and table
	 * 
	 * @param column
	 * @param columnModel
	 * @param tableModel
	 */
	void executeAction(IColumn<?> column, IColumnModel columnModel,
			IColumnSetTableModel tableModel);
}
