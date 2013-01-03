/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Creates names for play lists
 * 
 * @author alex
 * 
 */
public class PlayListNameCreator {

    /**
     * The play list counter used when creating new play lists with default
     * name.
     */
    private int playListNameCounter = 1;

    /**
     * Gets the name for playlist.
     * 
     * @param pl
     *            the pl
     * 
     * @return the name for playlist
     */
    String getNameForPlaylist(final IPlayListsContainer playLists,
	    final IPlayList pl) {
	if (pl == null || StringUtils.isEmpty(pl.getName())) {
	    return createPlayListName(playLists);
	}
	return pl.getName();
    }

    /**
     * @param playLists
     * @return
     */
    private String createPlayListName(final IPlayListsContainer playLists) {
	String name = null;
	do {
	    name = StringUtils.getString(I18nUtils.getString("PLAYLIST"), " ",
		    playListNameCounter++);
	} while (checkIfNameExists(playLists, name));
	return name;
    }

    /**
     * @param playLists
     * @param name
     * @return
     */
    private boolean checkIfNameExists(final IPlayListsContainer playLists,
	    final String name) {
	if (playLists != null) {
	    for (int i = 0; i < playLists.getPlayListsCount(); i++) {
		IPlayList playList = playLists.getPlayListAt(i);
		if (playList.getName() != null
			&& name.trim().equalsIgnoreCase(
				playList.getName().trim())) {
		    return true;
		}
	    }
	}
	return false;
    }
}
