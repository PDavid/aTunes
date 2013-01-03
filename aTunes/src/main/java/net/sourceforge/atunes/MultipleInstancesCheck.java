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

import java.io.IOException;
import java.net.ServerSocket;

import net.sourceforge.atunes.utils.ClosingUtils;

/**
 * Test if this instance is the only aTunes instance running
 * @author alex
 *
 */
public class MultipleInstancesCheck {

	/**
	 * @return
	 */
	public boolean isFirstInstance() {
		ServerSocket serverSocket = null;
        try {
            // Open server socket
            serverSocket = new ServerSocket(Constants.MULTIPLE_INSTANCES_SOCKET);
            // Server socket could be opened, so this instance is a "master"
            return true;
        } catch (IOException e) {
            // Server socket could not be opened, so this instance is a "slave"
            return false;
        } catch (SecurityException e) {
            // Server socket could not be opened, so this instance is a "slave"
            return false;
        } finally {
        	ClosingUtils.close(serverSocket);
        }
    }
}
