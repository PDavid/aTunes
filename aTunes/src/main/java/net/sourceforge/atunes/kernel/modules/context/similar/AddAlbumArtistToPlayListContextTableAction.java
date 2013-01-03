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

package net.sourceforge.atunes.kernel.modules.context.similar;

import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IArtistAlbumSelectorDialog;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Adds an album of select artist to play list
 * 
 * @author alex
 * 
 */
public final class AddAlbumArtistToPlayListContextTableAction extends
		ContextTableAction<IArtistInfo> {

	private static final long serialVersionUID = -3920095074089169426L;

	private IRepositoryHandler repositoryHandler;

	private IDialogFactory dialogFactory;

	private IPlayListHandler playListHandler;

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
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
     * 
     */
	AddAlbumArtistToPlayListContextTableAction() {
		super(I18nUtils.getString("ADD_ALBUM_ARTIST_TO_PLAYLIST"));
	}

	@Override
	protected void execute(final IArtistInfo object) {
		showAddArtistDragDialog(this.repositoryHandler.getArtist(object
				.getName()));
	}

	@Override
	protected IArtistInfo getSelectedObject(final int row) {
		return ((SimilarArtistsTableModel) getTable().getModel())
				.getArtist(row);
	}

	@Override
	protected boolean isEnabledForObject(final Object object) {
		return this.repositoryHandler.getArtist(((IArtistInfo) object)
				.getName()) != null;
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
}