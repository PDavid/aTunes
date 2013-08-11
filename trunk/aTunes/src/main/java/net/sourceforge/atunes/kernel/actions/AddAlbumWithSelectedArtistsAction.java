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
import java.util.List;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IArtistAlbumSelectorDialog;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * this action show a dialog to add album of artists to the current playlist If
 * more than one artist is selected, a dialog is show for each artist
 * 
 * @author encestre
 * 
 */
public class AddAlbumWithSelectedArtistsAction extends
		AbstractActionOverSelectedObjects<IAudioObject> {

	private static final long serialVersionUID = 242525309967706255L;

	private IRepositoryHandler repositoryHandler;

	private IPlayListHandler playListHandler;

	private IDialogFactory dialogFactory;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
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
	 * Constructor
	 */
	public AddAlbumWithSelectedArtistsAction() {
		super(I18nUtils.getString("ADD_ALBUM_ARTIST_TO_PLAYLIST"));
	}

	@Override
	protected void initialize() {
		super.initialize();
		setEnabled(false);
	}

	@Override
	protected void executeAction(final List<IAudioObject> objects) {
		// Get selected artists from play list
		List<IArtist> selectedArtists = new ArrayList<IArtist>();
		for (IAudioObject ao : objects) {
			String artistName = ao.getArtist(this.unknownObjectChecker);
			IArtist a = this.repositoryHandler.getArtist(artistName);
			if (a != null && !selectedArtists.contains(a)) {
				selectedArtists.add(a);
			}
		}

		// For every artist
		for (IArtist artist : selectedArtists) {
			showAddArtistDragDialog(artist);
		}
	}

	private void showAddArtistDragDialog(final IArtist currentArtist) {
		IArtistAlbumSelectorDialog dialog = this.dialogFactory
				.newDialog(IArtistAlbumSelectorDialog.class);
		dialog.setArtist(currentArtist);
		dialog.showDialog();
		IAlbum album = dialog.getAlbum();
		if (album != null) {
			this.playListHandler.addToVisiblePlayList(album.getAudioObjects());
		}
	}

	@Override
	public boolean isEnabledForPlayList(final IPlayList playlist) {
		return !playlist.isDynamic();
	}

}
