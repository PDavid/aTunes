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

package net.sourceforge.atunes.kernel.modules.webservices.youtube;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IVideoEntry;

/**
 * Result entry from YouTube service
 * 
 * @author Tobias Melcher
 * 
 */
class YoutubeResultEntry implements IVideoEntry {

    /**
     * Name of video
     */
    private String name;

    /**
     * Artist of video
     */
    private String artist;

    /**
     * Url of video
     */
    private String url;

    /**
     * Thumbnail image of video
     */
    private ImageIcon image;

    /**
     * Length of this video
     */
    private String duration;

    /**
     * @return the name
     */
    @Override
    public String getName() {
	return name;
    }

    /**
     * @param name
     *            the name to set
     */
    @Override
    public void setName(final String name) {
	this.name = name;
    }

    /**
     * @return the url
     */
    @Override
    public String getUrl() {
	return url;
    }

    /**
     * @param url
     *            the url to set
     */
    @Override
    public void setUrl(final String url) {
	this.url = url;
    }

    /**
     * @return the image
     */
    @Override
    public ImageIcon getImage() {
	return image;
    }

    /**
     * @param image
     *            the image to set
     */
    @Override
    public void setImage(final ImageIcon image) {
	this.image = image;
    }

    /**
     * @return the duration
     */
    @Override
    public String getDuration() {
	return duration;
    }

    /**
     * @param duration
     *            the duration to set
     */
    @Override
    public void setDuration(final String duration) {
	this.duration = duration;
    }

    @Override
    public String getArtist() {
	return artist;
    }

    @Override
    public void setArtist(final String artist) {
	this.artist = artist;
    }
}
