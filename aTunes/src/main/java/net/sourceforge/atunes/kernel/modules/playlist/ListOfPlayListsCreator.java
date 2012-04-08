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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IListOfPlayLists;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.utils.CollectionUtils;

class ListOfPlayListsCreator {

    /**
     * Gets the list of play lists.
     * @param playLists
     * @param activePlayListIndex
     * @param filtered
     * @param nonFilteredPlayList 
     * @return the list of play lists
     */
    IListOfPlayLists getListOfPlayLists(List<IPlayList> playLists, int activePlayListIndex, boolean filtered, IPlayList nonFilteredPlayList) {
    	if (CollectionUtils.isEmpty(playLists)) {
    		throw new IllegalArgumentException("Playlists empty or null");
    	}
    	
        ListOfPlayLists l = new ListOfPlayLists();

        // Clone play lists to make changes in returned list if current play list is filtered
        l.setPlayLists(new ArrayList<IPlayList>(playLists));
        l.setSelectedPlayList(activePlayListIndex);

        // If current play list is filtered return non-filtered play list
        if (filtered) {
            l.getPlayLists().remove(activePlayListIndex);
            l.getPlayLists().add(activePlayListIndex, nonFilteredPlayList);
        }

        return l;
    }

}
