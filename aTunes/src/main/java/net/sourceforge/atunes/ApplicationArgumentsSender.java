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
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Sends arguments to another instance of aTunes
 * @author alex
 *
 */
public class ApplicationArgumentsSender {

	/**
	 * Send arguments to a running instance
	 * @param args
	 */
	public void sendArgumentsToFirstInstance(final List<String> args) {
		Socket clientSocket = null;
		PrintWriter output = null;
		try {
			// Open client socket to communicate with "master"
			clientSocket = new Socket("localhost", Constants.MULTIPLE_INSTANCES_SOCKET);
			output = new PrintWriter(clientSocket.getOutputStream(), true);
			for (String arg : args) {
				System.out.println(StringUtils.getString("INFO: Sending argument \"", arg, "\""));
				output.write(arg);
				output.write(System.getProperty("line.separator"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ClosingUtils.close(output);
			ClosingUtils.close(clientSocket);
		}
	}
}
