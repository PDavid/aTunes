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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
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

    /** The queue. */
    private SongsQueue queue;
    
    private IOSManager osManager;
    
    private IRepositoryHandler repositoryHandler;
    
    private IRadioHandler radioHandler;
    
    private ICommandHandler commandHandler;

    /**
     * Instantiates a new socket listener.
     * @param multipleInstancesHandler
     * @param serverSocket
     * @param queue
     * @param osManager
     * @param repositoryHandler
     * @param radioHandler
     * @param commandHandler
     */
    SocketListener(MultipleInstancesHandler multipleInstancesHandler, ServerSocket serverSocket, SongsQueue queue, IOSManager osManager, IRepositoryHandler repositoryHandler, IRadioHandler radioHandler, ICommandHandler commandHandler) {
        super();
		this.multipleInstancesHandler = multipleInstancesHandler;
		this.osManager = osManager;
		this.repositoryHandler = repositoryHandler;
		this.radioHandler = radioHandler;
		this.commandHandler = commandHandler;
        this.socket = serverSocket;
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
                        List<String> songs = PlayListIO.read(fileStr, osManager);
                        List<IAudioObject> files = PlayListIO.getAudioObjectsFromFileNamesList(repositoryHandler, songs, radioHandler);
                        for (IAudioObject file : files) {
                            queue.addSong(file);
                        }
                    } else if (AudioFile.isValidAudioFile(fileStr)) {
                    	ILocalAudioObject file = repositoryHandler.getFileIfLoaded(str);
                        if (file == null) {
                            // file not in repository, and don't add it now
                            file = new AudioFile(fileStr);
                        }
                        queue.addSong(file);
                    } else if (commandHandler.isValidCommand(str)) {
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