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

import java.io.Serializable;

import javax.swing.ImageIcon;

import net.sf.ehcache.Element;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.utils.AbstractCache;
import net.sourceforge.atunes.utils.Logger;

/**
 * A cache of last.fm responses
 * 
 * @author alex
 * 
 */
public class LastFmCache extends AbstractCache {

	private static final String ARTIST_WIKI = "ARTIST_WIKI";

	private static final String ARTIST_THUMB = "ARTIST_THUMB";

	private static final String ARTIST_SIMILAR = "ARTIST_SIMILAR";

	private static final String ALBUM_LIST = "ALBUM_LIST";

	private static final String ARTIST_TOP_TRACKS = "ARTIST_TOP_TRACKS";

	private static final String ARTIST_IMAGE = "ARTIST_IMAGE";

	private static final String ALBUM_INFO = "ALBUM_INFO";

	private static final String ALBUM_COVER_THUMB = "ALBUM_COVER_THUMB";

	private static final String ALBUM_COVER = "ALBUM_COVER";

	private IStateContext stateContext;

	/**
	 * @param stateContext
	 */
	public void setStateContext(final IStateContext stateContext) {
		this.stateContext = stateContext;
	}

	/**
	 * Clears a cache, returning true if some error happened
	 * 
	 * @param cache
	 * @return
	 */
	public boolean clearCache() {
		return removeAll();
	}

	@SuppressWarnings("unchecked")
	private <T> T getObject(final String cacheId, final Serializable key) {
		if (!this.stateContext.isCacheLastFmContent()) {
			Logger.debug("Last.fm cache is disabled");
			return null;
		}
		Element element = get(new CacheKey(cacheId, key));
		if (element == null) {
			return null;
		} else {
			return (T) element.getValue();
		}
	}

	private void storeObject(final String cacheId, final Serializable key,
			final Serializable value) {
		if (!this.stateContext.isCacheLastFmContent()) {
			Logger.debug("Last.fm cache is disabled");
			return;
		}

		if (cacheId == null || key == null || value == null) {
			return;
		}
		put(new Element(new CacheKey(cacheId, key), value));
	}

	/**
	 * Retrieves an Album Cover from cache.
	 * 
	 * @param album
	 *            the album
	 * 
	 * @return the image
	 */
	public synchronized ImageIcon retrieveAlbumCover(final IAlbumInfo album) {
		return getObject(ALBUM_COVER, album);
	}

	/**
	 * Retrieves an Album Cover Thumb from cache.
	 * 
	 * @param album
	 *            the album
	 * 
	 * @return the image
	 */
	public synchronized ImageIcon retrieveAlbumCoverThumb(final IAlbumInfo album) {
		return getObject(ALBUM_COVER_THUMB, album);
	}

	/**
	 * Retrieves an Album Cover from cache.
	 * 
	 * @param album
	 *            the album
	 * @param artist
	 *            the artist
	 * 
	 * @return the audio scrobbler album
	 */
	public synchronized IAlbumInfo retrieveAlbumInfo(final String artist,
			final String album) {
		return getObject(ALBUM_INFO, artist + album);
	}

	/**
	 * Retrieves an Artist Image from cache.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the image
	 */
	public synchronized ImageIcon retrieveArtistImage(final String artist) {
		return getObject(ARTIST_IMAGE, artist);
	}

	/**
	 * Retrieves a list of top tracks for given artist from cache
	 * 
	 * @param artist
	 * @return
	 */
	public synchronized IArtistTopTracks retrieveArtistTopTracks(
			final String artist) {
		return getObject(ARTIST_TOP_TRACKS, artist);
	}

	/**
	 * Retrieves an albumList from cache.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the audio scrobbler album list
	 */
	public synchronized IAlbumListInfo retrieveAbumList(final String artist) {
		return getObject(ALBUM_LIST, artist);
	}

	/**
	 * Retrieves an Artist similar from cache.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the audio scrobbler similar artists
	 */
	public synchronized ISimilarArtistsInfo retrieveArtistSimilar(
			final String artist) {
		return getObject(ARTIST_SIMILAR, artist);
	}

