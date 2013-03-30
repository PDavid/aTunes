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
 * A part of the interface that shows navigation table
 * 
 * @author alex
 * 
 */
public interface INavigationTablePanel extends IPanel {

	/**
	 * Shows or hides navigation table filter
	 * 
	 * @param show
	 */
	void showNavigationTableFilter(boolean show);

	/**
	 * Called to notify when navigation tree is shown
	 * 
	 * @param show
	 */
	void showNavigationTree(boolean show);

	/**
	 * Called to notify when navigation view changes
	 * 
	 * @param view
	 */
	void showNavigationView(INavigationView view);

}