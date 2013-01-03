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

package net.sourceforge.atunes.gui.images;

import java.awt.Polygon;
import java.awt.Rectangle;

final class VolumeCommonImageIcon {

	static final int WIDTH = 24;
	static final int HEIGHT = 24;

	private VolumeCommonImageIcon() {}
	
	static final Polygon getVolumeShape() {
        Polygon volumeShape = new Polygon();
        volumeShape.addPoint(2, 8);
        volumeShape.addPoint(2, 16);
        volumeShape.addPoint(6, 16);        
        volumeShape.addPoint(11, 20);
        volumeShape.addPoint(11, 4);
        volumeShape.addPoint(6, 8);
        return volumeShape;
	}
	
	static final Rectangle getVolumeLevel1() {
		return new Rectangle(13, 8, 2, 8);
	}
	
	static final Rectangle getVolumeLevel2() {
		return new Rectangle(16, 6, 2, 12);
	}
	
	static final Rectangle getVolumeLevel3() {
		return new Rectangle(19, 4, 2, 16);
	}
	
	static final Polygon getMute() {
		Polygon mute = new Polygon();
		mute.addPoint(13, 10);
		mute.addPoint(15, 8);
		mute.addPoint(17, 10);
		mute.addPoint(19, 8);
		mute.addPoint(21, 10);
		mute.addPoint(19, 12);
		mute.addPoint(21, 14);
		mute.addPoint(19, 16);
		mute.addPoint(17, 14);
		mute.addPoint(15, 16);
		mute.addPoint(13, 14);
		mute.addPoint(15, 12);
		return mute;
	}
}
