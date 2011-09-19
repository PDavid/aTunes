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

package net.sourceforge.atunes.api;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;

import org.commonjukebox.plugins.model.PluginApi;

@PluginApi
public final class PlayListApi {

    private PlayListApi() {

    }

    /**
     * Returns number of play lists currently loaded
     * 
     * @return
     */
    public static int getNumberOfPlayLists() {
        return Context.getBean(IPlayListHandler.class).getPlayListCount();
    }

    /**
     * Returns name of play list at given index
     * 
     * @param index
     * @return
     */
    public static String getNameOfPlayList(int index) {
        return Context.getBean(IPlayListHandler.class).getPlayListNameAtIndex(index);
    }

    /**
     * Returns audio objects of play list being played
     * 
     * @return Audio objects being played
     */
    public static List<IAudioObject> getCurrentPlayList() {
        List<IAudioObject> result = new ArrayList<IAudioObject>();
        IPlayList playlist = Context.getBean(IPlayListHandler.class).getCurrentPlayList(false);
        for (int i = 0; i < playlist.size(); i++) {
            result.add(playlist.get(i));
        }
        return result;
    }

    /**
     * Returns audio objects of play list with given index
     * 
     * @param index
     * @return
     */
    public static List<IAudioObject> getPlayList(int index) {
        return Context.getBean(IPlayListHandler.class).getPlayListContent(index);
    }

    /**
     * Adds a list of audio objects to the current visible play list
     * 
     * @param objects
     */
    public static void add(List<IAudioObject> objects) {
        Context.getBean(IPlayListHandler.class).addToPlayList(objects);
    }
}
