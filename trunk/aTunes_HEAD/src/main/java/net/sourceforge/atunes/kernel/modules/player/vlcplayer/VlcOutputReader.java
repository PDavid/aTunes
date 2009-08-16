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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.ClosingUtils;

class VlcOutputReader extends Thread {

    protected static Logger logger = new Logger();

    VlcPlayerEngine engine;
    private BufferedReader in;
    private String lastLineReaded = "";
    /** the sentence used by vlc to present radio stream infos */
    protected static final String ACCESS_HTTP_DEBUG = "access_http access debug: New Title=";

    /**
     * Instantiates a new m player error reader.
     * 
     * @param handler
     *            the handler
     * @param process
     *            the process
     * @param audioObject
     *            the audio object
     */
    VlcOutputReader(VlcPlayerEngine handler, Process process) {
        this.engine = handler;
        in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    }

    protected void read(String line) {
        logger.debug(LogCategories.PLAYER, "VLC Debug Stream read : " + line);

        if (line.indexOf(ACCESS_HTTP_DEBUG) > 0) {
            //System.out.println("Stream info : " + line);
            this.lastLineReaded = line;
        }

    }

    @Override
    public void run() {
        String line;
        try {
            while ((line = in.readLine()) != null && !isInterrupted()) {
                final String readed = line;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        read(readed);
                    }
                });
            }
        } catch (final IOException e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    //PlayerHandler.notifyPlayerError(e);
                }
            });
        } finally {
            ClosingUtils.close(in);
        }
    }

    public String getLastLineReaded() {
        return lastLineReaded;
    }

}
