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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.utils.CollectionUtils;

/**
 * Action to create playlist with popular tracks of artist
 * 
 * @author alex
 * 
 */
public class CreatePlaylistWithPopularTracksActionListener implements
		ActionListener {

	private IRepositoryHandler repositoryHandler;

	private IPlayListHandler playListHandler;

	private IArtistTopTracks lastTopTracks;

	/**
	 * @param lastTopTracks
	 */
	public void setLastTopTracks(IArtistTopTracks lastTopTracks) {
		this.lastTopTracks = lastTopTracks;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (lastTopTracks != null
				&& !CollectionUtils.isEmpty(lastTopTracks.getTracks())) {
			// Get titles for top tracks
			List<String> artistTopTracks = new ArrayList<String>();
			for (ITrackInfo track : lastTopTracks.getTracks()) {
				artistTopTracks.add(track.getTitle());
			}

			// Find in repository
			List<ILocalAudioObject> audioObjectsInRepository = repositoryHandler
					.getAudioObjectsByTitle(lastTopTracks.getArtist(),
							artistTopTracks);
			if (!CollectionUtils.isEmpty(audioObjectsInRepository)) {
				// Create a new play list with artist as name and audio objects
				// selected
				playListHandler.newPlayList(lastTopTracks.getArtist(),
						audioObjectsInRepository);
			}
		}
	}
}