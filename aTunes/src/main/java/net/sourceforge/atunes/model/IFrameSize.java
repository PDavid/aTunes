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
 * Properties of size of a frame
 * 
 * @author alex
 * 
 */
public interface IFrameSize {

    /**
     * Returns if frame is maximized
     * 
     * @return
     */
    public boolean isMaximized();

    /**
     * Sets frame maximized
     * 
     * @param maximized
     */
    public void setMaximized(boolean maximized);

    /**
     * Returns frame width
     * 
     * @return
     */
    public int getWindowWidth();

    /**
     * Sets frame width
     * 
     * @param windowWidth
     */
    public void setWindowWidth(int windowWidth);

    /**
     * Returns frame height
     * 
     * @return
     */
    public int getWindowHeight();

    /**
     * Sets window height
     * 
     * @param windowHeight
     */
    public void setWindowHeight(int windowHeight);

}
