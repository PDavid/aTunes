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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;

/**
 * The listener interface for receiving playList key events.
 */
public final class PlayListKeyListener extends KeyAdapter {

    //	/**
    //	 * Play list controller
    //	 */
    //	private PlayListController controller;

    /**
     * Constructor
     * 
     * @param controller
     */
    PlayListKeyListener(PlayListController controller) {
        //this.controller = controller;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // FIX: ENTER key event is also fired from "PlayAction" which is associated to Play menu item
        // If user presses enter -> Play selected song
        //		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
        //			controller.playSelectedAudioObject();
        //		}

        // If user presses SPACE -> Pause actually playing song
        if (e.getKeyChar() == KeyEvent.VK_SPACE) {
            PlayerHandler.getInstance().playCurrentAudioObject(true);
        }

    }

}
