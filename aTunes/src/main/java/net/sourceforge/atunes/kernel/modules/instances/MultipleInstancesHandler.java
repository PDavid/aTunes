/*
 * aTunes 2.1.0-SNAPSHOT
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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.command.CommandHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class MultipleInstancesHandler.
 */
public final class MultipleInstancesHandler extends AbstractHandler {

    /**
     * Used to ignore errors in sockets when closing application
     */
    private boolean closing = false;

    /**
     * This class is responsible of listening to server socket and accept
     * connections from "slave" aTunes instances.
     */
    class SocketListener extends Thread {

        /** The socket. */
        private ServerSocket socket;

        /** The queue. */
        private SongsQueue queue;

        /**
         * Instantiates a new socket listener.
         * 
         * @param serverSocket
         *            the server socket
         * @param queue
         *            the queue
         */
        SocketListener(ServerSocket serverSocket, SongsQueue queue) {
            super();
            socket = serverSocket;
            this.queue = queue;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Thread#run()
         */
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
                    	File fileStr = new File(str);
                        Logger.info(StringUtils.getString("Received connection with content: \"", str, "\""));
                        if (PlayListIO.isValidPlayList(fileStr)) {
                            List<String> songs = PlayListIO.read(fileStr, getOsManager());
                            List<IAudioObject> files = PlayListIO.getAudioObjectsFromFileNamesList(songs);
                            for (IAudioObject file : files) {
                                queue.addSong(file);
                            }
                        } else if (AudioFile.isValidAudioFile(fileStr)) {
                        	ILocalAudioObject file = RepositoryHandler.getInstance().getFileIfLoaded(str);
                            if (file == null) {
                                // file not in repository, and don't add it now
                                file = new AudioFile(fileStr);
                            }
                            queue.addSong(file);
                        } else if (CommandHandler.getInstance().isValidCommand(str)) {
                            CommandHandler.getInstance().processAndRun(str);
                        }
                    }
                    ClosingUtils.close(br);
                    ClosingUtils.close(s);
                    Logger.info(StringUtils.getString("Connection finished"));
                }
            } catch (IOException e) {
                if (!MultipleInstancesHandler.this.isClosing()) {
                    Logger.error(e);
                }
            } finally {
                ClosingUtils.close(bos);
                ClosingUtils.close(br);
                ClosingUtils.close(s);
            }
        }
    }

    /**
     * This class is responsible of create a queue of songs to be added. When
     * opening multiple files, OS launch a "slave" aTunes for every file, so
     * this queue adds songs in the order connections are made, and when no more
     * connections are received, then add to playlist
     */
    static class SongsQueue extends Thread {

        /** The songs queue. */
        private List<IAudioObject> songsQueue;

        /** The last song added. */
        private long lastSongAdded = 0;

        /**
         * Instantiates a new songs queue.
         */
        SongsQueue() {
            songsQueue = new ArrayList<IAudioObject>();
        }

        /**
         * Adds the song.
         * 
         * @param song
         *            the song
         */
        public void addSong(IAudioObject song) {
            songsQueue.add(song);
            lastSongAdded = System.currentTimeMillis();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            try {
                while (true) {
                    if (!songsQueue.isEmpty() && lastSongAdded < System.currentTimeMillis() - 1000) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                // Get an auxiliar list with songs
                                ArrayList<IAudioObject> auxList = new ArrayList<IAudioObject>(songsQueue);
                                // Clear songs queue
                                songsQueue.clear();
                                // Add songs
                                Context.getBean(IPlayListHandler.class).addToPlayListAndPlay(auxList);
                            }
                        });
                    }
                    // Wait one second always, even if songsQueue was not empty, to avoid entering again until songsQueue is cleared
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /** The instance. */
    private static MultipleInstancesHandler instance;

    /** The server socket. */
    private ServerSocket serverSocket;

    /**
     * Instantiates a new multiple instances handler.
     */
    private MultipleInstancesHandler() {
        // Nothing to do
    }

    @Override
    public void applicationStateChanged(IState newState) {
    }

    @Override
    protected void initHandler() {
    }

    @Override
    public void applicationStarted(List<IAudioObject> playList) {
    }

    /**
     * Gets the single instance of MultipleInstancesHandler.
     * 
     * @return single instance of MultipleInstancesHandler
     */
    public static MultipleInstancesHandler getInstance() {
        if (instance == null) {
            instance = new MultipleInstancesHandler();
        }
        return instance;
    }

    /**
     * Called when aTunes finishes.
     */
    public void applicationFinish() {
        if (serverSocket != null) {
            closing = true;
            ClosingUtils.close(serverSocket);
        }
    }

    /**
     * Tries to open a server socket to listen to other aTunes instances.
     * 
     * @return true if server socket could be opened
     */

    public boolean isFirstInstance() {
        try {
            // Open server socket
            serverSocket = new ServerSocket(Constants.MULTIPLE_INSTANCES_SOCKET);
            System.out.println(StringUtils.getString("INFO: aTunes is listening for other instances on port ", Constants.MULTIPLE_INSTANCES_SOCKET));

            // Initialize songs queue
            SongsQueue songsQueue = new SongsQueue();

            // Initialize socket listener
            SocketListener listener = new SocketListener(serverSocket, songsQueue);

            // Start threads
            songsQueue.start();
            listener.start();

            // Server socket could be opened, so this instance is a "master"
            return true;
        } catch (Exception e) {
            // Server socket could not be opened, so this instance is a "slave"
            System.out.println("INFO: Another aTunes instance is running");
            return false;
        }
    }

    /**
     * Opens a client socket and sends arguments to "master".
     * 
     * @param args
     *            the args
     */
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
                } else if (CommandHandler.getInstance().isCommand(arg)) {
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
    
	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(IAudioObject audioObject) {}

	/**
	 * @return the closing
	 */
	private boolean isClosing() {
		return closing;
	}

}
