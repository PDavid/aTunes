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

package net.sourceforge.atunes;

import java.util.List;

import javax.swing.RepaintManager;

import net.sourceforge.atunes.gui.debug.CheckThreadViolationRepaintManager;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.Logger;

/**
 * Do all necessary to start application
 * 
 * @author alex
 * 
 */
public class ApplicationStarter {

	private IApplicationArguments applicationArguments;
	private IOSManager osManager;
	private ApplicationPropertiesLogger applicationPropertiesLogger;
	private MultipleInstancesCheck multipleInstancesCheck;
	private ApplicationArgumentsSender applicationArgumentsSender;
	private IKernel kernel;

	/**
	 * @param kernel
	 */
	public void setKernel(final IKernel kernel) {
		this.kernel = kernel;
	}

	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(
			final IApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param applicationPropertiesLogger
	 */
	public void setApplicationPropertiesLogger(
			final ApplicationPropertiesLogger applicationPropertiesLogger) {
		this.applicationPropertiesLogger = applicationPropertiesLogger;
	}

	/**
	 * @param multipleInstancesCheck
	 */
	public void setMultipleInstancesCheck(
			final MultipleInstancesCheck multipleInstancesCheck) {
		this.multipleInstancesCheck = multipleInstancesCheck;
	}

	/**
	 * @param applicationArgumentsSender
	 */
	public void setApplicationArgumentsSender(
			final ApplicationArgumentsSender applicationArgumentsSender) {
		this.applicationArgumentsSender = applicationArgumentsSender;
	}

	/**
	 * Starts application logic
	 * 
	 * @param arguments
	 */
	public void start(final List<String> arguments) {
		// For detecting Swing threading violations
		if (this.applicationArguments.isDebug()) {
			RepaintManager
					.setCurrentManager(new CheckThreadViolationRepaintManager());
		}

		// Set log4j properties
		Logger.loadProperties(this.applicationArguments.isDebug(),
				this.applicationArguments.isDebugLevelLog(), this.osManager);

		// First, look up for other instances running
		if (!this.applicationArguments.isMultipleInstance()
				&& !this.multipleInstancesCheck.isFirstInstance()) {
			// Is not first aTunes instance running, so send parameters and
			// finalize
			this.applicationArgumentsSender
					.sendArgumentsToFirstInstance(arguments);
		} else {
			// WE ARE CLOSING ERROR STREAM!!!
			// THIS IS DONE TO AVOID ANNOYING MESSAGES FROM SOME LIBRARIES
			System.err.close();

			// Log program properties
			this.applicationPropertiesLogger.logProgramProperties();

			// Start the Kernel, which really starts application
			this.kernel.start(arguments);
		}
	}
}
