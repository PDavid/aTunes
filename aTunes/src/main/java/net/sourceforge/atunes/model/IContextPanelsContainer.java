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
 * A component that contains context panels, only one visible at a time
 * @author alex
 *
 */
public interface IContextPanelsContainer extends IPanel {

	/**
	 * Updates context panels
	 */
	public void updateContextPanels();

	/**
	 * Adds a new context panel
	 * 
	 * @param panel
	 */
	public void addContextPanel(IContextPanel panel);

	/**
	 * Removes a context panel
	 * 
	 * @param panel
	 */
	public void removeContextPanel(IContextPanel panel);

	/**
	 * Returns selected panel
	 * 
	 * @return
	 */
	public IContextPanel getSelectedContextPanel();

	/**
	 * @param selectedContextPanel
	 */
	public void setSelectedContextPanel(String selectedContextPanel);

}