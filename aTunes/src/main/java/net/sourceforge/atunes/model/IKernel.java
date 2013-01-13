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

import java.util.List;

/**
 * Core of application, responsible of start, restart and stop application
 * 
 * @author alex
 * 
 */
public interface IKernel {

	/**
	 * Static method to create the Kernel instance. This method starts the
	 * application, so should be called from the main method of the application.
	 * 
	 * @param arguments
	 */
	public void start(List<String> arguments);

	/**
	 * Called when closing application
	 */
	public void finish();

	/**
	 * Called when restarting application
	 */
	public void restart();

	/**
	 * Terminates execution due to a fatal error
	 * 
	 * @param e
	 */
	public void terminateWithError(Throwable e);

}