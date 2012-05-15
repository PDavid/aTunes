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
package net.sourceforge.atunes.kernel.actions;

import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action only returns help text which the handler can then output to the telnet client.
 * 
 */
public class PrintHelpToTelnetAction extends RemoteAction {
    
    private static final long serialVersionUID = -6161108283405050138L;
    
    /**
     * Default constructor
     */
    public PrintHelpToTelnetAction() {
    	super("help");
    }

    @Override
    public String runCommand(java.util.List<String> parameters) {
        return StringUtils.getString(
        		"aTunes terminal line interface. This interface provides you with control of your player over a network.\n",
                "Each command has the format of command:\"command name [params]\", without \"\".\n",
                "Commands available are:\n",
                "play                                           Starts or pauses playback \n",
                "stop                                           Stops playback\n",
                "showOSD                                        Pops out a window with the current name of the song.\n",                       
                "fullscreen                                     Switches the player to fullscreen mode.\n",
                "volUP                                          Increments the volume by 5 percent.\n",
                "volDOWN                                        Decrements the volume by 5 percent.\n",
                "volume                                         Outputs the current volume.\n",
                "goto   [number]                                Skips the playback to the percentage chosen. 0-100\n",
                "add [--NOW] SONGNAME                           Appends SONGNAME to the end of the playlist.\n",
                "                                               If --NOW follows the add keyword, it plays the song immediately\n",
                "song [fileName]                                Prints out current song information, or the song information of the specified filename.\n",
                "next                                           Plays the next song in the playlist.\n",
                "previous                                       Plays the previous song in the playlist.\n",
                "shuffle [on|off]                               Toggles shuffle playlist on/off.\n",
                "repeat [on|off]                                Toggles looping of a song on/off\n",
                "list [artist [artistName] | album [albumName]] Prints out a list of all song files.\n",
                "                                               when parameter artist is used it prints the list of all artists, when the name of an artist is provided,\n",
                "                                               it ouputs all song which belong to the artist.");
    }
}
