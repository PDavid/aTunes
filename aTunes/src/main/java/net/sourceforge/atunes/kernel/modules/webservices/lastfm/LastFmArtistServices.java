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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmArtistTopTracks;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmEvent;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmSimilarArtists;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.IEvent;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Event;
import de.umass.lastfm.ImageSize;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Tag;
import de.umass.lastfm.Track;

/**
 * Services to retrieve information about artists
 * 
 * @author alex
 * 
 */
public class LastFmArtistServices {

	private static final String ARTIST_WILDCARD = "(%ARTIST%)";
	private static final String LANGUAGE_PARAM = "?setlang=";
	private static final String LANGUAGE_WILDCARD = "(%LANGUAGE%)";
	private static final String ARTIST_WIKI_URL = StringUtils.getString(
			"http://www.lastfm.com/music/", ARTIST_WILDCARD, "/+wiki",
			LANGUAGE_PARAM, LANGUAGE_WILDCARD);

	private static final int MAX_SIMILAR_ARTISTS = 15;

	private LastFmCache lastFmCache;

	private LastFmAPIKey lastFmAPIKey;

	private INetworkHandler networkHandler;

	private IStateCore stateCore;

	/**
	 * @param stateCore
	 */
	public void setStateCore(final IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param lastFmAPIKey
	 */
	public void setLastFmAPIKey(final LastFmAPIKey lastFmAPIKey) {
		this.lastFmAPIKey = lastFmAPIKey;
	}

	/**
	 * @param lastFmCache
	 */
	public void setLastFmCache(final LastFmCache lastFmCache) {
		this.lastFmCache = lastFmCache;
	}

	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(final INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	/**
	 * Gets the artist top tag.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the artist top tag
	 */
	String getArtistTopTag(final String artist) {
		try {
			Collection<Tag> topTags = Artist.getTopTags(artist,
					this.lastFmAPIKey.getApiKey());
			List<String> tags = new ArrayList<String>();
			for (Tag t : topTags) {
				tags.add(t.getName());
			}
			return tags.isEmpty() ? "" : tags.get(0);
		} catch (Exception e) {
			Logger.error(e);
		}
		return null;
	}

	/**
	 * Gets the image of an artist
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the image
	 */
	ImageIcon getArtistThumbImage(final IArtistInfo artist) {
		try {
			// Try to retrieve from cache
			ImageIcon img = this.lastFmCache.retrieveArtistThumbImage(artist);
			if (img == null && artist.getImageUrl() != null
					&& !artist.getImageUrl().isEmpty()) {
				// Try to get from Artist.getImages() method
				img = getArtistImageFromLastFM(artist.getName(),
						ImageSize.LARGE);

				// if not then get from artist info
				if (img == null) {
					img = new ImageIcon(
							this.networkHandler.getImage(this.networkHandler
									.getConnection(artist.getImageUrl())));
				}

				if (img != null) {
					// Resize image for thumb images
					img = new ImageIcon(ImageUtils.scaleBufferedImageBicubic(
							img.getImage(), Constants.THUMB_IMAGE_WIDTH,
							Constants.THUMB_IMAGE_HEIGHT));
					this.lastFmCache.storeArtistThumbImage(artist, img);
				}
			}
			return img;
		} catch (Exception e) {
			Logger.error(e);
		}
		return null;
	}

	/**
	 * Gets the image of the artist
	 * 
	 * @param artistName
	 *            the similar
	 * 
	 * @return the image
	 */
	ImageIcon getArtistImage(final String artistName) {
		try {
			// Try to retrieve from cache
			ImageIcon img = this.lastFmCache.retrieveArtistImage(artistName);

			if (img != null) {
				return img;
			}

			// Try to get from LastFM
			img = getArtistImageFromLastFM(artistName, ImageSize.HUGE);

			// Get from similar artist info
			if (img == null) {
				img = getArtistImageFromSimilarArtistInformation(artistName);
			}

			if (img != null) {
				// Resize image for thumb images
				img = new ImageIcon(ImageUtils.scaleBufferedImageBicubic(
						img.getImage(), Constants.ARTIST_IMAGE_SIZE,
						Constants.ARTIST_IMAGE_SIZE));
				this.lastFmCache.storeArtistImage(artistName, img);
			}

			return img;
		} catch (Exception e) {
			Logger.error(e);
		}
		return null;
	}

	/**
	 * @param artistName
	 * @return
	 * @throws IOException
	 */
	private ImageIcon getArtistImageFromSimilarArtistInformation(
			final String artistName) throws IOException {
		ISimilarArtistsInfo similarArtist = getSimilarArtists(artistName);
		if (similarArtist != null) {
			String similarUrl = similarArtist.getPicture();
			if (!similarUrl.trim().isEmpty()) {
				return new ImageIcon(
						this.networkHandler.getImage(this.networkHandler
								.getConnection(similarUrl)));
			}
		}
		return null;
	}

	/**
	 * Returns top tracks for given artist name
	 * 
	 * @param artistName
	 * @return
	 */
	IArtistTopTracks getTopTracks(final String artistName) {
		// Try to retrieve from cache
		IArtistTopTracks topTracks = this.lastFmCache
				.retrieveArtistTopTracks(artistName);

		if (topTracks != null) {
			return topTracks;
		}

		// Try to get from LastFM
		topTracks = LastFmArtistTopTracks.getTopTracks(artistName,
				Artist.getTopTracks(artistName, this.lastFmAPIKey.getApiKey()));

		// Complete album requesting each track
		for (ITrackInfo trackInfo : topTracks.getTracks()) {
			Track fullTrack = Track.getInfo(artistName, trackInfo.getTitle(),
					this.lastFmAPIKey.getApiKey());
			if (fullTrack != null) {
				trackInfo.setAlbum(fullTrack.getAlbum());
			}
		}

		if (topTracks != null) {
			this.lastFmCache.storeArtistTopTracks(artistName, topTracks);
		}

		return topTracks;
	}

	/**
	 * Returns current artist image at LastFM
	 * 
	 * @param artistName
	 * @param size
	 * @return
	 */
	private ImageIcon getArtistImageFromLastFM(final String artistName,
			final ImageSize size) {
		try {
			// Try to get from Artist.getImages() method
			PaginatedResult<de.umass.lastfm.Image> images = Artist.getImages(
					artistName, 1, 1, this.lastFmAPIKey.getApiKey());
			List<de.umass.lastfm.Image> imageList = new ArrayList<de.umass.lastfm.Image>(
					images.getPageResults());
			if (!imageList.isEmpty()) {
				String url = getSmallestURL(imageList.get(0), size);
				if (url != null) {
					return new ImageIcon(
							this.networkHandler.getImage(this.networkHandler
									.getConnection(url)));
				}
			}
		} catch (IOException e) {
			Logger.error(e);
		}
		return null;
	}

	/**
	 * Returns URL of the smallest image
	 * 
	 * @param a
	 * @return
	 */
	private static String getSmallestURL(final de.umass.lastfm.Image a,
			final ImageSize start) {
		// SMALL: 0
		// MEDIUM: 1
		// LARGE: 2
		// LARGESQUARE: 3
		// HUGE: 4
		// EXTRALARGE: 5
		// MEGA: 6
		// ORIGINAL: 7

		ImageSize[] sizes = ImageSize.values();
		for (int i = start.ordinal(); i < sizes.length; i++) {
			String url = a.getImageURL(sizes[i]);
			if (url != null) {
				return url;
			}
		}

		return null;
	}

	/**
	 * Gets the similar artists.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the similar artists
	 */
	ISimilarArtistsInfo getSimilarArtists(final String artist) {
		try {
			// Try to get from cache
			ISimilarArtistsInfo similar = this.lastFmCache
					.retrieveArtistSimilar(artist);

			// Check cache content. Since "match" value changed in last.fm api
			// can be entries in cache with old value.
			// For those entries match is equal or less than 1.0, so discard
			// entries where maximum match is that value
			if (similar != null) {
				float maxMatch = 0;
				for (IArtistInfo artistInfo : similar.getArtists()) {
					float match = 0;
					try {
						match = Float.parseFloat(artistInfo.getMatch());
						if (match > maxMatch) {
							maxMatch = match;
						}
					} catch (NumberFormatException e) {
						// Not a valid match value, better to discard cache
						// content
						similar = null;
					}
				}
				if (maxMatch <= 1) {
					similar = null;
				}
			}

			if (similar == null) {
				Collection<Artist> as = Artist.getSimilar(artist,
						this.lastFmAPIKey.getApiKey());
				Artist a = Artist
						.getInfo(artist, this.lastFmAPIKey.getApiKey());
				if (a != null) {
					similar = LastFmSimilarArtists.getSimilarArtists(as, a,
							MAX_SIMILAR_ARTISTS);
					this.lastFmCache.storeArtistSimilar(artist, similar);
				}
			}
			return similar;
		} catch (Exception e) {
			Logger.error(e);
		}
		return null;
	}

	/**
	 * Gets the wiki text.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the wiki text
	 */
	String getWikiText(final String artist) {
		try {
			// Try to get from cache
			String wikiText = this.lastFmCache.retrieveArtistWiki(artist);
			if (wikiText == null) {

				Artist a = Artist.getInfo(artist, this.stateCore.getLocale()
						.getLocale(), null, this.lastFmAPIKey.getApiKey());
				wikiText = a != null ? a.getWikiSummary() : "";
				wikiText = wikiText.replaceAll("<.*?>", "");
				wikiText = StringUtils.unescapeHTML(wikiText, 0);

				this.lastFmCache.storeArtistWiki(artist, wikiText);
			}
			return wikiText;
		} catch (Exception e) {
			Logger.error(e);
		}
		return null;
	}

	/**
	 * Gets the wiki url.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the wiki url
	 */
	String getWikiURL(final String artist) {
		return ARTIST_WIKI_URL.replace(ARTIST_WILDCARD,
				this.networkHandler.encodeString(artist)).replace(
				LANGUAGE_WILDCARD,
				this.stateCore.getLocale().getLocale().getLanguage());
	}

	/**
	 * Returns events of an artist. This is a convenience method to allow
	 * plugins access last fm services without opening access to api key outside
	 * this class
	 * 
	 * @param artist
	 * @return
	 */
	List<IEvent> getArtistEvents(final String artist) {
		List<IEvent> result = new ArrayList<IEvent>();
		Collection<Event> events = Artist.getEvents(artist,
				this.lastFmAPIKey.getApiKey()).getPageResults();
		if (events != null) {
			for (Event event : events) {
				IEvent e = LastFmEvent.getEvent(event, artist);
				if (!StringUtils.isEmpty(e.getSmallImageUrl())) {
					Image image = null;
					try {
						image = networkHandler.getImage(networkHandler
								.getConnection(e.getSmallImageUrl()));
					} catch (IOException e1) {
						Logger.error(e1);
					}
					if (image != null) {
						e.setImage(ImageUtils.scaleImageBicubic(image,
								Constants.THUMB_IMAGE_WIDTH,
								Constants.THUMB_IMAGE_HEIGHT));
					}
				}
				result.add(e);
			}
		}
		return result;
	}
}
