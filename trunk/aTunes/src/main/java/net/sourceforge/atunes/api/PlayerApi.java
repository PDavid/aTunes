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

package net.sourceforge.atunes.api;

import net.sourceforge.atunes.kernel.modules.player.PlaybackState;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.model.AudioObject;

import org.commonjukebox.plugins.model.PluginApi;

@PluginApi
public final class PlayerApi {

    private PlayerApi() {

    }

    /**
     * Stops current object being played
     */
    public static void stop() {
        PlayerHandler.getInstance().stopCurrentAudioObject(true);
    }

    /**
     * Starts playing previous object
     */
    public static void previous() {
        PlayerHandler.getInstance().playPreviousAudioObject();
    }

    /**
     * Starts playing next object
     */
    public static void next() {
        PlayerHandler.getInstance().playNextAudioObject();
    }

    /**
     * Plays or pauses current object
     */
    public static void play() {
        PlayerHandler.getInstance().playCurrentAudioObject(true);
    }

    /**
     * Returns current audio object in active play list
     * 
     * @return
     */
    public static AudioObject getCurrentAudioObject() {
        return PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList();
    }

    /**
     * Returns the current state of player: playing, paused...
     * 
     * @return
     */
    public static PlaybackState getCurrentPlaybackState() {
        return PlayerHandler.getInstance().getPlaybackState();
    }

    /**
     * Returns current audio object time played in milliseconds
     * 
     * @return
     */
    public static long getCurrentAudioObjectPlayedTime() {
        return PlayerHandler.getInstance().getCurrentAudioObjectPlayedTime();
    }

    /**
     * Returns length of current audio object in milliseconds
     * 
     * @return
     */
    public static long getCurrentAudioObjectLength() {
        return PlayerHandler.getInstance().getCurrentAudioObjectLength();
    }

    /**
     * Raise volume
     */
    public static void volumeUp() {
        PlayerHandler.getInstance().volumeUp();
    }

    /**
     * Lower volume
     */
    public static void volumeDown() {
        PlayerHandler.getInstance().volumeDown();
    }
}
