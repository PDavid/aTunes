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

package net.sourceforge.atunes.kernel.modules.os.macosx;

import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.model.IStateHandler;
import net.sourceforge.atunes.model.IUIHandler;

/**
 * Initializes application in Mac OS X environment
 * @author alex
 *
 */
public class MacOSXInitializer {

	private IUIHandler uiHandler;
	
	private IStateHandler stateHandler;
	
	private IKernel kernel;
	
	private IMacOSXApplication macOsApplication;
	
	/**
	 * @param uiHandler
	 */
	public void setUiHandler(IUIHandler uiHandler) {
		this.uiHandler = uiHandler;
	}
	
	/**
	 * @param stateHandler
	 */
	public void setStateHandler(IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}
	
	/**
	 * @param kernel
	 */
	public void setKernel(IKernel kernel) {
		this.kernel = kernel;
	}
	
	/**
	 * @param macOsApplication
	 */
	public void setMacOsApplication(IMacOSXApplication macOsApplication) {
		this.macOsApplication = macOsApplication;
	}
	
	/**
	 * Initializes application in Mac OS X environment
	 */
	public void initialize() {
		if (macOsApplication.initialize()) {
			macOsApplication.addDockIconMenu();
			macOsApplication.registerAbout(new MacOSXAboutHandler(uiHandler, "showAboutDialog"));
			macOsApplication.registerPreferences(new MacOSXPreferencesHandler(stateHandler, "editPreferences"));
			macOsApplication.registerQuit(new MacOSXQuitHandler(kernel, "finish"));
			macOsApplication.registerAppReOpenedListener(new MacOSXAppReOpenedListener(uiHandler, "showFullFrame"));
		}
	}
}