	/**
	 * Retrieves an Artist Thumb from cache.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the image
	 */
	public synchronized ImageIcon retrieveArtistThumbImage(
			final IArtistInfo artist) {
		return getObject(ARTIST_THUMB, artist);
	}

	/**
	 * Retrieves an Artist wiki from cache.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the string
	 */
	public synchronized String retrieveArtistWiki(final String artist) {
		return getObject(ARTIST_WIKI, artist);
	}

	/**
	 * Stores an Album Cover at cache.
	 * 
	 * @param album
	 *            the album
	 * @param cover
	 *            the cover
	 */
	public synchronized void storeAlbumCover(final IAlbumInfo album,
			final ImageIcon cover) {
		storeObject(ALBUM_COVER, album, cover);
		Logger.debug("Stored Album Cover for album ", album.getTitle());
	}

	/**
	 * Stores an Album Cover thumb at cache.
	 * 
	 * @param album
	 *            the album
	 * @param cover
	 *            the cover
	 */
	public synchronized void storeAlbumCoverThumb(final IAlbumInfo album,
			final ImageIcon cover) {
		storeObject(ALBUM_COVER_THUMB, album, cover);
		Logger.debug("Stored Album Cover Thumb for album ", album.getTitle());
	}

	/**
	 * Stores an Album Cover at cache.
	 * 
	 * @param album
	 *            the album
	 * @param artist
	 *            the artist
	 * @param albumObject
	 *            the album object
	 */
	public synchronized void storeAlbumInfo(final String artist,
			final String album, final IAlbumInfo albumObject) {
		storeObject(ALBUM_INFO, artist + album, albumObject);
		Logger.debug("Stored album info for album ", artist, " ", album);
	}

	/**
	 * Store an Artist Image at cache.
	 * 
	 * @param artist
	 *            the artist
	 * @param image
	 *            the image
	 */
	public synchronized void storeArtistImage(final String artist,
			final ImageIcon image) {
		storeObject(ARTIST_IMAGE, artist, image);
		Logger.debug("Stored artist image for ", artist);
	}

	/**
	 * Store an Artist top tracks list at cache
	 * 
	 * @param artist
	 * @param topTracks
	 */
	public synchronized void storeArtistTopTracks(final String artist,
			final IArtistTopTracks topTracks) {
		storeObject(ARTIST_TOP_TRACKS, artist, topTracks);
		Logger.debug("Stored artist top tracks for ", artist);
	}

	/**
	 * Store an album list at cache.
	 * 
	 * @param artist
	 *            the artist
	 * @param list
	 *            the list
	 */
	public synchronized void storeAlbumList(final String artist,
			final IAlbumListInfo list) {
		storeObject(ALBUM_LIST, artist, list);
		Logger.debug("Stored album list for ", artist);
	}

	/**
	 * Store an Artist similar at cache.
	 * 
	 * @param artist
	 *            the artist
	 * @param similar
	 *            the similar
	 */
	public synchronized void storeArtistSimilar(final String artist,
			final ISimilarArtistsInfo similar) {
		storeObject(ARTIST_SIMILAR, artist, similar);
		Logger.debug("Stored artist similar for ", artist);
	}

	/**
	 * Stores an Artist Thumb at cache.
	 * 
	 * @param artist
	 *            the artist
	 * @param image
	 *            the image
	 */
	public synchronized void storeArtistThumbImage(final IArtistInfo artist,
			final ImageIcon image) {
		storeObject(ARTIST_THUMB, artist, image);
		Logger.debug("Stored artist thumb for ", artist.getName());
	}

	/**
	 * Store an Artist wiki at cache.
	 * 
	 * @param artist
	 *            the artist
	 * @param wikiText
	 *            the wiki text
	 */
	public synchronized void storeArtistWiki(final String artist,
			final String wikiText) {
		storeObject(ARTIST_WIKI, artist, wikiText);
		Logger.debug("Stored artist wiki for ", artist);
	}

	/**
	 * Shutdowns cache
	 */
	public void shutdown() {
		dispose();
	}

	/**
	 * Flushes cache
	 */
	@Override
	public void flush() {
		Logger.debug("Flushing last.fm cache");
		super.flush();
	}
}
