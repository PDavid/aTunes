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

package net.sourceforge.atunes.kernel.modules.instances;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class is responsible of listening to server socket and accept
 * connections from "slave" aTunes instances.
 */
class SocketListener extends Thread {

    /**
	 * 
	 */
	private final MultipleInstancesHandler multipleInstancesHandler;

	/** The socket. */
    private ServerSocket socket;

    private ICommandHandler commandHandler;

    /**
     * Instantiates a new socket listener.
     * @param multipleInstancesHandler
     * @param serverSocket
     * @param commandHandler
     */
    SocketListener(MultipleInstancesHandler multipleInstancesHandler, ServerSocket serverSocket, ICommandHandler commandHandler) {
        super();
		this.multipleInstancesHandler = multipleInstancesHandler;
		this.commandHandler = commandHandler;
        this.socket = serverSocket;
    }

    @Override
    public void run() {
        Socket s = null;
        BufferedReader br = null;
        BufferedOutputStream bos = null;
        try {
            while (true) {
                s = socket.accept();
                // Once a connection arrives, read args
                br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String str;
                while ((str = br.readLine()) != null) {
                	Logger.info("Receiver argument: ", str);
                    if (commandHandler.isValidCommand(str)) {
                    	commandHandler.processAndRun(str);
                    }
                }
                ClosingUtils.close(br);
                ClosingUtils.close(s);
                Logger.info(StringUtils.getString("Connection finished"));
            }
        } catch (IOException e) {
            if (!this.multipleInstancesHandler.isClosing()) {
                Logger.error(e);
            }
        } finally {
            ClosingUtils.close(bos);
            ClosingUtils.close(br);
            ClosingUtils.close(s);
        }
    }
}