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

package net.sourceforge.atunes.kernel.modules.favorites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.ILocalAudioObject;

/**
 * The Class Favorites.
 */
public class Favorites implements IFavorites {

	private static final long serialVersionUID = 4783402394156393291L;

	/** The favorite songs. */
	private final Map<String, ILocalAudioObject> favoriteSongs;

	/** The favorite albums. */
	private final Map<String, IAlbum> favoriteAlbums;

	/** The favorite artists. */
	private final Map<String, IArtist> favoriteArtists;

	/**
	 * Instantiates a new favorites.
	 */
	protected Favorites() {
		this.favoriteSongs = new HashMap<String, ILocalAudioObject>();
		this.favoriteAlbums = new HashMap<String, IAlbum>();
		this.favoriteArtists = new HashMap<String, IArtist>();
	}

	@Override
	public List<ILocalAudioObject> getAllFavoriteSongs() {
		List<ILocalAudioObject> result = new ArrayList<ILocalAudioObject>();
		for (IArtist artist : this.favoriteArtists.values()) {
			result.addAll(artist.getAudioObjects());
		}
		for (IAlbum album : this.favoriteAlbums.values()) {
			result.addAll(album.getAudioObjects());
		}
		result.addAll(this.favoriteSongs.values());
		return result;
	}

	@Override
	public void addArtist(final IArtist artist) {
		this.favoriteArtists.put(artist.getName(), artist);
	}

	@Override
	public boolean containsArtist(final IArtist artist) {
		return this.favoriteArtists.containsKey(artist.getName());
	}

	@Override
	public boolean containsArtist(final String artist) {
		return this.favoriteArtists.containsKey(artist);
	}

	@Override
	public void removeArtist(final IArtist artist) {
		this.favoriteArtists.remove(artist.getName());
	}

	@Override
	public void removeArtist(final String artist) {
		this.favoriteArtists.remove(artist);
	}

	@Override
	public List<IArtist> getFavoriteArtists() {
		return new ArrayList<IArtist>(this.favoriteArtists.values());
	}

	@Override
	public boolean containsAlbum(final IAlbum album) {
		return this.favoriteAlbums.containsKey(album.getName());
	}

	@Override
	public boolean containsAlbum(final String album) {
		return this.favoriteAlbums.containsKey(album);
	}

	@Override
	public void addAlbum(final IAlbum album) {
		this.favoriteAlbums.put(album.getName(), album);
	}

	@Override
	public void removeAlbum(final IAlbum album) {
		this.favoriteAlbums.remove(album.getName());
	}

	@Override
	public void removeAlbum(final String album) {
		this.favoriteAlbums.remove(album);
	}

	@Override
	public List<IAlbum> getFavoriteAlbums() {
		return new ArrayList<IAlbum>(this.favoriteAlbums.values());
	}

	@Override
	public boolean containsSong(final ILocalAudioObject song) {
		return this.favoriteSongs.containsKey(song.getUrl());
	}

	@Override
	public void addSong(final ILocalAudioObject song) {
		this.favoriteSongs.put(song.getUrl(), song);
	}

	@Override
	public void removeSong(final ILocalAudioObject file) {
		this.favoriteSongs.remove(file.getUrl());
	}

	@Override
	public List<ILocalAudioObject> getFavoriteSongs() {
		return new ArrayList<ILocalAudioObject>(this.favoriteSongs.values());
	}
}
