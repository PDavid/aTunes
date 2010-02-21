/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

/**
 * The Class FadeAwayRunnable.
 */
class FadeAwayRunnable implements Runnable {

    private Logger logger;

    /** The process. */
    private Process process;

    /** The command writer. */
    private MPlayerCommandWriter commandWriter;

    /** The initial volume. */
    private int initialVolume;

    /** Flag to interrupt this runnable */
    private boolean interrupted = false;

    /**
     * MPlayerHandler instance
     */
    private MPlayerEngine handler;

    /**
     * Instantiates a new fade away runnable.
     * 
     * @param process
     *            the process
     * @param initialVolume
     *            the initial volume
     * @param handler
     *            the mplayer handler
     */
    FadeAwayRunnable(Process process, int initialVolume, MPlayerEngine handler) {
        this.process = process;
        this.initialVolume = initialVolume;
        this.handler = handler;
        commandWriter = new MPlayerCommandWriter(process);
    }

    @Override
    public void run() {
        getLogger().debug(LogCategories.PLAYER, "Fade away runnable started");
        try {
            int fadeVolume = initialVolume;
            int fadeStep = 0;
            // Lower volume until it reaches 0 or 50 volume reductions
            // or object is interrupted
            while (!interrupted && fadeStep < 50 && fadeVolume > 0) {
                fadeVolume = fadeVolume - 2;
                commandWriter.sendVolumeCommand(fadeVolume);
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    getLogger().error(LogCategories.PLAYER, e);
                }
                fadeStep++;
            }
        } finally {
            process.destroy();
            getLogger().debug(LogCategories.PLAYER, "Fade away runnable finished");
        }
        if (!interrupted) {
            // Notify finish to MPlayerHandler
            handler.finishedFadeAway();
        }
    }

    /**
     * This method is called when fade away must stop inmediately
     */
    void finish() {
        interrupted = true;
        handler.finishedFadeAway();
    }
    
    /**
     * Getter for logger
     * @return
     */
    private Logger getLogger() {
    	if (logger == null) {
    		logger = new Logger();
    	}
    	return logger;
    }


}
