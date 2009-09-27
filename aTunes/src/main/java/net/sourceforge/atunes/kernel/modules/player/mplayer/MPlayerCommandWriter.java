/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.kernel.modules.player.mplayer;

import java.io.PrintStream;

import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class MPlayerCommandWriter.
 */
class MPlayerCommandWriter {

    /** The process. */
    private volatile Process process;

    /** Stream used to send commands to mplayer */
    private volatile PrintStream streamToProcess;

    /**
     * Instantiates a new m player command writer.
     * 
     * @param process
     *            the process
     */
    MPlayerCommandWriter(Process process) {
        this.process = process;
    }

    /**
     * Send command.
     * 
     * @param command
     *            the command
     */
    private void sendCommand(String command) {
        if (process != null) {
            if (streamToProcess == null) {
                streamToProcess = new PrintStream(process.getOutputStream());
            }
            streamToProcess.print(command);
            streamToProcess.print('\n');
            streamToProcess.flush();
        }
    }

    /**
     * Send get duration command.
     */
    void sendGetDurationCommand() {
        sendCommand("get_time_length");
    }

    /**
     * Send get position command.
     */
    void sendGetPositionCommand() {
        sendCommand("get_time_pos");
    }

    /**
     * Send mute command.
     */
    void sendMuteCommand() {
        sendCommand("mute");
    }

    /**
     * Send pause command.
     */
    void sendPauseCommand() {
        sendCommand("pause");
    }

    /**
     * Send resume command.
     */
    void sendResumeCommand() {
        sendCommand("pause");
    }

    /**
     * Send seek command.
     * 
     * @param perCent
     *            the per cent
     */

    void sendSeekCommand(double perCent) {
        sendCommand(StringUtils.getString("seek ", perCent * 100, " 1"));
        sendPauseCommand();
    }

    /**
     * Send stop command.
     */
    void sendStopCommand() {
        sendCommand("quit");
    }

    /**
     * Send volume command.
     * 
     * @param perCent
     *            the per cent
     */

    void sendVolumeCommand(int perCent) {
        sendCommand(StringUtils.getString("pausing_keep volume ", perCent, " 1"));
    }

    /**
     * Gets the process.
     * 
     * @return the process
     */
    Process getProcess() {
        return process;
    }

    /**
     * Finish the process.
     * 
     * @param process
     *            the new process
     */
    void finishProcess() {
        this.streamToProcess.close();
        this.streamToProcess = null;
        this.process = null;
    }
}
