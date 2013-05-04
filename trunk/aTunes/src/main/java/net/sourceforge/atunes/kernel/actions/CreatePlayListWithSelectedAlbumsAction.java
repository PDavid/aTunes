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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action creates a new play list with all songs of the album that is
 * currently selected in play list
 * 
 * If more than one album is selected, creates one play list for each one
 * 
 * @author fleax
 * 
 */
public class CreatePlayListWithSelectedAlbumsAction extends
		AbstractActionOverSelectedObjects<IAudioObject> {

	private static final long serialVersionUID = -2917908051161952409L;

	private IRepositoryHandler repositoryHandler;

	private IPlayListHandler playListHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Default constructor
	 */
	public CreatePlayListWithSelectedAlbumsAction() {
		super(I18nUtils.getString("SET_ALBUM_AS_PLAYLIST"));
		putValue(SHORT_DESCRIPTION, I18nUtils.getString("ALBUM_BUTTON_TOOLTIP"));
		setEnabled(false);
	}

	@Override
	protected void executeAction(final List<IAudioObject> objects) {
		// Get selected albums from play list
		List<IAlbum> selectedAlbums = new ArrayList<IAlbum>();
		for (IAudioObject ao : objects) {
			String artistName = ao.getArtist(this.unknownObjectChecker);
			String album = ao.getAlbum(this.unknownObjectChecker);
			IArtist a = this.repositoryHandler.getArtist(artistName);
			if (a != null) {
				IAlbum alb = a.getAlbum(album);
				if (alb != null && !selectedAlbums.contains(alb)) {
					selectedAlbums.add(alb);
				}
			}
		}

		// Create one play list for each album
		createPlayLists(selectedAlbums);
	}

	/**
	 * @param selectedAlbums
	 */
	private void createPlayLists(final List<IAlbum> selectedAlbums) {
		for (IAlbum album : selectedAlbums) {
			List<ILocalAudioObject> audioObjects = this.repositoryHandler
					.getAudioFilesForAlbums(Collections.singletonList(album));
			this.beanFactory.getBean(IAudioObjectComparator.class).sort(
					audioObjects);

			// Create a new play list with album as name and audio objects
			this.playListHandler.newPlayList(album.getName(), audioObjects);
		}
	}
}
