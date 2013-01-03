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

import java.awt.Graphics;
import java.awt.Image;

/**
 * A cover.
 */
public final class HiddenCover extends Cover {

    private static final long serialVersionUID = -3836270786764203330L;
    
    private String previousArtist;
    private String previousAlbum;
    
    /**
     * Instantiates a cover.
     * @param imageSize
     */
    public HiddenCover() {
    	super(0);
    }

    @Override
    public void paintComponent(Graphics g) {
    	// Do nothing
    }

    /**
     * Sets the image.
     * 
     * @param image
     *            the image to set
     */
    void setImage(Image image, int width, int height) {
    	// Do nothing
    }
    
    /**
     * @return
     */
    public String getPreviousArtist() {
		return previousArtist;
	}
    
    /**
     * @return
     */
    public String getPreviousAlbum() {
		return previousAlbum;
	}
    
    /**
     * @param previousArtist
     */
    public void setPreviousArtist(String previousArtist) {
		this.previousArtist = previousArtist;
	}
    
    /**
     * @param previousAlbum
     */
    public void setPreviousAlbum(String previousAlbum) {
		this.previousAlbum = previousAlbum;
	}
    
    /**
     * @return
     */
    public int getImageSize() {
		return 0;
	}
}
