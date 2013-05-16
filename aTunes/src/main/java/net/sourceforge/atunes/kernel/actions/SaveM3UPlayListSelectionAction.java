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

import java.io.File;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileSelectorDialog;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListIOService;
import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action saves current play list selection to file as M3U
 * 
 * @author fleax
 * 
 */
public class SaveM3UPlayListSelectionAction extends CustomAbstractAction {

	private static final long serialVersionUID = -303252911138284095L;

	private IPlayListHandler playListHandler;

	private IPlayListIOService playListIOService;

	private IStatePlaylist statePlaylist;

	private IDialogFactory dialogFactory;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param statePlaylist
	 */
	public void setStatePlaylist(final IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}

	/**
	 * @param playListIOService
	 */
	public void setPlayListIOService(final IPlayListIOService playListIOService) {
		this.playListIOService = playListIOService;
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
	public SaveM3UPlayListSelectionAction() {
		super(StringUtils.getString(
				I18nUtils.getString("EXPORT_SAVE_PLAYLIST_SELECTION"), "..."));
	}

	@Override
	protected void executeAction() {
		IFileSelectorDialog dialog = this.dialogFactory
				.newDialog(IFileSelectorDialog.class);
		dialog.setFileFilter(this.playListIOService.getPlaylistM3UFileFilter());
		File file = dialog.saveFile(this.statePlaylist.getSavePlaylistPath(),
				this.playListHandler.getVisiblePlayList().getName());
		if (file != null) {

			this.statePlaylist.setSavePlaylistPath(FileUtils.getPath(file
					.getParentFile()));

			// If filename have incorrect extension, add it
			file = this.playListIOService.checkM3UPlayListFileName(file);

			this.playListIOService.writeM3U(
					this.playListHandler.getSelectedAudioObjects(), file);
		}
	}

	@Override
	public boolean isEnabledForPlayListSelection(
			final List<IAudioObject> selection) {
		return !this.playListHandler.getVisiblePlayList().isEmpty();
	}
}
