/*
 * aTunes 2.1.0-SNAPSHOT
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

/**
 * The Class MPlayerPositionThread.
 */
class MPlayerPositionThread extends Thread {

    private static final int STEP = 250;

    private MPlayerEngine engine;

    /**
     * Instantiates a new m player position thread.
     * 
     * @param engine
     *            the engine
     */
    MPlayerPositionThread(MPlayerEngine engine) {
        this.engine = engine;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (!engine.isPlaybackPaused()) {
                    engine.getCommandWriter().sendGetPositionCommand();
                }
                Thread.sleep(STEP);
            }
        } catch (InterruptedException e) {
            // Interrupted when playback is stopped
        }
    }
}
