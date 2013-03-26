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

import javax.swing.ImageIcon;

/**
 * A video entry related to some audio object
 * 
 * @author alex
 * 
 */
public interface IVideoEntry {

    /**
     * @return the name
     */
    public String getName();

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name);

    /**
     * @return the url
     */
    public String getUrl();

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url);

    /**
     * @return the image
     */
    public ImageIcon getImage();

    /**
     * @param image
     *            the image to set
     */
    public void setImage(ImageIcon image);

    /**
     * @return the duration
     */
    public String getDuration();

    /**
     * @param duration
     *            the duration to set
     */
    public void setDuration(String duration);

    /**
     * @return artist of video
     */
    public String getArtist();

    /**
     * @param artist
     */
    public void setArtist(String artist);

}