/*
 * aTunes 1.14.0
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

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;

public class VlcPlayerCommandWriter extends Thread {

    protected static final int VOLUME_FACTOR = 5;

    Logger logger = new Logger();

    private volatile Process process;
    /** The audio object to play. */
    private AudioObject audioObject;
    private VlcPlayerEngine engine;
    private VlcTelnetClient vlcTelnetClient;

    protected VlcPlayerCommandWriter(VlcPlayerEngine handler, Process process, AudioObject audioObject) {
        this.engine = handler;
        this.process = process;
        this.audioObject = audioObject;
    }

    @Override
    public final void run() {
        initTelnetClient();
        startTelnetClientReader();

        //try to retrieve length before position 
        sendCommand("");
        sendGetDurationCommand();

        startPostionThread();
    }

    private void initTelnetClient() {
        if (process != null) {
            //multiple chances to obtain a telnet client
            int tries = 4;

            for (int i = 0; i < tries; i++) {
                try {
                    vlcTelnetClient = new VlcTelnetClient(VlcPlayerEngine.REMOTE_HOST, VlcPlayerEngine.REMOTE_PORT);
                    break;

                } catch (VlcTelnetClientException ex) {
                    logger.info(LogCategories.PLAYER, "Error while getting new vlcTelnetClient (" + ex.getMessage() + "), retry " + (i + 1) + " of " + tries);

                    if (i == (tries - 1)) {
                        process.destroy();
                        logger.error(LogCategories.PLAYER, ex);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                engine.handlePlayerEngineError(new VlcTelnetClientException("Communication problem with vlc remote interface"));
                            }
                        });
                        break;
                    }
                }
            }
        }
    }

    private void startTelnetClientReader() {
        VlcPlayerOutputReader vlcPlayerOutputReader = VlcPlayerOutputReader.newInstance(engine, audioObject);
        vlcPlayerOutputReader.start();
    }

    private void startPostionThread() {
        VlcPlayerPositionThread positionThread = new VlcPlayerPositionThread(engine);
        positionThread.start();
    }

    private void sendCommand(String command) {
        if (vlcTelnetClient != null && process != null) {
            vlcTelnetClient.sendCommand(command);
        }/*
          * else { logger.error(LogCategories.PLAYER, "Command : " + command +
          * " not sent (vlcTelnetClient is null)"); }
          */
    }

    /**
     * Send get duration command.
     */
    protected void sendGetDurationCommand() {
        sendCommand("get_length");
    }

    /**
     * Send get position command.
     */
    void sendGetPositionCommand() {
        sendCommand("get_time");
    }

    /**
     * Send mute command.
     */
    void sendMuteCommand() {
        sendVolumeCommand(0);
    }

    /**
     * Send pause command.
     */
    protected void sendPauseCommand() {
        sendCommand("pause");
    }

    /**
     * Send resume command.
     */
    protected void sendResumeCommand() {
        sendCommand("pause");
    }

    protected void sendSeekCommand(long postion) {
        sendCommand("seek " + postion);
    }

    /**
     * Send stop command.
     */
    protected void sendStopCommand() {
        sendCommand("quit");
    }

    protected void sendVolumeCommand(int perCent) {
        sendCommand("volume " + perCent * VOLUME_FACTOR);
    }

    protected VlcTelnetClient getVlcTelnetClient() {
        return vlcTelnetClient;
    }

}
