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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListIOService;
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
	private final ServerSocket socket;

	private final ICommandHandler commandHandler;

	private final IBeanFactory beanFactory;

	private SongsQueue queue;

	/**
	 * Instantiates a new socket listener.
	 * 
	 * @param multipleInstancesHandler
	 * @param serverSocket
	 * @param commandHandler
	 * @param beanFactory
	 */
	SocketListener(final MultipleInstancesHandler multipleInstancesHandler,
			final ServerSocket serverSocket,
			final ICommandHandler commandHandler, final IBeanFactory beanFactory) {
		super();
		this.multipleInstancesHandler = multipleInstancesHandler;
		this.commandHandler = commandHandler;
		this.socket = serverSocket;
		this.beanFactory = beanFactory;
	}

	@Override
	public void run() {
		Socket s = null;
		while (true) {
			s = readSocket();
			if (s != null) {
				// Initialize queue
				if (this.queue == null) {
					this.queue = new SongsQueue(
							this.beanFactory.getBean(IPlayListHandler.class));
				}

				// Once a connection arrives, read args
				BufferedReader br = null;
				BufferedWriter bw = null;
				try {
					br = new BufferedReader(new InputStreamReader(
							s.getInputStream()));
					bw = new BufferedWriter(new OutputStreamWriter(
							s.getOutputStream()));
				} catch (IOException e) {
					Logger.error(e);
				}

				if (br != null && bw != null) {
					processConnection(br, bw);
					endConnection(s, br, bw);
				}
			}
		}
	}

	/**
	 * @return socket
	 */
	private Socket readSocket() {
		try {
			return this.socket.accept();
		} catch (IOException e) {
			if (!this.multipleInstancesHandler.isClosing()) {
				Logger.error(e);
			}
		}
		return null;
	}

	/**
	 * @param s
	 * @param br
	 * @param bw
	 */
	private void endConnection(final Socket s, final BufferedReader br,
			final BufferedWriter bw) {
		try {
			bw.append("Closing Socket");
			bw.flush();
		} catch (IOException e) {
			if (!this.multipleInstancesHandler.isClosing()) {
				Logger.error(e);
			}
		}
		ClosingUtils.close(bw);
		ClosingUtils.close(br);
		ClosingUtils.close(s);
		Logger.info(StringUtils.getString("Connection finished"));
	}

	private void processConnection(final BufferedReader br,
			final BufferedWriter bw) {
		boolean exit = false;
		String str = readFromConnection(br);
		while (!exit && str != null) {
			Logger.info("Received argument: ", str);
			if (this.commandHandler.isValidCommand(str.split(" ")[0])) {
				processCommandAndReturnResponse(bw, str);
			} else if (str.equalsIgnoreCase("exit")) {
				exit = true;
			} else if (!processAudioObjectOrPlayList(str)) {
				writeBadCommandResponse(bw);
			}
			str = readFromConnection(br);
		}
	}

	private String readFromConnection(final BufferedReader br) {
		if (br != null) {
			try {
				return br.readLine();
			} catch (IOException e) {
				if (!this.multipleInstancesHandler.isClosing()) {
					Logger.error(e);
				}
			}
		}
		return null;
	}

	/**
	 * @param bw
	 * @param str
	 */
	private void processCommandAndReturnResponse(final BufferedWriter bw,
			final String str) {
		try {
			bw.append(this.commandHandler.processAndRun(str));
			bw.append(System.getProperty("line.separator"));
			bw.flush();
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	/**
	 * @param bw
	 */
	private void writeBadCommandResponse(final BufferedWriter bw) {
		try {
			bw.append("Bad command name or format, type \"command:help\" for assistance.");
			bw.append(System.getProperty("line.separator"));
			bw.flush();
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	private boolean processAudioObjectOrPlayList(final String str) {
		if (this.beanFactory.getBean(IPlayListIOService.class).isValidPlayList(
				str)) {
			List<String> songs = this.beanFactory.getBean(
					IPlayListIOService.class).read(new File(str));
			List<IAudioObject> files = this.beanFactory.getBean(
					IPlayListIOService.class).getAudioObjectsFromFileNamesList(
					songs);
			for (IAudioObject file : files) {
				this.queue.addSong(file);
			}
			return true;
		} else if (this.beanFactory.getBean(ILocalAudioObjectValidator.class)
				.isValidAudioFile(str)) {
			IAudioObject file = this.beanFactory.getBean(
					IPlayListIOService.class).getAudioObjectOrCreate(str);
			this.queue.addSong(file);
			return true;
		}
		return false;
	}
}