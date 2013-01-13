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

package net.sourceforge.atunes.kernel.modules.instances;

import java.io.IOException;
import java.net.ServerSocket;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.model.IMultipleInstancesHandler;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * @author alex
 * 
 */
public final class MultipleInstancesHandler extends AbstractHandler implements
		IMultipleInstancesHandler {

	/**
	 * Used to ignore errors in sockets when closing application
	 */
	private boolean closing = false;

	/** The server socket. */
	private ServerSocket serverSocket;

	private IApplicationArguments applicationArguments;

	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(
			final IApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

	/**
	 * Called when aTunes finishes.
	 */
	@Override
	public void applicationFinish() {
		if (this.serverSocket != null) {
			this.closing = true;
			ClosingUtils.close(this.serverSocket);
		}
	}

	@Override
	public void deferredInitialization() {
		if (getOsManager().isMultipleInstancesSupported()
				&& !this.applicationArguments.isMultipleInstance()) {
			startListening();
		}
	}

	@Override
	public void startListening() {
		try {
			// Open server socket
			this.serverSocket = new ServerSocket(
					Constants.MULTIPLE_INSTANCES_SOCKET);
			Logger.info("Listening for other instances on port ",
					Constants.MULTIPLE_INSTANCES_SOCKET);

			// Initialize socket listener
			SocketListener listener = new SocketListener(this,
					this.serverSocket, getBean(ICommandHandler.class),
					getBean(IBeanFactory.class));

			// Start threads
			listener.start();

			// Server socket could be opened, so this instance is a "master"
		} catch (IOException e) {
			Logger.error(e);
			// Server socket could not be opened, so this instance is a "slave"
		} catch (SecurityException e) {
			Logger.error(e);
			// Server socket could not be opened, so this instance is a "slave"
		}
	}

	/**
	 * @return the closing
	 */
	boolean isClosing() {
		return this.closing;
	}
}
