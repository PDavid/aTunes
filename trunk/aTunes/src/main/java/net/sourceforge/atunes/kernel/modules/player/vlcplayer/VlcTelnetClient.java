/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.player.vlcplayer;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

import org.apache.commons.net.telnet.TelnetClient;

public final class VlcTelnetClient {

    protected static final Logger logger = new Logger();

    /** The TelnetClient implementation (apache) */
    private TelnetClient telnet = new TelnetClient();
    private InputStream in;
    private PrintStream out;

    protected VlcTelnetClient(String newServer, int newPort) throws VlcTelnetClientException {

        // wait a title time for vlc process to be created 
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            //we do nothing special
            //e.printStackTrace();
        }

        // Connect to the specified server
        logger.debug(LogCategories.NETWORK, "VlcTelnetClient : Connecting to port : ", newPort, " on server : ", newServer);

        try {
            telnet.connect(newServer, newPort);
        } catch (SocketException se) {
            se.printStackTrace();
            throw new VlcTelnetClientException("Error while connecting to : " + newServer + " on : " + newPort + " : " + se.toString(), se);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new VlcTelnetClientException("Error while connecting to : " + newServer + " on : " + newPort + " : " + ioe.toString(), ioe);
        }

        // Get input and output stream references
        try {
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());
            //throw new Exception("Can't write or read form telnet client");
        } catch (Exception ex) {
            throw new VlcTelnetClientException("Can't write or read form telnet client", ex);
        }

    }

    private void write(String value) {
        try {
            out.println(value);
            out.flush();
        } catch (Exception e) {
            logger.error(LogCategories.PLAYER, e);
        }
    }

    protected void sendCommand(String command) {
        write(command);
    }

    private void disconnect() {
        try {
            telnet.disconnect();
        } catch (IOException e) {
            logger.error(LogCategories.PLAYER, e);
        }
    }

    protected InputStream getIn() {
        return in;
    }

    protected PrintStream getOut() {
        return out;
    }

    /**
     * For testing purpose
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            VlcTelnetClient telnet = new VlcTelnetClient("127.0.0.1", 8888);
            telnet.sendCommand("pause");
            telnet.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
