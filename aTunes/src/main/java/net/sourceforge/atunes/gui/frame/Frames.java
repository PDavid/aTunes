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

package net.sourceforge.atunes.gui.frame;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IFrame;

/**
 * Types of frames and an image representing each one
 * 
 * @author alex
 * 
 */
public final class Frames {

    private List<Class<? extends IFrame>> frames;

    private Map<Class<? extends IFrame>, String> images;

    /**
     * @param images
     */
    public void setImages(final Map<Class<? extends IFrame>, String> images) {
	this.images = images;
    }

    /**
     * @param frames
     */
    public void setFrames(final List<Class<? extends IFrame>> frames) {
	this.frames = frames;
    }

    /**
     * @return
     */
    public List<Class<? extends IFrame>> getFrames() {
	return frames;
    }

    /**
     * @param clazz
     * @return image for given frame type
     */
    public ImageIcon getImage(final Class<? extends IFrame> clazz) {
	if (clazz == null) {
	    throw new IllegalArgumentException("class null");
	}
	return getImage(images.get(clazz));
    }

    private static ImageIcon getImage(final String string) {
	URL imgURL = Frames.class.getResource(string);
	return imgURL != null ? new ImageIcon(imgURL) : null;
    }
}