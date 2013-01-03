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

import java.io.Serializable;
import java.util.Comparator;

final class RowListComparator implements Comparator<Integer>, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -1389171859618293326L;
	
	private final boolean up;

    RowListComparator(boolean up) {
        this.up = up;
    }

    @Override
    public int compare(Integer o1, Integer o2) {
        return (up ? 1 : -1) * o1.compareTo(o2);
    }
}