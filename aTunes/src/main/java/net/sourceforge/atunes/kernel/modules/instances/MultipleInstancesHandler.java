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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.model.IMultipleInstancesHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class MultipleInstancesHandler.
 */
public final class MultipleInstancesHandler extends AbstractHandler implements IMultipleInstancesHandler {

    /**
     * Used to ignore errors in sockets when closing application
     */
    private boolean closing = false;

    /** The server socket. */
    private ServerSocket serverSocket;

    /**
     * Called when aTunes finishes.
     */
    public void applicationFinish() {
        if (serverSocket != null) {
            closing = true;
            ClosingUtils.close(serverSocket);
        }
    }

    @Override
	public boolean isFirstInstance() {
        try {
            // Open server socket
            serverSocket = new ServerSocket(Constants.MULTIPLE_INSTANCES_SOCKET);
            System.out.println(StringUtils.getString("INFO: aTunes is listening for other instances on port ", Constants.MULTIPLE_INSTANCES_SOCKET));

            // Initialize songs queue
            SongsQueue songsQueue = new SongsQueue();

            // Initialize socket listener
            SocketListener listener = new SocketListener(this, serverSocket, songsQueue, getBean(IOSManager.class), getBean(IRepositoryHandler.class), getBean(IRadioHandler.class), getBean(ICommandHandler.class));

            // Start threads
            songsQueue.start();
            listener.start();

            // Server socket could be opened, so this instance is a "master"
            return true;
        } catch (IOException e) {
            // Server socket could not be opened, so this instance is a "slave"
            System.out.println("INFO: Another aTunes instance is running");
            return false;
        } catch (SecurityException e) {
            // Server socket could not be opened, so this instance is a "slave"
            System.out.println("INFO: Another aTunes instance is running");
            return false;
        }
    }

    @Override
	public void sendArgumentsToFirstInstance(List<String> args) {
        Socket clientSocket = null;
        PrintWriter output = null;
        try {
            // Open client socket to communicate with "master"
            clientSocket = new Socket("localhost", Constants.MULTIPLE_INSTANCES_SOCKET);
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            for (String arg : args) {
                if (AudioFile.isValidAudioFile(arg) || PlayListIO.isValidPlayList(arg)) {
                    // Send args: audio files or play lists
                    System.out.println(StringUtils.getString("INFO: Sending arg \"", arg, "\""));
                    output.write(arg);
                } else if (getBean(ICommandHandler.class).isCommand(arg)) {
                    // It's a command
                    System.out.println(StringUtils.getString("INFO: Sending command \"", arg, "\""));
                    output.write(arg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ClosingUtils.close(output);
            ClosingUtils.close(clientSocket);
        }
    }
    
	/**
	 * @return the closing
	 */
	boolean isClosing() {
		return closing;
	}
}
