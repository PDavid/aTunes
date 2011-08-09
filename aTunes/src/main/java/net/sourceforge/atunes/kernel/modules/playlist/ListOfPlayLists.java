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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.AudioObject;

/**
 * This class is used to contain all playlists when storing and reading from
 * disk.
 * 
 * @author alex
 */
public class ListOfPlayLists implements Serializable {

    private static final long serialVersionUID = -9098493526495642598L;

    /** The play lists. */
    private List<PlayList> playLists;

    /** The selected play list. */
    private int selectedPlayListIndex;

    /**
     * Returns a list of playlists with an empty playlist.
     * 
     * @return the empty play list
     */
    public static ListOfPlayLists getEmptyPlayList() {
        ListOfPlayLists l = new ListOfPlayLists();
        l.setPlayLists(Collections.singletonList(new PlayList()));
        l.setSelectedPlayList(0);
        return l;
    }

    /**
     * Gets the play lists.
     * 
     * @return the playLists
     */
    List<PlayList> getPlayLists() {
        return playLists;
    }

    /**
     * Gets the selected play list.
     * 
     * @return the selectedPlayList
     */
    int getSelectedPlayList() {
        return selectedPlayListIndex;
    }

    /**
     * Sets the play lists.
     * 
     * @param playLists
     *            the playLists to set
     */
    void setPlayLists(List<PlayList> playLists) {
        this.playLists = playLists;
    }

    /**
     * Sets the selected play list.
     * 
     * @param selectedPlayList
     *            the selectedPlayList to set
     */
    void setSelectedPlayList(int selectedPlayList) {
        this.selectedPlayListIndex = selectedPlayList;
    }

    /**
     * Fills contents of a list of playlists
     * 
     * @param contents
     */
    public void setContents(List<List<AudioObject>> contents) {
    	if (playLists.size() != contents.size()) {
    		throw new IllegalArgumentException("Invalid play list contents");
    	}
        for (int i = 0; i < playLists.size(); i++) {
        	PlayList pl = getPlayLists().get(i);
            pl.setContent(contents.get(i));
            pl.setMode(PlayListMode.getPlayListMode(pl));
        }
    }

}
