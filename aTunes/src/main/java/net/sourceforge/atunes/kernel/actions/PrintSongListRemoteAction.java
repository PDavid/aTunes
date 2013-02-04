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

package net.sourceforge.atunes.kernel.actions;

import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.CollectionUtils;

import org.apache.commons.lang.StringUtils;

/**
 * This action enables a telnet client to browse the repository. It enables a
 * client to view a list of all song path so that a client then can create a
 * local list of songs and play them when needed. With parameters, it outputs
 * either all artists or all albums, then when an artist/album name is provided
 * it can output all songs belonging to him/it.
 * 
 */
public class PrintSongListRemoteAction extends RemoteAction {

	private static final String PLAYLIST = "playlist";
	private static final String ALBUM = "album";
	private static final String ARTIST = "artist";
	private static final long serialVersionUID = -1450032380215351834L;

	private IRepositoryHandler repositoryHandler;

	private IPlayListHandler playListHandler;

	private IFileManager fileManager;

	/**
	 * @param fileManager
	 */
	public void setFileManager(IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param handler
	 */
	public void setRepositoryHandler(final IRepositoryHandler handler) {
		this.repositoryHandler = handler;
	}

	/**
	 * @param handler
	 */
	public void setPlayListHandler(final IPlayListHandler handler) {
		this.playListHandler = handler;
	}

	@Override
	public String runCommand(final List<String> parameters) {
		Collection<ILocalAudioObject> objects = null;

		if (!CollectionUtils.isEmpty(parameters)) {
			String query = null;
			if (parameters.size() > 1) {
				query = StringUtils.join(
						parameters.subList(1, parameters.size()), ' ');
			}
			String firstParameter = parameters.get(0);
			if (firstParameter.equalsIgnoreCase(ARTIST)) {
				objects = processArtistParameter(query);
				if (objects == null) {
					return "Artist not found.";
				}
			} else if (firstParameter.equalsIgnoreCase(ALBUM)) {
				objects = processAlbumParameter(query);
				if (objects == null) {
					return "Album not found";
				}
			} else if (firstParameter.equalsIgnoreCase(PLAYLIST)) {
				return processPlaylistParameter();
			} else {
				return "Bad command";
			}
		} else {
			objects = processAllSongs();
		}

		StringBuilder sb = new StringBuilder();
		if (objects != null) {
			for (ILocalAudioObject iao : objects) {
				sb.append(fileManager.getPath(iao)).append("\n");
			}
		}
		return sb.toString();
	}

	private Collection<ILocalAudioObject> processAllSongs() {
		return repositoryHandler.getAudioFilesList();
	}

	private String processPlaylistParameter() {
		Collection<IAudioObject> playlistObjects = playListHandler
				.getActivePlayList().getAudioObjectsList();
		if (playlistObjects.isEmpty()) {
			return "Playlist is empty";
		} else {
			StringBuilder sb = new StringBuilder();
			for (IAudioObject audio : playlistObjects) {
				sb.append(audio.getTitleOrFileName()).append("\n");
			}
			return sb.toString();
		}
	}

	/**
	 * @param query
	 * @return
	 */
	private Collection<ILocalAudioObject> processArtistParameter(
			final String query) {
		if (net.sourceforge.atunes.utils.StringUtils.isEmpty(query)) {
			returnArtistList();
			return null;
		} else {
			IArtist artist = repositoryHandler.getArtist(query);
			if (artist == null) {
				return null;
			} else {
				return artist.getAudioObjects();
			}
		}
	}

	private Collection<ILocalAudioObject> processAlbumParameter(
			final String query) {
		if (net.sourceforge.atunes.utils.StringUtils.isEmpty(query)) {
			returnAlbumList();
			return null;
		} else {
			List<IAlbum> albums = repositoryHandler.getAlbums();
			// Find album
			IAlbum album = null;
			for (IAlbum alb : albums) {
				if (alb.getName().equalsIgnoreCase(query)) {
					album = alb;
					break;
				}
			}
			if (album == null) {
				return null;
			} else {
				return album.getAudioObjects();
			}
		}
	}

	/**
	 * Builds a list of artists
	 */
	private String returnArtistList() {
		StringBuilder sb = new StringBuilder();
		for (IArtist artist : repositoryHandler.getArtists()) {
			sb.append(artist.getName()).append("\n");
		}
		return sb.toString();
	}

	/**
	 * Builds a list of albums
	 */
	private String returnAlbumList() {
		StringBuilder sb = new StringBuilder();
		for (IAlbum album : repositoryHandler.getAlbums()) {
			sb.append(album.getName()).append("\n");
		}
		return sb.toString();
	}

	@Override
	protected String getHelpText() {
		return "Prints out a list of all song files. "
				+ "When parameter artist is used it prints the list of all artists, when the name of an artist is provided, "
				+ "it ouputs all song which belong to the artist";
	}

	@Override
	protected String getOptionalParameters() {
		return "[artist [artistName] | album [albumName]]";
	}
}
