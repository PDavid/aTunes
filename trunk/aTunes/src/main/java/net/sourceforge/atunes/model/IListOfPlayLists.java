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

package net.sourceforge.atunes.model;

import java.io.Serializable;
import java.util.List;


/**
 * A container of play lists
 * @author alex
 *
 */
public interface IListOfPlayLists extends Serializable {

	/**
	 * Sets the selected play list.
	 * 
	 * @param selectedPlayList
	 *            the selectedPlayList to set
	 */
	public void setSelectedPlayList(int selectedPlayList);

    /**
     * Gets the play lists.
     * 
     * @return the playLists
     */
    public List<IPlayList> getPlayLists();

    /**
     * Gets the selected play list.
     * 
     * @return the selectedPlayList
     */
    public int getSelectedPlayList();

}