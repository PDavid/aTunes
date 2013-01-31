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
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmAlbum;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmAlbumList;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAlbumListInfo;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;
import de.umass.lastfm.Album;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Playlist;

/**
 * Services to retrieve information about albums
 * 
 * @author alex
 * 
 */
public class LastFmAlbumServices {

	private LastFmCache lastFmCache;

	private LastFmAPIKey lastFmAPIKey;

	private INetworkHandler networkHandler;

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
	 * Gets the album.
	 * 
	 * @param artist
	 *            the artist
	 * @param album
	 *            the album
	 * 
	 * @return the album
	 */
	IAlbumInfo getAlbum(final String artist, final String album) {
		try {
			// Try to get from cache
			IAlbumInfo albumObject = lastFmCache.retrieveAlbumInfo(artist,
					album);
			if (albumObject == null) {
				Album a = Album
						.getInfo(artist, album, lastFmAPIKey.getApiKey());
				if (a != null) {
					Playlist pl = Playlist.fetchAlbumPlaylist(a.getId(),
							lastFmAPIKey.getApiKey());
					albumObject = LastFmAlbum.getAlbum(a, pl);
					lastFmCache.storeAlbumInfo(artist, album, albumObject);
				}
			}
			return albumObject;
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Gets the image of the album
	 * 
	 * @param artist
	 * @param album
	 * @return
	 */
	ImageIcon getAlbumImage(final String artist, final String album) {
		ImageIcon image = null;
		IAlbumInfo a = getAlbum(artist, album);
		if (a != null) {
			image = getAlbumImage(a);
		}
		return image;
	}

	/**
	 * Gets the album list.
	 * 
	 * @param artist
	 *            the artist
	 * 
	 * @return the album list
	 */
	IAlbumListInfo getAlbumList(final String artist) {
		// Try to get from cache
		IAlbumListInfo albumList = lastFmCache.retrieveAbumList(artist);
		if (albumList == null) {
			Collection<Album> as = Artist.getTopAlbums(artist,
					lastFmAPIKey.getApiKey());
			if (as != null) {
				IAlbumListInfo albums = LastFmAlbumList
						.getAlbumList(as, artist);

				List<IAlbumInfo> result = filterAlbumsFromSource(artist, albums);

				albumList = new LastFmAlbumList();
				albumList.setArtist(artist);
				albumList.setAlbums(result);
				lastFmCache.storeAlbumList(artist, albumList);
			}
		}

		return albumList;
	}

	/**
	 * Removes albums with no image or no mbid and with correct artist
	 * 
	 * @param artist
	 * @param albums
	 * @return
	 */
	private List<IAlbumInfo> filterAlbumsFromSource(final String artist,
			final IAlbumListInfo albums) {
		List<IAlbumInfo> result = new ArrayList<IAlbumInfo>();
		for (IAlbumInfo a : albums.getAlbums()) {
			if (hasMbid(a) && artistMatches(artist, a) && hasImage(a)) {
				result.add(a);
			}
		}
		return result;
	}

	/**
	 * Returns if album has mbid
	 * 
	 * @param a
	 * @return
	 */
	private boolean hasMbid(final IAlbumInfo a) {
		return !StringUtils.isEmpty(a.getMbid());
	}

	/**
	 * Returns if album has image
	 * 
	 * @param a
	 * @return
	 */
	private boolean hasImage(final IAlbumInfo a) {
		return !StringUtils.isEmpty(a.getThumbCoverURL())
				&& !a.getThumbCoverURL().contains("noimage");
	}

	/**
	 * Checks if artist of album is equal to given artist
	 * 
	 * @param artist
	 * @param album
	 * @return
	 */
	private boolean artistMatches(final String artist, final IAlbumInfo album) {
		return album.getArtist().equalsIgnoreCase(artist);
	}

	/**
	 * Gets the image.
	 * 
	 * @param album
	 *            the album
	 * 
	 * @return the image
	 */
	ImageIcon getAlbumImage(final IAlbumInfo album) {
		try {
			ImageIcon img = null;
			// Try to retrieve from cache
			img = lastFmCache.retrieveAlbumCover(album);
			if (img == null && !StringUtils.isEmpty(album.getBigCoverURL())) {
				Image tmp = networkHandler.getImage(networkHandler
						.getConnection(album.getBigCoverURL()));
				if (tmp != null) {
					img = new ImageIcon(tmp);
					lastFmCache.storeAlbumCover(album, img);
				}
			}

			return img;
		} catch (IOException e) {
			// Sometimes urls given by last.fm are forbidden, so avoid show full
			// error stack traces
			Logger.error(e.getMessage());
			Logger.debug(e);
		}
		return null;
	}

	/**
	 * Gets the thumbnail image of album
	 * 
	 * @param album
	 *            the album
	 * 
	 * @return the image
	 */
	ImageIcon getAlbumThumbImage(final IAlbumInfo album) {
		try {
			ImageIcon img = null;
			// Try to retrieve from cache
			img = lastFmCache.retrieveAlbumCoverThumb(album);
			if (img == null && !StringUtils.isEmpty(album.getThumbCoverURL())) {
				Image tmp = networkHandler.getImage(networkHandler
						.getConnection(album.getThumbCoverURL()));
				if (tmp != null) {
					// Resize image for thumb images
					img = new ImageIcon(ImageUtils.scaleBufferedImageBicubic(
							tmp, Constants.THUMB_IMAGE_WIDTH,
							Constants.THUMB_IMAGE_HEIGHT));
					lastFmCache.storeAlbumCoverThumb(album, img);
				}
			}

			return img;
		} catch (IOException e) {
			// Sometimes urls given by last.fm are forbidden, so avoid show full
			// error stack traces
			Logger.error(e.getMessage());
			Logger.debug(e);
		}
		return null;
	}

}
