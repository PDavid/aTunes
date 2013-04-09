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

package net.sourceforge.atunes.kernel.modules.os;

import net.sourceforge.atunes.model.IFrame;

/**
 * An interface for Mac OS X applications
 * 
 * @author alex
 * 
 */
public interface IMacOSXApplication {

	/**
	 * Initializes application object
	 * 
	 * @return if initialization is successful
	 */
	public boolean initialize();

	/**
	 * Register a handler for about menu
	 * 
	 * @param aboutHandler
	 */
	public void registerAbout(MacOSXAboutHandler aboutHandler);

	/**
	 * Register a handler for quit
	 * 
	 * @param quitHandler
	 */
	public void registerQuit(MacOSXQuitHandler quitHandler);

	/**
	 * Register a handler for preferences
	 * 
	 * @param prefsHandler
	 */
	public void registerPreferences(MacOSXPreferencesHandler prefsHandler);

	/**
	 * Register listener to be fired when apps is reopened
	 * 
	 * @param adapter
	 */
	public void registerAppReOpenedListener(MacOSXAppReOpenedListener adapter);

	/**
	 * Adds dock icon menu
	 */
	public void addDockIconMenu();

	/**
	 * Enables Full Screen mode available from OS X Lion
	 * 
	 * @param frame
	 */
	void enableFullscreen(IFrame frame);

}