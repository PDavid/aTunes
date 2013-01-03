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

package net.sourceforge.atunes.kernel.modules.draganddrop;

import net.sourceforge.atunes.model.IAudioObject;

/**
 * Represents a draggable play list row
 * 
 * @author alex
 * 
 */
public class PlayListDragableRow {

    /**
     * Content of the row
     */
    private final IAudioObject rowContent;

    /**
     * Original index of row when started drag and drop
     */
    private final int rowPosition;

    /**
     * @return the rowContent
     */
    public IAudioObject getRowContent() {
	return rowContent;
    }

    /**
     * @return the rowPosition
     */
    public int getRowPosition() {
	return rowPosition;
    }

    /**
     * @param rowContent
     * @param rowPosition
     */
    public PlayListDragableRow(final IAudioObject rowContent,
	    final int rowPosition) {
	super();
	this.rowContent = rowContent;
	this.rowPosition = rowPosition;
    }

}
