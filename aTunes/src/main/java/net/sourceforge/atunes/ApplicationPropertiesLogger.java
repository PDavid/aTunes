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

package net.sourceforge.atunes;

import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.Logger;

public class ApplicationPropertiesLogger {
	
	private ApplicationArguments applicationArguments;
	private IOSManager osManager;

	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(ApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
    /**
     * Log some properties
     * 
     * @param arguments
     * @param osManager
     */
    public void logProgramProperties() {
        Logger.info("Version: ", Constants.APP_NAME, " ", Constants.VERSION);
        Logger.info("Running in Java Virtual Machine ", System.getProperty("java.version"));
        Logger.info("Operating System: ", System.getProperty("os.name"), " ", System.getProperty("os.version"), " (", System.getProperty("os.arch"), ")");
        Logger.info("Arguments = ", applicationArguments.getOriginalArguments());
        Logger.info("Debug mode = ", applicationArguments.isDebug());
        Logger.info("Execution path = ", osManager.getWorkingDirectory());
    }


}