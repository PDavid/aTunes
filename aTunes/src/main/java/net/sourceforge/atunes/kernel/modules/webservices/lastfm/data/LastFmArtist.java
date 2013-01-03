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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm.data;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IArtistInfo;
import de.umass.lastfm.Artist;
import de.umass.lastfm.ImageSize;

/**
 * An artist retrieved from last.fm
 * 
 * @author alex
 * 
 */
public class LastFmArtist implements IArtistInfo {

    private static final long serialVersionUID = 2077813440652134441L;

    private String name;
    private String similarTo;
    private String match;
    private String url;
    private String imageUrl;
    // Used by renderers
    private ImageIcon image;

    /**
     * <Code>true</code> if this artist is available at repository
     */
    private transient boolean available;

    /**
     * Gets the artist.
     * 
     * @param a
     * @param similarTo
     * @return
     */
    protected static LastFmArtist getArtist(final Artist a,
	    final String similarTo) {
	LastFmArtist artist = new LastFmArtist();
	artist.name = a.getName();
	// Match is returned in [0-1] range and we show a percentage
	artist.match = String.valueOf((int) (a.getSimilarityMatch() * 100));
	String url2 = a.getUrl();
	artist.url = url2.startsWith("http") ? url2 : "http://" + url2;
	// SMALL images have low quality when scaling. Better to get largest
	// image
	artist.imageUrl = a.getImageURL(ImageSize.LARGE);
	artist.similarTo = similarTo;
	return artist;
    }

    /**
     * Gets the image.
     * 
     * @return the image
     */
    @Override
    public ImageIcon getImage() {
	return image;
    }

    /**
     * Gets the image url.
     * 
     * @return the image url
     */
    @Override
    public String getImageUrl() {
	return imageUrl;
    }

    /**
     * Gets the match.
     * 
     * @return the match
     */
    @Override
    public String getMatch() {
	return match;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    @Override
    public String getName() {
	return name;
    }

    /**
     * Gets the url.
     * 
     * @return the url
     */
    @Override
    public String getUrl() {
	return url;
    }

    /**
     * Sets the image.
     * 
     * @param image
     *            the new image
     */
    @Override
    public void setImage(final ImageIcon image) {
	this.image = image;
    }

    /**
     * Sets the image url.
     * 
     * @param imageUrl
     *            the imageUrl to set
     */
    @Override
    public void setImageUrl(final String imageUrl) {
	this.imageUrl = imageUrl;
    }

    /**
     * Sets the match.
     * 
     * @param match
     *            the match to set
     */
    @Override
    public void setMatch(final String match) {
	this.match = match;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            the name to set
     */
    @Override
    public void setName(final String name) {
	this.name = name;
    }

    /**
     * Sets the url.
     * 
     * @param url
     *            the url to set
     */
    @Override
    public void setUrl(final String url) {
	this.url = url;
    }

    /**
     * @return the available
     */
    @Override
    public boolean isAvailable() {
	return available;
    }

    /**
     * @param available
     *            the available to set
     */
    @Override
    public void setAvailable(final boolean available) {
	this.available = available;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	LastFmArtist other = (LastFmArtist) obj;
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	return true;
    }

    /**
     * @return the similarTo
     */
    @Override
    public String getSimilarTo() {
	return similarTo;
    }

    /**
     * @param similarTo
     *            the similarTo to set
     */
    @Override
    public void setSimilarTo(final String similarTo) {
	this.similarTo = similarTo;
    }

}
