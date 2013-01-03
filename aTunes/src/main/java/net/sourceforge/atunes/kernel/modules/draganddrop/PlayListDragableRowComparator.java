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

import java.io.Serializable;
import java.util.Comparator;

final class PlayListDragableRowComparator implements Comparator<PlayListDragableRow>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2196123391276254494L;
	private final boolean needReverseRows;

	PlayListDragableRowComparator(boolean needReverseRows) {
		this.needReverseRows = needReverseRows;
	}

	@Override
	public int compare(PlayListDragableRow o1, PlayListDragableRow o2) {
	    return (needReverseRows ? -1 : 1) * Integer.valueOf(o1.getRowPosition()).compareTo(Integer.valueOf(o2.getRowPosition()));
	}
}