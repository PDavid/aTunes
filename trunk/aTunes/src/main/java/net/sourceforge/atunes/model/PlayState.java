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

package net.sourceforge.atunes.model;

import java.awt.Paint;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.PlayListStateImageIcon;

/**
 * The play state of the playlist.
 */
public enum PlayState {

    STOPPED,

    PLAYING,

    PAUSED,

    /**
     * When it's not the active play list
     */
    NONE;

    public static ImageIcon getPlayStateIcon(Paint color, PlayState state) {
        switch (state) {
        case PLAYING:
            return PlayListStateImageIcon.getPlayIcon(color);
        case STOPPED:
            return PlayListStateImageIcon.getStopIcon(color);
        case PAUSED:
            return PlayListStateImageIcon.getPauseIcon(color);
        case NONE:
            return null;
        default:
            return null;
        }
    }
}