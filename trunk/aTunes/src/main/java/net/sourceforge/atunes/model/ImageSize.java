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


/**
 * Image sizes
 * 
 * @author alex
 * 
 */

public enum ImageSize {

    /**
     * 90 pixels
     */
    SIZE_90(90),

    /**
     * 120 pixels
     */
    SIZE_120(120),

    /**
     * 150 pixels
     */
    SIZE_150(150),

    /**
     * 200 pixels
     */
    SIZE_200(200),

    /**
     * maximum size
     */
    SIZE_MAX(-1);

    private int size;

    private ImageSize(final int size) {
	this.size = size;
    }

    /**
     * @return size
     */
    public int getSize() {
	return size;
    }

}
