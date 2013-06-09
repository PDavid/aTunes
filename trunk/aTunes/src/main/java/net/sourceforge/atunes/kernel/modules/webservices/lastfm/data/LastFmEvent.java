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

import java.util.Collection;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IEvent;

import org.joda.time.DateTime;

import de.umass.lastfm.Event;
import de.umass.lastfm.ImageSize;

/**
 * Events retrieved from last.fm
 * 
 * @author alex
 * 
 */
public class LastFmEvent implements IEvent {

	private String title;

	private String artist;

	private Collection<String> artists;

	private DateTime startDate;

	private String url;

	private String venue;

	private String city;

	private String country;

	private String smallImageUrl;

	private String originalImageUrl;

	private ImageIcon image;

	/**
	 * @param event
	 * @param artist
	 * @return event transformed to internal object
	 */
	public static IEvent getEvent(Event event, String artist) {
		LastFmEvent result = new LastFmEvent();
		result.setTitle(event.getTitle());
		result.setArtist(artist);
		result.setArtists(event.getArtists());
		result.setStartDate(new DateTime(event.getStartDate()));
		result.setUrl(event.getUrl());
		if (event.getVenue() != null) {
			result.setVenue(event.getVenue().getName());
			result.setCity(event.getVenue().getCity());
			result.setCountry(event.getVenue().getCountry());
		}
		result.setSmallImageUrl(event.getImageURL(ImageSize.MEDIUM));
		result.setOriginalImageUrl(event.getImageURL(ImageSize.EXTRALARGE));
		return result;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public String getArtist() {
		return this.artist;
	}

	@Override
	public void setArtist(String artist) {
		this.artist = artist;
	}

	@Override
	public DateTime getStartDate() {
		return this.startDate;
	}

	@Override
	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	@Override
	public String getCity() {
		return this.city;
	}

	@Override
	public String getCountry() {
		return this.country;
	}

	@Override
	public String getUrl() {
		return this.url;
	}

	@Override
	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void setSmallImageUrl(String imageUrl) {
		this.smallImageUrl = imageUrl;
	}

	@Override
	public String getSmallImageUrl() {
		return this.smallImageUrl;
	}

	@Override
	public void setOriginalImageUrl(String imageUrl) {
		this.originalImageUrl = imageUrl;
	}

	@Override
	public String getOriginalImageUrl() {
		return this.originalImageUrl;
	}

	@Override
	public void setImage(ImageIcon image) {
		this.image = image;
	}

	@Override
	public ImageIcon getImage() {
		return image;
	}

	@Override
	public void setArtists(Collection<String> artists) {
		this.artists = artists;
	}

	@Override
	public Collection<String> getArtists() {
		return artists;
	}

	@Override
	public void setVenue(String venue) {
		this.venue = venue;
	}

	@Override
	public String getVenue() {
		return venue;
	}
}
