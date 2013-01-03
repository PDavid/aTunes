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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.awt.Dimension;

/**
 * Calculates image size for full screen window
 * 
 * @author alex
 * 
 */
public class FullScreenImageSizeCalculator {

    private Dimension fullScreenCoverSize;

    /**
     * @param fullScreenCoverSize
     */
    public void setFullScreenCoverSize(final Dimension fullScreenCoverSize) {
	this.fullScreenCoverSize = fullScreenCoverSize;
    }

    int getImageSize(final int index) {
	int imageSize = fullScreenCoverSize.height;
	if (index == 2) {
	    return imageSize;
	} else if (index == 1 || index == 3) {
	    return imageSize * 3 / 4;
	} else {
	    return imageSize * 9 / 16;
	}
    }
}
