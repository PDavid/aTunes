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
 * Responsible of managing some UI operations
 * @author alex
 *
 */
public interface IUIHandler extends IHandler {

	/**
	 * Finish.
	 * 
	 * NOTE: This method is called using reflection from MACOSXAdapter. Refactoring will break code!
	 */
	public void finish();

	/**
	 * Convenience method, called from MacOSXAdapter
	 */
	public void showFullFrame();

	/**
	 * Show about dialog.
	 * 
	 * NOTE: This method is called using reflection from MACOSXAdapter. Refactoring will break code!
	 */
	public void showAboutDialog();

	/**
	 * Show status bar.
	 * 
	 * @param show
	 * @param save
	 */
	public void showStatusBar(boolean show, boolean save);

	/**
	 * Start visualization.
	 */
	public void startVisualization();

	/**
	 * Toggle window visibility.
	 */
	public void toggleWindowVisibility();

	/**
	 * Update title bar.
	 * 
	 * @param song
	 *            the song
	 */
	public void updateTitleBar(IAudioObject song);

}