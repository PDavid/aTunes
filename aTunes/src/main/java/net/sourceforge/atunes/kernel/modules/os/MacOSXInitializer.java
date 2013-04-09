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
import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.model.IUIHandler;

/**
 * Initializes application in Mac OS X environment
 * 
 * @author alex
 * 
 */
public class MacOSXInitializer {

	private IUIHandler uiHandler;

	private IStateService stateService;

	private IKernel kernel;

	private IMacOSXApplication macOsApplication;

	/**
	 * @param uiHandler
	 */
	public void setUiHandler(final IUIHandler uiHandler) {
		this.uiHandler = uiHandler;
	}

	/**
	 * @param stateService
	 */
	public void setStateService(final IStateService stateService) {
		this.stateService = stateService;
	}

	/**
	 * @param kernel
	 */
	public void setKernel(final IKernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * @param macOsApplication
	 */
	public void setMacOsApplication(final IMacOSXApplication macOsApplication) {
		this.macOsApplication = macOsApplication;
	}

	/**
	 * Initializes application in Mac OS X environment
	 * 
	 * @param frame
	 */
	public void initialize(IFrame frame) {
		if (this.macOsApplication.initialize()) {
			this.macOsApplication.addDockIconMenu();
			this.macOsApplication.registerAbout(new MacOSXAboutHandler(
					this.uiHandler, "showAboutDialog"));
			this.macOsApplication
					.registerPreferences(new MacOSXPreferencesHandler(
							this.stateService, "editPreferences"));
			this.macOsApplication.registerQuit(new MacOSXQuitHandler(
					this.kernel, "finish"));
			this.macOsApplication
					.registerAppReOpenedListener(new MacOSXAppReOpenedListener(
							this.uiHandler, "showFullFrame"));
			this.macOsApplication.enableFullscreen(frame);
		}
	}
}
